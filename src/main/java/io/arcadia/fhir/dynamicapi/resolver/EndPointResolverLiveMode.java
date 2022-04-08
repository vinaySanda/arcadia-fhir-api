package io.arcadia.fhir.dynamicapi.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnclassifiedServerFailureException;
import io.arcadia.fhir.client.ArcadiaClient;
import io.arcadia.fhir.exception.EndPointFailureException;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.util.AppConstants;
import io.arcadia.fhir.util.ConfigUtils;

/**
 * <p>
 * This is one of the implementation of {@link EndPointResolver}. This class
 * will be injected if the {@code app.mode} is set to {@code live} in
 * {@code application.properties} file.
 * </p>
 * 
 * <p>
 * This class is responsible for finding the target Arcadia API/APIs for the
 * given FHIR resource based on the {@code ResourceMapping.json} configurations
 * and execute them.
 * </p>
 * 
 * <p>
 * Also it handles exceptions and convert them into {@link OperationOutcome}
 * which is a FHIR resource for exceptions and errors.
 * </p>
 * 
 * @author Pradeep Kumara K
 * 
 * @see OperationOutcome
 * @see EndPointResolver
 * @see EndPointResolverDemoMode
 * @see EndPointFailureException
 *
 */
public class EndPointResolverLiveMode implements EndPointResolver{

	private static final Logger logger = LoggerFactory.getLogger(EndPointResolverLiveMode.class);

	@Autowired
	ArcadiaClient arcadiaClient;

	/**
	 * This method figure out the target Arcadia API/APIs based on input parameters
	 * and FHIR {@link ResourceType}, executes the APIs and return the response.
	 * 
	 * @param type        FHIR {@link ResourceType}
	 * @param inputParams FHIR provider input parameters
	 * @return Person Data for the requested resource type
	 */
	@Override
	public Map getArcadiaResources(ResourceType type, SearchParameterMap inputParams) {
		logger.info(
				"Inside getArcadiaResources for ResourceType " + type.toString() + " and inputParams: " + inputParams);

		String personId = null;
		if (inputParams.containsKey(AppConstants.PATIENT)) {
			String pateintId = getPatientId(inputParams.get(AppConstants.PATIENT));
			personId = AppConstants.PERSON_ID_PREFIX + pateintId;
			return getOneToManyMappingResources(type, personId);
		} else {
			throw new InvalidRequestException("Patient ID should be present in incoming API");
		}
	}

	/**
	 * This method is used when FHIR resources are mapped to exactly one Arcadia
	 * entity
	 * 
	 * @param type     FHIR {@link ResourceType}
	 * @param personId personId
	 * @return Person Data for the requested resource type
	 */
	public List<Map> getArcadiaResources(ResourceType type, String personId) {
		logger.info("Inside getArcadiaResources for ResourceType " + type.toString() + " and personId: " + personId);

		List<Map> arcadiaResources = new ArrayList<>();

		Map<?, ?> resourceMapping = null;
		try {
			resourceMapping = ConfigUtils.getConfig(AppConstants.RESOURCE_MAPPING_FILE);
		} catch (IOException e) {
			logger.error("Exception happend while reading config file: " + AppConstants.RESOURCE_MAPPING_FILE);
			throw new UnclassifiedServerFailureException(Constants.STATUS_HTTP_404_NOT_FOUND, e.getMessage());
		}

		Map<?, ?> apiInfo = ConfigUtils.valueMap(resourceMapping, AppConstants.API_INFO);
		Map<?, ?> resourceInfo = ConfigUtils.valueMap(ConfigUtils.valueMap(resourceMapping, AppConstants.RESOURCES), type.toString());

		// Reading API related info
		String querySyntax = ConfigUtils.valueString(apiInfo, AppConstants.GRAPH_QUERY_SYNTAX);

		// Reading resource related info
		String resourceName = ConfigUtils.valueString(resourceInfo, AppConstants.ARCADIA_RESOURCE_NAME);

		Map graphQLQuery = new HashMap<>();
		String query = querySyntax.replace(AppConstants.PERSON_ID_PLACEHOLDER, personId)
				.replace(AppConstants.ARCADIA_RESOURCE_PLACEHOLDER, resourceName);
		graphQLQuery.put(AppConstants.QUERY, query);

		Map arcadiaResponse = executeAPIandHandleErrors(graphQLQuery);
		
		Object resourceObj = (arcadiaResponse.get(AppConstants.DATA) != null
				&& ((Map) arcadiaResponse.get(AppConstants.DATA)).get(AppConstants.PERSON) != null)
						? ((Map) ((Map) arcadiaResponse.get(AppConstants.DATA)).get(AppConstants.PERSON))
								.get(resourceName)
						: new UnclassifiedServerFailureException(500, "Invalid Arcadia Response");

		if (resourceObj != null && resourceObj instanceof Map) {
			arcadiaResources.add((Map) resourceObj);
		} else if (resourceObj != null && resourceObj instanceof List) {
			arcadiaResources.addAll((List<Map>) resourceObj);
		}
		logger.info("Returning Arcadia entities: " + arcadiaResources);
		return arcadiaResources;
	}
	
	/**
	 * This method is used when FHIR resources are mapped to more than one Arcadia
	 * entities.
	 * 
	 * @param type     FHIR {@link ResourceType}
	 * @param personId personId
	 * @return Person Data for the requested resource type
	 */
	public Map getOneToManyMappingResources(ResourceType type, String personId) {
		logger.info("Inside getArcadiaResources for ResourceType " + type.toString() + " and personId: " + personId);

		Map arcadiaData = null;

		Map<?, ?> resourceMapping = null;
		try {
			resourceMapping = ConfigUtils.getConfig(AppConstants.RESOURCE_MAPPING_FILE);
		} catch (IOException e) {
			logger.error("Exception happend while reading config file: " + AppConstants.RESOURCE_MAPPING_FILE);
			throw new UnclassifiedServerFailureException(Constants.STATUS_HTTP_404_NOT_FOUND, e.getMessage());
		}

		Map<?, ?> apiInfo = (Map<?, ?>) resourceMapping.get(AppConstants.API_INFO);
		Map<?, ?> resourceInfo = (Map<?, ?>) ((Map<?, ?>) resourceMapping.get(AppConstants.RESOURCES))
				.get(type.toString());

		// Reading API related info
		String querySyntax = (String) apiInfo.get(AppConstants.GRAPH_QUERY_SYNTAX);

		// Reading resource related info
		String resourceNameString = (String) resourceInfo.get(AppConstants.ARCADIA_RESOURCE_NAME);

		Map graphQLQuery = new HashMap<>();
		String query = querySyntax.replace(AppConstants.PERSON_ID_PLACEHOLDER, personId)
				.replace(AppConstants.ARCADIA_RESOURCE_PLACEHOLDER, resourceNameString);
		graphQLQuery.put(AppConstants.QUERY, query);

		Map arcadiaResponse = executeAPIandHandleErrors(graphQLQuery);
		
		arcadiaData = (arcadiaResponse.get(AppConstants.DATA) != null
				&& ((Map) arcadiaResponse.get(AppConstants.DATA)).get(AppConstants.PERSON) != null)
						? (Map) ((Map) arcadiaResponse.get(AppConstants.DATA)).get(AppConstants.PERSON)
						: (Map) new UnclassifiedServerFailureException(500, "Invalid Arcadia Response");

		logger.info("Returning Arcadia entities: " + arcadiaData);
		return arcadiaData;
	}

	private Map executeAPIandHandleErrors(Map graphQLQuery) {
		Map arcadiaResponse = null;
		try {
			logger.debug("Calling GraphQl API with graphQLQuery: {}", graphQLQuery);
			arcadiaResponse = arcadiaClient.executeGraphQlApi(graphQLQuery);
			if (arcadiaResponse.containsKey(AppConstants.ERRORS)) {
				logger.info("GraphQl API for query:" + graphQLQuery
						+ " resulted in error. Convering errors to OperationOutcome");
				OperationOutcome outcome = convertToOperationOutcome(arcadiaResponse);
				throw new EndPointFailureException("Error from Arcadia server", outcome);
			}

		} catch (EndPointFailureException e) {
			logger.error("Exception happend while calling Arcadia APIs: " + e.getMessage());
			throw new UnclassifiedServerFailureException(Constants.STATUS_HTTP_500_INTERNAL_ERROR, e.getMessage(),
					e.getOperationOutcome());
		} catch (Exception e) {
			logger.error("Exception happend while calling Arcadia APIs: " + e.getMessage());
			throw new UnclassifiedServerFailureException(Constants.STATUS_HTTP_500_INTERNAL_ERROR, e.getMessage());
		}
		logger.info("GraphQl API call is successfull. Returned {}", arcadiaResponse);
		return arcadiaResponse;
	}

	private OperationOutcome convertToOperationOutcome(Map arcadiaResponse) {
		List<Map> errors = ((List) arcadiaResponse.get(AppConstants.ERRORS));
		OperationOutcome outcome = new OperationOutcome();
		for (Map error : errors) {
			outcome.addIssue().setSeverity(IssueSeverity.ERROR).setCode(IssueType.INVALID)
					.setDiagnostics((String) error.get("message"));
		}
		return outcome;
	}

	private String getPatientId(List<List<? extends IQueryParameterType>> list) {
		StringBuilder value = new StringBuilder();
		if (list != null) {
			for (List<? extends IQueryParameterType> values : list) {
				for (IQueryParameterType params : values) {
					if (params instanceof ReferenceParam) {
						ReferenceParam id = (ReferenceParam) params;
						if (id.getValue() != null) {
							value.append(id.getValue());
						}
					}
				}
			}
		}
		return value.toString();
	}
}

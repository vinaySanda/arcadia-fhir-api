package io.arcadia.fhir.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverityEnumFactory;
import org.hl7.fhir.r4.model.OperationOutcome.IssueTypeEnumFactory;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnclassifiedServerFailureException;
import io.arcadia.fhir.client.TransformClient;
import io.arcadia.fhir.client.ValidationClient;
import io.arcadia.fhir.query.SearchParameterMap;

/**
 * <p>
 * {@link TransformerUtils} is responsible for transforming Arcadia entities
 * into FHIR resources by calling Transformation microservice and validate the
 * resource by calling Validator microservice
 * </p>
 * 
 * <p>
 * Also it handles the errors and converts them into FHIR specified
 * {@link OperationOutcome}
 * </p>
 * 
 * @author Pradeep Kumara K
 *
 */
@Component
public class TransformerUtils {

	private static final Logger logger = LoggerFactory.getLogger(TransformerUtils.class);

	@Autowired
	FhirContext fhirContext;

	@Autowired
	TransformClient transformClient;

	@Autowired
	ValidationClient validationClient;

	/**
	 * This method is used when a FHIR Resource is mapped to only one Arcadia entity.
	 * 
	 * @param type FHIR {@link ResourceType}
	 * @param arcadiaResouceList List of Arcadia entities of same type.
	 * @param paramMap Input parameters 
	 * @return The Bundle of FHIR Resources. Bundle itself is a FHIR {@link Resource}
	 */
	public Bundle transform(ResourceType type, List<Map> arcadiaResouceList, SearchParameterMap paramMap) {

		logger.info("Inside transform method for Resource " + type + " and Arcadia Entities: " + arcadiaResouceList);

		Bundle bundle = new Bundle();

		List<BundleEntryComponent> fhirResources = new ArrayList<>();
		boolean includeProvenance = false;

		// Reading config file
		Map<?, ?> transformationConfig = null;
		try {
			transformationConfig = ConfigUtils.getConfig(AppConstants.TRANSFORMATION_CONFIG_FILE);
		} catch (IOException e) {
			logger.error("Exception occured while reading config file: " + AppConstants.TRANSFORMATION_CONFIG_FILE);
			throw new ResourceNotFoundException(e.getMessage());
		}

		Map<?, ?> resourceInfo = ConfigUtils.valueMap(ConfigUtils.valueMap(transformationConfig, AppConstants.RESOURCES), type.toString());

		// Reading resource related info
		String structureDefinition = ConfigUtils.valueString(resourceInfo, AppConstants.STRUCTURE_DEFINITION);
		String structureMap = ConfigUtils.valueString(resourceInfo, AppConstants.STRUCTURE_MAP);
		String resourceProfileUrl = ConfigUtils.valueString(resourceInfo, AppConstants.RESOURCE_PROFILE_URL);
		List<String> dateFields = ConfigUtils.valueList(resourceInfo, AppConstants.DATE_FIELDS);
		String provenanceStructureDefinition = ConfigUtils.valueString(resourceInfo, AppConstants.PROVENANCE_STRUCTURE_DEFINITION);
		String provenanceStructureMap = ConfigUtils.valueString(resourceInfo, AppConstants.PROVENANCE_STRUCTURE_MAP);

		if (paramMap.getRevIncludes().size() > 0) {
			if (resolveAndGetRevIncludeValue(paramMap.getRevIncludes()).equals("Provenance:target")) {
				includeProvenance = true;
			}
		}

		for (Map arcadiaResource : arcadiaResouceList) {

			convertDateFormat(arcadiaResource, dateFields);

			arcadiaResource.put(AppConstants.RESOURCE_TYPE, structureDefinition);
			String fhirResource = getFhirResource(structureMap, arcadiaResource, type);
			if (isValidResource(resourceProfileUrl, fhirResource))
				fhirResources.add(toEntryComponent(fhirResource));

			if (includeProvenance) {
				arcadiaResource.put(AppConstants.RESOURCE_TYPE, provenanceStructureDefinition);
				String provenanceString = getFhirResource(provenanceStructureMap, arcadiaResource,
						ResourceType.Provenance);
				if (isValidResource(resourceProfileUrl, provenanceString))
					fhirResources.add(toEntryComponent(provenanceString));
			}
		}

		bundle.setEntry(fhirResources);
		logger.info("Returning Bundle for Resource: " + type);

		return bundle;
	}

	/**
	 * This method is used when a FHIR Resource is mapped to more than one Arcadia entities.
	 * 
	 * @param type FHIR {@link ResourceType}
	 * @param arcadiaData Collection of Arcadia entities of multiple entity type.
	 * @param paramMap Input parameters 
	 * @return The Bundle of FHIR Resources. Bundle itself is a FHIR {@link Resource}
	 */
	public Bundle transform(ResourceType type, Map arcadiaData, SearchParameterMap paramMap) {

		logger.info("Inside transform method for Resource " + type + " and Arcadia Entities: " + arcadiaData);

		Bundle bundle = new Bundle();

		List<BundleEntryComponent> fhirResources = new ArrayList<>();
		boolean includeProvenance = false;

		// Reading config file
		Map<?, ?> transformationConfig = null;
		try {
			transformationConfig = ConfigUtils.getConfig(AppConstants.TRANSFORMATION_CONFIG_FILE);
		} catch (IOException e) {
			logger.error("Exception occured while reading config file: " + AppConstants.TRANSFORMATION_CONFIG_FILE);
			throw new ResourceNotFoundException(e.getMessage());
		}

		List<Map> resourceList = (List<Map>) ((Map<?, ?>) transformationConfig.get(AppConstants.RESOURCES))
				.get(type.toString());

		for (Map resourceInfo : resourceList) {

			// Reading resource related info
			String arcadiaResourceName = (String) resourceInfo.get(AppConstants.ARCADIA_RESOURCE_NAME);

			String structureDefinition = (String) resourceInfo.get(AppConstants.STRUCTURE_DEFINITION);
			String structureMap = (String) resourceInfo.get(AppConstants.STRUCTURE_MAP);
			String resourceProfileUrl = (String) resourceInfo.get(AppConstants.RESOURCE_PROFILE_URL);
			List<String> dateFields = (List<String>) resourceInfo.get(AppConstants.DATE_FIELDS);
			String provenanceStructureDefinition = (String) resourceInfo
					.get(AppConstants.PROVENANCE_STRUCTURE_DEFINITION);
			String provenanceStructureMap = (String) resourceInfo.get(AppConstants.PROVENANCE_STRUCTURE_MAP);

			if (paramMap.getRevIncludes().size() > 0) {
				if (resolveAndGetRevIncludeValue(paramMap.getRevIncludes()).equals("Provenance:target")) {
					includeProvenance = true;
				}
			}

			List<Map> arcadiaResources = new ArrayList<>();
			Object resourceObj = arcadiaData.get(arcadiaResourceName);
			if (resourceObj != null && resourceObj instanceof Map) {
				arcadiaResources.add((Map) resourceObj);
			} else if (resourceObj != null && resourceObj instanceof List) {
				arcadiaResources.addAll((List<Map>) resourceObj);
			}

			for (Map arcadiaResource : arcadiaResources) {

				convertDateFormat(arcadiaResource, dateFields);

				arcadiaResource.put(AppConstants.RESOURCE_TYPE, structureDefinition);
				String fhirResource = getFhirResource(structureMap, arcadiaResource, type);
				if (isValidResource(resourceProfileUrl, fhirResource))
					fhirResources.add(toEntryComponent(fhirResource));

				if (includeProvenance) {
					arcadiaResource.put(AppConstants.RESOURCE_TYPE, provenanceStructureDefinition);
					String provenanceString = getFhirResource(provenanceStructureMap, arcadiaResource,
							ResourceType.Provenance);
					if (isValidResource(resourceProfileUrl, provenanceString))
						fhirResources.add(toEntryComponent(provenanceString));
				}
			}
		}

		bundle.setEntry(fhirResources);
		logger.info("Returning Bundle for Resource: " + type);

		return bundle;
	}

	private String getFhirResource(String structureMap, Map<String, String> arcadiaResource, ResourceType type) {

		String fhirResource = null;
		try {
			logger.debug(
					"Calling Transformation service for Resource " + type + " and Arcadia Entity: " + arcadiaResource);
			fhirResource = transformClient.getFhirResource(structureMap, arcadiaResource);
		} catch (WebClientResponseException e) {
			logger.error("Transoformation failed for Resource " + type
					+ ". Converting response into OperationOutcome. Error message: " + e.getMessage());
			OperationOutcome outcome = toOperationOutcome(e.getResponseBodyAsString());
			throw new UnclassifiedServerFailureException(e.getRawStatusCode(), null, outcome);
		} catch (Exception e) {
			logger.error("Exception occured while calling transformation service. Error message: " + e.getMessage());
			throw new InternalErrorException(e.getMessage());
		}

		logger.debug("Transformation completed for Resource " + type + " Arcadia Entity: " + arcadiaResource);

		return fhirResource;
	}

	private boolean isValidResource(String resourceProfileUrl, String fhirResource) {
		boolean isValid = true;
		try {
			String validationOutcome = validationClient.validateFhirResource(resourceProfileUrl, fhirResource);

			ObjectMapper mapper = new ObjectMapper();
			Map outcomeMap = mapper.readValue(validationOutcome, Map.class);
			List<Map> issues = (List<Map>) outcomeMap.get("issue");

			for (Map issue : issues) {
				String severity = (String) issue.get("severity");
				if (severity != null && (severity.equalsIgnoreCase("error") || severity.equalsIgnoreCase("fatal"))) {
					logger.info("Found errors while validating FHIR Resource: {} and the Validtion Outcome: {}",
							fhirResource, validationOutcome);
					isValid = false;
					break;
				}
			}

		} catch (JsonProcessingException e) {
			logger.error(
					"JsonProcessingException occured while converting outcomeString to OperationOutcome Resource. Error message: "
							+ e.getMessage());
			throw new InternalErrorException(e.getMessage());
		} catch (Exception e) {
			logger.error("Exception occured while calling validation service. Error message: " + e.getMessage());
			throw new InternalErrorException(e.getMessage());
		}
		return isValid;
	}

	private OperationOutcome toOperationOutcome(String outComeString) {
		OperationOutcome outcome = new OperationOutcome();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map outcomeMap = mapper.readValue(outComeString, Map.class);
			List<Map> issues = (List<Map>) outcomeMap.get("issue");

			IssueSeverityEnumFactory isef = new IssueSeverityEnumFactory();
			IssueTypeEnumFactory itef = new IssueTypeEnumFactory();

			for (Map issue : issues) {
				outcome.addIssue().setSeverity(isef.fromCode((String) issue.get("severity")))
						.setCode(itef.fromCode((String) issue.get("code")))
						.setDiagnostics((String) issue.get("diagnostics"));
			}

		} catch (JsonProcessingException e) {
			logger.error(
					"JsonProcessingException occured while converting outcomeString to OperationOutcome Resource. Error message: "
							+ e.getMessage());
			throw new InternalErrorException(e.getMessage());
		}
		return outcome;
	}

	private BundleEntryComponent toEntryComponent(String fhirResourceString) {
		Resource resource = (Resource) fhirContext.newJsonParser().parseResource(fhirResourceString);
		BundleEntryComponent entryComponent = new BundleEntryComponent();
		entryComponent.setResource(resource);
		return entryComponent;
	}

	private String resolveAndGetRevIncludeValue(Set<Include> revIncludes) {
		String revIncludeValue = "";
		if (revIncludes != null && revIncludes.size() > 0) {
			for (Include include : revIncludes) {
				revIncludeValue = include.getValue();
			}
		}
		return revIncludeValue;
	}

	private Map<String, String> convertDateFormat(Map<String, String> arcadiaResource, List<String> dateFields) {

		if (dateFields != null)
			for (String dateField : dateFields) {
				if (null != arcadiaResource.get(dateField)) {
					String date = arcadiaResource.get(dateField).toString();

					DateFormat fhirDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
					DateFormat arcadiaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
					Date parseDate;
					try {
						parseDate = arcadiaDateFormat.parse(date);
						String fhirDate = fhirDateFormat.format(parseDate);
						arcadiaResource.replace(dateField, fhirDate);
					} catch (ParseException e) {
						logger.error("Exception occured while converting date format for date: " + date
								+ " and dateField: " + dateField);
					}

				}
			}
		return arcadiaResource;
	}

}

package io.arcadia.fhir.providers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.CarePlanService;

@Component
public class CarePlanResourceProvider extends AbstractJaxRsResourceProvider<CarePlan> {

	private static final Logger logger = LoggerFactory.getLogger(CarePlanResourceProvider.class);
	@Autowired
	private CarePlanService service;

	public CarePlanResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
	 * The getResourceType method comes from IResourceProvider, and must be
	 * overridden to indicate what type of resource this provider supplies.
	 */
	@Override
	public Class<CarePlan> getResourceType() {
		return CarePlan.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. This operation retrieves a resource by ID. Example URL to invoke
	 * this method: http://<server name>/<context>/fhir/CarePlan/1
	 *
	 * @param request
	 * @param response
	 * @param theId
	 * @return
	 */
	@Read
	public CarePlan readOrVread(HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
		String id;
		CarePlan carePlan = null;
		try {
			id = theId.getIdPart();
			carePlan = service.getCarePlanById(id);
		} catch (Exception e) {
			logger.error("Exception in readOrVread of CarePlanResourceProvider : ", e);
			throw e;
		}
		return carePlan;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 *
	 * @param request
	 * @param response
	 * @param theId
	 * @param theIdentifier
	 * @param theDate
	 * @param theCondition
	 * @param thePatient
	 * @param theStatus
	 * @param theCategory
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public Bundle search(HttpServletRequest request, HttpServletResponse response,
			@Description(shortDefinition = "The resource identity") @OptionalParam(name = CarePlan.SP_RES_ID) TokenAndListParam theId,
			@Description(shortDefinition = "A careplan identifier") @OptionalParam(name = CarePlan.SP_IDENTIFIER) TokenAndListParam theIdentifier,
			@Description(shortDefinition = "Time period plan covers") @OptionalParam(name = CarePlan.SP_DATE) DateRangeParam theDate,
			@Description(shortDefinition = "Health issues this plan addresses") @OptionalParam(name = CarePlan.SP_CONDITION, targetTypes = {
					Condition.class }) ReferenceAndListParam theCondition,
			@Description(shortDefinition = "Who the care plan is for") @OptionalParam(name = CarePlan.SP_PATIENT, targetTypes = {
					Patient.class }) ReferenceAndListParam thePatient,
			@Description(shortDefinition = "draft | active | suspended | completed | entered-in-error | cancelled | unknown") @OptionalParam(name = CarePlan.SP_STATUS) TokenAndListParam theStatus,
			@Description(shortDefinition = "Type of plan") @OptionalParam(name = CarePlan.SP_CATEGORY) TokenAndListParam theCategory,
			@IncludeParam(allow = { "*" }) Set<Include> theIncludes,
			@IncludeParam(reverse = true, allow = { "*" }) Set<Include> theRevIncludes) {

		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(CarePlan.SP_RES_ID, theId);
		paramMap.add(CarePlan.SP_IDENTIFIER, theIdentifier);
		paramMap.add(CarePlan.SP_DATE, theDate);
		paramMap.add(CarePlan.SP_CONDITION, theCondition);
		paramMap.add(CarePlan.SP_PATIENT, thePatient);
		paramMap.add(CarePlan.SP_STATUS, theStatus);
		paramMap.add(CarePlan.SP_CATEGORY, theCategory);
		paramMap.setIncludes(theIncludes);
		paramMap.setRevIncludes(theRevIncludes);
		Bundle bundle = new Bundle();
		try {
			bundle = service.search(paramMap);
		} catch (Exception e) {
			logger.error("Exception in @search of CarePlanResourceProvider : ", e);
			throw e;
		}
		return bundle;
	}
}

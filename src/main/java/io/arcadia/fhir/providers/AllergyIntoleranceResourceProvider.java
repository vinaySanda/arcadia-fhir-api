package io.arcadia.fhir.providers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
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
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.AllergyIntoleranceService;

@Component
public class AllergyIntoleranceResourceProvider extends AbstractJaxRsResourceProvider<AllergyIntolerance> {

	private static final Logger logger = LoggerFactory.getLogger(AllergyIntoleranceResourceProvider.class);

	@Autowired
	private AllergyIntoleranceService service;

	public AllergyIntoleranceResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
	 * The getResourceType method comes from IResourceProvider, and must be
	 * overridden to indicate what type of resource this provider supplies.
	 */
	@Override
	public Class<AllergyIntolerance> getResourceType() {
		return AllergyIntolerance.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. This operation retrieves a resource by ID. Example URL to invoke
	 * this method: "http://<server name>/<context>/fhir/AllergyIntolerance/1"
	 *
	 * @param request
	 * @param response
	 * @param theId
	 * @return
	 */
	@Read
	public AllergyIntolerance readOrVread(HttpServletRequest request, HttpServletResponse response,
			@IdParam IdType theId) {
		String id = theId.getIdPart();
		AllergyIntolerance allergyIntolerance = null;
		
		try {
			allergyIntolerance = service.getAllergyIntoleranceById(id);
		} catch (Exception e) {
			logger.error("Exception in @Search of AllergyIntoleranceResourceProvider : ", e);
			throw e;
		}
		
		return allergyIntolerance;
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
	 * @param theSeverity
	 * @param theDate
	 * @param theManifestation
	 * @param theRecorder
	 * @param theCode
	 * @param theVerificationStatus
	 * @param theCriticality
	 * @param theClinicalStatus
	 * @param theType
	 * @param theOnset
	 * @param theRoute
	 * @param theAsserter
	 * @param thePatient
	 * @param theCategory
	 * @param theLastDate
	 * @param theIncludes
	 * @param theSort
	 * @param theCount
	 * @return
	 */
	@Search()
	public Bundle search(HttpServletRequest request, HttpServletResponse response,

			@Description(shortDefinition = "Who the sensitivity is for") @OptionalParam(name = AllergyIntolerance.SP_PATIENT) ReferenceAndListParam thePatient,

			@IncludeParam(allow = { "*" }) Set<Include> theIncludes,
			@IncludeParam(reverse = true, allow = { "*" }) Set<Include> theRevIncludes) {

		SearchParameterMap paramMap = new SearchParameterMap();

		paramMap.add(AllergyIntolerance.SP_PATIENT, thePatient);

		paramMap.setIncludes(theIncludes);
		paramMap.setRevIncludes(theRevIncludes);
		Bundle bundle = null;
		
		try {
			bundle = service.search(paramMap);
		} catch (Exception e) {
			logger.error("Exception in @Search of AllergyIntoleranceResourceProvider : ", e);
			throw e;
		}
		return bundle;
	}
}

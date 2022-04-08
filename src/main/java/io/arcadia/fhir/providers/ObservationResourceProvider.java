package io.arcadia.fhir.providers;

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
import io.arcadia.fhir.service.ObservationService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObservationResourceProvider extends AbstractJaxRsResourceProvider<Observation> {
  private static final Logger logger = LoggerFactory.getLogger(ObservationResourceProvider.class);

  @Autowired private ObservationService service;

  public ObservationResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Observation> getResourceType() {
    return Observation.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Observation/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Observation readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Observation observation = new Observation();
    try {
      id = theId.getIdPart();
      observation = service.getObservationById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread for ObservationResourceProvider: ", e);
      throw e;
    }
    return observation;
  }

  /**
   * The "@Search" annotation indicates that this method supports the search operation. You may have
   * many different method annotated with this annotation, to support many different search
   * criteria. The search operation returns a bundle with zero-to-many resources of a given type,
   * matching a given set of parameters.
   *
   * @param request
   * @param response
   * @param theId
   * @param theIdentifier
   * @param theDate
   * @param theCode
   * @param thePatient
   * @param theCategory
   * @param theStatus
   * @param theIncludes
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Observation.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "The unique id for a particular observation")
          @OptionalParam(name = Observation.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(
              shortDefinition =
                  "Obtained date/time. If the obtained element is a period, a date that falls in the period")
          @OptionalParam(name = Observation.SP_DATE)
          DateRangeParam theDate,
      @Description(shortDefinition = "The code of the observation type")
          @OptionalParam(name = Observation.SP_CODE)
          TokenAndListParam theCode,
      @Description(shortDefinition = "The subject that the observation is about (if patient)")
          @OptionalParam(name = Observation.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @Description(shortDefinition = "The classification of the type of observation")
          @OptionalParam(name = Observation.SP_CATEGORY)
          TokenAndListParam theCategory,
      @Description(shortDefinition = "The status of the observation")
          @OptionalParam(name = Observation.SP_STATUS)
          TokenAndListParam theStatus,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Observation.SP_RES_ID, theId);
    paramMap.add(Observation.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Observation.SP_STATUS, theStatus);
    paramMap.add(Observation.SP_CATEGORY, theCategory);
    paramMap.add(Observation.SP_CODE, theCode);
    paramMap.add(Observation.SP_PATIENT, thePatient);
    paramMap.add(Observation.SP_DATE, theDate);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search for ObservationResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

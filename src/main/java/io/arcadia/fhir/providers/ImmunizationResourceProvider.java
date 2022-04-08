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
import io.arcadia.fhir.service.ImmunizationService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImmunizationResourceProvider extends AbstractJaxRsResourceProvider<Immunization> {
  private static final Logger logger = LoggerFactory.getLogger(ImmunizationResourceProvider.class);
  @Autowired private ImmunizationService service;

  public ImmunizationResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Immunization> getResourceType() {
    return Immunization.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Immunization/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Immunization readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Immunization immunization = new Immunization();
    try {
      id = theId.getIdPart();
      immunization = service.getImmunizationById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of ImmunizationResourceProvider : ", e);
      throw e;
    }
    return immunization;
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
   * @param theStatusReason
   * @param thePrimarySource
   * @param thePatient
   * @param theVaccineCode
   * @param theStatus
   * @param theIncludes
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Immunization.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "Business identifier")
          @OptionalParam(name = Immunization.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "Vaccination (non)-Administration Date")
          @OptionalParam(name = Immunization.SP_DATE)
          DateRangeParam theDate,
      @Description(shortDefinition = "Reason why the vaccine was not administered")
          @OptionalParam(name = Immunization.SP_STATUS_REASON)
          TokenAndListParam theStatusReason,
      @Description(shortDefinition = "Indicates context the data was recorded in")
          @OptionalParam(name = "primary-source")
          TokenAndListParam thePrimarySource,
      @Description(shortDefinition = "The patient for the vaccination record")
          @OptionalParam(name = Immunization.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @Description(shortDefinition = "Vaccine Product Administered")
          @OptionalParam(name = Immunization.SP_VACCINE_CODE)
          TokenAndListParam theVaccineCode,
      @Description(shortDefinition = "Immunization event status")
          @OptionalParam(name = Immunization.SP_STATUS)
          TokenAndListParam theStatus,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Immunization.SP_RES_ID, theId);
    paramMap.add(Immunization.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Immunization.SP_DATE, theDate);
    paramMap.add(Immunization.SP_STATUS_REASON, theStatusReason);
    paramMap.add(Immunization.SP_PATIENT, thePatient);
    paramMap.add(Immunization.SP_VACCINE_CODE, theVaccineCode);
    paramMap.add(Immunization.SP_STATUS, theStatus);
    paramMap.add("primary-source", thePrimarySource);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search of ImmunizationResourceProvider : ", e);
      throw e;
    }
    return bundle;
  }
}

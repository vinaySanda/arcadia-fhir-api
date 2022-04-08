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
import io.arcadia.fhir.service.EncounterService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterResourceProvider extends AbstractJaxRsResourceProvider<Encounter> {
  private static final Logger logger = LoggerFactory.getLogger(EncounterResourceProvider.class);
  @Autowired private EncounterService service;

  public EncounterResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Encounter> getResourceType() {
    return Encounter.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Encounter/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Encounter readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Encounter encounter = new Encounter();
    try {
      id = theId.getIdPart();
      encounter = service.getEncounterById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread for EncounterResourceProvider : ", e);
      throw e;
    }
    return encounter;
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
   * @param theType
   * @param thePatient
   * @param theClass
   * @param theStatus
   * @param theIncludes
   * @param theSort
   * @param theCount
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Encounter.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "Identifier(s) by which this encounter is knownr")
          @OptionalParam(name = Encounter.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "A date within the period the Encounter lasted")
          @OptionalParam(name = Encounter.SP_DATE)
          DateRangeParam theDate,
      @Description(shortDefinition = "Specific type of encounter")
          @OptionalParam(name = Encounter.SP_TYPE)
          TokenAndListParam theType,
      @Description(shortDefinition = "The patient or group present at the encounter")
          @OptionalParam(name = Encounter.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @Description(shortDefinition = "Classification of patient encounter")
          @OptionalParam(name = Encounter.SP_CLASS)
          TokenAndListParam theClass,
      @Description(
              shortDefinition =
                  "planned | arrived | triaged | in-progress | onleave | finished | cancelled +")
          @OptionalParam(name = Encounter.SP_STATUS)
          TokenAndListParam theStatus,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Encounter.SP_RES_ID, theId);
    paramMap.add(Encounter.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Encounter.SP_DATE, theDate);
    paramMap.add(Encounter.SP_TYPE, theType);
    paramMap.add(Encounter.SP_PATIENT, thePatient);
    paramMap.add(Encounter.SP_CLASS, theClass);
    paramMap.add(Encounter.SP_STATUS, theStatus);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search for EncounterResourceProvider : ", e);
      throw e;
    }
    return bundle;
  }
}

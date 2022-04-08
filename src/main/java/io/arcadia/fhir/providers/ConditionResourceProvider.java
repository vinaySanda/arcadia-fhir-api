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
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.ConditionService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConditionResourceProvider extends AbstractJaxRsResourceProvider<Condition> {

  private static final Logger logger = LoggerFactory.getLogger(ConditionResourceProvider.class);
  @Autowired private ConditionService service;

  public ConditionResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Condition> getResourceType() {
    return Condition.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Condition/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Condition readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Condition conditionList = new Condition();
    try {
      id = theId.getIdPart();
      conditionList = service.getConditionById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread for ConditionResourceProvider : ", e);
      throw e;
    }
    return conditionList;
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
   * @param theClinicalStatus
   * @param theVerificationStatus
   * @param theCategory
   * @param theSeverity
   * @param theCode
   * @param thePatient
   * @param theSort
   * @param theCount
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Condition.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "An Condition identifier")
          @OptionalParam(name = Condition.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "The clinical status of the condition")
          @OptionalParam(name = Condition.SP_CLINICAL_STATUS)
          TokenAndListParam theClinicalStatus,
      @Description(
              shortDefinition =
                  "The verification status to support the clinical status of the condition")
          @OptionalParam(name = Condition.SP_VERIFICATION_STATUS)
          TokenAndListParam theVerificationStatus,
      @Description(shortDefinition = "A category assigned to the condition")
          @OptionalParam(name = Condition.SP_CATEGORY)
          TokenAndListParam theCategory,
      @Description(
              shortDefinition =
                  "A subjective assessment of the severity of the condition as evaluated by the clinician")
          @OptionalParam(name = Condition.SP_SEVERITY)
          TokenAndListParam theSeverity,
      @Description(shortDefinition = "Identification of the condition, problem or diagnosis")
          @OptionalParam(name = Condition.SP_CODE)
          TokenAndListParam theCode,
      @Description(
              shortDefinition =
                  "Indicates the patient or group who the condition record is associated with")
          @OptionalParam(name = Condition.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Condition.SP_RES_ID, theId);
    paramMap.add(Condition.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Condition.SP_CLINICAL_STATUS, theClinicalStatus);
    paramMap.add(Condition.SP_VERIFICATION_STATUS, theVerificationStatus);
    paramMap.add(Condition.SP_CATEGORY, theCategory);
    paramMap.add(Condition.SP_SEVERITY, theSeverity);
    paramMap.add(Condition.SP_CODE, theCode);
    paramMap.add(Condition.SP_PATIENT, thePatient);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search for ConditionResourceProvider : ", e);
      throw e;
    }
    return bundle;
  }
}

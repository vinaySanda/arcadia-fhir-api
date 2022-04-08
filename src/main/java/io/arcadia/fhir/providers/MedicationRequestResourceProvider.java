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
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.MedicationRequestService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicationRequestResourceProvider
    extends AbstractJaxRsResourceProvider<MedicationRequest> {
  private static final Logger logger =
      LoggerFactory.getLogger(MedicationRequestResourceProvider.class);
  @Autowired private MedicationRequestService service;

  public MedicationRequestResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<MedicationRequest> getResourceType() {
    return MedicationRequest.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/MedicationRequest/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public MedicationRequest readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    MedicationRequest medicationRequestList = new MedicationRequest();
    try {
      id = theId.getIdPart();
      medicationRequestList = service.getMedicationRequestById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread for MedicationRequestResourceProvider: ", e);
      throw e;
    }
    return medicationRequestList;
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
   * @param theSubject
   * @param theStatus
   * @param theIntent
   * @param theCategory
   * @param theContext
   * @param thePriority
   * @param theRequester
   * @param theMedication
   * @param thePerformer
   * @param theAuthoredOn
   * @param thePatient
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = MedicationRequest.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "A medicationRequest identifier")
          @OptionalParam(name = MedicationRequest.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "The identity of a patient to list orders  for")
          @OptionalParam(name = MedicationRequest.SP_SUBJECT)
          ReferenceAndListParam theSubject,
      @Description(shortDefinition = "Status of the prescription")
          @OptionalParam(name = MedicationRequest.SP_STATUS)
          TokenAndListParam theStatus,
      @Description(shortDefinition = "Returns prescriptions with different intents")
          @OptionalParam(name = MedicationRequest.SP_INTENT)
          TokenAndListParam theIntent,
      @Description(shortDefinition = "Returns prescriptions with different categories")
          @OptionalParam(name = MedicationRequest.SP_CATEGORY)
          TokenAndListParam theCategory,
      @Description(
              shortDefinition =
                  "Return prescriptions with this encounter or episode of care identifier")
          @OptionalParam(name = MedicationRequest.SP_ENCOUNTER)
          ReferenceAndListParam theContext,
      @Description(shortDefinition = "Returns prescriptions with different priorities")
          @OptionalParam(name = MedicationRequest.SP_PRIORITY)
          TokenAndListParam thePriority,
      @Description(shortDefinition = "Returns prescriptions prescribed by this prescriber")
          @OptionalParam(name = MedicationRequest.SP_REQUESTER)
          ReferenceAndListParam theRequester,
      @Description(shortDefinition = "Returns requests for a specific type of performer")
          @OptionalParam(name = MedicationRequest.SP_MEDICATION)
          ReferenceAndListParam theMedication,
      @Description(shortDefinition = "Returns requests for a specific type of performer")
          @OptionalParam(name = MedicationRequest.SP_INTENDED_PERFORMER)
          ReferenceAndListParam thePerformer,
      @Description(shortDefinition = "Return prescriptions written on this date")
          @OptionalParam(name = MedicationRequest.SP_AUTHOREDON)
          DateAndListParam theAuthoredOn,
      @Description(shortDefinition = "Returns prescriptions for a specific patient")
          @OptionalParam(name = MedicationRequest.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {
    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(MedicationRequest.SP_RES_ID, theId);
    paramMap.add(MedicationRequest.SP_IDENTIFIER, theIdentifier);
    paramMap.add(MedicationRequest.SP_SUBJECT, theSubject);
    paramMap.add(MedicationRequest.SP_STATUS, theStatus);
    paramMap.add(MedicationRequest.SP_INTENT, theIntent);
    paramMap.add(MedicationRequest.SP_CATEGORY, theCategory);
    paramMap.add(MedicationRequest.SP_ENCOUNTER, theContext);
    paramMap.add(MedicationRequest.SP_PRIORITY, thePriority);
    paramMap.add(MedicationRequest.SP_REQUESTER, theRequester);
    paramMap.add(MedicationRequest.SP_MEDICATION, theMedication);
    paramMap.add(MedicationRequest.SP_INTENDED_PERFORMER, thePerformer);
    paramMap.add(MedicationRequest.SP_AUTHOREDON, theAuthoredOn);
    paramMap.add(MedicationRequest.SP_PATIENT, thePatient);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search for MedicationRequestResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

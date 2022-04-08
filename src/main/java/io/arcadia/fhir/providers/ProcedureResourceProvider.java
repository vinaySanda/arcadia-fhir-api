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
import io.arcadia.fhir.service.ProcedureService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcedureResourceProvider extends AbstractJaxRsResourceProvider<Procedure> {
  private static final Logger logger = LoggerFactory.getLogger(ProcedureResourceProvider.class);
  @Autowired private ProcedureService service;

  public ProcedureResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Procedure> getResourceType() {
    return Procedure.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Procedure/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Procedure readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Procedure procedureList = new Procedure();
    try {
      id = theId.getIdPart();
      procedureList = service.getProcedureById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of ProcedureResourceProvider: ", e);
      throw e;
    }
    return procedureList;
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
   * @param theSubject
   * @param thePatient
   * @param theStatus
   * @param theIncludes
   * @return
   * @throws IOException
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Procedure.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "A Procedure identifier")
          @OptionalParam(name = Procedure.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "When the procedure was performed")
          @OptionalParam(name = Procedure.SP_DATE)
          DateRangeParam theDate,
      @Description(shortDefinition = "A code to identify a  procedure")
          @OptionalParam(name = Procedure.SP_CODE)
          TokenAndListParam theCode,
      @Description(shortDefinition = "A code to identify a  procedure")
          @OptionalParam(name = Procedure.SP_SUBJECT)
          ReferenceAndListParam theSubject,
      @Description(shortDefinition = "Search by subject - a patient")
          @OptionalParam(name = Procedure.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @Description(
              shortDefinition =
                  "preparation | in-progress | not-done | suspended | aborted | completed | entered-in-error | unknown")
          @OptionalParam(name = Procedure.SP_STATUS)
          TokenAndListParam theStatus,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Procedure.SP_RES_ID, theId);
    paramMap.add(Procedure.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Procedure.SP_DATE, theDate);
    paramMap.add(Procedure.SP_CODE, theCode);
    paramMap.add(Procedure.SP_SUBJECT, theSubject);
    paramMap.add(Procedure.SP_PATIENT, thePatient);
    paramMap.add(Procedure.SP_STATUS, theStatus);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
		bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search of ProcedureResourceProvider ", e);
      throw e;
    }
    return bundle;
  }
}

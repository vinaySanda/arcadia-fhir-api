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
import io.arcadia.fhir.service.DiagnosticReportService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticReportResourceProvider
    extends AbstractJaxRsResourceProvider<DiagnosticReport> {
  private static final Logger logger =
      LoggerFactory.getLogger(DiagnosticReportResourceProvider.class);
  @Autowired private DiagnosticReportService service;

  public DiagnosticReportResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<DiagnosticReport> getResourceType() {
    return DiagnosticReport.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/DiagnosticReport/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public DiagnosticReport readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    DiagnosticReport diagnosticReports = new DiagnosticReport();
    try {
      id = theId.getIdPart();
      diagnosticReports = service.getDiagnosticReportById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of DiagnosticReportResourceProvider : ", e);
      throw e;
    }
    return diagnosticReports;
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
   * @param theStatus
   * @param theCode
   * @param theCategory
   * @param theDate
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
          @OptionalParam(name = DiagnosticReport.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "An DiagnosticReport  identifier")
          @OptionalParam(name = DiagnosticReport.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "The status of the diagnostic report")
          @OptionalParam(name = DiagnosticReport.SP_STATUS)
          TokenAndListParam theStatus,
      @Description(shortDefinition = "A code or name that describes this diagnostic report")
          @OptionalParam(name = DiagnosticReport.SP_CODE)
          TokenAndListParam theCode,
      @Description(shortDefinition = "This is used for searching, sorting and display purposes.")
          @OptionalParam(name = DiagnosticReport.SP_CATEGORY)
          TokenAndListParam theCategory,
      @Description(shortDefinition = "The time or time-period the observed values are related to")
          @OptionalParam(name = DiagnosticReport.SP_DATE)
          DateAndListParam theDate,
      @Description(shortDefinition = "The subject of the report if a patient")
          @OptionalParam(name = DiagnosticReport.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(DiagnosticReport.SP_RES_ID, theId);
    paramMap.add(DiagnosticReport.SP_IDENTIFIER, theIdentifier);
    paramMap.add(DiagnosticReport.SP_STATUS, theStatus);
    paramMap.add(DiagnosticReport.SP_CODE, theCode);
    paramMap.add(DiagnosticReport.SP_CATEGORY, theCategory);
    paramMap.add(DiagnosticReport.SP_DATE, theDate);
    paramMap.add(DiagnosticReport.SP_PATIENT, thePatient);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search of DiagnosticReportResourceProvider : ", e);
      throw e;
    }
    return bundle;
  }
}

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
import io.arcadia.fhir.service.DocumentReferenceService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentReferenceResourceProvider
    extends AbstractJaxRsResourceProvider<DocumentReference> {
  private static final Logger logger =
      LoggerFactory.getLogger(DocumentReferenceResourceProvider.class);
  @Autowired private DocumentReferenceService service;

  public DocumentReferenceResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<DocumentReference> getResourceType() {
    return DocumentReference.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/DocumentReference/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public DocumentReference readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    DocumentReference documentReference = new DocumentReference();
    try {
      id = theId.getIdPart();
      documentReference = service.getDocumentReferenceById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread for DocumentReferenceResourceProvider: ", e);
      throw e;
    }
    return documentReference;
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
   * @param theCategory
   * @param theStatus
   * @param thePeriod
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
          @OptionalParam(name = DocumentReference.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "A DocumentReference identifier")
          @OptionalParam(name = DocumentReference.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "When this document reference was created")
          @OptionalParam(name = DocumentReference.SP_DATE)
          DateRangeParam theDate,
      @Description(shortDefinition = "Kind of document (LOINC if possible)")
          @OptionalParam(name = DocumentReference.SP_TYPE)
          TokenAndListParam theType,
      @Description(shortDefinition = "Who/what is the subject of the document")
          @OptionalParam(name = DocumentReference.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @Description(shortDefinition = "Categorization of document")
          @OptionalParam(name = DocumentReference.SP_CATEGORY)
          TokenAndListParam theCategory,
      @Description(shortDefinition = "current | superseded | entered-in-error")
          @OptionalParam(name = DocumentReference.SP_STATUS)
          TokenAndListParam theStatus,
      @Description(shortDefinition = "Time of service that is being documented")
          @OptionalParam(name = DocumentReference.SP_PERIOD)
          DateRangeParam thePeriod,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(DocumentReference.SP_RES_ID, theId);
    paramMap.add(DocumentReference.SP_IDENTIFIER, theIdentifier);
    paramMap.add(DocumentReference.SP_DATE, theDate);
    paramMap.add(DocumentReference.SP_TYPE, theType);
    paramMap.add(DocumentReference.SP_PATIENT, thePatient);
    paramMap.add(DocumentReference.SP_CATEGORY, theCategory);
    paramMap.add(DocumentReference.SP_STATUS, theStatus);
    paramMap.add(DocumentReference.SP_PERIOD, thePeriod);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search for DocumentReferenceResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

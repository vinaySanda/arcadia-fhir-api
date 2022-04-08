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
import io.arcadia.fhir.service.ProvenanceService;
import java.io.IOException;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Provenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProvenanceResourceProviders extends AbstractJaxRsResourceProvider<Provenance> {
  private static final Logger logger = LoggerFactory.getLogger(ProvenanceResourceProviders.class);
  @Autowired private ProvenanceService service;

  public ProvenanceResourceProviders(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Provenance> getResourceType() {
    return Provenance.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Provenance/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   * @throws IOException
   */
  @Read
  public Provenance readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId)
      throws IOException {
    String id;
    Provenance provenanceList = new Provenance();
    try {
      id = theId.getIdPart();
      provenanceList = service.getProvenanceById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of ProvenanceResourceProvider: ", e);
      throw e;
    }
    return provenanceList;
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
   * @param thePatient
   * @param theIncludes
   * @return
   * @throws IOException
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Provenance.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "Search by subject - a patient")
          @OptionalParam(name = Provenance.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes)
      throws IOException {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Provenance.SP_RES_ID, theId);
    paramMap.add(Provenance.SP_PATIENT, thePatient);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in search of ProvenanceResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

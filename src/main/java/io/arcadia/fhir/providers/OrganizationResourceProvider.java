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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.OrganizationService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganizationResourceProvider extends AbstractJaxRsResourceProvider<Organization> {
  private static final Logger logger = LoggerFactory.getLogger(OrganizationResourceProvider.class);

  @Autowired private OrganizationService service;

  public OrganizationResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Organization> getResourceType() {
    return Organization.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Organization/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Organization readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Organization organizations = new Organization();
    try {
      id = theId.getIdPart();
      organizations = service.getOrganizationById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread for OrganizationResourceProvider: ", e);
      throw e;
    }
    return organizations;
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
   * @param thePartOf
   * @param theAddress
   * @param theAddressState
   * @param theActive
   * @param theType
   * @param thePostalCode
   * @param theAddressCountry
   * @param theEndPoint
   * @param theEndpointOrganizationList
   * @param theAddressUse
   * @param theName
   * @param theAddressCity
   * @param theTelecom
   * @param thePartofAddressList
   * @param thePartofNameList
   * @param thePartofTypeList
   * @param theCoverageArea
   * @param theIncludes
   * @param theRevIncludes
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Organization.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "An Organization  identifier")
          @OptionalParam(name = Organization.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "The organization of which this organization forms a part")
          @OptionalParam(name = Organization.SP_PARTOF)
          ReferenceAndListParam thePartOf,
      @Description(shortDefinition = "Visiting or addresses for the contact")
          @OptionalParam(name = Organization.SP_ADDRESS)
          StringAndListParam theAddress,
      @Description(shortDefinition = "Visiting or state addresses for the contact")
          @OptionalParam(name = Organization.SP_ADDRESS_STATE)
          StringAndListParam theAddressState,
      @Description(shortDefinition = "Whether the organization record is active")
          @OptionalParam(name = Organization.SP_ACTIVE)
          TokenAndListParam theActive,
      @Description(shortDefinition = "The kind(s) of organization that this is.")
          @OptionalParam(name = Organization.SP_TYPE)
          TokenAndListParam theType,
      @Description(shortDefinition = "Visiting or postal addresses for the contact")
          @OptionalParam(name = Organization.SP_ADDRESS_POSTALCODE)
          StringAndListParam thePostalCode,
      @Description(shortDefinition = "Visiting or country addresses for the contact")
          @OptionalParam(name = Organization.SP_ADDRESS_COUNTRY)
          StringAndListParam theAddressCountry,
      @Description(
              shortDefinition =
                  "Technical endpoints providing access to services operated for the organization")
          @OptionalParam(name = Organization.SP_ENDPOINT)
          ReferenceAndListParam theEndPoint,
      @Description(shortDefinition = "Visiting or  addresses for the contact")
          @OptionalParam(name = Organization.SP_ADDRESS_USE)
          TokenAndListParam theAddressUse,
      @Description(shortDefinition = "An Organization  name")
          @OptionalParam(name = Organization.SP_NAME)
          StringAndListParam theName,
      @Description(shortDefinition = "A city specified in an address")
          @OptionalParam(name = Organization.SP_ADDRESS_CITY)
          StringAndListParam theAddressCity,
      @Description(shortDefinition = "The value in any kind of telecom details of the organization")
          @OptionalParam(name = "telecom")
          StringAndListParam theTelecom,
      @Description(
              shortDefinition =
                  "Select health insurance provider networks available in a region described by the specified location")
          @OptionalParam(name = "coverage-area")
          ReferenceAndListParam theCoverageArea,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Organization.SP_RES_ID, theId);
    paramMap.add(Organization.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Organization.SP_PARTOF, thePartOf);
    paramMap.add(Organization.SP_ADDRESS, theAddress);
    paramMap.add(Organization.SP_ADDRESS_STATE, theAddressState);
    paramMap.add(Organization.SP_ACTIVE, theActive);
    paramMap.add(Organization.SP_TYPE, theType);
    paramMap.add(Organization.SP_ADDRESS_CITY, theAddressCity);
    paramMap.add(Organization.SP_NAME, theName);
    paramMap.add(Organization.SP_ADDRESS_POSTALCODE, thePostalCode);
    paramMap.add(Organization.SP_ADDRESS_COUNTRY, theAddressCountry);
    paramMap.add(Organization.SP_ENDPOINT, theEndPoint);
    paramMap.add(Organization.SP_ADDRESS_USE, theAddressUse);
    paramMap.add("telecom", theTelecom);
    paramMap.add("coverage-area", theCoverageArea);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search for OrganizationResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

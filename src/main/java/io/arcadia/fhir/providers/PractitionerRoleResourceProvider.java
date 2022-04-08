package io.arcadia.fhir.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.PractitionerRoleService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PractitionerRoleResourceProvider
    extends AbstractJaxRsResourceProvider<PractitionerRole> {
  private static final Logger logger =
      LoggerFactory.getLogger(PractitionerRoleResourceProvider.class);

  @Autowired private PractitionerRoleService service;

  public PractitionerRoleResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  @Override
  public Class<PractitionerRole> getResourceType() {
    return PractitionerRole.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/PractitionerRole/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public PractitionerRole readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    PractitionerRole practitionerRoleList = new PractitionerRole();
    try {
      id = theId.getIdPart();
      practitionerRoleList = service.getPractitionerRoleById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of PractitionerRoleResourceProvider: ", e);
      throw e;
    }
    return practitionerRoleList;
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
   * @param theTelecom
   * @param theEmail
   * @param theActive
   * @param thePhone
   * @param thePractitioner
   * @param theOrganization
   * @param theCode
   * @param theSpecialty
   * @param theLocation
   * @param theEndpoint
   * @param theService
   * @param theRole
   * @param theEndpointOrganizationList
   * @param theLocationAddressList
   * @param theLocationAddressPostalcodeList
   * @param theLocationAddressCityList
   * @param theLocationAddressStateList
   * @param theLocationOrganizationList
   * @param theLocationTypeList
   * @param theNetworkNameList
   * @param theNetworkPartofList
   * @param theNetworkAddressList
   * @param theNetworkTypeList
   * @param thePractitionerNameList
   * @param theOrganizationNameList
   * @param theOrganizationAddressList
   * @param theOrganizationPartofList
   * @param theOrganizationTypeList
   * @param theServiceServiceCategoryList
   * @param theServiceOrganizationList
   * @param theServiceLocationList
   * @param theNetwork
   * @param theIncludes
   * @param theRevIncludes
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = PractitionerRole.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "A PractitionerRole identifier")
          @OptionalParam(name = PractitionerRole.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(
              shortDefinition = "The value in any kind of telecom details of the PractitionerRole")
          @OptionalParam(name = PractitionerRole.SP_TELECOM)
          TokenAndListParam theTelecom,
      @Description(
              shortDefinition =
                  "A value in an email contact.PractitionerRole.telecom.where(system='email')")
          @OptionalParam(name = PractitionerRole.SP_EMAIL)
          TokenAndListParam theEmail,
      @Description(shortDefinition = "Whether the PractitionerRole record is active")
          @OptionalParam(name = PractitionerRole.SP_ACTIVE)
          TokenAndListParam theActive,
      @Description(
              shortDefinition =
                  "A value in a phone contact. Path: PractitionerRole.telecom(system=phone)")
          @OptionalParam(name = PractitionerRole.SP_PHONE)
          TokenAndListParam thePhone,
      @Description(
              shortDefinition =
                  "Practitioner that is able to provide the defined services for the organization")
          @OptionalParam(name = PractitionerRole.SP_PRACTITIONER)
          ReferenceAndListParam thePractitioner,
      @Description(
              shortDefinition =
                  "The identity of the organization the practitioner represents / acts on behalf of")
          @OptionalParam(
              name = PractitionerRole.SP_ORGANIZATION,
              targetTypes = {Organization.class})
          ReferenceAndListParam theOrganization,
      @Description(shortDefinition = "Whether the PractitionerRole record is active")
          @OptionalParam(name = "code")
          TokenAndListParam theCode,
      @Description(shortDefinition = "The practitioner has this specialty at an organization")
          @OptionalParam(name = PractitionerRole.SP_SPECIALTY)
          TokenAndListParam theSpecialty,
      @Description(
              shortDefinition = "One of the locations at which this practitioner provides care")
          @OptionalParam(name = PractitionerRole.SP_LOCATION)
          ReferenceAndListParam theLocation,
      @Description(
              shortDefinition =
                  "Technical endpoints providing access to services operated for the practitioner with this role")
          @OptionalParam(name = PractitionerRole.SP_ENDPOINT)
          ReferenceAndListParam theEndpoint,
      @Description(
              shortDefinition =
                  "The list of healthcare services that this worker provides for this role's Organization/Location(s)")
          @OptionalParam(name = PractitionerRole.SP_SERVICE)
          ReferenceAndListParam theService,
      @Description(
              shortDefinition = "The practitioner can perform this role at for the organization")
          @OptionalParam(name = PractitionerRole.SP_ROLE)
          TokenAndListParam theRole,
      @Description(
              shortDefinition =
                  "Select roles where the practitioner is a member of the specified health insurance provider network")
          @OptionalParam(name = "network")
          ReferenceAndListParam theNetwork,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes,
      @Sort SortSpec theSort,
      @Count Integer theCount) {
    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(PractitionerRole.SP_RES_ID, theId);
    paramMap.add(PractitionerRole.SP_IDENTIFIER, theIdentifier);
    paramMap.add(PractitionerRole.SP_TELECOM, theTelecom);
    paramMap.add(PractitionerRole.SP_EMAIL, theEmail);
    paramMap.add(PractitionerRole.SP_ACTIVE, theActive);
    paramMap.add(PractitionerRole.SP_PRACTITIONER, thePractitioner);
    paramMap.add(PractitionerRole.SP_LOCATION, theLocation);
    paramMap.add(PractitionerRole.SP_ENDPOINT, theEndpoint);
    paramMap.add(PractitionerRole.SP_ORGANIZATION, theOrganization);
    paramMap.add(PractitionerRole.SP_SPECIALTY, theSpecialty);
    paramMap.add("code", theCode);
    paramMap.add("network", theNetwork);
    paramMap.add(PractitionerRole.SP_ROLE, theRole);
    paramMap.add(PractitionerRole.SP_SERVICE, theService);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search of PractitionerRoleResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

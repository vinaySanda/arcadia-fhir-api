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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.PractitionerService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PractitionerResourceProvider extends AbstractJaxRsResourceProvider<Practitioner> {
  private static final Logger logger = LoggerFactory.getLogger(PractitionerResourceProvider.class);

  @Autowired private PractitionerService service;

  public PractitionerResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Practitioner> getResourceType() {
    return Practitioner.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Practitioner/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Practitioner readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Practitioner practitionerList = new Practitioner();
    try {
      id = theId.getIdPart();
      practitionerList = service.getPractitionerById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of PractitionerResourceProvider ", e);
      throw e;
    }
    return practitionerList;
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
   * @param theName
   * @param theFamily
   * @param theGiven
   * @param theCommunication
   * @param theTelecom
   * @param theAddress
   * @param theAddressCity
   * @param theAddressState
   * @param theAddressPostalcode
   * @param theAddressCountry
   * @param theGender
   * @param theEmail
   * @param theAddressUse
   * @param theActive
   * @param thePhone
   * @param theIncludes
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Practitioner.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "A Practitioner identifier")
          @OptionalParam(name = Practitioner.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "A portion of either family or given name of the Practitioner")
          @OptionalParam(name = Practitioner.SP_NAME)
          StringAndListParam theName,
      @Description(shortDefinition = "A portion of the family name")
          @OptionalParam(name = Practitioner.SP_FAMILY)
          StringAndListParam theFamily,
      @Description(shortDefinition = "A portion of the given name of the Practitioner")
          @OptionalParam(name = Practitioner.SP_GIVEN)
          StringAndListParam theGiven,
      @Description(
              shortDefinition = "One of the languages that the practitioner can communicate with")
          @OptionalParam(name = Practitioner.SP_COMMUNICATION)
          TokenAndListParam theCommunication,
      @Description(shortDefinition = "The value in any kind of telecom details of the Practitioner")
          @OptionalParam(name = Practitioner.SP_TELECOM)
          TokenAndListParam theTelecom,
      @Description(shortDefinition = "An address in any kind of address/part of the Practitioner")
          @OptionalParam(name = Practitioner.SP_ADDRESS)
          StringAndListParam theAddress,
      @Description(shortDefinition = "A city specified in an address")
          @OptionalParam(name = Practitioner.SP_ADDRESS_CITY)
          StringAndListParam theAddressCity,
      @Description(shortDefinition = "A state specified in an address")
          @OptionalParam(name = Practitioner.SP_ADDRESS_STATE)
          StringAndListParam theAddressState,
      @Description(shortDefinition = "A postalCode specified in an address")
          @OptionalParam(name = Practitioner.SP_ADDRESS_POSTALCODE)
          StringAndListParam theAddressPostalcode,
      @Description(shortDefinition = "A country specified in an address")
          @OptionalParam(name = Practitioner.SP_ADDRESS_COUNTRY)
          StringAndListParam theAddressCountry,
      @Description(shortDefinition = "Gender of the Practitioner")
          @OptionalParam(name = Practitioner.SP_GENDER)
          TokenAndListParam theGender,
      @Description(
              shortDefinition =
                  "A value in an email contact.Practitioner.telecom.where(system='email')")
          @OptionalParam(name = Practitioner.SP_EMAIL)
          TokenAndListParam theEmail,
      @Description(shortDefinition = "A use code specified in an address.Practitioner.address.use")
          @OptionalParam(name = Practitioner.SP_ADDRESS_USE)
          TokenAndListParam theAddressUse,
      @Description(shortDefinition = "Whether the Practitioner record is active")
          @OptionalParam(name = Practitioner.SP_ACTIVE)
          TokenAndListParam theActive,
      @Description(
              shortDefinition =
                  "A value in a phone contact. Path: Practitioner.telecom(system=phone)")
          @OptionalParam(name = Practitioner.SP_PHONE)
          TokenAndListParam thePhone,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {
    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Practitioner.SP_RES_ID, theId);
    paramMap.add(Practitioner.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Practitioner.SP_NAME, theName);
    paramMap.add(Practitioner.SP_FAMILY, theFamily);
    paramMap.add(Practitioner.SP_GIVEN, theGiven);
    paramMap.add(Practitioner.SP_COMMUNICATION, theCommunication);
    paramMap.add(Practitioner.SP_TELECOM, theTelecom);
    paramMap.add(Practitioner.SP_ADDRESS, theAddress);
    paramMap.add(Practitioner.SP_ADDRESS_CITY, theAddressCity);
    paramMap.add(Practitioner.SP_ADDRESS_STATE, theAddressState);
    paramMap.add(Practitioner.SP_ADDRESS_POSTALCODE, theAddressPostalcode);
    paramMap.add(Practitioner.SP_ADDRESS_COUNTRY, theAddressCountry);
    paramMap.add(Practitioner.SP_GENDER, theGender);
    paramMap.add(Practitioner.SP_EMAIL, theEmail);
    paramMap.add(Practitioner.SP_ADDRESS_USE, theAddressUse);
    paramMap.add(Practitioner.SP_ACTIVE, theActive);
    paramMap.add(Practitioner.SP_PHONE, thePhone);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @Search of PractitionerResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

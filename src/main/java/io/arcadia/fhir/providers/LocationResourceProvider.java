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
import ca.uhn.fhir.rest.param.HasAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.SpecialAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.LocationService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationResourceProvider extends AbstractJaxRsResourceProvider<Location> {
  private static final Logger logger = LoggerFactory.getLogger(LocationResourceProvider.class);

  @Autowired private LocationService service;

  public LocationResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Location> getResourceType() {
    return Location.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Location/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Location readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Location theLocation = new Location();
    try {
      id = theId.getIdPart();
      theLocation = service.getLocationById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread for LocationResourceProvider: ", e);
      throw e;
    }
    return theLocation;
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
   * @param thePartOf
   * @param theAddress
   * @param theAddressState
   * @param theOperationalStatus
   * @param theType
   * @param theAddressPostalCode
   * @param theAddressCountry
   * @param theEndpoint
   * @param theOrganization
   * @param theAddressUse
   * @param theNear
   * @param theAddressCity
   * @param theStatus
   * @param theTelecom
   * @param theIncludes
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The ID of the resource")
          @OptionalParam(name = Location.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "An identifier for the location")
          @OptionalParam(name = Location.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "A portion of the location's name or alias")
          @OptionalParam(name = Location.SP_NAME)
          StringAndListParam theName,
      @Description(shortDefinition = "A location of which this location is a part")
          @OptionalParam(name = Location.SP_PARTOF)
          ReferenceAndListParam thePartOf,
      @Description(shortDefinition = "A (part of the) address of the location")
          @OptionalParam(name = Location.SP_ADDRESS)
          StringAndListParam theAddress,
      @Description(shortDefinition = "A state specified in an address")
          @OptionalParam(name = Location.SP_ADDRESS_STATE)
          StringAndListParam theAddressState,
      @Description(
              shortDefinition =
                  "Searches for locations (typically bed/room) that have an operational status (e.g. contaminated, housekeeping)")
          @OptionalParam(name = Location.SP_OPERATIONAL_STATUS)
          TokenAndListParam theOperationalStatus,
      @Description(shortDefinition = "A code for the type of location")
          @OptionalParam(name = Location.SP_TYPE)
          TokenAndListParam theType,
      @Description(shortDefinition = "A postal code specified in an address")
          @OptionalParam(name = Location.SP_ADDRESS_POSTALCODE)
          StringAndListParam theAddressPostalCode,
      @Description(shortDefinition = "A country specified in an address")
          @OptionalParam(name = Location.SP_ADDRESS_COUNTRY)
          StringAndListParam theAddressCountry,
      @Description(
              shortDefinition =
                  "Technical endpoints providing access to services operated for the location")
          @OptionalParam(name = Location.SP_ENDPOINT)
          ReferenceAndListParam theEndpoint,
      @Description(
              shortDefinition =
                  "Searches for locations that are managed by the provided organization")
          @OptionalParam(name = Location.SP_ORGANIZATION)
          ReferenceAndListParam theOrganization,
      @Description(shortDefinition = "A use code specified in an address")
          @OptionalParam(name = Location.SP_ADDRESS_USE)
          TokenAndListParam theAddressUse,
      @Description(
              shortDefinition =
                  "Search for locations where the location.position is near to, or within a specified distance of, the provided coordinates expressed as [latitude]|[longitude]|[distance]|[units] (using the WGS84 datum, see notes).\r\n"
                      + "If the units are omitted, then kms should be assumed. If the distance is omitted, then the server can use its own discression as to what distances should be considered near (and units are irrelevant)\r\n"
                      + "\r\n"
                      + "Servers may search using various techniques that might have differing accuracies, depending on implementation efficiency.\r\n"
                      + "\r\n"
                      + "Requires the near-distance parameter to be provided also")
          @OptionalParam(name = Location.SP_NEAR)
          SpecialAndListParam theNear,
      @Description(shortDefinition = "A city specified in an address")
          @OptionalParam(name = Location.SP_ADDRESS_CITY)
          StringAndListParam theAddressCity,
      @Description(shortDefinition = "Searches for locations with a specific kind of status")
          @OptionalParam(name = Location.SP_STATUS)
          TokenAndListParam theStatus,
      @OptionalParam(name = "_has") HasAndListParam theHasParamList,
      @Description(shortDefinition = "The value in any kind of telecom details of the location")
          @OptionalParam(name = "telecom")
          TokenAndListParam theTelecom,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap searchParameterMap = new SearchParameterMap();
    searchParameterMap.add(Location.SP_RES_ID, theId);
    searchParameterMap.add(Location.SP_IDENTIFIER, theIdentifier);
    searchParameterMap.add(Location.SP_NAME, theName);
    searchParameterMap.add(Location.SP_ADDRESS, theAddress);
    searchParameterMap.add(Location.SP_ADDRESS_CITY, theAddressCity);
    searchParameterMap.add(Location.SP_ADDRESS_COUNTRY, theAddressCountry);
    searchParameterMap.add(Location.SP_ADDRESS_POSTALCODE, theAddressPostalCode);
    searchParameterMap.add(Location.SP_ADDRESS_STATE, theAddressState);
    searchParameterMap.add(Location.SP_ADDRESS_USE, theAddressUse);
    searchParameterMap.add(Location.SP_ENDPOINT, theEndpoint);
    searchParameterMap.add(Location.SP_NEAR, theNear);
    searchParameterMap.add(Location.SP_ORGANIZATION, theOrganization);
    searchParameterMap.add(Location.SP_STATUS, theStatus);
    searchParameterMap.add("_has", theHasParamList);
    searchParameterMap.add("telecom", theTelecom);
    searchParameterMap.add(Location.SP_PARTOF, thePartOf);
    searchParameterMap.add(Location.SP_TYPE, theType);
    searchParameterMap.add(Location.SP_OPERATIONAL_STATUS, theOperationalStatus);
    searchParameterMap.setIncludes(theIncludes);
    searchParameterMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(searchParameterMap);
    } catch (Exception e) {
      logger.error("Exception in @Search for LocationResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

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
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.DeviceService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceResourceProvider extends AbstractJaxRsResourceProvider<Device> {
  private static final Logger logger = LoggerFactory.getLogger(DeviceResourceProvider.class);
  @Autowired private DeviceService service;

  public DeviceResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<Device> getResourceType() {
    return Device.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/Device/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public Device readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    Device device = new Device();
    try {
      id = theId.getIdPart();
      device = service.getDeviceById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of DeviceResourceProvider: ", e);
      throw e;
    }
    return device;
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
   * @param theCarrier
   * @param theUdiDI
   * @param theStatus
   * @param theManufacturer
   * @param theExpirationDate
   * @param theManufactureDate
   * @param thelotNumber
   * @param theSerialNumber
   * @param theDistinctIdentifier
   * @param theDeviceName
   * @param theType
   * @param theModelNumber
   * @param thePatient
   * @param theOrganization
   * @param theSort
   * @param theCount
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "The resource identity")
          @OptionalParam(name = Device.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "An Device  identifier")
          @OptionalParam(name = Device.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(
              shortDefinition =
                  "Unique device identifier (UDI) assigned to device label or package")
          @OptionalParam(name = Device.SP_UDI_CARRIER)
          StringAndListParam theCarrier,
      @Description(shortDefinition = "The udi Device Identifier (DI)")
          @OptionalParam(name = Device.SP_UDI_DI)
          StringAndListParam theUdiDI,
      @Description(shortDefinition = "Status of the Device availability")
          @OptionalParam(name = Device.SP_STATUS)
          TokenAndListParam theStatus,
      @Description(shortDefinition = "A name of the manufacturer")
          @OptionalParam(name = Device.SP_MANUFACTURER)
          StringAndListParam theManufacturer,
      @Description(
              shortDefinition =
                  "The date and time beyond which this device is no longer valid or should not be used")
          @OptionalParam(name = "expiration-date")
          DateRangeParam theExpirationDate,
      @Description(shortDefinition = "Date when the device was made")
          @OptionalParam(name = "manufacture-date")
          DateRangeParam theManufactureDate,
      @Description(shortDefinition = "Lot number assigned by the manufacturer")
          @OptionalParam(name = "lot-number")
          StringAndListParam thelotNumber,
      @Description(
              shortDefinition =
                  "The serial number assigned by the organization when the device was manufactured")
          @OptionalParam(name = "serial-number")
          StringAndListParam theSerialNumber,
      @Description(shortDefinition = "The distinct identification string")
          @OptionalParam(name = "distinct-identifier")
          StringAndListParam theDistinctIdentifier,
      @Description(shortDefinition = "The type of deviceName")
          @OptionalParam(name = Device.SP_DEVICE_NAME)
          StringAndListParam theDeviceName,
      @Description(
              shortDefinition =
                  "Technical endpoints providing access to services operated for the organization")
          @OptionalParam(name = Device.SP_TYPE)
          TokenAndListParam theType,
      @Description(shortDefinition = "The model number for the device")
          @OptionalParam(name = Device.SP_MODEL)
          StringAndListParam theModelNumber,
      @Description(shortDefinition = "Patient information, If the device is affixed to a person")
          @OptionalParam(name = Device.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @Description(
              shortDefinition =
                  "An organization that is responsible for the provision and ongoing maintenance of the device")
          @OptionalParam(name = Device.SP_ORGANIZATION)
          StringAndListParam theOrganization,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes,
      @Sort SortSpec theSort,
      @Count Integer theCount,
      @OptionalParam(name = "search-offset") StringAndListParam theSearchOffset,
      @OptionalParam(name = "ixid") StringAndListParam theIXID) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(Device.SP_RES_ID, theId);
    paramMap.add(Device.SP_IDENTIFIER, theIdentifier);
    paramMap.add(Device.SP_UDI_CARRIER, theCarrier);
    paramMap.add(Device.SP_UDI_DI, theUdiDI);
    paramMap.add(Device.SP_STATUS, theStatus);
    paramMap.add(Device.SP_MANUFACTURER, theManufacturer);
    paramMap.add("manufacture-date", theExpirationDate);
    paramMap.add("expiration-date", theExpirationDate);
    paramMap.add("lot-number", thelotNumber);
    paramMap.add("serial-number", theSerialNumber);
    paramMap.add("distinct-identifier", theDistinctIdentifier);
    paramMap.add(Device.SP_DEVICE_NAME, theDeviceName);
    paramMap.add(Device.SP_TYPE, theType);
    paramMap.add(Device.SP_PATIENT, thePatient);
    paramMap.add(Device.SP_ORGANIZATION, theOrganization);
    paramMap.add(Device.SP_MODEL, theModelNumber);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in search of DeviceResourceProvider: ", e);
      throw e;
    }
    return bundle;
  }
}

package io.arcadia.fhir.providers;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.PatientService;

@Component
public class PatientResourceProvider extends AbstractJaxRsResourceProvider<Patient> {

	private static final Logger logger = LoggerFactory.getLogger(PatientResourceProvider.class);

	@Autowired
	FhirContext fhirContext;

	@Autowired
	private PatientService service;

	public PatientResourceProvider(FhirContext fhirContext) {
		super(fhirContext);
	}

	/**
	 * The getResourceType method comes from IResourceProvider, and must be
	 * overridden to indicate what type of resource this provider supplies.
	 */
	@Override
	public Class<Patient> getResourceType() {
		return Patient.class;
	}

	/**
	 * The "@Read" annotation indicates that this method supports the read
	 * operation. This operation retrieves a resource by ID. Example URL to invoke
	 * this method: http://<server name>/<context>/fhir/Patient/1
	 *
	 * @param request
	 * @param response
	 * @param theId
	 * @return
	 */
	@Read
	public Patient readOrVread(HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
		String id;
		Patient thePatient = null;
		try {
			id = theId.getIdPart();
			thePatient = service.getPatientById(id);
		} catch (Exception e) {
			logger.error("Exception in readOrVread of PatientResourceProvider : ", e);
			throw e;
		}
		return thePatient;
	}

	/**
	 * The "@Search" annotation indicates that this method supports the search
	 * operation. You may have many different method annotated with this annotation,
	 * to support many different search criteria. The search operation returns a
	 * bundle with zero-to-many resources of a given type, matching a given set of
	 * parameters.
	 *
	 * @param request
	 * @param response
	 * @param theId
	 * @param theIdentifier
	 * @param theName
	 * @param theFamily
	 * @param theGiven
	 * @param theOrganization
	 * @param theTelecom
	 * @param thePhone
	 * @param theEmail
	 * @param theAddress
	 * @param theGeneralPractitioner
	 * @param theAddressCity
	 * @param theAddressState
	 * @param theAddressPostalcode
	 * @param theAddressCountry
	 * @param theGender
	 * @param theLanguage
	 * @param theBirthdate
	 * @param theActive
	 * @param theAddressUse
	 * @param theLink
	 * @param theDeceased
	 * @param theDeathDate
	 * @param theIncludes
	 * @return
	 */
	@Search()
	public Bundle search(HttpServletRequest theRequest, HttpServletResponse theResponse,
			@Description(shortDefinition = "The resource identity") @OptionalParam(name = Patient.SP_RES_ID) TokenAndListParam theId,
			@Description(shortDefinition = "A patient identifier") @OptionalParam(name = Patient.SP_IDENTIFIER) TokenParam theIdentifier,
			@Description(shortDefinition = "A portion of either family or given name of the patient") @OptionalParam(name = Patient.SP_NAME) StringAndListParam theName,
			@Description(shortDefinition = "A portion of the family name of the patient") @OptionalParam(name = Patient.SP_FAMILY) StringAndListParam theFamily,
			@Description(shortDefinition = "A portion of the given name of the patient") @OptionalParam(name = Patient.SP_GIVEN) StringAndListParam theGiven,
			@Description(shortDefinition = "The organization that is the custodian of the patient record") @OptionalParam(name = Patient.SP_ORGANIZATION) ReferenceAndListParam theOrganization,
			@Description(shortDefinition = "The value in any kind of telecom details of the patient") @OptionalParam(name = Patient.SP_TELECOM) TokenAndListParam theTelecom,
			@Description(shortDefinition = "A value in a phone contact") @OptionalParam(name = Patient.SP_PHONE) TokenAndListParam thePhone,
			@Description(shortDefinition = "A value in a email contact") @OptionalParam(name = Patient.SP_EMAIL) TokenAndListParam theEmail,
			@Description(shortDefinition = "An address in any kind of address/part of the patient") @OptionalParam(name = Patient.SP_ADDRESS) StringAndListParam theAddress,
			@Description(shortDefinition = "Patient's nominated general practitioner, not the organization that manages the record") @OptionalParam(name = Patient.SP_GENERAL_PRACTITIONER) ReferenceAndListParam theGeneralPractitioner,
			@Description(shortDefinition = "A city specified in an address") @OptionalParam(name = Patient.SP_ADDRESS_CITY) StringAndListParam theAddressCity,
			@Description(shortDefinition = "A state specified in an address") @OptionalParam(name = Patient.SP_ADDRESS_STATE) StringAndListParam theAddressState,
			@Description(shortDefinition = "A postalCode specified in an address") @OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) StringAndListParam theAddressPostalcode,
			@Description(shortDefinition = "A country specified in an address") @OptionalParam(name = Patient.SP_ADDRESS_COUNTRY) StringAndListParam theAddressCountry,
			@Description(shortDefinition = "Gender of the patient") @OptionalParam(name = Patient.SP_GENDER) TokenAndListParam theGender,
			@Description(shortDefinition = "Language code (irrespective of use value)") @OptionalParam(name = Patient.SP_LANGUAGE) TokenAndListParam theLanguage,
			@Description(shortDefinition = "The patient's date of birth") @OptionalParam(name = Patient.SP_BIRTHDATE) DateRangeParam theBirthdate,
			@Description(shortDefinition = "Whether the patient record is active") @OptionalParam(name = Patient.SP_ACTIVE) TokenAndListParam theActive,
			@Description(shortDefinition = "A use code specified in an address.Patient.address.use") @OptionalParam(name = Patient.SP_ADDRESS_USE) TokenAndListParam theAddressUse,
			@Description(shortDefinition = "All patients linked to the given patient") @OptionalParam(name = Patient.SP_LINK, targetTypes = {
					Patient.class }) ReferenceAndListParam theLink,
			@Description(shortDefinition = "This patient has been marked as deceased, or as a death date entered") @OptionalParam(name = Patient.SP_DECEASED) TokenAndListParam theDeceased,
			@Description(shortDefinition = "The date of death has been provided and satisfies this search value") @OptionalParam(name = Patient.SP_DEATH_DATE) DateRangeParam theDeathDate,
			@IncludeParam(allow = { "*" }) Set<Include> theIncludes,
			@IncludeParam(reverse = true, allow = { "*" }) Set<Include> theRevIncludes) {

		SearchParameterMap paramMap = new SearchParameterMap();
		paramMap.add(Patient.SP_RES_ID, theId);
		paramMap.add(Patient.SP_IDENTIFIER, theIdentifier);
		paramMap.add(Patient.SP_NAME, theName);
		paramMap.add(Patient.SP_FAMILY, theFamily);
		paramMap.add(Patient.SP_GIVEN, theGiven);
		paramMap.add(Patient.SP_ORGANIZATION, theOrganization);
		paramMap.add(Patient.SP_TELECOM, theTelecom);
		paramMap.add(Patient.SP_PHONE, thePhone);
		paramMap.add(Patient.SP_ADDRESS, theAddress);
		paramMap.add(Patient.SP_ADDRESS_CITY, theAddressCity);
		paramMap.add(Patient.SP_ADDRESS_STATE, theAddressState);
		paramMap.add(Patient.SP_ADDRESS_POSTALCODE, theAddressPostalcode);
		paramMap.add(Patient.SP_ADDRESS_COUNTRY, theAddressCountry);
		paramMap.add(Patient.SP_ADDRESS_USE, theAddressUse);
		paramMap.add(Patient.SP_GENDER, theGender);
		paramMap.add(Patient.SP_LANGUAGE, theLanguage);
		paramMap.add(Patient.SP_BIRTHDATE, theBirthdate);
		paramMap.add(Patient.SP_ACTIVE, theActive);
		paramMap.add(Patient.SP_DECEASED, theDeceased);
		paramMap.add(Patient.SP_LINK, theLink);
		paramMap.add(Patient.SP_EMAIL, theEmail);
		paramMap.add(Patient.SP_GENERAL_PRACTITIONER, theGeneralPractitioner);
		paramMap.add(Patient.SP_DEATH_DATE, theDeathDate);
		paramMap.setIncludes(theIncludes);
		paramMap.setRevIncludes(theRevIncludes);
		Bundle bundle = new Bundle();
		try {
			bundle = service.getPatientsBySearchOption(paramMap);
		} catch (Exception e) {
			logger.error("\nException in search of PatientResourceProvider\n", e);
			throw e;
		}
		return bundle;
	}
}

package io.arcadia.fhir.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import io.arcadia.fhir.client.ArcadiaClient;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.PatientService;
import io.arcadia.fhir.util.ParamsUtil;
import io.arcadia.fhir.util.TransformerUtils;

@Service("patientService")
@Transactional
public class PatientServiceImpl implements PatientService {

	private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
	
	@Autowired
	ArcadiaClient arcadiaClient;
	
	@Autowired
	TransformerUtils transformerUtils;

	@Override
	public Patient getPatientById(String theId) {
		Patient thePatient = new Patient();
		return thePatient;
	}

	@Override
	public Bundle getPatientsBySearchOption(SearchParameterMap paramMap) {

		logger.info("Inside getPatientsBySearchOption: {}", paramMap);
		
		List<Map> patientResources = new ArrayList<Map>();
		Map resourceMap = new HashMap<>();

		String searchQuery = formArcdiaSearchQuery(paramMap);
		Map searchResponse = arcadiaClient.executeSerchQuery(searchQuery);
		if(searchResponse != null && searchResponse.containsKey("records")) {
			patientResources = (List<Map>) searchResponse.get("records");
			logger.info("Received Patient resources: {}", patientResources);
			resourceMap.put("patient", patientResources);
		}
		return transformerUtils.transform(ResourceType.Patient, resourceMap, paramMap);
	}

	/**
	 * Form the Arcadia search query using input parameters
	 * 
	 * @param paramMap : input parameters
	 * @return Arcadia search query
	 */
	private String formArcdiaSearchQuery(SearchParameterMap paramMap) {

		StringBuilder paramString = new StringBuilder();

		if (paramMap.containsKey(Patient.SP_NAME)) {
			boolean isFirstTime = true;
			if (paramString.length() > 0)
				paramString.append("&");
			List<String> names = ParamsUtil.readStringParams(paramMap.get(Patient.SP_NAME));
			paramString.append("query=");
			for (String name : names) {
				if(isFirstTime)
					isFirstTime=false;
				else
					paramString.append(" OR ");
				paramString.append("givenName:" + name).append(" OR familyName:" + name);
			}
		}

		if (paramMap.containsKey(Patient.SP_FAMILY)) {
			if (paramString.length() > 0)
				paramString.append("&");
			List<String> names = ParamsUtil.readStringParams(paramMap.get(Patient.SP_FAMILY));
			paramString.append("query=").append("familyName:" + names.get(0));
		}

		if (paramMap.containsKey(Patient.SP_BIRTHDATE)) {
			if (paramString.length() > 0)
				paramString.append("&");
			List<DateParam> dates = ParamsUtil.readDateParams(paramMap.get(Patient.SP_BIRTHDATE));
			paramString.append("birthDate=").append(dates.get(0).getValueAsString());
		}

		if (paramMap.containsKey(Patient.SP_RES_ID)) {
			if (paramString.length() > 0)
				paramString.append("&");
			List<TokenParam> tokens = ParamsUtil.readTokenParams(paramMap.get(Patient.SP_RES_ID));
			paramString.append("id=").append("urn:doid:arcadia.io:person!").append(tokens.get(0).getValue());
		}
		
		if (paramMap.containsKey(Patient.SP_IDENTIFIER)) {
			List<TokenParam> tokens = ParamsUtil.readTokenParams(paramMap.get(Patient.SP_IDENTIFIER));
			for (TokenParam tokenParam : tokens) {
				if (paramString.length() > 0)
					paramString.append("&");

				String[] codeAndValue;
				if (tokenParam.getValue().contains("|"))
					codeAndValue = tokenParam.getValue().split("\\|");
				else
					throw new InvalidRequestException("Code is mandatory to identify the Identifier");

				if (codeAndValue[0].equals("MR"))
					paramString.append("mrn=" + codeAndValue[1]);
				else if (codeAndValue[0].equals("MC"))
					paramString.append("medicareId=" + codeAndValue[1]);
				else
					throw new InvalidRequestException("Unknown Identifier");
			}
		}

		if (paramMap.containsKey(Patient.SP_GENDER)) {
			if (paramString.length() > 0)
				paramString.append("&");
			List<TokenParam> tokens = ParamsUtil.readTokenParams(paramMap.get(Patient.SP_GENDER));
			switch (tokens.get(0).getValue()) {
			case "male":
				paramString.append("sex=m");
				break;
			case "female":
				paramString.append("sex=f");
				break;
			default:
				throw new InvalidRequestException("Unknown gender type");
			}
		}

		if (!paramString.toString().contains("query")) {
			if (paramString.length() > 0)
				paramString.append("&");
			paramString.append("query=*");
		}

		logger.info("Final arcadia search query: {}", paramString);
		return paramString.toString();
	}

}

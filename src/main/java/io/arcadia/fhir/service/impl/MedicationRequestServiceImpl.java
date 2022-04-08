package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.MedicationRequestService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("medicationRequestService")
@Transactional
public class MedicationRequestServiceImpl implements MedicationRequestService {
	
	private static final Logger logger = LoggerFactory.getLogger(MedicationRequestServiceImpl.class);

	@Autowired
	EndPointResolver endPointResolver;
	
	@Autowired
	TransformerUtils transformerUtils;

	@Override
	public MedicationRequest getMedicationRequestById(String id) {
		MedicationRequest medicationRequest = null;
		return medicationRequest;
	}
	/**
	 * Responsible for finding Arcadia entities, filter the entities based on
	 * parameters, transform them into FHIR resources and returns them as a Bundle.
	 * 
	 * @param paramMap Search Parameter Map
	 * @return Bundle FHIR Resource
	 */
	@Override
	public Bundle search(SearchParameterMap paramMap) {
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.MedicationRequest, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.MedicationRequest, arcadiaResources, paramMap);
	}
}

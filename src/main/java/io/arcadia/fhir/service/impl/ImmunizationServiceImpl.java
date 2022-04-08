package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.ImmunizationService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("immunizationService")
@Transactional
public class ImmunizationServiceImpl implements ImmunizationService {

	private static final Logger logger = LoggerFactory.getLogger(ImmunizationServiceImpl.class);
	
	

	@Autowired
	EndPointResolver endPointResolver;
	
	@Autowired
	TransformerUtils transformerUtils;

	@Override
	public Immunization getImmunizationById(String id) {
		Immunization immunization = new Immunization();
		return immunization;
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
		
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.Immunization, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.Immunization, arcadiaResources, paramMap);

	}
}

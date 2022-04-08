package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.ConditionService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("conditionService")
@Transactional
public class ConditionServiceImpl implements ConditionService {

	private static final Logger logger = LoggerFactory.getLogger(ConditionServiceImpl.class);

	@Autowired
	EndPointResolver endPointResolver;

	@Autowired
	TransformerUtils transformerUtils;

	@Override
	public Condition getConditionById(String theId) {
		return null;
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

		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.Condition, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.Condition, arcadiaResources, paramMap);
	}
}

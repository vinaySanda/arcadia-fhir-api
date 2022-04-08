package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.ProvenanceService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("provenanceService")
@Transactional
public class ProvenanceServiceImpl implements ProvenanceService {
	private static final Logger logger = LoggerFactory.getLogger(ProvenanceServiceImpl.class);

	@Autowired
	EndPointResolver endPointResolver;

	@Autowired
	TransformerUtils transformerUtils;


	@Override
	public Provenance getProvenanceById(String theId) {
		Provenance provenance = null;
		return provenance;
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
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.Provenance, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.Provenance, arcadiaResources, paramMap);
	}
}

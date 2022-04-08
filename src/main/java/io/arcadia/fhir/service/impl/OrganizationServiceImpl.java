package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.OrganizationService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
	private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

	@Autowired
	EndPointResolver endPointResolver;

	@Autowired
	TransformerUtils transformerUtils;


	@Override
	public Organization getOrganizationById(String id) {
		Organization organization = new Organization();
		return organization;
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
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.Organization, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.Organization, arcadiaResources, paramMap);
	}
}

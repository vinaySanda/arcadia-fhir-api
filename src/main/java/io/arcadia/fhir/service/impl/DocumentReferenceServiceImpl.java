package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.DocumentReferenceService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("DocumentReferenceService")
@Transactional
public class DocumentReferenceServiceImpl implements DocumentReferenceService {
	private static final Logger logger = LoggerFactory.getLogger(DocumentReferenceServiceImpl.class);

	@Autowired
	EndPointResolver endPointResolver;

	@Autowired
	TransformerUtils transformerUtils;


	@Override
	public DocumentReference getDocumentReferenceById(String id) {
		DocumentReference documentReference = null;
		return documentReference;
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
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.DocumentReference, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.DocumentReference, arcadiaResources, paramMap);
	}
}

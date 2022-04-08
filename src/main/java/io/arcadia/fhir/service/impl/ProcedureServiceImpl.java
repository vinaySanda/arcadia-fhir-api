package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.ProcedureService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("procedureService")
@Transactional
public class ProcedureServiceImpl implements ProcedureService {
  private static final Logger logger = LoggerFactory.getLogger(ProcedureServiceImpl.class);
  
	@Autowired
	EndPointResolver endPointResolver;

	@Autowired
	TransformerUtils transformerUtils;

  @Override
  public Procedure getProcedureById(String theId) {
    Procedure procedure = null;
    return procedure;
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
	  
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.Procedure, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.Procedure, arcadiaResources, paramMap);
  }
}

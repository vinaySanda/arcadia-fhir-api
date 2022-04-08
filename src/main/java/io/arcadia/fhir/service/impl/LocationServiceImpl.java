package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.LocationService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("locationService")
@Transactional
public class LocationServiceImpl implements LocationService {
  private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);
  
	@Autowired
	EndPointResolver endPointResolver;

	@Autowired
	TransformerUtils transformerUtils;


  @Override
  public Location getLocationById(String id) {
    Location location = new Location();
    return location;
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
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.Location, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.Location, arcadiaResources, paramMap);
  }
}

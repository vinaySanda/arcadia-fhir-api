package io.arcadia.fhir.service.impl;

import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.DeviceService;
import io.arcadia.fhir.util.TransformerUtils;

import java.util.Map;

import javax.transaction.Transactional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("deviceService")
@Transactional
public class DeviceServiceImpl implements DeviceService {
	private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

	@Autowired
	EndPointResolver endPointResolver;

	@Autowired
	TransformerUtils transformerUtils;

	@Override
	public Device getDeviceById(String theId) {
		Device device = null;
		return device;
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
		Map arcadiaResources = endPointResolver.getArcadiaResources(ResourceType.Device, paramMap);

		// filter logic

		return transformerUtils.transform(ResourceType.Device, arcadiaResources, paramMap);
	}
}

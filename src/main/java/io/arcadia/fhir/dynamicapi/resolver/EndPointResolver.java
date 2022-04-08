package io.arcadia.fhir.dynamicapi.resolver;

import java.util.Map;

import org.hl7.fhir.r4.model.ResourceType;

import io.arcadia.fhir.query.SearchParameterMap;

/**
 * <p>
 * This class is responsible for finding the target Arcadia API/APIs for the
 * given FHIR resource based on the {@code ResourceMapping.json} configurations
 * and execute them.
 * </p>
 * 
 * 
 * @author Pradeep Kumara K
 *
 */
public interface EndPointResolver {

	/**
	 * 
	 * @param type FHIR {@link ResourceType} 
	 * @param inputParams input parameters passed to FHIR API providers
	 * @return the complete Arcadia entities requested by FHIR API
	 */
	Map getArcadiaResources(ResourceType type, SearchParameterMap inputParams);

}

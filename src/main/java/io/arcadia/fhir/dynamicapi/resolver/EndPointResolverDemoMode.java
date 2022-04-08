package io.arcadia.fhir.dynamicapi.resolver;

import java.util.Map;

import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.util.JSONUtil;

/**
 * <p>
 * This is one of the implementation of {@link EndPointResolver}. This class
 * will be injected if the {@code app.mode} is set to {@code demo} in
 * {@code application.properties} file.
 * </p>
 * 
 * <p>
 * It will read Arcadia entities from file system instead of Arcadia server
 * which can be used for demo purposes
 * </p>
 * 
 * @author Pradeep Kumara K
 * 
 * @see EndPointResolver
 * @see EndPointResolverLiveMode
 *
 */
public class EndPointResolverDemoMode implements EndPointResolver {

	@Value("${arcadia.resourceFolderPath}")
	private String resourcePath;

	private static final Logger logger = LoggerFactory.getLogger(EndPointResolverDemoMode.class);

	@Override
	public Map getArcadiaResources(ResourceType type, SearchParameterMap inputParams) {

		logger.info(
				"Inside getArcadiaResources for ResourceType " + type.toString() + " and inputParams: " + inputParams);

		String inputJSON = JSONUtil.readJsonFromFile(resourcePath + "/" + type.getPath() + ".json");
		Map arcadiaData = JSONUtil.jsonToMap(inputJSON);

		return arcadiaData;
	}
}

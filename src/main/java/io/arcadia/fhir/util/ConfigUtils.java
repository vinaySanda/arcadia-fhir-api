package io.arcadia.fhir.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;

/**
 * <p>
 * This utility class helps in reading JSON configurations files. It maintains a
 * <strong>config cache</strong> where all the config files will be stored and
 * reused every time the call is made.
 * </p>
 * 
 * <p>
 * Also it has multiple methods to read value from a given field and handles
 * exceptions.
 * </p>
 * 
 * @author Pradeep Kumara K
 * @see JSONUtil
 *
 */

@Component
public class ConfigUtils {

	private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

	private static final HashMap<String, Map> CONFIG_CACHE = new HashMap<String, Map>();

	/**
	 * Reads the JSON config file from <strong>Config Cache</strong>. If not found
	 * in cache it will be read from resource folder and add it into the cache
	 * before returning it to the caller so that from next time onwards it will be
	 * available in cache.
	 * 
	 * @param resource Name of the JSON configuration file.
	 * @return Map representation of JSON configuration file.
	 * @throws IOException
	 */
	public static Map<String, Map> getConfig(String resource) throws IOException {
		if (null != CONFIG_CACHE && !CONFIG_CACHE.containsKey(resource)) {
			logger.info("Could not find config for resource: " + resource + " in cache. Loading from resource folder");
			CONFIG_CACHE.put(resource, JSONUtil.convertResourceJSONFileToMap(resource));
		}
		return CONFIG_CACHE.get(resource);
	}

	/**
	 * <p>
	 * Should be used when the value of the config field is String
	 * </p>
	 * 
	 * @param config Configuration Data.
	 * @param key    Name of the configuration field.
	 * @return Value of the configuration filed.
	 */
	public static String valueString(Map config, String key) {
		if (config.get(key) != null) {
			try {
				return (String) config.get(key);
			} catch (Exception e) {
				throw new InternalErrorException("Exception for config field: [" + key + "] - " + e.getMessage());
			}
		} else
			throw new InternalErrorException("Config field [" + key + "] is not present");
	}

	/**
	 * <p>
	 * Should be used when the value of the config field is Map
	 * </p>
	 * 
	 * @param config Configuration Data.
	 * @param key    Name of the configuration field.
	 * @return Value of the configuration filed.
	 */
	public static Map valueMap(Map config, String key) {
		if (config.get(key) != null) {
			try {
				return (Map) config.get(key);
			} catch (Exception e) {
				OperationOutcome outcome = new OperationOutcome();
				CodeableConcept cc = new CodeableConcept();
				cc.setText(e.getMessage());
				outcome.addIssue().setSeverity(IssueSeverity.ERROR).setCode(IssueType.INVALID)
						.setDiagnostics("Could not read config field: [" + key + "]").setDetails(cc);
				throw new InternalErrorException("", outcome);
			}
		} else
			throw new InternalErrorException("Config field [" + key + "] is not present");
	}

	/**
	 * <p>
	 * Should be used when the value of the config field is List
	 * </p>
	 * 
	 * @param config Configuration Data.
	 * @param key    Name of the configuration field.
	 * @return Value of the configuration filed.
	 */
	public static List valueList(Map config, String key) {
		if (config.get(key) != null) {
			try {
				return (List) config.get(key);
			} catch (Exception e) {
				throw new InternalErrorException("Exception for config field: [" + key + "] - " + e.getMessage());
			}
		} else
			throw new InternalErrorException("Config field [" + key + "] is not present");
	}

}

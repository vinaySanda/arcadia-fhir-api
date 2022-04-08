package io.arcadia.fhir.util;

import java.util.ArrayList;
import java.util.List;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

/**
 * This utility class helps in extracting FHIR input parameters passed to FHIR API providers.
 * 
 * @author Pradeep Kumara K
 *
 */
public class ParamsUtil {

	/**
	 * Reads String parameters.
	 * 
	 * @param list List of QueryParams of type {@link IQueryParameterType}
	 * @return List of String values extracted from the QueryParams
	 */
	public static List<String> readStringParams(List<List<? extends IQueryParameterType>> list) {
		List<String> stringParamList = new ArrayList<>();
		for (List<? extends IQueryParameterType> values : list) {
			for (IQueryParameterType params : values) {
				StringParam stringParam = (StringParam) params;
				if (stringParam.getValue() != null) {
					stringParamList.add(stringParam.getValue());
				}
			}
		}
		return stringParamList;
	}

	/**
	 * Reads Date parameters.
	 * 
	 * @param list List of QueryParams of type {@link IQueryParameterType}
	 * @return List of Date values extracted from the QueryParams
	 */
	public static List<DateParam> readDateParams(List<List<? extends IQueryParameterType>> list) {
		List<DateParam> dateParamList = new ArrayList<>();
		for (List<? extends IQueryParameterType> values : list) {
			for (IQueryParameterType params : values) {
				DateParam dataParam = (DateParam) params;
				if (dataParam.getValue() != null) {
					dateParamList.add(dataParam);
				}
			}
		}
		return dateParamList;
	}

	/**
	 * Reads Token parameters.
	 * 
	 * @param list List of QueryParams of type {@link IQueryParameterType}
	 * @return List of Token values extracted from the QueryParams
	 */
	public static List<TokenParam> readTokenParams(List<List<? extends IQueryParameterType>> list) {
		List<TokenParam> tokenParamList = new ArrayList<>();
		for (List<? extends IQueryParameterType> values : list) {
			for (IQueryParameterType params : values) {
				TokenParam tokenParam = (TokenParam) params;
				if (tokenParam.getValue() != null) {
					tokenParamList.add(tokenParam);
				}
			}
		}
		return tokenParamList;
	}

}

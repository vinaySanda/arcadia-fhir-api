package io.arcadia.fhir.util;

/**
 * FHIR-API Microservice Constants.
 * 
 * @author Pradeep Kumara K
 *
 */
public interface AppConstants {

	String RESOURCE_MAPPING_FILE = "ResourceMapping.json";

	String TRANSFORMATION_CONFIG_FILE = "TransformationConfig.json";

	String ARCADIA_BASE_URL = "arcadiaBaseUrl";

	String ARCADIA_RESOURCE_URL = "resourceUrl";

	String STRUCTURE_MAP_URL = "structureMapUrl";

	String GRAPH_QL_URL = "graphQLUrl";

	String TRASFORM_SERVICE_URL = "trasformServiceUrl";

	String RESOURCES = "resources";

	String RESOURCE_TYPE = "resourceType";

	String ARCADIA_RESOURCE_NAME = "arcadiaResourceName";

	String PARAMETERS = "parameters";

	String API_INFO = "apiInfo";

	String STRUCTURE_MAP = "structureMap";

	String STRUCTURE_DEFINITION = "structureDefinition";

	String GRAPH_QL_QUERY = "graphQlQuery";

	String GRAPH_QUERY_SYNTAX = "querySyntax";

	String PERSON_ID = "id";

	String PERSON_ID_PLACEHOLDER = "<person-id>";

	String ARCADIA_RESOURCE_PLACEHOLDER = "<arcadia-resource>";

	String ARCADIA_ID_PLACEHOLDER = "<id>";

	String QUERY = "query";

	String RECORDS = "records";

	String DATA = "data";

	String PERSON = "person";

	String ERRORS = "errors";

	String PERSON_ID_PREFIX = "urn:doid:arcadia.io:person!";

	String PATIENT = "patient";

	String TARGET_PARAM = "targetParam";

	String SEND_AS_QUERY = "sendAsQuery";

	String ID_FIELD = "idField";

	String DATE_FIELDS = "dateFields";

	String RESOURCE_PROFILE_URL = "resourceProfileUrl";

	String VALIDATION_SERVICE_URL = "validationServiceUrl";

	String PROVENANCE_STRUCTURE_DEFINITION = "provenanceStructureDefinition";

	String PROVENANCE_STRUCTURE_MAP = "provenanceStructureMap";

	String TOKEN_PREFIX = "Bearer ";

	String APPLICATION_FHIR_JSON_TYPE = "application/fhir+json;fhirVersion=4.0";

}
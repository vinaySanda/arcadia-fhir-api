package io.arcadia.fhir.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.arcadia.fhir.util.AppConstants;
import reactor.core.publisher.Mono;

/**
 * The client is responsible for calling the Transformation microservice in
 * order to transform Arcadia entity into FHIR resource.
 * 
 * @author Pradeep Kumara K
 *
 */
@Component
public class TransformClient {
	
	private static final Logger logger = LoggerFactory.getLogger(TransformClient.class);
	
	@Autowired
	WebClient webClient;
	
	@Value("${service.trasformation.url}")
	private String url;

	/**
	 * Responsible for calling transform API
	 * 
	 * @param structureMap url of the StrctureMap
	 * @param arcadiaInput Arcadia Entity
	 * @return FHIR Resource String
	 */
	public String getFhirResource(String structureMap, Map arcadiaInput) {
		try {
			String response = webClient.post()
					.uri(url + structureMap)
					.header(HttpHeaders.CONTENT_TYPE, AppConstants.APPLICATION_FHIR_JSON_TYPE)
					.header(HttpHeaders.ACCEPT, AppConstants.APPLICATION_FHIR_JSON_TYPE)
			        .body(Mono.just(arcadiaInput), Map.class)
					.retrieve()
					.bodyToMono(String.class)
					.block();
					
			return response;
		}catch(Exception e) {
			logger.error("Exception occured while executing Transformation API: "+e.getMessage());
			throw e;
		}
		
	}
}

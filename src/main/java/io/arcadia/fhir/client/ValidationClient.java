/**
 * 
 */
package io.arcadia.fhir.client;

import org.hl7.fhir.r4.model.OperationOutcome;
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
 * The client is responsible for calling the Validation microservice in order to
 * validate the FHIR Resource once it is transformed by transformation service.
 * 
 * @author Ravishankar
 *
 */
@Component
public class ValidationClient {
	
	private static final Logger logger = LoggerFactory.getLogger(ValidationClient.class);

	@Autowired
	WebClient webClient;
	
	@Value("${service.validation.url}")
	private String url;
	
	/**
	 * Responsible for executing validate API.
	 * 
	 * @param resourceProfileUrl Resource profile URL defined by HL7
	 * @param fhirResourceString FHIR Resource in the form of String
	 * @return {@link OperationOutcome}
	 * 
	 */
	public String validateFhirResource(String resourceProfileUrl, String fhirResourceString) {
		
		try {
			String response = webClient.post()
					.uri(url + resourceProfileUrl)
					.header(HttpHeaders.CONTENT_TYPE, AppConstants.APPLICATION_FHIR_JSON_TYPE)
					.header(HttpHeaders.ACCEPT, AppConstants.APPLICATION_FHIR_JSON_TYPE)
			        .body(Mono.just(fhirResourceString), String.class)
					.retrieve()
					.bodyToMono(String.class)
					.block();
					
			return response;
		}catch(Exception e) {
			logger.error("Exception occured while executing Validation API: "+e.getMessage());
			throw e;
		}
	}
}

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
 * ArcadiaClient is responsible for calling Arcadia APIs
 * 
 * @author Pradeep Kumara K
 *
 */
@Component
public class ArcadiaClient {
	
	private static final Logger logger = LoggerFactory.getLogger(ArcadiaClient.class);

	@Autowired
	WebClient webClient;

	@Value("${client.arcadia.baseUrl}")
	private String clientUrl;

	@Value("${client.arcadia.seachServicePort}")
	private String searchPort;

	@Value("${client.arcadia.crudServicePort}")
	private String crudPort;

	@Value("${client.arcadia.auth.token}")
	private String authToken;

	/**
	 * Responsible for executing GraphQl API.
	 * 
	 * @param graphQlQuery GraphQlQuery to be executed
	 * @return the resource requested by the graphQlQuery
	 */
	public Map executeGraphQlApi(Map graphQlQuery) {
		try {
			Map response = webClient.post()
					.uri(clientUrl + ":" + crudPort + "/graphql")
					.header(HttpHeaders.AUTHORIZATION, getAuthToken())
					.body(Mono.just(graphQlQuery), Map.class)
					.retrieve()
					.bodyToMono(Map.class)
					.block();
			return response;
		} catch (Exception e) {
			logger.error("Exception occured while executing GraphQl API: "+e.getMessage());
			throw e;
		}
	}

	/**
	 * Responsible for executing Search Person API
	 * 
	 * @param searchQuery Search Query for the search person API
	 * @return List of Person data
	 */
	public Map executeSerchQuery(String searchQuery) {
		try {
			Map response = webClient.get()
					.uri(clientUrl + ":" + searchPort + "/search/person?" + searchQuery)
					.header(HttpHeaders.AUTHORIZATION, getAuthToken())
					.retrieve()
					.bodyToMono(Map.class)
					.block();
			return response;
		} catch (Exception e) {
			logger.error("Exception occured while executing Search API: "+e.getMessage());
			throw e;
		}
	}

	/**
	 * Responsible for executing the Get APIs
	 * 
	 * @param url Dynamically formed get url
	 * @return the resource mentioned in the url
	 */
	public Map getResource(String url) {
		try {
			Map response = webClient.get()
					.uri(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthToken())
					.retrieve()
					.bodyToMono(Map.class)
					.block();
			return response;
		} catch (Exception e) {
			logger.error("Exception occured while executing Get API: "+e.getMessage());
			throw e;
		}
	}

	// Need to implement logic to get auth token in real time
	private String getAuthToken() {
		return AppConstants.TOKEN_PREFIX + authToken;
	}
}

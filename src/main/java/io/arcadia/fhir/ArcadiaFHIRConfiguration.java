package io.arcadia.fhir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import io.arcadia.fhir.dynamicapi.resolver.EndPointResolver;
import io.arcadia.fhir.dynamicapi.resolver.EndPointResolverDemoMode;
import io.arcadia.fhir.dynamicapi.resolver.EndPointResolverLiveMode;

/**
 * FHIR API microservice Spring boot configuration
 * 
 * @author Pradeep Kumara K
 *
 */
@Configuration
public class ArcadiaFHIRConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(ArcadiaFHIRConfiguration.class);

	/**
	 * Injects {@link LoggingInterceptor} bean
	 * @return {@link LoggingInterceptor} bean
	 */
	@Bean
	public LoggingInterceptor loggingInterceptor() {
		return new LoggingInterceptor();
	}

	/**
	 * Injects {@link WebClient} bean used to call other microservices or external APIs
	 * @return {@link WebClient} bean
	 */
	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}

	/**
	 * Injects {@link FhirContext} bean.
	 * @return FhirContext for R4.
	 */
	@Bean
	public FhirContext getFhirContext() {
		return FhirContext.forR4();
	}

	/**
	 * @return {@link CorsFilter} bean.
	 */
	@Bean
	public CorsFilter corsFilter() {

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// you USUALLY want this
		// config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("PATCH");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);

	}
	
	/**
	 * Injects {@link EndPointResolver} bean. This bean will inject only if the
	 * {@code app.mode} is set to {@code live} in application.properties.
	 * 
	 * @return EndPointResolverLiveMode bean.
	 */
	@Bean
	@ConditionalOnProperty(
		    value="app.mode", 
		    havingValue = "live", 
		    matchIfMissing = false)
	public EndPointResolver liveMode() {
		logger.info("Application running in Live mode");
		return new EndPointResolverLiveMode();
	}
	
	/**
	 * Injects {@link EndPointResolver} bean. This bean will inject only if the
	 * {@code app.mode} is either not set or set to {@code demo} in
	 * application.properties.
	 * 
	 * @return EndPointResolverLiveMode bean.
	 */
	@Bean
	@ConditionalOnProperty(
		    value="app.mode", 
		    havingValue = "demo", 
		    matchIfMissing = true)
	public EndPointResolver demoMode() {
		logger.info("Application running in Demo mode");
		return new EndPointResolverDemoMode();
	}
}

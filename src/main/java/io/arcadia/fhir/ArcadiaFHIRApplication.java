package io.arcadia.fhir;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.spring.boot.autoconfigure.FhirRestfulServerCustomizer;
import io.arcadia.fhir.providers.CapabilityStatementResourceProvider;

@SpringBootApplication
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
public class ArcadiaFHIRApplication extends SpringBootServletInitializer
		implements IResourceProvider, FhirRestfulServerCustomizer {

	private static final Logger logger = LoggerFactory.getLogger(ArcadiaFHIRApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ArcadiaFHIRApplication.class, args);
	}

	@Override
	public void customize(RestfulServer server) {
		try {
			FhirContext.forR4();
			Collection<IResourceProvider> c = server.getResourceProviders();
			List<IResourceProvider> l = c.stream().filter(p -> p != this).collect(Collectors.toList());
			server.setServerConformanceProvider(new CapabilityStatementResourceProvider());
			server.setResourceProviders(l);
		} finally {
			logger.info("In Finally Block");
		}
	}

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return null;
	}

}

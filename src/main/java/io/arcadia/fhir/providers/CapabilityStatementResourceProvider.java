package io.arcadia.fhir.providers;

import ca.uhn.fhir.rest.annotation.Metadata;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hl7.fhir.r4.hapi.rest.server.ServerCapabilityStatementProvider;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestSecurityComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementSoftwareComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.RestfulCapabilityMode;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.UriType;

public class CapabilityStatementResourceProvider extends ServerCapabilityStatementProvider {

  @Metadata
  public CapabilityStatement getConformance(
      HttpServletRequest request, RequestDetails theRequestDetails) {

    CapabilityStatement conformance = super.getServerConformance(request, theRequestDetails);
    conformance.getFhirVersion();
    conformance.setId("Arcadia FHIR Server");
    conformance.setUrl("/fhir/metadata");
    conformance.setVersion("1.0");
    conformance.setName("Arcadia FHIR Server Metadata");
    conformance.setStatus(PublicationStatus.ACTIVE);
    conformance.setPublisher("Arcadia FHIR Server");

    // Set Software
    CapabilityStatementSoftwareComponent softwareComponent =
        new CapabilityStatementSoftwareComponent();
    softwareComponent.setName("Arcadia FHIR Server");
    softwareComponent.setVersion("1.6");
    conformance.setSoftware(softwareComponent);

    // Set Rest
    List<CapabilityStatementRestComponent> restList =
        new ArrayList<CapabilityStatementRestComponent>();
    CapabilityStatementRestComponent rest = new CapabilityStatementRestComponent();
    rest.setMode(RestfulCapabilityMode.SERVER);

    CapabilityStatementRestSecurityComponent restSecurity =
        new CapabilityStatementRestSecurityComponent();

    Extension conformanceExtension =
        new Extension("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");

    // Need to set Auth and Token URL
    conformanceExtension.addExtension(new Extension("authorize", new UriType("")));
    conformanceExtension.addExtension(new Extension("token", new UriType("")));

    restSecurity.addExtension(conformanceExtension);

    CodeableConcept serviceCC = new CodeableConcept();
    List<Coding> theCodingList = new ArrayList<>();
    Coding theCoding = new Coding();
    theCoding.setCode("SMART-on-FHIR");
    theCoding.setSystem("http://terminology.hl7.org/CodeSystem/restful-security-service");
    theCodingList.add(theCoding);
    serviceCC.setCoding(theCodingList);
    serviceCC.setText("OAuth2 using SMART-on-FHIR profile (see http://docs.smarthealthit.org)");
    restSecurity.getService().add(serviceCC);
    restSecurity.setCors(true);
    rest.setSecurity(restSecurity);

    List<CapabilityStatement.CapabilityStatementRestResourceComponent> resources =
        conformance.getRest().get(0).getResource();
    for (CapabilityStatement.CapabilityStatementRestResourceComponent resource : resources) {
      resource.setSearchRevInclude(
          resource
              .getSearchInclude()); // HAPI Fhir is not setting this value as per design, manually
      // setting the values
    }
    rest.setResource(resources);

    // rest.setResource(conformance.getRest().get(0).getResource());
    restList.add(rest);
    conformance.setRest(restList);

    return conformance;
  }
}

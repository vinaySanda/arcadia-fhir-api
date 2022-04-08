package io.arcadia.fhir.service;

import io.arcadia.fhir.query.SearchParameterMap;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;

public interface PractitionerService {

  Practitioner getPractitionerById(String id);

  Bundle search(SearchParameterMap paramMap);
}

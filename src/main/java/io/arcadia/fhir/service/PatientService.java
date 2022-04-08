package io.arcadia.fhir.service;

import io.arcadia.fhir.query.SearchParameterMap;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

public interface PatientService {

  Patient getPatientById(String id);

  Bundle getPatientsBySearchOption(SearchParameterMap paramMap);
}

package io.arcadia.fhir.service;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;

import io.arcadia.fhir.query.SearchParameterMap;

public interface AllergyIntoleranceService {

  AllergyIntolerance getAllergyIntoleranceById(String id);

  Bundle search(SearchParameterMap paramMap);
}

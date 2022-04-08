package io.arcadia.fhir.service;

import io.arcadia.fhir.query.SearchParameterMap;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;

public interface CarePlanService {

  CarePlan getCarePlanById(String id);

  Bundle search(SearchParameterMap paramMap);
}

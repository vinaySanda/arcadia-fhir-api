package io.arcadia.fhir.providers;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jaxrs.server.AbstractJaxRsResourceProvider;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import io.arcadia.fhir.query.SearchParameterMap;
import io.arcadia.fhir.service.CareTeamService;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CareTeamResourceProvider extends AbstractJaxRsResourceProvider<CareTeam> {

  private static final Logger logger = LoggerFactory.getLogger(CareTeamResourceProvider.class);

  @Autowired private CareTeamService service;

  public CareTeamResourceProvider(FhirContext fhirContext) {
    super(fhirContext);
  }

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   */
  @Override
  public Class<CareTeam> getResourceType() {
    return CareTeam.class;
  }

  /**
   * The "@Read" annotation indicates that this method supports the read operation. This operation
   * retrieves a resource by ID. Example URL to invoke this method: http://<server
   * name>/<context>/fhir/CareTeam/1
   *
   * @param request
   * @param response
   * @param theId
   * @return
   */
  @Read
  public CareTeam readOrVread(
      HttpServletRequest request, HttpServletResponse response, @IdParam IdType theId) {
    String id;
    CareTeam careTeamList = new CareTeam();
    try {
      id = theId.getIdPart();
      careTeamList = service.getCareTeamById(id);
    } catch (Exception e) {
      logger.error("Exception in readOrVread of CareTeamResourceProvider : ", e);
      throw e;
    }
    return careTeamList;
  }

  /**
   * The "@Search" annotation indicates that this method supports the search operation. You may have
   * many different method annotated with this annotation, to support many different search
   * criteria. The search operation returns a bundle with zero-to-many resources of a given type,
   * matching a given set of parameters.
   *
   * @param request
   * @param response
   * @param theDate
   * @param theId
   * @param theIdentifier
   * @param thePatient
   * @param theEncounter
   * @param theCategory
   * @param theParticipant
   * @param theStatus
   * @param theIncludes
   * @param theSort
   * @param theCount
   * @return
   */
  @Search()
  public Bundle search(
      HttpServletRequest request,
      HttpServletResponse response,
      @Description(shortDefinition = "Time period team covers")
          @OptionalParam(name = CareTeam.SP_DATE)
          DateAndListParam theDate,
      @Description(shortDefinition = "The ID of the resource")
          @OptionalParam(name = CareTeam.SP_RES_ID)
          TokenAndListParam theId,
      @Description(shortDefinition = "External Ids for this team")
          @OptionalParam(name = CareTeam.SP_IDENTIFIER)
          TokenAndListParam theIdentifier,
      @Description(shortDefinition = "Who care team is for")
          @OptionalParam(name = CareTeam.SP_PATIENT)
          ReferenceAndListParam thePatient,
      @Description(shortDefinition = "Encounter or episode associated with CareTeam")
          @OptionalParam(name = CareTeam.SP_ENCOUNTER)
          ReferenceAndListParam theEncounter,
      @Description(shortDefinition = "Type of team") @OptionalParam(name = CareTeam.SP_CATEGORY)
          TokenAndListParam theCategory,
      @Description(shortDefinition = "Who is involved")
          @OptionalParam(name = CareTeam.SP_PARTICIPANT)
          ReferenceAndListParam theParticipant,
      @Description(shortDefinition = "proposed | active | suspended | inactive | entered-in-error")
          @OptionalParam(name = CareTeam.SP_STATUS)
          TokenAndListParam theStatus,
      @IncludeParam(allow = {"*"}) Set<Include> theIncludes,
      @IncludeParam(
              reverse = true,
              allow = {"*"})
          Set<Include> theRevIncludes) {

    SearchParameterMap paramMap = new SearchParameterMap();
    paramMap.add(CareTeam.SP_DATE, theDate);
    paramMap.add(CareTeam.SP_RES_ID, theId);
    paramMap.add(CareTeam.SP_IDENTIFIER, theIdentifier);
    paramMap.add(CareTeam.SP_PATIENT, thePatient);
    paramMap.add(CareTeam.SP_ENCOUNTER, theEncounter);
    paramMap.add(CareTeam.SP_CATEGORY, theCategory);
    paramMap.add(CareTeam.SP_PARTICIPANT, theParticipant);
    paramMap.add(CareTeam.SP_STATUS, theStatus);
    paramMap.setIncludes(theIncludes);
    paramMap.setRevIncludes(theRevIncludes);
    Bundle bundle = new Bundle();
    try {
      bundle = service.search(paramMap);
    } catch (Exception e) {
      logger.error("Exception in @search of CareTeamResourceProvider : ", e);
      throw e;
    }
    return bundle;
  }
}

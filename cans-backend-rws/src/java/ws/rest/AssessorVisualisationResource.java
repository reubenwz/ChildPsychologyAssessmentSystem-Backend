/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ClientSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.VisualisationSessionBeanLocal;
import entity.AssessorEntity;
import entity.CertificationEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import util.exception.LoginTokenNotFoundException;
import util.exception.OrganisationNotFoundException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssessmentNameResponse;
import ws.datamodel.BarChartResponse;
import ws.datamodel.CertificationExpiryResponse;
import ws.datamodel.DistributionBarChartResponse;
import ws.datamodel.ErrorResponse;
import ws.datamodel.LineChartResponse;
import ws.datamodel.NeedsBarChartResponse;
import ws.datamodel.PercentageBarChartResponse;
import ws.datamodel.PieChartResponse;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 */
@Path("AssessorVisualisation")
public class AssessorVisualisationResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final VisualisationSessionBeanLocal visualisationSessionBean;
    private final ClientSessionBeanLocal clientSessionBean;

    /**
     * Creates a new instance of AssessorVisualisationResource
     */
    public AssessorVisualisationResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.visualisationSessionBean = sessionBeanLookUp.visualisationSessionBean;
        this.clientSessionBean = sessionBeanLookUp.clientSessionBean;
    }

    private AssessorEntity validateAssessor(HttpHeaders headers) throws UserNotFoundException {
        try {

            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAssessorSystem(headerToken);
        } catch (NullPointerException | LoginTokenNotFoundException ex) {
            throw new UserNotFoundException();
        }
    }

    @Path("age-piechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAgePieChart(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Get Age PieChart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Long>> results = visualisationSessionBean.getTotalAgeGroup(assessor.getOrganisation());
        List<String> labels = results.getKey();
        List<Long> data = results.getValue();

        System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new PieChartResponse("Total client age group", data, labels)).build();
    }

    @Path("race-piechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRacePieChart(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getRacePieChart(): Get Race PieChart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Long>> results = visualisationSessionBean.getCountByRace(assessor.getOrganisation());
        List<String> labels = results.getKey();
        List<Long> data = results.getValue();

        System.out.println("********** AssessorVisualisationResource.getRacePieChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new PieChartResponse("Total client race group", data, labels)).build();
    }

    @Path("gender-piechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenderPieChart(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getGenderPieChart(): Get Gender PieChart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Long>> results = visualisationSessionBean.getCountByGender(assessor.getOrganisation());
        List<String> labels = results.getKey();
        List<Long> data = results.getValue();

        System.out.println("********** AssessorVisualisationResource.getGenderPieChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new PieChartResponse("Total client gender group", data, labels)).build();
    }

    @Path("domain-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomainBarChart(@Context HttpHeaders headers, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup) {
        System.out.println("********** AssessorVisualisationResource.getDomainBarChart(): Get Age PieChart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Double>> results = visualisationSessionBean.getAllDomainAverage(DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup, assessor.getOrganisation());
        List<String> labels = results.getKey();
        List<Double> data = results.getValue();

        System.out.println("********** AssessorVisualisationResource.getDomainBarChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new BarChartResponse("Average total response for each domain", labels, data, "Average Response", startDate, endDate, ageGroup)).build();
    }

    @Path("topneeds-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopNeedsBarChart(@Context HttpHeaders headers, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {
        System.out.println("********** AssessorVisualisationResource.getTopNeedsBarChart(): Get Top Needs Bar Chart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {

            List<String> orgNames = new ArrayList<>();
            orgNames.add(assessor.getOrganisation().getName());
            HashMap<String, Integer> result = visualisationSessionBean.getTopNeeds(orgNames, DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup, race, gender);

            List<String> labels = new ArrayList<>(result.keySet());
            List<Integer> data = new ArrayList<>(result.values());

            System.out.println("********** AssessorVisualisationResource.getTopNeedsBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new NeedsBarChartResponse("Top Needs for Organisation", labels, data, "Total Number of 2/3")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getTopNeedsBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }

    }

    @Path("topstrength-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopStrengthBarChart(@Context HttpHeaders headers, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {
        System.out.println("********** AssessorVisualisationResource.getTopStrengthBarChart(): Get Top Strength Bar Chart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            List<String> orgNames = new ArrayList<>();
            orgNames.add(assessor.getOrganisation().getName());
            HashMap<String, Integer> result = visualisationSessionBean.getTopStrength(orgNames, DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup, race, gender);

            List<String> labels = new ArrayList<>(result.keySet());
            List<Integer> data = new ArrayList<>(result.values());

            System.out.println("********** AssessorVisualisationResource.getTopStrengthBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new NeedsBarChartResponse("Top Strengths for Organisation", labels, data, "Total Number of 2/3")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getTopStrengthBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }

    }

    @Path("toptrauma-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopTraumaBarChart(@Context HttpHeaders headers, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {
        System.out.println("********** AssessorVisualisationResource.getTopTraumaBarChart(): Get Top Trauma Bar Chart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            List<String> orgNames = new ArrayList<>();
            orgNames.add(assessor.getOrganisation().getName());

            HashMap<String, Integer> result = visualisationSessionBean.getTopTrauma(orgNames, ageGroup, race, gender);

            List<String> labels = new ArrayList<>(result.keySet());
            List<Integer> data = new ArrayList<>(result.values());

            System.out.println("********** AssessorVisualisationResource.getTopTraumaBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new NeedsBarChartResponse("Top Trauma for Organisation", labels, data, "Total Number of 2/3")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getTopTraumaBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }

    }

    @Path("traumapercentage-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTraumaPercentageBarChart(@Context HttpHeaders headers, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {

        System.out.println("********** AssessorVisualisationResource.getTraumaPercentageBarChart(): Get Trauma BarChart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            List<String> orgNames = new ArrayList<>();
            orgNames.add(assessor.getOrganisation().getName());

            Pair<List<String>, List<Double>> results = visualisationSessionBean.getTraumaPercentage(orgNames, ageGroup, race, gender);
            List<String> labels = results.getKey();
            List<Double> data = results.getValue();
            for (Double d : data) {
                System.out.println(d);
            }
            System.out.println("********** AssessorVisualisationResource.getTraumaPercentageBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new PercentageBarChartResponse("Trauma Percentage Within Organisation", labels, data, "Percentage Response")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getTopTraumaBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();
        }
    }

    @Path("locdistributionfilter-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocDistributionFilterBarChart(@Context HttpHeaders headers, @QueryParam("assessment_reasons") List<String> assReason, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup) {

        System.out.println("********** AssessorVisualisationResource.getLocDistributionFilterBarChart(): Get LOC Distribution Bar Chart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            List<String> orgNames = new ArrayList<>();
            orgNames.add(assessor.getOrganisation().getName());

            HashMap<String, List<Integer>> results = visualisationSessionBean.getDistributionOfLocByOrganisations(orgNames, DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup, assReason);
            List<String> org = new ArrayList<>(results.keySet());
            List<List<Integer>> data = new ArrayList<>(results.values());

            System.out.println("********** AssessorVisualisationResource.getLocDistributionFilterBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new DistributionBarChartResponse("Distribution of Loc Across Organisation Type", org, data, "Total Count for each LOC")).build();

        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getLocDistributionByOrgBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }
    }

    @Path("assessment-types")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAssessmentTypes(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getAllAssessmentTypes(): Get all assessment types request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> results = new ArrayList<>(
                (Arrays.asList("INITIAL",
                        "REASSESSMENT",
                        "DISCHARGE",
                        "CRITICAL_INCIDENT",
                        "TRANSFER")));
        System.out.println("********** AssessorVisualisationResource.getAllAssessmentTypes(): Finished **********");
        return Response.status(Response.Status.OK).entity(new AssessmentNameResponse(results)).build();
    }

    @Path("ethnicity")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEthnicity(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getAllEthnicity(): Get all ethcnicity types request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> results = clientSessionBean.retrieveUniqueEthnicityOfClients();
        System.out.println("********** AssessorVisualisationResource.getAllEthnicity(): Finished **********");
        return Response.status(Response.Status.OK).entity(results).build();
    }

    @Path("gender")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGender(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getAllGender(): Get all gender types request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> results = clientSessionBean.retrieveUniqueGenderOfClients();
        System.out.println("********** AssessorVisualisationResource.getAllGender(): Finished **********");
        return Response.status(Response.Status.OK).entity(results).build();
    }

    @Path("roles-piechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRolesPieChart(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getRolesPieChart(): Get Race PieChart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getRolesPieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Long>> results = visualisationSessionBean.getRoles(assessor.getOrganisation().getOrganisationId());
        List<String> labels = results.getKey();
        List<Long> data = results.getValue();

        System.out.println("********** AssessorVisualisationResource.getRolesPieChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new PieChartResponse("Assessor Roles Distribution", data, labels)).build();
    }

    @Path("scores-linechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScoresLineChart(@Context HttpHeaders headers, @QueryParam("assessor_id") Long assId) {
        System.out.println("********** AssessorVisualisationResource.getScoresLineChart(): Get Scores Line Chart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getScoresLineChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<Integer>, List<Double>> results = visualisationSessionBean.getScores(assId);
        List<Integer> labels = results.getKey();
        List<Double> data = results.getValue();

        System.out.println("********** AssessorVisualisationResource.getScoresLineChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new LineChartResponse("Scores VS Recertification", labels, data)).build();
    }

    @Path("Recertified-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecertifiedBarChart(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getRecertifiedBarChart(): Get Scores Line Chart request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getRecertifiedBarChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Double>> results = visualisationSessionBean.getRectifiedTimes(assessor.getOrganisation().getOrganisationId());
        List<String> labels = results.getKey();
        List<Double> data = results.getValue();

        System.out.println("********** AssessorVisualisationResource.getRecertifiedBarChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new BarChartResponse("Number of times Assessor has been Recertified", labels, data, "", "", "", "")).build();
    }

    @Path("expiredCaseworkerTable")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpiredPeople(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getExpiredPeople(): Get Expired Cert Caseworker request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getExpiredPeople(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<CertificationEntity>> results = visualisationSessionBean.getAssessorExpired(assessor.getOrganisation().getOrganisationId());
        List<String> labels = results.getKey();
        List<CertificationEntity> data = results.getValue();
        for (CertificationEntity da : data) {
            if (da != null) {
                da.setAssessor(null);
            }
        }

        System.out.println("********** AssessorVisualisationResource.getExpiredPeople(): Finished **********");
        return Response.status(Response.Status.OK).entity(new CertificationExpiryResponse("Caseworker with expired Certs", data, labels)).build();
    }

    @Path("expiredSoonCaseworkerTable")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSoonExpiredPeople(@Context HttpHeaders headers) {
        System.out.println("********** AssessorVisualisationResource.getSoonExpiredPeople(): Get Expired Cert Caseworker request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getSoonExpiredPeople(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<CertificationEntity>> results = visualisationSessionBean.getAssessorExpiredSoon(assessor.getOrganisation().getOrganisationId());
        List<String> labels = results.getKey();
        List<CertificationEntity> data = results.getValue();

        for (CertificationEntity da : data) {

            da.setAssessor(null);

        }

        System.out.println("********** AssessorVisualisationResource.getSoonExpiredPeople(): Finished **********");
        return Response.status(Response.Status.OK).entity(new CertificationExpiryResponse("Caseworker with Soon to expire Certs", data, labels)).build();
    }
}

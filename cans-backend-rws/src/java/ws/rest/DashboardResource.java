/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ClientSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.VisualisationSessionBeanLocal;
import entity.AdminUserEntity;
import helperClassess.OrganisationInfo;
import helperClassess.OrganisationTypeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import util.exception.LoginTokenNotFoundException;
import util.exception.OrganisationNotFoundException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssessmentNameResponse;
import ws.datamodel.ErrorResponse;
import ws.datamodel.BarChartResponse;
import ws.datamodel.DistributionBarChartResponse;
import ws.datamodel.NeedsBarChartResponse;
import ws.datamodel.OrganisationNameResponse;
import ws.datamodel.PercentageBarChartResponse;
import ws.datamodel.PieChartResponse;

/**
 * REST Web Service
 *
 * @author Ooi Jun Hao
 * @author Ong Bik Jeun
 */
@Path("Dashboard")
public class DashboardResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final VisualisationSessionBeanLocal visualisationSessionBean;
    private final ClientSessionBeanLocal clientSessionBean;

    public DashboardResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.visualisationSessionBean = sessionBeanLookUp.visualisationSessionBean;
        this.clientSessionBean = sessionBeanLookUp.clientSessionBean;
    }

    // helper method to check validity of token
    private AdminUserEntity validateAdminUser(HttpHeaders headers) throws UserNotFoundException {
        try {
            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAdminSystem(headerToken);
        } catch (LoginTokenNotFoundException | NullPointerException ex) {
            throw new UserNotFoundException();
        }
    }

    @Path("age-piechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAgePieChart(@Context HttpHeaders headers) {
        System.out.println("********** DashboardResource.getAgePieChart(): Get Age PieChart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Long>> results = visualisationSessionBean.getTotalAgeGroup();
        List<String> labels = results.getKey();
        List<Long> data = results.getValue();

        System.out.println("********** DashboardResource.getAgePieChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new PieChartResponse("Total client age group", data, labels)).build();
    }

    @Path("domain-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDomainBarChart(@Context HttpHeaders headers, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup) {
        System.out.println("********** DashboardResource.getDomainBarChart(): Get Age PieChart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getDomainBarChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Double>> results = visualisationSessionBean.getAllDomainAverage(DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup);
        List<String> labels = results.getKey();
        List<Double> data = results.getValue();

        System.out.println("********** DashboardResource.getDomainBarChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new BarChartResponse("Average total response for each domain", labels, data, "Average Response", startDate, endDate, ageGroup)).build();
    }

    @Path("org-bubblechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrgBubbleChart(@Context HttpHeaders headers, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate) {
        System.out.println("********** DashboardResource.getOrgBubbleChart(): Get Org Bubble Chart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getOrgBubbleChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<OrganisationInfo> results = visualisationSessionBean.getAssessmentDonePerOrganisation(DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime());

        System.out.println("********** DashboardResource.getOrgBubbleChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(results).build();

    }

    @Path("topneeds-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopNeedsBarChart(@Context HttpHeaders headers, @QueryParam("organisation_name") List<String> orgNames, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {
        System.out.println("********** DashboardResource.getTopNeedsBarChart(): Get Top Needs Bar Chart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTopNeedsBarChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {

            HashMap<String, Integer> result = visualisationSessionBean.getTopNeeds(orgNames, DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup, race, gender);

            List<String> labels = new ArrayList<>(result.keySet());
            List<Integer> data = new ArrayList<>(result.values());

            System.out.println("********** DashboardResource.getTopNeedsBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new NeedsBarChartResponse("Top Needs for Organisation", labels, data, "Total Number of 2/3")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTopNeedsBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }

    }

    @Path("topstrength-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopStrengthBarChart(@Context HttpHeaders headers, @QueryParam("organisation_name") List<String> orgNames, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {
        System.out.println("********** DashboardResource.getTopStrengthBarChart(): Get Top Strength Bar Chart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTopStrengthBarChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {

            HashMap<String, Integer> result = visualisationSessionBean.getTopStrength(orgNames, DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup, race, gender);

            List<String> labels = new ArrayList<>(result.keySet());
            List<Integer> data = new ArrayList<>(result.values());

            System.out.println("********** DashboardResource.getTopStrengthBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new NeedsBarChartResponse("Top Strengths for Organisation", labels, data, "Total Number of 2/3")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTopStrengthBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }

    }

    @Path("toptrauma-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopTraumaBarChart(@Context HttpHeaders headers, @QueryParam("organisation_name") List<String> orgNames, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {
        System.out.println("********** DashboardResource.getTopTraumaBarChart(): Get Top Trauma Bar Chart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTopTraumaBarChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {

            HashMap<String, Integer> result = visualisationSessionBean.getTopTrauma(orgNames, ageGroup, race, gender);

            List<String> labels = new ArrayList<>(result.keySet());
            List<Integer> data = new ArrayList<>(result.values());

            System.out.println("********** DashboardResource.getTopTraumaBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new NeedsBarChartResponse("Top Trauma for Organisation", labels, data, "Total Number of 2/3")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTopTraumaBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }

    }

    @Path("traumapercentage-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTraumaPercentageBarChart(@Context HttpHeaders headers, @QueryParam("organisation_name") List<String> orgNames, @QueryParam("age_group") String ageGroup, @QueryParam("race") List<String> race, @QueryParam("gender") List<String> gender) {

        System.out.println("********** DashboardResource.getTraumaPercentageBarChart(): Get Trauma BarChart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTraumaPercentageBarChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {

            Pair<List<String>, List<Double>> results = visualisationSessionBean.getTraumaPercentage(orgNames, ageGroup, race, gender);
            List<String> labels = results.getKey();
            List<Double> data = results.getValue();
            for (Double d : data) {
                System.out.println(d);
            }
            System.out.println("********** DashboardResource.getTraumaPercentageBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new PercentageBarChartResponse("Trauma Percentage Within Organisation", labels, data, "Percentage Response")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** DataManagementResources.getTopTraumaBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();
        }
    }

    @Path("locdistributionfilter-barchart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocDistributionFilterBarChart(@Context HttpHeaders headers, @QueryParam("organisation_name") List<String> orgNames, @QueryParam("assessment_reasons") List<String> assReason, @QueryParam("start_date") String startDate, @QueryParam("end_date") String endDate, @QueryParam("age_group") String ageGroup) {

        System.out.println("********** DashboardResource.getLocDistributionFilterBarChart(): Get LOC Distribution Bar Chart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getLocDistributionFilterBarChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {

            HashMap<String, List<Integer>> results = visualisationSessionBean.getDistributionOfLocByOrganisations(orgNames, DatatypeConverter.parseDateTime(startDate).getTime(), DatatypeConverter.parseDateTime(endDate).getTime(), ageGroup, assReason);
            List<String> org = new ArrayList<>(results.keySet());
            List<List<Integer>> data = new ArrayList<>(results.values());

            System.out.println("********** DashboardResource.getLocDistributionFilterBarChart(): Finished **********");
            return Response.status(Response.Status.OK).entity(new DistributionBarChartResponse("Distribution of Loc Across Organisation Type", org, data, "Total Count for each LOC")).build();

        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** DataManagementResources.getLocDistributionByOrgBarChart(): Request denied due organisation invalid **********");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Organisation do not exist.")).build();

        }
    }

    @Path("assessment-types")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAssessmentTypes(@Context HttpHeaders headers) {
        System.out.println("********** DashboardResource.getAllAssessmentTypes(): Get all assessment types request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getAllAssessmentTypes(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> results = new ArrayList<>(
                (Arrays.asList("INITIAL",
                        "REASSESSMENT",
                        "DISCHARGE",
                        "CRITICAL_INCIDENT",
                        "TRANSFER")));
        System.out.println("********** DashboardResource.getAllAssessmentTypes(): Finished **********");
        return Response.status(Response.Status.OK).entity(new AssessmentNameResponse(results)).build();
    }

    @Path("organisation-types")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrganisationTypes(@Context HttpHeaders headers) {
        System.out.println("********** DashboardResource.getAllOrganisationTypes(): Get all organisation types request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getAllOrganisationTypes(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> orgTypes = new ArrayList<>(Arrays.asList("FA - Fostering Agency",
                "VCH - Voluntary Children's Home",
                "PSGC - Pilot Small Group Care",
                "TGH - Therapeutic Group Home",
                "CFPS - Clinical and Forensic Psychology Service, MSF",
                "CIC - Children in Care, MSF",
                "CPS - Child Protection Service, MSF",
                "YRS - Youth Residential Service, MSF"));

        List<OrganisationTypeInfo> results = new ArrayList<>();

        for (String types : orgTypes) {
            List<String> orgNames = visualisationSessionBean.getOrgFromAgencyType(types);
            results.add(new OrganisationTypeInfo(types, orgNames));
        }

        System.out.println("********** DashboardResource.getAllOrganisationTypes(): Finished **********");
        return Response.status(Response.Status.OK).entity(new OrganisationNameResponse(results)).build();
    }

    @Path("race-piechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRacePieChart(@Context HttpHeaders headers) {
        System.out.println("********** DashboardResource.getRacePieChart(): Get Race PieChart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getRacePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Long>> results = visualisationSessionBean.getCountByRace();
        List<String> labels = results.getKey();
        List<Long> data = results.getValue();

        System.out.println("********** DashboardResource.getRacePieChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new PieChartResponse("Total client race group", data, labels)).build();
    }

    @Path("gender-piechart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenderPieChart(@Context HttpHeaders headers) {
        System.out.println("********** DashboardResource.getGenderPieChart(): Get Gender PieChart request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getGenderPieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        Pair<List<String>, List<Long>> results = visualisationSessionBean.getCountByGender();
        List<String> labels = results.getKey();
        List<Long> data = results.getValue();

        System.out.println("********** DashboardResource.getGenderPieChart(): Finished **********");
        return Response.status(Response.Status.OK).entity(new PieChartResponse("Total client gender group", data, labels)).build();
    }

    @Path("ethnicity")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEthnicity(@Context HttpHeaders headers) {
        System.out.println("********** DashboardResource.getAllEthnicity(): Get all ethcnicity types request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getAllEthnicity(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> results = clientSessionBean.retrieveUniqueEthnicityOfClients();
        System.out.println("********** DashboardResource.getAllEthnicity(): Finished **********");
        return Response.status(Response.Status.OK).entity(results).build();
    }

    @Path("gender")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGender(@Context HttpHeaders headers) {
        System.out.println("********** DashboardResource.getAllGender(): Get all gender types request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getAllGender(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> results = clientSessionBean.retrieveUniqueGenderOfClients();
        System.out.println("********** DashboardResource.getAllGender(): Finished **********");
        return Response.status(Response.Status.OK).entity(results).build();
    }

}

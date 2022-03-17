/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessmentSessionBeanLocal;
import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.DomainSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.OrganisationSessionBeanLocal;
import entity.AdminUserEntity;
import entity.AgeGroupEntity;
import entity.AssessorEntity;
import entity.DomainEntity;
import entity.MainQuestionEntity;
import entity.SubModuleEntity;
import entity.SubQuestionEntity;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.LoginTokenNotFoundException;
import util.exception.UserNotFoundException;
import ws.datamodel.ErrorResponse;

/**
 * REST Web Service
 *
 * @author Ooi Jun Hao
 */
@Path("AssessmentQuestionManagement")
public class AssessmentQuestionManagementResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final AssessmentSessionBeanLocal assessmentSessionBean;
    private final DomainSessionBeanLocal domainSessionBean;

    public AssessmentQuestionManagementResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.assessmentSessionBean = sessionBeanLookUp.assessmentSessionBean;
        this.domainSessionBean = sessionBeanLookUp.domainSessionBean;
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

    @Path("assessment-questions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAssessmentQuestions(@Context HttpHeaders headers) {
        System.out.println("********** AssessmentQuestionManagementResource.getAllAssessmentQuestions(): Get all domains and stuff request recieved **********");
        
        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentQuestionManagementResource.getAllAssessmentQuestions(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        
        List<DomainEntity> domains = domainSessionBean.retrieveAllDomains();
        for (DomainEntity domain : domains) {
            for (AgeGroupEntity ageGroup : domain.getAgeGroups()) {
                ageGroup.setDomain(null);
                List<MainQuestionEntity> mainQ = ageGroup.getQuestions();
                for (MainQuestionEntity question : mainQ) {
                    question.setAgeGroup(null);
                    SubModuleEntity subModule = question.getSubModule();
                    if (question.getSubModule() != null) {
                        subModule.getQues().clear();
                        List<SubQuestionEntity> subQuestions = subModule.getSubQues();
                        for (SubQuestionEntity subQuestion : subQuestions) {
                            subQuestion.setSubmodule(null);
                        }
                    }
                }
            }
        }
        System.out.println("**********  AssessmentQuestionManagementResource.getAllAssessmentQuestions(): Finished and returned " + domains.size() + " domains **********");
        GenericEntity<List<DomainEntity>> genericDomains = new GenericEntity<List<DomainEntity>>(domains) {
        };
        return Response.status(Response.Status.OK).entity(genericDomains).build();
    }

    @Path("assessment-questions")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateQuestions(@Context HttpHeaders headers, List<DomainEntity> domains) {
        System.out.println("********** AssessmentQuestionManagementResource.updateAssessmentQuestions(): Update domains request received **********");
        
        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentQuestionManagementResource.updateAssessmentQuestions(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        
        domainSessionBean.updateDomains(domains);
        
 
        return Response.status(Response.Status.OK).build();
    }
    
}

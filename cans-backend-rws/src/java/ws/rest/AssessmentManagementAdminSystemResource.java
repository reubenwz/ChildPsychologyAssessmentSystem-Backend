/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessmentSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import entity.AdminUserEntity;
import entity.AssessmentEntity;
import entity.CaretakerAssessmentEntity;
import entity.MainQuestionEntity;
import entity.QuestionEntity;
import entity.ResponseEntity;
import entity.SubQuestionEntity;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.enumeration.AssessmentStatusEnum;
import util.exception.AssessmentNotFoundException;
import util.exception.LoginTokenNotFoundException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssessmentStatusUpdateBody;
import ws.datamodel.ErrorResponse;

/**
 * REST Web Service
 *
 * @author Ooi Jun Hao
 */
@Path("AssessmentManagement-AdminSystem")
public class AssessmentManagementAdminSystemResource {

    @Context
    private UriInfo context;
    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final AssessmentSessionBeanLocal assessmentSessionBean;

    public AssessmentManagementAdminSystemResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.assessmentSessionBean = sessionBeanLookUp.assessmentSessionBean;
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

    @Path("assessments")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllAssessments(@Context HttpHeaders headers, @QueryParam("client_id") Long clientId) {
        System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAllAssessments(): Get assessments request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAllAssessments(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        // retrieve all assessments
        List<AssessmentEntity> assessments = assessmentSessionBean.retrieveAllAssessment();
        if (clientId != null) {
            assessments.removeIf(x -> !x.getClient().getClientId().equals(clientId));
        }
        for (AssessmentEntity assessment : assessments) {
            assessment.getAssessor().getAssessments().clear();
            assessment.getAssessor().getClients().clear();
            assessment.getAssessor().getSupervisee().clear();
            assessment.getAssessor().setSupervisor(null);
            assessment.getAssessor().getCertificates().clear();
            assessment.getAssessor().setOrganisation(null);
            assessment.getClient().getAssessment().clear();
            assessment.getClient().getCaretakers().clear();
            assessment.getClient().setAssessor(null);
            assessment.getResponse().clear();
            for (CaretakerAssessmentEntity caretakerAssessment : assessment.getCaretakerAssessments()) {
                caretakerAssessment.getCaretaker().setClient(null);
                caretakerAssessment.getCaretaker().getCaretakerAssessments().clear();
                caretakerAssessment.setAssessment(null);
                caretakerAssessment.getCaretakerResponses().clear();
            }
        }

        System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAllAssessments(): Finished and returned " + assessments.size() + " assessments **********");
        GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessments) {
        };
        return Response.status(Response.Status.OK).entity(genericAssessments).build();
    }

    @Path("assessments/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssessmentById(@Context HttpHeaders headers, @PathParam("id") Long assessmentId) {
        System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAssessmentById(): Get assessment by ID request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAssessmentById(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        // retrieve all assessments
        AssessmentEntity assessment;
        try {
            assessment = assessmentSessionBean.retrieveAssessmentById(assessmentId);

            assessment.getAssessor().getAssessments().clear();
            assessment.getAssessor().getClients().clear();
            assessment.getAssessor().getSupervisee().clear();
            if (assessment.getAssessor().getSupervisor() != null) {
                assessment.getAssessor().setSupervisor(null);
            }
            assessment.getAssessor().getCertificates().clear();
            assessment.getAssessor().setOrganisation(null);
            assessment.getClient().getAssessment().clear();
            assessment.getClient().getCaretakers().clear();
            if (assessment.getClient().getAssessor() != null) { // should always hit this
                assessment.getClient().setAssessor(null);
            }
            for (CaretakerAssessmentEntity caretakerAssessment : assessment.getCaretakerAssessments()) {
                if (caretakerAssessment.getCaretaker().getClient() != null) {
                    caretakerAssessment.getCaretaker().setClient(null);
                }
                caretakerAssessment.getCaretaker().getCaretakerAssessments().clear();
                caretakerAssessment.setAssessment(null);
                for (ResponseEntity response : caretakerAssessment.getCaretakerResponses()) {
                    QuestionEntity question = response.getQuestion();
                    if (question instanceof MainQuestionEntity) {
                        ((MainQuestionEntity) question).setAgeGroup(null);
                        ((MainQuestionEntity) question).setSubModule(null);
                    } else if (question instanceof SubQuestionEntity) {
                        ((SubQuestionEntity) question).setSubmodule(null);
                    }
                }
            }

            for (ResponseEntity response : assessment.getResponse()) {
                QuestionEntity question = response.getQuestion();
                if (question instanceof MainQuestionEntity) {
                    ((MainQuestionEntity) question).setAgeGroup(null);
                    ((MainQuestionEntity) question).setSubModule(null);
                } else if (question instanceof SubQuestionEntity) {
                    ((SubQuestionEntity) question).setSubmodule(null);
                }
            }

            System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAssessmentById(): Finished and returned assessment ID " + assessment.getAssessmentId() + " **********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAssessmentById(): Error -> AssessmentID " + assessmentId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment does not exist")).build();
        }
    }
    
    @Path("assessments/{id}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAssessmentById(@Context HttpHeaders headers, @PathParam("id") Long assessmentId, AssessmentStatusUpdateBody updateBody) {
        System.out.println("********** AssessmentManagementAdminSystemResource.updateAssessmentById(): Get assessment by ID request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentManagementAdminSystemResource.updateAssessmentById(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        // retrieve all assessments
        AssessmentEntity assessment;
        try {
            String new_status = updateBody.status;
            AssessmentStatusEnum enu = AssessmentStatusEnum.REJECTED;
            if (new_status.equals("ASSIGNED")) {
                enu = AssessmentStatusEnum.ASSIGNED;
            } else if (new_status.equals("SUBMITTED")) {
                enu = AssessmentStatusEnum.SUBMITTED;
            } else if (new_status.equals("APPROVED")) {
                enu = AssessmentStatusEnum.APPROVED;
            } 
            Long id = assessmentSessionBean.updateAssessmentStatus(assessmentId, enu);
            assessment = assessmentSessionBean.retrieveAssessmentById(assessmentId);

            assessment.getAssessor().getAssessments().clear();
            assessment.getAssessor().getClients().clear();
            assessment.getAssessor().getSupervisee().clear();
            if (assessment.getAssessor().getSupervisor() != null) {
                assessment.getAssessor().setSupervisor(null);
            }
            assessment.getAssessor().getCertificates().clear();
            assessment.getAssessor().setOrganisation(null);
            assessment.getClient().getAssessment().clear();
            assessment.getClient().getCaretakers().clear();
            if (assessment.getClient().getAssessor() != null) { // should always hit this
                assessment.getClient().setAssessor(null);
            }
            for (CaretakerAssessmentEntity caretakerAssessment : assessment.getCaretakerAssessments()) {
                if (caretakerAssessment.getCaretaker().getClient() != null) {
                    caretakerAssessment.getCaretaker().setClient(null);
                }
                caretakerAssessment.getCaretaker().getCaretakerAssessments().clear();
                caretakerAssessment.setAssessment(null);
                for (ResponseEntity response : caretakerAssessment.getCaretakerResponses()) {
                    QuestionEntity question = response.getQuestion();
                    if (question instanceof MainQuestionEntity) {
                        ((MainQuestionEntity) question).setAgeGroup(null);
                        ((MainQuestionEntity) question).setSubModule(null);
                    } else if (question instanceof SubQuestionEntity) {
                        ((SubQuestionEntity) question).setSubmodule(null);
                    }
                }
            }

            for (ResponseEntity response : assessment.getResponse()) {
                QuestionEntity question = response.getQuestion();
                if (question instanceof MainQuestionEntity) {
                    ((MainQuestionEntity) question).setAgeGroup(null);
                    ((MainQuestionEntity) question).setSubModule(null);
                } else if (question instanceof SubQuestionEntity) {
                    ((SubQuestionEntity) question).setSubmodule(null);
                }
            }

            System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAssessmentById(): Finished and returned assessment ID " + assessment.getAssessmentId() + " **********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentManagementAdminSystemResource.retrieveAssessmentById(): Error -> AssessmentID " + assessmentId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment does not exist")).build();
        }
    }
}

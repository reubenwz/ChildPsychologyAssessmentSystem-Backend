/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessmentSessionBeanLocal;
import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CaretakerAssessmentEntity;
import entity.MainQuestionEntity;
import entity.QuestionEntity;
import entity.ResponseEntity;
import entity.SubQuestionEntity;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.enumeration.AssessmentStatusEnum;
import util.exception.AssessmentNotFoundException;
import util.exception.AssessmentStatusUpdateException;
import util.exception.LoginTokenNotFoundException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssessmentSubmitDetailBody;
import ws.datamodel.ErrorResponse;

/**
 * REST Web Service
 *
 * @author Ziyue
 * @author Ong Bik Jeun

 */
@Path("/assessmentValidation")
public class AssessmentValidationResource {

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final AssessmentSessionBeanLocal assessmentSessionBean;
    private final AssessorSessionBeanLocal assessorSessionBean;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AssessmentValidationResource
     */
    public AssessmentValidationResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.assessmentSessionBean = sessionBeanLookUp.assessmentSessionBean;
        this.assessorSessionBean = sessionBeanLookUp.assessorSessionBean;
    }

    private AssessorEntity validateAssessor(HttpHeaders headers) throws UserNotFoundException {
        try {

            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAssessorSystem(headerToken);
        } catch (NullPointerException | LoginTokenNotFoundException ex) {
            throw new UserNotFoundException();
        }
    }


    @Path("supervisees")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSuperviseesBySupervisorId(@Context HttpHeaders headers) {
        System.out.println("AssessmentValidationResource.retrieveSuperviseesBySupervisorId() : Get Supervisees By Supervisor");

        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);

            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {

            supervisor = assessorSessionBean.retrieveUserById(supervisor.getAssessorId());

            List<AssessorEntity> assessors = new ArrayList<>();
            if (supervisor.isRoot()) {

                assessors = assessorSessionBean.retrieveAllAssessorsByOrganisation(supervisor.getOrganisation().getOrganisationId());
                AssessorEntity toRemove = null;
                for (AssessorEntity assessor : assessors) {
                    if (assessor.isRoot()) {
                        toRemove = assessor;
                        continue;
                    }
//                    assessor.getAssessments().clear();
                    List<AssessmentEntity> assessments = assessor.getAssessments();
                    for (AssessmentEntity assessment : assessments) {
                        assessment.setAssessor(null);
                        assessment.setClient(null);
                        assessment.getResponse().clear();
                        assessment.getCaretakerAssessments().clear();
                    }
                    assessor.getCertificates().clear();
                    assessor.getClients().clear();
//                    assessor.setOrganisation(null);
                    assessor.getOrganisation().getAssessors().clear();
                    assessor.getSupervisee().clear();
                    assessor.setSupervisor(null);
                    assessor.setSalt(null);
                    assessor.setPassword(null);
                }
                assessors.remove(toRemove);
            } else {
                assessors = supervisor.getSupervisee();
                for (AssessorEntity assessor : assessors) {
//                    assessor.getAssessments().clear();
                    List<AssessmentEntity> assessments = assessor.getAssessments();
                    for (AssessmentEntity assessment : assessments) {
                        assessment.setAssessor(null);
                        assessment.setClient(null);
                        assessment.getResponse().clear();
                        assessment.getCaretakerAssessments().clear();
                    }
                    assessor.getCertificates().clear();
                    assessor.getClients().clear();
//                    assessor.setOrganisation(null);
                    assessor.getOrganisation().getAssessors().clear();
                    assessor.getSupervisee().clear();
                    assessor.setSupervisor(null);
                    assessor.setSalt(null);
                    assessor.setPassword(null);
                }
            }

            System.out.println("********** AssessmentValidationResource.retrieveSuperviseesBySupervisorId(): Finished and returned " + assessors.size() + " assessors **********");
            GenericEntity<List<AssessorEntity>> genericAssessors = new GenericEntity<List<AssessorEntity>>(assessors) {
            };
            return Response.status(Response.Status.OK).entity(genericAssessors).build();
        } catch (UserNotFoundException ex) {

            System.out.println("********** AssessmentValidationResource.retrieveSuperviseesBySupervisorId(): Error -> Supervisor ID " + supervisor.getAssessorId() + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Supervisor does not exist")).build();
        }
    }

    @Path("assessments/submitted/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSubmittedAssessmentsBySuperviseeId(@Context HttpHeaders headers, @PathParam("id") Long superviseeId) {
        //return basic details and clear responses etc as there would be a seperate call
        System.out.println("********** AssessmentValidationResource.retrieveSubmittedAssessmentsBySuperviseeId(): Get submitted assessment by supervisee id " + superviseeId + " request received **********");


        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);
            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }
        } catch (UserNotFoundException ex) {

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            AssessorEntity supervisee = assessorSessionBean.retrieveUserById(superviseeId);
            List<AssessmentEntity> assessments = supervisee.getAssessments();
            List<AssessmentEntity> assessmentsToReturn = new ArrayList<>();
            for (AssessmentEntity assessment : assessments) {
                if (assessment.getStatus().equals(AssessmentStatusEnum.SUBMITTED)) {
                    assessmentsToReturn.add(assessment);
                }
            }
            for (AssessmentEntity assessment : assessmentsToReturn) {
                assessment.getCaretakerAssessments().clear();

//                assessment.setAssessor(null);
                assessment.getAssessor().getAssessments().clear();
                assessment.getAssessor().setPassword(null);
                assessment.getAssessor().setSalt(null);
                assessment.getAssessor().setOrganisation(null);
                assessment.getAssessor().getClients().clear();
                assessment.getAssessor().setSupervisor(null);
                assessment.getAssessor().getSupervisee().clear();
                assessment.getAssessor().getCertificates().clear();

//                assessment.setClient(null);
                assessment.getClient().getAssessment().clear();
                assessment.getClient().setAssessor(null);
                assessment.getClient().getCaretakers().clear();

                assessment.getResponse().clear();
            }

            System.out.println("********** AssessmentValidationResource.retrieveSubmittedAssessmentsBySuperviseeId(): Finished and returned assessments of size " + assessmentsToReturn.size() + " **********");
            GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessmentsToReturn) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentValidationResource.retrieveSubmittedAssessmentsBySuperviseeId(): Error -> Supervisee ID " + superviseeId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Supervisee does not exist")).build();
        }
    }

    @Path("assessments/assigned/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssignedAssessmentsBySuperviseeId(@Context HttpHeaders headers, @PathParam("id") Long superviseeId) {
        //return basic details and clear responses etc as there would be a seperate call
        System.out.println("********** AssessmentValidationResource.retrieveAssignedAssessmentsBySuperviseeId(): Get assigned assessment by supervisee id " + superviseeId + " request received **********");

        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);
            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }
        } catch (UserNotFoundException ex) {

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            AssessorEntity supervisee = assessorSessionBean.retrieveUserById(superviseeId);
            List<AssessmentEntity> assessments = supervisee.getAssessments();
            List<AssessmentEntity> assessmentsToReturn = new ArrayList<>();
            for (AssessmentEntity assessment : assessments) {
                if (assessment.getStatus().equals(AssessmentStatusEnum.ASSIGNED)) {
                    assessmentsToReturn.add(assessment);
                }
            }
            for (AssessmentEntity assessment : assessmentsToReturn) {
                assessment.getCaretakerAssessments().clear();

//                assessment.setAssessor(null);
                assessment.getAssessor().getAssessments().clear();
                assessment.getAssessor().setPassword(null);
                assessment.getAssessor().setSalt(null);
                assessment.getAssessor().setOrganisation(null);
                assessment.getAssessor().getClients().clear();
                assessment.getAssessor().setSupervisor(null);
                assessment.getAssessor().getSupervisee().clear();
                assessment.getAssessor().getCertificates().clear();

//                assessment.setClient(null);
                assessment.getClient().getAssessment().clear();
                assessment.getClient().setAssessor(null);
                assessment.getClient().getCaretakers().clear();

                assessment.getResponse().clear();
            }

            System.out.println("********** AssessmentValidationResource.retrieveAssignedAssessmentsBySuperviseeId(): Finished and returned assessments of size " + assessmentsToReturn.size() + " **********");
            GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessmentsToReturn) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentValidationResource.retrieveAssignedAssessmentsBySuperviseeId(): Error -> Supervisee ID " + superviseeId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Supervisee does not exist")).build();
        }
    }

    @Path("assessments/approved/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveApprovedAssessmentsBySuperviseeId(@Context HttpHeaders headers, @PathParam("id") Long superviseeId) {
        //return basic details and clear responses etc as there would be a seperate call
        System.out.println("********** AssessmentValidationResource.retrieveApprovedAssessmentsBySuperviseeId(): Get approved assessment by supervisee id " + superviseeId + " request received **********");

        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);
            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }
        } catch (UserNotFoundException ex) {

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            AssessorEntity supervisee = assessorSessionBean.retrieveUserById(superviseeId);
            List<AssessmentEntity> assessments = supervisee.getAssessments();
            List<AssessmentEntity> assessmentsToReturn = new ArrayList<>();
            for (AssessmentEntity assessment : assessments) {
                if (assessment.getStatus().equals(AssessmentStatusEnum.APPROVED)) {
                    assessmentsToReturn.add(assessment);
                }
            }
            for (AssessmentEntity assessment : assessmentsToReturn) {
                assessment.getCaretakerAssessments().clear();

//                assessment.setAssessor(null);
                assessment.getAssessor().getAssessments().clear();
                assessment.getAssessor().setPassword(null);
                assessment.getAssessor().setSalt(null);
                assessment.getAssessor().setOrganisation(null);
                assessment.getAssessor().getClients().clear();
                assessment.getAssessor().setSupervisor(null);
                assessment.getAssessor().getSupervisee().clear();
                assessment.getAssessor().getCertificates().clear();

//                assessment.setClient(null);
                assessment.getClient().getAssessment().clear();
                assessment.getClient().setAssessor(null);
                assessment.getClient().getCaretakers().clear();

                assessment.getResponse().clear();
            }

            System.out.println("********** AssessmentValidationResource.retrieveApprovedAssessmentsBySuperviseeId(): Finished and returned assessments of size " + assessmentsToReturn.size() + " **********");
            GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessmentsToReturn) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentValidationResource.retrieveApprovedAssessmentsBySuperviseeId(): Error -> Supervisee ID " + superviseeId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Supervisee does not exist")).build();
        }
    }

    @Path("assessments/rejected/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveRejectedAssessmentsBySuperviseeId(@Context HttpHeaders headers, @PathParam("id") Long superviseeId) {
        //return basic details and clear responses etc as there would be a seperate call
        System.out.println("********** AssessmentValidationResource.retrieveRejectedAssessmentsBySuperviseeId(): Get rejected assessment by supervisee id " + superviseeId + " request received **********");


        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);
            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }
        } catch (UserNotFoundException ex) {

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            AssessorEntity supervisee = assessorSessionBean.retrieveUserById(superviseeId);
            List<AssessmentEntity> assessments = supervisee.getAssessments();
            List<AssessmentEntity> assessmentsToReturn = new ArrayList<>();
            for (AssessmentEntity assessment : assessments) {
                if (assessment.getStatus().equals(AssessmentStatusEnum.REJECTED)) {
                    assessmentsToReturn.add(assessment);
                }
            }
            for (AssessmentEntity assessment : assessmentsToReturn) {
                assessment.getCaretakerAssessments().clear();

//                assessment.setAssessor(null);
                assessment.getAssessor().getAssessments().clear();
                assessment.getAssessor().setPassword(null);
                assessment.getAssessor().setSalt(null);
                assessment.getAssessor().setOrganisation(null);
                assessment.getAssessor().getClients().clear();
                assessment.getAssessor().setSupervisor(null);
                assessment.getAssessor().getSupervisee().clear();
                assessment.getAssessor().getCertificates().clear();

//                assessment.setClient(null);
                assessment.getClient().getAssessment().clear();
                assessment.getClient().setAssessor(null);
                assessment.getClient().getCaretakers().clear();

                assessment.getResponse().clear();
            }

            System.out.println("********** AssessmentValidationResource.retrieveRejectedAssessmentsBySuperviseeId(): Finished and returned assessments of size " + assessmentsToReturn.size() + " **********");
            GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessmentsToReturn) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentValidationResource.retrieveRejectedAssessmentsBySuperviseeId(): Error -> Supervisee ID " + superviseeId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Supervisee does not exist")).build();
        }
    }

    @Path("assessment/approveAssessment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response approveAssessment(@Context HttpHeaders headers, AssessmentSubmitDetailBody body) {
        System.out.println("********** AssessmentValidationResource.approveAssessment(): Approve assessment request received **********");

        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);
            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }
        } catch (UserNotFoundException ex) {

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            AssessmentEntity assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);
            assessmentSessionBean.approveAssessment(body.assessment_id);
            assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);

            assessment.setAssessor(null);
            assessment.setClient(null);

            assessment.getCaretakerAssessments().clear();
            assessment.getResponse().clear();

            System.out.println("********** AssessmentValidationResource.approveAssessment(): Assessment ID " + assessment.getAssessmentUniqueId() + " approved successfullly. **********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentValidationResource.approveAssessment(): Assessment of ID " + body.assessment_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment ID " + body.assessment_id + " does not exist.")).build();
        } catch (AssessmentStatusUpdateException ex) {
            System.out.println("********** AssessmentValidationResource.approveAssessment(): Assessment of ID " + body.assessment_id + " has to be submitted by the case worker before it can be approved **********");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("Assessment ID " + body.assessment_id + " has to be submitted by the case worker before it can be approved.")).build();
        }
    }

    @Path("assessment/rejectAssessment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejectAssessment(@Context HttpHeaders headers, AssessmentSubmitDetailBody body) {
        System.out.println("********** AssessmentValidationResource.rejectAssessment(): Reject assessment request received **********");

        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);
            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }
        } catch (UserNotFoundException ex) {

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            AssessmentEntity assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);
            assessmentSessionBean.rejectAssessment(body.assessment_id);
            assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);

            assessment.setAssessor(null);
            assessment.setClient(null);

            assessment.getCaretakerAssessments().clear();
            assessment.getResponse().clear();

            System.out.println("********** AssessmentValidationResource.rejectAssessment(): Assessment ID " + assessment.getAssessmentUniqueId() + " rejected successfullly. **********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentValidationResource.rejectAssessment(): Assessment of ID " + body.assessment_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment ID " + body.assessment_id + " does not exist.")).build();
        } catch (AssessmentStatusUpdateException ex) {
            System.out.println("********** AssessmentValidationResource.rejectAssessment(): Assessment of ID " + body.assessment_id + " has to be submitted by the case worker before it can be rejected **********");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("Assessment ID " + body.assessment_id + " has to be submitted by the case worker before it can be rejected.")).build();
        }
    }


    @Path("assessments/allSubmitted")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllSubmittedAssessmentsBySupervisorId(@Context HttpHeaders headers) {

        //return basic details and clear responses etc as there would be a seperate call of all supervisee assessmnents of supervisor
        System.out.println("AssessmentValidationResource.retrieveAllSubmittedAssessmentsBySupervisorId() : Get all submitted assessments to be approved By Supervisor");

        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {

            supervisor = assessorSessionBean.retrieveUserById(supervisor.getAssessorId());

            List<AssessorEntity> assessors = new ArrayList<>();
            List<AssessmentEntity> assessments = new ArrayList<>();
            if (supervisor.isRoot()) {

                assessors = assessorSessionBean.retrieveAllAssessorsByOrganisation(supervisor.getOrganisation().getOrganisationId());
                for (AssessorEntity assessor : assessors) {
                    if (!assessor.isRoot()) {
                        for (AssessmentEntity assessment : assessor.getAssessments()) {
                            if (assessment.getStatus().equals(AssessmentStatusEnum.SUBMITTED)) {
                                assessments.add(assessment);
                            }
                        }
                    }
                }
            } else {
                assessors = supervisor.getSupervisee();
                for (AssessorEntity assessor : assessors) {
                    for (AssessmentEntity assessment : assessor.getAssessments()) {
                        if (assessment.getStatus().equals(AssessmentStatusEnum.SUBMITTED)) {
                            assessments.add(assessment);
                        }
                    }
                }
            }

            for (AssessmentEntity assessment : assessments) {
                assessment.getCaretakerAssessments().clear();

//                assessment.setAssessor(null);
                assessment.getAssessor().getAssessments().clear();
                assessment.getAssessor().setPassword(null);
                assessment.getAssessor().setSalt(null);
                assessment.getAssessor().setOrganisation(null);
                assessment.getAssessor().getClients().clear();
                assessment.getAssessor().setSupervisor(null);
                assessment.getAssessor().getSupervisee().clear();
                assessment.getAssessor().getCertificates().clear();

//                assessment.setClient(null);
                assessment.getClient().getAssessment().clear();
                assessment.getClient().setAssessor(null);
                assessment.getClient().getCaretakers().clear();

                assessment.getResponse().clear();
            }

            System.out.println("********** AssessmentValidationResource.retrieveAllSubmittedAssessmentsBySupervisorId(): Finished and returned " + assessments.size() + " assessments **********");
            GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessments) {
            };
            return Response.status(Response.Status.OK).entity(genericAssessments).build();
        } catch (UserNotFoundException ex) {

            System.out.println("********** AssessmentValidationResource.retrieveAllSubmittedAssessmentsBySupervisorId(): Error -> Supervisor ID " + supervisor.getAssessorId() + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Supervisor does not exist")).build();
        }
    }


    @Path("assessment/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssessmentByAssessmentId(@Context HttpHeaders headers, @PathParam("id") Long assessmentId) {
        System.out.println("********** AssessmentValidationResource.retrieveAssessmentByAssessmentId(): Get assessment by assessment id " + assessmentId + " request received **********");


        AssessorEntity supervisor;
        try {
            supervisor = this.validateAssessor(headers);
            if (supervisor.getSupervisee().isEmpty()) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("You are not a supervisor.")).build();
            }
        } catch (UserNotFoundException ex) {

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            AssessmentEntity assessment = this.assessmentSessionBean.retrieveAssessmentByUniqueId(assessmentId);

            assessment.getAssessor().getAssessments().clear();
            assessment.getAssessor().setPassword(null);
            assessment.getAssessor().setSalt(null);
            assessment.getAssessor().setOrganisation(null);
            assessment.getAssessor().getClients().clear();
            assessment.getAssessor().setSupervisor(null);
            assessment.getAssessor().getSupervisee().clear();
            assessment.getAssessor().getCertificates().clear();
//            assessment.getResponse().clear();
            List<ResponseEntity> responses = assessment.getResponse();
            for (ResponseEntity response : responses) {
                QuestionEntity question = response.getQuestion();
//                question.getQuestionDescription().clear();
                question.getQuestionToConsider().clear();
                question.getRatingsDefinition().clear();
                if (question instanceof MainQuestionEntity) {
                    ((MainQuestionEntity) question).setAgeGroup(null);
                    ((MainQuestionEntity) question).setSubModule(null);
                } else if (question instanceof SubQuestionEntity) {
                    ((SubQuestionEntity) question).setSubmodule(null);
                }
            }

//            assessment.setClient(null);
            assessment.getClient().getAssessment().clear();
            assessment.getClient().setAssessor(null);
            assessment.getClient().getCaretakers().clear();

            List<CaretakerAssessmentEntity> caretakerAssessments = assessment.getCaretakerAssessments();

            for (CaretakerAssessmentEntity caretakerAssessment : caretakerAssessments) {
                caretakerAssessment.setAssessment(null);
                caretakerAssessment.setCaretaker(null);

                caretakerAssessment.getCaretakerResponses().clear();
            }

            System.out.println("********** AssessmentValidationResource.retrieveAssessmentByAssessmentId(): Finished and returned assessment " + assessmentId + " **********");
            GenericEntity<AssessmentEntity> genericAssessments = new GenericEntity<AssessmentEntity>(assessment) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentValidationResource.retrieveAssessmentByAssessmentId(): Error -> Assessment ID " + assessmentId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessment does not exist")).build();
        }
    }

}

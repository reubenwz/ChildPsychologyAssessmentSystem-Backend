/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessmentSessionBeanLocal;
import ejb.session.stateless.CaretakerAssessmentSessionBeanLocal;
import ejb.session.stateless.ClientSessionBeanLocal;
import ejb.session.stateless.DomainSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import entity.AgeGroupEntity;
import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CaretakerAssessmentEntity;
import entity.CaretakerEntity;
import entity.ClientEntity;
import entity.DomainEntity;
import entity.MainQuestionEntity;
import entity.QuestionEntity;
import entity.ResponseEntity;
import entity.SubModuleEntity;
import entity.SubQuestionEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import util.enumeration.AssessmentReasonEnum;
import util.enumeration.AssessmentStatusEnum;
import util.enumeration.CaretakerAlgorithmEnum;
import util.enumeration.CaretakerTypeEnum;
import util.exception.AssessmentExistsException;
import util.exception.AssessmentNotFoundException;
import util.exception.AssessmentStatusUpdateException;
import util.exception.CaretakerAssessmentExistsException;
import util.exception.CaretakerAssessmentNotFoundException;
import util.exception.CaretakerNotFoundException;
import util.exception.ClientNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.LoginTokenNotFoundException;
import util.exception.QuestionNotFoundException;
import util.exception.ResponseExistsException;
import util.exception.ResponseNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssessmentDetailBody;
import ws.datamodel.AssessmentNameResponse;
import ws.datamodel.AssessmentStatusDetailBody;
import ws.datamodel.AssessmentSubmitDetailBody;
import ws.datamodel.CaregiverAlgorithmResponse;
import ws.datamodel.CaregiverAssessmentCheckResponse;
import ws.datamodel.CaregiverAssessmentDetailBody;
import ws.datamodel.CaregiverTypeResponse;
import ws.datamodel.CaregiverResponsesDetailBody;
import ws.datamodel.ErrorResponse;
import ws.datamodel.RedoAssessmentDetailBody;
import ws.datamodel.ResponsesDetailBody;
import ws.datamodel.ResponsesStringDetailBody;

/**
 * REST Web Service
 *
 * @author Ziyue
 * @author Ong Bik Jeun
 */
@Path("/Assessment")
public class AssessmentResource {

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final ClientSessionBeanLocal clientSessionBean;
    private final AssessmentSessionBeanLocal assessmentSessionBean;
    private final DomainSessionBeanLocal domainSessionBean;
    private final CaretakerAssessmentSessionBeanLocal caretakerAssessmentSessionBean;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AssessmentResource
     */
    public AssessmentResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.clientSessionBean = sessionBeanLookUp.clientSessionBean;
        this.assessmentSessionBean = sessionBeanLookUp.assessmentSessionBean;
        this.domainSessionBean = sessionBeanLookUp.domainSessionBean;
        this.caretakerAssessmentSessionBean = sessionBeanLookUp.caretakerAssessmentSessionBean;
    }

    private AssessorEntity validateAssessor(HttpHeaders headers) throws UserNotFoundException {
        try {

            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAssessorSystem(headerToken);
        } catch (NullPointerException | LoginTokenNotFoundException ex) {
            throw new UserNotFoundException();
        }
    }
//error here!!!!

    @Path("assessments/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssessmentsByClientId(@Context HttpHeaders headers, @PathParam("id") Long clientId) {

        System.out.println("********** AssessmentResource.retrieveAssessmentsByClientId(): Get assessments by client id " + clientId + " request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            ClientEntity client = this.clientSessionBean.retrieveClientByUniqueId(clientId);

            client.setAssessor(null);
            client.getCaretakers().clear();

            // retrieve all assessments
            List<AssessmentEntity> assessments = client.getAssessment();
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

                assessment.setClient(null);

//                assessment.getResponse().clear();
                List<ResponseEntity> responses = assessment.getResponse();
                for (ResponseEntity response : responses) {
                    QuestionEntity question = response.getQuestion();
                    question.getQuestionDescription().clear();
                    question.getQuestionToConsider().clear();
                    question.getRatingsDefinition().clear();
                    if (question instanceof MainQuestionEntity) {
                        ((MainQuestionEntity) question).setAgeGroup(null);
                        ((MainQuestionEntity) question).setSubModule(null);
                    } else if (question instanceof SubQuestionEntity) {
                        ((SubQuestionEntity) question).setSubmodule(null);
                    }
                }
            }

            System.out.println("********** AssessmentResource.retrieveAssessmentsByClientId(): Finished and returned " + assessments.size() + " assessments **********");
            GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessments) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (ClientNotFoundException ex) {
            System.out.println("********** AssessmentResource.retrieveAssessmentsByClientId(): Error -> ClientID " + clientId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Client does not exist")).build();
        }
    }

    @Path("assessmentsId/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssessmentsIdByClientId(@Context HttpHeaders headers, @PathParam("id") Long clientId) {

        System.out.println("********** AssessmentResource.retrieveAssessmentsIdByClientId(): Get assessments id by client id " + clientId + " request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            ClientEntity client = this.clientSessionBean.retrieveClientByUniqueId(clientId);

            client.setAssessor(null);
            client.getCaretakers().clear();

            // retrieve all assessments
            List<AssessmentEntity> assessments = client.getAssessment();
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

                assessment.setClient(null);

                assessment.getResponse().clear();
            }

            System.out.println("********** AssessmentResource.retrieveAssessmentsIdByClientId(): Finished and returned " + assessments.size() + " assessments **********");
            GenericEntity<List<AssessmentEntity>> genericAssessments = new GenericEntity<List<AssessmentEntity>>(assessments) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (ClientNotFoundException ex) {
            System.out.println("********** AssessmentResource.retrieveAssessmentsIdByClientId(): Error -> ClientID " + clientId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Client does not exist")).build();
        }
    }

    @Path("caretakerAssessments/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCaretakerAssessmentsByAssessmentId(@Context HttpHeaders headers, @PathParam("id") Long assessmentId) {

        System.out.println("********** AssessmentResource.retrieveCaretakerAssessmentsByAssessmentId(): Get caretaker assessments by assessment id " + assessmentId + " request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
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
            assessment.getResponse().clear();
            assessment.setClient(null);

            List<CaretakerAssessmentEntity> caretakerAssessments = assessment.getCaretakerAssessments();

            for (CaretakerAssessmentEntity caretakerAssessment : caretakerAssessments) {
                caretakerAssessment.setAssessment(null);
//                caretakerAssessment.setCaretaker(null);
                CaretakerEntity caretaker = caretakerAssessment.getCaretaker();
                caretaker.setAccommodationStatus(null);
                caretaker.setAccommodationType(null);
                caretaker.setAddress(null);
                caretaker.getCaretakerAssessments().clear();
                caretaker.setClient(null);

                List<ResponseEntity> responses = caretakerAssessment.getCaretakerResponses();
                for (ResponseEntity response : responses) {
                    QuestionEntity question = response.getQuestion();
                    question.getQuestionDescription().clear();
                    question.getQuestionToConsider().clear();
                    question.getRatingsDefinition().clear();
                    if (question instanceof MainQuestionEntity) {
                        ((MainQuestionEntity) question).setAgeGroup(null);
                        ((MainQuestionEntity) question).setSubModule(null);
                    } else if (question instanceof SubQuestionEntity) {
                        ((SubQuestionEntity) question).setSubmodule(null);
                    }
                }
            }

            System.out.println("********** AssessmentResource.retrieveCaretakerAssessmentsByAssessmentId(): Finished and returned " + caretakerAssessments.size() + " caretaker assessments **********");
            GenericEntity<List<CaretakerAssessmentEntity>> genericAssessments = new GenericEntity<List<CaretakerAssessmentEntity>>(caretakerAssessments) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.retrieveCaretakerAssessmentsByAssessmentId(): Error -> Assessment ID " + assessmentId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessment does not exist")).build();
        }
    }

    @Path("assessment/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssessmentByAssessmentId(@Context HttpHeaders headers, @PathParam("id") Long assessmentId) {

        System.out.println("********** AssessmentResource.retrieveAssessmentByAssessmentId(): Get assessment by assessment id " + assessmentId + " request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
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
            assessment.setClient(null);

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

            List<CaretakerAssessmentEntity> caretakerAssessments = assessment.getCaretakerAssessments();

            for (CaretakerAssessmentEntity caretakerAssessment : caretakerAssessments) {
                caretakerAssessment.setAssessment(null);
                caretakerAssessment.setCaretaker(null);
                caretakerAssessment.getCaretakerResponses().clear();
            }

            System.out.println("********** AssessmentResource.retrieveAssessmentByAssessmentId(): Finished and returned assessment " + assessmentId + " **********");
            GenericEntity<AssessmentEntity> genericAssessments = new GenericEntity<AssessmentEntity>(assessment) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.retrieveAssessmentByAssessmentId(): Error -> Assessment ID " + assessmentId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessment does not exist")).build();
        }
    }

    @Path("caretakerAssessment/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCaretakerAssessmentByCaretakerAssessmentId(@Context HttpHeaders headers, @PathParam("id") Long caretakerAssessmentId) {

        System.out.println("********** AssessmentResource.retrieveCaretakerAssessmentByCaretakerAssessmentId(): Get caretaker assessment by caretaker assessment id " + caretakerAssessmentId + " request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            CaretakerAssessmentEntity caretakerAssessment = this.caretakerAssessmentSessionBean.retrieveCaretakerAssessmentById(caretakerAssessmentId);

            caretakerAssessment.getCaretaker().getCaretakerAssessments().clear();
            caretakerAssessment.getCaretaker().setClient(null);

            List<ResponseEntity> responses = caretakerAssessment.getCaretakerResponses();
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

//            caretakerAssessment.setAssessment(null);
            caretakerAssessment.getAssessment().getCaretakerAssessments().clear();
            caretakerAssessment.getAssessment().setClient(null);
            caretakerAssessment.getAssessment().setAssessor(null);
            caretakerAssessment.getAssessment().getResponse().clear();

            System.out.println("********** AssessmentResource.retrieveCaretakerAssessmentByCaretakerAssessmentId(): Finished and returned Caretaker assessment " + caretakerAssessmentId + " **********");
            GenericEntity<CaretakerAssessmentEntity> genericAssessments = new GenericEntity<CaretakerAssessmentEntity>(caretakerAssessment) {
            };

            return Response.status(Response.Status.OK).entity(genericAssessments).build();

        } catch (CaretakerAssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.retrieveCaretakerAssessmentByCaretakerAssessmentId(): Error -> Caretaker Assessment ID " + caretakerAssessmentId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Caretaker Assessment does not exist")).build();
        }
    }

    @Path("questions/{age}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveQuestionsFromClientAge(@Context HttpHeaders headers, @PathParam("age") int age) {

        System.out.println("********** AssessmentResource.retrieveQuestionsFromClientAge(): Get questions by client age " + age + " request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        //get questions from domain
        List<DomainEntity> domains = domainSessionBean.retrieveAllClientDomains();
        for (DomainEntity domain : domains) {
            List<AgeGroupEntity> ageGroups = domain.getAgeGroups();
            List<AgeGroupEntity> relevantAgeGroups = new ArrayList<>();
            for (AgeGroupEntity ageGroup : ageGroups) {
                if (domain.isModule() == true) {
                    ageGroup.setDomain(null);
                    relevantAgeGroups.add(ageGroup);
                } else {
                    ageGroup.setDomain(null);
                    int lower_bound;
                    int higher_bound;
                    if (ageGroup.getAgeRange().contains("+")) {
                        String ageGap = ageGroup.getAgeRange().substring(0, ageGroup.getAgeRange().length() - 1);
                        lower_bound = Integer.parseInt(ageGap);
                        higher_bound = 20;
                    } else {
                        String[] ageGap = ageGroup.getAgeRange().split("-");
                        lower_bound = Integer.parseInt(ageGap[0]);
                        higher_bound = Integer.parseInt(ageGap[1]);
                    }
                    if (age <= higher_bound && age >= lower_bound) {
                        relevantAgeGroups.add(ageGroup);
                    }
                }
            }
            domain.setAgeGroups(relevantAgeGroups);
            for (AgeGroupEntity ageGroup : domain.getAgeGroups()) {
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

        System.out.println("********** AssessmentResource.retrieveQuestionsFromClientAge(): Finished and returned " + domains.size() + " domains **********");
        GenericEntity<List<DomainEntity>> genericQuestions = new GenericEntity<List<DomainEntity>>(domains) {
        };

        return Response.status(Response.Status.OK).entity(genericQuestions).build();

    }

    @Path("questions/caretaker")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveQuestionsForCaretaker(@Context HttpHeaders headers) {

        System.out.println("********** AssessmentResource.retrieveQuestionsForCaretaker(): Get caretaker questions " + "request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        //get questions from domain
        List<DomainEntity> domains = domainSessionBean.retrieveAllCaretakerDomains();
        for (DomainEntity domain : domains) {
            List<AgeGroupEntity> ageGroups = domain.getAgeGroups();
            for (AgeGroupEntity ageGroup : ageGroups) {
                ageGroup.setDomain(null);
            }
            for (AgeGroupEntity ageGroup : ageGroups) {
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

        System.out.println("********** AssessmentResource.retrieveQuestionsForCaretaker(): Finished and returned " + domains.size() + " domains **********");
        GenericEntity<List<DomainEntity>> genericQuestions = new GenericEntity<List<DomainEntity>>(domains) {
        };

        return Response.status(Response.Status.OK).entity(genericQuestions).build();

    }

    @Path("assessment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAssessment(@Context HttpHeaders headers, AssessmentDetailBody body) {

        System.out.println("********** AssessmentResource.createNewAssessment(): Create new assessment request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            Long assessmentId = assessmentSessionBean.getNextAssessmentUniqueId();
            AssessmentReasonEnum reason = AssessmentReasonEnum.valueOf(body.assessment_reason);

            AssessmentEntity assessment = new AssessmentEntity(assessmentId, new Date(), AssessmentStatusEnum.ASSIGNED, reason, null, -1);
            assessmentSessionBean.createNewAssessment(assessment, body.client_id, body.assessor_id);
            
            //call update responses to generate -2 responses
            int age = clientSessionBean.retrieveClientByUniqueId(body.client_id).getAge();
            List<String> questionCodes = new ArrayList<>();
            List<DomainEntity> domains = domainSessionBean.retrieveAllClientDomains();
            for (DomainEntity domain : domains) {
                List<AgeGroupEntity> ageGroups = domain.getAgeGroups();
                List<AgeGroupEntity> relevantAgeGroups = new ArrayList<>();
                for (AgeGroupEntity ageGroup : ageGroups) {
                    if (domain.isModule() == true) {
                        relevantAgeGroups.add(ageGroup);
                    } else {
                        int lower_bound;
                        int higher_bound;
                        if (ageGroup.getAgeRange().contains("+")) {
                            String ageGap = ageGroup.getAgeRange().substring(0, ageGroup.getAgeRange().length() - 1);
                            lower_bound = Integer.parseInt(ageGap);
                            higher_bound = 20;
                        } else {
                            String[] ageGap = ageGroup.getAgeRange().split("-");
                            lower_bound = Integer.parseInt(ageGap[0]);
                            higher_bound = Integer.parseInt(ageGap[1]);
                        }
                        if (age <= higher_bound && age >= lower_bound) {
                            relevantAgeGroups.add(ageGroup);
                        }
                    }
                }
                domain.setAgeGroups(relevantAgeGroups);
                for (AgeGroupEntity ageGroup : domain.getAgeGroups()) {
                    List<MainQuestionEntity> mainQ = ageGroup.getQuestions();
                    for (MainQuestionEntity question : mainQ) {
                        questionCodes.add(question.getQuestionCode());
                        SubModuleEntity subModule = question.getSubModule();
                        if (question.getSubModule() != null) {
                            subModule.getQues().clear();
                            List<SubQuestionEntity> subQuestions = subModule.getSubQues();
                            for (SubQuestionEntity subQuestion : subQuestions) {
                                questionCodes.add(subQuestion.getQuestionCode());
                            }
                        }
                    }
                }
            }
            
            List<String> notes = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            for (String qnCode : questionCodes) {
                notes.add("");
                values.add(-2);
            }
            
            assessmentSessionBean.updateAssessmentResponses(assessmentId, questionCodes, values, notes);
            
            assessment.getAssessor().getAssessments().clear();
            assessment.getAssessor().setPassword(null);
            assessment.getAssessor().setSalt(null);
            assessment.getAssessor().setOrganisation(null);
            assessment.getAssessor().getClients().clear();
            assessment.getAssessor().setSupervisor(null);
            assessment.getAssessor().getSupervisee().clear();
            assessment.getAssessor().getCertificates().clear();

            assessment.getClient().getAssessment().clear();
            assessment.getClient().setAccommodationStatus(null);
            assessment.getClient().setAccommodationType(null);
            assessment.getClient().setAddress(null);
            assessment.getClient().setAdmissionType(null);
            assessment.getClient().setAssessor(null);
            assessment.getClient().getCaretakers().clear();
            assessment.getClient().setCurrentOccupation(null);
            assessment.getClient().setEducationLevel(null);
            assessment.getClient().setEthnicity(null);
            assessment.getClient().setGender(null);
            assessment.getClient().setIdNumber(null);
            assessment.getClient().setMonthlyIncome(0);

            System.out.println("********** AssessmentResource.createNewAssessment(): Assessment ID " + assessment.getAssessmentId() + " created successfully with " + questionCodes.size() + " empty responses created**********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentResource.createNewAssessment(): Assessor of ID " + body.assessor_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor ID " + body.assessor_id + " does not exist.")).build();
        } catch (ClientNotFoundException ex) {
            System.out.println("********** AssessmentResource.createNewAssessment(): Client of ID " + body.client_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Client ID " + body.client_id + " does not exist.")).build();
        } catch (AssessmentExistsException ex) {
            System.out.println("********** AssessmentResource.createNewAssessment(): Assessment Unique ID already exists, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment Unique ID already exists, unexpected error encountered")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** AssessmentResource.createNewAssessment(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** AssessmentResource.createNewAssessment(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (AssessmentNotFoundException | QuestionNotFoundException | ResponseNotFoundException | ResponseExistsException ex) {
            System.out.println("********** AssessmentResource.createNewAssessment(): Response generation error, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Response generation error, unexpected error encountered")).build();
        }
    }
    
    @Path("assessment/redoAssessment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response redoAssessment(@Context HttpHeaders headers, RedoAssessmentDetailBody body) {

        System.out.println("********** AssessmentResource.redoAssessment(): Redo assessment request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            AssessmentEntity prevAssessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);
            //create the new assessment
            Long assessmentId = assessmentSessionBean.getNextAssessmentUniqueId();
            AssessmentReasonEnum reason = prevAssessment.getReason();
            Long clientId = prevAssessment.getClient().getClientUniqueId();
            Long assessorId = prevAssessment.getAssessor().getAssessorId();
            AssessmentEntity assessment = new AssessmentEntity(assessmentId, new Date(), AssessmentStatusEnum.ASSIGNED, reason, null, -1);
            assessmentSessionBean.createNewAssessment(assessment, clientId, assessorId);
            
            //get the responses out of the prev assessment
            
            List<String> questionCodes = new ArrayList<>();
            List<String> notes = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            List<ResponseEntity> prevResponses = prevAssessment.getResponse();
            for (ResponseEntity response : prevResponses) {
                questionCodes.add(response.getQuestion().getQuestionCode());
                notes.add(response.getResponseNotes());
                values.add(response.getResponseValue());
            }
            
            assessmentSessionBean.updateAssessmentResponses(assessmentId, questionCodes, values, notes);
            
            //get the caretaker responses out of the prev assessment
            
            List<CaretakerAssessmentEntity> caretakerAssessments = prevAssessment.getCaretakerAssessments();
            for (CaretakerAssessmentEntity caretakerAssessment : caretakerAssessments) {
                CaretakerTypeEnum type = caretakerAssessment.getCaretakerType();
                CaretakerAssessmentEntity newCaretakerAssessment = new CaretakerAssessmentEntity(caretakerAssessment.getLevelOfNeeds(), type);
                Long caretakerAssessmentId = caretakerAssessmentSessionBean.createNewAssessment(newCaretakerAssessment, caretakerAssessment.getCaretaker().getCaretakerUniqueId(), assessmentId);

                
                questionCodes = new ArrayList<>();
                notes = new ArrayList<>();
                values = new ArrayList<>();
                prevResponses = caretakerAssessment.getCaretakerResponses();
                for (ResponseEntity response : prevResponses) {
                    questionCodes.add(response.getQuestion().getQuestionCode());
                    notes.add(response.getResponseNotes());
                    values.add(response.getResponseValue());
                }

                caretakerAssessmentSessionBean.updateCaretakerAssessmentResponses(caretakerAssessmentId, questionCodes, values, notes);

                newCaretakerAssessment.setCaretaker(null);
                newCaretakerAssessment.setAssessment(null);
                newCaretakerAssessment.getCaretakerResponses().clear();
            }
            
            
            
            assessment.getAssessor().getAssessments().clear();
            assessment.getAssessor().setPassword(null);
            assessment.getAssessor().setSalt(null);
            assessment.getAssessor().setOrganisation(null);
            assessment.getAssessor().getClients().clear();
            assessment.getAssessor().setSupervisor(null);
            assessment.getAssessor().getSupervisee().clear();
            assessment.getAssessor().getCertificates().clear();

            assessment.getClient().getAssessment().clear();
            assessment.getClient().setAccommodationStatus(null);
            assessment.getClient().setAccommodationType(null);
            assessment.getClient().setAddress(null);
            assessment.getClient().setAdmissionType(null);
            assessment.getClient().setAssessor(null);
            assessment.getClient().getCaretakers().clear();
            assessment.getClient().setCurrentOccupation(null);
            assessment.getClient().setEducationLevel(null);
            assessment.getClient().setEthnicity(null);
            assessment.getClient().setGender(null);
            assessment.getClient().setIdNumber(null);
            assessment.getClient().setMonthlyIncome(0);
            
            assessment.getCaretakerAssessments().clear();

            System.out.println("********** AssessmentResource.redoAssessment(): Assessment ID " + assessment.getAssessmentId() + " recreated successfully**********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Assessor does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor ID does not exist.")).build();
        } catch (ClientNotFoundException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Client does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Client does not exist.")).build();
        } catch (AssessmentExistsException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Assessment Unique ID already exists, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment Unique ID already exists, unexpected error encountered")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (AssessmentNotFoundException | QuestionNotFoundException | ResponseNotFoundException | ResponseExistsException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Response generation error, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Response generation error, unexpected error encountered")).build();
        } catch (CaretakerAssessmentExistsException | CaretakerAssessmentNotFoundException | CaretakerNotFoundException ex) {
            System.out.println("********** AssessmentResource.redoAssessment(): Caretaker assessment creation error, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caretaker assessment creation error, unexpected error encountered")).build();
        }
    }

    @Path("assessment/responses")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitResponsesForPage(@Context HttpHeaders headers, ResponsesDetailBody body) {
        System.out.println("********** AssessmentResource.submitResponsesForPage(): Submit responses request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            AssessmentEntity assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);
            int responsesBefore = assessment.getResponse().size();
            System.out.println("Response size before update: " + responsesBefore);
            assessmentSessionBean.updateAssessmentResponses(assessment.getAssessmentUniqueId(), body.question_codes, body.response_values, body.response_notes);
            int responsesAfter = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id).getResponse().size();
            System.out.println("Response size after update: " + responsesAfter);

            //Clear Associations to send back
            assessment.setAssessor(null);
            assessment.setClient(null);

            assessment.getCaretakerAssessments().clear();
            assessment.getResponse().clear();

            int responsesAdded = responsesAfter - responsesBefore;
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Assessment ID " + assessment.getAssessmentUniqueId() + " updated successfully with " + responsesAdded + " added **********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Assessment of ID " + body.assessment_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment ID " + body.assessment_id + " does not exist.")).build();
        } catch (ResponseNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Responses queried does not exist, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Responses queried does not exist, unexpected error encountered.")).build();
        } catch (QuestionNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Question codes provided does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Question codes provided does not exist.")).build();
        } catch (ResponseExistsException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Tried to create a response that exists, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Tried to create a response that exists, unexpected error encountered")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        }
    }

    @Path("assessment/submitAssessment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitAssessment(@Context HttpHeaders headers, AssessmentSubmitDetailBody body) {
        System.out.println("********** AssessmentResource.submitAssessment(): Submit assessment request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            AssessmentEntity assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);
//            List<String> unansweredQuestions = assessmentSessionBean.checkUnansweredQuestions(body.assessment_id);
//            System.out.println(unansweredQuestions.size());
//            if (unansweredQuestions.size() > 0) {
//                System.out.println("********** AssessmentResource.submitAssessment(): Unanswered questions of size " + unansweredQuestions.size() + " found **********");
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(unansweredQuestions).build();
//            }
            assessmentSessionBean.submitAssessment(body.assessment_id);
            assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(body.assessment_id);

            assessment.setAssessor(null);
            assessment.setClient(null);

            assessment.getCaretakerAssessments().clear();
            assessment.getResponse().clear();

            System.out.println("********** AssessmentResource.submitAssessment(): Assessment ID " + assessment.getAssessmentUniqueId() + " submitted successfullly. **********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitAssessment(): Assessment of ID " + body.assessment_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment ID " + body.assessment_id + " does not exist.")).build();
        } catch (AssessmentStatusUpdateException ex) {
            System.out.println("********** AssessmentResource.submitAssessment(): Submission error, unanswered responses in assessment or caregiver assessment **********");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponse("Submission error, unanswered responses in assessment or caregiver assessment.")).build();        
        } catch (CaretakerAssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitAssessment(): Caretaker Assessment not found, Unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caretaker Assessment not found, Unexpected error encountered.")).build();
        }
    }
    
    @Path("assessment/submitAssessment/checkAssessmentResponses/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAssessmentResponses(@Context HttpHeaders headers, @PathParam("id") Long assessmentId) {
        System.out.println("********** AssessmentResource.checkAssessmentResponses(): Check assessment response for " + assessmentId + " request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentResource.checkAssessmentResponses(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            List<String> unansweredQuestions = assessmentSessionBean.checkUnansweredQuestions(assessmentId);
            
            System.out.println("********** AssessmentResource.checkAssessmentResponses(): Unanswered questions of size " + unansweredQuestions.size() + " found **********");
            return Response.status(Response.Status.OK).entity(unansweredQuestions).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.checkAssessmentResponses(): Assessment of ID " + assessmentId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment ID " + assessmentId + " does not exist.")).build();
        }
    }
    
    @Path("assessment/submitAssessment/checkCaretakerAssessmentResponses/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkCaretakerAssessmentResponses(@Context HttpHeaders headers, @PathParam("id") Long assessmentId) {
        System.out.println("********** AssessmentResource.checkCaretakerAssessmentResponses(): Check caregiver assessment response for " + assessmentId + " received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentResource.checkCaretakerAssessmentResponses(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            AssessmentEntity assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(assessmentId);
            ClientEntity client = assessment.getClient();
            List<CaretakerEntity> caretakers = client.getCaretakers();
            List<CaretakerAssessmentEntity> caretakerAssessments = assessment.getCaretakerAssessments();
            List<CaregiverAssessmentCheckResponse> response = new ArrayList<>();
            for (CaretakerEntity caretaker : caretakers) {
                boolean isCreated = false;
                Long currentCaretakerAssessmentId = -1l;
                for (CaretakerAssessmentEntity caretakerAssessment : caretakerAssessments) {
                    Long caretakerAssessmentCaretakerId = caretakerAssessment.getCaretaker().getCaretakerUniqueId();
                    if (caretakerAssessmentCaretakerId.equals(caretaker.getCaretakerId())) {
                        isCreated = true;
                        currentCaretakerAssessmentId = caretakerAssessment.getCaretakerAssessmentId();
                        break;
                    }
                }
                if(isCreated) {
                    Long id = caretaker.getCaretakerUniqueId();
                    String name = caretaker.getName();
                    List<String> questionCodes = caretakerAssessmentSessionBean.checkUnansweredQuestions(currentCaretakerAssessmentId);
                    System.out.println(questionCodes.size());
                    response.add(new CaregiverAssessmentCheckResponse(id, name, isCreated, questionCodes));
                } else {
                    Long id = caretaker.getCaretakerUniqueId();
                    String name = caretaker.getName();
                    List<String> questionCodes = new ArrayList<>();
                    response.add(new CaregiverAssessmentCheckResponse(id, name, isCreated, questionCodes));
                }
            }
            GenericEntity<List<CaregiverAssessmentCheckResponse>> genericChecks = new GenericEntity<List<CaregiverAssessmentCheckResponse>>(response) {
            };
            
            System.out.println("********** AssessmentResource.checkCaretakerAssessmentResponses(): Checked through " + response.size() + " caregivers **********");
            return Response.status(Response.Status.OK).entity(genericChecks).build();
        } catch (CaretakerAssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.checkCaretakerAssessmentResponses(): Caretaker Assessment of ID " + "-1" + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caretaker Assessment ID " + "-1" + " does not exist.")).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.checkCaretakerAssessmentResponses(): Assessment of ID " + assessmentId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment ID " + assessmentId + " does not exist.")).build();
        }
    }

    @Path("assessment-types")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAssessmentTypes(@Context HttpHeaders headers) {
        System.out.println("********** AssessmentResource.getAllAssessmentTypes(): Get all assessment types request received **********");
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
        System.out.println("********** AssessmentResource.getAllAssessmentTypes(): Finished **********");
        return Response.status(Response.Status.OK).entity(new AssessmentNameResponse(results)).build();
    }

    @Path("assessmentbyassessor/{id}")
    @GET
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssessementByAssessorId(@Context HttpHeaders headers, @PathParam("id") Long id) {
        System.out.println("********** AssessmentResource.getAssessementByAssessorId(): Get all assessment request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentResource.getAssessementByAssessorId(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<AssessmentEntity> allAss = assessmentSessionBean.retrieveAllAssessmentByAssessorId(id);
        for (AssessmentEntity assessment : allAss) {
            assessment.getAssessor().getAssessments().clear();
            assessment.getAssessor().setPassword(null);
            assessment.getAssessor().setSalt(null);
            assessment.getAssessor().setOrganisation(null);
            assessment.getAssessor().getClients().clear();
            assessment.getAssessor().setSupervisor(null);
            assessment.getAssessor().getSupervisee().clear();
            assessment.getAssessor().getCertificates().clear();

            assessment.getClient().getAssessment().clear();
            assessment.getClient().setAccommodationStatus(null);
            assessment.getClient().setAccommodationType(null);
            assessment.getClient().setAddress(null);
            assessment.getClient().setAdmissionType(null);
            assessment.getClient().setAssessor(null);
            assessment.getClient().getCaretakers().clear();
            assessment.getClient().setCurrentOccupation(null);
            assessment.getClient().setEducationLevel(null);
            assessment.getClient().setEthnicity(null);
            assessment.getClient().setGender(null);
            assessment.getClient().setIdNumber(null);
            assessment.getClient().setMonthlyIncome(0);
        }
        System.out.println("********** AssessmentResource.getAssessementByAssessorId(): Finished **********");
        GenericEntity<List<AssessmentEntity>> genericQuestions = new GenericEntity<List<AssessmentEntity>>(allAss) {
        };

        return Response.status(Response.Status.OK).entity(genericQuestions).build();

    }

    @Path("caretaker-types")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCaretakerTypes(@Context HttpHeaders headers) {
        System.out.println("********** AssessmentResource.getAllCaretakerTypes(): Get all caretaker types request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        List<String> results = new ArrayList<>(
                (Arrays.asList("LONG_TERM_IDENTIFIED_CAREGIVER", "CURRENT_CAREGIVER")));
        System.out.println("********** AssessmentResource.getAllCaretakerTypes(): Finished **********");
        return Response.status(Response.Status.OK).entity(new CaregiverTypeResponse(results)).build();
    }

    @Path("caretaker-algorithms")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCaretakerAlgorithms(@Context HttpHeaders headers) {
        System.out.println("********** AssessmentResource.getAllCaretakerAlgorithms(): Get all caretaker algorithm types request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<String> results = new ArrayList<>(
                (Arrays.asList("LOW_NEEDS",
                        "LOW_NEEDS_WITH_RED_FLAGS",
                        "HIGH_NEEDS",
                        "HIGH_NEEDS_WITH_RED_FLAGS")));
        System.out.println("********** AssessmentResource.getAllCaretakerAlgorithms(): Finished **********");
        return Response.status(Response.Status.OK).entity(new CaregiverAlgorithmResponse(results)).build();
    }

    @Path("caretakerAssessment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    public Response createNewAssessment(@Context HttpHeaders headers, @QueryParam("client_id") Long clientId, @QueryParam("assessor_id") Long assesorId, @QueryParam("assessment_reason") String assessmentReason){
    public Response createNewCaretakerAssessment(@Context HttpHeaders headers, CaregiverAssessmentDetailBody body) {

        System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Create new caretaker assessment request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            CaretakerTypeEnum type = CaretakerTypeEnum.valueOf(body.caretaker_type);

            CaretakerAssessmentEntity caretakerAssessment = new CaretakerAssessmentEntity(CaretakerAlgorithmEnum.LOW_NEEDS, type);
            Long caretakerAssessmentId = caretakerAssessmentSessionBean.createNewAssessment(caretakerAssessment, body.caretaker_id, body.assessment_id);
            
            List<DomainEntity> caretakerDomains = domainSessionBean.retrieveAllCaretakerDomains();
            List<String> questionCodes = new ArrayList<>();
            
            for (DomainEntity domain : caretakerDomains) {
                for (AgeGroupEntity ageGroup : domain.getAgeGroups()) {
                    for (MainQuestionEntity mainQuestions : ageGroup.getQuestions()) {
                        questionCodes.add(mainQuestions.getQuestionCode());
                    }
                }
            }
            
            List<String> notes = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            for (String qnCode : questionCodes) {
                notes.add("");
                values.add(-2);
            }          
            
            caretakerAssessmentSessionBean.updateCaretakerAssessmentResponses(caretakerAssessmentId, questionCodes, values, notes);

            caretakerAssessment.setCaretaker(null);
            caretakerAssessment.setAssessment(null);
            caretakerAssessment.getCaretakerResponses().clear();

            System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Assessment ID " + caretakerAssessment.getCaretakerAssessmentId() + " created successfully  with " + questionCodes.size() + " empty responses created **********");
            return Response.status(Response.Status.OK).entity(caretakerAssessment).build();
        } catch (AssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Assessment of ID " + body.assessment_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessment ID " + body.assessment_id + " does not exist.")).build();
        } catch (CaretakerNotFoundException ex) {
            System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Caretaker of ID " + body.caretaker_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caretaker ID " + body.caretaker_id + " does not exist.")).build();
        } catch (CaretakerAssessmentExistsException ex) {
            System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Caretaker Assessment ID already exists, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caretaker Assessment ID already exists, unexpected error encountered")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (CaretakerAssessmentNotFoundException | QuestionNotFoundException | ResponseExistsException | ResponseNotFoundException ex) {
            System.out.println("********** AssessmentResource.createNewCaretakerAssessment(): Response Creation error, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Response Creation error, unexpected error encountered.")).build();
        }
    }

    @Path("caretakerAssessment/responses")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitCaretakerResponsesForPage(@Context HttpHeaders headers, CaregiverResponsesDetailBody body) {
        System.out.println("********** AssessmentResource.submitResponsesForPage(): Submit responses request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorVisualisationResource.getAgePieChart(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            CaretakerAssessmentEntity assessment = caretakerAssessmentSessionBean.retrieveCaretakerAssessmentById(body.caretaker_assessment_id);
            int responsesBefore = assessment.getCaretakerResponses().size();
            System.out.println("Response size before update: " + responsesBefore);
            caretakerAssessmentSessionBean.updateCaretakerAssessmentResponses(assessment.getCaretakerAssessmentId(), body.question_codes, body.response_values, body.response_notes);
            int responsesAfter = caretakerAssessmentSessionBean.retrieveCaretakerAssessmentById(body.caretaker_assessment_id).getCaretakerResponses().size();
            System.out.println("Response size after update: " + responsesAfter);

            //Clear Associations to send back
            assessment.setAssessment(null);
            assessment.setCaretaker(null);

            assessment.getCaretakerResponses().clear();

            int responsesAdded = responsesAfter - responsesBefore;
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Caretaker Assessment ID " + assessment.getCaretakerAssessmentId() + " updated successfully with " + responsesAdded + " added **********");
            return Response.status(Response.Status.OK).entity(assessment).build();
        } catch (CaretakerAssessmentNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Caretaker Assessment of ID " + body.caretaker_assessment_id + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caretaker Assessment ID " + body.caretaker_assessment_id + " does not exist.")).build();
        } catch (ResponseNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Responses queried does not exist, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Responses queried does not exist, unexpected error encountered.")).build();
        } catch (QuestionNotFoundException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Question codes provided does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Question codes provided does not exist.")).build();
        } catch (ResponseExistsException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Tried to create a response that exists, unexpected error encountered **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Tried to create a response that exists, unexpected error encountered")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** AssessmentResource.submitResponsesForPage(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        }
    }

    @Path("/caretakers/{id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCaretakersFromClientId(@Context HttpHeaders headers, @PathParam("id") Long clientId) {
        System.out.println("********** AssessmentResource.getCaretakersFromClientId(): Get caretakers by client id " + clientId + " request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessmentResource.getCaretakersFromClientId(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            ClientEntity client = this.clientSessionBean.retrieveClientByUniqueId(clientId);

            client.setAssessor(null);
            client.getAssessment().clear();
            List<CaretakerEntity> caretakers = client.getCaretakers();

            for (CaretakerEntity caretaker : caretakers) {
                caretaker.setClient(null);
                caretaker.getCaretakerAssessments().clear();
            }

            System.out.println("********** AssessmentResource.getCaretakersFromClientId(): Finished and returned " + caretakers.size() + " caretakers **********");
            GenericEntity<List<CaretakerEntity>> genericCaretakers = new GenericEntity<List<CaretakerEntity>>(caretakers) {
            };

            return Response.status(Response.Status.OK).entity(genericCaretakers).build();

        } catch (ClientNotFoundException ex) {
            System.out.println("********** AssessmentResource.getCaretakersFromClientId(): Error -> ClientID " + clientId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Client does not exist")).build();
        }
    }
}

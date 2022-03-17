/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.PasswordChangeRequestSessionBeanLocal;
import entity.AdminUserEntity;
import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CertificationEntity;
import entity.ClientEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.AssessorDeletionError;
import util.exception.AssessorExistsException;
import util.exception.AssociationException;
import util.exception.InputDataValidationException;
import util.exception.LoginTokenNotFoundException;
import util.exception.OrganisationNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssessorDetailBody;
import ws.datamodel.ErrorResponse;
import ws.datamodel.MessageResponse;

/**
 * REST Web Service
 *
 * @author Ooi Jun Hao
 * @author Ong Bik Jeun
 */
@Path("AssessorManagement-AssessorSystem")
public class AssessorManagementAssessorSystemResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final AssessorSessionBeanLocal assessorSessionBean;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;

    public AssessorManagementAssessorSystemResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        assessorSessionBean = sessionBeanLookUp.assessorSessionBean;
        loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
    }

    private AssessorEntity validateAssessor(HttpHeaders headers) throws UserNotFoundException {
        try {

            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAssessorSystem(headerToken);
        } catch (NullPointerException | LoginTokenNotFoundException ex) {
            throw new UserNotFoundException();
        }
    }

    @Path("assessors")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssessorsByOrganisation(@Context HttpHeaders headers) {
        System.out.println("AssessorManagemnetAssessorSystemResource.getAssessorsByOrganisation() : Get Assessor By Organisation");

        AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<AssessorEntity> assInOrg = assessorSessionBean.retrieveAllAssessorsByOrganisation(orgAdmin.getOrganisation().getOrganisationId());
        for (AssessorEntity ass : assInOrg) {
            ass.getAssessments().clear();
            ass.getCertificates().clear();
            ass.getClients().clear();
//            ass.setOrganisation(null);
            ass.getOrganisation().getAssessors().clear();
            ass.getSupervisee().clear();
            ass.setSupervisor(null);
        }

        System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Finished and returned " + assInOrg.size() + " assessors **********");
        GenericEntity<List<AssessorEntity>> genericClients = new GenericEntity<List<AssessorEntity>>(assInOrg) {
        };
        return Response.status(Response.Status.OK).entity(genericClients).build();
    }

    @Path("assessors/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssessorById(@Context HttpHeaders headers, @PathParam("id") Long assessorId) {

        System.out.println("********* AssessorManagemnetAssessorSystemResource.getAssessorById() : Get assessor ********");

        AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.getAssessorById(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            AssessorEntity ass = assessorSessionBean.retrieveUserById(assessorId);
            ass.getAssessments().clear();
            ass.getCertificates().clear();
            ass.getClients().clear();
            ass.setOrganisation(null);
             if (ass.getSupervisor() != null) {
                ass.getSupervisor().getAssessments().clear();
                ass.getSupervisor().getCertificates().clear();
                ass.getSupervisor().setOrganisation(null);
                ass.getSupervisor().getClients().clear();
                ass.getSupervisor().setPassword(null);
                ass.getSupervisor().setSalt(null);
                ass.getSupervisor().getSupervisee().clear();
                ass.getSupervisor().setSupervisor(null);
            }
            for (AssessorEntity supervisee : ass.getSupervisee()) {
                supervisee.getAssessments().clear();
                supervisee.getCertificates().clear();
                supervisee.setOrganisation(null);
                supervisee.getClients().clear();
                supervisee.setPassword(null);
                supervisee.setSalt(null);
                supervisee.getSupervisee().clear();
                supervisee.setSupervisor(null);
            }
            return Response.status(Response.Status.OK).entity(ass).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.getAssessorById(): Assessor ID " + assessorId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor ID " + assessorId + " does not exist.")).build();
        }
    }

    @Path("assessors")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAssessorForOrganisation(@Context HttpHeaders headers, AssessorDetailBody body) {

        System.out.println("********* AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation() : Create and Associate new assessor ********");

        AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            AssessorEntity ass = new AssessorEntity(body.email, body.name, false);
            AssessorEntity new_ass = assessorSessionBean.createNewAssessor(ass, body.organisation_id);
            body.assessorId = new_ass.getAssessorId();

            System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Assessor ID = " + new_ass.getAssessorId() + " created successfully **********");
            return Response.status(Response.Status.OK).entity(body).build();

        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Organisation doesnt exists ********** ");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Organisation ID " + body.organisation_id + " does not exist")).build();
        } catch (AssessorExistsException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Assesor already exists ********** ");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor already exist.")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.createNewAssessorForOrganisation(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        }

    }

    @Path("assessors/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAssessor(@Context HttpHeaders headers, @PathParam("id") Long assessorId) {

        System.out.println("********* AssessorManagemnetAssessorSystemResource.deleteAssessor() : Delete assessor ********");

        AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.deleteAssessor(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            assessorSessionBean.deleteAssessor(assessorId);
            return Response.status(Response.Status.OK).build();
        } catch (AssessorDeletionError ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.deleteAssessor(): Assessor deletion not allowed **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.deleteAssessor(): Assessor ID " + assessorId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor ID " + assessorId + " does not exist.")).build();
        }
    }

    @Path("assessors/{id}/updateActive")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateActive(@Context HttpHeaders headers, @PathParam("id") Long assessorId) {

        System.out.println("********* AssessorManagemnetAssessorSystemResource.updateActive() : Deactivate assessor ********");

        AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.updateActive(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            Boolean result = assessorSessionBean.updateActiveStatus(assessorId);
            return Response.status(Response.Status.OK).entity(new MessageResponse("Assessor is now " + ((result) ? "active" : "inactive"))).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.updateActive(): Assessor ID " + assessorId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor ID " + assessorId + " does not exist.")).build();
        } catch (AssessorDeletionError ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.deleteAssessor(): Assessor deactivation not allowed **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        }

    }

    @Path("assessors/assign")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignSupervisorSupervisee(@Context HttpHeaders headers, @QueryParam("supervisor_id") Long supervisorId, @QueryParam("supervisee_id") Long superviseeId) { // to consume a JSON with the ID of supervisor and supervisee
        System.out.println("********* AssessorManagemnetAssessorSystemResource.assignSupervisorSupervisee() : Assign Supervisor and Supervisee ********");

        AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.assignSupervisorSupervisee(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            assessorSessionBean.assignSupervisorSupervisee(supervisorId, superviseeId);
            return Response.status(Response.Status.OK).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.assignSupervisorSupervisee(): User not found **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("User Not Found")).build();
        } catch (AssociationException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.assignSupervisorSupervisee(): Organisation differs **********");
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse("Both do not belong in the same organisation")).build();
        }

    }
    
    @Path("assessors/{id}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAssessor(@Context HttpHeaders headers, @PathParam("id") long assessorId, AssessorDetailBody assessorDetail) {
        System.out.println("********** AssessorManagemnetAssessorSystemResource.updateAssessor(): Update assessor request received **********");

       AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.assignSupervisorSupervisee(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            this.assessorSessionBean.updateDetails(assessorId, assessorDetail.name, assessorDetail.email, assessorDetail.supervisor_id);
            AssessorEntity assessor = this.assessorSessionBean.retrieveUserById(assessorId);
            assessor.setPassword(null);
            assessor.setSalt(null);
            assessor.getOrganisation().getAssessors().clear();
            for (ClientEntity client : assessor.getClients()) {
                client.getCaretakers().clear();
                client.getAssessment().clear();
                client.setAssessor(null);
            }
            for (AssessmentEntity assessment : assessor.getAssessments()) {
                assessment.getCaretakerAssessments().clear();
                assessment.getResponse().clear();
                assessment.getClient().getCaretakers().clear();
                assessment.getClient().setAssessor(null);
                assessment.getClient().getAssessment().clear();
                assessment.setAssessor(null);
            }
            for (CertificationEntity cert : assessor.getCertificates()) {
                cert.setAssessor(null);
            }
            if (assessor.getSupervisor() != null) {
                assessor.getSupervisor().getAssessments().clear();
                assessor.getSupervisor().getCertificates().clear();
                assessor.getSupervisor().setOrganisation(null);
                assessor.getSupervisor().getClients().clear();
                assessor.getSupervisor().setPassword(null);
                assessor.getSupervisor().setSalt(null);
                assessor.getSupervisor().getSupervisee().clear();
                assessor.getSupervisor().setSupervisor(null);
            }
            for (AssessorEntity supervisee : assessor.getSupervisee()) {
                supervisee.getAssessments().clear();
                supervisee.getCertificates().clear();
                supervisee.setOrganisation(null);
                supervisee.getClients().clear();
                supervisee.setPassword(null);
                supervisee.setSalt(null);
                supervisee.getSupervisee().clear();
                supervisee.setSupervisor(null);
            }

            System.out.println("********** AssessorManagemnetAssessorSystemResource.updateAssessor(): Finished updating and returned assessor ID = " + assessor.getAssessorId() + " **********");
            return Response.status(Response.Status.OK).entity(assessor).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.updateAssessor(): Error -> AssessorID/SupervisorId does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor/Supervisor does not exist")).build();
        }
    }

    @Path("assessors/{id}/supervisee/{superviseeId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSuperviseeFromSupervisor(@Context HttpHeaders headers, @PathParam("id") long assessorId, @PathParam("superviseeId") long superviseeId) {
        System.out.println("********** AssessorManagementAdminSystemResource.removeSuperviseeFromSupervisor(): removeSuperviseeFromSupervisor request received **********");

         AssessorEntity orgAdmin;
        try {
            orgAdmin = this.validateAssessor(headers);
            if (!orgAdmin.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.assignSupervisorSupervisee(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }

        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            this.assessorSessionBean.removeSupervisorSupervisee(assessorId, superviseeId);
            System.out.println("********** AssessorManagementAdminSystemResource.removeSuperviseeFromSupervisor(): Supervisor and supervisee relationship removed successfully **********");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Successfully removed supervisor/supervisee relationship")).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.removeSuperviseeFromSupervisor(): Supervisor/Supervisee not found **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (AssociationException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.removeSuperviseeFromSupervisor(): Provided supervisor and supervisee are inaccurate **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        }
    }
}

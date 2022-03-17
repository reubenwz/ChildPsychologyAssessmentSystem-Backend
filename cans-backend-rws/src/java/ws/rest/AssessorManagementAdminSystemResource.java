/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.OrganisationSessionBeanLocal;
import entity.AdminUserEntity;
import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CertificationEntity;
import entity.ClientEntity;
import entity.OrganisationEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.enumeration.AgencyTypeEnum;
import util.exception.AssessorDeletionError;
import util.exception.AssessorExistsException;
import util.exception.AssociationException;
import util.exception.InputDataValidationException;
import util.exception.LoginTokenNotFoundException;
import util.exception.OrganisationExistsException;
import util.exception.OrganisationNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssessorDetailBody;
import ws.datamodel.ErrorResponse;
import ws.datamodel.MessageResponse;
import ws.datamodel.OrganisationDetailBody;

/**
 * REST Web Service
 *
 * @author Ooi Jun Hao
 */
@Path("AssessorManagement-AdminSystem")
public class AssessorManagementAdminSystemResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final OrganisationSessionBeanLocal organisationSessionBean;
    private final AssessorSessionBeanLocal assessorSessionBean;

    public AssessorManagementAdminSystemResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.organisationSessionBean = sessionBeanLookUp.organisationSessionBean;
        this.assessorSessionBean = sessionBeanLookUp.assessorSessionBean;
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

    @Path("assessors")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssessors(@Context HttpHeaders headers, @QueryParam("organisation_id") long organisationId) {  // should be retrieving by organisation instead of as a whole i think
        System.out.println("********** AssessorManagementAdminSystemResource.retrieveAssessors(): Get assessors request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.retrieveAssessors(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        // retrieve all assessors in org
        List<AssessorEntity> assessors = this.assessorSessionBean.retrieveAllAssessorsByOrganisation(organisationId); // to be replaced
        for (AssessorEntity assessor : assessors) {
            assessor.getAssessments().clear();
            assessor.getCertificates().clear();
            assessor.setOrganisation(null);
            assessor.getClients().clear();
            assessor.setPassword(null);
            assessor.setSalt(null);
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
        }

        System.out.println("********** AssessorManagementAdminSystemResource.retrieveAssessors(): Finished and returned " + assessors.size() + " assessors **********");
        GenericEntity<List<AssessorEntity>> genericAssessors = new GenericEntity<List<AssessorEntity>>(assessors) {
        };
        return Response.status(Response.Status.OK).entity(genericAssessors).build();
    }

    @Path("assessors/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAssessorById(@Context HttpHeaders headers, @PathParam("id") long assessorId) {
        System.out.println("********** AssessorManagementAdminSystemResource.retrieveAssessorById(): Get assessor by Id request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.retrieveAssessorById(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
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

            System.out.println("********** AssessorManagementAdminSystemResource.retrieveAssessorById(): Finished and returned assessor ID = " + assessor.getAssessorId() + " **********");
            return Response.status(Response.Status.OK).entity(assessor).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.retrieveAssessorById(): Error -> AssessorID " + assessorId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor does not exist")).build();
        }
    }

    @Path("assessors")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAssessor(@Context HttpHeaders headers, AssessorDetailBody assessorDetail) {
        System.out.println("********** AssessorManagementAdminSystemResource.createNewAssessor(): Create new assessor request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewAssessor(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        AssessorEntity assessor = new AssessorEntity(assessorDetail.email, assessorDetail.name, false); // a randome password will be generated iin this

        try {
            AssessorEntity new_ass = assessorSessionBean.createNewAssessor(assessor, assessorDetail.organisation_id);
            new_ass.setSalt(null);
            new_ass.setPassword(null);
            new_ass.getOrganisation().getAssessors().clear();
            System.out.println("********** AssessorManagementAdminSystemResource.createNewAssessor(): Finished and returned " + new_ass.getAssessorId() + " assessor id **********");
            return Response.status(Response.Status.OK).entity(new_ass).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewAssessor(): Error -> OrganisationID does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Organisation ID does not exist")).build();
        } catch (AssessorExistsException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewAssessor(): Assessor email already exists **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor Email already exists")).build();
        } catch (UnknownPersistenceException | InputDataValidationException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewAssessor(): Unknown persistence error / input validation error **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        }
    }

    @Path("assessors/{id}")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAssessor(@Context HttpHeaders headers, @PathParam("id") long assessorId, AssessorDetailBody assessorDetail) {
        System.out.println("********** AssessorManagementAdminSystemResource.updateAssessor(): Update assessor request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.updateAssessor(): Request denied due to invalid bearer token **********");
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

            System.out.println("********** AssessorManagementAdminSystemResource.updateAssessor(): Finished updating and returned assessor ID = " + assessor.getAssessorId() + " **********");
            return Response.status(Response.Status.OK).entity(assessor).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.updateAssessor(): Error -> AssessorID/SupervisorId does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor/Supervisor does not exist")).build();
        }
    }

    @Path("assessors/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAssessor(@Context HttpHeaders headers, @PathParam("id") long assessorId) {
        System.out.println("********** AssessorManagementAdminSystemResource.deleteAssessor(): Delete assessor request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.deleteAssessor(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            assessorSessionBean.deleteAssessor(assessorId);
            System.out.println("********** AssessorManagemnetAdminSystemResource.deleteAssessor(): Assessor " + assessorId + " deleted successfully **********");
            return Response.status(Response.Status.OK).build();
        } catch (AssessorDeletionError ex) {
            System.out.println("********** AssessorManagemnetAdminSystemResource.deleteAssessor(): Assessor deletion not allowed **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAdminSystemResource.deleteAssessor(): Assessor ID " + assessorId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor ID " + assessorId + " does not exist.")).build();
        }
    }

    @Path("organisations")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllOrganisations(@Context HttpHeaders headers) {
        System.out.println("********** AssessorManagementAdminSystemResource.retrieveAllOrganisations(): Retrieve all organisations request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.retrieveAllOrganisations(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<OrganisationEntity> orgs = organisationSessionBean.retrieveAllOrganisation();
        for (OrganisationEntity org : orgs) {
            org.getAssessors().clear();
        }

        System.out.println("********** AssessorManagementAdminSystemResource.retrieveAllOrganisations(): Finished and returned " + orgs.size() + " organisations  **********");
        GenericEntity<List<OrganisationEntity>> response = new GenericEntity<List<OrganisationEntity>>(orgs) {
        };
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Path("organisation-types")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retreiveOrganisationTypes(@Context HttpHeaders headers) {
        System.out.println("********** AssessorManagementAdminSystemResource.retrieveOrganisationTypes(): Retrieve organisation types request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.retrieveOrganisationTypes(): Request denied due to invalid bearer token **********");
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
        GenericEntity<List<String>> response = new GenericEntity<List<String>>(orgTypes) {
        };
        System.out.println("********** AssessorManagementAdminSystemResource.retrieveOrganisationTypes(): Finished and returned " + orgTypes.size() + " organisation types  **********");
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Path("organisations")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewOrganisations(@Context HttpHeaders headers, OrganisationDetailBody details) {
        System.out.println("********** AssessorManagementAdminSystemResource.createNewOrganisations(): Create new organisation request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewOrganisations(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<AgencyTypeEnum> types = new ArrayList<>();
        for (String s : details.organisationTypes) {  // we can assume all will be valid
            s = s.toUpperCase();
            String[] parts = s.split(" ");
            String agency = parts[0];
            AgencyTypeEnum agencyType = AgencyTypeEnum.valueOf(agency);
            types.add(agencyType);
        }
        try {
            OrganisationEntity org = new OrganisationEntity(details.name, types);

            AssessorEntity org_admin = new AssessorEntity(details.adminEmail, details.adminName, true);
            OrganisationEntity new_org = organisationSessionBean.createNewOrganisationWithOrgAdmin(org, org_admin);

            for (AssessorEntity ass : new_org.getAssessors()) {
                ass.setOrganisation(null);
                ass.setPassword(null);
                ass.setSalt(null);
                ass.getAssessments().clear();
                ass.getCertificates().clear();
                ass.getClients().clear();
            }
            System.out.println("********** AssessorManagementAdminSystemResource.createNewOrganisations(): Finished and returned " + new_org.getOrganisationId() + " organisation id **********");
            return Response.status(Response.Status.OK).entity(new_org).build();
        } catch (UnknownPersistenceException | InputDataValidationException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewOrganisations(): Unknown persistence error / input validation error **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (OrganisationExistsException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewOrganisations(): Organisation Name already exists **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Organisation name already exists")).build();
        } catch (AssessorExistsException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.createNewOrganisations(): Assessor email already exists **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor email already exists")).build();
        }
    }

    @Path("organisations/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOrganisation(@Context HttpHeaders headers, @PathParam("id") long orgId) {
        System.out.println("********** AssessorManagementAdminSystemResource.deleteOrganisation(): Delete organisation request recieved **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.deleteOrganisation(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            organisationSessionBean.removeOrganisation(orgId);
            System.out.println("********** AssessorManagementAdminSystemResource.deleteOrganisation(): Organisation ID " + orgId + " removed successfully **********");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Organisation ID " + orgId + " removed successfully")).build();
        } catch (OrganisationNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.deleteOrganisation():  Organisation ID " + orgId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Organisation ID " + orgId + " does not exist")).build();
        }
    }

    @Path("assessors/{id}/updateActive")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAssessorActiveStatus(@Context HttpHeaders headers, @PathParam("id") long assessorId) {
        System.out.println("********** AssessorManagementAdminSystemResource.updateAssessorActiveStatus(): Update assessor active status request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.updateAssessorActiveStatus(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            boolean result = this.assessorSessionBean.updateActiveStatus(assessorId);
            System.out.println("********** AssessorManagementAdminSystemResource.updateAssessorActiveStatus(): Assessor ID " + assessorId + " is now set to " + ((result) ? "active" : "inactive") + " **********");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Assessor is now " + ((result) ? "active" : "inactive"))).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.updateAssessorActiveStatus(): Assessor id " + assessorId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor ID " + assessorId + " does not exist")).build();
        } catch (AssessorDeletionError ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.updateAssessorActiveStatus(): Assessor not advised to become inactive due to currently assigned clients **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        }
    }

    @Path("assessors/{id}/supervisee/{superviseeId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSuperviseeFromSupervisor(@Context HttpHeaders headers, @PathParam("id") long assessorId, @PathParam("superviseeId") long superviseeId) {
        System.out.println("********** AssessorManagementAdminSystemResource.removeSuperviseeFromSupervisor(): removeSuperviseeFromSupervisor request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorManagementAdminSystemResource.removeSuperviseeFromSupervisor(): Request denied due to invalid bearer token **********");
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

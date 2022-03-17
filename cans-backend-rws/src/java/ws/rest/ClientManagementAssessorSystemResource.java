/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.CaretakerSessionBeanLocal;
import ejb.session.stateless.ClientSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import entity.AdminUserEntity;
import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CaretakerEntity;
import entity.ClientEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import util.exception.CaretakerExistsException;
import util.exception.CaretakerNotFoundException;
import util.exception.ClientExistsException;
import util.exception.ClientNotFoundException;
import util.exception.ClientUpdateException;
import util.exception.InputDataValidationException;
import util.exception.LoginTokenNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import ws.datamodel.CaregiverDetailBody;
import ws.datamodel.ClientDetailBody;
import ws.datamodel.ErrorResponse;
import ws.datamodel.MessageResponse;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 */
@Path("clientManagementAssessorSystem")
public class ClientManagementAssessorSystemResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final AssessorSessionBeanLocal assessorSessionBean;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final ClientSessionBeanLocal clientSessionBean;
    private final CaretakerSessionBeanLocal caretakerSessionBean;

    /**
     * Creates a new instance of ClientManagementAssessorSystemResource
     */
    public ClientManagementAssessorSystemResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        assessorSessionBean = sessionBeanLookUp.assessorSessionBean;
        loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        clientSessionBean = sessionBeanLookUp.clientSessionBean;
        caretakerSessionBean = sessionBeanLookUp.caretakerSessionBean;

    }

    private AssessorEntity validateAssessor(HttpHeaders headers) throws UserNotFoundException {
        try {

            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAssessorSystem(headerToken);
        } catch (NullPointerException | LoginTokenNotFoundException ex) {
            throw new UserNotFoundException();
        }
    }

    @Path("assigned")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveClient(@Context HttpHeaders headers) {
        System.out.println("********** ClientManagementAssessorSystemResource.retrieveClient(): Get client request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        List<ClientEntity> clients;
        if (assessor.isRoot()) {
            clients = clientSessionBean.retrieveAllClientsInOrg(assessor.getOrganisation().getOrganisationId());
        } else {
            clients = clientSessionBean.retrieveClientByAssessor(assessor.getAssessorId());
        }

        for (ClientEntity client : clients) {
            client.getAssessment().clear();
            client.getCaretakers().clear();
            client.getAssessor().getAssessments().clear();
            client.getAssessor().setPassword(null);
            client.getAssessor().setSalt(null);
            client.getAssessor().setOrganisation(null);
            client.getAssessor().getClients().clear();
            client.getAssessor().setSupervisor(null);
            client.getAssessor().getSupervisee().clear();
            client.getAssessor().getCertificates().clear();
        }
        System.out.println("********** ClientManagementAssessorSystemResource.retrieveClient(): Finished and returned " + clients.size() + " clients **********");
        GenericEntity<List<ClientEntity>> genericClients = new GenericEntity<List<ClientEntity>>(clients) {
        };
        return Response.status(Response.Status.OK).entity(genericClients).build();
    }

    @Path("unassigned")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveUnassignedClient(@Context HttpHeaders headers) {

        System.out.println("********** ClientManagementAssessorSystemResource.retrieveUnassignedClient(): Get client request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<ClientEntity> results = clientSessionBean.retreiveUnassignedClientsInOrg(assessor.getOrganisation().getOrganisationId());
        for (ClientEntity client : results) {
            client.getAssessment().clear();
            client.getCaretakers().clear();
            client.getAssessor().getAssessments().clear();
            client.getAssessor().setPassword(null);
            client.getAssessor().setSalt(null);
            client.getAssessor().setOrganisation(null);
            client.getAssessor().getClients().clear();
            client.getAssessor().setSupervisor(null);
            client.getAssessor().getSupervisee().clear();
            client.getAssessor().getCertificates().clear();
        }
        System.out.println("********** ClientManagementAssessorSystemResource.retrieveUnassignedClient(): Finished and returned " + results.size() + " clients **********");
        GenericEntity<List<ClientEntity>> genericClients = new GenericEntity<List<ClientEntity>>(results) {
        };
        return Response.status(Response.Status.OK).entity(genericClients).build();
    }

    @Path("clients/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveClient(@Context HttpHeaders headers, @PathParam("id") Long clientId) {
        try {
            System.out.println("********** ClientManagementAssessorSystemResource.retrieveClient(): Get client request received **********");

            AssessorEntity assessor;
            try {
                assessor = this.validateAssessor(headers);
            } catch (UserNotFoundException ex) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
            }

            ClientEntity client = this.clientSessionBean.retrieveClientById(clientId);
            for (AssessmentEntity assessment : client.getAssessment()) {
                assessment.getAssessor().getAssessments().clear();
                assessment.getAssessor().setPassword(null);
                assessment.getAssessor().setSalt(null);
                assessment.getAssessor().setOrganisation(null);
                assessment.getAssessor().getClients().clear();
                assessment.getAssessor().setSupervisor(null);
                assessment.getAssessor().getSupervisee().clear();
                assessment.getAssessor().getCertificates().clear();
                assessment.getCaretakerAssessments().clear();
                assessment.setClient(null);
                assessment.getResponse().clear();
            }
            for (CaretakerEntity caretaker : client.getCaretakers()) {
                caretaker.setClient(null);
                caretaker.getCaretakerAssessments().clear(); // might have to fix this for ability to show caretaker assessments
            }
            client.getAssessor().getAssessments().clear();
            client.getAssessor().setPassword(null);
            client.getAssessor().setSalt(null);
            client.getAssessor().setOrganisation(null);
            client.getAssessor().getClients().clear();
            client.getAssessor().setSupervisor(null);
            client.getAssessor().getSupervisee().clear();
            client.getAssessor().getCertificates().clear();
            System.out.println("********** ClientManagementAssessorSystemResource.retrieveClient(): Finished and returned client ID = " + client.getClientId() + ", unique ID = " + client.getClientUniqueId() + " **********");
            return Response.status(Response.Status.OK).entity(client).build();

        } catch (ClientNotFoundException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.retrieveClient(): Error -> ClientID " + clientId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Client does not exist")).build();
        }
    }

    @Path("clients")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewClient(@Context HttpHeaders headers, ClientDetailBody body) {
        System.out.println("********** ClientManagementAssessorSystemResource.createNewClient(): Create new client request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
            if (!assessor.isRoot()) {
                System.out.println("********** ClientManagementAdminSystemResource.createNewClient(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        ClientEntity client = new ClientEntity(body.clientUniqueId, body.name, body.idNumber, body.gender, DatatypeConverter.parseDateTime(body.dob).getTime(), body.address, body.ethnicity, body.admissionType, body.placementType, body.accommodationStatus, body.accommodationType, body.educationLevel, body.currentOccupation, body.monthlyIncome);
        try {
            ClientEntity newClient = clientSessionBean.createNewClient(client, body.assessorEmail);
            newClient.getAssessor().getAssessments().clear();
            newClient.getAssessor().setPassword(null);
            newClient.getAssessor().setSalt(null);
            newClient.getAssessor().setOrganisation(null);
            newClient.getAssessor().getClients().clear();
            newClient.getAssessor().setSupervisor(null);
            newClient.getAssessor().getSupervisee().clear();
            newClient.getAssessor().getCertificates().clear();
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClient(): Client ID " + newClient.getClientId() + " deleted successfully **********");
            return Response.status(Response.Status.OK).entity(newClient).build();
        } catch (ClientExistsException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClient(): Client Unique ID already exists ********** ");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Client unique ID already exist.")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClient(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClient(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.deleteClient(): Assessor Email " + body.assessorEmail + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor email " + body.assessorEmail + " does not exist.")).build();
        }
    }

    @Path("clients/{id}")
    @PATCH
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClientDetails(@Context HttpHeaders headers, @PathParam("id") Long clientId, ClientDetailBody body) {
        System.out.println("********** ClientManagementAssessorSystemResource.updateClientDetails(): Update client detail request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
            if (!assessor.isRoot()) {
                System.out.println("********** ClientManagementAssessorSystemResource.updateClientDetails(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            clientSessionBean.updateClientDetails(clientId, body.clientUniqueId, body.name, body.idNumber, body.gender, DatatypeConverter.parseDateTime(body.dob).getTime(), body.address, body.ethnicity, body.admissionType, body.placementType, body.accommodationStatus, body.accommodationType, body.educationLevel, body.currentOccupation, body.monthlyIncome, body.assessorEmail);
            System.out.println("********** ClientManagementAssessorSystemResource.updateClientDetails: Client ID " + clientId + " details changed successfully **********");
            return Response.status(Response.Status.OK).build();
        } catch (ClientNotFoundException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.updateClientDetails: Client ID " + clientId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Client does not exist.")).build();
        } catch (ClientUpdateException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.updateClientDetails: Client ID " + clientId + " desired unique ID already exists **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Client Unique ID already exists.")).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** ClientManagementAdminSystemResource.updateClientDetails: Assessor Email " + body.assessorEmail + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor Email " + body.assessorEmail + " does not exist.")).build();
        }
    }

    @Path("clients/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteClient(@Context HttpHeaders headers, @PathParam("id") Long clientId) {
        System.out.println("********** ClientManagementAssessorSystemResource.deleteClient(): Delete client request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
            if (!assessor.isRoot()) {
                System.out.println("********** ClientManagementAdminSystemResource.updateClientDetails(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            clientSessionBean.deleteClient(clientId);
            System.out.println("********** ClientManagementAssessorSystemResource.deleteClient(): Client ID " + clientId + " deleted successfully **********");
            return Response.status(Response.Status.OK).build();
        } catch (ClientNotFoundException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.deleteClient(): Client ID " + clientId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Client does not exist.")).build();
        }
    }

    @Path("caretakers")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewCaretaker(@Context HttpHeaders headers, CaregiverDetailBody body) {
        System.out.println("********** ClientManagementAssessorSystemResource.createNewCaretaker(): Create new caretaker request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
            if (!assessor.isRoot()) {
                System.out.println("********** ClientManagementAssessorSystemResource.updateClientDetails(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        CaretakerEntity caretaker = new CaretakerEntity(body.caretakerUniqueId, body.name, body.idNumber, body.gender, DatatypeConverter.parseDateTime(body.dob).getTime(), body.relationshipToClient, body.address, body.accommodationStatus, body.accommodationType, body.educationLevel, body.currentOccupation, body.monthlyIncome, true);
        try {
            long caretakerId = caretakerSessionBean.createNewCaretaker(caretaker, body.clientId);
            body.caretakerId = caretakerId;
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClaretaker(): Caretaker ID = " + caretakerId + "; caretakerUniqueId = " + body.caretakerUniqueId + " created successfully **********");
            return Response.status(Response.Status.OK).entity(body).build();
        } catch (CaretakerExistsException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClaretaker(): Caretaker Unique Id already exists ********** ");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caregiver unique ID already exist.")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClaretaker(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClaretaker(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (ClientNotFoundException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.createNewClaretaker(): Client ID " + body.clientId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Client ID " + body.clientId + " does not exist.")).build();
        }
    }

    @Path("caretakers/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCaretaker(@Context HttpHeaders headers, @PathParam("id") Long caretakerId) {
        System.out.println("********** ClientManagementAssessorSystemResource.deleteCaretaker(): Delete caretaker request received **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
            if (!assessor.isRoot()) {
                System.out.println("********** ClientManagementAssessorSystemResource.updateClientDetails(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            caretakerSessionBean.deleteCaretaker(caretakerId);
            System.out.println("********** ClientManagementAssessorSystemResource.deleteCaretaker(): Caretaker ID " + caretakerId + " deleted successfully **********");
            return Response.status(Response.Status.OK).build();
        } catch (CaretakerNotFoundException ex) {
            System.out.println("********** ClientManagementAssessorSystemResource.deleteCaretaker(): Caretaker ID " + caretakerId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Caregiver ID " + caretakerId + " does not exist.")).build();
        }
    }

    @Path("caretakers/{id}/updateActive")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCaretakerActive(@Context HttpHeaders headers, @PathParam("id") Long caretakerId) {

        System.out.println("********* AssessorManagemnetAssessorSystemResource.updateCaretakerActive() : Deactivate caretaker ********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
            if (!assessor.isRoot()) {
                System.out.println("********** AssessorManagemnetAssessorSystemResource.updateClientDetails(): Request denied due to lack of authority to perform this action **********");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            Boolean result = caretakerSessionBean.updateActiveStatus(caretakerId);
            System.out.println("**********AssessorManagemnetAssessorSystemResource.updateCaretakerActive(): Caretaker ID " + caretakerId + " is now set to " + ((result) ? "active" : "inactive") + " **********");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Caregiver is now " + ((result) ? "active" : "inactive"))).build();
        } catch (CaretakerNotFoundException ex) {
            System.out.println("********** AssessorManagemnetAssessorSystemResource.updateCaretakerActive(): Caretaker ID " + caretakerId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Caregiver ID " + caretakerId + " does not exist.")).build();
        }

    }
}

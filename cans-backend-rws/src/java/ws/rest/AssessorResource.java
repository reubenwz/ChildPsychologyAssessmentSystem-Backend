/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.PasswordChangeRequestSessionBeanLocal;
import entity.AssessorEntity;
import entity.ClientEntity;
import entity.LoginTokenEntity;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
import util.exception.LoginTokenNotFoundException;
import util.exception.PasswordChangeRequestNotFoundException;
import util.exception.UserNotFoundException;
import ws.datamodel.AssesorDetailResponse;
import ws.datamodel.ErrorResponse;
import ws.datamodel.LoginBody;
import ws.datamodel.MessageResponse;
import ws.datamodel.PasswordResetAfterBody;
import ws.datamodel.PasswordResetBody;
import ws.datamodel.PasswordResetInternalBody;
import ws.datamodel.ValidAssessorLogin;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 */
@Path("/Assessor")
public class AssessorResource {

    private final SessionBeanLookup sessionBeanLookUp;
    private final AssessorSessionBeanLocal assessorSessionBean;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final PasswordChangeRequestSessionBeanLocal passwordChangeRequestSessionBean;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AssessorResource
     */
    public AssessorResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        assessorSessionBean = sessionBeanLookUp.assessorSessionBean;
        loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        passwordChangeRequestSessionBean = sessionBeanLookUp.passwordChangeRequestSessionBean;
    }

    private AssessorEntity validateAssessor(HttpHeaders headers) throws UserNotFoundException {
        try {

            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAssessorSystem(headerToken);
        } catch (NullPointerException | LoginTokenNotFoundException ex) {
            throw new UserNotFoundException();
        }
    }

    // helper method to determine if password is alphanumeric
    private boolean isAlphanumeric(String s) {
        boolean character = false;
        boolean digit = false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                digit = true;
            } else if (Character.isLetter(s.charAt(i))) {
                character = true;
            }
        }
        return character && digit;
    }

    @Path("login")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response assessorLogin(LoginBody loginDetails) {
        try {
            String email = loginDetails.email;
            String password = loginDetails.password;
            AssessorEntity assessor = assessorSessionBean.assessorLogin(email, password);
            System.out.println("********** AdminUserResources.adminUserLogin(): Assessor " + assessor.getEmail() + " login remotely via web service");

            assessor.setPassword(null);
            assessor.setSalt(null);

            LoginTokenEntity loginToken = loginTokenSessionBean.createNewLoginTokenForAssessorSystem(assessor.getAssessorId());
            String role = assessor.isRoot() ? "root" : "assessor";
            assessor.getCertificates().clear();
            assessor.getAssessments().clear();
            assessor.getClients().clear();
            assessor.getSupervisee().clear();
//            assessor.setOrganisation(null);
            assessor.getOrganisation().getAssessors().clear();
            assessor.setSupervisor(null);

            ValidAssessorLogin valid = new ValidAssessorLogin(loginToken.getTokenId(), loginToken.getExpiry(), role, assessor);
            return Response.status(Response.Status.OK).entity(valid).build();
        } catch (InvalidLoginCredentialException ex) {
            ErrorResponse invalid = new ErrorResponse("Incorrect login details.");
            return Response.status(Response.Status.CONFLICT).entity(invalid).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }

    }

    @Path("token/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeLoginToken(@Context HttpHeaders headers, @PathParam("id") String loginTokenId) {

        try {
            AssessorEntity assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorResources.removeLoginToken(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            if (!headerToken.equals(loginTokenId)) {
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You do not have the permission to do this.")).build();
            }

            loginTokenSessionBean.removeLoginToken(loginTokenId);
            return Response.status(Response.Status.OK).build();
        } catch (LoginTokenNotFoundException ex) {
            ErrorResponse invalid = new ErrorResponse("Token does not exist.");
            return Response.status(Response.Status.NOT_FOUND).entity(invalid).build();
        }
    }

    // user is not logged in when he makes this change request
    @Path("password-reset")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(PasswordResetBody body) {
        String email = body.email;
        boolean resetSuccess = passwordChangeRequestSessionBean.createPasswordChangeRequestAssessorSystem(email);
        if (resetSuccess) {
            System.out.println("********** AssessorResources.resetPassword(): Assessor " + email + " successfully requested password change via web service");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Password reset link is sent to your email address.")).build();
        } else {
            System.out.println("********** AssessorResources.resetPassword(): Assessor " + email + " password change request denied via web service");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Email address does not exist.")).build();
        }
    }

    @Path("password-resets/{id}")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPasswordComplete(@PathParam("id") String passwordRequestId, PasswordResetAfterBody body) {
        String password = body.password;
        if (password.length() < 8 || !isAlphanumeric(password)) {
            System.out.println("********** AssessorResources.resetPasswordComplete(): Reqeuest ID " + passwordRequestId + " new password is not suitable, requested via web service");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("New password does not fulfill alphanumeric requirements.")).build();
        }
        try {
            passwordChangeRequestSessionBean.updatePasswordAssessorSystem(passwordRequestId, password);
            System.out.println("********** AssessorResources.resetPasswordComplete(): Reqeuest ID " + passwordRequestId + " completed, requested via web service");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Password has been changed successfully!")).build();
        } catch (PasswordChangeRequestNotFoundException ex) {
            System.out.println("********** AssessorResources.resetPasswordComplete(): Reqeuest ID " + passwordRequestId + " not found, requested via web service");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Reset ID does not exist.")).build();
        }
    }

    @Path("password-reset-internal")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPasswordInternal(@Context HttpHeaders headers, PasswordResetInternalBody body) {
        System.out.println("********** AssessorResources.resetPasswordInternal(): Reset password change requested via web service  **********");

        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorResources.resetPasswordInternal(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        String new_password = body.new_password;
        String old_password = body.old_password;
        if (new_password.length() < 8 || !isAlphanumeric(new_password)) {
            System.out.println("********** AssessorResources.resetPasswordInternal(): Reset password change denied as new password is not suitable, requested via web service  **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("New password does not fulfill alphanumeric requirements.")).build();
        }

        try {
            assessorSessionBean.updatePassword(assessor.getAssessorId(), old_password, new_password);
            System.out.println("********** AssessorResources.resetPasswordInternal(): Reset password change completed for admin user " + assessor.getAssessorId());
            return Response.status(Response.Status.OK).entity(new MessageResponse("Password has been changed successfully!")).build();
        } catch (UserNotFoundException | InvalidLoginCredentialException ex) {
            System.out.println("********** AssessorResources.resetPasswordInternal(): Reset password change completed for admin user " + assessor.getAssessorId() + " denied due to incorrect old password  **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Invalid old password.")).build();
        }
    }

    @Path("detail-change")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePersonalDetails(@Context HttpHeaders headers, @QueryParam("name") String name) {

        AssessorEntity admin;
        try {
            admin = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorResources.changePersonalDetails(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            assessorSessionBean.updateDetails(admin.getAssessorId(), name);
            return Response.status(Response.Status.OK).entity(new MessageResponse("Details has been changed successfully!")).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorResources.changePersonalDetails(): Reset password change completed for admin user " + admin.getAssessorId() + " denied due to incorrect old password  **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Invalid old password.")).build();
        }
    }

    @Path("get-detail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonalDetails(@Context HttpHeaders headers) {
        AssessorEntity admin;
        try {
            admin = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorResources.getPersonalDetails(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        return Response.status(Response.Status.OK).entity(new AssesorDetailResponse(admin.getName())).build();
    }

    @Path("clients/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveClientByAssessorId(@Context HttpHeaders headers, @PathParam("id") Long assessorId) {

        try {
            System.out.println("********** AssessorResource.retrieveClientByAssessorId(): Get clients by assessor id " + assessorId + " request received **********");

            // retrieve assessor
            AssessorEntity assessor = this.assessorSessionBean.retrieveUserById(assessorId);

            assessor.getAssessments().clear();
            //            assessor.getClients().clear();
            assessor.setSupervisor(null);
            assessor.setOrganisation(null);
            assessor.getSupervisee().clear();
            assessor.getCertificates().clear();

            // retrieve all clients
            List<ClientEntity> clients = assessor.getClients();
            for (ClientEntity client : clients) {
                client.getAssessment().clear();
//                List<AssessmentEntity> assessments = client.getAssessment();
//                for (AssessmentEntity assessment : assessments) {
//                    assessment.getCaretakerAssessments().clear();
//                    assessment.setAssessor(null);
//                    assessment.setClient(null);
//                    assessment.getResponse().clear();
//                }
                client.getCaretakers().clear();
                client.setAssessor(null);
            }

            System.out.println("********** AssessorResource.retrieveClientByAssessorId(): Finished and returned " + clients.size() + " clients **********");
            GenericEntity<List<ClientEntity>> genericClients = new GenericEntity<List<ClientEntity>>(clients) {
            };

            return Response.status(Response.Status.OK).entity(genericClients).build();

        } catch (UserNotFoundException ex) {
            System.out.println("********** AssessorResource.retrieveClientByAssessorId(): Error -> AssessorID " + assessorId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Assessor does not exist")).build();
        }
    }

}

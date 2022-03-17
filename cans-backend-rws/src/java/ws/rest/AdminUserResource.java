/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AdminUserSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.PasswordChangeRequestSessionBeanLocal;
import entity.AdminUserEntity;
import entity.LoginTokenEntity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import util.exception.AdminUserExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.LoginTokenNotFoundException;
import util.exception.PasswordChangeRequestNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import ws.datamodel.AdminUserDetailChangeBody;
import ws.datamodel.AdminUserDetailResponse;
import ws.datamodel.AssessorEmailsResponse;
import ws.datamodel.ErrorResponse;
import ws.datamodel.LoginBody;
import ws.datamodel.MessageResponse;
import ws.datamodel.PasswordResetAfterBody;
import ws.datamodel.PasswordResetBody;
import ws.datamodel.PasswordResetInternalBody;
import ws.datamodel.ValidAdminSystemLogin;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 * @author Wang Ziyue
 * @author Ooi Jun Hao
 */
@Path("/AdminUser")
public class AdminUserResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final AdminUserSessionBeanLocal adminUserSessionBean;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final PasswordChangeRequestSessionBeanLocal passwordChangeRequestSessionBean;

    /**
     * Creates a new instance of AdminUserResource
     */
    public AdminUserResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        adminUserSessionBean = sessionBeanLookUp.adminUserSessionBean;
        loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        passwordChangeRequestSessionBean = sessionBeanLookUp.passwordChangeRequestSessionBean;
    }

    // helper method to check validaity of token
    private AdminUserEntity validateAdminUser(HttpHeaders headers) throws UserNotFoundException {
        try {
            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAdminSystem(headerToken);
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
    public Response adminUserLogin(LoginBody loginDetails) {
        try {
            String email = loginDetails.email;
            String password = loginDetails.password;
            AdminUserEntity adminUser = adminUserSessionBean.adminUserLogin(email, password);
            System.out.println("********** AdminUserResources.adminUserLogin(): Admin " + adminUser.getEmail() + " login remotely via web service");

            // for security reasons
            adminUser.setPassword(null);
            adminUser.setSalt(null);

            LoginTokenEntity loginToken = loginTokenSessionBean.createNewLoginTokenForAdminSystem(adminUser.getAdminId());
            String role = adminUser.isRoot() ? "root" : "admin";
            adminUser.getDoc().clear();
            ValidAdminSystemLogin valid = new ValidAdminSystemLogin(loginToken.getTokenId(), loginToken.getExpiry(), role, adminUser);
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
            AdminUserEntity admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getDataUpload(): Request denied due to invalid bearer token **********");
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
        boolean resetSuccess = passwordChangeRequestSessionBean.createPasswordChangeRequestAdminSystem(email);
        if (resetSuccess) {
            System.out.println("********** AdminUserResources.resetPassword(): Admin " + email + " successfully requested password change via web service");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Password reset link is sent to your email address.")).build();
        } else {
            System.out.println("********** AdminUserResources.resetPassword(): Admin " + email + " password change request denied via web service");
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
            System.out.println("********** AdminUserResources.resetPasswordComplete(): Reqeuest ID " + passwordRequestId + " new password is not suitable, requested via web service");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("New password does not fulfill alphanumeric requirements.")).build();
        }
        try {
            passwordChangeRequestSessionBean.updatePasswordAdminSystem(passwordRequestId, password);
            System.out.println("********** AdminUserResources.resetPasswordComplete(): Reqeuest ID " + passwordRequestId + " completed, requested via web service");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Password has been changed successfully!")).build();
        } catch (PasswordChangeRequestNotFoundException ex) {
            System.out.println("********** AdminUserResources.resetPasswordComplete(): Reqeuest ID " + passwordRequestId + " not found, requested via web service");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Reset ID does not exist.")).build();
        }
    }

    @Path("password-reset-internal")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPasswordInternal(@Context HttpHeaders headers, PasswordResetInternalBody body) {
        System.out.println("********** AdminUserResources.resetPasswordInternal(): Reset password change requested via web service  **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.resetPasswordInternal(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        String new_password = body.new_password;
        String old_password = body.old_password;
        if (new_password.length() < 8 || !isAlphanumeric(new_password)) {
            System.out.println("********** AdminUserResources.resetPasswordInternal(): Reset password change denied as new password is not suitable, requested via web service  **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("New password does not fulfill alphanumeric requirements.")).build();
        }

        try {
            adminUserSessionBean.updatePassword(admin.getAdminId(), old_password, new_password);
            System.out.println("********** AdminUserResources.resetPasswordInternal(): Reset password change completed for admin user " + admin.getAdminId());
            return Response.status(Response.Status.OK).entity(new MessageResponse("Password has been changed successfully!")).build();
        } catch (UserNotFoundException | InvalidLoginCredentialException ex) {
            System.out.println("********** AdminUserResources.resetPasswordInternal(): Reset password change completed for admin user " + admin.getAdminId() + " denied due to incorrect old password  **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Invalid old password.")).build();
        }
    }

    @Path("detail-change")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePersonalDetails(@Context HttpHeaders headers, AdminUserDetailChangeBody body) {
        AdminUserEntity admin;
        String dob = body.dob;
        String gender = body.gender;
        String name = body.name;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.changePersonalDetails(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        Date dob2 = null;
        if (dob != null) {
            dob2 = DatatypeConverter.parseDateTime(dob).getTime();
        }
        adminUserSessionBean.updateDetails(admin.getAdminId(), gender, dob2, name);
        return Response.status(Response.Status.OK).entity(new MessageResponse("Details has been changed successfully!")).build();
    }

    @Path("get-detail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonalDetails(@Context HttpHeaders headers) {
        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getPersonalDetails(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//        df.setTimeZone(tz);
//        String nowAsISO = df.format(admin.getDob());
        return Response.status(Response.Status.OK).entity(new AdminUserDetailResponse(admin.getDob(), admin.getname(), admin.getGender())).build();
    }

    @Path("admins")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllAdminUsers(@Context HttpHeaders headers) {
        try {
            AdminUserEntity admin = this.validateAdminUser(headers);
            if (!admin.isRoot()) {
                System.out.println("********** AdminUserResources.getAllAdmins(): Request denied due non root user **********");
                return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Current admin is not a root admin")).build();
            }
        } catch (UserNotFoundException ex) {
            System.out.println("********** AdminUserResources.getAllAdmins(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<AdminUserEntity> admins = adminUserSessionBean.retrieveAllAdmins();
        for (AdminUserEntity admin : admins) {
            admin.getDoc().clear();
        }
        GenericEntity<List<AdminUserEntity>> response = new GenericEntity<List<AdminUserEntity>>(admins) {
        };
        System.out.println("********** AdminUserResources.getAllAdmins(): Finished and return " + admins.size() + " admins **********");
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Path("admins/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdminUser(@Context HttpHeaders headers, @PathParam("id") long adminUserId) {
        try {
            AdminUserEntity admin = this.validateAdminUser(headers);
            if (!admin.isRoot()) {
                System.out.println("********** AdminUserResources.deleteAdmin(): Request denied due non root user **********");
                return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Current admin is not a root admin")).build();
            }
        } catch (UserNotFoundException ex) {
            System.out.println("********** AdminUserResources.deleteAdmin(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            adminUserSessionBean.deleteAdminUser(adminUserId);
            System.out.println("********** AdminUserResources.deleteAdmin(): Successfully removed admin ID " + adminUserId + " **********");
            return Response.status(Response.Status.OK).entity(new MessageResponse("Admin User has been deleted successfully")).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** AdminUserResources.deleteAdmin(): Admin User ID " + adminUserId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Admin User ID " + adminUserId + " does not exist")).build();
        }
    }
    
    @Path("admins")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAdminUser(@Context HttpHeaders headers, AdminUserDetailChangeBody adminUser) {
        try {
            AdminUserEntity admin = this.validateAdminUser(headers);
            if (!admin.isRoot()) {
                System.out.println("********** AdminUserResources.createNewAdminUser(): Request denied due non root user **********");
                return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Current admin is not a root admin")).build();
            }
        } catch (UserNotFoundException ex) {
            System.out.println("********** AdminUserResources.createNewAdminUser(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        try {
            AdminUserEntity new_admin = new AdminUserEntity(adminUser.email, adminUser.name, DatatypeConverter.parseDateTime(adminUser.dob).getTime(), adminUser.gender, false);
            new_admin = adminUserSessionBean.createNewAdminUserFromSystem(new_admin);
         
            System.out.println("********** AdminUserResources.createNewAdminUser(): Successfully created new admin with ID " + new_admin.getAdminId() + " **********");
            return Response.status(Response.Status.OK).entity(new_admin).build();
        } catch (UnknownPersistenceException | InputDataValidationException ex) {
            System.out.println("********** AdminUserResources.createNewAdminUser(): Unknown persistence exception/input data validation exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (AdminUserExistsException ex) {
            System.out.println("********** AdminUserResources.createNewAdminUser(): Admin User Email " + adminUser.email + " already exists **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Email " + adminUser.email + " already exists")).build();
        }
 
    }
}

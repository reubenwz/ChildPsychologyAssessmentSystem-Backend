/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CertificationSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import entity.AssessorEntity;
import entity.CertificationEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import util.exception.CertificationExistsException;
import util.exception.CertificationNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.LoginTokenNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import ws.datamodel.CertificationDetailBody;
import ws.datamodel.ErrorResponse;

/**
 * REST Web Service
 *
 * @author Ong Bik Jeun
 */
@Path("Certification")
public class CertificationResource {

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final CertificationSessionBeanLocal certificationSessionBean;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CertificationResource
     */
    public CertificationResource() {
        this.sessionBeanLookUp = new SessionBeanLookup();
        this.loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        this.certificationSessionBean = sessionBeanLookUp.certificationSessionBean;
    }

    private AssessorEntity validateAssessor(HttpHeaders headers) throws UserNotFoundException {
        try {

            String headerToken = headers.getRequestHeader("Authorization").get(0).split(" ")[1];
            return loginTokenSessionBean.validateLoginTokenAssessorSystem(headerToken);
        } catch (NullPointerException | LoginTokenNotFoundException ex) {
            throw new UserNotFoundException();
        }
    }

    @Path("certificates")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCertification(@Context HttpHeaders headers) {
        System.out.println("********** CertificationResource.retrieveAllCertification(): Get Certification request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<CertificationEntity> cert;
        if (assessor.isRoot()) {
            cert = certificationSessionBean.retrieveAllCertificate(assessor.getOrganisation().getOrganisationId());
        } else {
            cert = certificationSessionBean.retrieveAllCertificatebyAssessorId(assessor.getAssessorId());
        }

        for (CertificationEntity eachCert : cert) {
            eachCert.getAssessor().getCertificates().clear();
            eachCert.getAssessor().getAssessments().clear();
            eachCert.getAssessor().getClients().clear();
            eachCert.getAssessor().setOrganisation(null);
            eachCert.getAssessor().setSupervisor(null);
            eachCert.getAssessor().getSupervisee().clear();  
        }
        System.out.println("********** CertificationResource.retrieveAllCertification(): Finished and returned " + cert.size() + " certs **********");
        GenericEntity<List<CertificationEntity>> genericClients = new GenericEntity<List<CertificationEntity>>(cert) {
        };
        return Response.status(Response.Status.OK).entity(genericClients).build();

    }

    @Path("certificate")
    @POST
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewCertification(@Context HttpHeaders headers, CertificationDetailBody body) {

        System.out.println("********** CertificationResource.createNewCertification(): Create Certification request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        CertificationEntity cert = new CertificationEntity(DatatypeConverter.parseDateTime(body.dateOfCert).getTime(), body.vignette, body.recentScore, body.noOfTimesRecertified);
        try {
            CertificationEntity newCert = certificationSessionBean.createCertificate(cert, assessor.getAssessorId());
            newCert.setAssessor(null);
            System.out.println("********** CertificationResource.createNewCertification(): Certification created successfully **********");
            return Response.status(Response.Status.OK).entity(newCert).build();
        } catch (CertificationExistsException ex) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Certification already exists")).build();
        } catch (UnknownPersistenceException ex) {
            System.out.println("********** CertificationResource.createNewCertification(): Unknown Persistence Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Unknown persistence exception occured. Please try again.")).build();
        } catch (InputDataValidationException ex) {
            System.out.println("********** CertificationResource.createNewCertification(): Input Data Exception **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex.getMessage())).build();
        } catch (UserNotFoundException ex) {
            System.out.println("********** CertificationResource.createNewCertification(): Assessor ID " + assessor.getAssessorId() + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Assessor ID " + assessor.getAssessorId() + " does not exist.")).build();
        }
    }

    @Path("certificate/{id}")
    @PATCH
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCertificate(@Context HttpHeaders headers, @PathParam("id") Long certId, CertificationDetailBody body) {

        System.out.println("********** CertificationResource.updateCertificate(): Update Certification request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            certificationSessionBean.updateCertification(certId, DatatypeConverter.parseDateTime(body.dateOfCert).getTime(), body.vignette, body.recentScore, body.noOfTimesRecertified);
            System.out.println("********** CertificationResource.updateCertificate(): Certification Updated successfully **********");
            return Response.status(Response.Status.OK).build();

        } catch (CertificationNotFoundException ex) {
            System.out.println("********** CertificationResource.updateCertificate: Certificate ID " + certId + " does not exist **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("Certification does not exist.")).build();

        }

    }

    @Path("certificate/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCertification(@Context HttpHeaders headers, @PathParam("id") Long certId) {

        System.out.println("********** CertificationResource.deleteCertification(): Update Certification request received **********");
        AssessorEntity assessor;
        try {
            assessor = this.validateAssessor(headers);
        } catch (UserNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            certificationSessionBean.deleteCertification(certId);
            System.out.println("********** CertificationResource.deleteCertification(): Cert ID " + certId + " deleted successfully **********");
            return Response.status(Response.Status.OK).build();
        } catch (CertificationNotFoundException ex) {
            System.out.println("********** CertificationResource.deleteCertification(): Cert ID " + certId + " does not exist **********");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponse("Cert does not exist.")).build();
        }
    }

}

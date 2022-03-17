/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ExportSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.UploadSessionBeanLocal;
import entity.AdminUserEntity;
import entity.UploadEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import util.exception.DataUploadException;
import util.exception.ExportDataException;
import util.exception.LoginTokenNotFoundException;
import util.exception.UserNotFoundException;
import ws.datamodel.DataUploadResponse;
import ws.datamodel.DataUploadsResponse;
import ws.datamodel.DownloadDataResponse;
import ws.datamodel.ErrorResponse;

/**
 * REST Web Service
 *
 * @author Ooi Jun Hao
 */
@Path("DataManagement")
public class DataManagementResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookUp;
    private final LoginTokenSessionBeanLocal loginTokenSessionBean;
    private final UploadSessionBeanLocal uploadSessionBean;
    private final ExportSessionBeanLocal exportSessionBean;

    public DataManagementResource() {
        sessionBeanLookUp = new SessionBeanLookup();
        loginTokenSessionBean = sessionBeanLookUp.loginTokenSessionBean;
        uploadSessionBean = sessionBeanLookUp.uploadSessionBean;
        exportSessionBean = sessionBeanLookUp.exportSessionBean;
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

    @Path("data-upload")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadData(@Context HttpHeaders headers,
            @FormDataParam("upload-file") InputStream uploadedInputStream,
            @FormDataParam("upload-file") FormDataContentDisposition fileDetail) {

        System.out.println("********** DataManagementResources.uploadData(): File " + fileDetail.getFileName() + " upload request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.uploadData(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        String newFileName = UUID.randomUUID().toString() + ".xlsx";
        // local disk path to store the file
        String uploadedFileLocation = "C:\\glassfish-5.1.0-uploadedfiles\\CANS\\" + newFileName;
        System.out.println(uploadedFileLocation);
        // save it
        File objFile = new File(uploadedFileLocation);
        if (objFile.exists()) {
            objFile.delete();
        }
        try {
            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UploadEntity upload;
        try {
            upload = uploadSessionBean.importRawData(uploadedFileLocation, newFileName, admin.getAdminId());
            upload.getAdmin().getDoc().clear();
            upload.getAdmin().setSalt(null);
            upload.getAdmin().setPassword(null);
            if (!upload.isSuccess()) {
                DataUploadResponse response = new DataUploadResponse(upload.getDocId(), upload.getUploadDate(), upload.getDocDetails(), upload.getUrl(), false, upload.getAdmin());
                String output = "********** DataManagementResources.uploadData(): File uploaded successfully (with missing fields) via JAX-RS based RESTFul Webservice to: " + uploadedFileLocation;
                System.out.println(output);
                return Response.status(Response.Status.OK).entity(response).build();
            } else {
                DataUploadResponse response = new DataUploadResponse(upload.getDocId(), upload.getUploadDate(), upload.getDocDetails(), upload.getUrl(), true, upload.getAdmin());
                String output = "********** DataManagementResources.uploadData(): File uploaded successfully via JAX-RS based RESTFul Webservice to: " + uploadedFileLocation;
                System.out.println(output);
                return Response.status(Response.Status.OK).entity(response).build();
            }
        } catch (UserNotFoundException | DataUploadException ex) {
            try {
                String output = "********** DataManagementResources.uploadData(): File uploaded with error via JAX-RS based RESTFul Webservice to: " + uploadedFileLocation;
                System.out.println(output);
                upload = uploadSessionBean.renderUnsuccesfulUpload(ex.getMessage(), newFileName, admin.getAdminId());
                upload.getAdmin().getDoc().clear();
                upload.getAdmin().setSalt(null);
                upload.getAdmin().setPassword(null);
                DataUploadResponse response = new DataUploadResponse(upload.getDocId(), upload.getUploadDate(), upload.getDocDetails(), upload.getUrl(), false, upload.getAdmin());
                output = "********** DataManagementResources.uploadData(): File uploaded successfully (with missing fields) via JAX-RS based RESTFul Webservice to: " + uploadedFileLocation;
                System.out.println(output);
                return Response.status(Response.Status.OK).entity(response).build();
            } catch (UserNotFoundException ex1) {
                return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse(ex1.getMessage())).build();
            }
        }

    }

    @Path("data-uploads")
    @GET
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataUploads(@Context HttpHeaders headers, 
            @QueryParam("page") Integer page,
            @QueryParam("uploadDate") String uploadDate,
            @QueryParam("docDetails") String docDetails,
            @QueryParam("adminName") String uploaderName) {

        System.out.println("********** DataManagementResources.getDataUpload(): Retrieve data uploads request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.getDataUpload(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }

        List<UploadEntity> uploads = uploadSessionBean.retrieveAllUploads();
        for (UploadEntity upload : uploads) {
            upload.getAdmin().getDoc().clear();
            upload.getAdmin().setPassword(null);
            upload.getAdmin().setSalt(null);
        }
        //filter the upload entities based on search criterias    
        if (uploadDate != null) {
            LocalDate dateOfInterest = DatatypeConverter.parseDateTime(uploadDate).getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            uploads.removeIf(u -> !DatatypeConverter.parseDateTime(u.getUploadDate()).getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(dateOfInterest));     
        }
        if (docDetails != null) {
            uploads.removeIf(u -> !u.getDocDetails().toLowerCase().contains(docDetails.toLowerCase())); // pretty scuffed by oh well
        }
        if (uploaderName != null) {
            uploads.removeIf(u -> !u.getAdmin().getname().toLowerCase().contains(uploaderName.toLowerCase()));
        }
        
        int per_page = 10;
        int total_records = uploads.size();
        int current_page = page == null ? 1 : page;
        int last_page = (total_records - 1) / per_page + 1;
        List<UploadEntity> to_return;
        try {
            to_return = uploads.subList(per_page * (current_page - 1), per_page * (current_page));
        } catch (IndexOutOfBoundsException ex) {
            to_return = uploads.subList(per_page * (current_page - 1), uploads.size());
        }
        DataUploadsResponse res = new DataUploadsResponse(per_page, current_page, last_page, total_records, to_return);

        System.out.println("********** DataManagementResources.getDataUpload(): Finished **********");
        return Response.status(Response.Status.OK).entity(res).build();
    }
 
    @Path("data-download")
    @GET
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadData(@Context HttpHeaders headers, @QueryParam("start_date") String start_date, @QueryParam("end_date") String end_date) {
        System.out.println("********** DataManagementResources.downloadData(): Download data request received **********");

        AdminUserEntity admin;
        try {
            admin = this.validateAdminUser(headers);
        } catch (UserNotFoundException ex) {
            System.out.println("********** DataManagementResources.downloadData(): Request denied due to invalid bearer token **********");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse("Invalid bearer token.")).build();
        }
        try {
            String link = exportSessionBean.exportCleanedData(start_date, end_date);
            System.out.println("********** DataManagementResources.downloadData(): Finished generating data excel **********");
            return Response.status(Response.Status.CREATED).entity(new DownloadDataResponse(link)).build();
        } catch (ExportDataException ex) {
            System.out.println("********** DataManagementResources.downloadData(): Error -> No data retrieved **********");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorResponse("There is no data available in the stated date range")).build();
        }
    }
}

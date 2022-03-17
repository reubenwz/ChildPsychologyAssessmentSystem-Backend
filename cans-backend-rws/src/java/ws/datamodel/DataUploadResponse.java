/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.AdminUserEntity;
import java.util.Date;

/**
 *
 * @author Ooi Jun Hao
 */
public class DataUploadResponse {
    
    private long docId;
    private String uploadDate;
    private String docDetails;
    private String url;
    private boolean success;
    private AdminUserEntity admin;

    public DataUploadResponse() {
    }

    public DataUploadResponse(long docId, String uploadDate, String docDetails, String url, boolean success, AdminUserEntity admin) {
        this.docId = docId;
        this.uploadDate = uploadDate;
        this.docDetails = docDetails;
        this.url = url;
        this.success = success;
        this.admin = admin;
    }

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDocDetails() {
        return docDetails;
    }

    public void setDocDetails(String docDetails) {
        this.docDetails = docDetails;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public AdminUserEntity getAdmin() {
        return admin;
    }

    public void setAdmin(AdminUserEntity admin) {
        this.admin = admin;
    }
    
}

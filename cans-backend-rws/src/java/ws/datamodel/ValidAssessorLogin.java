/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.AssessorEntity;
import java.util.Date;

/**
 *
 * @author Ong Bik Jeun
 */
public class ValidAssessorLogin {

    private String token;
    private Date token_expiry;
    private String role;
    private AssessorEntity assessor;

    public ValidAssessorLogin() {
    }

    public ValidAssessorLogin(String token, Date token_expiry, String role, AssessorEntity assessor) {
        this.token = token;
        this.token_expiry = token_expiry;
        this.role = role;
        this.assessor = assessor;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getToken_expiry() {
        return token_expiry;
    }

    public void setToken_expiry(Date token_expiry) {
        this.token_expiry = token_expiry;
    }

    public AssessorEntity getAssessor() {
        return assessor;
    }

    public void setAssessor(AssessorEntity assessor) {
        this.assessor = assessor;
    }

}

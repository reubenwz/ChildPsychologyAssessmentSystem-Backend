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
public class ValidAdminSystemLogin {
    
    private String token;
    private Date token_expiry;
    private String role;
    private AdminUserEntity user;

    public ValidAdminSystemLogin() {
    }

    public ValidAdminSystemLogin(String token, Date token_expiry, String role, AdminUserEntity user) {
        this.token = token;
        this.token_expiry = token_expiry;
        this.role = role;
        this.user = user;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public AdminUserEntity getUser() {
        return user;
    }

    public void setUser(AdminUserEntity user) {
        this.user = user;
    }
    
}

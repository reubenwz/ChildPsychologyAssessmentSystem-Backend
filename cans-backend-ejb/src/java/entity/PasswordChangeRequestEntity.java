/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class PasswordChangeRequestEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date requestDateTime;
    @Column(unique = true, nullable = false, columnDefinition = "CHAR(128) NOT NULL")
    @NotNull
    private String requestToken;
    @Column(nullable = false)
    @NotNull
    private boolean newAccount;

    @JoinColumn(nullable = true)
    @OneToOne
    private AdminUserEntity admin;
    @JoinColumn(nullable = true)
    @OneToOne
    private AssessorEntity assessor;

    public PasswordChangeRequestEntity() {
    }

    public PasswordChangeRequestEntity(Date requestDateTime, String hash) {
        this.requestDateTime = requestDateTime;
        this.requestToken = hash;
        this.newAccount = false;
    }
    
    public PasswordChangeRequestEntity(Date requestDateTime, String hash, boolean newAccount) {
        this.requestDateTime = requestDateTime;
        this.requestToken = hash;
        this.newAccount = newAccount;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Date getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(Date requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public AdminUserEntity getAdmin() {
        return admin;
    }

    public void setAdmin(AdminUserEntity admin) {
        this.admin = admin;
    }

    public AssessorEntity getAssessor() {
        return assessor;
    }

    public void setAssessor(AssessorEntity assessor) {
        this.assessor = assessor;
    }
    
    public boolean isNewAccount() {
        return newAccount;
    }

    public void setNewAccount(boolean newAccount) {
        this.newAccount = newAccount;
    }
}

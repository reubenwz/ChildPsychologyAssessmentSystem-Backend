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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class LoginTokenEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenDBId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date creationDateTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date expiry;
    @Column(unique = true, nullable = false, columnDefinition = "CHAR(128) NOT NULL")
    @NotNull
    private String tokenId;

    @OneToOne
    private AdminUserEntity admin;
    @OneToOne
    private AssessorEntity assessor;

    public LoginTokenEntity() {
    }

    public LoginTokenEntity(Date creationDateTime, Date expiry, String tokenValue) {
        this.creationDateTime = creationDateTime;
        this.expiry = expiry;
        this.tokenId = tokenValue;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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

    public Long getTokenDBId() {
        return tokenDBId;
    }

    public void setTokenDBId(Long tokenDBId) {
        this.tokenDBId = tokenDBId;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

}

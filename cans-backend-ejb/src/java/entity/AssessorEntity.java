/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.security.CryptographicHelper;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class AssessorEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assessorId;
    @Column(nullable = false, unique = true, length = 64)
    @NotNull
    @Size(max = 64)
    @Email
    private String email;
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String password;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String name;
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String salt;
    @Column(nullable = false)
    @NotNull
    private boolean root;
    @Column(nullable = false)
    @NotNull
    private boolean active;

    @OneToMany(mappedBy = "assessor", fetch = FetchType.EAGER)
    private List<ClientEntity> clients;
    @OneToMany(mappedBy = "assessor", fetch = FetchType.EAGER)
    private List<AssessmentEntity> assessments;
    @OneToMany(mappedBy = "assessor", fetch = FetchType.EAGER)
    private List<CertificationEntity> certificates;
    @OneToMany(mappedBy = "supervisor", fetch = FetchType.EAGER)
    private List<AssessorEntity> supervisee;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private AssessorEntity supervisor;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private OrganisationEntity organisation;

    public AssessorEntity() {
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
        this.clients = new ArrayList<>();
        this.assessments = new ArrayList<>();
        this.certificates = new ArrayList<>();
        this.supervisee = new ArrayList<>();

    }
    
    // for creating of new assessor without a prior password -> put random password
    public AssessorEntity(String email, String name, boolean root) {
        this();
        this.email = email;
        this.name = name;
        this.root = root;
        this.active = true;
        
        setPassword(CryptographicHelper.getInstance().generateRandomString(12));
    }

    public AssessorEntity(String email, String firstName, boolean root, String password) {
        this();
        this.email = email;
        this.name = firstName;
        this.root = root;
        this.active = true;

        setPassword(password);
    }

    public AssessorEntity(String email, String name, boolean root, String password, OrganisationEntity organisation) {
        this();
        this.email = email;
        this.name = name;
        this.root = root;
        this.organisation = organisation;
        this.active = true;

        setPassword(password);
    }

    public OrganisationEntity getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationEntity organisation) {
        this.organisation = organisation;
    }

    public List<AssessorEntity> getSupervisee() {
        return supervisee;
    }

    public void setSupervisee(List<AssessorEntity> supervisee) {
        this.supervisee = supervisee;
    }

    public AssessorEntity getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(AssessorEntity supervisor) {
        this.supervisor = supervisor;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Long getAssessorId() {
        return assessorId;
    }

    public void setAssessorId(Long assessorId) {
        this.assessorId = assessorId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + this.salt));
        } else {
            this.password = null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<ClientEntity> getClients() {
        return clients;
    }

    public void setClients(List<ClientEntity> clients) {
        this.clients = clients;
    }

    public List<AssessmentEntity> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<AssessmentEntity> assessments) {
        this.assessments = assessments;
    }

    public List<CertificationEntity> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<CertificationEntity> certificates) {
        this.certificates = certificates;
    }

    public void addAssessment(AssessmentEntity assessment) {
        List<AssessmentEntity> assessments = this.getAssessments();
        for (AssessmentEntity existingAssessment : assessments) {
            if (Objects.equals(existingAssessment.getAssessmentId(), assessment.getAssessmentId())) {
                assessments.remove(existingAssessment);
                assessments.add(assessment);
                return;
            }
        }
        this.getAssessments().add(assessment);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}

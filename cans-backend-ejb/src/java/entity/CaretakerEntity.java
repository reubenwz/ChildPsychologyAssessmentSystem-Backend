/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ziyue
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "retrieveAllCaretakers", query = "SELECT c from CaretakerEntity c")})
public class CaretakerEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caretakerId;
    @Column(nullable = false, unique = true)
    @NotNull
    private long caretakerUniqueId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String name;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String idNumber;   
    @Column(nullable = false, length = 6)
    @NotNull
    private String gender;
    @Temporal(TemporalType.DATE)
    @Column(nullable = true) 
    private Date dob;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String relationshipToClient;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String address;
    @Column(nullable = false, length = 64) 
    @NotNull 
    @Size(max = 64)
    private String accommodationStatus;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String accommodationType;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String educationLevel;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String currentOccupation;
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private int monthlyIncome;
    @Column(nullable = false)
    @NotNull
    private boolean active;
    
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private ClientEntity client;
    @OneToMany(mappedBy = "caretaker")
    private List<CaretakerAssessmentEntity> caretakerAssessments;

    public CaretakerEntity() {
        this.caretakerAssessments = new ArrayList<>();
    }    

    public CaretakerEntity(long caretakerUniqueId) {
        this();
        this.caretakerUniqueId = caretakerUniqueId;
    }
    
    public CaretakerEntity(long caretakerUniqueId, String name, String idNumber, String gender, Date dob, String relationshipToClient, String address, String accomodationStatus, String accomodationType, String educationLevel, String currentOccupation, int monthlyIncome, boolean active) {
        this();
        this.caretakerUniqueId = caretakerUniqueId;
        this.name = name;
        this.idNumber = idNumber;
        this.gender = gender;
        this.dob = dob;
        this.relationshipToClient = relationshipToClient;
        this.address = address;
        this.accommodationStatus = accomodationStatus;
        this.accommodationType = accomodationType;
        this.educationLevel = educationLevel;
        this.currentOccupation = currentOccupation;
        this.monthlyIncome = monthlyIncome;
        this.active = active;
    }
    
    public long getCaretakerUniqueId() {
        return caretakerUniqueId;
    }

    public void setCaretakerUniqueId(long caretakerUniqueId) {
        this.caretakerUniqueId = caretakerUniqueId;
    }
   
    public Long getCaretakerId() {
        return caretakerId;
    }

    public void setCaretakerId(Long caretakerId) {
        this.caretakerId = caretakerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(dob);
        return nowAsISO;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getRelationshipToClient() {
        return relationshipToClient;
    }

    public void setRelationshipToClient(String relationshipToClient) {
        this.relationshipToClient = relationshipToClient;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccommodationStatus() {
        return accommodationStatus;
    }

    public void setAccommodationStatus(String accommodationStatus) {
        this.accommodationStatus = accommodationStatus;
    }

    public String getAccommodationType() {
        return accommodationType;
    }

    public void setAccommodationType(String accommodationType) {
        this.accommodationType = accommodationType;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getCurrentOccupation() {
        return currentOccupation;
    }

    public void setCurrentOccupation(String currentOccupation) {
        this.currentOccupation = currentOccupation;
    }

    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(int monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public List<CaretakerAssessmentEntity> getCaretakerAssessments() {
        return caretakerAssessments;
    }

    public void setCaretakerAssessments(List<CaretakerAssessmentEntity> caretakerAssessments) {
        this.caretakerAssessments = caretakerAssessments;
    }
    
    public void addCaretakerAssessment(CaretakerAssessmentEntity newCaretakerAssessment) {
        for (CaretakerAssessmentEntity existingAssessment : this.caretakerAssessments) {
            if (existingAssessment.equals(newCaretakerAssessment)) {
                this.caretakerAssessments.remove(existingAssessment);
                this.caretakerAssessments.add(newCaretakerAssessment);
                return;
            }
        }
       this.caretakerAssessments.add(newCaretakerAssessment);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (caretakerId != null ? caretakerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the caretakerId fields are not set
        if (!(object instanceof CaretakerEntity)) {
            return false;
        }
        CaretakerEntity other = (CaretakerEntity) object;
        if (this.caretakerId == null || other.caretakerId == null) {
            return false;
        }
        if ((this.caretakerId == null && other.caretakerId != null) || (this.caretakerId != null && !this.caretakerId.equals(other.caretakerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CaretakerEntity{" + "caretakerId=" + caretakerId + ", caretakerUniqueId=" + caretakerUniqueId + ", idNumber=" + idNumber + '}';
    }
}
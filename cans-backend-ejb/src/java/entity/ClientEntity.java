/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "retrieveAllClients", query = "SELECT c from ClientEntity c")})
public class ClientEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;
    @Column(nullable = false, unique = true)
    @NotNull
    private long clientUniqueId;
    @Column(nullable = false, length = 126)
    @NotNull
    @Size(max = 126)
    private String name;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String idNumber;
    @Column(nullable = false, length = 6)
    @NotNull
    private String gender;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @NotNull
    private Date dob;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String address;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String ethnicity;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String admissionType;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String placementType;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String accommodationStatus;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String accommodationType;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String educationLevel;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String currentOccupation;
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private int monthlyIncome;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private AssessorEntity assessor; //current assessor
    @OneToMany(mappedBy = "client")
    private List<AssessmentEntity> assessment;
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private List<CaretakerEntity> caretakers;

    public ClientEntity() {
        assessment = new ArrayList<>();
        caretakers = new ArrayList<>();
    }

    public ClientEntity(long clientUniqueId) {
        this();
        this.clientUniqueId = clientUniqueId;
    }

    public ClientEntity(long clientUniqueId, String name, String idNumber, String gender, Date dob, String address, String ethnicity, String admissionType, String placementType, String accommodationStatus, String accommodationType, String educationLevel, String currentOccupation, int monthlyIncome) {
        this();
        this.clientUniqueId = clientUniqueId;
        this.name = name;
        this.idNumber = idNumber;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.ethnicity = ethnicity;
        this.admissionType = admissionType;
        this.placementType = placementType;
        this.accommodationStatus = accommodationStatus;
        this.accommodationType = accommodationType;
        this.educationLevel = educationLevel;
        this.currentOccupation = currentOccupation;
        this.monthlyIncome = monthlyIncome;
    } 

    public ClientEntity(long clientUniqueId, String name, String idNumber, String gender, Date dob, String address, String ethnicity, String admisssionType, String placementType, String accommodationStatus, String accommodationType, String educationLevel, String currentOccupation, int monthlyIncome, AssessorEntity assessor, List<AssessmentEntity> assessment, List<CaretakerEntity> caretakers) {
        this();
        this.clientUniqueId = clientUniqueId;
        this.name = name;
        this.idNumber = idNumber;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.ethnicity = ethnicity;
        this.admissionType = admisssionType;
        this.placementType = placementType;
        this.accommodationStatus = accommodationStatus;
        this.accommodationType = accommodationType;
        this.educationLevel = educationLevel;
        this.currentOccupation = currentOccupation;
        this.monthlyIncome = monthlyIncome;
        this.assessor = assessor;
        this.assessment = assessment;
        this.caretakers = caretakers;
    }
    
    

    public long getClientUniqueId() {
        return clientUniqueId;
    }

    public void setClientUniqueId(long clientUniqueId) {
        this.clientUniqueId = clientUniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
    
    public Date getRawDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    public String getPlacementType() {
        return placementType;
    }

    public void setPlacementType(String placementType) {
        this.placementType = placementType;
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

    public AssessorEntity getAssessor() {
        return assessor;
    }

    public void setAssessor(AssessorEntity assessor) {
        this.assessor = assessor;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public List<AssessmentEntity> getAssessment() {
        return assessment;
    }

    public void setAssessment(List<AssessmentEntity> assessment) {
        this.assessment = assessment;
    }

    public void addAssessment(AssessmentEntity newAssessment) {
        for (AssessmentEntity existingAssessment : this.assessment) {
            if (existingAssessment.equals(assessment)) {
                this.assessment.remove(existingAssessment);
                this.assessment.add(newAssessment);
                return;
            }
        }
        this.assessment.add(newAssessment);
    }

    public void addCaretaker(CaretakerEntity newCaretaker) {
        for (CaretakerEntity existingCaretakers : this.caretakers) {
            if (existingCaretakers.equals(newCaretaker)) {
                this.caretakers.remove(existingCaretakers);
                this.caretakers.add(newCaretaker);
                return;
            }
        }
        this.caretakers.add(newCaretaker);
    }

    public List<CaretakerEntity> getCaretakers() {
        return caretakers;
    }

    public void setCaretakers(List<CaretakerEntity> caretakers) {
        this.caretakers = caretakers;
    }

    public int getAge() {
        LocalDate dobLocal = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(dobLocal, LocalDate.now());
        return (period.getYears());
    }
    
    public List<AssessmentEntity> getAssessmentsInDateRange(Date start, Date end) {
        List<AssessmentEntity> toReturn = new ArrayList<>();
        for (AssessmentEntity assessment : this.getAssessment()) {
            if (!(DatatypeConverter.parseDateTime(assessment.getAssessmentDate()).getTime().before(start) || DatatypeConverter.parseDateTime(assessment.getAssessmentDate()).getTime().after(end))) {
                toReturn.add(assessment);
            }
        }
        return toReturn;
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
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientEntity other = (ClientEntity) obj;
        if (this.clientId == null || other.clientId == null) {
            return false;
        }
        if (!Objects.equals(this.clientId, other.clientId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.clientId);
        return hash;
    }
}

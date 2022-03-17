/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import util.enumeration.AssessmentReasonEnum;
import util.enumeration.AssessmentStatusEnum;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class AssessmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assessmentId;
    @Column(nullable = false, unique = true)
    @NotNull
    private long assessmentUniqueId;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @NotNull
    private Date assessmentDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private AssessmentStatusEnum status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private AssessmentReasonEnum reason;
    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date approvedDate;
    @Column(nullable = true)
    @Min(-1)
    @Max(3)
    private int loc;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private ClientEntity client;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private AssessorEntity assessor;
    @OneToMany // WARNING DO NOT SET THIS TO EAGER FETCHING, OUR DATA UPLOAD WILL CRASH
    private List<ResponseEntity> response;
    @OneToMany(mappedBy = "assessment")
    private List<CaretakerAssessmentEntity> caretakerAssessments;

    //to link with Response
    public AssessmentEntity() {
        this.response = new ArrayList<>();
        this.caretakerAssessments = new ArrayList<>();
    }

    public AssessmentEntity(long assessmentUniqueId) {
        this();
        this.assessmentUniqueId = assessmentUniqueId;
    }

    public AssessmentEntity(long assessmentUniqueId, Date assessmentDate, AssessmentStatusEnum status, AssessmentReasonEnum reason, Date approvedDate, int loc) {
        this();
        this.assessmentUniqueId = assessmentUniqueId;
        this.assessmentDate = assessmentDate;
        this.status = status;
        this.reason = reason;
        this.approvedDate = approvedDate;
        this.loc = loc;
    }

    public long getAssessmentUniqueId() {
        return assessmentUniqueId;
    }

    public void setAssessmentUniqueId(long assessmentUniqueId) {
        this.assessmentUniqueId = assessmentUniqueId;
    }

    public List<ResponseEntity> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseEntity> response) {
        this.response = response;
    }

    public AssessorEntity getAssessor() {
        return assessor;
    }

    public void setAssessor(AssessorEntity assessor) {
        this.assessor = assessor;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getAssessmentDate() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(this.assessmentDate);
        return nowAsISO;
    }
    
    public Date getRawAssessmentDate() {
        return assessmentDate;
    }
    
    public Date getRawApprovedDate() {
        return approvedDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public AssessmentStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatusEnum status) {
        this.status = status;
    }

    public AssessmentReasonEnum getReason() {
        return reason;
    }

    public void setReason(AssessmentReasonEnum reason) {
        this.reason = reason;
    }

    public String getApprovedDate() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        if (this.approvedDate != null) {
            String nowAsISO = df.format(this.approvedDate);
            return nowAsISO;
        } else {
            return null;
        }
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
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

    public List<CaretakerAssessmentEntity> getCaretakerAssessments() {
        return caretakerAssessments;
    }

    public void setCaretakerAssessments(List<CaretakerAssessmentEntity> caretakerAssessments) {
        this.caretakerAssessments = caretakerAssessments;
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
        final AssessmentEntity other = (AssessmentEntity) obj;
        if (this.assessmentId == null || other.assessmentId == null) {
            return false;
        }
        if (!Objects.equals(this.assessmentId, other.assessmentId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.assessmentId);
        return hash;
    }

    @Override
    public String toString() {
        return "AssessmentEntity{" + "assessmentUniqueId=" + assessmentUniqueId + '}';
    }

}

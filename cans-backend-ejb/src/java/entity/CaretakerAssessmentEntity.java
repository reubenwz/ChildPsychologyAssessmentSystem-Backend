/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import util.enumeration.CaretakerAlgorithmEnum;
import util.enumeration.CaretakerTypeEnum;

/**
 *
 * @author Ziyue
 */
@Entity
public class CaretakerAssessmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caretakerAssessmentId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CaretakerAlgorithmEnum levelOfNeeds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CaretakerTypeEnum caretakerType;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private CaretakerEntity caretaker;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private AssessmentEntity assessment;

    @OneToMany
    private List<ResponseEntity> caretakerResponses;

    public CaretakerAssessmentEntity() {
        this.caretakerResponses = new ArrayList<>();
    }

    public CaretakerAssessmentEntity(CaretakerAlgorithmEnum levelOfNeeds, CaretakerTypeEnum caretakerType) {
        this();
        this.levelOfNeeds = levelOfNeeds;
        this.caretakerType = caretakerType;
    }

    public CaretakerAssessmentEntity(CaretakerAlgorithmEnum levelOfNeeds, CaretakerTypeEnum caretakerType, CaretakerEntity caretaker, AssessmentEntity assessment) {
        this();
        this.levelOfNeeds = levelOfNeeds;
        this.caretakerType = caretakerType;
        this.caretaker = caretaker;
        this.assessment = assessment;
    }

    public Long getCaretakerAssessmentId() {
        return caretakerAssessmentId;
    }

    public void setCaretakerAssessmentId(Long caretakerAssessmentId) {
        this.caretakerAssessmentId = caretakerAssessmentId;
    }

    public CaretakerAlgorithmEnum getLevelOfNeeds() {
        return levelOfNeeds;
    }

    public void setLevelOfNeeds(CaretakerAlgorithmEnum levelOfNeeds) {
        this.levelOfNeeds = levelOfNeeds;
    }

    public CaretakerEntity getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(CaretakerEntity caretaker) {
        this.caretaker = caretaker;
    }

    public AssessmentEntity getAssessment() {
        return assessment;
    }

    public void setAssessment(AssessmentEntity assessment) {
        this.assessment = assessment;
    }

    public List<ResponseEntity> getCaretakerResponses() {
        return caretakerResponses;
    }

    public void setCaretakerResponses(List<ResponseEntity> caretakerResponses) {
        this.caretakerResponses = caretakerResponses;
    }

    public CaretakerTypeEnum getCaretakerType() {
        return caretakerType;
    }

    public void setCaretakerType(CaretakerTypeEnum caretakerType) {
        this.caretakerType = caretakerType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (caretakerAssessmentId != null ? caretakerAssessmentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the caretakerAssessmentId fields are not set
        if (!(object instanceof CaretakerAssessmentEntity)) {
            return false;
        }
        CaretakerAssessmentEntity other = (CaretakerAssessmentEntity) object;
        if (this.caretakerAssessmentId == null || other.caretakerAssessmentId == null) {
            return false;
        }
        if ((this.caretakerAssessmentId == null && other.caretakerAssessmentId != null) || (this.caretakerAssessmentId != null && !this.caretakerAssessmentId.equals(other.caretakerAssessmentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CaretakerAssessment[ id=" + caretakerAssessmentId + " ]";
    }
}

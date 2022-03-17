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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class AgeGroupEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ageGroupId;
    @Column(nullable = false)
    @NotNull
    private String ageRange;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private DomainEntity domain;
    @OneToMany(mappedBy = "ageGroup", fetch = FetchType.EAGER)
    private List<MainQuestionEntity> questions;

    public AgeGroupEntity() {
        questions = new ArrayList<>();
    }

    public AgeGroupEntity(String ageRange) {
        this();
        this.ageRange = ageRange;
    }

    public Long getAgeGroupId() {
        return ageGroupId;
    }

    public void setAgeGroupId(Long ageGroupId) {
        this.ageGroupId = ageGroupId;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public DomainEntity getDomain() {
        return domain;
    }

    public void setDomain(DomainEntity domain) {
        this.domain = domain;
    }

    public List<MainQuestionEntity> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MainQuestionEntity> questions) {
        this.questions = questions;
    }

}

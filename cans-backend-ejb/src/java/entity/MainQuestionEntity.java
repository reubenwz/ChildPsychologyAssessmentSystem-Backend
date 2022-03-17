/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class MainQuestionEntity extends QuestionEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private AgeGroupEntity ageGroup;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private SubModuleEntity subModule;

    public MainQuestionEntity() {
        super();
    }

    public MainQuestionEntity(String questionCode, String questionTitle) {
        super(questionCode, questionTitle);
    }

    public MainQuestionEntity(String questionCode, String questionTitle, List<String> questionDescription, List<String> questionToConsider, Map<Integer, String> ratingsDefinition) {
        super(questionCode, questionTitle, questionDescription, questionToConsider, ratingsDefinition);
    }

    public MainQuestionEntity(AgeGroupEntity ageGroup, SubModuleEntity submodule) {
        this.ageGroup = ageGroup;
        this.subModule = submodule;
    }

    public AgeGroupEntity getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroupEntity ageGroup) {
        this.ageGroup = ageGroup;
    }

    public SubModuleEntity getSubModule() {
        return subModule;
    }

    public void setSubModule(SubModuleEntity subModule) {
        this.subModule = subModule;
    }

}
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
public class SubQuestionEntity extends QuestionEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private SubModuleEntity submodule;

    public SubQuestionEntity() {
        super();
    }

    public SubQuestionEntity(String questionCode, String questionTitle) {
        super(questionCode, questionTitle);
    }

    public SubQuestionEntity(String questionCode, String questionTitle, List<String> questionDescription, List<String> questionToConsider, Map<Integer, String> ratingsDefinition) {
        super(questionCode, questionTitle, questionDescription, questionToConsider, ratingsDefinition);
    }

    public SubModuleEntity getSubmodule() {
        return submodule;
    }

    public void setSubmodule(SubModuleEntity submodule) {
        this.submodule = submodule;
    }

}
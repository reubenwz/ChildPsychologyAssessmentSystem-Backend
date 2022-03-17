/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class QuestionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;
    @Column(nullable = false)
    @NotNull
    private String questionCode;
    @Column(nullable = false)
    @NotNull
    private String questionTitle;
    @Column(nullable = true)
    private List<String> questionDescription;
    @Column(nullable = true)
    private List<String> questionToConsider;
    @Column(nullable = true)
    private Map<Integer, String> ratingsDefinition;

    public QuestionEntity() {
        questionDescription = new ArrayList<>();
        questionToConsider = new ArrayList<>();
        ratingsDefinition = new HashMap<>();
    }

    public QuestionEntity(String questionCode, String questionTitle) {
        this.questionCode = questionCode;
        this.questionTitle = questionTitle;
    }

    public QuestionEntity(String questionCode, String questionTitle, List<String> questionDescription, List<String> questionToConsider, Map<Integer, String> ratingsDefinition) {
        this();
        this.questionCode = questionCode;
        this.questionTitle = questionTitle;
        this.questionDescription = questionDescription;
        this.questionToConsider = questionToConsider;
        this.ratingsDefinition = ratingsDefinition;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public List<String> getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(List<String> questionDescription) {
        this.questionDescription = questionDescription;
    }

    public List<String> getQuestionToConsider() {
        return questionToConsider;
    }

    public void setQuestionToConsider(List<String> questionToConsider) {
        this.questionToConsider = questionToConsider;
    }

    public Map<Integer, String> getRatingsDefinition() {
        return ratingsDefinition;
    }

    public void setRatingsDefinition(Map<Integer, String> ratingsDefinition) {
        this.ratingsDefinition = ratingsDefinition;
    }

}
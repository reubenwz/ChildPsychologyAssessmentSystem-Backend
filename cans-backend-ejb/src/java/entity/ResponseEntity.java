/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class ResponseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long responseId;
    @Column(nullable = false)
    @NotNull
    @Min(-2)
    @Max(3)
    private int responseValue;
    @Column(nullable = true)
    private String responseNotes;

    @ManyToOne(fetch = FetchType.EAGER)
    private QuestionEntity question;

    public ResponseEntity() {
    }

    public ResponseEntity(int responseValue, String responseNotes) {
        this.responseValue = responseValue;
        this.responseNotes = responseNotes;
    }

    public Long getResponseId() {
        return responseId;
    }

    public void setResponseId(Long responseId) {
        this.responseId = responseId;
    }

    public int getResponseValue() {
        return responseValue;
    }

    public void setResponseValue(int responseValue) {
        this.responseValue = responseValue;
    }

    public String getResponseNotes() {
        return responseNotes;
    }

    public void setResponseNotes(String responseNotes) {
        this.responseNotes = responseNotes;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" + "responseId=" + responseId + ", responseValue=" + responseValue + ", responseNotes=" + responseNotes + ", question=" + question + '}';
    }

}

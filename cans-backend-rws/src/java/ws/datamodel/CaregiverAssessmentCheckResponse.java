/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ziyue
 */
public class CaregiverAssessmentCheckResponse {
    private Long caregiverId;
    private String caregiverName;
    private Boolean isCreated;
    private List<String> questionCodes;

    public CaregiverAssessmentCheckResponse() {
        this.questionCodes = new ArrayList<>();
    }
    
    public CaregiverAssessmentCheckResponse(Long caregiverId, String caregiverName, Boolean isCreated, List<String> questionCodes) {
        this();
        this.caregiverId = caregiverId;
        this.caregiverName = caregiverName;
        this.isCreated = isCreated;
        this.questionCodes = questionCodes;
    }

    public Long getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(Long caregiverId) {
        this.caregiverId = caregiverId;
    }

    public String getCaregiverName() {
        return caregiverName;
    }

    public void setCaregiverName(String caregiverName) {
        this.caregiverName = caregiverName;
    }

    public Boolean getIsCreated() {
        return isCreated;
    }

    public void setIsCreated(Boolean isCreated) {
        this.isCreated = isCreated;
    }

    public List<String> getQuestionCodes() {
        return questionCodes;
    }

    public void setQuestionCodes(List<String> questionCodes) {
        this.questionCodes = questionCodes;
    }
}

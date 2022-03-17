/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.List;

/**
 *
 * @author Ong Bik Jeun
 */
public class AssessmentNameResponse {

    List<String> assessmentsReasons;

    public AssessmentNameResponse() {
    }

    public AssessmentNameResponse(List<String> assessmentsReasons) {
        this.assessmentsReasons = assessmentsReasons;
    }

    public List<String> getAssessmentsReasons() {
        return assessmentsReasons;
    }

    public void setAssessmentsReasons(List<String> assessmentsReasons) {
        this.assessmentsReasons = assessmentsReasons;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.List;

/**
 *
 * @author Ziyue
 */
public class CaregiverAlgorithmResponse {

    private List<String> caretakerAlgorithms;

    public CaregiverAlgorithmResponse() {
    }

    public CaregiverAlgorithmResponse(List<String> caretakerAlgorithms) {
        this.caretakerAlgorithms = caretakerAlgorithms;
    }   

    public List<String> getCaretakerAlgorithms() {
        return caretakerAlgorithms;
    }

    public void setCaretakerAlgorithms(List<String> caretakerAlgorithms) {
        this.caretakerAlgorithms = caretakerAlgorithms;
    }

}
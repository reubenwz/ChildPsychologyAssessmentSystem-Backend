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
public class CaregiverTypeResponse {

    private List<String> caretakerTypes;

    public CaregiverTypeResponse() {
    }

    public CaregiverTypeResponse(List<String> caretakerTypes) {
        this.caretakerTypes = caretakerTypes;
    }

    public List<String> getCaretakerTypes() {
        return caretakerTypes;
    }

    public void setCaretakerTypes(List<String> caretakerTypes) {
        this.caretakerTypes = caretakerTypes;
    }


}
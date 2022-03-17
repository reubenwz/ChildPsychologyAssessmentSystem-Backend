/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ziyue
 */
@XmlRootElement
public class CaregiverResponsesDetailBody {
    @XmlElement
    public Long caretaker_assessment_id;
    @XmlElement
    public List<String> question_codes;
    @XmlElement
    public List<Integer> response_values;
    @XmlElement
    public List<String> response_notes;
}
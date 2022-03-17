/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ziyue
 */
@XmlRootElement
public class CaregiverAssessmentDetailBody {
    @XmlElement
    public Long caretaker_id;
    @XmlElement
    public Long assessment_id;
    @XmlElement
    public String caretaker_type;
}
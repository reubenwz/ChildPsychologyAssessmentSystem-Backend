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
 * @author Ooi Jun Hao
 */
@XmlRootElement
public class AssessmentStatusUpdateBody {
    @XmlElement
    public String status;
}

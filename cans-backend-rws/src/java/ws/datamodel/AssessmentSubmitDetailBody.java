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
public class AssessmentSubmitDetailBody {
    @XmlElement
    public Long assessment_id;
}

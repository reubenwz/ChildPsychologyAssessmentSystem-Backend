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
 * @author Ong Bik Jeun
 */
@XmlRootElement
public class AssessorDetailBody {

    @XmlElement
    public Long assessorId;
    @XmlElement
    public String email;
    @XmlElement
    public String name;
    @XmlElement
    public String password;
    @XmlElement
    public boolean root;
    @XmlElement
    public Long organisation_id;
    @XmlElement
    public Long supervisor_id;

}

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
 * @author Ooi Jun Hao
 */
@XmlRootElement
public class OrganisationDetailBody {
    @XmlElement
    public String name;
    @XmlElement
    public List<String> organisationTypes;
    @XmlElement
    public String adminEmail;
    @XmlElement
    public String adminName;
}

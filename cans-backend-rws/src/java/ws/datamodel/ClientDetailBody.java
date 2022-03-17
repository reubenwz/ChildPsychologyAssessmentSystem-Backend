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
public class ClientDetailBody {
    @XmlElement
    public Long clientUniqueId;
    @XmlElement
    public String name;
    @XmlElement
    public String idNumber;
    @XmlElement
    public String gender;
    @XmlElement
    public String dob;
    @XmlElement
    public String address;
    @XmlElement
    public String ethnicity;
    @XmlElement
    public String admissionType;
    @XmlElement
    public String placementType;
    @XmlElement
    public String accommodationStatus;
    @XmlElement
    public String accommodationType;
    @XmlElement
    public String educationLevel;
    @XmlElement
    public String currentOccupation;
    @XmlElement
    public int monthlyIncome;
    @XmlElement
    public String assessorEmail;
}

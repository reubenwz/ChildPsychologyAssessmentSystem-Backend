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
public class CertificationDetailBody {

    @XmlElement
    public String dateOfCert;
    @XmlElement
    public String vignette;
    @XmlElement
    public double recentScore;
    @XmlElement
    public int noOfTimesRecertified;

}

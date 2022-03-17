/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
@Cacheable(false)
public class CertificationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificationId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date dateOfCert;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String vignette;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    private double recentScore;
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private int noOfTimesRecertified;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private AssessorEntity assessor;

    public CertificationEntity() {
    }

    public CertificationEntity(Date dateOfCert, String vignette, double recentScore, int noOfTimesRecertified) {
        this.dateOfCert = dateOfCert;
        this.vignette = vignette;
        this.recentScore = recentScore;
        this.noOfTimesRecertified = noOfTimesRecertified;
    }

    public AssessorEntity getAssessor() {
        return assessor;
    }

    public void setAssessor(AssessorEntity assessor) {
        this.assessor = assessor;
    }

    public Long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }

    public String getDateOfCert() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(dateOfCert);
        return nowAsISO;
    }
    
    public Date getRawDateOfCert() {
        return this.dateOfCert;
    }

    public void setDateOfCert(Date dateOfCert) {
        this.dateOfCert = dateOfCert;
    }

    public String getVignette() {
        return vignette;
    }

    public void setVignette(String vignette) {
        this.vignette = vignette;
    }

    public double getRecentScore() {
        return recentScore;
    }

    public void setRecentScore(double recentScore) {
        this.recentScore = recentScore;
    }

    public int getNoOfTimesRecertified() {
        return noOfTimesRecertified;
    }

    public void setNoOfTimesRecertified(int noOfTimesRecertified) {
        this.noOfTimesRecertified = noOfTimesRecertified;
    }

}

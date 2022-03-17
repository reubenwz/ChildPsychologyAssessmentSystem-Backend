/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class DomainEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long domainId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String domainName;
    @Column(nullable = true)
    private List<String> domainDescription;
    @Column(nullable = false)
    @NotNull
    private boolean module;
    @Column(nullable = false)
    @NotNull
    private int version;
    @Column(nullable = false)
    @NotNull
    private boolean caregiverDomain;

    @OneToMany(mappedBy = "domain", fetch = FetchType.EAGER)
    private List<AgeGroupEntity> ageGroups;

    public DomainEntity() {
        ageGroups = new ArrayList<>();
        domainDescription = new ArrayList<>();
    }

    public DomainEntity(String domainName, List<String> domainDescription, boolean module, int version, boolean caregiverDomain) {
        this();
        this.domainName = domainName;
        this.domainDescription = domainDescription;
        this.module = module;
        this.version = version;
        this.caregiverDomain = caregiverDomain;
    }    

    public List<String> getDomainDescription() {
        return domainDescription;
    }

    public void setDomainDescription(List<String> domainDescription) {
        this.domainDescription = domainDescription;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public List<AgeGroupEntity> getAgeGroups() {
        return ageGroups;
    }

    public void setAgeGroups(List<AgeGroupEntity> ageGroups) {
        this.ageGroups = ageGroups;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isCaregiverDomain() {
        return caregiverDomain;
    }

    public void setCaregiverDomain(boolean caregiverDomain) {
        this.caregiverDomain = caregiverDomain;
    }

}
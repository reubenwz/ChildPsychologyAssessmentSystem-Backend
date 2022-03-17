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
import util.enumeration.AgencyTypeEnum;

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class OrganisationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organisationId;
    @Column(nullable = false, unique = true)
    @NotNull
    private String name;
    @Column(nullable = false)
    @NotNull
    private List<AgencyTypeEnum> organisationTypes;

    @OneToMany(mappedBy = "organisation", fetch = FetchType.EAGER)
    private List<AssessorEntity> assessors;

    public OrganisationEntity() {
        assessors = new ArrayList<>();
        organisationTypes = new ArrayList<>();
    }

    public OrganisationEntity(String name, List<AgencyTypeEnum> organisationTypes) {
        this();
        this.name = name;
        this.organisationTypes = organisationTypes;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssessorEntity> getAssessors() {
        return assessors;
    }

    public void setAssessors(List<AssessorEntity> assessors) {
        this.assessors = assessors;
    }

    public List<AgencyTypeEnum> getOrganisationTypes() {
        return organisationTypes;
    }

    public void setOrganisationTypes(List<AgencyTypeEnum> organisationTypes) {
        this.organisationTypes = organisationTypes;
    }

}

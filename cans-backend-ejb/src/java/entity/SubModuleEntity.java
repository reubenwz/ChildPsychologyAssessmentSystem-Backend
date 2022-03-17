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

/**
 *
 * @author Ong Bik Jeun
 */
@Entity
public class SubModuleEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subModuleId;
    @Column(nullable = false)
    @NotNull
    private String subModuleName;
    @Column(nullable = true)
    private List<String> subModuleDescription;
    @Column(nullable = false)
    @NotNull
    private Boolean isInfo;

    @OneToMany(mappedBy = "submodule", fetch = FetchType.EAGER)
    private List<SubQuestionEntity> subQues;
    @OneToMany(mappedBy = "subModule", fetch = FetchType.EAGER)
    private List<MainQuestionEntity> ques;

    public SubModuleEntity() {
        subQues = new ArrayList<>();
        ques = new ArrayList<>();
    }

    public SubModuleEntity(String subModuleName, List<String> subModuleDescription, Boolean isInfo) {
        this();
        this.subModuleName = subModuleName;
        this.subModuleDescription = subModuleDescription;
        this.isInfo = isInfo;
    }

    public List<String> getSubModuleDescription() {
        return subModuleDescription;
    }

    public void setSubModuleDescription(List<String> subModuleDescription) {
        this.subModuleDescription = subModuleDescription;
    }

    public Long getSubModuleId() {
        return subModuleId;
    }

    public void setSubModuleId(Long subModuleId) {
        this.subModuleId = subModuleId;
    }

    public String getSubModuleName() {
        return subModuleName;
    }

    public void setSubModuleName(String subModuleName) {
        this.subModuleName = subModuleName;
    }

    public List<SubQuestionEntity> getSubQues() {
        return subQues;
    }

    public void setSubQues(List<SubQuestionEntity> subQues) {
        this.subQues = subQues;
    }

    public List<MainQuestionEntity> getQues() {
        return ques;
    }

    public void setQues(List<MainQuestionEntity> ques) {
        this.ques = ques;
    }

    public Boolean getIsInfo() {
        return isInfo;
    }

    public void setIsInfo(Boolean isInfo) {
        this.isInfo = isInfo;
    }

}

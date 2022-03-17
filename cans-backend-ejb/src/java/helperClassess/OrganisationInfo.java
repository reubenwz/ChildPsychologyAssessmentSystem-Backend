/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperClassess;

/**
 *
 * @author Ong Bik Jeun
 */
public class OrganisationInfo {

    private String orgName;
    private int numOfClients;
    private int numOfAssessors = 0;
    private int numofAssessments = 0;

    public OrganisationInfo(String orgName, int numOfClients) {
        this.orgName = orgName;
        this.numOfClients = numOfClients;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getNumOfClients() {
        return numOfClients;
    }

    public void setNumOfClients(int numOfClients) {
        this.numOfClients = numOfClients;
    }

    public int getNumOfAssessors() {
        return numOfAssessors;
    }

    public void setNumOfAssessors(int numOfAssessors) {
        this.numOfAssessors = numOfAssessors;
    }

    public int getNumofAssessments() {
        return numofAssessments;
    }

    public void setNumofAssessments(int numofAssessments) {
        this.numofAssessments = numofAssessments;
    }

    public void incrementNumOfAssessments() {
        this.numofAssessments++;
    }

    public void incrementNumOfAssessors() {
        this.numOfAssessors++;
    }

}

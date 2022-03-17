/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import helperClassess.OrganisationInfo;
import java.util.List;

/**
 *
 * @author Ong Bik Jeun
 */
public class BubbleChartResponse {

    private String title;
    private List<OrganisationInfo> orgInfo;
    private String startDate;
    private String endDate;

    public BubbleChartResponse() {
    }

    public BubbleChartResponse(String title, List<OrganisationInfo> orgInfo, String startDate, String endDate) {
        this.title = title;
        this.orgInfo = orgInfo;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<OrganisationInfo> getOrgInfo() {
        return orgInfo;
    }

    public void setOrgInfo(List<OrganisationInfo> orgInfo) {
        this.orgInfo = orgInfo;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}

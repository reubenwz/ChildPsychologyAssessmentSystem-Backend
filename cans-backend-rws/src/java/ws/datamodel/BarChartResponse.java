/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.List;

/**
 *
 * @author Ooi Jun Hao
 */
public class BarChartResponse {

    private String title;
    private List<String> labels;
    private List<Double> data;
    private String dataLabel;
    private String startDate;
    private String endDate;
    private String ageGroup;

    public BarChartResponse() {
    }

    public BarChartResponse(String title, List<String> labels, List<Double> data, String dataLabel, String startDate, String endDate, String ageGroup) {
        this.title = title;
        this.labels = labels;
        this.data = data;
        this.dataLabel = dataLabel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ageGroup = ageGroup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(String dataLabel) {
        this.dataLabel = dataLabel;
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

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    } 

}

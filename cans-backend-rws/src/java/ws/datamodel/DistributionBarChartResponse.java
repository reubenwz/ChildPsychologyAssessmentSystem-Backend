/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.List;

/**
 *
 * @author Ong Bik Jeun
 */
public class DistributionBarChartResponse {

    private String title;
    private List<String> labels;
    private List<List<Integer>> data;
    private String dataLabel;

    public DistributionBarChartResponse() {
    }

    public DistributionBarChartResponse(String title, List<String> labels, List<List<Integer>> data, String dataLabel) {
        this.title = title;
        this.labels = labels;
        this.data = data;
        this.dataLabel = dataLabel;
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

    public List<List<Integer>> getData() {
        return data;
    }

    public void setData(List<List<Integer>> data) {
        this.data = data;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public void setDataLabel(String dataLabel) {
        this.dataLabel = dataLabel;
    }

}

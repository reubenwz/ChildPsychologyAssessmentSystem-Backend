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
public class LineChartResponse {

    private String title;
    private List<Integer> data;
    private List<Double> labels;

    public LineChartResponse() {
    }

    public LineChartResponse(String title, List<Integer> data, List<Double> labels) {
        this.title = title;
        this.data = data;
        this.labels = labels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

    public List<Double> getLabels() {
        return labels;
    }

    public void setLabels(List<Double> labels) {
        this.labels = labels;
    }

}

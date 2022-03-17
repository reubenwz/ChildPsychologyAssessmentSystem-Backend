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
public class PieChartResponse {
    private String title;
    private List<Long> data;
    private List<String> labels;

    public PieChartResponse() {
    }

    public PieChartResponse(String title, List<Long> data, List<String> labels) {
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

    public List<Long> getData() {
        return data;
    }

    public void setData(List<Long> data) {
        this.data = data;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.CertificationEntity;
import java.util.List;

/**
 *
 * @author Ong Bik Jeun
 */
public class CertificationExpiryResponse {

    private String title;
    private List<CertificationEntity> data;
    private List<String> labels;

    public CertificationExpiryResponse() {
    }

    public CertificationExpiryResponse(String title, List<CertificationEntity> data, List<String> labels) {
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

    public List<CertificationEntity> getData() {
        return data;
    }

    public void setData(List<CertificationEntity> data) {
        this.data = data;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

}

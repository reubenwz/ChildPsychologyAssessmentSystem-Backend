/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.UploadEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ooi Jun Hao
 */
public class DataUploadsResponse {
    
    private int per_page;
    private int current_page;
    private int last_page;
    private int total_records;
    private List<UploadEntity> uploads;

    public DataUploadsResponse() {
        uploads = new ArrayList<>();
    }

    public DataUploadsResponse(int per_page, int last_page, int total_records) {
        this();
        this.per_page = per_page;
        this.last_page = last_page;
        this.total_records = total_records;
    }

    public DataUploadsResponse(int per_page, int current_page, int last_page, int total_records, List<UploadEntity> uploads) {
        this.per_page = per_page;
        this.current_page = current_page;
        this.last_page = last_page;
        this.total_records = total_records;
        this.uploads = uploads;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public int getTotal_records() {
        return total_records;
    }

    public void setTotal_records(int total_records) {
        this.total_records = total_records;
    }

    public List<UploadEntity> getUploads() {
        return uploads;
    }

    public void setUploads(List<UploadEntity> uploads) {
        this.uploads = uploads;
    }
    
    
}

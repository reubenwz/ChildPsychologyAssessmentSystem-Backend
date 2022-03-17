/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ooi Jun Hao
 */
public class AssessorEmailsResponse {

    public class Item {

        private String label;
        private String value;

        public Item() {
        }

        public Item(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
    private String label; // organisation name
    private String value;
    private List<Item> items; //emails

    public AssessorEmailsResponse() {
        this.items = new ArrayList<>();
    }

    public AssessorEmailsResponse(String label, String value) {
        this();
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    
    public void addItem(String email) {
        this.items.add(new Item(email, email));
    }

}

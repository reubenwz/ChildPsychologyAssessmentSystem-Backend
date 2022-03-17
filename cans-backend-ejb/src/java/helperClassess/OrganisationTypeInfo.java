/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperClassess;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ong Bik Jeun
 */
public class OrganisationTypeInfo {

    String type;
    List<String> organisations;

    public OrganisationTypeInfo() {
        this.organisations = new ArrayList<>();
    }

    public OrganisationTypeInfo(String type) {
        this();
        this.type = type;
    }

    public OrganisationTypeInfo(String type, List<String> organisations) {
        this();
        this.type = type;
        this.organisations = organisations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<String> organisations) {
        this.organisations = organisations;
    }

}

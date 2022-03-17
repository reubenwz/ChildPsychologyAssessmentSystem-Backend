/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import helperClassess.OrganisationTypeInfo;
import java.util.List;

/**
 *
 * @author Ooi Jun Hao
 */
public class OrganisationNameResponse {

    private List<OrganisationTypeInfo> organisations;

    public OrganisationNameResponse() {
    }

    public OrganisationNameResponse(List<OrganisationTypeInfo> organisations) {
        this.organisations = organisations;
    }

    public List<OrganisationTypeInfo> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<OrganisationTypeInfo> organisations) {
        this.organisations = organisations;
    }

}

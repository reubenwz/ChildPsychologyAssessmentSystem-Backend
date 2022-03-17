/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessorEntity;
import entity.ClientEntity;
import entity.OrganisationEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AssessorExistsException;
import util.exception.InputDataValidationException;
import util.exception.OrganisationExistsException;
import util.exception.OrganisationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface OrganisationSessionBeanLocal {

    public List<OrganisationEntity> retrieveAllOrganisation();

    public List<ClientEntity> retrieveAllClientsInOrganisation(Long orgId);

    public OrganisationEntity retrieveOrganisationByName(String name) throws OrganisationNotFoundException;

    public OrganisationEntity createNewOrganisation(OrganisationEntity organisation) throws UnknownPersistenceException, OrganisationExistsException, InputDataValidationException;

    public List<OrganisationEntity> retrieveAllOrganisationByType(String type);

    public List<String> retrieveAllOrganisationNames();

    public List<String> retrieveAllAssessorEmailsByOrganisationName(String name) throws OrganisationNotFoundException;

    public OrganisationEntity retrieveOrgById(Long id) throws OrganisationNotFoundException;

    public OrganisationEntity createNewOrganisationWithOrgAdmin(OrganisationEntity organisation, AssessorEntity ass) throws AssessorExistsException, UnknownPersistenceException, InputDataValidationException, OrganisationExistsException;

    public void removeOrganisation(long orgId) throws OrganisationNotFoundException;

    public List<AssessorEntity> retrieveRootFromOrg(Long orgId);

    public List<AssessorEntity> retrieveSupervisorFromOrg(Long orgId);

    public List<AssessorEntity> retrieveCaseworkerFromOrg(Long orgId);

}

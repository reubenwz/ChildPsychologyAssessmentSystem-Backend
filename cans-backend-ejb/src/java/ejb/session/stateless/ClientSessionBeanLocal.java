/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ClientEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.ClientExistsException;
import util.exception.ClientNotFoundException;
import util.exception.ClientUpdateException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface ClientSessionBeanLocal {

    public List<ClientEntity> retrieveAllClientEntities();

    public Long createNewClient(ClientEntity clientEntity) throws ClientExistsException, UnknownPersistenceException, InputDataValidationException;

    public boolean clientInDatabase(Long clientId);

    public ClientEntity retrieveClientById(Long clientId) throws ClientNotFoundException;

    public ClientEntity retrieveClientByUniqueId(Long clientUniqueId) throws ClientNotFoundException;

    public List<ClientEntity> retreiveClientsByAgeGroup(String ageGroup);

    public List<ClientEntity> retrieveAllClientsInOrg(Long orgId);

    public void updateClientDetails(long cliendId, long newClientUniqueID, String newName, String newIdNumber, String newGender, Date newDOB, String newAddress, String newEthnicity, String newAdmissionType, String newPlacementType, String newAccommodationStatus, String newAccommodationType, String newEducationLevel, String newCurrentOccupation, int newMonthlyIncome, String newAssessorEmail) throws ClientNotFoundException, ClientUpdateException, UserNotFoundException;

    public void deleteClient(long clientId) throws ClientNotFoundException;

    public ClientEntity createNewClient(ClientEntity clientEntity, String assessorEmail) throws ClientExistsException, UnknownPersistenceException, InputDataValidationException, UserNotFoundException;

    public List<String> retrieveUniqueEthnicityOfClients();

    public List<String> retrieveUniqueGenderOfClients();

    public List<ClientEntity> retrieveClientsByEnthnicity(String race);

    public List<ClientEntity> retrieveClientsByGender(String gender);

    public List<ClientEntity> retrieveClientsByAgeGroupInOrg(Long orgId, String ageGroup);

    public List<ClientEntity> retrieveClientsByGender(Long orgId, String gender);

    public List<ClientEntity> retrieveClientsByEnthnicityInOrg(Long orgId, String race);

    public List<ClientEntity> retrieveClientByAssessor(Long assId);

    public List<ClientEntity> retreiveUnassignedClientsInOrg(Long orgId);

}

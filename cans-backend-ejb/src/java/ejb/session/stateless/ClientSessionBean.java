/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CaretakerAssessmentEntity;
import entity.CaretakerEntity;
import entity.ClientEntity;
import entity.ResponseEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.ClientExistsException;
import util.exception.ClientNotFoundException;
import util.exception.ClientUpdateException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 * @author Wang Ziyue
 * @author Ong Bik Jeun
 */
@Stateless
public class ClientSessionBean implements ClientSessionBeanLocal {

    @EJB
    private OrganisationSessionBeanLocal organisationSessionBean;

    @EJB
    private AssessorSessionBeanLocal assessorSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ClientSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.getValidator();
    }

    @Override
    public Long createNewClient(ClientEntity clientEntity) throws ClientExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ClientEntity>> constraintViolations = validator.validate(clientEntity);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(clientEntity);
                em.flush();
                return clientEntity.getClientId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ClientExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (ConstraintViolationException e) {
                Logger.getLogger(ClientSessionBean.class.getName()).log(Level.SEVERE, "Exception: ");
                e.getConstraintViolations().forEach(err -> Logger.getLogger(ClientSessionBean.class.getName()).log(Level.SEVERE, err.toString()));
                throw new InputDataValidationException();
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public ClientEntity createNewClient(ClientEntity clientEntity, String assessorEmail) throws ClientExistsException, UnknownPersistenceException, InputDataValidationException, UserNotFoundException { // user not found == assessor does not exist
        Set<ConstraintViolation<ClientEntity>> constraintViolations = validator.validate(clientEntity);
        AssessorEntity assessor = assessorSessionBean.retrieveUserByEmail(assessorEmail);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(clientEntity);
                clientEntity.setAssessor(assessor);
                assessor.getClients().add(clientEntity);
                em.flush();
                return clientEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ClientExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (ConstraintViolationException e) { // not sure what this is catching but sure
                Logger.getLogger(ClientSessionBean.class.getName()).log(Level.SEVERE, "Exception: ");
                e.getConstraintViolations().forEach(err -> Logger.getLogger(ClientSessionBean.class.getName()).log(Level.SEVERE, err.toString()));
                throw new InputDataValidationException("Client Unique ID already exists.");
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public boolean clientInDatabase(Long clientId) {
        ClientEntity client = em.find(ClientEntity.class, clientId);
        return client != null;
    }

    @Override
    public ClientEntity retrieveClientById(Long clientId) throws ClientNotFoundException {

        ClientEntity clientEntity = em.find(ClientEntity.class, clientId);
        if (clientEntity == null) {
            throw new ClientNotFoundException("ClientNotFoundException: Client with id " + clientId + " does not exist!");
        }

        return clientEntity;
    }

    @Override
    public ClientEntity retrieveClientByUniqueId(Long clientUniqueId) throws ClientNotFoundException {
        Query query = em.createQuery("SELECT a FROM ClientEntity a WHERE a.clientUniqueId=:uniqueId");
        query.setParameter("uniqueId", clientUniqueId);
        try {
            return (ClientEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ClientNotFoundException();
        }
    }

    @Override
    public List<ClientEntity> retrieveAllClientEntities() {
        Query query = em.createNamedQuery("retrieveAllClients");
        return query.getResultList();
    }

    @Override
    public List<ClientEntity> retreiveClientsByAgeGroup(String ageGroup) {
        List<ClientEntity> allClients = retrieveAllClientEntities();
        List<ClientEntity> requestedAge = new ArrayList<>();

        String[] ageGroupArr = ageGroup.split("-");
        int lower = Integer.parseInt(ageGroupArr[0]);
        int upper = Integer.parseInt(ageGroupArr[1]);

        for (ClientEntity client : allClients) {
            int age = client.getAge();
            if (age >= lower && age <= upper) {
                requestedAge.add(client);
            }
        }
        return requestedAge;
    }

    @Override
    public List<ClientEntity> retrieveClientsByAgeGroupInOrg(Long orgId, String ageGroup) {
        List<ClientEntity> allClients = organisationSessionBean.retrieveAllClientsInOrganisation(orgId);
        List<ClientEntity> requestedAge = new ArrayList<>();

        String[] ageGroupArr = ageGroup.split("-");
        int lower = Integer.parseInt(ageGroupArr[0]);
        int upper = Integer.parseInt(ageGroupArr[1]);

        for (ClientEntity client : allClients) {
            int age = client.getAge();
            if (age >= lower && age <= upper) {
                requestedAge.add(client);
            }
        }
        return requestedAge;

    }

    @Override
    public void updateClientDetails(long cliendId,
            long newClientUniqueID,
            String newName,
            String newIdNumber,
            String newGender,
            Date newDOB,
            String newAddress,
            String newEthnicity,
            String newAdmissionType,
            String newPlacementType,
            String newAccommodationStatus,
            String newAccommodationType,
            String newEducationLevel,
            String newCurrentOccupation,
            int newMonthlyIncome,
            String newAssessorEmail) throws ClientNotFoundException, ClientUpdateException, UserNotFoundException { // might have to differentiate userNotFoundException next time
        ClientEntity client = this.retrieveClientById(cliendId);
        AssessorEntity oldAssessor = client.getAssessor();
        AssessorEntity newAssessor = this.assessorSessionBean.retrieveUserByEmail(newAssessorEmail);
        try {
            client.setClientUniqueId(newClientUniqueID);
            client.setName(newName);
            client.setIdNumber(newIdNumber);
            client.setGender(newGender);
            client.setDob(newDOB);
            client.setAddress(newAddress);
            client.setEthnicity(newEthnicity);
            client.setAdmissionType(newAdmissionType);
            client.setPlacementType(newPlacementType);
            client.setAccommodationStatus(newAccommodationStatus);
            client.setAccommodationType(newAccommodationType);
            client.setEducationLevel(newEducationLevel);
            client.setCurrentOccupation(newCurrentOccupation);
            client.setMonthlyIncome(newMonthlyIncome);
            client.setAssessor(newAssessor);
            newAssessor.getClients().add(client);
            oldAssessor.getClients().remove(client); // this will work because client id has already been set
            em.flush();
        } catch (PersistenceException ex) {
            throw new ClientUpdateException();
        }
    }

    @Override
    public List<ClientEntity> retrieveAllClientsInOrg(Long orgId) {
        Query query = em.createQuery("SELECT c FROM ClientEntity c WHERE c.assessor.organisation.organisationId =:orgId");
        query.setParameter("orgId", orgId);

        return query.getResultList();
    }

    @Override
    public List<String> retrieveUniqueEthnicityOfClients() {
        Query query = em.createQuery("SELECT DISTINCT c.ethnicity FROM ClientEntity c");
        return query.getResultList();
    }

    @Override
    public List<String> retrieveUniqueGenderOfClients() {
        Query query = em.createQuery("SELECT DISTINCT c.gender FROM ClientEntity c");
        return query.getResultList();
    }

    @Override
    public List<ClientEntity> retrieveClientsByEnthnicity(String race) {
        Query query = em.createQuery(("SELECT c FROM ClientEntity c WHERE c.ethnicity = :race"));
        query.setParameter("race", race);

        return query.getResultList();
    }

    @Override
    public List<ClientEntity> retrieveClientsByEnthnicityInOrg(Long orgId, String race) {
        Query query = em.createQuery(("SELECT c FROM ClientEntity c WHERE c.ethnicity = :race AND c.assessor.organisation.organisationId = :orgId"));
        query.setParameter("race", race);
        query.setParameter("orgId", orgId);

        return query.getResultList();
    }

    @Override
    public List<ClientEntity> retrieveClientsByGender(String gender) {
        Query query = em.createQuery("SELECT c FROM ClientEntity c WHERE c.gender = :gender");
        query.setParameter("gender", gender);

        return query.getResultList();
    }

    @Override
    public List<ClientEntity> retrieveClientsByGender(Long orgId, String gender) {
        Query query = em.createQuery("SELECT c FROM ClientEntity c WHERE c.gender = :gender AND c.assessor.organisation.organisationId = :orgId");
        query.setParameter("gender", gender);
        query.setParameter("orgId", orgId);

        return query.getResultList();
    }

    @Override
    public List<ClientEntity> retrieveClientByAssessor(Long assId) {
        Query query = em.createQuery("SELECT c FROM ClientEntity c WHERE c.assessor.assessorId = :assId");
        query.setParameter("assId", assId);

        return query.getResultList();
    }

    @Override
    public List<ClientEntity> retreiveUnassignedClientsInOrg(Long orgId) {
        List<ClientEntity> allClients = retrieveAllClientsInOrg(orgId);
        List<ClientEntity> results = new ArrayList<>();
        for (ClientEntity client : allClients) {
            if (client.getAssessor() == null) {
                results.add(client);
            }
        }

        return results;
    }

    @Override
    public void deleteClient(long clientId) throws ClientNotFoundException {
        ClientEntity clientToRemove = this.retrieveClientById(clientId);
        em.remove(clientToRemove);
        clientToRemove.getAssessor().getClients().remove(clientToRemove);
        for (CaretakerEntity caretaker : clientToRemove.getCaretakers()) {
            em.remove(caretaker);
            for (CaretakerAssessmentEntity cta : caretaker.getCaretakerAssessments()) {
                em.remove(cta);
                for (ResponseEntity res : cta.getCaretakerResponses()) {
                    em.remove(res);
                }
            }
        }
        for (AssessmentEntity assessment : clientToRemove.getAssessment()) {
            assessment.getAssessor().getAssessments().remove(assessment);
            em.remove(assessment);
            for (ResponseEntity res : assessment.getResponse()) {
                em.remove(res);
            }
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ClientEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

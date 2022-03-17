/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CaretakerAssessmentEntity;
import entity.CaretakerEntity;
import entity.ClientEntity;
import entity.ResponseEntity;
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
import util.exception.CaretakerExistsException;
import util.exception.CaretakerNotFoundException;
import util.exception.ClientNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ziyue
 */
@Stateless
public class CaretakerSessionBean implements CaretakerSessionBeanLocal {

    @EJB
    private ClientSessionBeanLocal clientSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CaretakerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.getValidator();
    }

    @Override
    public Long createNewCaretaker(CaretakerEntity caretakerEntity, Long clientId) throws CaretakerExistsException, UnknownPersistenceException, InputDataValidationException, ClientNotFoundException {
        if (clientId == null) {
            throw new InputDataValidationException("InputDataValidationException: Invalid client id!");
        }
        ClientEntity client = clientSessionBean.retrieveClientById(clientId);
        Set<ConstraintViolation<CaretakerEntity>> constraintViolations = validator.validate(caretakerEntity);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(caretakerEntity);
                caretakerEntity.setClient(client);
                client.addCaretaker(caretakerEntity);
                em.flush();
                return caretakerEntity.getCaretakerId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CaretakerExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (ConstraintViolationException e) {
                Logger.getLogger(CaretakerSessionBean.class.getName()).log(Level.SEVERE, "Exception: ");
                e.getConstraintViolations().forEach(err -> Logger.getLogger(ClientSessionBean.class.getName()).log(Level.SEVERE, err.toString()));
                throw new InputDataValidationException();
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public boolean caretakerInDatabase(Long caretakerId) {
        CaretakerEntity caretaker = em.find(CaretakerEntity.class, caretakerId);
        if (caretaker != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CaretakerEntity retrieveCaretakerById(Long caretakerId) throws CaretakerNotFoundException {

        CaretakerEntity caretakerEntity = em.find(CaretakerEntity.class, caretakerId);
        if (caretakerEntity == null) {
            throw new CaretakerNotFoundException("CaretakerNotFoundException: Caretaker with id " + caretakerId + " does not exist!");
        }
        return caretakerEntity;
    }

    @Override
    public CaretakerEntity retrieveCaretakerByUniqueId(Long caretakerId) throws CaretakerNotFoundException {
        Query query = em.createQuery("SELECT a FROM CaretakerEntity a WHERE a.caretakerUniqueId=:uniqueId");
        query.setParameter("uniqueId", caretakerId);
        try {
            return (CaretakerEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CaretakerNotFoundException();
        }
    }

    //overloaded to facilitate lack of caretaker unique number
    @Override
    public CaretakerEntity retrieveCaretakerByIdNumber(String idNumber) throws CaretakerNotFoundException {
        try {
            Query query = em.createQuery("select c from CaretakerEntity c where c.idNumber =:inIdNumber");
            query.setParameter("inIdNumber", idNumber);
            return (CaretakerEntity) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new CaretakerNotFoundException();
        }

    }

    @Override
    public List<CaretakerEntity> retrieveAllCaretakerEntities() {
        Query query = em.createNamedQuery("retrieveAllCaretakers");
        return query.getResultList();
    }

    // removing everything for the time being
    @Override
    public void deleteCaretaker(long caretakerId) throws CaretakerNotFoundException {
        CaretakerEntity caretaker = retrieveCaretakerById(caretakerId);
        em.remove(caretaker);
        caretaker.getClient().getCaretakers().remove(caretaker);
        for (CaretakerAssessmentEntity cta : caretaker.getCaretakerAssessments()) {
            em.remove(cta);
            cta.getAssessment().getCaretakerAssessments().remove(cta);
            for (ResponseEntity res : cta.getCaretakerResponses()) {
                em.remove(res);
            }
        }
        em.flush();
    }
    
    @Override
    public boolean updateActiveStatus(long caretakerId) throws CaretakerNotFoundException {
        CaretakerEntity caretaker = retrieveCaretakerById(caretakerId);
        caretaker.setActive(!caretaker.isActive());
        return caretaker.isActive();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CaretakerEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

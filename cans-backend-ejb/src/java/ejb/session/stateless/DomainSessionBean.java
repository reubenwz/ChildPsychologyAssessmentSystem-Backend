/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AgeGroupEntity;
import entity.DomainEntity;
import entity.MainQuestionEntity;
import entity.QuestionEntity;
import entity.SubModuleEntity;
import entity.SubQuestionEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DomainExistsException;
import util.exception.DomainNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
@Stateless
public class DomainSessionBean implements DomainSessionBeanLocal {

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public DomainSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public DomainEntity createNewDomain(DomainEntity domain) throws UnknownPersistenceException, DomainExistsException, InputDataValidationException {
        Set<ConstraintViolation<DomainEntity>> constraintViolations = validator.validate(domain);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(domain);
                em.flush();

                return domain;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new DomainExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public DomainEntity retrieveDomainById(Long id) throws DomainNotFoundException {
        DomainEntity domain = em.find(DomainEntity.class, id);
        if (domain != null) {
            return domain;
        } else {
            throw new DomainNotFoundException("Domain ID " + id + " does not exist");
        }
    }

    @Override
    public List<DomainEntity> retrieveAllDomains() {
        int latestVersionNumber = this.retrieveLatestVersionNumber();
        Query query = em.createQuery("SELECT d FROM DomainEntity d WHERE d.version=:vn");
        query.setParameter("vn", latestVersionNumber);
        return query.getResultList();
    }

    @Override
    public List<DomainEntity> retrieveAllClientDomains() {
        List<DomainEntity> domains = retrieveAllDomains();
        List<DomainEntity> domainsToRemove = new ArrayList<>();
        for (DomainEntity d : domains) {
            if (d.isCaregiverDomain()) {
                domainsToRemove.add(d);
            }
        }
        for (DomainEntity d : domainsToRemove) {
            domains.remove(d);
        }
        return domains;
    }

    @Override
    public List<DomainEntity> retrieveAllCaretakerDomains() {
        List<DomainEntity> domains = retrieveAllDomains();
        List<DomainEntity> domainsToRemove = new ArrayList<>();
        for (DomainEntity d : domains) {
            if (!d.isCaregiverDomain()) {
                domainsToRemove.add(d);
            }
        }
        for (DomainEntity d : domainsToRemove) {
            domains.remove(d);
        }
        return domains;
    }

    @Override
    public List<DomainEntity> retrieveAllNonModuleDomains() {
        Query query = em.createQuery("SELECT d FROM DomainEntity d WHERE d.module = FALSE AND d.version=:vn");
        int latestVersionNumber = this.retrieveLatestVersionNumber();
        query.setParameter("vn", latestVersionNumber);
        return query.getResultList();
    }

    @Override
    public List<DomainEntity> retrieveAllModules() {
        Query query = em.createQuery("SELECT d FROM DomainEntity d WHERE d.module = TRUE AND d.version=:vn");
        int latestVersionNumber = this.retrieveLatestVersionNumber();
        query.setParameter("vn", latestVersionNumber);
        return query.getResultList();
    }

    @Override
    public void updateDomains(List<DomainEntity> newDomains) {
        int nextVersionNumber = this.retrieveLatestVersionNumber() + 1;
        for (DomainEntity domain : newDomains) {
            domain.setVersion(nextVersionNumber);
            em.persist(domain);
            for (AgeGroupEntity ageGroup : domain.getAgeGroups()) {
                em.persist(ageGroup);
                ageGroup.setDomain(domain);
                
                for (MainQuestionEntity question : ageGroup.getQuestions()) {
                    em.persist(question);
                    question.setAgeGroup(ageGroup);
                    
                    if (question.getSubModule() != null) {
                        SubModuleEntity sm = question.getSubModule();
                        em.persist(sm);
                        sm.getQues().add(question);
                        
                        for (SubQuestionEntity sq : sm.getSubQues()) {
                            em.persist(sq);
                            sq.setSubmodule(sm);
                            
                        }
                    }
                }
            }
        }
        em.flush();
    }

    private int retrieveLatestVersionNumber() {
        Query query = em.createQuery("SELECT d FROM DomainEntity d");
        List<DomainEntity> domains = query.getResultList();
        int latestNo = -1;
        for (DomainEntity domain : domains) {
            if (domain.getVersion() > latestNo) {
                latestNo = domain.getVersion();
            }
        }
        return latestNo;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<DomainEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

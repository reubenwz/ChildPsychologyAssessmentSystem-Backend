/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AgeGroupEntity;
import entity.DomainEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AgeGroupExistsException;
import util.exception.AgeGroupNotFoundException;
import util.exception.DomainNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class AgeGroupSessionBean implements AgeGroupSessionBeanLocal {

    @EJB
    private DomainSessionBeanLocal domainSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AgeGroupSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public AgeGroupEntity createNewAgeGroupForDomain(AgeGroupEntity age, Long id) throws AgeGroupExistsException, UnknownPersistenceException, InputDataValidationException, DomainNotFoundException {
        Set<ConstraintViolation<AgeGroupEntity>> constraintViolations = validator.validate(age);
        if (constraintViolations.isEmpty()) {
            try {
                DomainEntity domain = domainSessionBean.retrieveDomainById(id);
                age.setDomain(domain);
                em.persist(age);

                domain.getAgeGroups().add(age);
                em.flush();
                return age;
            } catch (DomainNotFoundException ex) {
                throw new DomainNotFoundException(ex.getMessage());
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AgeGroupExistsException();
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
    public AgeGroupEntity retrieveAgeGroupById(Long id) throws AgeGroupNotFoundException {
        AgeGroupEntity age = em.find(AgeGroupEntity.class, id);
        if (age != null) {
            return age;
        } else {
            throw new AgeGroupNotFoundException("Age Group ID: " + id + "does not exist");
        }
    }
    
    @Override
    public List<Long> retrieveAgeGroupIdbyAge(int age) throws AgeGroupNotFoundException {
        List<AgeGroupEntity> ageGroups = retrieveAllAgeGroups();
        if (age < 0 || age > 20) {
            throw new AgeGroupNotFoundException("Age provided is out of range!");
        }
        
        List<Long> ageGroupIds = new ArrayList<>();
        
        for (AgeGroupEntity ageGroup : ageGroups) {
            int lower_bound;
            int higher_bound;
            if (ageGroup.getAgeRange().contains("+")) {
                String ageGap = ageGroup.getAgeRange().substring(0, ageGroup.getAgeRange().length() - 1);
                lower_bound = Integer.parseInt(ageGap);
                higher_bound = 20;
            } else {
                String[] ageGap = ageGroup.getAgeRange().split("-");
                lower_bound = Integer.parseInt(ageGap[0]);
                higher_bound = Integer.parseInt(ageGap[1]);
            }
            if (age <= higher_bound && age >= lower_bound) {
                ageGroupIds.add(ageGroup.getAgeGroupId());
            }
        }
        return ageGroupIds;
    }
    
    @Override
    public List<AgeGroupEntity> retrieveAllAgeGroups() {
        Query query = em.createQuery("SELECT a FROM AgeGroupEntity a");
        return query.getResultList();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AgeGroupEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    
}

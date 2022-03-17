/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.MainQuestionEntity;
import entity.QuestionEntity;
import entity.SubModuleEntity;
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
import util.exception.InputDataValidationException;
import util.exception.QuestionNotFoundException;
import util.exception.QuestionTypeInaccurateException;
import util.exception.SubModuleExistsException;
import util.exception.SubModuleNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class SubModuleSessionBean implements SubModuleSessionBeanLocal {

    @EJB
    private QuestionsSessionBeanLocal questionsSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SubModuleSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public SubModuleEntity createNewSubModule(SubModuleEntity mod, Long mainQuesId) throws SubModuleExistsException, UnknownPersistenceException, InputDataValidationException, QuestionTypeInaccurateException, QuestionNotFoundException {
        Set<ConstraintViolation<SubModuleEntity>> constraintViolations = validator.validate(mod);
        if (constraintViolations.isEmpty()) {
            try {
                QuestionEntity ques = questionsSessionBean.retrieveQuestionById(mainQuesId);
                if (ques instanceof MainQuestionEntity) {
                    mod.getQues().add((MainQuestionEntity) ques);
                    em.persist(mod);

                    ((MainQuestionEntity) ques).setSubModule(mod);
                    em.flush();
                } else {
                    throw new QuestionTypeInaccurateException("Question Type must be of Main Question");
                }

                return mod;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new SubModuleExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (QuestionNotFoundException ex) {
                throw new QuestionNotFoundException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public SubModuleEntity createNewSubModuleWithManyMainQues(SubModuleEntity mod, List<Long> mainQuesIds) throws SubModuleExistsException, UnknownPersistenceException, InputDataValidationException, QuestionTypeInaccurateException, QuestionNotFoundException {
        Set<ConstraintViolation<SubModuleEntity>> constraintViolations = validator.validate(mod);
        if (constraintViolations.isEmpty()) {
            try {

                for (Long id : mainQuesIds) {
                    QuestionEntity ques = questionsSessionBean.retrieveQuestionById(id);
                    if (ques instanceof MainQuestionEntity) {
                        mod.getQues().add((MainQuestionEntity) ques);
                        ((MainQuestionEntity) ques).setSubModule(mod);

                    } else {
                        throw new QuestionTypeInaccurateException("Question Type must be of Main Question");
                    }
                    em.persist(mod);
                    em.flush();
                }

                return mod;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new SubModuleExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (QuestionNotFoundException ex) {
                throw new QuestionNotFoundException(ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public SubModuleEntity retreiveModuleById(Long id) throws SubModuleNotFoundException {
        SubModuleEntity mod = em.find(SubModuleEntity.class, id);
        if (mod != null) {
            return mod;
        } else {
            throw new SubModuleNotFoundException("SubModule ID: " + id + " does not exist!");
        }
    }

    @Override
    public List<SubModuleEntity> retrieveSubModuleByCategory(Boolean isInfo) {
        Query query = em.createQuery("SELECT s FROM SubModuleEntity s WHERE s.isInfo = :isInfo");
        query.setParameter("isInfo", isInfo);

        return query.getResultList();

    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<SubModuleEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

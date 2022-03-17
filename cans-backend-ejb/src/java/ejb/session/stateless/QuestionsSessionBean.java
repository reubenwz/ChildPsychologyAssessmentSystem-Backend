/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AgeGroupEntity;
import entity.MainQuestionEntity;
import entity.QuestionEntity;
import entity.SubModuleEntity;
import entity.SubQuestionEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.AgeGroupNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.MainQuestionExistsException;
import util.exception.QuestionNotFoundException;
import util.exception.SubModuleNotFoundException;
import util.exception.SubQuestionExistsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 * @author Ong Bik Jeun
 * @author Wang Ziyue
 */
@Stateless
public class QuestionsSessionBean implements QuestionsSessionBeanLocal {

    @EJB
    private SubModuleSessionBeanLocal subModuleSessionBean;
    @EJB
    private AgeGroupSessionBeanLocal ageGroupSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public QuestionsSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public QuestionEntity createMainQuestionForAgeGroup(Long ageId, MainQuestionEntity ques) throws AgeGroupNotFoundException, MainQuestionExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<MainQuestionEntity>> constraintViolations = validator.validate(ques);
        if (constraintViolations.isEmpty()) {
            try {
                AgeGroupEntity age = ageGroupSessionBean.retrieveAgeGroupById(ageId);
                ques.setAgeGroup(age);
                em.persist(ques);

                age.getQuestions().add(ques);
                em.flush();
                return ques;
            } catch (AgeGroupNotFoundException ex) {
                throw new AgeGroupNotFoundException(ex.getMessage());
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new MainQuestionExistsException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessageForMain(constraintViolations));
        }
    }

    @Override
    public QuestionEntity createSubQuestionForSubModule(Long modId, SubQuestionEntity ques) throws SubModuleNotFoundException, SubQuestionExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<SubQuestionEntity>> constraintViolations = validator.validate(ques);
        if (constraintViolations.isEmpty()) {
            try {
                SubModuleEntity mod = subModuleSessionBean.retreiveModuleById(modId);
                ques.setSubmodule(mod);

                em.persist(ques);

                mod.getSubQues().add(ques);

                em.flush();
                return ques;
            } catch (SubModuleNotFoundException ex) {
                throw new SubModuleNotFoundException(ex.getMessage());
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new SubQuestionExistsException();
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
    public List<QuestionEntity> retrieveAllQuestion() {
        Query query = em.createQuery("SELECT q FROM QuestionEntity q");
        return query.getResultList();

    }

    @Override
    public QuestionEntity retrieveQuestionById(Long id) throws QuestionNotFoundException {
        QuestionEntity ques = em.find(QuestionEntity.class, id);
        if (ques != null) {
            return ques;
        } else {
            throw new QuestionNotFoundException("Question ID: " + id + " does not exist!");
        }
    }

    @Override
    public QuestionEntity retrieveQuestionByCode(String code) throws QuestionNotFoundException {
        Query query = em.createQuery("SELECT q FROM QuestionEntity q WHERE q.questionCode = :code");
        query.setParameter("code", code);
        List<QuestionEntity> questions = query.getResultList();
        if (questions.isEmpty()) {
            throw new QuestionNotFoundException("Question code: " + code + " does not exist!");
        }
        QuestionEntity q = questions.get(0);
        int v = 0;
        if (q instanceof MainQuestionEntity) {
            MainQuestionEntity mq = (MainQuestionEntity) q;
            v = mq.getAgeGroup().getDomain().getVersion();
        } else if (q instanceof SubQuestionEntity) {
            SubQuestionEntity sq = (SubQuestionEntity) q;
            v = sq.getSubmodule().getQues().get(0).getAgeGroup().getDomain().getVersion();
        }
        for (QuestionEntity q2 : questions) {
            if (q2 instanceof MainQuestionEntity) {
                MainQuestionEntity mq2 = (MainQuestionEntity) q2;
                int v2 = mq2.getAgeGroup().getDomain().getVersion();
                if (v2 > v) {
                    v = v2;
                    q = q2;
                }
            } else if (q2 instanceof SubQuestionEntity) {
                SubQuestionEntity sq2 = (SubQuestionEntity) q2;
                int v2 = sq2.getSubmodule().getQues().get(0).getAgeGroup().getDomain().getVersion();
                if (v2 > v) {
                    v = v2;
                    q = q2;
                }
            }
        }
        return q;
    }

    @Override
    public QuestionEntity retrieveQuestionByQuestionTitle(String questionTitle) throws QuestionNotFoundException {
        Query query = em.createQuery("select q from QuestionEntity q where q.questionTitle = UPPER(:inQuestionTitle)"); // upper for SR1
        query.setParameter("inQuestionTitle", questionTitle);
        List<QuestionEntity> questions = query.getResultList();
        if (questions.isEmpty()) {
            throw new QuestionNotFoundException("Question title: " + questionTitle + " does not exist!");
        }
        QuestionEntity q = questions.get(0);
        int v = 0;
        if (q instanceof MainQuestionEntity) {
            MainQuestionEntity mq = (MainQuestionEntity) q;
            v = mq.getAgeGroup().getDomain().getVersion();
        } else if (q instanceof SubQuestionEntity) {
            SubQuestionEntity sq = (SubQuestionEntity) q;
            v = sq.getSubmodule().getQues().get(0).getAgeGroup().getDomain().getVersion();
        }
        for (QuestionEntity q2 : questions) {
            if (q2 instanceof MainQuestionEntity) {
                MainQuestionEntity mq2 = (MainQuestionEntity) q2;
                int v2 = mq2.getAgeGroup().getDomain().getVersion();
                if (v2 > v) {
                    v = v2;
                    q = q2;
                }
            } else if (q2 instanceof SubQuestionEntity) {
                SubQuestionEntity sq2 = (SubQuestionEntity) q2;
                int v2 = sq2.getSubmodule().getQues().get(0).getAgeGroup().getDomain().getVersion();
                if (v2 > v) {
                    v = v2;
                    q = q2;
                }
            }
        }
        return q;
    }

    private String prepareInputDataValidationErrorsMessageForMain(Set<ConstraintViolation<MainQuestionEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<SubQuestionEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

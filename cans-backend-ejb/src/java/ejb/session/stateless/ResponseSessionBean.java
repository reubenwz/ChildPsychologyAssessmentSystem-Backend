/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessmentEntity;
import entity.CaretakerAssessmentEntity;
import entity.QuestionEntity;
import entity.ResponseEntity;
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
import util.exception.AssessmentNotFoundException;
import util.exception.CaretakerAssessmentNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.QuestionNotFoundException;
import util.exception.ResponseExistsException;
import util.exception.ResponseNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ziyue
 * @author Ong Bik Jeun
 */
@Stateless
public class ResponseSessionBean implements ResponseSessionBeanLocal {

    @EJB
    private CaretakerAssessmentSessionBeanLocal caretakerAssessmentSessionBean;

    @EJB
    private AssessmentSessionBeanLocal assessmentSessionBean;

    @EJB
    private QuestionsSessionBeanLocal questionsSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ResponseSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.getValidator();
    }

    @Override
    public Long createNewAssessmentResponse(ResponseEntity responseEntity, String questionCode, Long assessmentId) throws AssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ResponseEntity>> constraintViolations = validator.validate(responseEntity);
        if (constraintViolations.isEmpty()) {
            try {
                AssessmentEntity assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(assessmentId);
                QuestionEntity question = questionsSessionBean.retrieveQuestionByCode(questionCode);
                responseEntity.setQuestion(question);
                assessment.getResponse().add(responseEntity);

                em.persist(responseEntity);
                em.flush();
                return responseEntity.getResponseId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ResponseExistsException();
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
    public Long createNewCaretakerAssessmentResponse(ResponseEntity responseEntity, String questionCode, Long caretakerAssessmentId) throws CaretakerAssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<ResponseEntity>> constraintViolations = validator.validate(responseEntity);
        if (constraintViolations.isEmpty()) {
            try {
                CaretakerAssessmentEntity assessment = caretakerAssessmentSessionBean.retrieveCaretakerAssessmentById(caretakerAssessmentId);
                QuestionEntity question = questionsSessionBean.retrieveQuestionByCode(questionCode);
                responseEntity.setQuestion(question);
                assessment.getCaretakerResponses().add(responseEntity);

                em.persist(responseEntity);
                em.flush();
                return responseEntity.getResponseId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new ResponseExistsException();
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
    public Long updateResponse(Long responseId, Integer value, String note) throws ResponseNotFoundException {
        ResponseEntity response = retrieveResponseById(responseId);
        response.setResponseValue(value);
        response.setResponseNotes(note);
        em.flush();

        return responseId;
    }

    @Override
    public ResponseEntity retrieveResponseById(Long responseId) throws ResponseNotFoundException {

        ResponseEntity responseEntity = em.find(ResponseEntity.class, responseId);
        if (responseEntity == null) {
            throw new ResponseNotFoundException("ResponseNotFoundException: Response with id " + responseId + " does not exist!");
        }

        return responseEntity;
    }

    @Override
    public List<ResponseEntity> retrieveAllResponseByQuesId(Long id) {
        Query query = em.createQuery("SELECT r FROM ResponseEntity r WHERE r.question.questionId = :id");
        query.setParameter("id", id);

        return query.getResultList();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ResponseEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
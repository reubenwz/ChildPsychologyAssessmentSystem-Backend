/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessmentEntity;
import entity.CaretakerAssessmentEntity;
import entity.CaretakerEntity;
import entity.MainQuestionEntity;
import entity.ResponseEntity;
import java.util.ArrayList;
import java.util.Iterator;
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
import util.exception.AssessmentNotFoundException;
import util.exception.CaretakerAssessmentExistsException;
import util.exception.CaretakerAssessmentNotFoundException;
import util.exception.CaretakerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.QuestionNotFoundException;
import util.exception.ResponseExistsException;
import util.exception.ResponseNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ooi Jun Hao
 * @author Ziyue
 */
@Stateless
public class CaretakerAssessmentSessionBean implements CaretakerAssessmentSessionBeanLocal {

    @EJB
    private ResponseSessionBeanLocal responseSessionBean;

    @EJB
    private AssessmentSessionBeanLocal assessmentSessionBean;
    @EJB
    private CaretakerSessionBeanLocal caretakerSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CaretakerAssessmentSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.getValidator();
    }

    @Override
    public Long createNewAssessment(CaretakerAssessmentEntity caretakerAssessment, Long caretakerId, Long assessmentId) throws CaretakerAssessmentExistsException, UnknownPersistenceException, InputDataValidationException, AssessmentNotFoundException, CaretakerNotFoundException {
        if (caretakerId == null) {
            throw new InputDataValidationException("InputDataValidationException: Invalid caretaker id!");
        }

        if (assessmentId == null) {
            throw new InputDataValidationException("InputDataValidationException: Invalid assessment id!");
        }

        CaretakerEntity caretaker = caretakerSessionBean.retrieveCaretakerById(caretakerId);
        AssessmentEntity assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(assessmentId);

        caretakerAssessment.setCaretaker(caretaker);
        caretaker.addCaretakerAssessment(caretakerAssessment);


        caretakerAssessment.setAssessment(assessment);
        assessment.addCaretakerAssessment(caretakerAssessment);

        Set<ConstraintViolation<CaretakerAssessmentEntity>> constraintViolations = validator.validate(caretakerAssessment);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(caretakerAssessment);
                em.flush();
                return caretakerAssessment.getCaretakerAssessmentId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CaretakerAssessmentExistsException();
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
    public List<String> checkUnansweredQuestions(Long assessmentId) throws CaretakerAssessmentNotFoundException {
        CaretakerAssessmentEntity assessment = retrieveCaretakerAssessmentById(assessmentId);
        List<String> questionCodes = new ArrayList<>();
        List<ResponseEntity> responses = assessment.getCaretakerResponses();
        for(ResponseEntity response : responses) {
            if (response.getQuestion() instanceof MainQuestionEntity) {
                if (response.getResponseValue() == -2) {
                    questionCodes.add(response.getQuestion().getQuestionCode());
                }
            }
        }
        
        return questionCodes;
    }

    @Override
    public CaretakerAssessmentEntity retrieveCaretakerAssessmentById(Long caretakerAssessmentId) throws CaretakerAssessmentNotFoundException {

        CaretakerAssessmentEntity caretakerAssessmentEntity = em.find(CaretakerAssessmentEntity.class, caretakerAssessmentId);
        if (caretakerAssessmentEntity == null) {
            throw new CaretakerAssessmentNotFoundException("CaretakerAssessmentNotFoundException: Caretaker Assessment with id " + caretakerAssessmentId + " does not exist!");
        }

        return caretakerAssessmentEntity;
    }

    @Override
    public CaretakerAssessmentEntity retrieveCaretakerAssessmentByAssessmentIdAndCaretakerId(long assessmentId, long caretakerId) throws CaretakerAssessmentNotFoundException {
        Query query = em.createQuery("SELECT c FROM CaretakerAssessmentEntity c WHERE c.assessment.assessmentId=:assessId AND c.caretaker.caretakerId=:ctID");
        query.setParameter("assessId", assessmentId);
        query.setParameter("ctID", caretakerId);
        try {
            return (CaretakerAssessmentEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CaretakerAssessmentNotFoundException();
        } 
    }

    @Override
    public ResponseEntity getResponseFromCaretakerAssessmentByCode(Long caretakerAssessmentId, String code) throws CaretakerAssessmentNotFoundException {
        CaretakerAssessmentEntity caretakerAssessment = retrieveCaretakerAssessmentById(caretakerAssessmentId);
        for (ResponseEntity response : caretakerAssessment.getCaretakerResponses()) {
            if (response.getQuestion().getQuestionCode().equals(code)) {
                return response;
            }
        }
        return null;
    }

    @Override
    public List<CaretakerAssessmentEntity> retrieveAllCaretakerAssessment() {
        Query query = em.createQuery("SELECT c FROM CaretakerAssessmentEntity c");
        return query.getResultList();
    }

    @Override
    public Long updateCaretakerAssessmentResponses(Long caretakerAssessmentId, List<String> questionCodes, List<Integer> responseValues, List<String> responseNotes) throws UnknownPersistenceException, InputDataValidationException, CaretakerAssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, ResponseNotFoundException {
        CaretakerAssessmentEntity assessment = retrieveCaretakerAssessmentById(caretakerAssessmentId);
        Iterator<String> codes = questionCodes.iterator();
        Iterator<Integer> values = responseValues.iterator();
        Iterator<String> notes = responseNotes.iterator();  
        while (codes.hasNext() && values.hasNext() && notes.hasNext()) {
            String questionCode = codes.next();
            Integer responseValue = values.next();
            String responseNote = notes.next();
            boolean exists = false;
            Long responseId = 0l;
            for (ResponseEntity response : assessment.getCaretakerResponses()) {
                if (response.getQuestion().getQuestionCode().equals(questionCode)) {
                    exists = true;
                    responseId = response.getResponseId();
                    break;
                }
            }
            if (!exists) {
                ResponseEntity response = new ResponseEntity(responseValue, responseNote);
                responseSessionBean.createNewCaretakerAssessmentResponse(response, questionCode, caretakerAssessmentId);
            } else {
                responseSessionBean.updateResponse(responseId, responseValue, responseNote);
            }

        }
        return caretakerAssessmentId;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CaretakerAssessmentEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CaretakerAssessmentEntity;
import entity.ResponseEntity;
import java.util.List;
import javax.ejb.Local;
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
 */
@Local
public interface CaretakerAssessmentSessionBeanLocal {

    public CaretakerAssessmentEntity retrieveCaretakerAssessmentById(Long caretakerAssessmentId) throws CaretakerAssessmentNotFoundException;

    public Long createNewAssessment(CaretakerAssessmentEntity caretakerAssessment, Long caretakerId, Long assessmentId) throws CaretakerAssessmentExistsException, UnknownPersistenceException, InputDataValidationException, AssessmentNotFoundException, CaretakerNotFoundException;

    public CaretakerAssessmentEntity retrieveCaretakerAssessmentByAssessmentIdAndCaretakerId(long assessmentId, long caretakerId) throws CaretakerAssessmentNotFoundException;

    public ResponseEntity getResponseFromCaretakerAssessmentByCode(Long caretakerAssessmentId, String code) throws CaretakerAssessmentNotFoundException;

    public List<CaretakerAssessmentEntity> retrieveAllCaretakerAssessment();

    public Long updateCaretakerAssessmentResponses(Long caretakerAssessmentId, List<String> questionCodes, List<Integer> responseValues, List<String> responseNotes) throws UnknownPersistenceException, InputDataValidationException, CaretakerAssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, ResponseNotFoundException;

    public List<String> checkUnansweredQuestions(Long assessmentId) throws CaretakerAssessmentNotFoundException;
    
}

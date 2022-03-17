/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ResponseEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AssessmentNotFoundException;
import util.exception.CaretakerAssessmentNotFoundException;
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
public interface ResponseSessionBeanLocal {

    public ResponseEntity retrieveResponseById(Long responseId) throws ResponseNotFoundException;

    public List<ResponseEntity> retrieveAllResponseByQuesId(Long id);

    public Long createNewAssessmentResponse(ResponseEntity responseEntity, String questionCode, Long assessmentId) throws AssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, UnknownPersistenceException, InputDataValidationException;

    public Long updateResponse(Long responseId, Integer value, String note) throws ResponseNotFoundException;

    public Long createNewCaretakerAssessmentResponse(ResponseEntity responseEntity, String questionCode, Long caretakerAssessmentId) throws CaretakerAssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, UnknownPersistenceException, InputDataValidationException;

}

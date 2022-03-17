/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessmentEntity;
import entity.ResponseEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.AssessmentStatusEnum;
import util.exception.AssessmentExistsException;
import util.exception.AssessmentNotFoundException;
import util.exception.AssessmentStatusUpdateException;
import util.exception.CaretakerAssessmentNotFoundException;
import util.exception.ClientNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.QuestionNotFoundException;
import util.exception.ResponseExistsException;
import util.exception.ResponseNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface AssessmentSessionBeanLocal {

    public boolean assessmentInDatabase(Long assessmentId);

    public Long createNewAssessment(AssessmentEntity assessmentEntity, Long clientId, Long assessorId) throws AssessmentExistsException, UnknownPersistenceException, InputDataValidationException, ClientNotFoundException, UserNotFoundException;

    public AssessmentEntity retrieveAssessmentById(Long assessmentId) throws AssessmentNotFoundException;

    public AssessmentEntity retrieveAssessmentByUniqueId(Long assessmentId) throws AssessmentNotFoundException;

    public ResponseEntity getResponseFromAssessmentByCode(Long assessmentId, String code) throws AssessmentNotFoundException;

    public List<AssessmentEntity> retrieveAllAssessment();

    public List<AssessmentEntity> retrieveAssessmentWithinDate(Date start, Date end);

    public List<AssessmentEntity> retrieveAssessmentByClientWithinDate(Long clientId, Date start, Date end);

    public Long updateAssessmentStatus(Long assessmentId, AssessmentStatusEnum status) throws AssessmentNotFoundException;

    public Long getNextAssessmentUniqueId();

    public Long updateAssessmentResponses(Long assessmentId, List<String> questionCodes, List<Integer> responseValues, List<String> responseNotes) throws AssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, UnknownPersistenceException, InputDataValidationException, ResponseNotFoundException;

    public Long submitAssessment(Long assessmentId) throws AssessmentNotFoundException, AssessmentStatusUpdateException, CaretakerAssessmentNotFoundException;

    public Long approveAssessment(Long assessmentId) throws AssessmentNotFoundException, AssessmentStatusUpdateException;

    public Long rejectAssessment(Long assessmentId) throws AssessmentNotFoundException, AssessmentStatusUpdateException;

    public List<String> checkUnansweredQuestions(Long assessmentId) throws AssessmentNotFoundException;

    public List<AssessmentEntity> retrieveAllAssessmentByAssessorId(Long id);

}

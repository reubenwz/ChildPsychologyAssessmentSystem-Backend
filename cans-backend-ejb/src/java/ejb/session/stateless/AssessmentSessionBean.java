/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AgeGroupEntity;
import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CaretakerAssessmentEntity;
import entity.CaretakerEntity;
import entity.ClientEntity;
import entity.DomainEntity;
import entity.MainQuestionEntity;
import entity.ResponseEntity;
import entity.SubModuleEntity;
import entity.SubQuestionEntity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.xml.bind.DatatypeConverter;
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
 * @author Ziyue
 * @author Ong Bik Jeun
 */
@Stateless
public class AssessmentSessionBean implements AssessmentSessionBeanLocal {

    @EJB
    private CaretakerAssessmentSessionBeanLocal caretakerAssessmentSessionBean;

    @EJB
    private DomainSessionBeanLocal domainSessionBean;

    @EJB
    private ResponseSessionBeanLocal responseSessionBean;

    @EJB
    private ClientSessionBeanLocal clientSessionBean;
    @EJB
    private AssessorSessionBeanLocal assessorSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AssessmentSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.getValidator();
    }

    @Override
    public Long createNewAssessment(AssessmentEntity assessmentEntity, Long clientId, Long assessorId) throws AssessmentExistsException, UnknownPersistenceException, InputDataValidationException, ClientNotFoundException, UserNotFoundException {
        if (clientId == null) {
            throw new InputDataValidationException("InputDataValidationException: Invalid client id!");
        }

        if (assessorId == null) {
            throw new InputDataValidationException("InputDataValidationException: Invalid assessor id!");
        }

        ClientEntity client = clientSessionBean.retrieveClientById(clientId);

        assessmentEntity.setClient(client);
        client.addAssessment(assessmentEntity);

        AssessorEntity assessor = assessorSessionBean.retrieveUserById(assessorId);

        assessmentEntity.setAssessor(assessor);
        assessor.addAssessment(assessmentEntity);
        
        Set<ConstraintViolation<AssessmentEntity>> constraintViolations = validator.validate(assessmentEntity);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(assessmentEntity);
                em.flush();
                return assessmentEntity.getAssessmentId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AssessmentExistsException();
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
    public boolean assessmentInDatabase(Long assessmentId) {
        AssessmentEntity assessmentEntity = em.find(AssessmentEntity.class, assessmentId);
        if (assessmentEntity != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public AssessmentEntity retrieveAssessmentById(Long assessmentId) throws AssessmentNotFoundException {

        AssessmentEntity assessmentEntity = em.find(AssessmentEntity.class, assessmentId);
        if (assessmentEntity == null) {
            throw new AssessmentNotFoundException("AssessmentNotFoundException: Assessment with id " + assessmentId + " does not exist!");
        }

        return assessmentEntity;
    }

    @Override
    public AssessmentEntity retrieveAssessmentByUniqueId(Long assessmentId) throws AssessmentNotFoundException {
        Query query = em.createQuery("SELECT a FROM AssessmentEntity a WHERE a.assessmentUniqueId=:uniqueId");
        query.setParameter("uniqueId", assessmentId);
        try {
            return (AssessmentEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AssessmentNotFoundException();
        }
    }

    @Override
    public ResponseEntity getResponseFromAssessmentByCode(Long assessmentId, String code) throws AssessmentNotFoundException {
        AssessmentEntity assessment = retrieveAssessmentById(assessmentId);
        for (ResponseEntity response : assessment.getResponse()) {
            if (response.getQuestion().getQuestionCode().equals(code)) {
                return response;
            }
        }
        return null;
    }

    @Override
    public List<AssessmentEntity> retrieveAllAssessment() {
        Query query = em.createQuery("SELECT a FROM AssessmentEntity a");
        return query.getResultList();
    }

    @Override
    public List<AssessmentEntity> retrieveAllAssessmentByAssessorId(Long id) {
        Query query = em.createQuery("SELECT a FROM AssessmentEntity a WHERE a.assessor.assessorId = :id");
        query.setParameter("id", id);

        return query.getResultList();
    }

    @Override
    public Long getNextAssessmentUniqueId() {
        List<AssessmentEntity> assessments = retrieveAllAssessment();
        for (Long i = 1l; i <= assessments.size() + 1; i++) {
            if (isUniqueIdAvailable(i)) {
                return i;
            }
        }
        return 0l;
    }

    private Boolean isUniqueIdAvailable(Long id) {
        try {
            retrieveAssessmentByUniqueId(id);
        } catch (AssessmentNotFoundException ex) {
            return true;
        }
        return false;
    }

    @Override
    public Long updateAssessmentResponses(Long assessmentId, List<String> questionCodes, List<Integer> responseValues, List<String> responseNotes) throws AssessmentNotFoundException, QuestionNotFoundException, ResponseExistsException, UnknownPersistenceException, InputDataValidationException, ResponseNotFoundException {
        AssessmentEntity assessment = retrieveAssessmentByUniqueId(assessmentId);
        Iterator<String> codes = questionCodes.iterator();
        Iterator<Integer> values = responseValues.iterator();
        Iterator<String> notes = responseNotes.iterator();
        while (codes.hasNext() && values.hasNext() && notes.hasNext()) {
            String questionCode = codes.next();
            Integer responseValue = values.next();
            String responseNote = notes.next();
            boolean exists = false;
            Long responseId = 0l;
            for (ResponseEntity response : assessment.getResponse()) {
                if (response.getQuestion().getQuestionCode().equals(questionCode)) {
                    exists = true;
                    responseId = response.getResponseId();
                    break;
                }
            }
            if (!exists) {
                ResponseEntity response = new ResponseEntity(responseValue, responseNote);
                responseSessionBean.createNewAssessmentResponse(response, questionCode, assessmentId);
            } else {
                responseSessionBean.updateResponse(responseId, responseValue, responseNote);
            }

        }
        return assessmentId;
    }

    @Override
    public Long updateAssessmentStatus(Long assessmentId, AssessmentStatusEnum status) throws AssessmentNotFoundException {
        AssessmentEntity assessment = retrieveAssessmentByUniqueId(assessmentId);
        assessment.setStatus(status);
        if (status.equals(AssessmentStatusEnum.APPROVED)) {
            assessment.setApprovedDate(new Date());
        } else {
            assessment.setApprovedDate(null);
        }
        em.flush();
        return assessmentId;
    }

    @Override
    public Long submitAssessment(Long assessmentId) throws AssessmentNotFoundException, AssessmentStatusUpdateException, CaretakerAssessmentNotFoundException {
        AssessmentEntity assessment = retrieveAssessmentByUniqueId(assessmentId);
        for (CaretakerEntity caretaker : assessment.getClient().getCaretakers()) {
            boolean isCreated = false;
            for (CaretakerAssessmentEntity caretakerAssessment : assessment.getCaretakerAssessments()) {
                Long caretakerAssessmentCaretakerId = caretakerAssessment.getCaretaker().getCaretakerUniqueId();
                if (caretakerAssessmentCaretakerId.equals(caretaker.getCaretakerId())) {
                    isCreated = true;
                }
            }
            if(!isCreated) {
                throw new AssessmentStatusUpdateException();
            }
        }
        List<String> unansweredQuestions = this.checkUnansweredQuestions(assessmentId);
        if (unansweredQuestions.size() > 0) {
            throw new AssessmentStatusUpdateException();
        }
        for (CaretakerAssessmentEntity caretakerAssessment : assessment.getCaretakerAssessments()) {
            unansweredQuestions = caretakerAssessmentSessionBean.checkUnansweredQuestions(caretakerAssessment.getCaretakerAssessmentId());
            if (unansweredQuestions.size() > 0) {
                throw new AssessmentStatusUpdateException();
            }
        }
        assessment.setStatus(AssessmentStatusEnum.SUBMITTED);
        List<ResponseEntity> responses = assessment.getResponse();
        double totalLoc = 0;
        int loc = 1;
        for (ResponseEntity response : responses) {
            totalLoc += response.getResponseValue();
        }
        System.out.println("locs: " + totalLoc + " size: " + responses.size());
        loc = (int) Math.ceil(totalLoc / responses.size());
        if (loc < 1) {
            loc = 1;
        }
        System.out.println("loc: " + loc);
        assessment.setLoc(loc);
        em.flush();
        return assessmentId;
    }
    
    @Override
    public List<String> checkUnansweredQuestions(Long assessmentId) throws AssessmentNotFoundException {
        AssessmentEntity assessment = retrieveAssessmentByUniqueId(assessmentId);
        List<String> questionCodes = new ArrayList<>();
        List<ResponseEntity> responses = assessment.getResponse();
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
    public Long approveAssessment(Long assessmentId) throws AssessmentNotFoundException, AssessmentStatusUpdateException {
        AssessmentEntity assessment = retrieveAssessmentByUniqueId(assessmentId);
        if (!assessment.getStatus().equals(AssessmentStatusEnum.SUBMITTED)) {
            throw new AssessmentStatusUpdateException();
        }
        assessment.setStatus(AssessmentStatusEnum.APPROVED);
        assessment.setApprovedDate(new Date());
        em.flush();
        return assessmentId;
    }
    
    @Override
    public Long rejectAssessment(Long assessmentId) throws AssessmentNotFoundException, AssessmentStatusUpdateException {
        AssessmentEntity assessment = retrieveAssessmentByUniqueId(assessmentId);
        if (!assessment.getStatus().equals(AssessmentStatusEnum.SUBMITTED)) {
            throw new AssessmentStatusUpdateException();
        }
        assessment.setStatus(AssessmentStatusEnum.REJECTED);
        em.flush();
        return assessmentId;
    }

    @Override
    public List<AssessmentEntity> retrieveAssessmentByClientWithinDate(Long clientId, Date start, Date end) {
        Query query = em.createQuery("SELECT a FROM AssessmentEntity a WHERE a.client.clientUniqueId = :clientId");
        query.setParameter("clientId", clientId);
        List<AssessmentEntity> allAssessment = query.getResultList();
        List<AssessmentEntity> filtered = filterAssByDate(allAssessment, start, end);
        return filtered;

    }

    @Override
    public List<AssessmentEntity> retrieveAssessmentWithinDate(Date start, Date end) {
        List<AssessmentEntity> allAssessment = retrieveAllAssessment();
        List<AssessmentEntity> filtered = filterAssByDate(allAssessment, start, end);
        return filtered;
    }

    private List<AssessmentEntity> filterAssByDate(List<AssessmentEntity> allAss, Date start, Date end) {
        List<AssessmentEntity> filtered = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.DATE, -1);
        Date startNew = c.getTime();
        c.setTime(end);
        c.add(Calendar.DATE, +1);
        Date endNew = c.getTime();

        allAss.stream().filter(assess -> (DatatypeConverter.parseDateTime(assess.getAssessmentDate()).getTime().after(startNew) && DatatypeConverter.parseDateTime(assess.getAssessmentDate()).getTime().before(endNew))).forEachOrdered(assess -> {
            filtered.add(assess);
        });
        return filtered;
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AssessmentEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}

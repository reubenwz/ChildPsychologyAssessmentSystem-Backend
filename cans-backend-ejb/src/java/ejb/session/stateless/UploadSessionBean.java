/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminUserEntity;
import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CaretakerAssessmentEntity;
import entity.CaretakerEntity;
import entity.ClientEntity;
import entity.MainQuestionEntity;
import entity.OrganisationEntity;
import entity.QuestionEntity;
import entity.ResponseEntity;
import entity.SubQuestionEntity;
import entity.UploadEntity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.exception.ClientNotFoundException;
import util.exception.UserNotFoundException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.ConstraintViolationException;
import org.apache.poi.ss.usermodel.CellType;
import util.enumeration.AssessmentReasonEnum;
import util.enumeration.AssessmentStatusEnum;
import util.enumeration.CaretakerAlgorithmEnum;
import util.enumeration.CaretakerTypeEnum;
import util.exception.AssessmentNotFoundException;
import util.exception.CaretakerNotFoundException;
import util.exception.DataUploadException;
import util.exception.OrganisationNotFoundException;
import util.exception.QuestionNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 * @author Wang Ziyue
 * @author Ong Bik Jeun
 *
 */
@Stateless
public class UploadSessionBean implements UploadSessionBeanLocal {

    @EJB
    private OrganisationSessionBeanLocal organisationSessionBean;

    @EJB
    private AdminUserSessionBeanLocal adminUserSessionBean;
    @EJB
    private QuestionsSessionBeanLocal questionsSessionBean;
    @EJB
    private AssessmentSessionBeanLocal assessmentSessionBean;
    @EJB
    private CaretakerSessionBeanLocal caretakerSessionBean;
    @EJB
    private ClientSessionBeanLocal clientSessionBean;

    @Resource
    private EJBContext eJBContext;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    @Override
    public List<UploadEntity> retrieveAllUploads() {
        Query query = em.createQuery("SELECT u FROM UploadEntity u ORDER BY u.uploadDate DESC");
        List<UploadEntity> uploads = query.getResultList();
        return uploads;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UploadEntity importRawData(String fileDirectory, String fileName, long adminUserId) throws UserNotFoundException, DataUploadException {

        boolean to_persist = true;
        String upload_details = "";

        AdminUserEntity admin = adminUserSessionBean.retrieveUserById(adminUserId);
        List<ClientEntity> clients = new ArrayList<>();
        List<CaretakerEntity> caretakers = new ArrayList<>();
        List<AssessmentEntity> assessments = new ArrayList<>();
        List<CaretakerAssessmentEntity> caretakerAssessments = new ArrayList<>();

        try (FileInputStream excelFile = new FileInputStream(new File(fileDirectory))) {

            XSSFWorkbook rawWorkBook = new XSSFWorkbook(excelFile);

            //Iterate through client sheet
            XSSFSheet clientSheet = rawWorkBook.getSheetAt(0);
//            AssessorEntity assessor;
//            try {
//                assessor = assessorSessionBean.retrieveUserByEmail("assessor1@msf.gov.sg");
//            } catch (UserNotFoundException e) {
//                throw new DataUploadException("Internal error: code 'I LOVE CAPSTONE'");
//            }

            System.out.println("** Processing Sheet 1 **");
            for (int i = 1; i < clientSheet.getLastRowNum() + 1; i++) {
                boolean new_entity = false;
                System.out.println("Processing Sheet 1 Row " + (i + 1));
                Row r = clientSheet.getRow(i);

                Long id = (long) (r.getCell(2).getNumericCellValue());
                ClientEntity clientToInsert;
                try {
                    clientToInsert = clientSessionBean.retrieveClientByUniqueId(id);
                    System.out.println("\tClient ID " + id + " exists");
                } catch (ClientNotFoundException e) {
                    System.out.println("\tClient ID " + id + " does not exist. Creating new client");
                    clientToInsert = new ClientEntity(id);
                    new_entity = true;
                }

                clientToInsert.setName((r.getCell(0) == null) ? "Unknown" : r.getCell(0).getStringCellValue());
                clientToInsert.setIdNumber((r.getCell(3) == null) ? "Unknown" : r.getCell(3).getStringCellValue());
                clientToInsert.setAccommodationStatus((r.getCell(7) == null) ? "Unknown" : r.getCell(7).getStringCellValue());
                clientToInsert.setAccommodationType((r.getCell(8) == null) ? "Unknown" : r.getCell(8).getStringCellValue());
                //newly added
                clientToInsert.setEducationLevel((r.getCell(14) == null) ? "Unknown" : r.getCell(14).getStringCellValue());
                clientToInsert.setCurrentOccupation((r.getCell(21) == null) ? "N/A" : r.getCell(21).getStringCellValue());
                clientToInsert.setMonthlyIncome((r.getCell(22).getCellType() != CellType.NUMERIC) ? 0 : (int) r.getCell(22).getNumericCellValue());

                String address = "Unknown";
                try {
                    // saving address as postal code + unit number
                    if ((int) r.getCell(5).getNumericCellValue() == 0) { //if postal empty is unknown/not reported
                        to_persist = false;
                        upload_details += "Missing entry in sheet 1, row " + (i + 1) + ", column 4/5/6\n";
                        System.out.println("\tMissing entry in sheet 1, row " + (i + 1) + ", column 4/5/6");
                    }
                    address = "#" + (int) r.getCell(4).getNumericCellValue() + "-" + (int) r.getCell(6).getNumericCellValue() + " S'" + (int) r.getCell(5).getNumericCellValue();
                } catch (NullPointerException e) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 1, row " + (i + 1) + ", column 4/5/6\n";
                    System.out.println("\tMissing entry in sheet 1, row " + (i + 1) + ", column 4/5/6");
                }
                clientToInsert.setAddress(address);
                clientToInsert.setAdmissionType((r.getCell(24) == null) ? "" : r.getCell(24).getStringCellValue());

//                ResponseEntity childSkillResponse = new ResponseEntity((r.getCell(618).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(618).getNumericCellValue(), (r.getCell(619) == null) ? null : r.getCell(619).getStringCellValue());
//                QuestionEntity question = questionsSessionBean.retrieveQuestionByCode("1.1");
//                childSkillResponse.setQuestion(question);
//                new_responses.add(childSkillResponse);
                // Orgnisation/agency types through assessor
                AssessorEntity assessor = null;
                OrganisationEntity organisation = null;
                List<AssessorEntity> assessors = new ArrayList<>();
                try {
                    String orgName = r.getCell(32).getStringCellValue();
                    String assessorName = r.getCell(33).getStringCellValue();
                    organisation = organisationSessionBean.retrieveOrganisationByName(orgName);
                    assessors = organisation.getAssessors();
                    boolean correct = false;
                    for (AssessorEntity a : assessors) {
                        if (a.getName().equals(assessorName)) {
                            assessor = a;
                            correct = true;
                            break;
                        }
                    }
                    if (!correct) {
                        to_persist = false;
                        upload_details += "Missing entry in sheet 1, row " + (i + 1) + ", column 33/34\n";
                        System.out.println("\tMissing entry in sheet 1, row " + (i + 1) + ", column 33/34");
                    }
                } catch (NullPointerException e) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 1, row " + (i + 1) + ", column 33/34\n";
                    System.out.println("\tMissing entry in sheet 1, row " + (i + 1) + ", column 33/34");
                } catch (OrganisationNotFoundException ex) {
                    System.out.println("WTF ***************************************************************************");
                }

                clientToInsert.setAssessor(assessor);
                assessor.getClients().add(clientToInsert);

                try {
                    clientToInsert.setDob(new SimpleDateFormat("dd/MM/yyyy").parse(r.getCell(11).getStringCellValue()));
                } catch (ParseException ex) {
                    to_persist = false;
                    upload_details += "Incorrect entry in sheet 1, row " + (i + 1) + ", column 12\n";
                    System.out.println("\tIncorrect entry in sheet 1, row " + (i + 1) + ", column 12");
                }
                clientToInsert.setEthnicity((r.getCell(10) == null) ? "Unknown" : r.getCell(10).getStringCellValue());
                clientToInsert.setGender((r.getCell(9) == null) ? "Unknown" : r.getCell(9).getStringCellValue());
                clientToInsert.setPlacementType((r.getCell(31) == null) ? "Unknown" : r.getCell(31).getStringCellValue());

                clients.add(clientToInsert);
                if (new_entity) {
                    em.persist(clientToInsert);
                }
            }

            // Iterate through caregivers to persist as caretakers
            System.out.println("** Processing Sheet 2 **");

            XSSFSheet caretakerSheet = rawWorkBook.getSheetAt(1);
            for (int i = 1; i < caretakerSheet.getLastRowNum() + 1; i++) {
                boolean new_entity = false;

                System.out.println("Processing Sheet 2 Row " + (i + 1));
                Row r = caretakerSheet.getRow(i);

                Long clientId = (long) (r.getCell(0).getNumericCellValue());

                ClientEntity caretakerClient;
                try {
                    caretakerClient = clientSessionBean.retrieveClientByUniqueId(clientId);
                    System.out.println("\tClient ID " + clientId + " exists");
                } catch (ClientNotFoundException ex) {
                    to_persist = false;
                    upload_details += "Invalid entry in sheet 2, row " + (i + 1) + ", column 1\n";
                    System.out.println("\tInvalid entry in sheet 2, row " + (i + 1) + ", column 1");
                    continue;
                }

                Long caretakerId = (long) (r.getCell(4).getNumericCellValue());
                CaretakerEntity caretakerToInsert;

                try {
                    caretakerToInsert = caretakerSessionBean.retrieveCaretakerByUniqueId(caretakerId);
                    if (caretakerToInsert.getClient().getClientUniqueId() != caretakerClient.getClientUniqueId()) { // caretaker already exists but belongs to a different client
                        to_persist = false;
                        upload_details += "Invalid entry in sheet 2, row " + (i + 1) + ", column 5\n";
                        System.out.println("\tInvalid entry in sheet 2, row " + (i + 1) + ", column 5");
                        continue;
                    }
                    System.out.println("\tCaretaker " + caretakerId + " exists. Updating caretaker");
                } catch (CaretakerNotFoundException ex) {
                    caretakerToInsert = new CaretakerEntity(caretakerId);
                    new_entity = true;
                    System.out.println("\tCreating New Caretaker: " + caretakerId);
                }

                caretakerToInsert.setName((r.getCell(2) == null) ? "Unknown" : r.getCell(2).getStringCellValue());
                caretakerToInsert.setIdNumber((r.getCell(5) == null) ? "Unknown" : r.getCell(5).getStringCellValue());
                caretakerToInsert.setAccommodationStatus((r.getCell(11) == null) ? "Unknown" : r.getCell(11).getStringCellValue());
                caretakerToInsert.setAccommodationType((r.getCell(12) == null) ? "Unknown" : r.getCell(12).getStringCellValue());
                String address = "Unknown";

                try {
                    // saving address as postal code + unit number
                    if ((int) r.getCell(8).getNumericCellValue() == 0) { //if postal empty is unknown/not reported
                        to_persist = false;
                        upload_details += "Missing entry in sheet 2, row " + (i + 1) + ", column 9/10/11\n";
                        System.out.println("\tMissing entry in sheet 2, row " + (i + 1) + ", column 9/10/11\n");
                        continue;
                    }
                    address = "#" + (int) r.getCell(9).getNumericCellValue() + "-" + (int) r.getCell(10).getNumericCellValue() + " S'" + (int) r.getCell(8).getNumericCellValue();
                } catch (NullPointerException e) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 2, row " + (i + 1) + ", column 9/10/11\n";
                    System.out.println("\tMissing entry in sheet 2, row " + (i + 1) + ", column 9/10/11\n");
                    continue;
                }

                caretakerToInsert.setAddress(address);

                try {
                    caretakerToInsert.setDob(new SimpleDateFormat("dd/MM/yyyy").parse(r.getCell(6).getStringCellValue()));
                } catch (ParseException ex) {
                    caretakerToInsert.setDob(null); // probably not neccesary
                    to_persist = false;
                    upload_details += "Incorrect entry in sheet 2, row " + (i + 1) + ", column 7\n";
                    System.out.println("\tIncorrect entry in sheet 2, row " + (i + 1) + ", column 7");
                    continue;
                }
                caretakerToInsert.setGender((r.getCell(13) == null) ? "Unknown" : r.getCell(13).getStringCellValue());

                caretakerToInsert.setCurrentOccupation((r.getCell(15) == null) ? "Unknown" : r.getCell(15).getStringCellValue());
                caretakerToInsert.setEducationLevel((r.getCell(14) == null) ? "Unknown" : r.getCell(14).getStringCellValue());

                String status = (r.getCell(17) == null) ? null : r.getCell(17).getStringCellValue();
                boolean isActive = true;
                if (status == null || status.charAt(0) == 'I') {
                    isActive = false;
                }
                caretakerToInsert.setActive(isActive);

                caretakerToInsert.setMonthlyIncome((int) ((r.getCell(16).getCellType() != CellType.NUMERIC) ? 0 : r.getCell(16).getNumericCellValue())); //numeric returns 0 if blank
                caretakerToInsert.setRelationshipToClient((r.getCell(7) == null) ? "Unknown" : r.getCell(7).getStringCellValue());

                caretakerClient.addCaretaker(caretakerToInsert);
                caretakerToInsert.setClient(caretakerClient); // to be assiocated before hand

                caretakers.add(caretakerToInsert);
                if (new_entity) {
                    em.persist(caretakerToInsert);
                }
            }

            //Iterate through assessments to get client's results
            System.out.println("** Processing Sheet 3 **");
            XSSFSheet assessmentSheet = rawWorkBook.getSheetAt(2);
            for (int i = 1; i < assessmentSheet.getLastRowNum() + 1; i++) {
                boolean new_entity = false;

                System.out.println("Processing Sheet 3 Row " + (i + 1));
                Row r = assessmentSheet.getRow(i);

                Long clientId = (long) (r.getCell(0).getNumericCellValue());

                ClientEntity assessmentClient = null;
                try {
                    assessmentClient = clientSessionBean.retrieveClientByUniqueId(clientId);
                    System.out.println("\tClient ID " + clientId + " exists");
                } catch (ClientNotFoundException ex) {
                    to_persist = false;
                    upload_details += "Invalid entry in sheet 3, row " + (i + 1) + ", column 1\n";
                    System.out.println("\tInvalid entry in sheet 3, row " + (i + 1) + ", column 1");
                    continue;
                }

                Long assessmentId = (long) (r.getCell(2).getNumericCellValue());

                AssessmentEntity assessment;
                try {
                    assessment = assessmentSessionBean.retrieveAssessmentByUniqueId(assessmentId);
                    if (assessment.getClient().getClientUniqueId() != assessmentClient.getClientUniqueId()) { // assessment already exists but belongs to another client
                        to_persist = false;
                        upload_details += "Invalid entry in sheet 3, row " + (i + 1) + ", column 3\n";
                        System.out.println("\tInvalid entry in sheet 3, row " + (i + 1) + ", column 3");
                        continue;
                    }
                    System.out.println("\tAssessment ID " + assessmentId + " exists");
                } catch (AssessmentNotFoundException e) {
                    assessment = new AssessmentEntity(assessmentId);
                    assessment.setClient(assessmentClient);
                    assessmentClient.getAssessment().add(assessment);
                    new_entity = true;
                    System.out.println(assessment);
                    System.out.println("\tCreating New Assessment: " + assessmentId);
                }

                // associate the relations
                AssessorEntity assessor = assessmentClient.getAssessor();
                assessment.setAssessor(assessor);
                assessor.addAssessment(assessment);

                String status = (r.getCell(3) == null) ? null : r.getCell(3).getStringCellValue();
                AssessmentStatusEnum statusEnum;
                if (status == null) {
                    throw new NullPointerException();
                } else if (status.isEmpty()) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 3, row " + (i + 1) + ", column 4\n";
                    System.out.println("\tMissing entry in sheet 3, row " + (i + 1) + ", column 4");
                    continue;
                } else if (status.charAt(1) == 'u') {
                    statusEnum = AssessmentStatusEnum.SUBMITTED;
                } else if (status.charAt(1) == 'p') {
                    statusEnum = AssessmentStatusEnum.APPROVED;
                } else if (status.charAt(1) == 's') {
                    statusEnum = AssessmentStatusEnum.ASSIGNED;
                } else {
                    statusEnum = AssessmentStatusEnum.REJECTED;
                }
                assessment.setStatus(statusEnum);

                try {
                    assessment.setAssessmentDate(new SimpleDateFormat("dd/MM/yyyy").parse(r.getCell(6).getStringCellValue()));
                } catch (ParseException e) {
                    to_persist = false;
                    upload_details += "Incorrect entry in sheet 3, row " + (i + 1) + ", column 7\n";
                    System.out.println("\tIncorrect entry in sheet 3, row " + (i + 1) + ", column 7");
                    continue;
                }

                String reason = (r.getCell(5) == null) ? null : r.getCell(5).getStringCellValue();
                AssessmentReasonEnum reasonEnum;
                if (reason == null) {
                    throw new NullPointerException();
                } else if (reason.charAt(0) == 'I') {
                    reasonEnum = AssessmentReasonEnum.INITIAL;
                } else if (reason.charAt(0) == 'R') {
                    reasonEnum = AssessmentReasonEnum.REASSESSMENT;
                } else if (reason.charAt(0) == 'D') {
                    reasonEnum = AssessmentReasonEnum.DISCHARGE;
                } else if (reason.charAt(0) == 'C') {
                    reasonEnum = AssessmentReasonEnum.CRITICAL_INCIDENT;
                } else {
                    reasonEnum = AssessmentReasonEnum.TRANSFER;
                }
                assessment.setReason(reasonEnum);

                if (statusEnum == AssessmentStatusEnum.APPROVED) {
                    try {
                        assessment.setApprovedDate(new SimpleDateFormat("dd/MM/yyyy").parse(r.getCell(7).getStringCellValue()));
                    } catch (ParseException e) {
                        to_persist = false;
                        upload_details += "Incorrect entry in sheet 3, row " + (i + 1) + ", column 8\n";
                        System.out.println("\tIncorrect entry in sheet 3, row " + (i + 1) + ", column 8");
                        continue;
                    }
                    assessment.setLoc((int) r.getCell(8).getNumericCellValue());

                } else if (statusEnum == AssessmentStatusEnum.SUBMITTED || statusEnum == AssessmentStatusEnum.REJECTED) {
                    assessment.setLoc((int) r.getCell(8).getNumericCellValue());
                } else {
                    assessment.setApprovedDate(null);
                    assessment.setLoc(-1); //insert dummy algo + caretaker dummy algo -jh //also the loc is also stated in rawdata cell col 8
                    //client loc need to be updated after assessment gets persisted
                }
                if (new_entity) {
                    em.persist(assessment);
                }

                List<ResponseEntity> new_responses = new ArrayList<>();

                try {

                    ResponseEntity childSkillResponse = new ResponseEntity((r.getCell(618).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(618).getNumericCellValue(), (r.getCell(619) == null) ? null : r.getCell(619).getStringCellValue());
                    QuestionEntity question = questionsSessionBean.retrieveQuestionByCode("1.1");
                    childSkillResponse.setQuestion(question);
                    new_responses.add(childSkillResponse);

                    ResponseEntity playEngagement = new ResponseEntity((r.getCell(824).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(824).getNumericCellValue(), (r.getCell(825) == null) ? null : r.getCell(825).getStringCellValue());
                    QuestionEntity question1 = questionsSessionBean.retrieveQuestionByCode("CS1");
                    playEngagement.setQuestion(question1);
                    new_responses.add(playEngagement);

                    ResponseEntity communication = new ResponseEntity((r.getCell(826).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(826).getNumericCellValue(), (r.getCell(827) == null) ? null : r.getCell(827).getStringCellValue());
                    QuestionEntity question2 = questionsSessionBean.retrieveQuestionByCode("CS2");
                    communication.setQuestion(question2);
                    new_responses.add(communication);

                    ResponseEntity motor = new ResponseEntity((r.getCell(828).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(828).getNumericCellValue(), (r.getCell(829) == null) ? null : r.getCell(829).getStringCellValue());
                    QuestionEntity question3 = questionsSessionBean.retrieveQuestionByCode("CS3");
                    motor.setQuestion(question3);
                    new_responses.add(motor);

                    ResponseEntity problemSolving = new ResponseEntity((r.getCell(830).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(830).getNumericCellValue(), (r.getCell(831) == null) ? null : r.getCell(831).getStringCellValue());
                    QuestionEntity question4 = questionsSessionBean.retrieveQuestionByCode("CS4");
                    problemSolving.setQuestion(question4);
                    new_responses.add(problemSolving);

                    ResponseEntity personalSocial = new ResponseEntity((r.getCell(832).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(832).getNumericCellValue(), (r.getCell(833) == null) ? null : r.getCell(833).getStringCellValue());
                    QuestionEntity question5 = questionsSessionBean.retrieveQuestionByCode("CS5");
                    personalSocial.setQuestion(question5);
                    new_responses.add(personalSocial);

                    ResponseEntity socialEmotional = new ResponseEntity((r.getCell(620).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(620).getNumericCellValue(), (r.getCell(621) == null) ? null : r.getCell(621).getStringCellValue());
                    QuestionEntity question6 = questionsSessionBean.retrieveQuestionByCode("1.2");
                    socialEmotional.setQuestion(question6);
                    new_responses.add(socialEmotional);

                    ResponseEntity interaction = new ResponseEntity((r.getCell(622).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(622).getNumericCellValue(), (r.getCell(623) == null) ? null : r.getCell(623).getStringCellValue());
                    QuestionEntity question7 = questionsSessionBean.retrieveQuestionByCode("1.3");
                    interaction.setQuestion(question7);
                    new_responses.add(interaction);

                    ResponseEntity access = new ResponseEntity((r.getCell(624).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(624).getNumericCellValue(), (r.getCell(625) == null) ? null : r.getCell(625).getStringCellValue());
                    QuestionEntity question8 = questionsSessionBean.retrieveQuestionByCode("1.4");
                    access.setQuestion(question8);
                    new_responses.add(access);

                    ResponseEntity qualityOfPlay = new ResponseEntity((r.getCell(626).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(626).getNumericCellValue(), (r.getCell(627) == null) ? null : r.getCell(627).getStringCellValue());
                    QuestionEntity question9 = questionsSessionBean.retrieveQuestionByCode("1.5");
                    qualityOfPlay.setQuestion(question9);
                    new_responses.add(qualityOfPlay);

                    ResponseEntity exposure = new ResponseEntity((r.getCell(628).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(628).getNumericCellValue(), (r.getCell(629) == null) ? null : r.getCell(629).getStringCellValue());
                    QuestionEntity question10 = questionsSessionBean.retrieveQuestionByCode("1.6");
                    exposure.setQuestion(question10);
                    new_responses.add(exposure);

                    ResponseEntity needs = new ResponseEntity((r.getCell(630).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(630).getNumericCellValue(), (r.getCell(631) == null) ? null : r.getCell(631).getStringCellValue());
                    QuestionEntity question11 = questionsSessionBean.retrieveQuestionByCode("1.7");
                    needs.setQuestion(question11);
                    new_responses.add(needs);

                    ResponseEntity suitability = new ResponseEntity((r.getCell(836).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(836).getNumericCellValue(), (r.getCell(837) == null) ? null : r.getCell(837).getStringCellValue());
                    QuestionEntity question12 = questionsSessionBean.retrieveQuestionByCode("PCN1");
                    suitability.setQuestion(question12);
                    new_responses.add(suitability);

                    ResponseEntity childFunctioning = new ResponseEntity((r.getCell(838).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(838).getNumericCellValue(), (r.getCell(839) == null) ? null : r.getCell(839).getStringCellValue());
                    QuestionEntity questionPCN2 = questionsSessionBean.retrieveQuestionByCode("PCN2");
                    childFunctioning.setQuestion(questionPCN2);
                    new_responses.add(childFunctioning);

                    ResponseEntity attendance = new ResponseEntity((r.getCell(840).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(840).getNumericCellValue(), (r.getCell(841) == null) ? null : r.getCell(841).getStringCellValue());
                    QuestionEntity questionPCN3 = questionsSessionBean.retrieveQuestionByCode("PCN3");
                    attendance.setQuestion(questionPCN3);
                    new_responses.add(attendance);

                    //Life-functioning, would only use 1 response entity for this segment
                    ResponseEntity lifeFunctioning = new ResponseEntity((r.getCell(634).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(634).getNumericCellValue(), (r.getCell(635) == null) ? null : r.getCell(635).getStringCellValue());
                    QuestionEntity question16 = questionsSessionBean.retrieveQuestionByCode("2.1");
                    lifeFunctioning.setQuestion(question16);
                    new_responses.add(lifeFunctioning);
                    ResponseEntity LF2_2 = new ResponseEntity((r.getCell(636).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(636).getNumericCellValue(), (r.getCell(637) == null) ? null : r.getCell(637).getStringCellValue());
                    QuestionEntity question17 = questionsSessionBean.retrieveQuestionByCode("2.1");
                    LF2_2.setQuestion(question17);
                    new_responses.add(LF2_2);
                    ResponseEntity LF2_3 = new ResponseEntity((r.getCell(638).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(638).getNumericCellValue(), (r.getCell(639) == null) ? null : r.getCell(639).getStringCellValue());
                    QuestionEntity question18 = questionsSessionBean.retrieveQuestionByCode("2.3");
                    LF2_3.setQuestion(question18);
                    new_responses.add(LF2_3);
                    ResponseEntity LF2_4 = new ResponseEntity((r.getCell(640).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(640).getNumericCellValue(), (r.getCell(641) == null) ? null : r.getCell(641).getStringCellValue());
                    QuestionEntity question19 = questionsSessionBean.retrieveQuestionByCode("2.4");
                    LF2_4.setQuestion(question19);
                    new_responses.add(LF2_4);
                    ResponseEntity LF2_5 = new ResponseEntity((r.getCell(642).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(642).getNumericCellValue(), (r.getCell(643) == null) ? null : r.getCell(643).getStringCellValue());
                    QuestionEntity question20 = questionsSessionBean.retrieveQuestionByCode("2.5");
                    LF2_5.setQuestion(question20);
                    new_responses.add(LF2_5);
                    ResponseEntity LFDN1 = new ResponseEntity((r.getCell(844).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(844).getNumericCellValue(), (r.getCell(845) == null) ? null : r.getCell(845).getStringCellValue());
                    QuestionEntity question21 = questionsSessionBean.retrieveQuestionByCode("DN1");
                    LFDN1.setQuestion(question21);
                    new_responses.add(LFDN1);
                    ResponseEntity LFDN2 = new ResponseEntity((r.getCell(846).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(846).getNumericCellValue(), (r.getCell(847) == null) ? null : r.getCell(847).getStringCellValue());
                    QuestionEntity question22 = questionsSessionBean.retrieveQuestionByCode("DN2");
                    LFDN2.setQuestion(question22);
                    new_responses.add(LFDN2);
                    ResponseEntity LFDN3 = new ResponseEntity((r.getCell(848).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(848).getNumericCellValue(), (r.getCell(849) == null) ? null : r.getCell(849).getStringCellValue());
                    QuestionEntity question23 = questionsSessionBean.retrieveQuestionByCode("DN3");
                    LFDN3.setQuestion(question23);
                    new_responses.add(LFDN3);
                    ResponseEntity LFDN4 = new ResponseEntity((r.getCell(850).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(850).getNumericCellValue(), (r.getCell(851) == null) ? null : r.getCell(851).getStringCellValue());
                    QuestionEntity question24 = questionsSessionBean.retrieveQuestionByCode("DN4");
                    LFDN4.setQuestion(question24);
                    new_responses.add(LFDN4);

                    ResponseEntity LF2_6 = new ResponseEntity((r.getCell(644).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(644).getNumericCellValue(), (r.getCell(645) == null) ? null : r.getCell(645).getStringCellValue());
                    QuestionEntity question25 = questionsSessionBean.retrieveQuestionByCode("2.6");
                    LF2_6.setQuestion(question25);
                    new_responses.add(LF2_6);

                    ResponseEntity LF2_7 = new ResponseEntity((r.getCell(646).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(646).getNumericCellValue(), (r.getCell(647) == null) ? null : r.getCell(647).getStringCellValue());
                    QuestionEntity question26 = questionsSessionBean.retrieveQuestionByCode("2.7");
                    LF2_7.setQuestion(question26);
                    new_responses.add(LF2_7);

                    ResponseEntity LF2_8 = new ResponseEntity((r.getCell(648).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(648).getNumericCellValue(), (r.getCell(649) == null) ? null : r.getCell(649).getStringCellValue());
                    QuestionEntity question27 = questionsSessionBean.retrieveQuestionByCode("2.8");
                    LF2_8.setQuestion(question27);
                    new_responses.add(LF2_8);

                    ResponseEntity LF2_9 = new ResponseEntity((r.getCell(650).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(650).getNumericCellValue(), (r.getCell(651) == null) ? null : r.getCell(651).getStringCellValue());
                    QuestionEntity question28 = questionsSessionBean.retrieveQuestionByCode("2.9");
                    LF2_9.setQuestion(question28);
                    new_responses.add(LF2_9);

                    //Medical Physical
                    ResponseEntity medicalPhysical = new ResponseEntity((r.getCell(654).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(654).getNumericCellValue(), (r.getCell(655) == null) ? null : r.getCell(655).getStringCellValue());
                    QuestionEntity question29 = questionsSessionBean.retrieveQuestionByCode("3.1");
                    medicalPhysical.setQuestion(question29);
                    new_responses.add(medicalPhysical);
                    ResponseEntity oralHealth = new ResponseEntity((r.getCell(656).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(656).getNumericCellValue(), (r.getCell(657) == null) ? null : r.getCell(657).getStringCellValue());
                    QuestionEntity question30 = questionsSessionBean.retrieveQuestionByCode("3.2");
                    oralHealth.setQuestion(question30);
                    new_responses.add(oralHealth);
                    ResponseEntity sleep = new ResponseEntity((r.getCell(658).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(658).getNumericCellValue(), (r.getCell(659) == null) ? null : r.getCell(659).getStringCellValue());
                    QuestionEntity question31 = questionsSessionBean.retrieveQuestionByCode("3.3");
                    sleep.setQuestion(question31);
                    new_responses.add(sleep);
                    ResponseEntity preDisposing = new ResponseEntity((r.getCell(660).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(660).getNumericCellValue(), (r.getCell(661) == null) ? null : r.getCell(661).getStringCellValue());
                    QuestionEntity question32 = questionsSessionBean.retrieveQuestionByCode("3.4");
                    preDisposing.setQuestion(question32);
                    new_responses.add(preDisposing);
                    ResponseEntity birthWeight = new ResponseEntity((r.getCell(854).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(854).getNumericCellValue(), (r.getCell(855) == null) ? null : r.getCell(855).getStringCellValue());
                    QuestionEntity question33 = questionsSessionBean.retrieveQuestionByCode("PRF1");
                    birthWeight.setQuestion(question33);
                    new_responses.add(birthWeight);
                    ResponseEntity antenatalCare = new ResponseEntity((r.getCell(856).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(856).getNumericCellValue(), (r.getCell(857) == null) ? null : r.getCell(857).getStringCellValue());
                    QuestionEntity question34 = questionsSessionBean.retrieveQuestionByCode("PRF2");
                    antenatalCare.setQuestion(question34);
                    new_responses.add(antenatalCare);
                    ResponseEntity labour = new ResponseEntity((r.getCell(858).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(858).getNumericCellValue(), (r.getCell(859) == null) ? null : r.getCell(859).getStringCellValue());
                    QuestionEntity question35 = questionsSessionBean.retrieveQuestionByCode("PRF3");
                    labour.setQuestion(question35);
                    new_responses.add(labour);
                    ResponseEntity substance = new ResponseEntity((r.getCell(860).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(860).getNumericCellValue(), (r.getCell(861) == null) ? null : r.getCell(861).getStringCellValue());
                    QuestionEntity question36 = questionsSessionBean.retrieveQuestionByCode("PRF4");
                    substance.setQuestion(question36);
                    new_responses.add(substance);
                    ResponseEntity parentSibling = new ResponseEntity((r.getCell(862).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(862).getNumericCellValue(), (r.getCell(863) == null) ? null : r.getCell(863).getStringCellValue());
                    QuestionEntity question37 = questionsSessionBean.retrieveQuestionByCode("PRF5");
                    parentSibling.setQuestion(question37);
                    new_responses.add(parentSibling);
                    ResponseEntity immunisation = new ResponseEntity((r.getCell(662).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(662).getNumericCellValue(), (r.getCell(663) == null) ? null : r.getCell(663).getStringCellValue());
                    QuestionEntity question00 = questionsSessionBean.retrieveQuestionByCode("3.5");
                    immunisation.setQuestion(question00);
                    new_responses.add(immunisation);
                    ResponseEntity eatingRoutine = new ResponseEntity((r.getCell(664).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(664).getNumericCellValue(), (r.getCell(665) == null) ? null : r.getCell(665).getStringCellValue());
                    QuestionEntity question38 = questionsSessionBean.retrieveQuestionByCode("3.6");
                    eatingRoutine.setQuestion(question38);
                    new_responses.add(eatingRoutine);
                    ResponseEntity problemsEating = new ResponseEntity((r.getCell(666).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(666).getNumericCellValue(), (r.getCell(667) == null) ? null : r.getCell(667).getStringCellValue());
                    QuestionEntity question39 = questionsSessionBean.retrieveQuestionByCode("3.7");
                    problemsEating.setQuestion(question39);
                    new_responses.add(problemsEating);
                    ResponseEntity elimination = new ResponseEntity((r.getCell(668).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(668).getNumericCellValue(), (r.getCell(669) == null) ? null : r.getCell(669).getStringCellValue());
                    QuestionEntity question40 = questionsSessionBean.retrieveQuestionByCode("3.8");
                    elimination.setQuestion(question40);
                    new_responses.add(elimination);
                    ResponseEntity failureToThrive = new ResponseEntity((r.getCell(670).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(670).getNumericCellValue(), (r.getCell(671) == null) ? null : r.getCell(671).getStringCellValue());
                    QuestionEntity question41 = questionsSessionBean.retrieveQuestionByCode("3.9");
                    failureToThrive.setQuestion(question41);
                    new_responses.add(failureToThrive);

                    //School
                    ResponseEntity schoolBehaviour = new ResponseEntity((r.getCell(328).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(328).getNumericCellValue(), (r.getCell(329) == null) ? null : r.getCell(329).getStringCellValue());
                    QuestionEntity question42 = questionsSessionBean.retrieveQuestionByCode("4.1");
                    schoolBehaviour.setQuestion(question42);
                    new_responses.add(schoolBehaviour);
                    ResponseEntity schoolPerformance = new ResponseEntity((r.getCell(332).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(332).getNumericCellValue(), (r.getCell(333) == null) ? null : r.getCell(333).getStringCellValue());
                    QuestionEntity question43 = questionsSessionBean.retrieveQuestionByCode("4.2");
                    schoolPerformance.setQuestion(question43);
                    new_responses.add(schoolPerformance);
                    ResponseEntity schoolAttendance = new ResponseEntity((r.getCell(336).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(336).getNumericCellValue(), (r.getCell(337) == null) ? null : r.getCell(337).getStringCellValue());
                    QuestionEntity question44 = questionsSessionBean.retrieveQuestionByCode("4.3");
                    schoolAttendance.setQuestion(question44);
                    new_responses.add(schoolAttendance);
                    ResponseEntity readingWriting = new ResponseEntity((r.getCell(340).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(340).getNumericCellValue(), (r.getCell(341) == null) ? null : r.getCell(341).getStringCellValue());
                    QuestionEntity question45 = questionsSessionBean.retrieveQuestionByCode("4.4");
                    readingWriting.setQuestion(question45);
                    new_responses.add(readingWriting);

                    //Behavioural and emotional needs
                    ResponseEntity behaviouralEmotional = new ResponseEntity((r.getCell(674).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(674).getNumericCellValue(), (r.getCell(675) == null) ? null : r.getCell(675).getStringCellValue());
                    QuestionEntity question46 = questionsSessionBean.retrieveQuestionByCode("5.1");
                    behaviouralEmotional.setQuestion(question46);
                    new_responses.add(behaviouralEmotional);
                    ResponseEntity behaviouralEmotional1 = new ResponseEntity((r.getCell(676).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(676).getNumericCellValue(), (r.getCell(677) == null) ? null : r.getCell(677).getStringCellValue());
                        QuestionEntity question47 = questionsSessionBean.retrieveQuestionByCode("5.2");
                    behaviouralEmotional1.setQuestion(question47);
                    new_responses.add(behaviouralEmotional1);
                    ResponseEntity behaviouralEmotional2 = new ResponseEntity((r.getCell(678).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(678).getNumericCellValue(), (r.getCell(679) == null) ? null : r.getCell(679).getStringCellValue());
                    QuestionEntity question48 = questionsSessionBean.retrieveQuestionByCode("5.3");
                    behaviouralEmotional2.setQuestion(question48);
                    new_responses.add(behaviouralEmotional2);
                    ResponseEntity behaviouralEmotional3 = new ResponseEntity((r.getCell(680).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(680).getNumericCellValue(), (r.getCell(681) == null) ? null : r.getCell(681).getStringCellValue());
                    QuestionEntity question49 = questionsSessionBean.retrieveQuestionByCode("5.4");
                    behaviouralEmotional3.setQuestion(question49);
                    new_responses.add(behaviouralEmotional3);
                    ResponseEntity behaviouralEmotional4 = new ResponseEntity((r.getCell(682).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(682).getNumericCellValue(), (r.getCell(683) == null) ? null : r.getCell(683).getStringCellValue());
                    QuestionEntity question50 = questionsSessionBean.retrieveQuestionByCode("5.5");
                    behaviouralEmotional4.setQuestion(question50);
                    new_responses.add(behaviouralEmotional4);
                    ResponseEntity behaviouralEmotional5 = new ResponseEntity((r.getCell(684).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(684).getNumericCellValue(), (r.getCell(685) == null) ? null : r.getCell(685).getStringCellValue());
                    QuestionEntity question51 = questionsSessionBean.retrieveQuestionByCode("5.6");
                    behaviouralEmotional5.setQuestion(question51);
                    new_responses.add(behaviouralEmotional5);
                    ResponseEntity behaviouralEmotional6 = new ResponseEntity((r.getCell(686).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(686).getNumericCellValue(), (r.getCell(687) == null) ? null : r.getCell(687).getStringCellValue());
                    QuestionEntity question52 = questionsSessionBean.retrieveQuestionByCode("5.7");
                    behaviouralEmotional6.setQuestion(question52);
                    new_responses.add(behaviouralEmotional6);
                    ResponseEntity behaviouralEmotional7 = new ResponseEntity((r.getCell(688).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(688).getNumericCellValue(), (r.getCell(689) == null) ? null : r.getCell(689).getStringCellValue());
                    QuestionEntity question53 = questionsSessionBean.retrieveQuestionByCode("5.8");
                    behaviouralEmotional7.setQuestion(question53);
                    new_responses.add(behaviouralEmotional7);
                    ResponseEntity behaviouralEmotional8 = new ResponseEntity((r.getCell(866).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(866).getNumericCellValue(), (r.getCell(867) == null) ? null : r.getCell(867).getStringCellValue());
                    QuestionEntity question54 = questionsSessionBean.retrieveQuestionByCode("SN1");
                    behaviouralEmotional8.setQuestion(question54);
                    new_responses.add(behaviouralEmotional8);
                    ResponseEntity behaviouralEmotional9 = new ResponseEntity((r.getCell(868).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(868).getNumericCellValue(), (r.getCell(869) == null) ? null : r.getCell(869).getStringCellValue());
                    QuestionEntity question55 = questionsSessionBean.retrieveQuestionByCode("SN2");
                    behaviouralEmotional9.setQuestion(question55);
                    new_responses.add(behaviouralEmotional9);
                    ResponseEntity behaviouralEmotional10 = new ResponseEntity((r.getCell(870).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(870).getNumericCellValue(), (r.getCell(871) == null) ? null : r.getCell(871).getStringCellValue());
                    QuestionEntity question56 = questionsSessionBean.retrieveQuestionByCode("SN3");
                    behaviouralEmotional10.setQuestion(question56);
                    new_responses.add(behaviouralEmotional10);
                    ResponseEntity behaviouralEmotional11 = new ResponseEntity((r.getCell(872).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(872).getNumericCellValue(), (r.getCell(873) == null) ? null : r.getCell(873).getStringCellValue());
                    QuestionEntity question57 = questionsSessionBean.retrieveQuestionByCode("SN4");
                    behaviouralEmotional11.setQuestion(question57);
                    new_responses.add(behaviouralEmotional11);
                    ResponseEntity behaviouralEmotional12 = new ResponseEntity((r.getCell(874).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(874).getNumericCellValue(), (r.getCell(875) == null) ? null : r.getCell(875).getStringCellValue());
                    QuestionEntity question58 = questionsSessionBean.retrieveQuestionByCode("SN5");
                    behaviouralEmotional12.setQuestion(question58);
                    new_responses.add(behaviouralEmotional12);
                    ResponseEntity behaviouralEmotional13 = new ResponseEntity((r.getCell(876).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(876).getNumericCellValue(), (r.getCell(877) == null) ? null : r.getCell(877).getStringCellValue());
                    QuestionEntity question59 = questionsSessionBean.retrieveQuestionByCode("SN6");
                    behaviouralEmotional13.setQuestion(question59);
                    new_responses.add(behaviouralEmotional13);
                    ResponseEntity behaviouralEmotional14 = new ResponseEntity((r.getCell(690).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(690).getNumericCellValue(), (r.getCell(691) == null) ? null : r.getCell(691).getStringCellValue());
                    QuestionEntity question60 = questionsSessionBean.retrieveQuestionByCode("5.9");
                    behaviouralEmotional14.setQuestion(question60);
                    new_responses.add(behaviouralEmotional14);
                    ResponseEntity behaviouralEmotional15 = new ResponseEntity((r.getCell(692).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(692).getNumericCellValue(), (r.getCell(693) == null) ? null : r.getCell(693).getStringCellValue());
                    QuestionEntity question61 = questionsSessionBean.retrieveQuestionByCode("5.10");
                    behaviouralEmotional15.setQuestion(question61);
                    new_responses.add(behaviouralEmotional15);
                    ResponseEntity behaviouralEmotional16 = new ResponseEntity((r.getCell(694).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(694).getNumericCellValue(), (r.getCell(695) == null) ? null : r.getCell(695).getStringCellValue());
                    QuestionEntity question62 = questionsSessionBean.retrieveQuestionByCode("5.11");
                    behaviouralEmotional16.setQuestion(question62);
                    new_responses.add(behaviouralEmotional16);
                    ResponseEntity behaviouralEmotional17 = new ResponseEntity((r.getCell(696).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(696).getNumericCellValue(), (r.getCell(697) == null) ? null : r.getCell(697).getStringCellValue());
                    QuestionEntity question63 = questionsSessionBean.retrieveQuestionByCode("5.12");
                    behaviouralEmotional17.setQuestion(question63);
                    new_responses.add(behaviouralEmotional17);

                    //Child risks behaviours
                    ResponseEntity childRisk = new ResponseEntity((r.getCell(348).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(348).getNumericCellValue(), (r.getCell(349) == null) ? null : r.getCell(349).getStringCellValue());
                    QuestionEntity question64 = questionsSessionBean.retrieveQuestionByCode("6.1");
                    childRisk.setQuestion(question64);
                    new_responses.add(childRisk);
                    ResponseEntity childRisk1 = new ResponseEntity((r.getCell(800).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(800).getNumericCellValue(), (r.getCell(801) == null) ? null : r.getCell(801).getStringCellValue());
                    QuestionEntity question65 = questionsSessionBean.retrieveQuestionByCode("TR1");
                    childRisk1.setQuestion(question65);
                    new_responses.add(childRisk1);
                    ResponseEntity childRisk2 = new ResponseEntity((r.getCell(802).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(802).getNumericCellValue(), (r.getCell(803) == null) ? null : r.getCell(803).getStringCellValue());

                    QuestionEntity question66 = questionsSessionBean.retrieveQuestionByCode("TR2");
                    childRisk2.setQuestion(question66);
                    new_responses.add(childRisk2);
                    ResponseEntity childRisk3 = new ResponseEntity((r.getCell(804).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(804).getNumericCellValue(), (r.getCell(805) == null) ? null : r.getCell(805).getStringCellValue());

                    QuestionEntity question67 = questionsSessionBean.retrieveQuestionByCode("TR3");
                    childRisk3.setQuestion(question67);
                    new_responses.add(childRisk3);
                    ResponseEntity childRisk4 = new ResponseEntity((r.getCell(806).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(806).getNumericCellValue(), (r.getCell(807) == null) ? null : r.getCell(807).getStringCellValue());

                    QuestionEntity question68 = questionsSessionBean.retrieveQuestionByCode("TR4");
                    childRisk4.setQuestion(question68);
                    new_responses.add(childRisk4);
                    ResponseEntity childRisk5 = new ResponseEntity((r.getCell(808).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(808).getNumericCellValue(), (r.getCell(809) == null) ? null : r.getCell(809).getStringCellValue());

                    QuestionEntity question69 = questionsSessionBean.retrieveQuestionByCode("TR4a");
                    childRisk5.setQuestion(question69);
                    new_responses.add(childRisk5);
                    ResponseEntity childRisk6 = new ResponseEntity((r.getCell(810).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(810).getNumericCellValue(), (r.getCell(811) == null) ? null : r.getCell(811).getStringCellValue());

                    QuestionEntity question70 = questionsSessionBean.retrieveQuestionByCode("TR4b");
                    childRisk6.setQuestion(question70);
                    new_responses.add(childRisk6);
                    ResponseEntity childRisk7 = new ResponseEntity((r.getCell(812).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(812).getNumericCellValue(), (r.getCell(813) == null) ? null : r.getCell(813).getStringCellValue());

                    QuestionEntity question71 = questionsSessionBean.retrieveQuestionByCode("TR4c");
                    childRisk7.setQuestion(question71);
                    new_responses.add(childRisk7);
                    ResponseEntity childRisk8 = new ResponseEntity((r.getCell(814).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(814).getNumericCellValue(), (r.getCell(815) == null) ? null : r.getCell(815).getStringCellValue());

                    QuestionEntity question72 = questionsSessionBean.retrieveQuestionByCode("TR5");
                    childRisk8.setQuestion(question72);
                    new_responses.add(childRisk8);
                    ResponseEntity childRisk9 = new ResponseEntity((r.getCell(816).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(816).getNumericCellValue(), (r.getCell(817) == null) ? null : r.getCell(817).getStringCellValue());

                    QuestionEntity question73 = questionsSessionBean.retrieveQuestionByCode("TR6");
                    childRisk9.setQuestion(question73);
                    new_responses.add(childRisk9);
                    ResponseEntity childRisk10 = new ResponseEntity((r.getCell(818).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(818).getNumericCellValue(), (r.getCell(819) == null) ? null : r.getCell(819).getStringCellValue());

                    QuestionEntity question74 = questionsSessionBean.retrieveQuestionByCode("TR7");
                    childRisk10.setQuestion(question74);
                    new_responses.add(childRisk10);
                    ResponseEntity childRisk11 = new ResponseEntity((r.getCell(820).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(820).getNumericCellValue(), (r.getCell(821) == null) ? null : r.getCell(821).getStringCellValue());

                    QuestionEntity question75 = questionsSessionBean.retrieveQuestionByCode("TR8");
                    childRisk11.setQuestion(question75);
                    new_responses.add(childRisk11);
                    ResponseEntity childRisk12 = new ResponseEntity((r.getCell(354).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(354).getNumericCellValue(), (r.getCell(355) == null) ? null : r.getCell(355).getStringCellValue());

                    QuestionEntity question76 = questionsSessionBean.retrieveQuestionByCode("6.2");
                    childRisk12.setQuestion(question76);
                    new_responses.add(childRisk12);
                    ResponseEntity childRisk13 = new ResponseEntity((r.getCell(352).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(352).getNumericCellValue(), (r.getCell(353) == null) ? null : r.getCell(353).getStringCellValue());

                    QuestionEntity question77 = questionsSessionBean.retrieveQuestionByCode("6.3");
                    childRisk13.setQuestion(question77);
                    new_responses.add(childRisk13);
                    ResponseEntity childRisk14 = new ResponseEntity((r.getCell(344).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(344).getNumericCellValue(), (r.getCell(345) == null) ? null : r.getCell(345).getStringCellValue());

                    QuestionEntity question78 = questionsSessionBean.retrieveQuestionByCode("6.4");
                    childRisk14.setQuestion(question78);
                    new_responses.add(childRisk14);
                    ResponseEntity childRisk15 = new ResponseEntity((r.getCell(350).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(350).getNumericCellValue(), (r.getCell(351) == null) ? null : r.getCell(351).getStringCellValue());

                    QuestionEntity question79 = questionsSessionBean.retrieveQuestionByCode("6.5");
                    childRisk15.setQuestion(question79);
                    new_responses.add(childRisk15);
                    ResponseEntity childRisk16 = new ResponseEntity((r.getCell(378).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(378).getNumericCellValue(), (r.getCell(379) == null) ? null : r.getCell(379).getStringCellValue());

                    QuestionEntity question80 = questionsSessionBean.retrieveQuestionByCode("6.6");
                    childRisk16.setQuestion(question80);
                    new_responses.add(childRisk16);
                    ResponseEntity childRisk17 = new ResponseEntity((r.getCell(880).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(880).getNumericCellValue(), (r.getCell(881) == null) ? null : r.getCell(881).getStringCellValue());

                    QuestionEntity question81 = questionsSessionBean.retrieveQuestionByCode("RN1");
                    childRisk17.setQuestion(question81);
                    new_responses.add(behaviouralEmotional17);
                    ResponseEntity childRisk18 = new ResponseEntity((r.getCell(882).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(882).getNumericCellValue(), (r.getCell(883) == null) ? null : r.getCell(883).getStringCellValue());

                    QuestionEntity question82 = questionsSessionBean.retrieveQuestionByCode("RN2");
                    childRisk18.setQuestion(question82);
                    new_responses.add(childRisk18);
                    ResponseEntity childRisk19 = new ResponseEntity((r.getCell(884).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(884).getNumericCellValue(), (r.getCell(885) == null) ? null : r.getCell(885).getStringCellValue());

                    QuestionEntity question83 = questionsSessionBean.retrieveQuestionByCode("RN3");
                    childRisk19.setQuestion(question83);
                    new_responses.add(childRisk19);
                    ResponseEntity childRisk20 = new ResponseEntity((r.getCell(886).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(886).getNumericCellValue(), (r.getCell(887) == null) ? null : r.getCell(887).getStringCellValue());

                    QuestionEntity question84 = questionsSessionBean.retrieveQuestionByCode("RN4");
                    childRisk20.setQuestion(question84);
                    new_responses.add(childRisk20);
                    ResponseEntity childRisk21 = new ResponseEntity((r.getCell(888).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(888).getNumericCellValue(), (r.getCell(889) == null) ? null : r.getCell(889).getStringCellValue());

                    QuestionEntity question85 = questionsSessionBean.retrieveQuestionByCode("RN5");
                    childRisk21.setQuestion(question85);
                    new_responses.add(childRisk21);
                    ResponseEntity childRisk22 = new ResponseEntity((r.getCell(890).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(890).getNumericCellValue(), (r.getCell(891) == null) ? null : r.getCell(891).getStringCellValue());

                    QuestionEntity question86 = questionsSessionBean.retrieveQuestionByCode("RN6");
                    childRisk22.setQuestion(question86);
                    new_responses.add(childRisk22);
                    ResponseEntity childRisk23 = new ResponseEntity((r.getCell(892).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(892).getNumericCellValue(), (r.getCell(893) == null) ? null : r.getCell(893).getStringCellValue());

                    QuestionEntity question87 = questionsSessionBean.retrieveQuestionByCode("RN7");
                    childRisk23.setQuestion(question87);
                    new_responses.add(childRisk23);
                    ResponseEntity childRisk24 = new ResponseEntity((r.getCell(894).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(894).getNumericCellValue(), (r.getCell(895) == null) ? null : r.getCell(895).getStringCellValue());

                    QuestionEntity question88 = questionsSessionBean.retrieveQuestionByCode("RN8");
                    childRisk24.setQuestion(question88);
                    new_responses.add(childRisk24);
                    ResponseEntity childRisk25 = new ResponseEntity((r.getCell(384).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(384).getNumericCellValue(), (r.getCell(385) == null) ? null : r.getCell(385).getStringCellValue());

                    QuestionEntity question89 = questionsSessionBean.retrieveQuestionByCode("6.7");
                    childRisk25.setQuestion(question89);
                    new_responses.add(childRisk25);
                    ResponseEntity childRisk26 = new ResponseEntity((r.getCell(898).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(898).getNumericCellValue(), (r.getCell(899) == null) ? null : r.getCell(899).getStringCellValue());

                    QuestionEntity question90 = questionsSessionBean.retrieveQuestionByCode("JJN1");
                    childRisk26.setQuestion(question90);
                    new_responses.add(childRisk26);
                    ResponseEntity childRisk27 = new ResponseEntity((r.getCell(900).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(900).getNumericCellValue(), (r.getCell(901) == null) ? null : r.getCell(901).getStringCellValue());

                    QuestionEntity question91 = questionsSessionBean.retrieveQuestionByCode("JJN2");
                    childRisk27.setQuestion(question91);
                    new_responses.add(childRisk27);
                    ResponseEntity childRisk28 = new ResponseEntity((r.getCell(904).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(904).getNumericCellValue(), (r.getCell(905) == null) ? null : r.getCell(905).getStringCellValue());

                    QuestionEntity question92 = questionsSessionBean.retrieveQuestionByCode("JJN3");
                    childRisk28.setQuestion(question92);
                    new_responses.add(childRisk28);
                    ResponseEntity childRisk29 = new ResponseEntity((r.getCell(902).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(902).getNumericCellValue(), (r.getCell(903) == null) ? null : r.getCell(903).getStringCellValue());

                    QuestionEntity question93 = questionsSessionBean.retrieveQuestionByCode("JJN4");
                    childRisk29.setQuestion(question93);
                    new_responses.add(childRisk29);
                    ResponseEntity childRisk30 = new ResponseEntity((r.getCell(906).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(906).getNumericCellValue(), (r.getCell(907) == null) ? null : r.getCell(907).getStringCellValue());

                    QuestionEntity question94 = questionsSessionBean.retrieveQuestionByCode("JJN5");
                    childRisk30.setQuestion(question94);
                    new_responses.add(childRisk30);
                    ResponseEntity childRisk31 = new ResponseEntity((r.getCell(908).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(908).getNumericCellValue(), (r.getCell(909) == null) ? null : r.getCell(909).getStringCellValue());

                    QuestionEntity question95 = questionsSessionBean.retrieveQuestionByCode("JJN6");
                    childRisk31.setQuestion(question95);
                    new_responses.add(childRisk31);
                    ResponseEntity childRisk32 = new ResponseEntity((r.getCell(910).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(910).getNumericCellValue(), (r.getCell(911) == null) ? null : r.getCell(911).getStringCellValue());

                    QuestionEntity question96 = questionsSessionBean.retrieveQuestionByCode("JJN7");
                    childRisk32.setQuestion(question96);
                    new_responses.add(childRisk32);
                    ResponseEntity childRisk33 = new ResponseEntity((r.getCell(398).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(398).getNumericCellValue(), (r.getCell(399) == null) ? null : r.getCell(399).getStringCellValue());

                    QuestionEntity question97 = questionsSessionBean.retrieveQuestionByCode("6.8");
                    childRisk33.setQuestion(question97);
                    new_responses.add(childRisk33);
                    ResponseEntity childRisk34 = new ResponseEntity((r.getCell(404).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(404).getNumericCellValue(), (r.getCell(405) == null) ? null : r.getCell(405).getStringCellValue());

                    QuestionEntity question98 = questionsSessionBean.retrieveQuestionByCode("6.9");
                    childRisk34.setQuestion(question98);
                    new_responses.add(childRisk34);
                    ResponseEntity childRisk35 = new ResponseEntity((r.getCell(402).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(402).getNumericCellValue(), (r.getCell(403) == null) ? null : r.getCell(403).getStringCellValue());

                    QuestionEntity question99 = questionsSessionBean.retrieveQuestionByCode("6.10");
                    childRisk35.setQuestion(question99);
                    new_responses.add(childRisk35);
                    ResponseEntity childRisk36 = new ResponseEntity((r.getCell(914).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(914).getNumericCellValue(), (r.getCell(915) == null) ? null : r.getCell(915).getStringCellValue());

                    QuestionEntity question100 = questionsSessionBean.retrieveQuestionByCode("ORB1");
                    childRisk36.setQuestion(question100);
                    new_responses.add(childRisk36);
                    ResponseEntity childRisk37 = new ResponseEntity((r.getCell(916).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(916).getNumericCellValue(), (r.getCell(917) == null) ? null : r.getCell(917).getStringCellValue());

                    QuestionEntity question101 = questionsSessionBean.retrieveQuestionByCode("ORB2");
                    childRisk37.setQuestion(question101);
                    new_responses.add(childRisk37);
                    ResponseEntity childRisk38 = new ResponseEntity((r.getCell(918).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(918).getNumericCellValue(), (r.getCell(919) == null) ? null : r.getCell(919).getStringCellValue());

                    QuestionEntity question102 = questionsSessionBean.retrieveQuestionByCode("ORB3");
                    childRisk38.setQuestion(question102);
                    new_responses.add(childRisk38);
                    ResponseEntity childRisk39 = new ResponseEntity((r.getCell(920).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(920).getNumericCellValue(), (r.getCell(921) == null) ? null : r.getCell(921).getStringCellValue());

                    QuestionEntity question103 = questionsSessionBean.retrieveQuestionByCode("ORB4");
                    childRisk39.setQuestion(question103);
                    new_responses.add(childRisk39);

                    //Strengths
                    ResponseEntity strength = new ResponseEntity((r.getCell(700).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(700).getNumericCellValue(), (r.getCell(701) == null) ? null : r.getCell(701).getStringCellValue());
                    QuestionEntity question104 = questionsSessionBean.retrieveQuestionByCode("7.1");
                    strength.setQuestion(question104);
                    new_responses.add(strength);
                    ResponseEntity strength1 = new ResponseEntity((r.getCell(702).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(702).getNumericCellValue(), (r.getCell(703) == null) ? null : r.getCell(703).getStringCellValue());
                    QuestionEntity question105 = questionsSessionBean.retrieveQuestionByCode("7.2");
                    strength1.setQuestion(question105);
                    new_responses.add(strength1);
                    ResponseEntity strength2 = new ResponseEntity((r.getCell(704).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(704).getNumericCellValue(), (r.getCell(705) == null) ? null : r.getCell(705).getStringCellValue());
                    QuestionEntity question106 = questionsSessionBean.retrieveQuestionByCode("7.3");
                    strength2.setQuestion(question106);
                    new_responses.add(strength2);
                    ResponseEntity strength3 = new ResponseEntity((r.getCell(706).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(706).getNumericCellValue(), (r.getCell(707) == null) ? null : r.getCell(707).getStringCellValue());
                    QuestionEntity question107 = questionsSessionBean.retrieveQuestionByCode("7.4");
                    strength3.setQuestion(question107);
                    new_responses.add(strength3);
                    ResponseEntity strength4 = new ResponseEntity((r.getCell(708).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(708).getNumericCellValue(), (r.getCell(709) == null) ? null : r.getCell(709).getStringCellValue());
                    QuestionEntity question108 = questionsSessionBean.retrieveQuestionByCode("7.5");
                    strength4.setQuestion(question108);
                    new_responses.add(strength4);
                    ResponseEntity strength5 = new ResponseEntity((r.getCell(710).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(710).getNumericCellValue(), (r.getCell(711) == null) ? null : r.getCell(711).getStringCellValue());
                    QuestionEntity question109 = questionsSessionBean.retrieveQuestionByCode("7.6");
                    strength5.setQuestion(question109);
                    new_responses.add(strength5);
                    ResponseEntity strength6 = new ResponseEntity((r.getCell(712).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(712).getNumericCellValue(), (r.getCell(713) == null) ? null : r.getCell(713).getStringCellValue());
                    QuestionEntity question110 = questionsSessionBean.retrieveQuestionByCode("7.7");
                    strength6.setQuestion(question110);
                    new_responses.add(strength6);
                    ResponseEntity strength7 = new ResponseEntity((r.getCell(714).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(714).getNumericCellValue(), (r.getCell(715) == null) ? null : r.getCell(715).getStringCellValue());

                    QuestionEntity question111 = questionsSessionBean.retrieveQuestionByCode("7.8");
                    strength7.setQuestion(question111);
                    new_responses.add(strength7);
                    ResponseEntity strength8 = new ResponseEntity((r.getCell(716).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(716).getNumericCellValue(), (r.getCell(717) == null) ? null : r.getCell(717).getStringCellValue());

                    QuestionEntity question112 = questionsSessionBean.retrieveQuestionByCode("7.9");
                    strength8.setQuestion(question112);
                    new_responses.add(strength8);
                    ResponseEntity strength9 = new ResponseEntity((r.getCell(718).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(718).getNumericCellValue(), (r.getCell(719) == null) ? null : r.getCell(719).getStringCellValue());
                    QuestionEntity question113 = questionsSessionBean.retrieveQuestionByCode("7.10");
                    strength9.setQuestion(question113);
                    new_responses.add(strength9);
                    ResponseEntity strength10 = new ResponseEntity((r.getCell(720).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(720).getNumericCellValue(), (r.getCell(721) == null) ? null : r.getCell(721).getStringCellValue());

                    QuestionEntity question114 = questionsSessionBean.retrieveQuestionByCode("7.11");
                    strength10.setQuestion(question114);
                    new_responses.add(strength10);
                    ResponseEntity strength11 = new ResponseEntity((r.getCell(722).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(722).getNumericCellValue(), (r.getCell(723) == null) ? null : r.getCell(723).getStringCellValue());
                    QuestionEntity question115 = questionsSessionBean.retrieveQuestionByCode("7.12");
                    strength11.setQuestion(question115);
                    new_responses.add(strength11);
                    ResponseEntity strength12 = new ResponseEntity((r.getCell(724).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(724).getNumericCellValue(), (r.getCell(725) == null) ? null : r.getCell(725).getStringCellValue());
                    QuestionEntity question116 = questionsSessionBean.retrieveQuestionByCode("7.13");
                    strength12.setQuestion(question116);
                    new_responses.add(strength12);
                    ResponseEntity strength13 = new ResponseEntity((r.getCell(726).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(726).getNumericCellValue(), (r.getCell(727) == null) ? null : r.getCell(727).getStringCellValue());
                    QuestionEntity question117 = questionsSessionBean.retrieveQuestionByCode("7.14");
                    strength13.setQuestion(question117);
                    new_responses.add(strength13);
                    ResponseEntity strength14 = new ResponseEntity((r.getCell(728).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(728).getNumericCellValue(), (r.getCell(729) == null) ? null : r.getCell(729).getStringCellValue());
                    QuestionEntity question118 = questionsSessionBean.retrieveQuestionByCode("7.15");
                    strength14.setQuestion(question118);
                    new_responses.add(strength14);

                    //Transition to adulthood
                    ResponseEntity adulthood = new ResponseEntity((r.getCell(772).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(772).getNumericCellValue(), (r.getCell(773) == null) ? null : r.getCell(773).getStringCellValue());
                    QuestionEntity question119 = questionsSessionBean.retrieveQuestionByCode("9.1");
                    adulthood.setQuestion(question119);
                    new_responses.add(adulthood);
                    ResponseEntity adulthood1 = new ResponseEntity((r.getCell(774).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(774).getNumericCellValue(), (r.getCell(775) == null) ? null : r.getCell(775).getStringCellValue());
                    QuestionEntity question120 = questionsSessionBean.retrieveQuestionByCode("9.2");
                    adulthood1.setQuestion(question120);
                    new_responses.add(adulthood1);
                    ResponseEntity adulthood2 = new ResponseEntity((r.getCell(776).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(776).getNumericCellValue(), (r.getCell(777) == null) ? null : r.getCell(777).getStringCellValue());
                    QuestionEntity question121 = questionsSessionBean.retrieveQuestionByCode("9.3");
                    adulthood2.setQuestion(question121);
                    new_responses.add(adulthood2);
                    ResponseEntity adulthood3 = new ResponseEntity((r.getCell(778).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(778).getNumericCellValue(), (r.getCell(779) == null) ? null : r.getCell(779).getStringCellValue());
                    QuestionEntity question122 = questionsSessionBean.retrieveQuestionByCode("9.4");
                    adulthood3.setQuestion(question122);
                    new_responses.add(adulthood3);
                    ResponseEntity adulthood4 = new ResponseEntity((r.getCell(780).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(780).getNumericCellValue(), (r.getCell(781) == null) ? null : r.getCell(781).getStringCellValue());
                    QuestionEntity question123 = questionsSessionBean.retrieveQuestionByCode("9.5");
                    adulthood4.setQuestion(question123);
                    new_responses.add(adulthood4);
                    ResponseEntity adulthood5 = new ResponseEntity((r.getCell(782).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(782).getNumericCellValue(), (r.getCell(783) == null) ? null : r.getCell(783).getStringCellValue());
                    QuestionEntity question124 = questionsSessionBean.retrieveQuestionByCode("9.6");
                    adulthood5.setQuestion(question124);
                    new_responses.add(adulthood5);
                    ResponseEntity adulthood6 = new ResponseEntity((r.getCell(784).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(784).getNumericCellValue(), (r.getCell(785) == null) ? null : r.getCell(785).getStringCellValue());
                    QuestionEntity question125 = questionsSessionBean.retrieveQuestionByCode("9.7");
                    adulthood6.setQuestion(question125);
                    new_responses.add(adulthood6);
                    ResponseEntity adulthood7 = new ResponseEntity((r.getCell(924).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(924).getNumericCellValue(), (r.getCell(925) == null) ? null : r.getCell(925).getStringCellValue());
                    QuestionEntity question126 = questionsSessionBean.retrieveQuestionByCode("CN1");
                    adulthood7.setQuestion(question126);
                    new_responses.add(adulthood7);
                    ResponseEntity adulthood8 = new ResponseEntity((r.getCell(926).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(926).getNumericCellValue(), (r.getCell(927) == null) ? null : r.getCell(927).getStringCellValue());
                    QuestionEntity question127 = questionsSessionBean.retrieveQuestionByCode("CN2");
                    adulthood8.setQuestion(question127);
                    new_responses.add(adulthood8);
                    ResponseEntity adulthood9 = new ResponseEntity((r.getCell(928).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(928).getNumericCellValue(), (r.getCell(929) == null) ? null : r.getCell(929).getStringCellValue());
                    QuestionEntity question128 = questionsSessionBean.retrieveQuestionByCode("CN3");
                    adulthood9.setQuestion(question128);
                    new_responses.add(adulthood9);
                    ResponseEntity adulthood10 = new ResponseEntity((r.getCell(930).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(930).getNumericCellValue(), (r.getCell(931) == null) ? null : r.getCell(931).getStringCellValue());
                    QuestionEntity question129 = questionsSessionBean.retrieveQuestionByCode("CN4");
                    adulthood10.setQuestion(question129);
                    new_responses.add(adulthood10);
                    ResponseEntity adulthood11 = new ResponseEntity((r.getCell(932).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(932).getNumericCellValue(), (r.getCell(933) == null) ? null : r.getCell(933).getStringCellValue());
                    QuestionEntity question130 = questionsSessionBean.retrieveQuestionByCode("CN5");
                    adulthood11.setQuestion(question130);
                    new_responses.add(adulthood11);
                    //Residential Care
                    ResponseEntity residentialCare = new ResponseEntity((r.getCell(788).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(788).getNumericCellValue(), (r.getCell(789) == null) ? null : r.getCell(789).getStringCellValue());
                    QuestionEntity question131 = questionsSessionBean.retrieveQuestionByCode("10.1");
                    residentialCare.setQuestion(question131);
                    new_responses.add(residentialCare);
                    ResponseEntity residentialCare1 = new ResponseEntity((r.getCell(790).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(790).getNumericCellValue(), (r.getCell(791) == null) ? null : r.getCell(791).getStringCellValue());
                    QuestionEntity question132 = questionsSessionBean.retrieveQuestionByCode("10.2");
                    residentialCare1.setQuestion(question132);
                    new_responses.add(residentialCare1);
                    ResponseEntity residentialCare2 = new ResponseEntity((r.getCell(792).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(792).getNumericCellValue(), (r.getCell(793) == null) ? null : r.getCell(793).getStringCellValue());
                    QuestionEntity question133 = questionsSessionBean.retrieveQuestionByCode("10.3");
                    residentialCare2.setQuestion(question133);
                    new_responses.add(residentialCare2);
                    ResponseEntity residentialCare3 = new ResponseEntity((r.getCell(794).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(794).getNumericCellValue(), (r.getCell(795) == null) ? null : r.getCell(795).getStringCellValue());
                    QuestionEntity question134 = questionsSessionBean.retrieveQuestionByCode("10.4");
                    residentialCare3.setQuestion(question134);
                    new_responses.add(residentialCare3);
                    ResponseEntity residentialCare4 = new ResponseEntity((r.getCell(796).getCellType() != CellType.NUMERIC) ? -1 : (int) r.getCell(796).getNumericCellValue(), (r.getCell(797) == null) ? null : r.getCell(797).getStringCellValue());
                    QuestionEntity question135 = questionsSessionBean.retrieveQuestionByCode("10.5");
                    residentialCare4.setQuestion(question135);
                    new_responses.add(residentialCare4);

                    // TO ADD REMOVAL FROM PERSISTANCE CONTEXT FOR OLD RESPONSES
                    assessment.getResponse().clear();

                    // check if all valid or not
                    for (ResponseEntity response : new_responses) {
                        QuestionEntity questionnn = response.getQuestion();
                        if (questionnn instanceof MainQuestionEntity) {
                            MainQuestionEntity mainQ = (MainQuestionEntity) questionnn;
                            if (!mainQ.getAgeGroup().getDomain().isModule()) {
                                int lower_bound;
                                int higher_bound;
                                if (mainQ.getAgeGroup().getAgeRange().contains("+")) {
                                    String ageGap = mainQ.getAgeGroup().getAgeRange().substring(0, mainQ.getAgeGroup().getAgeRange().length() - 1);
                                    lower_bound = Integer.parseInt(ageGap);
                                    higher_bound = 20;
                                } else {
                                    String[] ageGap = mainQ.getAgeGroup().getAgeRange().split("-");
                                    lower_bound = Integer.parseInt(ageGap[0]);
                                    higher_bound = Integer.parseInt(ageGap[1]);
                                }
                                ClientEntity client = assessment.getClient();
//                                int age = client.getAge();
                                LocalDate assessmentDate = assessment.getRawAssessmentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                LocalDate clientDob = client.getRawDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                int age = Period.between(clientDob, assessmentDate).getYears();
                                if (age >= lower_bound && age <= higher_bound) {

                                    if (response.getResponseValue() == -1) {
                                        to_persist = false;
                                        upload_details += "Missing/Incorrect assessment response in sheet 3, row " + (i + 1) + "\n";
                                        System.out.println("\tMissing/Incorrect assessment response in sheet 3, row " + (i + 1));
                                    } else {
                                        em.persist(response);
                                        assessment.getResponse().add(response);
//                                        System.out.println("Pairing assessment " + assessment.getAssessmentUniqueId() + " with response ");
                                    }
                                }
                            } else {
                                if (response.getResponseValue() != -1) {
                                    em.persist(response);
                                    assessment.getResponse().add(response);
//                                    System.out.println("Pairing assessment " + assessment.getAssessmentUniqueId() + " with response ");
                                }
                            }

                        } else if (questionnn instanceof SubQuestionEntity) { // this means this is a subquestion
                            SubQuestionEntity sQuestion = (SubQuestionEntity) questionnn;
                            List<MainQuestionEntity> mainQs = sQuestion.getSubmodule().getQues();
                            for (MainQuestionEntity mainQ : mainQs) {
                                String qCode = mainQ.getQuestionCode();
                                ResponseEntity response_check = assessmentSessionBean.getResponseFromAssessmentByCode(assessmentId, qCode);
//                                if (response_check != null) {
//                                    System.out.println("Response Value: " + response_check.getResponseValue() + " AssessmentID + qcode + subqcode " + assessmentId + " " + qCode + " " + questionnn.getQuestionCode());
//                                } else {
//                                    System.out.println("null response");
//                                }
                                
                                if (response_check != null && (response_check.getResponseValue() == 1 | response_check.getResponseValue() == 2 | response_check.getResponseValue() == 3)) {
                                    // this means that this field is neccersary and check if its there or not
//                                    System.out.println(qCode + " passes the check");
                                    if (response.getResponseValue() == -1) {
                                        to_persist = false;
                                        upload_details += "Missing/Incorrect assessment response in sheet 3, row " + (i + 1) + "\n";
                                        System.out.println("\tMissing/Incorrect assessment response in sheet 3, row " + (i + 1));
                                    } else {
                                        em.persist(response);
                                        assessment.getResponse().add(response);
                                        // System.out.println("Pairing assessment " + assessment.getAssessmentUniqueId() + " with response ");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } catch (QuestionNotFoundException | AssessmentNotFoundException ex) {
                    System.out.println("WTF ***************************************************************************");
                }
                assessments.add(assessment);
            }

            //Iterate through caretaker assessments to get caretaker's results, caretaker and assessments must exist first
            System.out.println("** Processing Sheet 4 **");
            XSSFSheet caretakerAssessmentSheet = rawWorkBook.getSheetAt(3);

            for (int i = 1; i < caretakerAssessmentSheet.getLastRowNum() + 1; i++) {
                boolean new_entity = false;
                System.out.println("Processing Sheet 4 Row " + (i + 1));

                Row r = caretakerAssessmentSheet.getRow(i);

                Long clientId = (long) (r.getCell(0).getNumericCellValue());
                String caretakerIdNumber = (r.getCell(3).getStringCellValue() == null) ? null : r.getCell(3).getStringCellValue();
                Long assessmentId = (long) (r.getCell(2).getNumericCellValue());

                // check for missing values
                if (caretakerIdNumber == null) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 4, row " + (i + 1) + ", column 4\n";
                    System.out.println("\tMissing entry in sheet 4, row " + (i + 1) + ", column 4");
                    continue;
                } else if (clientId == 0l) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 4, row " + (i + 1) + ", column 1\n";
                    System.out.println("\tMissing entry in sheet 4, row " + (i + 1) + ", column 1");
                    continue;
                } else if (assessmentId == 0l) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 4, row " + (i + 1) + ", column 2\n";
                    System.out.println("\tMissing entry in sheet 4, row " + (i + 1) + ", column 2");
                    continue;
                }

                ClientEntity caretakerClient = null;
                List<CaretakerEntity> clientCaretakers = new ArrayList<>();
                CaretakerAssessmentEntity caretakerAssessment = null;
                try {
                    caretakerClient = clientSessionBean.retrieveClientByUniqueId(clientId);
                    clientCaretakers = caretakerClient.getCaretakers();
                    System.out.println("\tClient ID " + clientId + " exists in DB");
                } catch (ClientNotFoundException ex) {
                    // iterate through previous list to find
                    boolean found = false;
                    for (ClientEntity c : clients) {
                        if (Objects.equals(clientId, c.getClientUniqueId())) {
                            caretakerClient = c;
                            clientCaretakers = caretakerClient.getCaretakers();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        to_persist = false;
                        upload_details += "Invalid entry in sheet 4, row " + (i + 1) + ", column 1\n";
                        System.out.println("\tInvalid entry in sheet 4, row " + (i + 1) + ", column 1");
                        continue;
                    } else {
                        System.out.println("\tClient ID " + clientId + " exists in previous sheet");
                    }
                }
                boolean proceed = true;
                boolean caretaker_match = false;
                for (CaretakerEntity ct : clientCaretakers) {
                    // look for caretaker under client
                    if (caretakerIdNumber.equals(ct.getIdNumber())) {
                        caretaker_match = true;
                        boolean caretakerAssessment_exists = false;
                        for (CaretakerAssessmentEntity cta : ct.getCaretakerAssessments()) {
                            if (Objects.equals(cta.getAssessment().getAssessmentUniqueId(), assessmentId)) { // check the owning assessment for match
                                // caretakerAssessment already exists and we will be updating
                                caretakerAssessment_exists = true;
                                caretakerAssessment = cta;
                                break;
                            }

                        }
                        if (!caretakerAssessment_exists) { // there is no caretaker assessment whoms owning assessment matches the provided assessment Id
                            boolean assessmentExists = false;
                            for (AssessmentEntity ass : caretakerClient.getAssessment()) { // will definately be initialized
                                if (Objects.equals(ass.getAssessmentUniqueId(), assessmentId)) {
                                    // create new caretakeAssessment
                                    caretakerAssessment = new CaretakerAssessmentEntity();
                                    caretakerAssessment.setAssessment(ass);
                                    ass.addCaretakerAssessment(caretakerAssessment);
                                    caretakerAssessment.setCaretaker(ct);
                                    ct.addCaretakerAssessment(caretakerAssessment);
                                    new_entity = true;
                                    assessmentExists = true;
                                    break;
                                }
                            }
                            if (!assessmentExists) { // the assessment does not match under the client's assessments
                                to_persist = false;
                                upload_details += "Invalid entry in sheet 4, row " + (i + 1) + ", column 3\n";
                                System.out.println("\tInvalid entry in sheet 4, row " + (i + 1) + ", column 3");

                                proceed = false;
                            }
                        }
                    }
                }
                if (!caretaker_match) { // the caretaker does not match with the client
                    to_persist = false;
                    upload_details += "Invalid entry in sheet 4, row " + (i + 1) + ", column 4\n";
                    System.out.println("\tInvalid entry in sheet 4, row " + (i + 1) + ", column 4");
                    proceed = false;
                    continue;
                }
                if (!proceed) {
                    continue;
                }

                String typeString = (r.getCell(4) == null) ? null : r.getCell(4).getStringCellValue();
                CaretakerTypeEnum caretakerType;
                if (typeString == null || typeString.length() == 0) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 4, row " + (i + 1) + ", column 5\n";
                    System.out.println("\tMissing entry in sheet 4, row " + (i + 1) + ", column 5");
                    continue;
                } else if (typeString.charAt(0) == 'C') {
                    caretakerType = CaretakerTypeEnum.CURRENT_CAREGIVER;
                } else {
                    caretakerType = CaretakerTypeEnum.LONG_TERM_IDENTIFIED_CAREGIVER;
                }
                caretakerAssessment.setCaretakerType(caretakerType);

                String needsString = (r.getCell(7) == null) ? null : r.getCell(7).getStringCellValue();
                //  System.out.println("*****************" + needsString);
                CaretakerAlgorithmEnum levelOfNeeds;
                if (needsString == null || needsString.length() == 0) {
                    to_persist = false;
                    upload_details += "Missing entry in sheet 4, row " + (i + 1) + ", column 8\n";
                    System.out.println("\tMissing entry in sheet 4, row " + (i + 1) + ", column 8");
                    continue;
                } else if (needsString.charAt(0) == 'H' && needsString.length() > 10) {
                    levelOfNeeds = CaretakerAlgorithmEnum.HIGH_NEEDS_WITH_RED_FLAGS;
                } else if (needsString.charAt(0) == 'H') {
                    levelOfNeeds = CaretakerAlgorithmEnum.HIGH_NEEDS;
                } else if (needsString.charAt(0) == 'L' && needsString.length() > 9) {
                    levelOfNeeds = CaretakerAlgorithmEnum.LOW_NEEDS_WITH_RED_FLAGS;
                } else {
                    levelOfNeeds = CaretakerAlgorithmEnum.LOW_NEEDS;
                }

                caretakerAssessment.setLevelOfNeeds(levelOfNeeds);
                if (new_entity) {
                    em.persist(caretakerAssessment);
                }
                try { // assume all responses are required

                    List<ResponseEntity> new_responses = new ArrayList<>();

                    //Caregiver Domain
                    ResponseEntity supervisionDiscipline = new ResponseEntity((int) r.getCell(31).getNumericCellValue(), (r.getCell(32) == null) ? null : r.getCell(32).getStringCellValue());
                    QuestionEntity question136 = questionsSessionBean.retrieveQuestionByCode("8A.1");
                    supervisionDiscipline.setQuestion(question136);
                    new_responses.add(supervisionDiscipline);
                    ResponseEntity involvement = new ResponseEntity((int) r.getCell(39).getNumericCellValue(), (r.getCell(40) == null) ? null : r.getCell(40).getStringCellValue());
                    QuestionEntity question137 = questionsSessionBean.retrieveQuestionByCode("8A.2");
                    involvement.setQuestion(question137);
                    new_responses.add(involvement);
                    ResponseEntity empathy = new ResponseEntity((int) r.getCell(35).getNumericCellValue(), (r.getCell(36) == null) ? null : r.getCell(36).getStringCellValue());
                    QuestionEntity question138 = questionsSessionBean.retrieveQuestionByCode("8A.3");
                    empathy.setQuestion(question138);
                    new_responses.add(empathy);
                    ResponseEntity knowledge = new ResponseEntity((int) r.getCell(37).getNumericCellValue(), (r.getCell(38) == null) ? null : r.getCell(38).getStringCellValue());
                    QuestionEntity question139 = questionsSessionBean.retrieveQuestionByCode("8A.4");
                    knowledge.setQuestion(question139);
                    new_responses.add(knowledge);
                    ResponseEntity organisation = new ResponseEntity((int) r.getCell(33).getNumericCellValue(), (r.getCell(34) == null) ? null : r.getCell(34).getStringCellValue());
                    QuestionEntity question140 = questionsSessionBean.retrieveQuestionByCode("8A.5");
                    organisation.setQuestion(question140);
                    new_responses.add(organisation);
                    ResponseEntity intervention = new ResponseEntity((int) r.getCell(43).getNumericCellValue(), (r.getCell(44) == null) ? null : r.getCell(44).getStringCellValue());
                    QuestionEntity question141 = questionsSessionBean.retrieveQuestionByCode("8A.6");
                    intervention.setQuestion(question141);
                    new_responses.add(intervention);
                    ResponseEntity stress = new ResponseEntity((int) r.getCell(41).getNumericCellValue(), (r.getCell(42) == null) ? null : r.getCell(42).getStringCellValue());
                    QuestionEntity question142 = questionsSessionBean.retrieveQuestionByCode("8A.7");
                    stress.setQuestion(question142);
                    new_responses.add(stress);
                    ResponseEntity socialResources = new ResponseEntity((int) r.getCell(9).getNumericCellValue(), (r.getCell(10) == null) ? null : r.getCell(10).getStringCellValue());
                    QuestionEntity question143 = questionsSessionBean.retrieveQuestionByCode("8B.1");
                    socialResources.setQuestion(question143);
                    new_responses.add(socialResources);
                    ResponseEntity housing = new ResponseEntity((int) r.getCell(13).getNumericCellValue(), (r.getCell(14) == null) ? null : r.getCell(14).getStringCellValue());
                    QuestionEntity question144 = questionsSessionBean.retrieveQuestionByCode("8B.2");
                    housing.setQuestion(question144);
                    new_responses.add(housing);
                    ResponseEntity employment = new ResponseEntity((int) r.getCell(11).getNumericCellValue(), (r.getCell(12) == null) ? null : r.getCell(12).getStringCellValue());
                    QuestionEntity question145 = questionsSessionBean.retrieveQuestionByCode("8B.3");
                    employment.setQuestion(question145);
                    new_responses.add(employment);
                    ResponseEntity financial = new ResponseEntity((int) r.getCell(15).getNumericCellValue(), (r.getCell(16) == null) ? null : r.getCell(16).getStringCellValue());
                    QuestionEntity question146 = questionsSessionBean.retrieveQuestionByCode("8B.4");
                    financial.setQuestion(question146);
                    new_responses.add(financial);
                    ResponseEntity mentalHealth = new ResponseEntity((int) r.getCell(18).getNumericCellValue(), (r.getCell(19) == null) ? null : r.getCell(19).getStringCellValue());
                    QuestionEntity question147 = questionsSessionBean.retrieveQuestionByCode("8C.1");
                    mentalHealth.setQuestion(question147);
                    new_responses.add(mentalHealth);
                    ResponseEntity physicalHealth = new ResponseEntity((int) r.getCell(28).getNumericCellValue(), (r.getCell(29) == null) ? null : r.getCell(29).getStringCellValue());
                    QuestionEntity question148 = questionsSessionBean.retrieveQuestionByCode("8C.2");
                    physicalHealth.setQuestion(question148);
                    new_responses.add(physicalHealth);
                    ResponseEntity substance = new ResponseEntity((int) r.getCell(24).getNumericCellValue(), (r.getCell(25) == null) ? null : r.getCell(25).getStringCellValue());
                    QuestionEntity question149 = questionsSessionBean.retrieveQuestionByCode("8C.3");
                    substance.setQuestion(question149);
                    new_responses.add(substance);
                    ResponseEntity disability = new ResponseEntity((int) r.getCell(20).getNumericCellValue(), (r.getCell(21) == null) ? null : r.getCell(21).getStringCellValue());
                    QuestionEntity question150 = questionsSessionBean.retrieveQuestionByCode("8C.4");
                    disability.setQuestion(question150);
                    new_responses.add(disability);
                    ResponseEntity legal = new ResponseEntity((int) r.getCell(22).getNumericCellValue(), (r.getCell(23) == null) ? null : r.getCell(23).getStringCellValue());
                    QuestionEntity question151 = questionsSessionBean.retrieveQuestionByCode("8C.5");
                    legal.setQuestion(question151);
                    new_responses.add(legal);
                    ResponseEntity safety = new ResponseEntity((int) r.getCell(26).getNumericCellValue(), (r.getCell(27) == null) ? null : r.getCell(27).getStringCellValue());
                    QuestionEntity question152 = questionsSessionBean.retrieveQuestionByCode("8C.6");
                    safety.setQuestion(question152);
                    new_responses.add(safety);

                    // TO ADD REMOVAL FROM PERSISTANCE CONTEXT FOR OLD RESPONSES
                    caretakerAssessment.getCaretakerResponses().clear();
                    for (ResponseEntity caregiverResponse : new_responses) {
                        if (caregiverResponse.getResponseValue() == -1) {
                            to_persist = false;
                            upload_details += "Missing/Incorrect assessment response in sheet 4, row " + i + 1 + "\n";
                            System.out.println("\tMissing/Incorrect assessment response in sheet 4, row " + i + 1);
                        } else {
                            em.persist(caregiverResponse);
                            caretakerAssessment.getCaretakerResponses().add(caregiverResponse);
                        }
                    }
                } catch (QuestionNotFoundException e) {
                    System.out.println(e.getMessage());
                }
                caretakerAssessments.add(caretakerAssessment);
            }

        } catch (IOException | ConstraintViolationException e) {
            throw new DataUploadException(e.getMessage());
        }

        // build persist/update logic
        if (to_persist) {
            // deprecated logic: persist in this order: client, caretaker, assessment, caretakerAssessment (caretaker assessment will be inherently persisted)

            upload_details = "Added/Updated " + clients.size() + " client(s)\nAdded/Updated " + caretakers.size() + " caretaker(s)\nAdded/Updated " + assessments.size() + " assessment(s)\nAdded/Updated " + caretakerAssessments.size() + " caretaker assessment(s)";
            String access_link = "http://localhost:8080/cans-backend-rws/CANS/" + fileName;
            UploadEntity upload = new UploadEntity(new Date(), upload_details, access_link, true);
            admin.getDoc().add(upload);
            upload.setAdmin(admin);
            em.persist(upload);
            em.flush();

            return upload;
        } else {
            // UploadEntity upload = this.renderUnsuccesfulUpload(upload_details, fileName, adminUserId);
            eJBContext.setRollbackOnly();
            throw new DataUploadException(upload_details);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public UploadEntity renderUnsuccesfulUpload(String upload_details, String fileName, long adminUserId) throws UserNotFoundException {
        try {
            String access_link = "http://localhost:8080/cans-backend-rws/CANS/" + fileName;
            UploadEntity upload = new UploadEntity(new Date(), upload_details, access_link, false);
            AdminUserEntity admin = adminUserSessionBean.retrieveUserById(adminUserId);
            admin.getDoc().add(upload);
            upload.setAdmin(admin);
            em.persist(upload);
            em.flush();
            System.out.println("persisted the failed one <-------");
            return upload;
        } catch (UserNotFoundException ex) {
            System.out.println("GGWP JUN HAO PLS CHECK DATA INIT");
            throw new UserNotFoundException();
        }
    }
}

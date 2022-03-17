/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CaretakerAssessmentEntity;
import entity.CaretakerEntity;
import entity.ClientEntity;
import entity.ResponseEntity;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.DatatypeConverter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.enumeration.AgencyTypeEnum;
import util.enumeration.CaretakerAlgorithmEnum;
import util.exception.AssessmentNotFoundException;
import util.exception.CaretakerAssessmentNotFoundException;
import util.exception.ExportDataException;

/**
 *
 * @author Ziyue
 */
@Stateless
public class ExportSessionBean implements ExportSessionBeanLocal {

    @EJB(name = "CaretakerAssessmentSessionBeanLocal")
    private CaretakerAssessmentSessionBeanLocal caretakerAssessmentSessionBeanLocal;

    @EJB(name = "AssessmentSessionBeanLocal")
    private AssessmentSessionBeanLocal assessmentSessionBeanLocal;

    @EJB(name = "ClientSessionBeanLocal")
    private ClientSessionBeanLocal clientSessionBeanLocal;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    @Override
    public String exportCleanedData(String startDate, String endDate) throws ExportDataException {
        try {
            XSSFWorkbook cleanWorkbook = new XSSFWorkbook();
            XSSFSheet cleanSheet = cleanWorkbook.createSheet("Cleaned Sheet");

            Date start = DatatypeConverter.parseDateTime(startDate).getTime();
            Date end = DatatypeConverter.parseDateTime(endDate).getTime();

            //generate headers
            String[] headers
                    = {"S/N", "Client_ID", "Gender", "Admission_Type", "Placement_Type", "Age" //0-5
                        ,
                         "Age Group(1=0-2, 2=3-6, 3=7-20)", "Calculated LOC", "Actual LOC", "Current Assessor", "Assessor's Organisation" //6-10
                        ,
                         "Assessor Agency Type", "Assessment Status", "Assessment Reason", "Assessment_Date", "Approved Date" //11-15..
                        //child development domain
                        ,
                         "Proposed Caregiver LON", "Child Skills", "Play Engagement", "Communication", "Motor" //16-20
                        //note that col 17 INSERT CANS DATA HERE => is not included, so all columns after that drops by 1 index
                        ,
                         "Problem Solving", "Personal-Social", "Social Emotional", "Caregiver/Child Interaction", "Access To Play" //21-25
                        ,
                         "Quality of Play", "Exposure To Screen Time", "Pre‐school/Childcare", "Suitability of pre-school/childcare", "Child's Functioning in PreSchool/Childcare" //26-30
                        //life functioning domain
                        ,
                         "Preschool/ Childcare Attendance", "Relationship with Family Members", "Relationship Permanenece", "Living Situation", "Attachment" //31-35
                        ,
                         "Developmental", "Cognitive", "Communication", "Social-Emotional Development", "Daily Functioning Skills" //36 - 40
                        //physical/medical domain
                        ,
                         "Social Relationships", "Leisure/Play Activities", "Sexual Development", "Independent Living", "Physical/Medical" //41 - 45
                        ,
                         "Oral Health", "Sleep", "Pre-disposing Risk Factors", "Birth Weight", "Antenatal Care" //46 - 50
                        ,
                         "Labour/Delivery", "Substance Exposure", "Parent/Sibling Cognitive/Physical Capacity", "Immunisation", "Eating Routines/Patterns" //51 - 55
                        //school domain
                        ,
                         "Problems in Eating", "Elimination: Problems in passing motion", "Failure to Thrive", "School Behaviour", "School Performance" // 56 - 60
                        //Behavioural and emotional needs domain
                        ,
                         "School Attendance", "Reading & Writing Abilities", "Anxiety", "Adjustment to Trauma", "Frustration Tolerance / Tantrums" // 61 - 65
                        ,
                         "Attention / Concentration", "Impulsivity /  Hyperactivity", "Oppositional", "Conduct", "Substance Use" // 66 - 70
                        ,
                         "Type of Substance(s)", "Frequency of Use: ", "Duration of Use: ", "Readiness To Change", "Recovery Environment" // 71 - 75
                        ,
                         "Relapse Prevention Skills", "Psychotic Symptoms", "Depression", "Eating Disturbance", "Anger Control" // 76 - 80
                        //Child Risk domain
                        ,
                         "Traumatic Events", "Neglect", "Physical Abuse", "Emotional Abuse", "Sexual Abuse" // 81 - 85
                        ,
                         "Emotional Closeness to Perpetrator (PEER/NON-PEER)", "Physical Force", "Reaction To Disclosure", "Witness to Domestic Violence", "Witness/Victim to Criminal Activity" // 86 - 90
                        ,
                         "Disruptions in caregiving/attachment losses", "Any Other Significant Trauma", "Sexually Reactive Behaviour", "Self Injury", "Suicide Risk" // 91 - 95
                        ,
                         "Self-Harm", "Runaway", "Frequency of Running: ", "Duration of Absense: ", "Consistency of Destination" // 96 - 100
                        ,
                         "Planning", "Safety of Destination", "Involvement in illegal activities", "Likelihood of return on own", "Involvement with others" // 101 - 105
                        ,
                         "Delinquent Behaviour", "State Type of Behaviour: ", "Frequency of Delinquent Behaviour ", "Planning", "Severity of Deliquent behaviour" // 106 - 110
                        ,
                         "Peer Influences", "Parent Criminal Behaviour/Influences", "Environmental Influences", "Bullying", "Victim of Bullying" // 111 - 115
                        ,
                         "Other Risk Behaviours", "Other risk-taking behaviours", "Danger to Others", "Sexual Aggression", "Sanction-Seeking Behaviour" // 116 - 120
                        //strengths
                        ,
                         "Family Relationship", "Interpersonal", "Adaptability", "Curiosity", "Confidence" // 121 - 125
                        ,
                         "Optimism", "Educational", "Talents/Interest", "Spiritual / Religious", "Prosocial Groups" // 126 - 130
                        ,
                         "Child Involvement with Care", "Community Supports", "Resilience", "Resourcefulness", "Vocational" // 131 - 135
                        //transition to adulthood
                        ,
                         "Placement/Housing", "Financial Resources", "Treatment Adherence", "Relationship with Significant Others", "Victimisation" // 136 - 140
                        ,
                         "Employment Functioning", "Needs as a Caregiver", "State Type of Caregiving Responsibility", "Social Resources", "Caregiving Stress" // 141 - 145
                        //residential care
                        ,
                         "Basic Care/Daily Living", "Safety By The Youth", "Community Outings", "Home Leave", "Caregiver Participation" // 146 - 150
                        //caregiver module
                        ,
                         "Progress Towards Goals", "Preparation For Discharge Placement", "Has_Caregiver(s)", "Caregiver_Type", "Caregiver_Algorithm" // 151 - 155
                        ,
                         "Supervision/Discipline", "Involvement in Caregiving", "Empathy for Chlid", "Knowledge", "Organisation" // 156 - 160
                        ,
                         "Intervention Adherence", "Caregiving_ Stress", "Social Resources", "Housing Stability", "Employment Functioning" // 161 - 165
                        ,
                         "Financial Resources", "Mental Health", "Physical Health", "Substance_ Use", "Intellectual/Developemental Disability" // 166 - 170
                        ,
                         "Legal", "Safety" // 171 - 173
                    };

            Row headerRow = cleanSheet.createRow(0);

            //System.out.println("Creating row: " + 0);
            int colNum = 0;

            for (String s : headers) {
                Cell newCell = headerRow.createCell(colNum++);
                newCell.setCellValue(s);
            }

            int rowIndex = 0;
            for (ClientEntity client : clientSessionBeanLocal.retrieveAllClientEntities()) {

                List<AssessmentEntity> currAssessments = client.getAssessmentsInDateRange(start, end);
                if (currAssessments.isEmpty()) {
                    continue;
                }

                for (AssessmentEntity currAssessment : currAssessments) {
//
                    rowIndex++;
                    System.out.println("Creating row: " + rowIndex);
                    Row toInsert = cleanSheet.createRow(rowIndex);
                    //S/N
                    toInsert.createCell(0).setCellValue(String.valueOf(rowIndex));

                    toInsert.createCell(1).setCellValue(String.valueOf(client.getClientId()));
                    toInsert.createCell(2).setCellValue(client.getGender());
                    toInsert.createCell(3).setCellValue(client.getAdmissionType());
                    toInsert.createCell(4).setCellValue(client.getPlacementType());
                    toInsert.createCell(5).setCellValue(String.valueOf(client.getAge()));
                    int age = client.getAge();
                    int ageGroup = 3;
                    if (age <= 2) {
                        ageGroup = 1;
                    } else if (age <= 7) {
                        ageGroup = 2;
                    }
                    toInsert.createCell(6).setCellValue(String.valueOf(ageGroup));

                    int loc = currAssessment.getLoc();
                    if (loc != -1) {
                        toInsert.createCell(7).setCellValue(String.valueOf(loc));
                        toInsert.createCell(8).setCellValue(String.valueOf(loc));
                    }

                    AssessorEntity currAssessor = client.getAssessor();
                    toInsert.createCell(9).setCellValue(currAssessor.getName());
                    toInsert.createCell(10).setCellValue(currAssessor.getOrganisation().getName());

                    String agencyType = "";
                    for (AgencyTypeEnum type : currAssessor.getOrganisation().getOrganisationTypes()) {
                        agencyType += type.toString() + " ";
                    }
                    toInsert.createCell(11).setCellValue(agencyType);

                    if (currAssessment.getStatus() != null) {
                        toInsert.createCell(12).setCellValue(currAssessment.getStatus().toString());
                    }
                    if (currAssessment.getReason() != null) {
                        toInsert.createCell(13).setCellValue(currAssessment.getReason().toString());
                    }
                    if (currAssessment.getAssessmentDate() != null) {
                        toInsert.createCell(14).setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(currAssessment.getRawAssessmentDate()));
                    }
                    if (currAssessment.getApprovedDate() != null) {
                        toInsert.createCell(15).setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(currAssessment.getRawApprovedDate()));
                    }

                    //populate responses
                    //note that childskills is at column 17 instead of 18 and there is no "INSERT CANS DATA HERE" column
                    Long assessmentId = currAssessment.getAssessmentId();
                    try {
                        //child skills 17 - 31
                        ResponseEntity response17 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "1.1");
                        toInsert.createCell(17).setCellValue((response17 == null) ? "" : String.valueOf(response17.getResponseValue()));
                        ResponseEntity response18 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CS1");
                        toInsert.createCell(18).setCellValue((response18 == null) ? "" : String.valueOf(response18.getResponseValue()));
                        ResponseEntity response19 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CS2");
                        toInsert.createCell(19).setCellValue((response19 == null) ? "" : String.valueOf(response19.getResponseValue()));
                        ResponseEntity response20 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CS3");
                        toInsert.createCell(20).setCellValue((response20 == null) ? "" : String.valueOf(response20.getResponseValue()));
                        ResponseEntity response21 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CS4");
                        toInsert.createCell(21).setCellValue((response21 == null) ? "" : String.valueOf(response21.getResponseValue()));
                        ResponseEntity response22 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CS5");
                        toInsert.createCell(22).setCellValue((response22 == null) ? "" : String.valueOf(response22.getResponseValue()));
                        ResponseEntity response23 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "1.2");
                        toInsert.createCell(23).setCellValue((response23 == null) ? "" : String.valueOf(response23.getResponseValue()));
                        ResponseEntity response24 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "1.3");
                        toInsert.createCell(24).setCellValue((response24 == null) ? "" : String.valueOf(response24.getResponseValue()));
                        ResponseEntity response25 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "1.4");
                        toInsert.createCell(25).setCellValue((response25 == null) ? "" : String.valueOf(response25.getResponseValue()));
                        ResponseEntity response26 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "1.5");
                        toInsert.createCell(26).setCellValue((response26 == null) ? "" : String.valueOf(response26.getResponseValue()));
                        ResponseEntity response27 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "1.6");
                        toInsert.createCell(27).setCellValue((response27 == null) ? "" : String.valueOf(response27.getResponseValue()));
                        ResponseEntity response28 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "1.7");
                        toInsert.createCell(28).setCellValue((response28 == null) ? "" : String.valueOf(response28.getResponseValue()));
                        ResponseEntity response29 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PCN1");
                        toInsert.createCell(29).setCellValue((response29 == null) ? "" : String.valueOf(response29.getResponseValue()));
                        ResponseEntity response30 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PCN2");
                        toInsert.createCell(30).setCellValue((response30 == null) ? "" : String.valueOf(response30.getResponseValue()));
                        ResponseEntity response31 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PCN3");
                        toInsert.createCell(31).setCellValue((response31 == null) ? "" : String.valueOf(response31.getResponseValue()));

                        //Life functioning 32 - 44
                        ResponseEntity response32 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.1");
                        toInsert.createCell(32).setCellValue((response32 == null) ? "" : String.valueOf(response32.getResponseValue()));
                        ResponseEntity response33 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.2");
                        toInsert.createCell(33).setCellValue((response33 == null) ? "" : String.valueOf(response33.getResponseValue()));
                        ResponseEntity response34 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.3");
                        toInsert.createCell(34).setCellValue((response34 == null) ? "" : String.valueOf(response34.getResponseValue()));
                        ResponseEntity response35 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.4");
                        toInsert.createCell(35).setCellValue((response35 == null) ? "" : String.valueOf(response35.getResponseValue()));
                        ResponseEntity response36 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.5");
                        toInsert.createCell(36).setCellValue((response36 == null) ? "" : String.valueOf(response36.getResponseValue()));
                        ResponseEntity response37 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "DN1");
                        toInsert.createCell(37).setCellValue((response37 == null) ? "" : String.valueOf(response37.getResponseValue()));
                        ResponseEntity response38 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "DN2");
                        toInsert.createCell(38).setCellValue((response38 == null) ? "" : String.valueOf(response38.getResponseValue()));
                        ResponseEntity response39 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "DN3");
                        toInsert.createCell(39).setCellValue((response39 == null) ? "" : String.valueOf(response39.getResponseValue()));
                        ResponseEntity response40 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "DN4");
                        toInsert.createCell(40).setCellValue((response40 == null) ? "" : String.valueOf(response40.getResponseValue()));
                        ResponseEntity response41 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.6");
                        toInsert.createCell(41).setCellValue((response41 == null) ? "" : String.valueOf(response41.getResponseValue()));
                        ResponseEntity response42 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.7");
                        toInsert.createCell(42).setCellValue((response42 == null) ? "" : String.valueOf(response42.getResponseValue()));
                        ResponseEntity response43 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.8");
                        toInsert.createCell(43).setCellValue((response43 == null) ? "" : String.valueOf(response43.getResponseValue()));
                        ResponseEntity response44 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "2.9");
                        toInsert.createCell(44).setCellValue((response44 == null) ? "" : String.valueOf(response44.getResponseValue()));

                        //physical health 45 - 58
                        ResponseEntity response45 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.1");
                        toInsert.createCell(45).setCellValue((response45 == null) ? "" : String.valueOf(response45.getResponseValue()));
                        ResponseEntity response46 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.2");
                        toInsert.createCell(46).setCellValue((response46 == null) ? "" : String.valueOf(response46.getResponseValue()));
                        ResponseEntity response47 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.3");
                        toInsert.createCell(47).setCellValue((response47 == null) ? "" : String.valueOf(response47.getResponseValue()));
                        ResponseEntity response48 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.4");
                        toInsert.createCell(48).setCellValue((response48 == null) ? "" : String.valueOf(response48.getResponseValue()));
                        ResponseEntity response49 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PRF1");
                        toInsert.createCell(49).setCellValue((response49 == null) ? "" : String.valueOf(response49.getResponseValue()));
                        ResponseEntity response50 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PRF2");
                        toInsert.createCell(50).setCellValue((response50 == null) ? "" : String.valueOf(response50.getResponseValue()));
                        ResponseEntity response51 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PRF3");
                        toInsert.createCell(51).setCellValue((response51 == null) ? "" : String.valueOf(response51.getResponseValue()));
                        ResponseEntity response52 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PRF4");
                        toInsert.createCell(52).setCellValue((response52 == null) ? "" : String.valueOf(response52.getResponseValue()));
                        ResponseEntity response53 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "PRF5");
                        toInsert.createCell(53).setCellValue((response53 == null) ? "" : String.valueOf(response53.getResponseValue()));
                        ResponseEntity response54 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.5");
                        toInsert.createCell(54).setCellValue((response54 == null) ? "" : String.valueOf(response54.getResponseValue()));
                        ResponseEntity response55 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.6");
                        toInsert.createCell(55).setCellValue((response55 == null) ? "" : String.valueOf(response55.getResponseValue()));
                        ResponseEntity response56 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.7");
                        toInsert.createCell(56).setCellValue((response56 == null) ? "" : String.valueOf(response56.getResponseValue()));
                        ResponseEntity response57 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.8");
                        toInsert.createCell(57).setCellValue((response57 == null) ? "" : String.valueOf(response57.getResponseValue()));
                        ResponseEntity response58 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "3.9");
                        toInsert.createCell(58).setCellValue((response58 == null) ? "" : String.valueOf(response58.getResponseValue()));

                        //school 59 - 62
                        ResponseEntity response59 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "4.1");
                        toInsert.createCell(59).setCellValue((response59 == null) ? "" : String.valueOf(response59.getResponseValue()));
                        ResponseEntity response60 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "4.2");
                        toInsert.createCell(60).setCellValue((response60 == null) ? "" : String.valueOf(response60.getResponseValue()));
                        ResponseEntity response61 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "4.3");
                        toInsert.createCell(61).setCellValue((response61 == null) ? "" : String.valueOf(response61.getResponseValue()));
                        ResponseEntity response62 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "4.4");
                        toInsert.createCell(62).setCellValue((response62 == null) ? "" : String.valueOf(response62.getResponseValue()));

                        //behavioural and emotional 63 - 80
                        ResponseEntity response63 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.1");
                        toInsert.createCell(63).setCellValue((response63 == null) ? "" : String.valueOf(response63.getResponseValue()));
                        ResponseEntity response64 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.2");
                        toInsert.createCell(64).setCellValue((response64 == null) ? "" : String.valueOf(response64.getResponseValue()));
                        ResponseEntity response65 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.3");
                        toInsert.createCell(65).setCellValue((response65 == null) ? "" : String.valueOf(response65.getResponseValue()));
                        ResponseEntity response66 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.4");
                        toInsert.createCell(66).setCellValue((response66 == null) ? "" : String.valueOf(response66.getResponseValue()));
                        ResponseEntity response67 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.5");
                        toInsert.createCell(67).setCellValue((response67 == null) ? "" : String.valueOf(response67.getResponseValue()));
                        ResponseEntity response68 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.6");
                        toInsert.createCell(68).setCellValue((response68 == null) ? "" : String.valueOf(response68.getResponseValue()));
                        ResponseEntity response69 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.7");
                        toInsert.createCell(69).setCellValue((response69 == null) ? "" : String.valueOf(response69.getResponseValue()));
                        ResponseEntity response70 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.8");
                        toInsert.createCell(70).setCellValue((response70 == null) ? "" : String.valueOf(response70.getResponseValue()));
                        ResponseEntity response71 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "SN1");
                        toInsert.createCell(71).setCellValue((response71 == null) ? "" : String.valueOf(response71.getResponseValue()));
                        ResponseEntity response72 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "SN2");
                        toInsert.createCell(72).setCellValue((response72 == null) ? "" : String.valueOf(response72.getResponseValue()));
                        ResponseEntity response73 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "SN3");
                        toInsert.createCell(73).setCellValue((response73 == null) ? "" : String.valueOf(response73.getResponseValue()));
                        ResponseEntity response74 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "SN4");
                        toInsert.createCell(74).setCellValue((response74 == null) ? "" : String.valueOf(response74.getResponseValue()));
                        ResponseEntity response75 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "SN5");
                        toInsert.createCell(75).setCellValue((response75 == null) ? "" : String.valueOf(response75.getResponseValue()));
                        ResponseEntity response76 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "SN6");
                        toInsert.createCell(76).setCellValue((response76 == null) ? "" : String.valueOf(response76.getResponseValue()));
                        ResponseEntity response77 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.9");
                        toInsert.createCell(77).setCellValue((response77 == null) ? "" : String.valueOf(response77.getResponseValue()));
                        ResponseEntity response78 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.10");
                        toInsert.createCell(78).setCellValue((response78 == null) ? "" : String.valueOf(response78.getResponseValue()));
                        ResponseEntity response79 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.11");
                        toInsert.createCell(79).setCellValue((response79 == null) ? "" : String.valueOf(response79.getResponseValue()));
                        ResponseEntity response80 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "5.12");
                        toInsert.createCell(80).setCellValue((response80 == null) ? "" : String.valueOf(response80.getResponseValue()));

                        // child risks
                        ResponseEntity response81 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.1");
                        toInsert.createCell(81).setCellValue((response81 == null) ? "" : String.valueOf(response81.getResponseValue()));
                        ResponseEntity response82 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR1");
                        toInsert.createCell(82).setCellValue((response82 == null) ? "" : String.valueOf(response82.getResponseValue()));
                        ResponseEntity response83 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR2");
                        toInsert.createCell(83).setCellValue((response83 == null) ? "" : String.valueOf(response83.getResponseValue()));
                        ResponseEntity response84 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR3");
                        toInsert.createCell(84).setCellValue((response84 == null) ? "" : String.valueOf(response84.getResponseValue()));
                        ResponseEntity response85 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR4");
                        toInsert.createCell(85).setCellValue((response85 == null) ? "" : String.valueOf(response85.getResponseValue()));
                        ResponseEntity response86 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR4a");
                        toInsert.createCell(86).setCellValue((response86 == null) ? "" : String.valueOf(response86.getResponseValue()));
                        ResponseEntity response87 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR4b");
                        toInsert.createCell(87).setCellValue((response87 == null) ? "" : String.valueOf(response87.getResponseValue()));
                        ResponseEntity response88 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR4c");
                        toInsert.createCell(88).setCellValue((response88 == null) ? "" : String.valueOf(response88.getResponseValue()));
                        ResponseEntity response89 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR5");
                        toInsert.createCell(89).setCellValue((response89 == null) ? "" : String.valueOf(response89.getResponseValue()));
                        ResponseEntity response90 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR6");
                        toInsert.createCell(90).setCellValue((response90 == null) ? "" : String.valueOf(response90.getResponseValue()));
                        ResponseEntity response91 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR7");
                        toInsert.createCell(91).setCellValue((response91 == null) ? "" : String.valueOf(response91.getResponseValue()));
                        ResponseEntity response92 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "TR8");
                        toInsert.createCell(92).setCellValue((response92 == null) ? "" : String.valueOf(response92.getResponseValue()));
                        ResponseEntity response93 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.2");
                        toInsert.createCell(93).setCellValue((response93 == null) ? "" : String.valueOf(response93.getResponseValue()));
                        ResponseEntity response94 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.3");
                        toInsert.createCell(94).setCellValue((response94 == null) ? "" : String.valueOf(response94.getResponseValue()));
                        ResponseEntity response95 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.4");
                        toInsert.createCell(95).setCellValue((response95 == null) ? "" : String.valueOf(response95.getResponseValue()));
                        ResponseEntity response96 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.5");
                        toInsert.createCell(96).setCellValue((response96 == null) ? "" : String.valueOf(response96.getResponseValue()));
                        ResponseEntity response97 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.6");
                        toInsert.createCell(97).setCellValue((response97 == null) ? "" : String.valueOf(response97.getResponseValue()));
                        ResponseEntity response98 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN1");
                        toInsert.createCell(98).setCellValue((response98 == null) ? "" : String.valueOf(response98.getResponseValue()));
                        ResponseEntity response99 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN2");
                        toInsert.createCell(99).setCellValue((response99 == null) ? "" : String.valueOf(response99.getResponseValue()));
                        ResponseEntity response100 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN3");
                        toInsert.createCell(100).setCellValue((response100 == null) ? "" : String.valueOf(response100.getResponseValue()));
                        ResponseEntity response101 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN4");
                        toInsert.createCell(101).setCellValue((response101 == null) ? "" : String.valueOf(response101.getResponseValue()));
                        ResponseEntity response102 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN5");
                        toInsert.createCell(102).setCellValue((response102 == null) ? "" : String.valueOf(response102.getResponseValue()));
                        ResponseEntity response103 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN6");
                        toInsert.createCell(103).setCellValue((response103 == null) ? "" : String.valueOf(response103.getResponseValue()));
                        ResponseEntity response104 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN7");
                        toInsert.createCell(104).setCellValue((response104 == null) ? "" : String.valueOf(response104.getResponseValue()));
                        ResponseEntity response105 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "RN8");
                        toInsert.createCell(105).setCellValue((response105 == null) ? "" : String.valueOf(response105.getResponseValue()));
                        ResponseEntity response106 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.7");
                        toInsert.createCell(106).setCellValue((response106 == null) ? "" : String.valueOf(response106.getResponseValue()));
                        ResponseEntity response107 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "JJN1");
                        toInsert.createCell(107).setCellValue((response107 == null) ? "" : String.valueOf(response107.getResponseValue()));
                        ResponseEntity response108 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "JJN2");
                        toInsert.createCell(108).setCellValue((response108 == null) ? "" : String.valueOf(response108.getResponseValue()));
                        ResponseEntity response109 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "JJN3");
                        toInsert.createCell(109).setCellValue((response109 == null) ? "" : String.valueOf(response109.getResponseValue()));
                        ResponseEntity response110 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "JJN4");
                        toInsert.createCell(110).setCellValue((response110 == null) ? "" : String.valueOf(response110.getResponseValue()));
                        ResponseEntity response111 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "JJN5");
                        toInsert.createCell(111).setCellValue((response111 == null) ? "" : String.valueOf(response111.getResponseValue()));
                        ResponseEntity response112 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "JJN6");
                        toInsert.createCell(112).setCellValue((response112 == null) ? "" : String.valueOf(response112.getResponseValue()));
                        ResponseEntity response113 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "JJN7");
                        toInsert.createCell(113).setCellValue((response113 == null) ? "" : String.valueOf(response113.getResponseValue()));
                        ResponseEntity response114 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.8");
                        toInsert.createCell(114).setCellValue((response114 == null) ? "" : String.valueOf(response114.getResponseValue()));
                        ResponseEntity response115 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.9");
                        toInsert.createCell(115).setCellValue((response115 == null) ? "" : String.valueOf(response115.getResponseValue()));
                        ResponseEntity response116 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "6.10");
                        toInsert.createCell(116).setCellValue((response116 == null) ? "" : String.valueOf(response116.getResponseValue()));
                        ResponseEntity response117 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "ORB1");
                        toInsert.createCell(117).setCellValue((response117 == null) ? "" : String.valueOf(response117.getResponseValue()));
                        ResponseEntity response118 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "ORB2");
                        toInsert.createCell(118).setCellValue((response118 == null) ? "" : String.valueOf(response118.getResponseValue()));
                        ResponseEntity response119 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "ORB3");
                        toInsert.createCell(119).setCellValue((response119 == null) ? "" : String.valueOf(response119.getResponseValue()));
                        ResponseEntity response120 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "ORB4");
                        toInsert.createCell(120).setCellValue((response120 == null) ? "" : String.valueOf(response120.getResponseValue()));

                        //Strengths 121 - 135
                        ResponseEntity response121 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.1");
                        toInsert.createCell(121).setCellValue((response121 == null) ? "" : String.valueOf(response121.getResponseValue()));
                        ResponseEntity response122 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.2");
                        toInsert.createCell(122).setCellValue((response122 == null) ? "" : String.valueOf(response122.getResponseValue()));
                        ResponseEntity response123 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.3");
                        toInsert.createCell(123).setCellValue((response123 == null) ? "" : String.valueOf(response123.getResponseValue()));
                        ResponseEntity response124 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.4");
                        toInsert.createCell(124).setCellValue((response124 == null) ? "" : String.valueOf(response124.getResponseValue()));
                        ResponseEntity response125 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.5");
                        toInsert.createCell(125).setCellValue((response125 == null) ? "" : String.valueOf(response125.getResponseValue()));
                        ResponseEntity response126 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.6");
                        toInsert.createCell(126).setCellValue((response126 == null) ? "" : String.valueOf(response126.getResponseValue()));
                        ResponseEntity response127 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.7");
                        toInsert.createCell(127).setCellValue((response127 == null) ? "" : String.valueOf(response127.getResponseValue()));
                        ResponseEntity response128 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.8");
                        toInsert.createCell(128).setCellValue((response128 == null) ? "" : String.valueOf(response128.getResponseValue()));
                        ResponseEntity response129 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.9");
                        toInsert.createCell(129).setCellValue((response129 == null) ? "" : String.valueOf(response129.getResponseValue()));
                        ResponseEntity response130 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.10");
                        toInsert.createCell(130).setCellValue((response130 == null) ? "" : String.valueOf(response130.getResponseValue()));
                        ResponseEntity response131 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.11");
                        toInsert.createCell(131).setCellValue((response131 == null) ? "" : String.valueOf(response131.getResponseValue()));
                        ResponseEntity response132 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.12");
                        toInsert.createCell(132).setCellValue((response132 == null) ? "" : String.valueOf(response132.getResponseValue()));
                        ResponseEntity response133 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.13");
                        toInsert.createCell(133).setCellValue((response133 == null) ? "" : String.valueOf(response133.getResponseValue()));
                        ResponseEntity response134 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.14");
                        toInsert.createCell(134).setCellValue((response134 == null) ? "" : String.valueOf(response134.getResponseValue()));
                        ResponseEntity response135 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "7.15");
                        toInsert.createCell(135).setCellValue((response135 == null) ? "" : String.valueOf(response135.getResponseValue()));

                        // Transition to adulthood 136 - 147
                        ResponseEntity response136 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "9.1");
                        toInsert.createCell(136).setCellValue((response136 == null) ? "" : String.valueOf(response136.getResponseValue()));
                        ResponseEntity response137 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "9.2");
                        toInsert.createCell(137).setCellValue((response137 == null) ? "" : String.valueOf(response137.getResponseValue()));
                        ResponseEntity response138 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "9.3");
                        toInsert.createCell(138).setCellValue((response138 == null) ? "" : String.valueOf(response138.getResponseValue()));
                        ResponseEntity response139 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "9.4");
                        toInsert.createCell(139).setCellValue((response139 == null) ? "" : String.valueOf(response139.getResponseValue()));
                        ResponseEntity response140 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "9.5");
                        toInsert.createCell(140).setCellValue((response140 == null) ? "" : String.valueOf(response140.getResponseValue()));
                        ResponseEntity response141 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "9.6");
                        toInsert.createCell(141).setCellValue((response141 == null) ? "" : String.valueOf(response141.getResponseValue()));
                        ResponseEntity response142 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "9.7");
                        toInsert.createCell(142).setCellValue((response142 == null) ? "" : String.valueOf(response142.getResponseValue()));
                        ResponseEntity response143 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CN1");
                        toInsert.createCell(143).setCellValue((response143 == null) ? "" : String.valueOf(response143.getResponseValue()));
                        ResponseEntity response144 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CN2");
                        toInsert.createCell(144).setCellValue((response144 == null) ? "" : String.valueOf(response144.getResponseValue()));
                        ResponseEntity response145 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CN3");
                        toInsert.createCell(145).setCellValue((response145 == null) ? "" : String.valueOf(response145.getResponseValue()));
                        ResponseEntity response146 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CN4");
                        toInsert.createCell(146).setCellValue((response146 == null) ? "" : String.valueOf(response146.getResponseValue()));
                        ResponseEntity response147 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "CN5");
                        toInsert.createCell(147).setCellValue((response147 == null) ? "" : String.valueOf(response147.getResponseValue()));

                        // resdential care
                        ResponseEntity response148 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "10.1");
                        toInsert.createCell(148).setCellValue((response148 == null) ? "" : String.valueOf(response148.getResponseValue()));
                        ResponseEntity response149 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "10.2");
                        toInsert.createCell(149).setCellValue((response149 == null) ? "" : String.valueOf(response149.getResponseValue()));
                        ResponseEntity response150 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "10.3");
                        toInsert.createCell(150).setCellValue((response150 == null) ? "" : String.valueOf(response150.getResponseValue()));
                        ResponseEntity response151 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "10.4");
                        toInsert.createCell(151).setCellValue((response151 == null) ? "" : String.valueOf(response151.getResponseValue()));
                        ResponseEntity response152 = assessmentSessionBeanLocal.getResponseFromAssessmentByCode(assessmentId, "10.5");
                        toInsert.createCell(152).setCellValue((response152 == null) ? "" : String.valueOf(response152.getResponseValue()));

                    } catch (AssessmentNotFoundException ex) { //shouldnt happen
                        Logger.getLogger(ExportSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    toInsert.createCell(153).setCellValue("No"); //preset to no unless otherwise stated

                    if (currAssessment.getCaretakerAssessments().size() != 0) {
                        //caretaker algorithm
                        List<CaretakerAssessmentEntity> caretakerAssessments = currAssessment.getCaretakerAssessments();
                        CaretakerEntity currCaretaker = null;
                        CaretakerAlgorithmEnum levelOfNeeds = null;
                        CaretakerAssessmentEntity currCaretakerAssessment = null;
                        for (CaretakerAssessmentEntity caretakerAssessment : caretakerAssessments) {
                            CaretakerEntity tempCaretaker = caretakerAssessment.getCaretaker();
                            CaretakerAlgorithmEnum currLevelOfNeeds = caretakerAssessment.getLevelOfNeeds();
                            if (levelOfNeeds == null) {
                                currCaretaker = tempCaretaker;
                                currCaretakerAssessment = caretakerAssessment;
                                //levelOfNeeds = currLevelOfNeeds;
                            } else if (levelOfNeeds == CaretakerAlgorithmEnum.HIGH_NEEDS_WITH_RED_FLAGS) {
                                currCaretaker = tempCaretaker;
                                currCaretakerAssessment = caretakerAssessment;
                                break;
                            } else if (levelOfNeeds != CaretakerAlgorithmEnum.HIGH_NEEDS_WITH_RED_FLAGS && currLevelOfNeeds == CaretakerAlgorithmEnum.HIGH_NEEDS_WITH_RED_FLAGS) {
                                currCaretaker = tempCaretaker;
                                currCaretakerAssessment = caretakerAssessment;
                                //levelOfNeeds = currLevelOfNeeds;
                            } else if (levelOfNeeds != CaretakerAlgorithmEnum.HIGH_NEEDS && currLevelOfNeeds == CaretakerAlgorithmEnum.HIGH_NEEDS) {
                                currCaretaker = tempCaretaker;
                                currCaretakerAssessment = caretakerAssessment;
                                //levelOfNeeds = currLevelOfNeeds;
                            } else if ((levelOfNeeds == CaretakerAlgorithmEnum.LOW_NEEDS && currLevelOfNeeds == CaretakerAlgorithmEnum.LOW_NEEDS_WITH_RED_FLAGS)) {
                                currCaretaker = tempCaretaker;
                                currCaretakerAssessment = caretakerAssessment;
                                //levelOfNeeds = currLevelOfNeeds;
                            }
                        }

                        if (currCaretakerAssessment != null) { //populate assessments
                            toInsert.createCell(153).setCellValue("Yes");
                            toInsert.createCell(16).setCellValue(currCaretakerAssessment.getLevelOfNeeds().toString());
                            toInsert.createCell(154).setCellValue(currCaretakerAssessment.getCaretakerType().toString());
                            toInsert.createCell(155).setCellValue(currCaretakerAssessment.getLevelOfNeeds().toString());
                            Long caretakerAssessmentId = currCaretakerAssessment.getCaretakerAssessmentId();

                            //get caretaker assessment responses
                            try {
                                ResponseEntity response156 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8A.1");
                                toInsert.createCell(156).setCellValue((response156 == null) ? "" : String.valueOf(response156.getResponseValue()));
                                ResponseEntity response157 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8A.2");
                                toInsert.createCell(157).setCellValue((response157 == null) ? "" : String.valueOf(response157.getResponseValue()));
                                ResponseEntity response158 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8A.3");
                                toInsert.createCell(158).setCellValue((response158 == null) ? "" : String.valueOf(response158.getResponseValue()));
                                ResponseEntity response159 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8A.4");
                                toInsert.createCell(159).setCellValue((response159 == null) ? "" : String.valueOf(response159.getResponseValue()));
                                ResponseEntity response160 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8A.5");
                                toInsert.createCell(160).setCellValue((response160 == null) ? "" : String.valueOf(response160.getResponseValue()));
                                ResponseEntity response161 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8A.6");
                                toInsert.createCell(161).setCellValue((response161 == null) ? "" : String.valueOf(response161.getResponseValue()));
                                ResponseEntity response162 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8A.7");
                                toInsert.createCell(162).setCellValue((response162 == null) ? "" : String.valueOf(response162.getResponseValue()));
                                ResponseEntity response163 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8B.1");
                                toInsert.createCell(163).setCellValue((response163 == null) ? "" : String.valueOf(response163.getResponseValue()));
                                ResponseEntity response164 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8B.2");
                                toInsert.createCell(164).setCellValue((response164 == null) ? "" : String.valueOf(response164.getResponseValue()));
                                ResponseEntity response165 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8B.3");
                                toInsert.createCell(165).setCellValue((response165 == null) ? "" : String.valueOf(response165.getResponseValue()));
                                ResponseEntity response166 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8B.4");
                                toInsert.createCell(166).setCellValue((response166 == null) ? "" : String.valueOf(response166.getResponseValue()));
                                ResponseEntity response167 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8C.1");
                                toInsert.createCell(167).setCellValue((response167 == null) ? "" : String.valueOf(response167.getResponseValue()));
                                ResponseEntity response168 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8C.2");
                                toInsert.createCell(168).setCellValue((response168 == null) ? "" : String.valueOf(response168.getResponseValue()));
                                ResponseEntity response169 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8C.3");
                                toInsert.createCell(169).setCellValue((response169 == null) ? "" : String.valueOf(response169.getResponseValue()));
                                ResponseEntity response170 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8C.4");
                                toInsert.createCell(170).setCellValue((response170 == null) ? "" : String.valueOf(response170.getResponseValue()));
                                ResponseEntity response171 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8C.5");
                                toInsert.createCell(171).setCellValue((response171 == null) ? "" : String.valueOf(response171.getResponseValue()));
                                ResponseEntity response172 = caretakerAssessmentSessionBeanLocal.getResponseFromCaretakerAssessmentByCode(caretakerAssessmentId, "8C.6");
                                toInsert.createCell(172).setCellValue((response172 == null) ? "" : String.valueOf(response172.getResponseValue()));
                            } catch (CaretakerAssessmentNotFoundException ex) {
                                Logger.getLogger(ExportSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }

            Row row = cleanSheet.getRow(0);

            CellStyle whiteBorder = cleanSheet.getWorkbook().createCellStyle();
            whiteBorder.setBorderRight(BorderStyle.THICK);

            CellStyle greyStyle = cleanSheet.getWorkbook().createCellStyle();
            greyStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            // and solid fill pattern produces solid grey cell fill
            greyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle goldStyle = cleanSheet.getWorkbook().createCellStyle();
            goldStyle.setFillForegroundColor(IndexedColors.GOLD.index);
            // and solid fill pattern produces solid grey cell fill
            goldStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle tanStyle = cleanSheet.getWorkbook().createCellStyle();
            tanStyle.setFillForegroundColor(IndexedColors.TAN.index);
            // and solid fill pattern produces solid grey cell fill
            tanStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle tanStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            tanStyleBorder.setFillForegroundColor(IndexedColors.TAN.index);
            // and solid fill pattern produces solid grey cell fill
            tanStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tanStyleBorder.setBorderRight(BorderStyle.THICK);

            Font whiteFont = cleanWorkbook.createFont();
            whiteFont.setColor(IndexedColors.WHITE.index);
            CellStyle redStyle = cleanSheet.getWorkbook().createCellStyle();
            // fill foreground color ...
            redStyle.setFillForegroundColor(IndexedColors.RED.index);
            // and solid fill pattern produces solid grey cell fill
            redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            redStyle.setFont(whiteFont);

            CellStyle redStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            // fill foreground color ...
            redStyleBorder.setFillForegroundColor(IndexedColors.RED.index);
            // and solid fill pattern produces solid grey cell fill
            redStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            redStyleBorder.setFont(whiteFont);
            redStyleBorder.setBorderRight(BorderStyle.THICK);

            CellStyle blueDarkStyle = cleanSheet.getWorkbook().createCellStyle();
            blueDarkStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
            // and solid fill pattern produces solid grey cell fill
            blueDarkStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle blueDarkStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            blueDarkStyleBorder.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
            // and solid fill pattern produces solid grey cell fill
            blueDarkStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            blueDarkStyleBorder.setBorderRight(BorderStyle.THICK);

            CellStyle blueLightStyle = cleanSheet.getWorkbook().createCellStyle();
            blueLightStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.index);
            // and solid fill pattern produces solid grey cell fill
            blueLightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle blueLightStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            blueLightStyleBorder.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.index);
            // and solid fill pattern produces solid grey cell fill
            blueLightStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            blueLightStyleBorder.setBorderRight(BorderStyle.THICK);

            CellStyle greenDarkStyle = cleanSheet.getWorkbook().createCellStyle();
            greenDarkStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
            // and solid fill pattern produces solid grey cell fill
            greenDarkStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            greenDarkStyle.setFont(whiteFont);

            CellStyle greenDarkStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            greenDarkStyleBorder.setFillForegroundColor(IndexedColors.SEA_GREEN.index);
            // and solid fill pattern produces solid grey cell fill
            greenDarkStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            greenDarkStyleBorder.setBorderRight(BorderStyle.THICK);
            greenDarkStyleBorder.setFont(whiteFont);

            CellStyle greenLightStyle = cleanSheet.getWorkbook().createCellStyle();
            greenLightStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
            // and solid fill pattern produces solid grey cell fill
            greenLightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle yellowDarkStyle = cleanSheet.getWorkbook().createCellStyle();
            yellowDarkStyle.setFillForegroundColor(IndexedColors.GOLD.index);
            // and solid fill pattern produces solid grey cell fill
            yellowDarkStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle yellowDarkStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            yellowDarkStyleBorder.setFillForegroundColor(IndexedColors.GOLD.index);
            // and solid fill pattern produces solid grey cell fill
            yellowDarkStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            yellowDarkStyleBorder.setBorderRight(BorderStyle.THICK);

            CellStyle yellowLightStyle = cleanSheet.getWorkbook().createCellStyle();
            yellowLightStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            // and solid fill pattern produces solid grey cell fill
            yellowLightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle yellowLightStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            yellowLightStyleBorder.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            // and solid fill pattern produces solid grey cell fill
            yellowLightStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            yellowLightStyleBorder.setBorderRight(BorderStyle.THICK);

            CellStyle purpleDarkStyle = cleanSheet.getWorkbook().createCellStyle();
            purpleDarkStyle.setFillForegroundColor(IndexedColors.PLUM.index);
            // and solid fill pattern produces solid grey cell fill
            purpleDarkStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            purpleDarkStyle.setFont(whiteFont);

            CellStyle purpleLightStyle = cleanSheet.getWorkbook().createCellStyle();
            purpleLightStyle.setFillForegroundColor(IndexedColors.LAVENDER.index);
            // and solid fill pattern produces solid grey cell fill
            purpleLightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle purpleLightStyleBorder = cleanSheet.getWorkbook().createCellStyle();
            purpleLightStyleBorder.setFillForegroundColor(IndexedColors.LAVENDER.index);
            // and solid fill pattern produces solid grey cell fill
            purpleLightStyleBorder.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            purpleLightStyleBorder.setBorderRight(BorderStyle.THICK);

            //setting header style
            row.getCell(0).setCellStyle(greyStyle);
            row.getCell(1).setCellStyle(goldStyle);
            for (int i = 2; i < 5; i++) {
                row.getCell(i).setCellStyle(tanStyle);
            }
            row.getCell(5).setCellStyle(greyStyle);
            for (int i = 6; i < 9; i++) {
                row.getCell(i).setCellStyle(redStyle);
            }
            for (int i = 9; i < 11; i++) {
                row.getCell(i).setCellStyle(tanStyle);
            }
            row.getCell(11).setCellStyle(greyStyle);
            for (int i = 12; i < 16; i++) {
                row.getCell(i).setCellStyle(goldStyle);
            }
            row.getCell(16).setCellStyle(redStyleBorder);
            row.getCell(17).setCellStyle(blueDarkStyle);
            for (int i = 18; i < 23; i++) {
                row.getCell(i).setCellStyle(blueLightStyle);
            }
            for (int i = 23; i < 29; i++) {
                row.getCell(i).setCellStyle(blueDarkStyle);
            }
            for (int i = 29; i < 31; i++) {
                row.getCell(i).setCellStyle(blueLightStyle);
            }
            row.getCell(31).setCellStyle(blueLightStyleBorder);
            for (int i = 32; i < 36; i++) {
                row.getCell(i).setCellStyle(greenDarkStyle);
            }
            row.getCell(36).setCellStyle(yellowDarkStyle);
            for (int i = 37; i < 41; i++) {
                row.getCell(i).setCellStyle(yellowLightStyle);
            }
            for (int i = 41; i < 44; i++) {
                row.getCell(i).setCellStyle(yellowDarkStyle);
            }
            row.getCell(44).setCellStyle(tanStyleBorder);
            for (int i = 45; i < 48; i++) {
                row.getCell(i).setCellStyle(greenDarkStyle);
            }
            row.getCell(48).setCellStyle(blueDarkStyle);
            for (int i = 49; i < 54; i++) {
                row.getCell(i).setCellStyle(blueLightStyle);
            }
            for (int i = 54; i < 58; i++) {
                row.getCell(i).setCellStyle(blueDarkStyle);
            }
            row.getCell(58).setCellStyle(blueDarkStyleBorder);
            for (int i = 59; i < 65; i++) {
                row.getCell(i).setCellStyle(greenDarkStyle);
            }
            row.getCell(62).setCellStyle(greenDarkStyleBorder);
            row.getCell(65).setCellStyle(blueDarkStyle);
            for (int i = 66; i < 71; i++) {
                row.getCell(i).setCellStyle(yellowDarkStyle);
            }
            for (int i = 71; i < 77; i++) {
                row.getCell(i).setCellStyle(yellowLightStyle);
            }
            for (int i = 77; i < 80; i++) {
                row.getCell(i).setCellStyle(yellowDarkStyle);
            }
            row.getCell(80).setCellStyle(yellowDarkStyleBorder);
            row.getCell(81).setCellStyle(greenDarkStyle);
            for (int i = 82; i < 86; i++) {
                row.getCell(i).setCellStyle(greenLightStyle);
            }
//            for (int i = 86; i < 89; i++) {
//                row.getCell(i).setCellStyle(greyStyle);
//            }
            for (int i = 89; i < 93; i++) {
                row.getCell(i).setCellStyle(greenLightStyle);
            }
            row.getCell(93).setCellStyle(greenDarkStyle);
            row.getCell(94).setCellStyle(blueDarkStyle);
            for (int i = 95; i < 98; i++) {
                row.getCell(i).setCellStyle(yellowDarkStyle);
            }
            for (int i = 98; i < 106; i++) {
                row.getCell(i).setCellStyle(yellowLightStyle);
            }
            row.getCell(106).setCellStyle(yellowDarkStyle);
            for (int i = 107; i < 114; i++) {
                row.getCell(i).setCellStyle(yellowLightStyle);
            }
            for (int i = 114; i < 117; i++) {
                row.getCell(i).setCellStyle(yellowDarkStyle);
            }
            for (int i = 117; i < 120; i++) {
                row.getCell(i).setCellStyle(yellowLightStyle);
            }
            row.getCell(120).setCellStyle(yellowLightStyleBorder);
            for (int i = 121; i < 123; i++) {
                row.getCell(i).setCellStyle(greenDarkStyle);
            }
            for (int i = 123; i < 126; i++) {
                row.getCell(i).setCellStyle(blueDarkStyle);
            }
            for (int i = 126; i < 135; i++) {
                row.getCell(i).setCellStyle(yellowDarkStyle);
            }
            row.getCell(135).setCellStyle(tanStyleBorder);
            for (int i = 136; i < 143; i++) {
                row.getCell(i).setCellStyle(purpleDarkStyle);
            }
            for (int i = 143; i < 147; i++) {
                row.getCell(i).setCellStyle(purpleLightStyle);
            }
            row.getCell(147).setCellStyle(purpleLightStyleBorder);
            for (int i = 148; i < 152; i++) {
                row.getCell(i).setCellStyle(greenDarkStyle);
            }
            row.getCell(152).setCellStyle(greenDarkStyleBorder);
            row.getCell(153).setCellStyle(redStyle);
            row.getCell(155).setCellStyle(redStyle);
            for (int i = 156; i < 172; i++) {
                row.getCell(i).setCellStyle(greenDarkStyle);
            }
            row.getCell(172).setCellStyle(greenDarkStyleBorder);

//            Row testRow = cleanSheet.createRow(1); //test
//            testRow.getCell(31).setCellStyle(greenDarkStyleBorder); //null pointer
//
//            if (testRow.getCell(31) != null) {
//                System.out.println("*************************************1");
//                testRow.getCell(31).setCellStyle(greenDarkStyleBorder);
//            } else {
//                testRow.createCell(31).setCellStyle(greenDarkStyleBorder);
//            } works
            for (Row r : cleanSheet) {
                if (r.getRowNum() == 0) {
                    continue;
                }

                if (r.getCell(16) != null) {
                    r.getCell(16).setCellStyle(whiteBorder);
                } else {
                    r.createCell(16).setCellStyle(whiteBorder);
                }

                if (r.getCell(31) != null) {
                    r.getCell(31).setCellStyle(whiteBorder);
                } else {
                    r.createCell(31).setCellStyle(whiteBorder);
                }

                if (r.getCell(44) != null) {
                    r.getCell(44).setCellStyle(whiteBorder);
                } else {
                    r.createCell(44).setCellStyle(whiteBorder);
                }

                if (r.getCell(58) != null) {
                    r.getCell(58).setCellStyle(whiteBorder);
                } else {
                    r.createCell(58).setCellStyle(whiteBorder);
                }

                if (r.getCell(62) != null) {
                    r.getCell(62).setCellStyle(whiteBorder);
                } else {
                    r.createCell(62).setCellStyle(whiteBorder);
                }

                if (r.getCell(80) != null) {
                    r.getCell(80).setCellStyle(whiteBorder);
                } else {
                    r.createCell(80).setCellStyle(whiteBorder);
                }

                if (r.getCell(120) != null) {
                    r.getCell(120).setCellStyle(whiteBorder);
                } else {
                    r.createCell(120).setCellStyle(whiteBorder);
                }

                if (r.getCell(135) != null) {
                    r.getCell(135).setCellStyle(whiteBorder);
                } else {
                    r.createCell(135).setCellStyle(whiteBorder);
                }

                if (r.getCell(147) != null) {
                    r.getCell(147).setCellStyle(whiteBorder);
                } else {
                    r.createCell(147).setCellStyle(whiteBorder);
                }

                if (r.getCell(152) != null) {
                    r.getCell(152).setCellStyle(whiteBorder);
                } else {
                    r.createCell(152).setCellStyle(whiteBorder);
                }

                if (r.getCell(172) != null) {
                    r.getCell(172).setCellStyle(whiteBorder);
                } else {
                    r.createCell(172).setCellStyle(whiteBorder);
                }
            }

            for (int c = 0; c < 173; c++) {
                cleanSheet.autoSizeColumn(c);
            }

            //print in output directory
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
            Date date = new Date();
            String dateNow = formatter.format(date);
            FileOutputStream outputStream = new FileOutputStream("C:\\glassfish-5.1.0-uploadedfiles\\CANS_GENERATED\\" + dateNow + ".xlsx");
            cleanWorkbook.write(outputStream); //C:/Users/Ziyue/Desktop/NUS/IS4103_Capstone/CANS/CANS-Backend/CANSBackend-ejb/tmp // this ziyue so shameless...
            cleanWorkbook.close();
            System.out.println("Excel file generated succesfully at http://localhost:8080/cans-backend-rws/CANS_GENERATED/" + dateNow + ".xlsx");
            return "http://localhost:8080/cans-backend-rws/CANS_GENERATED/" + dateNow + ".xlsx";
        } catch (IOException e) {
            throw new ExportDataException();
        }
    }

}

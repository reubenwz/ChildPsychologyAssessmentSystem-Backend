/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CertificationEntity;
import entity.OrganisationEntity;
import helperClassess.OrganisationInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Local;
import util.exception.OrganisationNotFoundException;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface VisualisationSessionBeanLocal {

    public Pair<List<String>, List<Double>> getAvgOfAllQuesAgainstQuesGraph();

    public Pair<List<String>, List<Double>> getAllDomainAverage(Date startDate, Date endDate, String ageGroup);

    public Pair<List<String>, List<Long>> getTotalAgeGroup();

    public Pair<List<String>, List<Long>> getTotalLoc();

    public List<OrganisationInfo> getAssessmentDonePerOrganisation(Date start, Date end);

//    public HashMap<String, List<Integer>> getDistributionOfLoc();
//
//    public HashMap<String, List<Integer>> getDistributionOfLocByOrganisations(List<String> organisations) throws OrganisationNotFoundException;
//
//    public HashMap<String, List<Integer>> getDistributionOfLocByAgencyType(List<String> type);
    public HashMap<String, List<Integer>> getDistributionOfLocByOrganisations(List<String> organisations, Date start, Date end, String ageRange, List<String> assessmentResons) throws OrganisationNotFoundException;

//    public List<String> getOrgFromAgencyType(List<String> type);
    public List<String> getOrgFromAgencyType(String type);

    public Pair<List<String>, List<Long>> getCountByRace();

    public Pair<List<String>, List<Long>> getCountByGender();

    public Pair<List<String>, List<Double>> getTraumaPercentage(List<String> organisationNames, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException;

    public HashMap<String, Integer> getTopTrauma(List<String> organisationNames, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException;

    public HashMap<String, Integer> getTopStrength(List<String> organisationNames, Date start, Date end, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException;

    public HashMap<String, Integer> getTopNeeds(List<String> organisationNames, Date start, Date end, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException;

    public Pair<List<String>, List<Long>> getTotalAgeGroup(OrganisationEntity org);

    public Pair<List<String>, List<Long>> getCountByGender(OrganisationEntity org);

    public Pair<List<String>, List<Long>> getCountByRace(OrganisationEntity org);

    public Pair<List<String>, List<Double>> getAllDomainAverage(Date startDate, Date endDate, String ageGroup, OrganisationEntity org);

    public Pair<List<String>, List<Long>> getRoles(Long orgId);

    public Pair<List<Integer>, List<Double>> getScores(Long assId);

    public Pair<List<String>, List<Double>> getRectifiedTimes(Long orgId);

    public Pair<List<String>, List<CertificationEntity>> getAssessorExpired(Long orgId);

    public Pair<List<String>, List<CertificationEntity>> getAssessorExpiredSoon(Long orgId);

}

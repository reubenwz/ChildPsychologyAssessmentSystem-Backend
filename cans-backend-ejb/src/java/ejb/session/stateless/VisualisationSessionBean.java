/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessmentEntity;
import entity.AssessorEntity;
import entity.CertificationEntity;
import entity.ClientEntity;
import entity.DomainEntity;
import entity.MainQuestionEntity;
import entity.OrganisationEntity;
import entity.QuestionEntity;
import entity.ResponseEntity;
import entity.SubModuleEntity;
import entity.SubQuestionEntity;
import helperClassess.OrganisationInfo;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.OrganisationNotFoundException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class VisualisationSessionBean implements VisualisationSessionBeanLocal {

    @EJB
    private CertificationSessionBeanLocal certificationSessionBean;

    @EJB
    private AssessorSessionBeanLocal assessorSessionBean;

    @EJB
    private SubModuleSessionBeanLocal subModuleSessionBean;

    @EJB
    private OrganisationSessionBeanLocal organisationSessionBean;

    @EJB
    private AssessmentSessionBeanLocal assessmentSessionBean;

    @EJB
    private ClientSessionBeanLocal clientSessionBean;

    @EJB
    private ResponseSessionBeanLocal responseSessionBean;

    @EJB
    private QuestionsSessionBeanLocal questionsSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    public VisualisationSessionBean() {
    }

    @Override
    public Pair<List<String>, List<Double>> getAvgOfAllQuesAgainstQuesGraph() {

        List<QuestionEntity> allQues = questionsSessionBean.retrieveAllQuestion();
        List<String> xAxis = allQues.stream().map(q -> q.getQuestionTitle()).collect(Collectors.toList());
        List<Double> yAxis = new ArrayList<>();
        for (int i = 0; i < allQues.size(); i++) {
            long quesId = allQues.get(i).getQuestionId();
            List<ResponseEntity> allResponseForQues = responseSessionBean.retrieveAllResponseByQuesId(quesId);

            if (allResponseForQues.isEmpty()) {
                yAxis.add(Double.valueOf(0));
                continue;
            }
            double sum = allResponseForQues.stream().mapToDouble(r -> r.getResponseValue()).sum();
            double avg = sum / Double.valueOf(allResponseForQues.size());
            yAxis.add(avg);
        }
        Pair<List<String>, List<Double>> map = new Pair(xAxis, yAxis);
        return map;
    }

    @Override
    public Pair<List<String>, List<Long>> getTotalLoc() {
        List<String> labels = new ArrayList<>();
        labels.add("Level One");
        labels.add("Level Two");
        labels.add("Level Three");

        List<Long> sumOfClients = new ArrayList<>();
        List<ClientEntity> allClients = clientSessionBean.retrieveAllClientEntities();
        Long locOneCount = 0l;
        Long locTwoCount = 0l;
        Long locThreeCount = 0l;
        for (ClientEntity client : allClients) {
            List<AssessmentEntity> allAssessment = client.getAssessment();

            // getting latest assessment results
            int loc = allAssessment.get(allAssessment.size() - 1).getLoc();
            if (loc == 1) {
                locOneCount++;
            } else if (loc == 2) {
                locTwoCount++;
            } else if (loc == 3) {
                locThreeCount++;
            } else { //case of null loc results
                continue;
            }

        }
        sumOfClients.add(locOneCount);
        sumOfClients.add(locTwoCount);
        sumOfClients.add(locThreeCount);

        Pair<List<String>, List<Long>> results = new Pair(labels, sumOfClients);
        return results;
    }

    @Override
    public Pair<List<String>, List<Long>> getTotalAgeGroup() {

        List<String> ageGroupLabels = new ArrayList<>();
        ageGroupLabels.add("0-6");
        ageGroupLabels.add("7-13");
        ageGroupLabels.add("14-17");
        ageGroupLabels.add("17+");

        List<ClientEntity> adol = clientSessionBean.retreiveClientsByAgeGroup("0-6");
        List<ClientEntity> child = clientSessionBean.retreiveClientsByAgeGroup("7-13");
        List<ClientEntity> teen = clientSessionBean.retreiveClientsByAgeGroup("14-16");
        List<ClientEntity> youngAdult = clientSessionBean.retreiveClientsByAgeGroup("17-20");

        List<Long> countOfClients = new ArrayList<>();
        countOfClients.add(new Long(adol.size()));
        countOfClients.add(new Long(child.size()));
        countOfClients.add(new Long(teen.size()));
        countOfClients.add(new Long(youngAdult.size()));
        Pair<List<String>, List<Long>> results = new Pair(ageGroupLabels, countOfClients);
        return results;
    }

    // for assessor system
    @Override
    public Pair<List<String>, List<Long>> getTotalAgeGroup(OrganisationEntity org) {

        List<String> ageGroupLabels = new ArrayList<>();
        ageGroupLabels.add("0-6");
        ageGroupLabels.add("7-13");
        ageGroupLabels.add("14-17");
        ageGroupLabels.add("17+");

        List<ClientEntity> adol = clientSessionBean.retrieveClientsByAgeGroupInOrg(org.getOrganisationId(), "0-6");
        List<ClientEntity> child = clientSessionBean.retrieveClientsByAgeGroupInOrg(org.getOrganisationId(), "7-13");
        List<ClientEntity> teen = clientSessionBean.retrieveClientsByAgeGroupInOrg(org.getOrganisationId(), "14-16");
        List<ClientEntity> youngAdult = clientSessionBean.retrieveClientsByAgeGroupInOrg(org.getOrganisationId(), "17-20");

        List<Long> countOfClients = new ArrayList<>();
        countOfClients.add(new Long(adol.size()));
        countOfClients.add(new Long(child.size()));
        countOfClients.add(new Long(teen.size()));
        countOfClients.add(new Long(youngAdult.size()));
        Pair<List<String>, List<Long>> results = new Pair(ageGroupLabels, countOfClients);
        return results;
    }

    @Override
    public Pair<List<String>, List<Double>> getAllDomainAverage(Date startDate, Date endDate, String ageGroup) {
        HashMap<String, List<Integer>> quesResMap = new HashMap<>();

        List<AssessmentEntity> assessments = assessmentSessionBean.retrieveAssessmentWithinDate(startDate, endDate);

        String[] ageGroupArr = ageGroup.split("-");
        int lower = Integer.parseInt(ageGroupArr[0]);
        int upper = Integer.parseInt(ageGroupArr[1]);

        for (AssessmentEntity ass : assessments) {

            if (isWithinAgeRange(ass, lower, upper)) {
                List<ResponseEntity> responses = ass.getResponse();

                for (ResponseEntity res : responses) {
                    DomainEntity domain = null;
                    if (res.getQuestion() instanceof MainQuestionEntity) {
                        domain = ((MainQuestionEntity) res.getQuestion()).getAgeGroup().getDomain();
                    } else {

                        MainQuestionEntity associatedMainQues = ((SubQuestionEntity) res.getQuestion()).getSubmodule().getQues().get(0);
                        domain = associatedMainQues.getAgeGroup().getDomain();
                    }

                    if (quesResMap.containsKey(domain.getDomainName())) {
                        quesResMap.get(domain.getDomainName()).add(res.getResponseValue());

                    } else {
                        List<Integer> responseList = new ArrayList<>();
                        responseList.add(res.getResponseValue());
                        quesResMap.put(domain.getDomainName(), responseList);
                    }
                }
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        quesResMap.entrySet()
                .forEach(map -> {
                    String domainName = (String) map.getKey();
                    List<Integer> responses = (List<Integer>) map.getValue();

                    double sum = responses.stream().mapToDouble(r -> r).sum();
                    double avg = sum / Double.valueOf(responses.size());

                    labels.add(domainName);
                    data.add(avg);
                }
                );

        return new Pair<>(labels, data);
    }

    @Override
    public Pair<List<String>, List<Double>> getAllDomainAverage(Date startDate, Date endDate, String ageGroup, OrganisationEntity org) {
        HashMap<String, List<Integer>> quesResMap = new HashMap<>();

        List<AssessmentEntity> assessments = assessmentSessionBean.retrieveAssessmentWithinDate(startDate, endDate);

        String[] ageGroupArr = ageGroup.split("-");
        int lower = Integer.parseInt(ageGroupArr[0].trim());
        int upper = Integer.parseInt(ageGroupArr[1].trim());

        for (AssessmentEntity ass : assessments) {

            if (isWithinAgeRange(ass, lower, upper) && ass.getAssessor().getOrganisation().getOrganisationId() == org.getOrganisationId()) {
                List<ResponseEntity> responses = ass.getResponse();

                for (ResponseEntity res : responses) {
                    DomainEntity domain = null;
                    if (res.getQuestion() instanceof MainQuestionEntity) {
                        domain = ((MainQuestionEntity) res.getQuestion()).getAgeGroup().getDomain();
                    } else {

                        MainQuestionEntity associatedMainQues = ((SubQuestionEntity) res.getQuestion()).getSubmodule().getQues().get(0);
                        domain = associatedMainQues.getAgeGroup().getDomain();
                    }

                    if (quesResMap.containsKey(domain.getDomainName())) {
                        quesResMap.get(domain.getDomainName()).add(res.getResponseValue());

                    } else {
                        List<Integer> responseList = new ArrayList<>();
                        responseList.add(res.getResponseValue());
                        quesResMap.put(domain.getDomainName(), responseList);
                    }
                }
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        quesResMap.entrySet()
                .forEach(map -> {
                    String domainName = (String) map.getKey();
                    List<Integer> responses = (List<Integer>) map.getValue();

                    double sum = responses.stream().mapToDouble(r -> r).sum();
                    double avg = sum / Double.valueOf(responses.size());

                    labels.add(domainName);
                    data.add(avg);
                }
                );

        return new Pair<>(labels, data);
    }

    @Override
    public List<OrganisationInfo> getAssessmentDonePerOrganisation(Date start, Date end) {

        //all assessments in the year
        List<AssessmentEntity> allAssessments = assessmentSessionBean.retrieveAssessmentWithinDate(start, end);
        List<OrganisationEntity> allOrg = organisationSessionBean.retrieveAllOrganisation();
        HashMap<String, OrganisationInfo> map = new HashMap<>();

        //create new org info object for each org
        for (OrganisationEntity org : allOrg) {
            List<ClientEntity> clients = organisationSessionBean.retrieveAllClientsInOrganisation(org.getOrganisationId());
            map.put(org.getName(), new OrganisationInfo(org.getName(), clients.size()));
        }

        //since each assessor belong to 1 org, if it exist in collection it has previously done an
        //assessment in the same year
        Set<AssessorEntity> collection = new HashSet<>();
        for (AssessmentEntity ass : allAssessments) {
            AssessorEntity assessor = ass.getAssessor();
            OrganisationEntity org = assessor.getOrganisation();
            OrganisationInfo info = map.get(org.getName());
            //ensuring only new assessors are counted
            if (!collection.contains(assessor)) {
                collection.add(assessor);
                info.incrementNumOfAssessors();
            }
            info.incrementNumOfAssessments();
//            System.out.println(info);
            map.put(org.getName(), info);
        }

//        System.out.println(map.values());
        List<OrganisationInfo> result = new ArrayList<>();

        for (OrganisationInfo url : map.values()) {
            result.add(url);

        }
        return result;

    }

    @Override
    public HashMap<String, Integer> getTopNeeds(List<String> organisationNames, Date start, Date end, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException {
        try {
            List<AssessmentEntity> allAssessment = new ArrayList<>();

            // get all organisation
            for (String orgName : organisationNames) {
                OrganisationEntity org = organisationSessionBean.retrieveOrganisationByName(orgName);
                List<ClientEntity> allClients = clientSessionBean.retrieveAllClientsInOrg(org.getOrganisationId());

                List<ClientEntity> clientNeeded = new ArrayList<>();

                for (ClientEntity client : allClients) {
                    if (race.contains(client.getEthnicity()) && gender.contains(client.getGender())) {
                        clientNeeded.add(client);
                    }
                }

                for (int i = 0; i < clientNeeded.size(); i++) {
                    //get all assessments of this client that fits the date range
                    List<AssessmentEntity> ass = assessmentSessionBean.retrieveAssessmentByClientWithinDate(clientNeeded.get(i).getClientUniqueId(), start, end);
                    for (int j = 0; j < ass.size(); j++) {
                        allAssessment.add(ass.get(j));
                    }
                }
            }

            String domainToIgnore = "Strength";
            List<SubModuleEntity> subModuleToIgnore = subModuleSessionBean.retrieveSubModuleByCategory(true);

            String[] ageGroupArr = ageRange.split("-");
            int lower = Integer.parseInt(ageGroupArr[0].trim());
            int upper = Integer.parseInt(ageGroupArr[1].trim());

            HashMap<String, Integer> map = new HashMap<>();
            for (int k = 0; k < allAssessment.size(); k++) {
                AssessmentEntity currentAss = allAssessment.get(k);
                if (isWithinAgeRange(currentAss, lower, upper)) {
                    List<ResponseEntity> allRes = currentAss.getResponse();

                    for (int l = 0; l < allRes.size(); l++) {
                        Boolean ignore = false;
                        ResponseEntity currRes = allRes.get(l);

                        DomainEntity domain = null;
                        //checking which domain res belong to
                        if (currRes.getQuestion() instanceof MainQuestionEntity) {
                            domain = ((MainQuestionEntity) currRes.getQuestion()).getAgeGroup().getDomain();
                            if (domainToIgnore.equalsIgnoreCase(domain.getDomainName())) {
                                ignore = true;
                            }
                        } else {
                            SubModuleEntity submodule = ((SubQuestionEntity) currRes.getQuestion()).getSubmodule();
                            if (subModuleToIgnore.contains(submodule)) {
                                ignore = true;
                            } else {
                                System.out.println(submodule.getSubModuleId());
                                System.out.println(submodule.getSubModuleName());
                                System.out.println(submodule.getQues().size());
                                MainQuestionEntity associatedMainQues = submodule.getQues().get(0);
                                domain = associatedMainQues.getAgeGroup().getDomain();
                            }
                        }
                        if (!ignore && (currRes.getResponseValue() == 2 || currRes.getResponseValue() == 3)) {
                            if (map.containsKey(currRes.getQuestion().getQuestionTitle())) {
                                int value = map.get(currRes.getQuestion().getQuestionTitle());

                                map.put(currRes.getQuestion().getQuestionTitle(), ++value);
                            } else {

                                map.put(currRes.getQuestion().getQuestionTitle(), 1);
                            }
                        }
                    }
                }
            }
            return map;
        } catch (OrganisationNotFoundException ex) {
            throw new OrganisationNotFoundException(ex.getMessage());
        }

    }

    @Override
    public HashMap<String, Integer> getTopStrength(List<String> organisationNames, Date start, Date end, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException {
        try {

            List<AssessmentEntity> allAssessment = new ArrayList<>();

            // get all organisation
            for (String orgName : organisationNames) {
                OrganisationEntity org = organisationSessionBean.retrieveOrganisationByName(orgName);
                List<ClientEntity> allClients = clientSessionBean.retrieveAllClientsInOrg(org.getOrganisationId());
                List<ClientEntity> clientNeeded = new ArrayList<>();

                for (ClientEntity client : allClients) {
                    if (race.contains(client.getEthnicity()) && gender.contains(client.getGender())) {
                        clientNeeded.add(client);
                    }
                }

                for (int i = 0; i < clientNeeded.size(); i++) {
                    //get all assessments of this client that fits the date range
                    List<AssessmentEntity> ass = assessmentSessionBean.retrieveAssessmentByClientWithinDate(clientNeeded.get(i).getClientUniqueId(), start, end);
                    for (int j = 0; j < ass.size(); j++) {
                        allAssessment.add(ass.get(j));
                    }
                }
            }

            String[] ageGroupArr = ageRange.split("-");
            int lower = Integer.parseInt(ageGroupArr[0].trim());
            int upper = Integer.parseInt(ageGroupArr[1].trim());

            HashMap<String, Integer> map = new HashMap<>();
            for (int k = 0; k < allAssessment.size(); k++) {
                AssessmentEntity currentAss = allAssessment.get(k);
                if (isWithinAgeRange(currentAss, lower, upper)) {
                    List<ResponseEntity> allRes = currentAss.getResponse();
                    for (int l = 0; l < allRes.size(); l++) {
                        ResponseEntity currRes = allRes.get(l);
                        //checking domain of res
                        if (currRes.getQuestion() instanceof MainQuestionEntity) {
                            DomainEntity domain = ((MainQuestionEntity) currRes.getQuestion()).getAgeGroup().getDomain();
                            if (domain.getDomainName().equalsIgnoreCase("Strength")) {
                                if (currRes.getResponseValue() == 0 || currRes.getResponseValue() == 1) {
                                    if (map.containsKey(currRes.getQuestion().getQuestionTitle())) {
                                        int value = map.get(currRes.getQuestion().getQuestionTitle());
                                        map.put(currRes.getQuestion().getQuestionTitle(), ++value);
                                    } else {
                                        map.put(currRes.getQuestion().getQuestionTitle(), 1);
                                    }
                                }
                            } else { // belongs to other domain
                                continue;
                            }
                        } else { //strengths do not have a sub module
                            continue;
                        }
                    }
                }
            }

            return map;
        } catch (OrganisationNotFoundException ex) {
            throw new OrganisationNotFoundException(ex.getMessage());
        }

    }

    @Override
    public HashMap<String, Integer> getTopTrauma(List<String> organisationNames, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException {
        try {
            List<AssessmentEntity> allAssessment = new ArrayList<>();
            // get all organisation
            for (String orgName : organisationNames) {
                OrganisationEntity org = organisationSessionBean.retrieveOrganisationByName(orgName);
                List<ClientEntity> allClients = clientSessionBean.retrieveAllClientsInOrg(org.getOrganisationId());
                List<ClientEntity> clientNeeded = new ArrayList<>();

                for (ClientEntity client : allClients) {
                    if (race.contains(client.getEthnicity()) && gender.contains(client.getGender())) {
                        clientNeeded.add(client);
                    }
                }

                for (int i = 0; i < clientNeeded.size(); i++) {
                    //for trauma only the 1st assessment is needed
                    if (!clientNeeded.get(i).getAssessment().isEmpty()) {
                        allAssessment.add(clientNeeded.get(i).getAssessment().get(0));
                    }
                }
            }

            String[] ageGroupArr = ageRange.split("-");
            int lower = Integer.parseInt(ageGroupArr[0].trim());
            int upper = Integer.parseInt(ageGroupArr[1].trim());

            HashMap<String, Integer> map = new HashMap<>();
            for (int k = 0; k < allAssessment.size(); k++) {
                AssessmentEntity currentAss = allAssessment.get(k);
                if (isWithinAgeRange(currentAss, lower, upper)) {
                    List<ResponseEntity> allRes = currentAss.getResponse();
                    for (int l = 0; l < allRes.size(); l++) {
                        ResponseEntity currRes = allRes.get(l);
                        //checking domain of res
                        if (currRes.getQuestion() instanceof SubQuestionEntity) {
                            if (((SubQuestionEntity) currRes.getQuestion()).getSubmodule().getSubModuleName().equalsIgnoreCase("Trauma (TR) Module")
                                    && !(currRes.getQuestion().getQuestionCode().equals("TR4a") || currRes.getQuestion().getQuestionCode().equals("TR4b")
                                    || currRes.getQuestion().getQuestionCode().equals("TR4c"))) {
                                if (currRes.getResponseValue() == 2 || currRes.getResponseValue() == 3) {
                                    if (map.containsKey(currRes.getQuestion().getQuestionTitle())) {
                                        int value = map.get(currRes.getQuestion().getQuestionTitle());
                                        map.put(currRes.getQuestion().getQuestionTitle(), ++value);
                                    } else {
                                        map.put(currRes.getQuestion().getQuestionTitle(), 1);
                                    }
                                }
                            }
                        } else {
                            continue;
                        }

                    }
                }
            }

            return map;
        } catch (OrganisationNotFoundException ex) {
            throw new OrganisationNotFoundException(ex.getMessage());
        }

    }

    @Override
    public Pair<List<String>, List<Double>> getTraumaPercentage(List<String> organisationNames, String ageRange, List<String> race, List<String> gender) throws OrganisationNotFoundException {
        try {

            List<AssessmentEntity> allAssessment = new ArrayList<>();
            List<ClientEntity> allClients = new ArrayList<>();

            // get all organisation
            for (String orgName : organisationNames) {
                OrganisationEntity org = organisationSessionBean.retrieveOrganisationByName(orgName);
                List<ClientEntity> clients = new ArrayList<>();

                for (ClientEntity client : clientSessionBean.retrieveAllClientsInOrg(org.getOrganisationId())) {
                    if (race.contains(client.getEthnicity()) && gender.contains(client.getGender())) {
                        allClients.add(client);
                        clients.add(client);
                    }
                }

                for (int i = 0; i < clients.size(); i++) {
                    //for trauma only the 1st assessment is needed
                    if (!clients.get(i).getAssessment().isEmpty()) {
                        allAssessment.add(clients.get(i).getAssessment().get(0));
                    }
                }

            }

            String[] ageGroupArr = ageRange.split("-");
            int lower = Integer.parseInt(ageGroupArr[0].trim());
            int upper = Integer.parseInt(ageGroupArr[1].trim());

            //set up initial counter in hashmap
            HashMap<String, Integer> map = new HashMap<>();
            map.put("0", 0);
            map.put("1", 0);
            map.put("2", 0);
            map.put("3", 0);
            map.put("4+", 0);

            for (int i = 0; i < allAssessment.size(); i++) {
                AssessmentEntity currentAss = allAssessment.get(i); //belong to 1 client
                if (isWithinAgeRange(currentAss, lower, upper)) {
                    List<ResponseEntity> allRes = currentAss.getResponse();
                    int count = 0;
                    for (int l = 0; l < allRes.size(); l++) {
                        ResponseEntity currRes = allRes.get(l);
                        //checking domain of res
                        if (currRes.getQuestion() instanceof SubQuestionEntity) {
                            if (((SubQuestionEntity) currRes.getQuestion()).getSubmodule().getSubModuleName().equalsIgnoreCase("Trauma (TR) Module")
                                    && !(currRes.getQuestion().getQuestionCode().equals("TR4a") || currRes.getQuestion().getQuestionCode().equals("TR4b")
                                    || currRes.getQuestion().getQuestionCode().equals("TR4c"))) {
                                if (currRes.getResponseValue() == 2 || currRes.getResponseValue() == 3) {
                                    count++;
                                    System.out.println(count);
                                }
                            }
                        }
                    }

                    if (count >= 4) { // 4+ includes 4
                        int prevValue = map.get("4+");
                        map.put("4+", ++prevValue);
                    } else {
                        int prevValue = map.get(Integer.toString(count));
                        map.put(Integer.toString(count), ++prevValue);
                    }

                }
            }

            List<String> labels = new ArrayList<>();
            List<Double> data = new ArrayList<>();

            map.entrySet()
                    .forEach(p -> {
                        String key = (String) p.getKey();
                        int responses = p.getValue();

                        System.out.println("key: " + key);
                        System.out.println("responses: " + responses);

                        double percentage = ((responses * 1.0) / allClients.size()) * 100;

                        labels.add(key);
                        data.add(allClients.isEmpty() ? 0 : percentage);
                    }
                    );

            return new Pair<>(labels, data);

        } catch (OrganisationNotFoundException ex) {
            throw new OrganisationNotFoundException(ex.getMessage());
        }
    }

    @Override
    public HashMap<String, List<Integer>> getDistributionOfLocByOrganisations(List<String> organisations, Date start, Date end, String ageRange, List<String> assessmentResons) throws OrganisationNotFoundException {
        HashMap<String, List<Integer>> map = new HashMap<>();
        List<OrganisationEntity> allOrg = new ArrayList<>();
        for (String org : organisations) {
            try {
                allOrg.add(organisationSessionBean.retrieveOrganisationByName(org));
            } catch (OrganisationNotFoundException ex) {
                throw new OrganisationNotFoundException(ex.getMessage());
            }
        }

        //set the backbone structure
        for (OrganisationEntity org : allOrg) {
            List<Integer> setCountList = new ArrayList<>();
            setCountList.add(0);
            setCountList.add(0);
            setCountList.add(0);
            map.put(org.getName(), setCountList);
        }

        String[] ageGroupArr = ageRange.split("-");
        int lower = Integer.parseInt(ageGroupArr[0].trim());
        int upper = Integer.parseInt(ageGroupArr[1].trim());

        //get assessments
        for (OrganisationEntity org : allOrg) {
            List<Integer> values = map.get(org.getName());
            List<ClientEntity> clients = clientSessionBean.retrieveAllClientsInOrg(org.getOrganisationId());
            for (ClientEntity client : clients) {
                //get all assessments by client which fits the date range
                List<AssessmentEntity> allAss = assessmentSessionBean.retrieveAssessmentByClientWithinDate(client.getClientUniqueId(), start, end);
                if (allAss.isEmpty()) {
                    continue;
                }
                //get latest assessment from all in the date range
                AssessmentEntity requiredAss = allAss.get(allAss.size() - 1);
                // check tat the the age range fits and that the reason for assessment fits
                if (isWithinAgeRange(requiredAss, lower, upper) && assessmentResons.contains(requiredAss.getReason().toString())) {
                    int loc = requiredAss.getLoc();
                    if (loc == -1) {
                        continue;

                    }
                    int prevValue = values.get(loc - 1);
                    values.set(loc - 1, ++prevValue);

                }

            }
            map.replace(org.getName(), values);

        }
        return map;
    }

    @Override
    public List<String> getOrgFromAgencyType(String type) {
        List<OrganisationEntity> allOrg = organisationSessionBean.retrieveAllOrganisationByType(type);

        List<String> orgNames = new ArrayList<>();
        for (OrganisationEntity org : allOrg) {
            orgNames.add(org.getName());
        }
        return orgNames;
    }

    @Override
    public Pair<List<String>, List<Long>> getCountByRace() {
        List<String> allRace = clientSessionBean.retrieveUniqueEthnicityOfClients();
        List<Long> counts = new ArrayList<>();
        for (String race : allRace) {
            List<ClientEntity> clients = clientSessionBean.retrieveClientsByEnthnicity(race);
            counts.add(new Long(clients.size()));
        }

        Pair<List<String>, List<Long>> results = new Pair(allRace, counts);
        return results;
    }

    // for assesssor system
    @Override
    public Pair<List<String>, List<Long>> getCountByRace(OrganisationEntity org) {
        List<String> allRace = clientSessionBean.retrieveUniqueEthnicityOfClients();
        List<Long> counts = new ArrayList<>();
        for (String race : allRace) {
            List<ClientEntity> clients = clientSessionBean.retrieveClientsByEnthnicityInOrg(org.getOrganisationId(), race);
            counts.add(new Long(clients.size()));
        }

        Pair<List<String>, List<Long>> results = new Pair(allRace, counts);
        return results;
    }

    @Override
    public Pair<List<String>, List<Long>> getCountByGender() {
        List<String> allGender = clientSessionBean.retrieveUniqueGenderOfClients();
        List<Long> counts = new ArrayList<>();
        for (String gender : allGender) {
            List<ClientEntity> clients = clientSessionBean.retrieveClientsByGender(gender);
            counts.add(new Long(clients.size()));
        }

        Pair<List<String>, List<Long>> results = new Pair(allGender, counts);
        return results;
    }

    //for assessor system
    @Override
    public Pair<List<String>, List<Long>> getCountByGender(OrganisationEntity org) {
        List<String> allGender = clientSessionBean.retrieveUniqueGenderOfClients();
        List<Long> counts = new ArrayList<>();
        for (String gender : allGender) {
            List<ClientEntity> clients = clientSessionBean.retrieveClientsByGender(org.getOrganisationId(), gender);
            counts.add(new Long(clients.size()));
        }

        Pair<List<String>, List<Long>> results = new Pair(allGender, counts);
        return results;
    }

    private boolean isWithinAgeRange(AssessmentEntity currentAss, int lower, int upper) {
        return (currentAss.getClient().getAge() >= lower && currentAss.getClient().getAge() <= upper);
    }

    @Override
    public Pair<List<String>, List<Long>> getRoles(Long orgId) {
        List<String> roles = new ArrayList<>();
        roles.add("Admin");
        roles.add("Case Worker");
        roles.add("Supervisor");

        List<AssessorEntity> assessor = organisationSessionBean.retrieveRootFromOrg(orgId);
        List<Long> counts = new ArrayList<>();
        counts.add(new Long(assessor.size()));
        assessor = organisationSessionBean.retrieveCaseworkerFromOrg(orgId);
        counts.add(new Long(assessor.size()));
        assessor = organisationSessionBean.retrieveSupervisorFromOrg(orgId);
        counts.add(new Long(assessor.size()));

        Pair<List<String>, List<Long>> results = new Pair(roles, counts);
        return results;

    }

    @Override
    public Pair<List<Integer>, List<Double>> getScores(Long assId) {
        List<CertificationEntity> certs = certificationSessionBean.retrieveAllCertificatebyAssessorId(assId);
        List<Integer> time = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        for (CertificationEntity cert : certs) {
            time.add(cert.getNoOfTimesRecertified());
            scores.add(cert.getRecentScore());
        }

        Pair<List<Integer>, List<Double>> results = new Pair(time, scores);
        return results;
    }

    @Override
    public Pair<List<String>, List<Double>> getRectifiedTimes(Long orgId) {
        List<AssessorEntity> assessorsInOrg = assessorSessionBean.retrieveAllAssessorsByOrganisation(orgId);
        List<String> assessorName = new ArrayList<>();
        List<Double> recertified = new ArrayList<>();
        for (AssessorEntity ass : assessorsInOrg) {
            assessorName.add(ass.getName());
            recertified.add(new Double(ass.getCertificates().isEmpty() ? 0: ass.getCertificates().size() - 1));
        }

        Pair<List<String>, List<Double>> results = new Pair(assessorName, recertified);
        return results;
    }

    @Override
    public Pair<List<String>, List<CertificationEntity>> getAssessorExpired(Long orgId) {
        List<AssessorEntity> assessorsInOrg = assessorSessionBean.retrieveAllAssessorsByOrganisation(orgId);
        List<String> assessorName = new ArrayList<>();
        List<CertificationEntity> certificates = new ArrayList<>();
        for (AssessorEntity ass : assessorsInOrg) {
            List<CertificationEntity> certs = ass.getCertificates();
            int size = certs.size();
            //no certs obtained yet
            if (size == 0) {
                assessorName.add(ass.getName());
                certificates.add(null);
                continue;
            }
            CertificationEntity certOfInterest = certs.get(0);
            Date dateOfCert = certOfInterest.getRawDateOfCert();
            Calendar cal = Calendar.getInstance();         
            cal.setTime(dateOfCert);     
            for (int i = 1; i < certs.size(); i++) {
                if (cal.getTime().before(certs.get(i).getRawDateOfCert())) {
                    cal.setTime(certs.get(i).getRawDateOfCert());
                    certOfInterest = certs.get(i);
                }
            }
            cal.add(Calendar.YEAR, 2);
            Date expiry = cal.getTime();
            if (expiry.before(new Date())) {
                assessorName.add(ass.getName());
                certificates.add(certOfInterest);
            }
        }
        Pair<List<String>, List<CertificationEntity>> results = new Pair(assessorName, certificates);
        return results;

    }

    @Override
    public Pair<List<String>, List<CertificationEntity>> getAssessorExpiredSoon(Long orgId) {
        List<AssessorEntity> assessorsInOrg = assessorSessionBean.retrieveAllAssessorsByOrganisation(orgId);
        List<String> assessorName = new ArrayList<>();
        List<CertificationEntity> certificates = new ArrayList<>();
        for (AssessorEntity ass : assessorsInOrg) {
            List<CertificationEntity> certs = ass.getCertificates();
            int size = certs.size();
            //no certs obtained yet
            if (size == 0) {
                continue;
            }

            Date dateOfCert = certs.get(size - 1).getRawDateOfCert();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateOfCert);
            cal.add(Calendar.YEAR, 2);
            Date expiry = cal.getTime();

            Date currentDate = new Date();
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(currentDate);

            // 3 month to current day
            cal2.add(Calendar.MONTH, 6);

            Date monthBefore = cal2.getTime();

            // check if expiry is after the current date and expiring soon in 1 month time
            if (expiry.after(new Date()) && expiry.before(monthBefore)) {
                assessorName.add(ass.getName());
                certificates.add(certs.get(size - 1));
            }
        }
        Pair<List<String>, List<CertificationEntity>> results = new Pair(assessorName, certificates);
        return results;

    }

}

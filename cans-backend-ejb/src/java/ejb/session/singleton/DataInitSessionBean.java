/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AdminUserSessionBeanLocal;
import ejb.session.stateless.AgeGroupSessionBeanLocal;
import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.DomainSessionBeanLocal;
import ejb.session.stateless.OrganisationSessionBeanLocal;
import ejb.session.stateless.QuestionsSessionBeanLocal;
import ejb.session.stateless.SubModuleSessionBeanLocal;
import entity.AdminUserEntity;
import entity.AgeGroupEntity;
import entity.AssessorEntity;
import entity.DomainEntity;
import entity.MainQuestionEntity;
import entity.OrganisationEntity;
import entity.SubModuleEntity;
import entity.SubQuestionEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AgencyTypeEnum;
import util.exception.AdminUserExistsException;
import util.exception.AgeGroupExistsException;
import util.exception.AgeGroupNotFoundException;
import util.exception.AssessorExistsException;
import util.exception.DomainExistsException;
import util.exception.DomainNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.MainQuestionExistsException;
import util.exception.OrganisationExistsException;
import util.exception.QuestionNotFoundException;
import util.exception.QuestionTypeInaccurateException;
import util.exception.SubModuleExistsException;
import util.exception.SubModuleNotFoundException;
import util.exception.SubQuestionExistsException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 * ______ _ _ __ __ _ _ _ __ _____ _ _ _ ______ _ _ _____ _ _ | ____| | | | | |
 * \/ (_) (_) | | / _| / ____| (_) | | | | | ____| (_) | | __ \ | | | | | |__
 * ___ _ __ | |_| |__ ___ | \ / |_ _ __ _ ___| |_ _ __ _ _ ___ | |_ | (___ ___
 * ___ _ __ _| | __ _ _ __ __| | | |__ __ _ _ __ ___ _| |_ _ | | | | _____
 * _____| | ___ _ __ _ __ ___ ___ _ __ | |_ | __/ _ \| '__| | __| '_ \ / _ \ |
 * |\/| | | '_ \| / __| __| '__| | | | / _ \| _| \___ \ / _ \ / __| |/ _` | | /
 * _` | '_ \ / _` | | __/ _` | '_ ` _ \| | | | | | | | | |/ _ \ \ / / _ \ |/ _
 * \| '_ \| '_ ` _ \ / _ \ '_ \| __| | | | (_) | | | |_| | | | __/ | | | | | | |
 * | \__ \ |_| | | |_| | | (_) | | ____) | (_) | (__| | (_| | | | (_| | | | |
 * (_| | | | | (_| | | | | | | | | |_| | | |__| | __/\ V / __/ | (_) | |_) | | |
 * | | | __/ | | | |_ |_| \___/|_| \__|_| |_|\___| |_| |_|_|_| |_|_|___/\__|_|
 * \__, | \___/|_| |_____/ \___/ \___|_|\__,_|_| \__,_|_| |_|\__,_| |_| \__,_|_|
 * |_| |_|_|_|\__, | |_____/ \___| \_/ \___|_|\___/| .__/|_| |_| |_|\___|_|
 * |_|\__| __/ | __/ | | | |___/ |___/ |_|
 */
/**
 *
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 * @author Wang Ziyue
 */
@Startup
@Singleton
@LocalBean
public class DataInitSessionBean {

    @EJB
    private OrganisationSessionBeanLocal organisationSessionBean;
    @EJB
    private AssessorSessionBeanLocal assessorSessionBean;
    @EJB
    private SubModuleSessionBeanLocal subModuleSessionBean;
    @EJB
    private QuestionsSessionBeanLocal questionsSessionBean;
    @EJB
    private AdminUserSessionBeanLocal adminSessionBean;
    @EJB
    private DomainSessionBeanLocal domainSessionBean;
    @EJB
    private AgeGroupSessionBeanLocal ageGroupSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        if (em.find(AdminUserEntity.class, 1l) == null) {
            dataInitialise();
        }
    }

    private void dataInitialise() {

        try {
            System.out.println("********** Begin Data Initialization **********");

            // ===== Create Child Development Domain =============
            DomainEntity cdd = new DomainEntity("Child Development", null, false, 1, false);
            AgeGroupEntity cddadol = new AgeGroupEntity("0-6");
            List<MainQuestionEntity> ques = new ArrayList<>();

            //create 1.1
            MainQuestionEntity q1_1 = new MainQuestionEntity("1.1", "Child Skills");
            List<String> desc = new ArrayList<>();
            desc.add("This item describes the child's development and highlights any developmental concerns/delays");
            desc.add("When rating this item, consider if the child is meeting his/her developmental milestones that are typical of "
                    + "his/her age in the following areas:");
            desc.add(" Play Engagement: Child is engaged/interested in play and able to sustain play.");
            desc.add("Communication: Child is able to understand and speak; the communication may be verbal or non‐"
                    + "verbal and may be in English or other languages.");
            desc.add("Motor: Child has control over large‐muscle activities (e.g., moving one’s arms, sitting, crawling and"
                    + "walking) and finely tuned movements(e.g., grasping a toy or picking up small objectssuch as a raisin).");
            desc.add("Problem Solving: Child is able to play with toys and solve problems; child has no issues with his/her"
                    + "cognitive development.");
            desc.add("Personal­Social: Child exhibits age‐appropriate self‐help skills and interaction with others.");

            List<String> quesToConsider1_1 = new ArrayList<>();
            quesToConsider1_1.add(" Is the child exhibiting behaviours that are typical of children in the same age range?");

            Map<Integer, String> rating1_1 = new HashMap<>();
            rating1_1.put(0, "No concern or delay in development");
            rating1_1.put(1, "There are some concerns about child’s development, but child is meeting developmental milestones.");
            rating1_1.put(2, "Child is not meeting the average developmental milestones and needs further support/intervention.");
            rating1_1.put(3, "Child has lost a skill he/she once had and needs further support/intervention.");

            q1_1.setQuestionDescription(desc);
            q1_1.setQuestionToConsider(quesToConsider1_1);
            q1_1.setRatingsDefinition(rating1_1);

            ques.add(q1_1);

            //create 1.2
            MainQuestionEntity q1_2 = new MainQuestionEntity("1.2", "Social Emotional");
            List<String> desc1_2 = new ArrayList<>();
            desc1_2.add("This item rates the child’s social functioning within/outside the family.");
            desc1_2.add(" When rating this item, consider the child’s behaviours in the following 7 areas: ");
            desc1_2.add("1. Self­regulation: child can calm/settle down or adjust to physiological/environmental conditions/stimulation"
                    + "2. Compliance: child conforms to directions from others and follows rules"
                    + "3. Adaptive functioning: child copes with physiological needs such as sleeping, eating, elimination"
                    + "4. Autonomy: child initiates or responds without guidance (i.e. moving towards independence)"
                    + "5. Affect: child demonstrates feelings and empathy for others"
                    + "6. Social­communication: child interacts with others by responding to or initiating signals to indicate"
                    + "interests, needs, and feelings"
                    + "7. Interaction with people: child engages with, responds to or initiates social responses to parents, other adults and peers");
            desc1_2.add("Remember to consider the child’s development/age when rating this item.");
            desc1_2.add("For children under the age of 2 years, please use the corrected age for prematurity.");

            List<String> quesToConsider1_2 = new ArrayList<>();
            quesToConsider1_2.add("How well does the child get along with others?");
            quesToConsider1_2.add("Does the child show age-appropriate social behaviours?");
            quesToConsider1_2.add("Does the child respond to adults or peers appropriately?");

            Map<Integer, String> rating1_2 = new HashMap<>();
            rating1_2.put(0, "No evidence of problems in social functioning."
                    + "OR"
                    + "Child scored ‘white’ on the ASQ:SE‐2, with no concerns about the child’s"
                    + "development in this domain.");
            rating1_2.put(1, "Child is having some minor problems in social relationships. Infants may"
                    + "be slow to respond to adults. Toddlers may need support to interact with"
                    + "peers, and pre‐schoolers may resist social situations."
                    + "OR"
                    + "Child scored ‘white’ on the ASQ:SE‐2, but caregivers/professionals are"
                    + "concerned about child’s development in this domain.");
            rating1_2.put(2, "Child is having some moderate problems with his/her social"
                    + "relationships. Infants may be unresponsive to adults and unaware of"
                    + "other infants. Toddlers may be aggressive and resist parallel play. Pre‐"
                    + "schoolers may argue excessively with adults and peers, or lack ability to"
                    + "play in groups even with adult support."
                    + "OR"
                    + "Child scored ‘grey’ on the ASQ:SE‐2 and needs further"
                    + "support/intervention.");
            rating1_2.put(3, "Child is experiencing severe disruptions in his/her social relationships."
                    + "Infants may show no ability to interact in a meaningful manner. Toddlers"
                    + "may be excessively withdrawn and unable to relate to familiar adults."
                    + "Pre‐schoolers may show no joy or sustained interaction with peers or"
                    + "adults, and/or aggression that may be putting others at risk."
                    + "OR"
                    + "Child scored ‘black’ on the ASQ:SE‐2 and needs further"
                    + "support/intervention.");

            q1_2.setQuestionDescription(desc1_2);
            q1_2.setQuestionToConsider(quesToConsider1_2);
            q1_2.setRatingsDefinition(rating1_2);

            ques.add(q1_2);

            //create 1.3
            MainQuestionEntity q1_3 = new MainQuestionEntity("1.3", "Caregiver/Child Interaction");
            List<String> desc1_3 = new ArrayList<>();
            desc1_3.add("This item rates how the caregiver and child respond to each other,"
                    + "and the relationship between them.");
            desc1_3.add("It assesses whether the caregiver and child have a healthy relationship, as demonstrated by good communication and care.");
            desc1_3.add("Pre‐school teachers/educarers will not be considered caregivers when rating this item.");
            desc1_3.add("Unhealthy communication will be demonstrated by a failure to communicate consistently, difficulty with"
                    + "affection or attention in the relationship, or, in the extreme, neglect and/or abuse");
            desc1_3.add("Note:"
                    + "Serve‐and‐return interactions are crucial for the development of children aged up to 2 years. The caregiver"
                    + "and child take turns to respond to each other several times on a topic, and responses can be verbal or non‐"
                    + "verbal. Scaffolding or supporting a child’s development involves an awareness that the adult can expand a"
                    + "child’s learning to a level the child would otherwise be unable to reach on his or her own");

            List<String> quesToConsider1_3 = new ArrayList<>();
            quesToConsider1_3.add("How does the caregiver react to the child?");
            quesToConsider1_3.add("How does the child react to the caregiver?");

            Map<Integer, String> rating1_3 = new HashMap<>();
            rating1_3.put(0, "There is no evidence of problems in the caregiver/child interaction.");
            rating1_3.put(1, "There is either a history of problems or suboptimal functioning in caregiver/child interaction. "
                    + "There may be inconsistent interactions or indications that interaction is not optimal, "
                    + "but this has not yet resulted in problems.");
            rating1_3.put(2, "The caregiver and child interact in a way that is problematic, and this has led "
                    + "to interference with the child’s growth and development.");
            rating1_3.put(3, "The caregiver and child are having significant problems in communication "
                    + "that can be characterised as abusive or neglectful");

            q1_3.setQuestionDescription(desc1_3);
            q1_3.setQuestionToConsider(quesToConsider1_3);
            q1_3.setRatingsDefinition(rating1_3);

            ques.add(q1_3);

            //create 1.4
            MainQuestionEntity q1_4 = new MainQuestionEntity("1.4", "Access To Play");
            List<String> desc1_4 = new ArrayList<>();
            desc1_4.add("This item measures whether the child has access to play‐time and age‐appropriate play activities in the home setting.");
            desc1_4.add("Access to play in the pre‐school setting can be captured in other items (e.g., suitability of pre‐school/childcare).");

            List<String> quesToConsider1_4 = new ArrayList<>();
            quesToConsider1_4.add("Is the child given adequate play time?");
            quesToConsider1_4.add("Does the child have access to age-appropriate play activities/materials?");

            Map<Integer, String> rating1_4 = new HashMap<>();
            rating1_4.put(0, "No evidence that child has problems with access to play‐time and/or age‐appropriate play activities/materials.");
            rating1_4.put(1, "Child has access to some play‐time and/or age‐appropriate play "
                    + "activities/materials although some problems may exist. Some play "
                    + "activities may not be appropriate for the child’s age.");
            rating1_4.put(2, "Child has limited access to play‐time and/or age‐appropriate play activities/materials.");
            rating1_4.put(3, "Child has no access to play time and appropriate play activities/materials;"
                    + "OR"
                    + "Child has access to inappropriate play activities/materials.");

            q1_4.setQuestionDescription(desc1_4);
            q1_4.setQuestionToConsider(quesToConsider1_4);
            q1_4.setRatingsDefinition(rating1_4);

            ques.add(q1_4);

            //create 1.4
            MainQuestionEntity q1_5 = new MainQuestionEntity("1.5", "Quality of Play");
            List<String> desc1_5 = new ArrayList<>();
            desc1_5.add("This item rates the quality of play the child has access to in the home setting.");
            desc1_5.add("The caregiver plays an important role in providing quality play to the child. Age‐appropriate play "
                    + "activities include the involvement of caregivers (e.g., caregivers preparing the play environment and "
                    + "providing specific play materials; caregivers playing with the child).");
            desc1_5.add("Quality of play in the pre‐school setting can be captured in other items (e.g., suitability of pre‐school/childcare).");

            List<String> quesToConsider1_5 = new ArrayList<>();
            quesToConsider1_5.add("Is the child/s parent or caregiver typically actively involed in play?");
            quesToConsider1_5.add("Is there positive adult-child interaction involved in the child's play?");

            Map<Integer, String> rating1_5 = new HashMap<>();
            rating1_5.put(0, "Caregiver consistently guides the child and provides interactive play, showing encouragement, affirmation, responsiveness and teaching. ");
            rating1_5.put(1, "Caregiver shows some effort to engage in positive interaction with the"
                    + "child in the areas of encouragement, affirmation, responsiveness and"
                    + "teaching. ");
            rating1_5.put(2, "Caregiver lets the child play on his/her own most of the time, with"
                    + "occasional attention and interactions.");
            rating1_5.put(3, "Caregiver does not supervise the child in play. The child is left alone all"
                    + "the time.");

            q1_5.setQuestionDescription(desc1_5);
            q1_5.setQuestionToConsider(quesToConsider1_5);
            q1_5.setRatingsDefinition(rating1_5);

            ques.add(q1_5);

            //create 1.6
            MainQuestionEntity q1_6 = new MainQuestionEntity("1.6", "Exposure To Screen Time");
            List<String> desc1_6 = new ArrayList<>();
            desc1_6.add("This item rates the child’s exposure to screen time.");
            desc1_6.add("Screen time is the amount of time spent on “screen devices” like video games, phones, tablets, computers, televisions and other electronic devices used for viewing or entertainment");
            desc1_6.add("Too much screen time can have physical, developmental and safety risks.");
            desc1_6.add("Limiting screen time appropriately can ensure that it does not get in the way of healthy sleep and other "
                    + "activities that are good for the child’s development. These activities include physical play, reading,"
                    + "drawing, interactions with caregivers and social time with friends and family.");
            desc1_6.add("Child development expertsrecommend limiting children’s daily screen time and ensuring adequate adult"
                    + "supervision when child has access to screens.");
            desc1_6.add("Supervised screen time is when the caregiver is present with the child the majority of the time and"
                    + "actively helps the child understand what he/she is seeing and applies it to the child’s learning and"
                    + "development.");
            desc1_6.add("Examples of unsupervised screen time include a child playing while the television is switched on in the"
                    + "background, or a caregiver who sits passively beside the child while the child views the medium.");

            List<String> quesToConsider1_6 = new ArrayList<>();
            quesToConsider1_6.add("Is the child's use of screen time appropriate for his/her age?");
            quesToConsider1_6.add("Is the child's use of screens supervised by an adult?");

            Map<Integer, String> rating1_6 = new HashMap<>();
            rating1_6.put(0, "Child is below 18 months old, and has no exposure to screens, apart from video‐chatting;"
                    + "OR"
                    + "Child is 18 months to 5 years old, and has supervised screen time;"
                    + "OR"
                    + "Child is 6 years old, and has consistent limits on screen time that are"
                    + "age‐appropriate.");
            rating1_6.put(1, "Child is 18 months to 5 years old, and has unsupervised access to"
                    + "screens for 1 hour or less a day.");
            rating1_6.put(2, "Child is below 18 months old, and has supervised/unsupervised"
                    + "access to screens;"
                    + "OR"
                    + "Child is 18 months to 5 years old, and has unsupervised access to"
                    + "screens for more than 1 hour a day;"
                    + "OR"
                    + "Child is 6 years old, and does not have consistent limits on screen time"
                    + "that are age‐appropriate");
            rating1_6.put(3, "Child is 18 months to 6 years old, and access to screen time is affecting"
                    + "his/her daily functioning (e.g. unable to sleep or eat without screen"
                    + "time, unable to stop screen time during transition to other activities).");

            q1_6.setQuestionDescription(desc1_6);
            q1_6.setQuestionToConsider(quesToConsider1_6);
            q1_6.setRatingsDefinition(rating1_6);

            ques.add(q1_6);

            //create 1.7
            MainQuestionEntity q1_7 = new MainQuestionEntity("1.7", "Exposure To Screen Time");
            List<String> desc1_7 = new ArrayList<>();
            desc1_7.add("This item rates the child’s exposure to screen time.");
            desc1_7.add("Screen time is the amount of time spent on “screen devices” like video games, phones, tablets, computers, televisions and other electronic devices used for viewing or entertainment");
            desc1_7.add("Too much screen time can have physical, developmental and safety risks.");
            desc1_7.add("Limiting screen time appropriately can ensure that it does not get in the way of healthy sleep and other "
                    + "activities that are good for the child’s development. These activities include physical play, reading,"
                    + "drawing, interactions with caregivers and social time with friends and family.");
            desc1_7.add("Child development expertsrecommend limiting children’s daily screen time and ensuring adequate adult"
                    + "supervision when child has access to screens.");
            desc1_7.add("Supervised screen time is when the caregiver is present with the child the majority of the time and"
                    + "actively helps the child understand what he/she is seeing and applies it to the child’s learning and"
                    + "development.");
            desc1_7.add("Examples of unsupervised screen time include a child playing while the television is switched on in the"
                    + "background, or a caregiver who sits passively beside the child while the child views the medium.");

            List<String> quesToConsider1_7 = new ArrayList<>();
            quesToConsider1_7.add("Is the child's use of screen time appropriate for his/her age?");
            quesToConsider1_7.add("Is the child's use of screens supervised by an adult?");

            Map<Integer, String> rating1_7 = new HashMap<>();
            rating1_7.put(0, "Child is below 18 months old, and has no exposure to screens, apart from video‐chatting;"
                    + "OR"
                    + "Child is 18 months to 5 years old, and has supervised screen time;"
                    + "OR"
                    + "Child is 6 years old, and has consistent limits on screen time that are"
                    + "age‐appropriate.");
            rating1_7.put(1, "Child is 18 months to 5 years old, and has unsupervised access to"
                    + "screens for 1 hour or less a day.");
            rating1_7.put(2, "Child is below 18 months old, and has supervised/unsupervised"
                    + "access to screens;"
                    + "OR"
                    + "Child is 18 months to 5 years old, and has unsupervised access to"
                    + "screens for more than 1 hour a day;"
                    + "OR"
                    + "Child is 6 years old, and does not have consistent limits on screen time"
                    + "that are age‐appropriate");
            rating1_7.put(3, "Child is 18 months to 6 years old, and access to screen time is affecting"
                    + "his/her daily functioning (e.g. unable to sleep or eat without screen"
                    + "time, unable to stop screen time during transition to other activities).");

            q1_7.setQuestionDescription(desc1_7);
            q1_7.setQuestionToConsider(quesToConsider1_7);
            q1_7.setRatingsDefinition(rating1_7);

            ques.add(q1_7);

            DomainEntity domain1 = domainSessionBean.createNewDomain(cdd);
            AgeGroupEntity age1 = ageGroupSessionBean.createNewAgeGroupForDomain(cddadol, domain1.getDomainId());
            for (MainQuestionEntity questions : ques) {
                questionsSessionBean.createMainQuestionForAgeGroup(age1.getAgeGroupId(), questions);
            }

            // ====================== Create Life Functioning Domain ===================
            DomainEntity lfd = new DomainEntity("Life Functioning", null, false, 1, false);
            AgeGroupEntity lfdall = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques2 = new ArrayList<>();

            //create 2.1
            MainQuestionEntity q2_1 = new MainQuestionEntity("2.1", "Relationship with Family Members");
            List<String> desc2_1 = new ArrayList<>();
            desc2_1.add("This item rates how the child is functioning within his/her family.");
            desc2_1.add("The definition of family usually includes biological or adoptive relatives and significant others with whom"
                    + "the child has contact with.");
            desc2_1.add("If the child has not been living with his/her birth family for a long time/since birth, rate according to the"
                    + "current caregiver/family whom the child has been living with for 3 months or longer.");
            desc2_1.add("However, if the child is in residential care, DO NOT rate the relationships with the staff of the residential"
                    + "home for this item. Instead, rate the caregiver/family whom the child has been living with before"
                    + "admission into the home or the caregiver/family whom the child will be reintegrated back into.");
            desc2_1.add("If the child had multiple changes of caregivers within the last 3 months and is able to describe his/her"
                    + "family, the definition of family should come from the child’s perspective.");

            List<String> quesToConsider2_1 = new ArrayList<>();
            quesToConsider2_1.add("How is the relationship between the child and his/her family members?");
            quesToConsider2_1.add("Are there any problems"
                    + "between family"
                    + "members? Has there"
                    + "ever been any violence"
                    + "at home?"
                    + "➢ If so, is the child’s"
                    + "functioning affected"
                    + "as a result?");

            Map<Integer, String> rating2_1 = new HashMap<>();
            rating2_1.put(0, "Child has no issues in his/her relationships with family members.");
            rating2_1.put(1, "Child has history, suspicion of or is facing mild problems in"
                    + "his/her relationships with parents, siblings and/or other family"
                    + "members, but child’s functioning is not affected. ");
            rating2_1.put(2, "Child is facing moderate problems in his/her relationships with"
                    + "parents, siblings and/or other family members."
                    + "For example, there may be frequent arguing and/or difficulties"
                    + "maintaining positive relationships.");
            rating2_1.put(3, "Child is facing severe problems in his/her relationships with"
                    + "parents, siblings, and/or other family members."
                    + "For example, there may be problems of domestic violence or"
                    + "absence of any positive relationships.");

            q2_1.setQuestionDescription(desc2_1);
            q2_1.setQuestionToConsider(quesToConsider2_1);
            q2_1.setRatingsDefinition(rating2_1);

            ques2.add(q2_1);

            //create 2.2
            MainQuestionEntity q2_2 = new MainQuestionEntity("2.2", "Relationship Permanenece");
            List<String> desc2_2 = new ArrayList<>();
            desc2_2.add("This item refers to the stability of significant relationships and care"
                    + "arrangements in the child's life, as frequent changes will affect the child’s development. The quality of"
                    + "the relationship should NOT be considered here.");
            desc2_2.add("Take into consideration the child’s relationship to an adult who is seen as a parental figure (e.g.,"
                    + "relatives, natural/foster parents).");
            desc2_2.add("The child’s relationships and care arrangements may be considered beyond the 30‐day rating window"
                    + "if it is in the best interests of the child");

            List<String> quesToConsider2_2 = new ArrayList<>();
            quesToConsider2_2.add("Does the child have"
                    + "any relationships"
                    + "with adults that have"
                    + "lasted his/her"
                    + "lifetime?");
            quesToConsider2_2.add("Is he/she in contact"
                    + "with both parents?");

            Map<Integer, String> rating2_2 = new HashMap<>();
            rating2_2.put(0, "Child has very stable relationships. Family members, friends, and"
                    + "community have been stable for most of his/her life and are likely to"
                    + "remain so in the foreseeable future.");
            rating2_2.put(1, "Child has had stable relationships but there is some concern about"
                    + "instability in the coming year due to transitions, illness, or aging."
                    + "History of instability in caregiving relationships will be rated here.");
            rating2_2.put(2, "Child has had at least one stable relationship over his/her lifetime but"
                    + "has experienced other instability through factors such as divorce,"
                    + "moving, removal from home, mental illness or death");
            rating2_2.put(3, "Child does not have any stability in relationships. Permanency"
                    + "planning options must be considered");

            q2_2.setQuestionDescription(desc2_2);
            q2_2.setQuestionToConsider(quesToConsider2_2);
            q2_2.setRatingsDefinition(rating2_2);

            ques2.add(q2_2);

            //create 2.3
            MainQuestionEntity q2_3 = new MainQuestionEntity("2.3", "Living Situation");
            List<String> desc2_3 = new ArrayList<>();
            desc2_3.add("This refers to the child’s functioning within his/her current living arrangement"
                    + "(i.e. where he/she resides most of the time).");
            desc2_3.add("For children who return to their natural family on home leave for some days a week but reside in out‐of‐"
                    + "home care (e.g., foster care/children’s home) for the majority of the week, the client’s functioning in the"
                    + "out‐of‐home care setting should be rated for this item.");

            List<String> quesToConsider2_3 = new ArrayList<>();
            quesToConsider2_3.add("How is the child"
                    + "behaving and getting"
                    + "along with others in"
                    + "place of residence?");
            quesToConsider2_3.add("Is the child at risk of"
                    + "being removed from"
                    + "place of residence?");

            Map<Integer, String> rating2_3 = new HashMap<>();
            rating2_3.put(0, "No evidence of problems with functioning in current living situation");
            rating2_3.put(1, "Mild problems with functioning in current living situation."
                    + "For example, caregivers may be concerned about child’s behaviour at"
                    + "place of residence.");
            rating2_3.put(2, "Moderate problems with functioning in current living situation."
                    + "For example, child may have difficulties maintaining his/her behaviour"
                    + "in this setting, creating significant problems for others at place of"
                    + "residence.");
            rating2_3.put(3, "Severe problems with functioning in current living situation. Child is"
                    + "at immediate risk of being removed from place of residence due to"
                    + "his/her behaviour.");

            q2_3.setQuestionDescription(desc2_3);
            q2_3.setQuestionToConsider(quesToConsider2_3);
            q2_3.setRatingsDefinition(rating2_3);

            ques2.add(q2_3);

            //create 2.4
            MainQuestionEntity q2_4 = new MainQuestionEntity("2.4", "Attachment");
            List<String> desc2_4 = new ArrayList<>();
            desc2_4.add("This item looks at issues concerning child’s separation and attachment with others.");
            desc2_4.add("This item refers to the general (overall) attachment style of the child, which includes both family and"
                    + "non‐family members, and cannot be judged by looking at any one relationship.");
            desc2_4.add("Consider whether the child has developed a sense of security and trust in his/her relationships with\n"
                    + "significant others, and whether the child is able to form healthy relationships with others. ");
            desc2_4.add("The main attachment figure for children is the immediate family (caregivers/siblings). For youths,\n"
                    + "attachment may broaden to peers and other individuals.");
            desc2_4.add("Consider developmental appropriateness of the child’s attachment to others when rating the item.");

            List<String> quesToConsider2_4 = new ArrayList<>();
            quesToConsider2_4.add("Does the child approach or"
                    + "attach to strangers in"
                    + "indiscriminate ways (e.g."
                    + "child becomes"
                    + "physically/emotionally close"
                    + "with a stranger)?");
            quesToConsider2_4.add("Does the child have the"
                    + "ability to form healthy"
                    + "attachments to others? Are"
                    + "his/her relationships marked"
                    + "by intense fear or avoidance?");
            quesToConsider2_4.add("For younger children:"
                    + "➢ Does the child have"
                    + "difficulties with separation"
                    + "from the caregiver?  ");
            quesToConsider2_4.add("For older children/youth:"
                    + "➢ Does the youth seem"
                    + "clingy/anxious about his/her"
                    + "relationships with significant"
                    + "others (e.g., classmates,"
                    + "boy/girlfriend)?");

            Map<Integer, String> rating2_4 = new HashMap<>();
            rating2_4.put(0, "No evidence of problems with attachment.");
            rating2_4.put(1, "Mild problems with attachment. Child may have minor"
                    + "difficulties with appropriate physical/emotional boundaries"
                    + "with others and/or there is some evidence of insecurity in"
                    + "the child’s relationship with others.");
            rating2_4.put(2, "Moderate problems with attachment. Child may have"
                    + "ongoing difficulties with separation resulting in interference"
                    + "with development and/or may consistently avoid contact"
                    + "with caregivers. Child may have ongoing difficulties with"
                    + "physical/emotional boundaries with others. Attachment"
                    + "relationship is marked by sufficient difficulty as to require"
                    + "intervention.");
            rating2_4.put(3, "Severe problems with attachment. Child is unable to form"
                    + "attachment relationships with others and/or have ongoing"
                    + "difficulties with physical/emotional boundaries leading to"
                    + "indiscriminate attachment patterns, or withdrawn, inhibited"
                    + "attachment patterns. Child has experienced significant"
                    + "separation from or loss of caregiver and/or has experienced"
                    + "inadequate care from early caregivers which interfere with"
                    + "the formation of positive attachment relationships");

            q2_4.setQuestionDescription(desc2_4);
            q2_4.setQuestionToConsider(quesToConsider2_4);
            q2_4.setRatingsDefinition(rating2_4);

            ques2.add(q2_4);

            DomainEntity domain2 = domainSessionBean.createNewDomain(lfd);
            AgeGroupEntity age2 = ageGroupSessionBean.createNewAgeGroupForDomain(lfdall, domain2.getDomainId());
            for (MainQuestionEntity questions : ques2) {
                questionsSessionBean.createMainQuestionForAgeGroup(age2.getAgeGroupId(), questions);
            }

            AgeGroupEntity lfchild = new AgeGroupEntity("7+");
            List<MainQuestionEntity> ques2child = new ArrayList<>();

            //create 2.5
            MainQuestionEntity q2_5 = new MainQuestionEntity("2.5", "Developmental");
            List<String> desc2_5 = new ArrayList<>();
            desc2_5.add("This item looks at developmental impairments, which include, but not limited to,"
                    + "Mental Retardation, Intellectual Disabilities (ID) and Developmental Disabilities (e.g., Autism Spectrum"
                    + "Disorder).");
            desc2_5.add("When rating this item, please consider whether the child has needs in ANY of the following areas: ");
            desc2_5.add("Cognitive: Child has low IQ or mild/profound mental retardation.");
            desc2_5.add("Communication: Child has problems with receptive (what the child understands) and expressive"
                    + "(what the child can express) communication skills.");
            desc2_5.add("Social­Emotional Development: Child has impaired social interactions (e.g., fails to develop peer"
                    + "reaction to others), lacks emotional response to others (e.g., fails to express empathy) and/or"
                    + "displays repetitive/stereotypical patterns of behaviours or interest.");
            desc2_5.add("Daily Functioning Skills: Child has problems in daily living skills (e.g., eating, bathing, dressing,"
                    + "toileting) and relies on others for help more than his/her age group.");
            desc2_5.add("Other considerations:");
            desc2_5.add("All developmental disabilities occur on a continuum – consider the degree of impairment when rating.");
            desc2_5.add("Do not rate a child with dyslexia here – rate the item “Reading and Writing” instead.");
            desc2_5.add("Rate a child that is suspected of having developmental delays or is currently being assessed for"
                    + "developmental needs as a “1”.");

            List<String> quesToConsider2_5 = new ArrayList<>();
            quesToConsider2_5.add("Has the child developed"
                    + "like other children"
                    + "his/her age?");
            quesToConsider2_5.add("Does the child’s growth"
                    + "and development seem"
                    + "healthy?");
            quesToConsider2_5.add("Has the child been"
                    + "screened for any"
                    + "developmental"
                    + "problems?");

            Map<Integer, String> rating2_5 = new HashMap<>();
            rating2_5.put(0, "There is no evidence of developmental problems or low IQ.");
            rating2_5.put(1, "Child has mild developmental problems or may have low IQ.");
            rating2_5.put(2, "Child has moderate developmental problems or mild mental"
                    + "retardation.");
            rating2_5.put(3, "Child has severe developmental problems or moderate/profound"
                    + "mental retardation.");

            q2_5.setQuestionDescription(desc2_5);
            q2_5.setQuestionToConsider(quesToConsider2_5);
            q2_5.setRatingsDefinition(rating2_5);

            ques2child.add(q2_5);

            //create 2.6
            MainQuestionEntity q2_6 = new MainQuestionEntity("2.6", "Social Relationships");
            List<String> desc2_6 = new ArrayList<>();
            desc2_6.add("This item rates the child’s social functioning outside the family. This includes"
                    + "age‐appropriate behaviour and the ability to make and maintain relationships with both peers and"
                    + "adults");
            desc2_6.add("Rate this item on the child’s ability to form positive social relationships with peers and other adults (e.g.,"
                    + "teachers, counsellors).");
            desc2_6.add("Do not rate the child’s relationship with caregiver here.");
            desc2_6.add("If a child has friends but is having problems with them, those needs would be described here.");

            List<String> quesToConsider2_6 = new ArrayList<>();
            quesToConsider2_6.add("How well does the child"
                    + "get along with others?");
            quesToConsider2_6.add("Does he/she make new"
                    + "friends easily?");
            quesToConsider2_6.add("Has there been an"
                    + "increase in peer"
                    + "conflicts?");
            quesToConsider2_6.add("Does he/she have"
                    + "unhealthy friendships?");
            quesToConsider2_6.add("Does he/she keep"
                    + "friends for a long time"
                    + "or tend to change"
                    + "friends frequently?");

            Map<Integer, String> rating2_6 = new HashMap<>();
            rating2_6.put(0, "Child has positive social relationships. Child interacts"
                    + "appropriately with others, and builds and maintains relationships");
            rating2_6.put(1, "Child is having some minor problems with current social"
                    + "relationships. Child may have some difficulty interacting with"
                    + "others and building and/or maintaining relationships. ");
            rating2_6.put(2, "Child is having some moderate problems with his/her current"
                    + "social relationships. Child may have problems interacting with"
                    + "others, and building and maintaining relationships."
                    + "For example, child may argue frequently with adults and peers.  ");
            rating2_6.put(3, "Child is experiencing severe disruptions in his/her current social"
                    + "relationships. Child may have serious problems interacting with"
                    + "others, and building and maintaining relationships."
                    + "For example, child may be very withdrawn or aggressive with peers"
                    + "and adults, and have notable difficulties relating to others that"
                    + "interfere with the child’s functioning.");

            q2_6.setQuestionDescription(desc2_6);
            q2_6.setQuestionToConsider(quesToConsider2_6);
            q2_6.setRatingsDefinition(rating2_6);

            ques2child.add(q2_6);

            //create 2.7
            MainQuestionEntity q2_7 = new MainQuestionEntity("2.7", "Leisure/Play Activities");
            List<String> desc2_7 = new ArrayList<>();
            desc2_7.add("This item rates the degree to which a child uses leisure time or play activities"
                    + "in a positive manner");
            desc2_7.add("Satisfaction with leisure activities is associated with children’s behavioural and emotional well‐being.");
            desc2_7.add("Participation in leisure and play activities promotes the development of physical and social"
                    + "competencies.");

            List<String> quesToConsider2_7 = new ArrayList<>();
            quesToConsider2_7.add("Does the child have"
                    + "things that he/she likes"
                    + "to do in his/her free"
                    + "time?");
            quesToConsider2_7.add("Are the activities a"
                    + "positive use of his/her"
                    + "free time?");
            quesToConsider2_7.add("Does the child often"
                    + "claim to be bored or"
                    + "have nothing to do?");

            Map<Integer, String> rating2_7 = new HashMap<>();
            rating2_7.put(0, "Child has enjoyable positive leisure/play activities on an ongoing"
                    + "basis. Child makes full use of leisure time to pursue activities that"
                    + "support his/her healthy development and enjoyment.");
            rating2_7.put(1, "Child is doing adequately in terms of leisure/play activities"
                    + "although some problems may exist.");
            rating2_7.put(2, "Child is having moderate problems with leisure/play activities."
                    + "Child may experience problems with effective use of"
                    + "leisure/playtime."
                    + "For example, child may not have access to leisure activities,"
                    + "struggle to engage in activities without the direction of others, or"
                    + "uninterested in making use of leisure time.");
            rating2_7.put(3, "Child has no access to or interest in leisure/play activities. Child"
                    + "has significant difficulties making use of leisure/playtime.");

            q2_7.setQuestionDescription(desc2_7);
            q2_7.setQuestionToConsider(quesToConsider2_7);
            q2_7.setRatingsDefinition(rating2_7);

            ques2child.add(q2_7);

            //create 2.8
            MainQuestionEntity q2_8 = new MainQuestionEntity("2.8", "Sexual Development");
            List<String> desc2_8 = new ArrayList<>();
            desc2_8.add("This item looks at broad issues around sexual development.");
            desc2_8.add("When rating this item, please consider whether the child exhibits or experiences ANY of the following:");
            desc2_8.add("Developmentally inappropriate or problematic sexual behaviours.");
            desc2_8.add("Anxiety or distress over sexual identity issues (e.g., sexual orientation and/or gender identity).");
            desc2_8.add("Negative reactions from others as a result of sexual identity issues (e.g., sexual orientation and/or"
                    + "gender identity).");
            desc2_8.add("Other considerations: ");
            desc2_8.add("Do not rate this item if the child’s sexual development is normal, but his/her caregiver has a negative"
                    + "reaction to it. For example, if a 19‐year‐old youth engages in a normal, healthy relationship but his/her"
                    + "caregivers react negatively, rate this item as “0”");
            desc2_8.add("For sexual orientation and/or gender identity, take cultural norms into account such as: whether the"
                    + "child, the child’s family and people in the child’s immediate environment (e.g., the children’s home,"
                    + "school, etc.) are comfortable with the child’s sexual orientation and find it socially acceptable");
            desc2_8.add("The rating ought to take into account whether placement stability and/or child’s overall wellbeing is"
                    + "affected");

            List<String> quesToConsider2_8 = new ArrayList<>();
            quesToConsider2_8.add("Are there any"
                    + "concerns about"
                    + "the child's sexual"
                    + "development?");
            quesToConsider2_8.add("Is the child"
                    + "sexually active?");
            quesToConsider2_8.add("Does he/she"
                    + "have more"
                    + "interest in sex"
                    + "than other"
                    + "children his/her"
                    + "age?");

            Map<Integer, String> rating2_8 = new HashMap<>();
            rating2_8.put(0, "No evidence of any problems with sexual development; "
                    + "AND/OR "
                    + "No evidence of concerns about sexual identity issues.");
            rating2_8.put(1, "Mild problems with sexual development; "
                    + "AND/OR "
                    + "Child may have some concerns about sexual identity and/or anxiety "
                    + "about the reactions of others to sexual identity issues.");
            rating2_8.put(2, "Moderate problems with sexual development. "
                    + "For example, child may have multiple partners or have high‐risk sexual"
                    + "behaviours; "
                    + "AND/OR"
                    + "Child may have moderate anxiety or distress over sexual identity issues "
                    + "and/or negative reactions of others to sexual identity issues");
            rating2_8.put(3, "Severe problems with sexual development. "
                    + "For example, child may have engaged in under‐aged sex, prostitution, "
                    + "frequent risky sexual behaviour or be sexually aggressive against others;  "
                    + "AND/OR\n"
                    + "Child may have severe anxiety or distress over sexual identity issues "
                    + "and/or negative reactions of others to sexual identity issues, and it may "
                    + "interfere with child’s functioning at home, in school or with peers.");

            q2_8.setQuestionDescription(desc2_8);
            q2_8.setQuestionToConsider(quesToConsider2_8);
            q2_8.setRatingsDefinition(rating2_8);

            ques2child.add(q2_8);

            AgeGroupEntity age3 = ageGroupSessionBean.createNewAgeGroupForDomain(lfchild, domain2.getDomainId());
            for (MainQuestionEntity questions : ques2child) {
                questionsSessionBean.createMainQuestionForAgeGroup(age3.getAgeGroupId(), questions);
            }

            AgeGroupEntity lfteen = new AgeGroupEntity("14+");
            List<MainQuestionEntity> ques2teen = new ArrayList<>();

            //create 2.9
            MainQuestionEntity q2_9 = new MainQuestionEntity("2.9", "Independent Living");
            List<String> desc2_9 = new ArrayList<>();
            desc2_9.add("Consider how capable the youth will be at maintaining him/herself and his/her"
                    + "own home environment.");
            desc2_9.add("When rating this item, you may want to consider whether the youth has the following skills:");
            desc2_9.add("Budgeting/Managing finances");
            desc2_9.add("Ability to prepare a simple meal");
            desc2_9.add("Ability to do his/her own laundry");
            desc2_9.add("Basic housekeeping (e.g., mopping the floor, keeping the house tidy)");

            List<String> quesToConsider2_9 = new ArrayList<>();
            quesToConsider2_9.add("Does the youth know "
                    + "how to take care of "
                    + "personal needs?");
            quesToConsider2_9.add("Can the youth manage "
                    + "his/her personal "
                    + "finances, and settle "
                    + "his/her own meals?");
            quesToConsider2_9.add("Is he/she developing "
                    + "the skills to eventually "
                    + "live by him/herself?"
                    + "➢ If no, what skills "
                    + "does he/she need "
                    + "to develop?");

            Map<Integer, String> rating2_9 = new HashMap<>();
            rating2_9.put(0, "Youth is capable of independent living. Youth has sufficient"
                    + "independent living skills to live independently.");
            rating2_9.put(1, "Youth has mild impairment in independent living skills. Some"
                    + "problems exist in maintaining reasonable cleanliness, diet, finances,"
                    + "or time management, etc. Youth needs to learn additional"
                    + "independent living skills.");
            rating2_9.put(2, "Youth has moderate impairment in independent living skills."
                    + "Notable problems exist in maintaining reasonable cleanliness, diet, "
                    + "finances, time management, etc. Youth needs to be in a supervised "
                    + "living environment.");
            rating2_9.put(3, "Youth has severe impairment in independent living skills. Youth is "
                    + "unable to live independently, and will certainly need a structured "
                    + "and supervised living environment in young adulthood.");

            q2_9.setQuestionDescription(desc2_9);
            q2_9.setQuestionToConsider(quesToConsider2_9);
            q2_9.setRatingsDefinition(rating2_9);

            ques2teen.add(q2_9);

            AgeGroupEntity age4 = ageGroupSessionBean.createNewAgeGroupForDomain(lfteen, domain2.getDomainId());
            for (MainQuestionEntity questions : ques2teen) {
                questionsSessionBean.createMainQuestionForAgeGroup(age4.getAgeGroupId(), questions);
            }

            // ============== Create Medical & Physical Health Domain ================
            DomainEntity mph = new DomainEntity("Medical & Physical Health", null, false, 1, false);
            AgeGroupEntity mphall = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques3 = new ArrayList<>();

            //create 3.1
            MainQuestionEntity q3_1 = new MainQuestionEntity("3.1", "Physical/Medical");
            List<String> desc3_1 = new ArrayList<>();
            desc3_1.add("This item rates both health problems and chronic/acute physical conditions or "
                    + "impediments (e.g., blindness, deafness, motor difficulties).");
            desc3_1.add("If the child has a psychological condition (e.g., ADHD) or is taking psychotropic medication (e.g., anti‐\n"
                    + "depressants), do not rate this item unless the child is experiencing significant side‐effects due to the "
                    + "medication.");

            List<String> quesToConsider3_1 = new ArrayList<>();
            quesToConsider3_1.add("Is the child generally healthy?");
            quesToConsider3_1.add("Does he/she have any "
                    + "medical problems? "
                    + "➢ If yes, how much "
                    + "does this interfere "
                    + "with his/her life?");
            quesToConsider3_1.add("Does child have to see a "
                    + "doctor regularly to treat "
                    + "any problems (such as "
                    + "asthma, diabetes)?");

            Map<Integer, String> rating3_1 = new HashMap<>();
            rating3_1.put(0, "No evidence that the child has any medical or physical problems, "
                    + "and/or child is healthy");
            rating3_1.put(1, "Child has mild, transient or well­managed physical or medical "
                    + "problems. "
                    + "For example, a child aged below 12 years old with a missing "
                    + "health booklet would be rated here. ");
            rating3_1.put(2, "Child has any medical, physical problems or public health "
                    + "infection that requires medical treatment/intervention; "
                    + "OR "
                    + "Child has a chronic illness or a physical challenge that requires "
                    + "ongoing medical intervention");
            rating3_1.put(3, "Child has life­threatening illness or severe medical/physical "
                    + "condition. Immediate and/or intensive action should be taken "
                    + "due to imminent danger to child’s safety, health, and/or "
                    + "development.");

            q3_1.setQuestionDescription(desc3_1);
            q3_1.setQuestionToConsider(quesToConsider3_1);
            q3_1.setRatingsDefinition(rating3_1);

            ques3.add(q3_1);

            //create 3.2
            MainQuestionEntity q3_2 = new MainQuestionEntity("3.2", "Oral Health");
            List<String> desc3_2 = new ArrayList<>();
            desc3_2.add("This item rates the child’s dental health practices and risk of dental caries.");
            desc3_2.add("Oral health is important as it has a direct impact on overall physical health.");
            desc3_2.add("For younger children, it also affects the development of adult teeth.");
            desc3_2.add("nce an infant’s first teeth appear (around 6 months of age), it is important for caregivers to "
                    + "clean/brush his or her teeth twice a day, once in the morning and once at night before sleeping.");
            desc3_2.add("Night feeds or the use of pacifier with sweeteners (like honey or other sweetened drinks) can "
                    + "cause tooth decay in children.");

            List<String> quesToConsider3_2 = new ArrayList<>();
            quesToConsider3_2.add("Are the child’s teeth "
                    + "cleaned/brushed twice a "
                    + "day? ");
            quesToConsider3_2.add("Does the child feed on "
                    + "sweetened drinks or milk "
                    + "to sleep?  ");
            quesToConsider3_2.add("Does the child go for "
                    + "regular dental check "
                    + "ups?");

            Map<Integer, String> rating3_2 = new HashMap<>();
            rating3_2.put(0, "Child brushes his/her teeth regularly and/or visited a dentist in "
                    + "the past year with no dental issues; "
                    + "OR "
                    + "Child has no teeth yet.");
            rating3_2.put(1, "Child brushes his/her teeth regularly but has not visited a dentist "
                    + "in the past year.");
            rating3_2.put(2, "Child has poor dental hygiene or habits (e.g., not brushing teeth "
                    + "regularly, eating/feeding at night without brushing after "
                    + "eating/feeding).");
            rating3_2.put(3, "Child has obvious dental issues with no follow­up with a dentist.");

            q3_2.setQuestionDescription(desc3_2);
            q3_2.setQuestionToConsider(quesToConsider3_2);
            q3_2.setRatingsDefinition(rating3_2);

            ques3.add(q3_2);

            //create 3.3
            MainQuestionEntity q3_3 = new MainQuestionEntity("3.3", "Sleep");
            List<String> desc3_3 = new ArrayList<>();
            desc3_3.add("This item rates the child’s quality of sleep, which include any disruptions in sleep, problems with "
                    + "going to sleep, staying asleep, waking up early or sleeping too much.");
            desc3_3.add("Some examples of sleep problems include bed wetting, nightmares, sleep‐walking, night terrors, "
                    + "irregular sleep routines.");
            desc3_3.add("Sleep problems regardless of cause would be described here (e.g., sleep disorders or lifestyle "
                    + "choices/routines resulting in too little or too much sleep).");
            desc3_3.add("Remember to consider the child’s development/age when rating this item (see supplementary "
                    + "information).");

            List<String> quesToConsider3_3 = new ArrayList<>();
            quesToConsider3_3.add("How many hours, on "
                    + "average, does the child "
                    + "sleep each night?");
            quesToConsider3_3.add("Is this amount age "
                    + "appropriate?");
            quesToConsider3_3.add("Does he/she have any "
                    + "trouble falling asleep or "
                    + "staying asleep?");
            quesToConsider3_3.add("Any nightmares or "
                    + "bedwetting?");

            Map<Integer, String> rating3_3 = new HashMap<>();
            rating3_3.put(0, "No evidence of problems with sleep");
            rating3_3.put(1, "Child has some problems with sleep. "
                    + "Toddler resists sleep and consistently needs a great deal of "
                    + "support to sleep. Child may have either a history of poor sleep or "
                    + "continued problems, on average, once a week.");
            rating3_3.put(2, "Child is having problems with sleep. "
                    + "Child may experience difficulty falling asleep, night waking, night "
                    + "terrors or nightmares with frequency and intensity; sleep may be "
                    + "often disrupted and child seldom obtains a full night of sleep. "
                    + "Child may experience sleep problems, on average, more than "
                    + "once a week");
            rating3_3.put(3, "Child is experiencing significant sleep problems, and these result "
                    + "in sleep deprivation. Child and/or family members may be "
                    + "exhausted from lack of sleep due to the child’s problems with "
                    + "sleep.");

            q3_3.setQuestionDescription(desc3_3);
            q3_3.setQuestionToConsider(quesToConsider3_3);
            q3_3.setRatingsDefinition(rating3_3);

            ques3.add(q3_3);

            DomainEntity domain3 = domainSessionBean.createNewDomain(mph);
            AgeGroupEntity age5 = ageGroupSessionBean.createNewAgeGroupForDomain(mphall, domain3.getDomainId());
            for (MainQuestionEntity questions : ques3) {
                questionsSessionBean.createMainQuestionForAgeGroup(age5.getAgeGroupId(), questions);
            }

            AgeGroupEntity mphadol = new AgeGroupEntity("0-6");
            List<MainQuestionEntity> ques3adol = new ArrayList<>();

            //create 3.4
            MainQuestionEntity q3_4 = new MainQuestionEntity("3.4", "Pre-disposing Risk Factors");
            List<String> desc3_4 = new ArrayList<>();
            desc3_4.add("This item rates the pre‐disposing risk factors of the child.");
            desc3_4.add("When rating this item, please consider whether the child has experienced ANY of the following: ");
            desc3_4.add("Birth Weight: Child was born with low birth weight (1.5‐2.5kg), very low birth weight (1‐1.5kg) or "
                    + "extremely low birth weight (less than 1kg).");
            desc3_4.add("Antenatal Care: Child’s biological mother had received poor or uncertain antenatal care, or had "
                    + "pregnancy‐related illness (with/without medication).");
            desc3_4.add("Labour and Delivery: Child or child’s biological mother had problems during delivery that may or may "
                    + "not have an adverse impact.");
            desc3_4.add("Substance Exposure: Child was exposed to alcohol or drugs in the womb/child’s biological mother "
                    + "abused substances during pregnancy.");
            desc3_4.add("Parent/Sibling Cognitive or Physical Capacity: Child’s biological parent/s and/or sibling/s have "
                    + "developmental (e.g. low IQ, autism) or behavioural (e.g., attention deficit, oppositional defiant or "
                    + "conduct disorders) problems");

            List<String> quesToConsider3_4 = new ArrayList<>();
            quesToConsider3_4.add("What was the child’s birth weight?");
            quesToConsider3_4.add("Did the child’s biological mother receive adequate "
                    + "medical attention or suffer from any illness during "
                    + "pregnancy?");
            quesToConsider3_4.add("Were there any unusual circumstances related to "
                    + "the labour and delivery of the child?");
            quesToConsider3_4.add("Did the child’s biological mother abuse substances "
                    + "during pregnancy?");
            quesToConsider3_4.add("Do the child’s parents or siblings have any "
                    + "developmental disabilities/behavioural problems?");

            Map<Integer, String> rating3_4 = new HashMap<>();
            rating3_4.put(0, "No evidence of pre‐disposing risk "
                    + "factors.");
            rating3_4.put(1, "Child has mild pre‐disposing risk "
                    + "factors.");
            rating3_4.put(2, "Child has moderate pre‐disposing risk "
                    + "factors.");
            rating3_4.put(3, "Child has severe pre‐disposing risk "
                    + "factors.");

            q3_4.setQuestionDescription(desc3_4);
            q3_4.setQuestionToConsider(quesToConsider3_4);
            q3_4.setRatingsDefinition(rating3_4);

            ques3adol.add(q3_4);

            //create 3.5
            MainQuestionEntity q3_5 = new MainQuestionEntity("3.5", "Immunisation");
            List<String> desc3_5 = new ArrayList<>();
            desc3_5.add("This item rates whether the child has up‐to‐date immunisation [which refers to all "
                    + "routine vaccinations on the National Childhood Immunisation Schedule (NCIS)].");
            desc3_5.add("This includes: ");
            desc3_5.add("Birth: BCG, HepB (D1)");
            desc3_5.add("1 month: HepB (D2)");
            desc3_5.add("3 months: DTaP (D1), IPV (D1), Hib (D1)");
            desc3_5.add("4 months: DtaP (D2), IPV (D2), Hib (D2)");
            desc3_5.add("5 months: DtaP (D3), IPV (D3), Hib (D3), HepB (D3)");
            desc3_5.add("6 months: HepB (D3) if not done at 5 months");
            desc3_5.add("12 months: MMR (D1)");
            desc3_5.add("15 months: MMR (D2)");
            desc3_5.add("18 months: DtaP (B1), IPV (B1), Hib (B1), MMR (D2) if not done at 15 months");
            desc3_5.add("Immunisation taken within 3 months of the recommended age of administration would be considered "
                    + "on schedule.");

            List<String> quesToConsider3_5 = new ArrayList<>();
            quesToConsider3_5.add("Has the child "
                    + "missed any of "
                    + "his/her routine "
                    + "vaccinations on "
                    + "the NCIS?");
            quesToConsider3_5.add("Does the "
                    + "parent/caregiver "
                    + "understand the "
                    + "importance of "
                    + "vaccinations for "
                    + "the child’s health "
                    + "and well‐being? ");
            Map<Integer, String> rating3_5 = new HashMap<>();
            rating3_5.put(0, "Child is up­to­date on all routine vaccinations on the NCIS. ");
            rating3_5.put(1, "Child is missing at least one vaccination on the NCIS. However, caregiver "
                    + "understands the importance of vaccinations, and intends to go for "
                    + "follow­up appointments for the child to catch up on missed "
                    + "vaccinations. ");
            rating3_5.put(2, "Child is missing at least one vaccination on the NCIS. Caregiver requires "
                    + "significant support or prompting before following up with on "
                    + "appointments for the child to catch up on missed vaccinations. Child is "
                    + "lagging behind on his/her vaccinations for at least 6 months but less "
                    + "than 1 year. ");
            rating3_5.put(3, "Caregiver is opposed to sending the child for any vaccinations in spite of "
                    + "information shared with him/her on the importance of these "
                    + "vaccinations for the child’s health and well‐being. "
                    + "OR "
                    + "Child is lagging behind on his/her vaccinations by a year or longer.");

            q3_5.setQuestionDescription(desc3_5);
            q3_5.setQuestionToConsider(quesToConsider3_5);
            q3_5.setRatingsDefinition(rating3_5);

            ques3adol.add(q3_5);

            //create 3.6
            MainQuestionEntity q3_6 = new MainQuestionEntity("3.6", "Eating Routines/Patterns");
            List<String> desc3_6 = new ArrayList<>();
            desc3_6.add("This item rates the child’s daily eating arrangements and patterns. It "
                    + "rates whether the child is being fed regularly and whether the amount, variety and nutritional content "
                    + "of food are sufficient for healthy development.");

            List<String> quesToConsider3_6 = new ArrayList<>();
            quesToConsider3_6.add("Is the child being fed "
                    + "regularly?");
            quesToConsider3_6.add("Does the child’s diet "
                    + "consist of a variety of "
                    + "food with sufficient "
                    + "nutritional content?");

            Map<Integer, String> rating3_6 = new HashMap<>();
            rating3_6.put(0, "Child appears to be properly fed.");
            rating3_6.put(1, "Child is fed regular meals, but little attention is paid to ensure that "
                    + "child’s nutritional intake is balanced. "
                    + "For example, meals consist mainly of carbohydrates like "
                    + "rice/porridge, and seldom include other classes of nutrients such as "
                    + "proteins, minerals and vitamins.");
            rating3_6.put(2, "Child has meals, but they may be irregular and there may not be "
                    + "sufficient food at times."
                    + "For example, child only has one or two meals a day (including meals "
                    + "provided at childcare).");
            rating3_6.put(3, "Child does not have regular meals and shows signs of "
                    + "malnourishment.");

            q3_6.setQuestionDescription(desc3_6);
            q3_6.setQuestionToConsider(quesToConsider3_6);
            q3_6.setRatingsDefinition(rating3_6);

            ques3adol.add(q3_6);

            //create 3.7
            MainQuestionEntity q3_7 = new MainQuestionEntity("3.7", "Problems in Eating");
            List<String> desc3_7 = new ArrayList<>();
            desc3_7.add("This item describes problems related to eating.");

            List<String> quesToConsider3_7 = new ArrayList<>();
            quesToConsider3_7.add("Does the child "
                    + "have any problems "
                    + "with eating? "
                    + "➢ If yes, does this "
                    + "interfere with "
                    + "the child’s "
                    + "functioning?");

            Map<Integer, String> rating3_7 = new HashMap<>();
            rating3_7.put(0, "No evidence of problems related to eating.");
            rating3_7.put(1, "Mild problems with eating have been present in the past or are "
                    + "currently present some of the time causing mild impairment in "
                    + "functioning");
            rating3_7.put(2, "Moderate problems with eating are present, and impair the child’s "
                    + "functioning. Child may be a finicky eater, spits food or overeats. Infant "
                    + "may have problems with oral motor control. Older children may overeat, "
                    + "have few food preferences, and not have a clear pattern of when they "
                    + "eat.");
            rating3_7.put(3, "Severe problems with eating are present that hinder the appropriate "
                    + "developmental milestones of the child. The child and family are very "
                    + "distressed and unable to overcome problems in this area. ");

            q3_7.setQuestionDescription(desc3_7);
            q3_7.setQuestionToConsider(quesToConsider3_7);
            q3_7.setRatingsDefinition(rating3_7);

            ques3adol.add(q3_7);

            //create 3.8
            MainQuestionEntity q3_8 = new MainQuestionEntity("3.8", "Elimination: Problems in passing motion");
            List<String> desc3_8 = new ArrayList<>();
            desc3_8.add("This item describes problems related to passing "
                    + "motion.");

            List<String> quesToConsider3_8 = new ArrayList<>();
            quesToConsider3_8.add("Does the child "
                    + "have any problems "
                    + "passing motion? "
                    + "➢ If yes, do these "
                    + "problems "
                    + "interfere with "
                    + "the child’s "
                    + "functioning?");

            Map<Integer, String> rating3_8 = new HashMap<>();
            rating3_8.put(0, "No evidence of elimination problems.");
            rating3_8.put(1, "Child may have a history of difficultiesin passing motion but is presently "
                    + "not experiencing this other than on rare occasions.");
            rating3_8.put(2, "Child demonstrates problems with passing motion on a consistent "
                    + "basis, and this is interfering with child’s functioning. Infants may "
                    + "completely lack a routine in elimination and develop constipation as a "
                    + "result. Older children may experience the same issues as infants along "
                    + "with encopresis and enuresis (inability to control urination/stools). ");
            rating3_8.put(3, "Child demonstrates significant difficulty with passing motion, to the "
                    + "extent that the child and his/her parent(s) are in significant distress, or "
                    + "previous interventions have failed.");

            q3_8.setQuestionDescription(desc3_8);
            q3_8.setQuestionToConsider(quesToConsider3_8);
            q3_8.setRatingsDefinition(rating3_8);

            ques3adol.add(q3_8);

            //create 3.9
            MainQuestionEntity q3_9 = new MainQuestionEntity("3.9", "Failure to Thrive");
            List<String> desc3_9 = new ArrayList<>();
            desc3_8.add("This item rates the experience(s) of the child in relation to problems with physical "
                    + "development such as growth and weight gain");
            desc3_8.add("Failure to thrive is defined as height or weight for age that falls below the 3rd percentile on multiple "
                    + "occasions or a downward change in growth across two major growth percentiles.");
            desc3_8.add("For children under the age of 2 years, please use the corrected age for prematurity.");

            List<String> quesToConsider3_9 = new ArrayList<>();
            quesToConsider3_8.add("Are there any "
                    + "problems with the "
                    + "child’s physical "
                    + "development?");
            quesToConsider3_8.add("Is the weight of "
                    + "the child "
                    + "significantly lower "
                    + "than other "
                    + "children of similar "
                    + "age and gender?");

            Map<Integer, String> rating3_9 = new HashMap<>();
            rating3_9.put(0, "No evidence of failure to thrive.");
            rating3_9.put(1, "Child may have experienced past problems with growth and ability to "
                    + "gain weight but is currently not experiencing problems. Child may "
                    + "presently be experiencing slow development in this area. ");
            rating3_9.put(2, "Child is experiencing problems in his/her ability to maintain weight or "
                    + "growth. Child’s weight or height is significantly less than what is "
                    + "expected for age and gender.");
            rating3_9.put(3, "Child has one or more of all of the above and is currently at serious "
                    + "medical risk. ");

            q3_9.setQuestionDescription(desc3_9);
            q3_9.setQuestionToConsider(quesToConsider3_9);
            q3_9.setRatingsDefinition(rating3_9);

            ques3adol.add(q3_9);

            AgeGroupEntity age6 = ageGroupSessionBean.createNewAgeGroupForDomain(mphadol, domain3.getDomainId());
            for (MainQuestionEntity questions : ques3adol) {
                questionsSessionBean.createMainQuestionForAgeGroup(age6.getAgeGroupId(), questions);
            }

            // =============== Create School Domain ==================
            List<String> description = new ArrayList<>();
            description.add("For children in special education schools:\n"
                    + "➢ Rate the child’s performance and behaviour relative to other children in the special education "
                    + "school of the same developmental age (not chronological age)");
            description.add("For children receiving special education services in mainstream schools:\n"
                    + "➢ Rate the child’s performance and behaviour relative to other children in the mainstream school of "
                    + "the same chronological age.");
            description.add("For children who attend compulsory classes/learning programmes in a residential facility such as a "
                    + "children’s home or institutional setting (e.g., Singapore Boys’ Home/Singapore Girls’ Home):\n"
                    + "➢ Rate all items based on the classes the child has been attending at the residential facility.\n"
                    + "➢ If there are no opportunities to rate item 4.3 “School Attendance” due to the nature of "
                    + "classes/programme structures in the residential facility, rate based on the last known school "
                    + "attendance");
            description.add("For children who did not complete secondary school education (minimum ‘N’ level or its equivalent):\n"
                    + "➢ Rate the child as a student who chooses not to go to school (hence the item “School Attendance” "
                    + "should be rated “3”)\n"
                    + "➢ Rate the child based on the last time he/she was in school");
            description.add("Rate N/A if the child:\n"
                    + "➢ Is below 7 years old and not yet in formal education\n"
                    + "➢ Has completed his/her secondary school education (minimum ‘N’ level or its equivalent) and no "
                    + "longer wishes to resume his/her education\n"
                    + "➢ Is 18 years old and above and no longer wishes to resume his/her education");

            DomainEntity sch = new DomainEntity("School", description, false, 1, false);
            AgeGroupEntity schall = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques4 = new ArrayList<>();

            //create 4.1
            MainQuestionEntity q4_1 = new MainQuestionEntity("4.1", "School Behaviour");
            List<String> desc4_1 = new ArrayList<>();
            desc4_1.add("This item rates the child’s behaviour in school.");
            desc4_1.add("This item should be rated independently from the child’s attendance (e.g., the child/youth may skip "
                    + "school often but behave appropriately when he/she is in school).");

            List<String> quesToConsider4_1 = new ArrayList<>();
            quesToConsider4_1.add("How is the child behaving in "
                    + "school?");
            quesToConsider4_1.add("Does he/she have any "
                    + "behavioural problems in "
                    + "school?");
            quesToConsider4_1.add("Have the teachers or the "
                    + "school counsellor informed "
                    + "the caregiver about any "
                    + "problems the child is facing "
                    + "at school?");

            Map<Integer, String> rating4_1 = new HashMap<>();
            rating4_1.put(0, "Child is behaving well in school.");
            rating4_1.put(1, "Child is behaving adequately in school although some "
                    + "behavioural problems exist.");
            rating4_1.put(2, "Child is having moderate behavioural problems at school. "
                    + "For example, he/she is disruptive and may have received "
                    + "warnings including detention.");
            rating4_1.put(3, "Child is having severe behavioural problems at school. "
                    + "For example, he/she isfrequently orseverely disruptive, may\n"
                    + "be suspended from school, and/or be at risk of expulsion due\n"
                    + "to the behaviour.");
            rating4_1.put(4, "Child is below 7 years old and not yet in formal education; "
                    + "OR "
                    + "Child has completed his/her secondary school education or is "
                    + "≥  18 years old, and no longer wishes to resume his/her "
                    + "education.");

            q4_1.setQuestionDescription(desc4_1);
            q4_1.setQuestionToConsider(quesToConsider4_1);
            q4_1.setRatingsDefinition(rating4_1);

            ques4.add(q4_1);

            //create 4.2
            MainQuestionEntity q4_2 = new MainQuestionEntity("4.2", "School Performance");
            List<String> desc4_2 = new ArrayList<>();
            desc4_2.add("This item rates the child’s current level of academic performance.");

            List<String> quesToConsider4_2 = new ArrayList<>();
            quesToConsider4_2.add("What are the child's results "
                    + "like?");
            quesToConsider4_2.add("Is he/she having "
                    + "difficulties with any "
                    + "subjects?");
            quesToConsider4_2.add("Is he/she failing any "
                    + "subjects?"
                    + "➢ If yes, is he/she at risk "
                    + "of being retained?");
            quesToConsider4_2.add("Have the teachers raised any "
                    + "concerns about the child's "
                    + "academic performance?");

            Map<Integer, String> rating4_2 = new HashMap<>();
            rating4_2.put(0, "Child is doing well in school/passing all subjects.");
            rating4_2.put(1, "Child is doing adequately in school, although some problems "
                    + "with academic performance exist (e.g., child is failing one "
                    + "subject)");
            rating4_2.put(2, "Child is having moderate problems with academic "
                    + "performance. He/she may be failing some subjects.");
            rating4_2.put(3, "Child is having severe problems with academic performance. "
                    + "He/she may be failing most subjects or is more than one year "
                    + "behind same‐aged peers in academic performance");
            rating4_2.put(4, "Child is having severe problems with academic performance. "
                    + "He/she may be failing most subjects or is more than one year "
                    + "behind same‐aged peers in academic performance");

            q4_2.setQuestionDescription(desc4_2);
            q4_2.setQuestionToConsider(quesToConsider4_2);
            q4_2.setRatingsDefinition(rating4_2);

            ques4.add(q4_2);

            //create 4.3
            MainQuestionEntity q4_3 = new MainQuestionEntity("4.3", "School Attendance");
            List<String> desc4_3 = new ArrayList<>();
            desc4_3.add("This item rates the child’s attendance at school.");
            desc4_3.add("If school is having a holiday break, rate the last known school attendance.");
            desc4_3.add("If the child is in a children’s home or institutional setting and attending compulsory classes/learning "
                    + "programmes within the institution, rate last known school attendance prior to admission.");

            List<String> quesToConsider4_3 = new ArrayList<>();
            quesToConsider4_3.add("What is the child's attendance "
                    + "at school?");
            quesToConsider4_3.add("Are there any concerns raised "
                    + "by the teachers about the "
                    + "child's attendance? "
                    + "➢ If yes, how many days, on "
                    + "average, does the child "
                    + "miss per week?");
            quesToConsider4_3.add("Are there any interventions in "
                    + "place to ensure that the child "
                    + "attends school?");

            Map<Integer, String> rating4_3 = new HashMap<>();
            rating4_3.put(0, "Child attends school regularly.");
            rating4_3.put(1, "Child has some problems with school attendance but "
                    + "generally goes to school OR child may have moderate to "
                    + "severe problems in the last six months but has been "
                    + "attending school regularly in the past month. ");
            rating4_3.put(2, "Child has moderate problems with school attendance. "
                    + "He/she has been missing school at least once a week on "
                    + "average.");
            rating4_3.put(3, "Child is generally truant or refusing to go to school.");
            rating4_3.put(4, "Child is below 7 years old and not yet in formal education; "
                    + "OR "
                    + "Child has completed his/her secondary school education or "
                    + "is ≥ 18 years old, and no longer wishes to resume his/her "
                    + "education.");

            q4_3.setQuestionDescription(desc4_3);
            q4_3.setQuestionToConsider(quesToConsider4_3);
            q4_3.setRatingsDefinition(rating4_3);

            ques4.add(q4_3);

            //create 4.4
            MainQuestionEntity q4_4 = new MainQuestionEntity("4.4", "Reading & Writing Abilities");
            List<String> desc4_4 = new ArrayList<>();
            desc4_4.add("This item identifies children who may have literacy problems (e.g., "
                    + "dyslexia or reading/writing disabilities).");

            List<String> quesToConsider4_4 = new ArrayList<>();
            quesToConsider4_4.add("Does the child have any "
                    + "difficulties with reading, "
                    + "writing or spelling?"
                    + "➢ If yes, is the child in "
                    + "learning support?");
            quesToConsider4_4.add("Has he/she ever been "
                    + "assessed for learning "
                    + "difficulties?");

            Map<Integer, String> rating4_4 = new HashMap<>();
            rating4_4.put(0, "Child’s reading and writing abilities are comparable to sameaged peers. No evidence of problems with reading and "
                    + "writing.");
            rating4_4.put(1, "Child hassome difficulties with reading and writing  compared "
                    + "with same‐aged peers but child does not require special "
                    + "education intervention.");
            rating4_4.put(2, "Child has clear difficulties with reading and writing.");
            rating4_4.put(3, "Child has severe difficulties or is significantly impaired in "
                    + "reading and writing.");
            rating4_4.put(4, "Child is below 7 years old and not yet in formal education; "
                    + "OR\n"
                    + "Child has completed his/her secondary school education or is "
                    + "≥  18 years old, and no longer wishes to resume his/her "
                    + "education.");

            q4_4.setQuestionDescription(desc4_4);
            q4_4.setQuestionToConsider(quesToConsider4_4);
            q4_4.setRatingsDefinition(rating4_4);

            ques4.add(q4_4);

            DomainEntity domain4 = domainSessionBean.createNewDomain(sch);
            AgeGroupEntity age7 = ageGroupSessionBean.createNewAgeGroupForDomain(schall, domain4.getDomainId());
            for (MainQuestionEntity questions : ques4) {
                questionsSessionBean.createMainQuestionForAgeGroup(age7.getAgeGroupId(), questions);
            }

//            // Create Behavioural & Emotional Needs Domain
            DomainEntity ben = new DomainEntity("Behavioural & Emotional Needs", null, false, 1, false);
            AgeGroupEntity benall = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques5 = new ArrayList<>();

            //create 5.1
            MainQuestionEntity q5_1 = new MainQuestionEntity("5.1", "Anxiety");
            List<String> desc5_1 = new ArrayList<>();
            desc5_1.add("This item rates the child’s level of fearfulness, worry or other characteristics of anxiety.");
            desc5_1.add("Symptoms of anxiety in children include:");
            desc5_1.add("Anxiety when interacting with a caregiver");
            desc5_1.add("Refusing to go to pre‐school/childcare/school");
            desc5_1.add("Frequent stomach‐aches and other physical complaints");
            desc5_1.add("Constant worrying or concerns about family, school, friends, or activities");
            desc5_1.add("Being overly clingy");
            desc5_1.add("Having trouble sleeping or experiencing nightmares");
            desc5_1.add("Avoidance of social situations");

            List<String> quesToConsider5_1 = new ArrayList<>();
            quesToConsider5_1.add("Does the child seem "
                    + "anxious or fearful in "
                    + "general? "
                    + "➢ E.g., does the child "
                    + "avoid any activities "
                    + "or situations because "
                    + "he/she is afraid?");
            quesToConsider5_1.add("Does the child worry a "
                    + "lot (more than what is "
                    + "usual for children "
                    + "his/her age)?");

            Map<Integer, String> rating5_1 = new HashMap<>();
            rating5_1.put(0, "No evidence of anxiety.");
            rating5_1.put(1, "History or suspicion of anxiety problems or mild anxiety that may "
                    + "be associated with a negative life event. ");
            rating5_1.put(2, "Moderate problems with anxiety that has significantly interfered "
                    + "with child’s ability to function in one of these areas: in school, with "
                    + "friends or at home.");
            rating5_1.put(3, "Severe problems with anxiety that has significantly interfered with "
                    + "child’s ability to function in more than one area: in school, with\n"
                    + "friends or at home.");

            q5_1.setQuestionDescription(desc5_1);
            q5_1.setQuestionToConsider(quesToConsider5_1);
            q5_1.setRatingsDefinition(rating5_1);

            ques5.add(q5_1);

            //create 5.2
            MainQuestionEntity q5_2 = new MainQuestionEntity("5.2", "Adjustment to Trauma");
            List<String> desc5_2 = new ArrayList<>();
            desc5_2.add("This item describes a child who is having difficulties adjusting to any "
                    + "traumatic or adverse childhood experience.");
            desc5_2.add("Thisitem covers trauma symptoms* that the child may have experienced as a result of exposure to prior "
                    + "traumatic/adverse experiences");
            desc5_2.add("Traumatic events are the child’s direct experience, witnessing, or confrontation with an event or events "
                    + "that involve actual or threatened death or serious injury to the child or others, or a threat to the "
                    + "psychological or physical integrity of the child or others.  ");
            desc5_2.add("If a child has not experienced any trauma, or, if his/her traumatic experiences did not have an impact "
                    + "on his/her functioning, then he/she would be rated a “0” on this item.");

            List<String> quesToConsider5_2 = new ArrayList<>();
            quesToConsider5_2.add("If yes: "
                    + "➢ Has the child been regressing "
                    + "developmentally?");
            quesToConsider5_2.add("Does the child have difficulties "
                    + "coping with the trauma (e.g., "
                    + "experiencing nightmares, "
                    + "flash‐backs)?");
            quesToConsider5_2.add(" Does the child seem very "
                    + "jumpy or easily startled (e.g., "
                    + "by loud noises)?");
            quesToConsider5_2.add("Does the child avoid any "
                    + "thoughts or situations that "
                    + "remind him/her of the trauma?");

            Map<Integer, String> rating5_2 = new HashMap<>();
            rating5_2.put(0, "No evidence of trauma or adjustment to trauma issues.");
            rating5_2.put(1, "History or suspicion of difficulties adjusting to "
                    + "traumatic experience. Child may be in the process of "
                    + "recovering from extreme reaction to a traumatic "
                    + "experience. "
                    + "OR "
                    + "Child has mild problems with adjustment to trauma or "
                    + "there are mild changesin the child’s behaviourthat are "
                    + "well­managed by caregivers.  ");
            rating5_2.put(2, "Child has moderate problems with adjustment to "
                    + "trauma. Symptoms can vary widely (e.g., "
                    + "sleeping/eating disturbances, regressive behaviour) "
                    + "and they may interfere with child’s functioning at "
                    + "home, in school or with peers.  ");
            rating5_2.put(3, "Child has severe problems with adjustment to trauma. "
                    + "Child may display symptoms (e.g., flashbacks, "
                    + "nightmares,significant anxiety, and disturbing thoughts "
                    + "of trauma experience) which makes it impossible for "
                    + "the child to function at home, in school or with peers.");

            q5_2.setQuestionDescription(desc5_2);
            q5_2.setQuestionToConsider(quesToConsider5_2);
            q5_2.setRatingsDefinition(rating5_2);

            ques5.add(q5_2);

            DomainEntity domain5 = domainSessionBean.createNewDomain(ben);
            AgeGroupEntity age8 = ageGroupSessionBean.createNewAgeGroupForDomain(benall, domain5.getDomainId());
            for (MainQuestionEntity questions : ques5) {
                questionsSessionBean.createMainQuestionForAgeGroup(age8.getAgeGroupId(), questions);
            }

            AgeGroupEntity benadol = new AgeGroupEntity("0-6");
            List<MainQuestionEntity> ques5adol = new ArrayList<>();

            //create 5.3
            MainQuestionEntity q5_3 = new MainQuestionEntity("5.3", "Frustration Tolerance / Tantrums");
            List<String> desc5_3 = new ArrayList<>();
            desc5_3.add("This item rates a child’s level of agitation and/or anger when "
                    + "frustrated. ");
            desc5_3.add("This may include a demonstration of aggressive behaviours when things do not go as the child wished.  ");
            desc5_3.add("The child’s ability to control and modulate intense emotions (e.g., coping with frustration and "
                    + "transitions) is also rated here.");
            desc5_3.add("At this developmental stage, some sources of frustration for pre‐schoolers can be peers, adults, etc");

            List<String> quesToConsider5_3 = new ArrayList<>();
            quesToConsider5_3.add("How often does "
                    + "the child engage in "
                    + "tantrum "
                    + "behaviours?");
            quesToConsider5_3.add("Are tantrums ever "
                    + "at a dangerous "
                    + "level?  ");
            quesToConsider5_3.add("Can the child cope "
                    + "well when "
                    + "frustrated?");

            Map<Integer, String> rating5_3 = new HashMap<>();
            rating5_3.put(0, "No evidence of problems with dealing with frustration.");
            rating5_3.put(1, "History or mild difficulties dealing with frustration. Child may sometimes "
                    + "become agitated or verbally hostile, aggressive or anxious when "
                    + "frustrated.  ");
            rating5_3.put(2, "Child struggles with tolerating frustration. Child’sreaction to frustration\n"
                    + "may impair functioning at home, in pre‐school or with peers. He/she\n"
                    + "may engage in tantrum behaviours when frustrated.  ");
            rating5_3.put(3, "Child engages in violent tantrum behaviours when frustrated. Others "
                    + "may be afraid of child’s tantrums or child may hurt self or others during "
                    + "tantrums.");

            q5_3.setQuestionDescription(desc5_3);
            q5_3.setQuestionToConsider(quesToConsider5_3);
            q5_3.setRatingsDefinition(rating5_3);

            ques5adol.add(q5_3);

            AgeGroupEntity age9 = ageGroupSessionBean.createNewAgeGroupForDomain(benadol, domain5.getDomainId());
            for (MainQuestionEntity questions : ques5adol) {
                questionsSessionBean.createMainQuestionForAgeGroup(age9.getAgeGroupId(), questions);
            }

            AgeGroupEntity benchild = new AgeGroupEntity("7+");
            List<MainQuestionEntity> ques5child = new ArrayList<>();

            //create 5.4
            MainQuestionEntity q5_4 = new MainQuestionEntity("5.4", "Attention / Concentration");
            List<String> desc5_4 = new ArrayList<>();
            desc5_4.add("  This item rates problems with attention, concentration and task "
                    + "completion. ");
            desc5_4.add("This may include symptoms that are consistent with the Inattention aspect of Attention‐Deficit "
                    + "Hyperactivity Disorder (ADHD), such as a child who:");
            desc5_4.add("Often makes careless mistakes in schoolwork and other activities");
            desc5_4.add("Often has trouble holding attention on tasks or play activities");
            desc5_4.add("Often loses things necessary for tasks and activities");
            desc5_4.add("Is often easily distracted");
            desc5_4.add("Is often forgetful in daily activities");

            List<String> quesToConsider5_4 = new ArrayList<>();
            quesToConsider5_4.add("Does the child have "
                    + "difficulties sustaining "
                    + "attention?");
            quesToConsider5_4.add("Does the child seem to "
                    + "have a short attention "
                    + "span?");
            quesToConsider5_4.add("Does the child often make "
                    + "careless mistakes at home "
                    + "or at school?");
            quesToConsider5_4.add("Does the child often lose "
                    + "or forget items?");

            Map<Integer, String> rating5_4 = new HashMap<>();
            rating5_4.put(0, "No evidence of problems with attention/concentration. "
                    + "The child is able to stay on task in an age‐appropriate manner.");
            rating5_4.put(1, "History, suspicion or mild problems with "
                    + "attention/concentration. "
                    + "Child may have some difficulties staying on task for an age‐ "
                    + "appropriate time period in school or play.");
            rating5_4.put(2, "Child has moderate problems with attention/concentration. "
                    + "Child may have problems with sustained attention, become "
                    + "easily distracted or forgetful in daily activities, have trouble "
                    + "following through on activities, and become reluctant to engage "
                    + "in activities that require sustained effort.   ");
            rating5_4.put(3, "Child has severe problems with attention/concentration. "
                    + "Child may have significant attention difficulties.  ");

            q5_4.setQuestionDescription(desc5_4);
            q5_4.setQuestionToConsider(quesToConsider5_4);
            q5_4.setRatingsDefinition(rating5_4);

            ques5child.add(q5_4);

            //create 5.5
            MainQuestionEntity q5_5 = new MainQuestionEntity("5.5", "Impulsivity /  Hyperactivity");
            List<String> desc5_5 = new ArrayList<>();
            desc5_5.add("This item rates the child’s level of impulsivity or hyperactivity.");
            desc5_5.add("Problems with impulse control and impulsive behaviours would be rated here");
            desc5_5.add("Children and adolescents with impulse problems tend to engage in behaviour without thinking, "
                    + "regardless of the consequences");
            desc5_5.add("This item may include symptoms that are consistent with the Hyperactive aspect of Attention‐Deficit "
                    + "Hyperactivity Disorder (ADHD), such as a child who:");
            desc5_5.add("Often has trouble waiting his/her turn");
            desc5_5.add("Often runs about or climbs in situations where it is not appropriate");
            desc5_5.add("Often leaves seat in situations when remaining seated in expected");
            desc5_5.add("Often fidgets with or taps hands or feet, or squirms in seat");

            List<String> quesToConsider5_5 = new ArrayList<>();
            quesToConsider5_5.add("Does the child have "
                    + "difficulties sitting still for "
                    + "short periods of time?");
            quesToConsider5_5.add("Has anyone described the "
                    + "child as being “hyper”?");
            quesToConsider5_5.add("Is the child usually able to "
                    + "control him/herself? ");

            Map<Integer, String> rating5_5 = new HashMap<>();
            rating5_5.put(0, "No evidence of problems with impulsivity and/or hyperactivity.  "
                    + "The child is able to stay on task in an age‐appropriate manner.");
            rating5_5.put(1, "History, suspicion or mild problems with impulsivity and/or "
                    + "hyperactivity.  ");
            rating5_5.put(2, "Clear evidence of moderate problems with impulsivity and/or "
                    + "hyperactivity that interferes with child’s ability to function in "
                    + "at least one area: at school, at home or with friends. ");
            rating5_5.put(3, "Clear evidence of severe problems with impulsivity and/or "
                    + "hyperactivity that places the child at physical harm and/or "
                    + "prevents the child from functioning in at least one area: in "
                    + "school, at home or with friends.");

            q5_5.setQuestionDescription(desc5_5);
            q5_5.setQuestionToConsider(quesToConsider5_5);
            q5_5.setRatingsDefinition(rating5_5);

            ques5child.add(q5_5);

            //create 5.6
            MainQuestionEntity q5_6 = new MainQuestionEntity("5.6", "Oppositional");
            List<String> desc5_6 = new ArrayList<>();
            desc5_6.add("This item rates how the child relates/complies to authority figures.");
            desc5_6.add("Authority figures are people with responsibility for and control over the child");
            desc5_6.add("Generally, oppositional behaviour occurs in response to conditions set by an authority figure, such as "
                    + "parents or teachers.");
            desc5_6.add("Some behaviours characteristic of an oppositional child include a child who: ");
            desc5_6.add("Is argumentative with adults/authority figures");
            desc5_6.add("Refuses to comply with adults’ requests and rules");
            desc5_6.add("Often questions rules");
            desc5_6.add("Unlike children with conduct problems, children with oppositional behaviour are not aggressive toward "
                    + "people or animals, do not destroy property and do not show a pattern of theft or deceit.");

            List<String> quesToConsider5_6 = new ArrayList<>();
            quesToConsider5_6.add("Does the child follow his/her "
                    + "parent’s rules?");
            quesToConsider5_6.add("Does the child usually do "
                    + "what authority figures ask "
                    + "him/her to do?");
            quesToConsider5_6.add("Have teachers or other "
                    + "youths reported that the "
                    + "child does not follow rules or "
                    + "directions?");
            quesToConsider5_6.add("Does the child argue with "
                    + "adults when they give "
                    + "him/her instructions?");

            Map<Integer, String> rating5_6 = new HashMap<>();
            rating5_6.put(0, "Child has no problems with compliance.");
            rating5_6.put(1, "Child has a history or mild problems with compliance with "
                    + "rules or adult instructions. "
                    + "For example, child may occasionally talk back to teacher, "
                    + "parent/caregiver.");
            rating5_6.put(2, "Child has moderate problems with compliance with rules or "
                    + "adult instructions. "
                    + "For example, child may often argue with adults and actively "
                    + "defy or refuse to comply with rules or instructions from "
                    + "authority figures");
            rating5_6.put(3, "Child has severe problems with compliance with rules or "
                    + "adult instructions. "
                    + "For example, child may be almost always noncompliant, "
                    + "and/or repeatedly ignores authority.");

            q5_6.setQuestionDescription(desc5_6);
            q5_6.setQuestionToConsider(quesToConsider5_6);
            q5_6.setRatingsDefinition(rating5_6);

            ques5child.add(q5_6);

            //create 5.7
            MainQuestionEntity q5_7 = new MainQuestionEntity("5.7", "Conduct");
            List<String> desc5_7 = new ArrayList<>();
            desc5_7.add("This item rates antisocial behaviours and conduct problems");
            desc5_7.add("Examples of antisocial behaviours:");
            desc5_7.add("Bullying and intimidating others");
            desc5_7.add("Being physically cruel to animals and people");
            desc5_7.add("Initiating fights/assault");
            desc5_7.add("Theft/shoplifting");
            desc5_7.add("Forcing others into sexual activity");
            desc5_7.add("Deliberately destroying property/vandalism");
            desc5_7.add("Serious violation of rules");
            desc5_7.add("Lying");

            List<String> quesToConsider5_7 = new ArrayList<>();
            quesToConsider5_7.add("Has the child ever "
                    + "shown threatening or "
                    + "violent behaviour "
                    + "towards others?");
            quesToConsider5_7.add("Has the child "
                    + "demonstrated any "
                    + "criminal behaviour?");
            quesToConsider5_7.add("Is the child generally "
                    + "honest, or does he/she "
                    + "often tell lies?");
            quesToConsider5_7.add("Has the child ever "
                    + "tortured animals or set "
                    + "fires?");

            Map<Integer, String> rating5_7 = new HashMap<>();
            rating5_7.put(0, "No evidence of antisocial behaviours/conduct problems");
            rating5_7.put(1, "History, suspicion or mild level of antisocial behaviours/conduct "
                    + "problems. "
                    + "For example, this could include petty theft from family or "
                    + "occasional truancy.  ");
            rating5_7.put(2, "Moderate level of antisocial behaviours/conduct problems. "
                    + "For example, this could include episodes of planned aggression.");
            rating5_7.put(3, "Severe level of antisocial behaviours/conduct problems. "
                    + "For example, this could include frequent episodes of unprovoked, "
                    + "planned aggression or other antisocial behaviour that places the "
                    + "child or community at significant risk of physical harm");

            q5_7.setQuestionDescription(desc5_7);
            q5_7.setQuestionToConsider(quesToConsider5_7);
            q5_7.setRatingsDefinition(rating5_7);

            ques5child.add(q5_7);

            //create 5.8
            MainQuestionEntity q5_8 = new MainQuestionEntity("5.8", "Substance Use");
            List<String> desc5_8 = new ArrayList<>();
            desc5_8.add("This item rates the use of alcohol, illegal drugs, misuse of prescription medications "
                    + "and the inhalation of any substances for recreational purposes (e.g., glue‐sniffing).  ");
            desc5_8.add("Some commonly abused substances include");
            desc5_8.add("Buprenorphine (Subutex, Su Su, Tec)");
            desc5_8.add("BZP and TFMPP (Party Pills)");
            desc5_8.add("Cannabis (Ganja, Marijuana, Grass, Pot, Joints, Hashish, Weed)");
            desc5_8.add("Cocaine (Snow, Coke, Crack)");
            desc5_8.add("Ecstasy (part of the stimulant Amphetamine family)");
            desc5_8.add("Heroin (Ubat, White, Smack, Putih, Medicine, Junk, Powder)");
            desc5_8.add("Inhalants (glue‐sniffing, solvent abuse, solvent inhalation, solvent sniffing)");
            desc5_8.add("Ketamine (K, Kit Kat, Special K, Vitamin K)");
            desc5_8.add("Kratom (Ketum, Thom, Ithang, Biak Biak, Kakuam)");
            desc5_8.add("Lysergide LSD (Acid, Trips, Blotters, Tabs, Stamp, Black Sesame, Seed, Micro, Micro Dot)");
            desc5_8.add("Mephedrone (Bubbles, mcat, Snow, Meow)");
            desc5_8.add("Methamphetamine (Yaba, Ice, Speed, Glass, Crystal, Quartz, Ice Cream, Hirropon, Shabu, Syabu)");
            desc5_8.add("New Psychoactive Substances (NPS) (Spice, K2, Bath Salts, Kronic, Bromo‐Dragonfly)");
            desc5_8.add("Nimetazepam (Erimin‐5)");
            desc5_8.add("Misuse of prescription of medication (e.g., painkillers, cough medication, Ritalin etc.)");

            List<String> quesToConsider5_8 = new ArrayList<>();
            quesToConsider5_8.add("Has the child ever "
                    + "experimented with or "
                    + "abused alcohol, drugs or "
                    + "inhalants?");
            quesToConsider5_8.add("Has anyone suspected "
                    + "that the child may be "
                    + "using alcohol and/or "
                    + "drugs?");

            Map<Integer, String> rating5_8 = new HashMap<>();
            rating5_8.put(0, "No evidence of substance use problems");
            rating5_8.put(1, "History of substance use problems which have been addressed; "
                    + "OR "
                    + "Suspicion of substance use problems. ");
            rating5_8.put(2, "Substance use problems that may or may not interfere with "
                    + "functioning; "
                    + "OR "
                    + "History of substance use problems which has not been addressed "
                    + "and/or the child is highly vulnerable to a relapse");
            rating5_8.put(3, "Substance dependence that consistently impairsthe child’s ability "
                    + "to function in one of these areas: in school, with friends or at "
                    + "home. Child requires detoxification or is addicted to alcohol and/or "
                    + "drugs. ");

            q5_8.setQuestionDescription(desc5_8);
            q5_8.setQuestionToConsider(quesToConsider5_8);
            q5_8.setRatingsDefinition(rating5_8);

            ques5child.add(q5_8);

            //create 5.9
            MainQuestionEntity q5_9 = new MainQuestionEntity("5.9", "Psychotic Symptoms");
            List<String> desc5_9 = new ArrayList<>();
            desc5_9.add("Thisitem rates the psychotic symptoms that characterise psychiatric disorders "
                    + "such as Schizophrenia and bipolar disorder.");
            desc5_9.add("The primary symptoms of psychosis include:");
            desc5_9.add("Hallucinations (experiencing things that others do not experience");
            desc5_9.add("Delusions (a false belief based on an incorrect inference about reality that is firmly sustained despite "
                    + "the fact that nearly everybody thinks the belief is false or proof of its inaccuracy exists)");
            desc5_9.add("Strange speech");
            desc5_9.add("Bizarre/idiosyncratic behaviour");

            List<String> quesToConsider5_9 = new ArrayList<>();
            quesToConsider5_9.add("Has the child ever "
                    + "talked about hearing, "
                    + "seeing or feeling "
                    + "something that was not "
                    + "actually there?");
            quesToConsider5_9.add("Has the child ever done "
                    + "any strange or bizarre "
                    + "things that made no "
                    + "sense?");
            quesToConsider5_9.add("Does the child have "
                    + "strange beliefs about "
                    + "things?");

            Map<Integer, String> rating5_9 = new HashMap<>();
            rating5_9.put(0, "No evidence of psychotic symptoms.");
            rating5_9.put(1, "Child has a history, suspicion or mild disturbance in thought "
                    + "processes or content, hallucination, delusion or bizarre behaviour "
                    + "that might be associated with some form of psychosis.  ");
            rating5_9.put(2, "Child has moderate disturbance in thought processes or content, "
                    + "hallucination, delusion, or bizarre behaviour. The child may be "
                    + "somewhat delusional or have brief or intermittent hallucinations. "
                    + "The child’s speech may be at times quite tangential or illogical.");
            rating5_9.put(3, "Child has severe psychosis. There is evidence of ongoing "
                    + "dangerous hallucination, delusion, bizarre behaviour or a "
                    + "combination of 2 or 3 of them. Psychotic symptoms may place the "
                    + "child or others at risk of physical harm");

            q5_9.setQuestionDescription(desc5_9);
            q5_9.setQuestionToConsider(quesToConsider5_9);
            q5_9.setRatingsDefinition(rating5_9);

            ques5child.add(q5_9);

            //create 5.10
            MainQuestionEntity q5_10 = new MainQuestionEntity("5.10", "Depression");
            List<String> desc5_10 = new ArrayList<>();
            desc5_10.add("This item rates the symptoms of depression.");
            desc5_10.add("Symptoms of depression include:");
            desc5_10.add("Persistent and irritable mood");
            desc5_10.add("Loss of interest or pleasure in activities once enjoyed");
            desc5_10.add("Difficulty sleeping or oversleeping");
            desc5_10.add("Significant change in appetite");
            desc5_10.add("Loss of energy ");
            desc5_10.add("Difficulties concentrating");
            desc5_10.add("The main difference between symptoms of depression in children and those in adults is that children are "
                    + "more likely to exhibit irritability rather than depressed mood");

            List<String> quesToConsider5_10 = new ArrayList<>();
            quesToConsider5_10.add("Does the child seem sad "
                    + "or irritable?");
            quesToConsider5_10.add("Does the child seem to "
                    + "have less interest in the "
                    + "activities he/she used to "
                    + "enjoy?");
            quesToConsider5_10.add("Does the child seem "
                    + "withdrawn or lonely?");

            Map<Integer, String> rating5_10 = new HashMap<>();
            rating5_10.put(0, "No evidence of depression. ");
            rating5_10.put(1, "History, suspicion or mild depression with minimal impact on the "
                    + "child’s functioning in school, with friends or at home ");
            rating5_10.put(2, "Child has moderate depression. Child has either very low mood or "
                    + "is very irritable.  Depression hassignificant impact on child’s ability "
                    + "to function in school, with friends, or at home.");
            rating5_10.put(3, "Child has severe depression. "
                    + "For example, child stays at home or in bed all day due to "
                    + "depression. Child’s emotional symptoms may prevent any "
                    + "participation in school, with friends, or at home.");

            q5_10.setQuestionDescription(desc5_10);
            q5_10.setQuestionToConsider(quesToConsider5_10);
            q5_10.setRatingsDefinition(rating5_10);

            ques5child.add(q5_10);

            //create 5.11
            MainQuestionEntity q5_11 = new MainQuestionEntity("5.11", "Eating Disturbance");
            List<String> desc5_11 = new ArrayList<>();
            desc5_11.add("This item rates any problems with eating, which include eating disorders such "
                    + "as Anorexia and Bulimia, food hoarding, food gorging/excessive over‐eating, obesity, picky eating (if "
                    + "it results in weight loss or malnutrition), disturbances in body image and refusal to maintain normal "
                    + "weight.");

            List<String> quesToConsider5_11 = new ArrayList<>();
            quesToConsider5_11.add("Does the child have any "
                    + "problems with eating?");
            quesToConsider5_11.add("Does he/she refuse to "
                    + "eat, eat too much "
                    + "(binge) or hoard food?");
            quesToConsider5_11.add("Has the child ever been "
                    + "hospitalised/received "
                    + "medical treatment for "
                    + "eating‐related issues?");
            quesToConsider5_11.add("Does he/she seem "
                    + "overly concerned about "
                    + "his/her weight?");

            Map<Integer, String> rating5_11 = new HashMap<>();
            rating5_11.put(0, "No evidence of eating disturbance.");
            rating5_11.put(1, "There is a history, suspicion or mild level of eating disturbance. "
                    + "This could include occasional preoccupation with weight, calorie "
                    + "intake, or body size ortype when of normal weight or underweight. ");
            rating5_11.put(2, "Moderate level of eating disturbance. "
                    + "This could include a more intense preoccupation with weight gain "
                    + "or becoming fat when underweight, restrictive eating habits or "
                    + "excessive exercising in order to maintain below normal weight, "
                    + "and/or emaciated body appearance. This could also include more "
                    + "notable binge eating episodes that are followed by compensatory "
                    + "behaviours in order to prevent weight gain (e.g., vomiting, use of "
                    + "laxatives, excessive exercising). Food hoarding would also be rated "
                    + "here");
            rating5_11.put(3, "Severe level of eating disturbance that is disabling. "
                    + "This could include significantly low weight where hospitalisation is "
                    + "required or excessive binge‐purge behaviours (at least once per "
                    + "day).");

            q5_11.setQuestionDescription(desc5_11);
            q5_11.setQuestionToConsider(quesToConsider5_11);
            q5_11.setRatingsDefinition(rating5_11);

            ques5child.add(q5_11);

            //create 5.12
            MainQuestionEntity q5_12 = new MainQuestionEntity("5.12", "Anger Control");
            List<String> desc5_12 = new ArrayList<>();
            desc5_12.add("This item rates the child’s ability to identify and manage his/her anger when "
                    + "frustrated");

            List<String> quesToConsider5_12 = new ArrayList<>();
            quesToConsider5_12.add("Is the child generally able "
                    + "to manage his/her anger?");
            quesToConsider5_12.add("Does the child get upset "
                    + "or frustrated easily?");
            quesToConsider5_12.add("Has the child ever hit "
                    + "anyone (used physical "
                    + "aggression) when angry?");
            quesToConsider5_12.add("Does the child react badly "
                    + "if someone criticises or "
                    + "rejects him/her?");

            Map<Integer, String> rating5_12 = new HashMap<>();
            rating5_12.put(0, "No evidence of problems with anger control.");
            rating5_12.put(1, "History, suspicion or mild problems with anger control. "
                    + "Child may sometimes become verbally or physically aggressive "
                    + "when frustrated.   Peers and family may be aware of and may "
                    + "attempt to avoid stimulating angry outbursts.  ");
            rating5_12.put(2, "Moderate problems with anger control. "
                    + "Child’s temper may have gotten him/her in significant trouble "
                    + "with peers, family and/or in school.  Anger may be associated "
                    + "with physical violence. Others are likely aware of anger "
                    + "potential.");
            rating5_12.put(3, "Severe problems with anger control. "
                    + "Child’s temper is likely associated with frequent fighting that is "
                    + "often physical. Others likely fear him/her.");

            q5_12.setQuestionDescription(desc5_12);
            q5_12.setQuestionToConsider(quesToConsider5_12);
            q5_12.setRatingsDefinition(rating5_12);

            ques5child.add(q5_12);

            AgeGroupEntity age10 = ageGroupSessionBean.createNewAgeGroupForDomain(benchild, domain5.getDomainId());
            for (MainQuestionEntity questions : ques5child) {
                questionsSessionBean.createMainQuestionForAgeGroup(age10.getAgeGroupId(), questions);
            }

            // Create Child Risk Behaviours Domain
            DomainEntity crb = new DomainEntity("Child Risk Behaviours", null, false, 1, false);
            AgeGroupEntity crball = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques6 = new ArrayList<>();

            //create 6.1
            MainQuestionEntity q6_1 = new MainQuestionEntity("6.1", "Traumatic Events");
            List<String> desc6_1 = new ArrayList<>();
            desc6_1.add("This item rates the child’s history and/or current trauma experience/s, which\n"
                    + "includes the risk of abuse/neglect.");
            desc6_1.add("Traumatic events are the child’s direct experience/witnessing of, or confrontation with an event/s that\n"
                    + "involve actual or threatened death orserious injury to the child or others, or a threat to the psychological\n"
                    + "or physical integrity of the child or others.  ");
            desc6_1.add("When rating this item, consider if the child has experienced or is experiencing ANY of the following");
            desc6_1.add("Neglect: Caregiver is/was emotionally and/or physically unavailable to the infant after birth, child is\n"
                    + "being left alone without adult supervision, caregiver fails to meet basic needs of the child");
            desc6_1.add("Physical Abuse: Child is physically harmed by the caregiver.");
            desc6_1.add("Emotional abuse: Child experiences repeated pattern of caregiver behaviour or extreme incident(s)\n"
                    + "that convey to him/her that he/she is worthless, unloved, unwanted, endangered, etc.");
            desc6_1.add("Sexual Abuse: Child is a victim of sexual abuse, which can include both touching and non‐touching\n"
                    + "behaviours");
            desc6_1.add("Witness to Domestic Violence: Child witnessed violence (e.g., spousal violence or sibling being\n"
                    + "physically abused) within the household or family.");
            desc6_1.add("Witness/Victim to Criminal Activity: Child is exposed to/victim of criminal activity (e.g., drug‐taking,\n"
                    + "prostitution, assault, illegal vices)");
            desc6_1.add("Disruptions in Caregiving: Child experiences disruptions in caregiving involving separation from\n"
                    + "primary attachment figure(s) and/or attachment losses (e.g., placement in out‐of‐home care)");
            desc6_1.add("Other Significant Trauma: Child experiences other kinds of trauma (e.g., medical trauma, natural\n"
                    + "disasters, car accidents)");

            List<String> quesToConsider6_1 = new ArrayList<>();
            quesToConsider6_1.add("Has the child ever "
                    + "experienced a "
                    + "traumatic event?");
            quesToConsider6_1.add("Are there any "
                    + "allegations/evidence "
                    + "that the child has "
                    + "been or is currently "
                    + "being "
                    + "abused/neglected?");

            Map<Integer, String> rating6_1 = new HashMap<>();
            rating6_1.put(0, "There is no evidence that child has experienced any traumatic event/s.");
            rating6_1.put(1, "Child has experienced traumatic event/s in the past but there is no "
                    + "evidence that child is currently experiencing any traumatic event/s; "
                    + "OR "
                    + "There is suspicion that the child is currently experiencing traumatic "
                    + "event/s.  ");
            rating6_1.put(2, "Child has experienced traumatic event/s in the past, and there are "
                    + "some signs that the child is currently experiencing traumatic event/s "
                    + "or is still at risk of experiencing traumatic event/s");
            rating6_1.put(3, "There is clear evidence that the child is currently experiencing "
                    + "traumatic event/s.");

            q6_1.setQuestionDescription(desc6_1);
            q6_1.setQuestionToConsider(quesToConsider6_1);
            q6_1.setRatingsDefinition(rating6_1);

            ques6.add(q6_1);

            //create 6.2
            MainQuestionEntity q6_2 = new MainQuestionEntity("6.2", "Sexually Reactive Behaviour");
            List<String> desc6_2 = new ArrayList<>();
            desc6_2.add("This item refers to sexual behaviour* that may lead to victimisation, "
                    + "or sexually “acting out” that is designed to get attention or a response from others.");
            desc6_2.add("Examples of sexually reactive behaviour include: ");
            desc6_2.add("Touching/engaging in sexual play with a similar‐aged or younger child (more than children his/her "
                    + "age)");
            desc6_2.add("Using sexualised language or gestures to a significantly greater extent than same‐aged peers");
            desc6_2.add("Sexually provocative behaviours");
            desc6_2.add("Sexually Reactive Behaviour is not considered Sexual Aggression (item 3 in the “Other Risk Behaviours "
                    + "Supplementary Module) unless the threat or use of force is involved");
            desc6_2.add("If you have any difficulty in deciding whether an observed behaviour is within the norm for the "
                    + "developmental stage of the child, please consult a paediatrician or relevant mental health professional "
                    + "for further clarification.");

            List<String> quesToConsider6_2 = new ArrayList<>();
            quesToConsider6_2.add("Has the child ever "
                    + "engaged in sexually "
                    + "inappropriate/provocative "
                    + "behaviour?");
            quesToConsider6_2.add("Do you think that the "
                    + "child may be imitating "
                    + "sexually inappropriate "
                    + "behaviour he/she may "
                    + "have witnessed or "
                    + "experienced in the past?");
            quesToConsider6_2.add("Is the child at risk of "
                    + "moral danger?");

            Map<Integer, String> rating6_2 = new HashMap<>();
            rating6_2.put(0, "No evidence of problems with sexually reactive behaviour");
            rating6_2.put(1, "History, suspicion or mild level of sexually reactive behaviour. "
                    + "For example, child may exhibit occasional inappropriate sexual "
                    + "language or behaviour (e.g., flirts when age‐inappropriate). A "
                    + "youth above 16 years old who engages in unprotected sex with a "
                    + "single partner will be rated here.");
            rating6_2.put(2, "Moderate problems with sexually reactive behaviour that places "
                    + "child at some risk within the past year. "
                    + "For example, child may exhibit more frequent sexually "
                    + "provocative behaviours in a manner that impairs functioning, "
                    + "engage in promiscuous sexual behaviours and/or have "
                    + "unprotected sex with multiple partners");
            rating6_2.put(3, "Significant problems with sexually reactive behaviours. "
                    + "For example, child exhibits sexual behaviours that place child or "
                    + "others at immediate risk");

            q6_2.setQuestionDescription(desc6_2);
            q6_2.setQuestionToConsider(quesToConsider6_2);
            q6_2.setRatingsDefinition(rating6_2);

            ques6.add(q6_2);

            DomainEntity domain6 = domainSessionBean.createNewDomain(crb);
            AgeGroupEntity age11 = ageGroupSessionBean.createNewAgeGroupForDomain(crball, domain6.getDomainId());
            for (MainQuestionEntity questions : ques6) {
                questionsSessionBean.createMainQuestionForAgeGroup(age11.getAgeGroupId(), questions);
            }

            AgeGroupEntity crbadol = new AgeGroupEntity("0-6");
            List<MainQuestionEntity> ques6adol = new ArrayList<>();

            //create 6.3
            MainQuestionEntity q6_3 = new MainQuestionEntity("6.3", "Self-Injury");
            List<String> desc6_3 = new ArrayList<>();
            desc6_3.add("This item is used to describe repetitive behaviour that results in physical injury to the child.");
            desc6_3.add("These behaviours are generally done for purposes of self‐soothing or emotional self‐regulation.");
            desc6_3.add("Some forms of self‐injury include — face‐slapping, head‐banging, hair‐pulling, pinching, and biting.");
            desc6_3.add("Take into account whether a caregiver can stop child from engaging in these behaviours, for example a "
                    + "rating of “2” indicates a child who harms him/herself and the caregiver is not able to stop it.");

            List<String> quesToConsider6_3 = new ArrayList<>();
            quesToConsider6_3.add("Has the child’s parent/teacher/caseworker ever "
                    + "noticed bruises, cuts or marks on the child that "
                    + "he/she could not explain?");
            quesToConsider6_3.add("Has the child ever hurt him/herself on purpose "
                    + "(e.g., pinching self, pulling own hair)? \n"
                    + "➢ If yes, when was the last time the child hurt "
                    + "him/herself?");
            quesToConsider6_3.add("Is the caregiver able to stop this behaviour "
                    + "when it occurs?");
            quesToConsider6_3.add("If within the last 30 days:  was medical "
                    + "attention required?");

            Map<Integer, String> rating6_3 = new HashMap<>();
            rating6_3.put(0, "No evidence of self‐injury.");
            rating6_3.put(1, "History, suspicion or a mild level of self‐"
                    + "injury. Caregiver is able to stop child from\n"
                    + "engaging in self‐injury");
            rating6_3.put(2, "Moderate level ofself­injury such as head‐"
                    + "banging, which interferes with the child’s\n"
                    + "functioning. Caregiver is unable to stop\n"
                    + "child from engaging in self‐injury.  ");
            rating6_3.put(3, "Severe level of self­injury that puts the "
                    + "child’s safety and well‐being at risk.");

            q6_3.setQuestionDescription(desc6_3);
            q6_3.setQuestionToConsider(quesToConsider6_3);
            q6_3.setRatingsDefinition(rating6_3);

            ques6adol.add(q6_3);

            AgeGroupEntity age12 = ageGroupSessionBean.createNewAgeGroupForDomain(crbadol, domain6.getDomainId());
            for (MainQuestionEntity questions : ques6adol) {
                questionsSessionBean.createMainQuestionForAgeGroup(age12.getAgeGroupId(), questions);
            }

            AgeGroupEntity crbteen = new AgeGroupEntity("7+");
            List<MainQuestionEntity> ques6teen = new ArrayList<>();

            //create 6.4
            MainQuestionEntity q6_4 = new MainQuestionEntity("6.4", "Suicide Risk");
            List<String> desc6_4 = new ArrayList<>();
            desc6_4.add("This item rates the presence of suicidal behaviour i.e. thinking about or attempting to kill "
                    + "oneself");

            List<String> quesToConsider6_4 = new ArrayList<>();
            quesToConsider6_4.add("Has the child ever "
                    + "attempted to commit "
                    + "suicide?");
            quesToConsider6_4.add("Has the child ever talked "
                    + "about wanting to kill "
                    + "him/herself?");

            Map<Integer, String> rating6_4 = new HashMap<>();
            rating6_4.put(0, "No evidence of suicidal ideation or behaviours. ");
            rating6_4.put(1, "History ofsuicidal ideation or behaviours but none during the past "
                    + "30 days.");
            rating6_4.put(2, "History ofsuicidal ideation or behaviours but none during the past "
                    + "30 days.");
            rating6_4.put(3, "Current suicidal thoughts and attempts in the past 24 hours.");

            q6_4.setQuestionDescription(desc6_4);
            q6_4.setQuestionToConsider(quesToConsider6_4);
            q6_4.setRatingsDefinition(rating6_4);

            ques6teen.add(q6_4);

            //create 6.5
            MainQuestionEntity q6_5 = new MainQuestionEntity("6.5", "Self-Harm");
            List<String> desc6_5 = new ArrayList<>();
            desc6_5.add("This includes repetitive, physically harmful behavioursthat generally serve a coping or self‐ "
                    + "soothing function to the child.");
            desc6_5.add("There are five major forms of self‐harm: cutting, burning, face slapping, head banging and hair pulling.");
            desc6_5.add("Self‐harm actions do not necessarily constitute suicidal behaviour");

            List<String> quesToConsider6_5 = new ArrayList<>();
            quesToConsider6_5.add("Has the child’s "
                    + "parent/teacher/caseworker ever "
                    + "noticed cuts or burns on the child "
                    + "that he/she could not explain?");
            quesToConsider6_5.add("Has the child ever hurt him/herself "
                    + "on purpose (e.g., cutting)?\n"
                    + "➢ If yes, when was the last time "
                    + "the child hurt him/herself?\n"
                    + "➢ If within the last 30 days, was "
                    + "medical attention required?");

            Map<Integer, String> rating6_5 = new HashMap<>();
            rating6_5.put(0, "No evidence of self‐harm");
            rating6_5.put(1, "History of self‐harm, but no self­harm behaviours "
                    + "within the last 30 days.");
            rating6_5.put(2, "Engaged in 1 incident of self­harm within the last 30 "
                    + "days that did not require medical attention (e.g., "
                    + "hospitalisation, going to a doctor).");
            rating6_5.put(3, "Engaged in multiple incidents of self­harm within the "
                    + "last 30 days and/or engaged in at least 1 incident of "
                    + "self­harm that required medical attention (e.g., "
                    + "hospitalisation, going to a doctor).");

            q6_5.setQuestionDescription(desc6_5);
            q6_5.setQuestionToConsider(quesToConsider6_5);
            q6_5.setRatingsDefinition(rating6_5);

            ques6teen.add(q6_5);

            //create 6.6
            MainQuestionEntity q6_6 = new MainQuestionEntity("6.6", "Runaway");
            List<String> desc6_6 = new ArrayList<>();
            desc6_6.add("This item describes the risk of, or actual runaway behaviour");
            desc6_6.add("Rate this item even if child attempts to run away but is caught before successfully running away. ");
            desc6_6.add("If the child/youth is in a secure placement (e.g., Singapore Boys’/Girls’ Home) and does not have the "
                    + "opportunity to run away, rate the child’s behaviour during home leave or the child’slast known runaway "
                    + "behaviour");

            List<String> quesToConsider6_6 = new ArrayList<>();
            quesToConsider6_6.add("Has the child ever run "
                    + "away from school, home "
                    + "or other places? \n"
                    + "• If yes, when was the "
                    + "last time the child "
                    + "ran away?");
            quesToConsider6_6.add("Has the child ever "
                    + "threatened to run away?");

            Map<Integer, String> rating6_6 = new HashMap<>();
            rating6_6.put(0, "No evidence of runaway behaviours");
            rating6_6.put(1, "History of running away from home or other settings. Attempts of "
                    + "or thoughts about running away or running away for a few hours "
                    + "or 1 overnight absence more than 30 days ago would be rated "
                    + "here. ");
            rating6_6.put(2, "Recent runaway behaviour or thoughts in the past 7 to 30 days");
            rating6_6.put(3, "Child is at risk of running away as shown by recent runaway "
                    + "attempts within the past 7 days or child is currently a runaway.");

            q6_6.setQuestionDescription(desc6_6);
            q6_6.setQuestionToConsider(quesToConsider6_6);
            q6_6.setRatingsDefinition(rating6_6);

            ques6teen.add(q6_6);

            //create 6.7
            MainQuestionEntity q6_7 = new MainQuestionEntity("6.7", "Delinquent Behaviour");
            List<String> desc6_7 = new ArrayList<>();
            desc6_7.add("This relates to delinquent behaviour which may result in a child being "
                    + "involved with the police and/or legal system (e.g., any behaviour that can get the child arrested).");
            desc6_7.add("Examples of Delinquent Behaviour:\n"
                    + "• Theft\n"
                    + "• Under‐age drinking\n"
                    + "• Under‐age smoking\n"
                    + "• Rioting\n"
                    + "• Involvement in gang activity\n"
                    + "• Vandalism\n"
                    + "• House‐breaking");
            desc6_7.add("Rate the child’s history or current delinquent behaviour/s, even if he/she was not caught or charged for "
                    + "it.");
            desc6_7.add("This item also includes a child who is under probation or Family Guidance Order.");

            List<String> quesToConsider6_7 = new ArrayList<>();
            quesToConsider6_7.add("Has the child ever been "
                    + "arrested?");
            quesToConsider6_7.add("Do you know of any laws "
                    + "that the child may have "
                    + "broken (regardless of "
                    + "whether the child was "
                    + "charged)?");
            quesToConsider6_7.add("Has the child been "
                    + "involved in any "
                    + "delinquent activities?");

            Map<Integer, String> rating6_7 = new HashMap<>();
            rating6_7.put(0, "No evidence of delinquency. ");
            rating6_7.put(1, "History of delinquency but no acts of delinquency in past 30 days");
            rating6_7.put(2, "History of delinquency but no acts of delinquency in past 30 days");
            rating6_7.put(3, "Severe acts of delinquency that place others at risk of significant "
                    + "loss or injury or place child at risk of being charged within the last "
                    + "30 days. "
                    + "For example, serious theft, gang‐related activities and loan‐ "
                    + "sharking would be rated here.");

            q6_7.setQuestionDescription(desc6_7);
            q6_7.setQuestionToConsider(quesToConsider6_7);
            q6_7.setRatingsDefinition(rating6_7);

            ques6teen.add(q6_7);

            //create 6.8
            MainQuestionEntity q6_8 = new MainQuestionEntity("6.8", "Bullying");
            List<String> desc6_8 = new ArrayList<>();
            desc6_8.add("This item rates whether the child has engaged in physical, verbal, emotional or cyber‐bullying "
                    + "of peers within the school or community setting.");
            desc6_8.add("Please take into consideration the severity, frequency and impact of the bullying upon the victim");

            List<String> quesToConsider6_8 = new ArrayList<>();
            quesToConsider6_8.add("Has the child ever picked "
                    + "on, made fun of and/or "
                    + "harassed or intimidated "
                    + "another child?\n"
                    + "➢ If yes, how often "
                    + "does this occur?\n"
                    + "➢ Is the bullying done "
                    + "alone or in a group?");
            quesToConsider6_8.add("Does the child hang out "
                    + "with other children who "
                    + "are known as bullies?");
            quesToConsider6_8.add("➢ Is there any suspicion "
                    + "that the child may be a "
                    + "bully?");

            Map<Integer, String> rating6_8 = new HashMap<>();
            rating6_8.put(0, "No evidence of bullying behaviour");
            rating6_8.put(1, "History of bullying behaviour; "
                    + "AND/OR "
                    + "Child has been involved with groups that have bullied other "
                    + "child/children either in school or the community; however, child "
                    + "has not had a leadership role in these groups; "
                    + "AND/OR "
                    + "Child individually engages in acts of mild bullying towards another "
                    + "child/children (e.g. occasional name‐calling).");
            rating6_8.put(2, "Child has led a group that bullied another child/children. "
                    + "AND/OR "
                    + "Child individually engages in acts of moderate bullying towards "
                    + "another child/children.");
            rating6_8.put(3, "Child has utilised threats or actual violence to bully another "
                    + "child/children in school and/or community repeatedly.");

            q6_8.setQuestionDescription(desc6_8);
            q6_8.setQuestionToConsider(quesToConsider6_8);
            q6_8.setRatingsDefinition(rating6_8);

            ques6teen.add(q6_8);

            //create 6.9
            MainQuestionEntity q6_9 = new MainQuestionEntity("6.9", "Victim of Bullying");
            List<String> desc6_9 = new ArrayList<>();
            desc6_9.add("This item rates the extent a child or youth has been a victim of bullying from "
                    + "his/her peers or other individuals");
            desc6_9.add("If there is a strong indication from other sources that the child has been bullied before, a “1” should be "
                    + "rated even if the child did not report any history of being bullied.");

            List<String> quesToConsider6_9 = new ArrayList<>();
            quesToConsider6_9.add("Has the child ever been "
                    + "bullied?");
            quesToConsider6_9.add("Has the child ever been "
                    + "physically harmed or "
                    + "threatened when "
                    + "bullied?");

            Map<Integer, String> rating6_9 = new HashMap<>();
            rating6_9.put(0, "No evidence that the child has been bullied");
            rating6_9.put(1, "History or suspicion that child has been bullied at school, in the "
                    + "community or at home.");
            rating6_9.put(2, "History or suspicion that child has been bullied at school, in the "
                    + "community or at home.");
            rating6_9.put(3, "Clear evidence that child is frequently and/or severely bullied at "
                    + "school, in the community or at home. Child may be threatened or "
                    + "physically harmed");

            q6_9.setQuestionDescription(desc6_9);
            q6_9.setQuestionToConsider(quesToConsider6_9);
            q6_9.setRatingsDefinition(rating6_9);

            ques6teen.add(q6_9);

            //create 6.10
            MainQuestionEntity q6_10 = new MainQuestionEntity("6.10", "Other Risk Behaviours");
            List<String> desc6_10 = new ArrayList<>();
            desc6_10.add("This item rates other risk behaviours not captured by the previous items.");
            desc6_10.add("Other Risk­Taking Behaviours: Child engaged in behaviour other than suicide orself‐harm that places "
                    + "him/her in danger of physical harm (e.g., dangerous dares, running across a busy road for thrills, "
                    + "unregulated parkour, having unprotected sex with multiple partners).");
            desc6_10.add("Danger to Others: Child has violent thoughts or behaviours and/or intent to physically harm others ");
            desc6_10.add("Sexual Aggression: Child engaged in sexual behaviour with the use of force or threat and/or takes "
                    + "advantage of a more vulnerable child through seduction, coercion or force.");
            desc6_10.add("Sanction­Seeking Behaviour: Child is intentionally misbehaving in order to force adults to "
                    + "sanction/punish him/her");

            List<String> quesToConsider6_10 = new ArrayList<>();
            quesToConsider6_10.add("Has the child ever engaged in any activity or acted "
                    + "in a way that might be dangerous to him/her?");
            quesToConsider6_10.add("Has the child ever injured anyone on purpose?");
            quesToConsider6_10.add("Has the child ever been sexually aggressive to "
                    + "another child?\n"
                    + "➢ If yes, has the child ever targeted younger "
                    + "children or e.g., children with low IQ?  ");
            quesToConsider6_10.add("Does the child seem to purposely get into trouble to "
                    + "gain attention or make adults angry in order to get "
                    + "punished?");

            Map<Integer, String> rating6_10 = new HashMap<>();
            rating6_10.put(0, "No evidence of other risk behaviours");
            rating6_10.put(1, "Mild problems with other risk "
                    + "behaviours.");
            rating6_10.put(2, "Moderate problems with other risk "
                    + "behaviours.");
            rating6_10.put(3, "Severe problems with other risk "
                    + "behaviours.");

            q6_10.setQuestionDescription(desc6_10);
            q6_10.setQuestionToConsider(quesToConsider6_10);
            q6_10.setRatingsDefinition(rating6_10);

            ques6teen.add(q6_10);

            AgeGroupEntity age13 = ageGroupSessionBean.createNewAgeGroupForDomain(crbteen, domain6.getDomainId());
            for (MainQuestionEntity questions : ques6teen) {
                questionsSessionBean.createMainQuestionForAgeGroup(age13.getAgeGroupId(), questions);
            }

            // Create Strengths Domain
            List<String> descriptionStrength = new ArrayList<>();
            descriptionStrength.add("Strengths are rated differently from the rest of the items of the CANS.");
            descriptionStrength.add("This rating scale can help you think about the ratings in terms of actions:");
            descriptionStrength.add("A “0” would indicate that this is a significant and functional strength that could become the centrepiece in "
                    + "service planning. For example, a child with a significant interest and involvement in games, sports or dance "
                    + "activities and who feels good about his/her involvement, can have action plans to ensure that these activities "
                    + "are part of his/her schedule in order to gainfully occupy the child and create chances for the child to develop "
                    + "his/her talent");
            descriptionStrength.add("A “1” would indicate that this is a useful strength that could become part of the service plan");
            descriptionStrength.add("A “2” would indicate that a potentialstrength has been identified but requires development to become useful "
                    + "to the child. For example, a child who showssome curiosity but hesitant to explore.  A plan could be developed "
                    + "for encouragement of the child to explore item/situation that induces his/her curiosit");
            descriptionStrength.add("A “3” would indicate that no strength has been identified at this time. A rating at this level would suggest "
                    + "that, in this area, the focus would be on identifying and building strengthsthat can become useful to the child. "
                    + "For example, a planning focus may be to work with a child without interpersonal skills to begin identifying "
                    + "possible areas of interest for the child");
            descriptionStrength.add("Remember that strengths are NOT the opposite of needs. "
                    + "Increasing a child’s strengths, while addressing his/her needs, can lead to better functioning and outcomes in "
                    + "the child, as opposed to only focusing on the needs. Identifying areas where strengths can be built is an "
                    + "important element of service planning");

            DomainEntity strength = new DomainEntity("Strength", descriptionStrength, false, 1, false);
            AgeGroupEntity strengthall = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques7 = new ArrayList<>();

            //create 7.1
            MainQuestionEntity q7_1 = new MainQuestionEntity("7.1", "Family Relationships");
            List<String> desc7_1 = new ArrayList<>();
            desc7_1.add("This item refers to the presence of a sense of family identity, love and "
                    + "communication among family members. Even families who are struggling often have a firm foundation "
                    + "that consists of a positive sense of family and strong underlying love and commitment to each other.  ");
            desc7_1.add("Similar to Relationship with Family Members (Item 2.1), the definition of family comes from the child’s "
                    + "perspective (i.e. who the child describes as his/her family). If this information is not known, we "
                    + "recommend a definition of family that includes biological/adoptive relatives and their significant others "
                    + "with whom the child is still in contact.");
            desc7_1.add("Relationships with extended family members such as grandparents, aunts, uncles or cousins can be "
                    + "considered.");
            desc7_1.add("Foster parents can also be considered if the child is residing with foster parents who are going to assume "
                    + "long‐term care of the child. ");
            desc7_1.add("Even a single family member who is supportive of the child and that the child feels comfortable to go to "
                    + "for support, can be considered as a centerpiece strength and rated “0”.");

            List<String> quesToConsider7_1 = new ArrayList<>();
            quesToConsider7_1.add("How does the child get "
                    + "along with family "
                    + "members in the "
                    + "household?");
            quesToConsider7_1.add("Is there a family member "
                    + "that the child can go to in "
                    + "times of need for "
                    + "support?");

            Map<Integer, String> rating7_1 = new HashMap<>();
            rating7_1.put(0, "Significant Strength : Family has strong relationships, good communication and has "
                    + "much love and respect for one another. Child is fully included "
                    + "in family activities. \n"
                    + "OR "
                    + "There is at least one family member who has a strong loving "
                    + "relationship with the child and is able to provide significant "
                    + "emotional or concrete support. ");
            rating7_1.put(1, "Useful Strength : Family has some good relationships and good "
                    + "communication, which may not be consistently "
                    + "demonstrated. Family members are able to enjoy each other’s "
                    + "company. "
                    + "OR "
                    + "There is at least one family member who has a good, loving "
                    + "relationship with the child and is able to provide limited "
                    + "emotional or concrete support. ");
            rating7_1.put(2, "Potential Strength : Family needs some assistance in developing relationships "
                    + "and/or communications. Family members are known, but "
                    + "currently none are able to provide emotional or concrete "
                    + "support. ");
            rating7_1.put(3, "No Strength Identified : No evidence of strength currently. Family needs significant "
                    + "assistance in developing relationships and communications. "
                    + "OR "
                    + "Child has no identified family. Child is not included in normal "
                    + "family activities");

            q7_1.setQuestionDescription(desc7_1);
            q7_1.setQuestionToConsider(quesToConsider7_1);
            q7_1.setRatingsDefinition(rating7_1);

            ques7.add(q7_1);

            //create 7.2
            MainQuestionEntity q7_2 = new MainQuestionEntity("7.2", "Interpersonal");
            List<String> desc7_2 = new ArrayList<>();
            desc7_2.add("This item rates a child’s social and relationship skills. A child exhibiting this strength "
                    + "may be more likely to have long‐standing relationship making and maintaining skills. Children who have "
                    + "this strength are also more likely to be able to express their needs and have their needs met.");
            desc7_2.add("This item is different from Social Emotional (Item 1.2) and Social Relationships (Item 2.6).");
            desc7_2.add("A child can have strong interpersonal skills but may have current conflicts with friends. This child would "
                    + "score a “0” on this item, reflecting this strength, but may have an actionable need on Social Emotional "
                    + "(Item 1.2) or Social Relationships (Item 2.6)");
            desc7_2.add("The child should be 2 years of age or older to rate this item");

            List<String> quesToConsider7_2 = new ArrayList<>();
            quesToConsider7_2.add("Does the child have the "
                    + "ability to make friends?");
            quesToConsider7_2.add("Do you feel that the child "
                    + "is pleasant and likeable?");
            quesToConsider7_2.add("Do other children/adults "
                    + "generally like him/her?");
            quesToConsider7_2.add("Is the child generally able "
                    + "to act appropriately in "
                    + "most social settings?");

            Map<Integer, String> rating7_2 = new HashMap<>();
            rating7_2.put(0, "Significant Strength : Child has well­developed interpersonal skills and "
                    + "healthy friendships.  ");
            rating7_2.put(1, "Useful Strength : Child has good interpersonal skills, and has shown "
                    + "the ability to develop healthy friendships.");
            rating7_2.put(2, "Potential Strength : Child has good interpersonal skills, and has shown "
                    + "the ability to develop healthy friendships.");
            rating7_2.put(3, "No Strength Identified : There is no evidence of observable interpersonal "
                    + "skills, and/or child needs significant help in "
                    + "developing interpersonal skills and healthy "
                    + "friendships.");
            rating7_2.put(4, "Child is less than 2 years of age.");

            q7_2.setQuestionDescription(desc7_2);
            q7_2.setQuestionToConsider(quesToConsider7_2);
            q7_2.setRatingsDefinition(rating7_2);

            ques7.add(q7_2);

            DomainEntity domain7 = domainSessionBean.createNewDomain(strength);
            AgeGroupEntity age14 = ageGroupSessionBean.createNewAgeGroupForDomain(strengthall, domain7.getDomainId());
            for (MainQuestionEntity questions : ques7) {
                questionsSessionBean.createMainQuestionForAgeGroup(age14.getAgeGroupId(), questions);
            }

            AgeGroupEntity strengthadol = new AgeGroupEntity("0-6");
            List<MainQuestionEntity> ques7adol = new ArrayList<>();

            //create 7.3
            MainQuestionEntity q7_3 = new MainQuestionEntity("7.3", "Adaptability");
            List<String> desc7_3 = new ArrayList<>();
            desc7_3.add("This item rates how well a child can adjust in times of transition.");
            desc7_3.add("Some children move from one environment or activity to another smoothly. Others struggle with any "
                    + "such changes.");
            desc7_3.add("A toddler who cries when transitioning from one activity to another but is able to make the transition "
                    + "with the support of his caregiver would be rated ‘1.’");

            List<String> quesToConsider7_3 = new ArrayList<>();
            quesToConsider7_3.add("In what ways does "
                    + "the child adjust to "
                    + "change? ");
            quesToConsider7_3.add("Does the caregiver "
                    + "need to provide "
                    + "support during "
                    + "transitions?");

            Map<Integer, String> rating7_3 = new HashMap<>();
            rating7_3.put(0, "Significant Strength : Child has the ability to adjust to changes and transitions\n"
                    + "very well. ");
            rating7_3.put(1, "Useful Strength : Child has the ability to adjust to changes and transitions\n"
                    + "with caregiver support.");
            rating7_3.put(2, "Potential Strength : Child has difficulties adjusting to changes and transitions\n"
                    + "even with caregiver support. ");
            rating7_3.put(3, "No Strength Identified : Most of the time, child has difficulties adjusting to changes\n"
                    + "and transitions. Adults are minimally able to impact child’s\n"
                    + "difficulties in this area.  ");

            q7_3.setQuestionDescription(desc7_3);
            q7_3.setQuestionToConsider(quesToConsider7_3);
            q7_3.setRatingsDefinition(rating7_3);

            ques7adol.add(q7_3);

            //create 7.4
            MainQuestionEntity q7_4 = new MainQuestionEntity("7.4", "Curiosity");
            List<String> desc7_4 = new ArrayList<>();
            desc7_4.add("This item describes the child’s self‐initiated efforts to discover his/her world.");

            List<String> quesToConsider7_4 = new ArrayList<>();
            quesToConsider7_4.add("In what ways does "
                    + "the child seek out "
                    + "objects and people "
                    + "when interested?");
            quesToConsider7_4.add("Does the infant "
                    + "look around when "
                    + "people are near, "
                    + "taking an interest "
                    + "in surroundings?");

            Map<Integer, String> rating7_4 = new HashMap<>();
            rating7_4.put(0, "Significant Strength : Child has exceptional curiosity."
                    + "Infant displays mouthing and banging of objects within "
                    + "grasp; older children crawl or walk to objects of interest.");
            rating7_4.put(1, "Useful Strength : Child has good curiosity. "
                    + "A toddler who does not approach interesting objects, but "
                    + "will actively explore them when presented to him/her, "
                    + "would be rated here.  ");
            rating7_4.put(2, "Potential Strength : Child has limited curiosity. "
                    + "Child may be hesitant to seek out new information or "
                    + "environments, or reluctant to explore presented objects.");
            rating7_4.put(3, "No Strength Identified : Child has very limited or no observable curiosity");

            q7_4.setQuestionDescription(desc7_4);
            q7_4.setQuestionToConsider(quesToConsider7_4);
            q7_4.setRatingsDefinition(rating7_4);

            ques7adol.add(q7_4);

            //create 7.5
            MainQuestionEntity q7_5 = new MainQuestionEntity("7.5", "Confidence");
            List<String> desc7_5 = new ArrayList<>();
            desc7_5.add("This item rates how well a child demonstrates his/her sense of mastery of activities");
            desc7_5.add("Typically, a child who interacts well with others and is able to demonstrate pride (e.g., a toddler who "
                    + "beams or claps for him/herself after completing a difficult task) will be rated as having this strength.");

            List<String> quesToConsider7_5 = new ArrayList<>();
            quesToConsider7_5.add("In what way does "
                    + "the child exhibit "
                    + "positive self‐regard?");
            quesToConsider7_5.add("Does the child need "
                    + "support in "
                    + "recognising his/her "
                    + "self‐esteem?");

            Map<Integer, String> rating7_5 = new HashMap<>();
            rating7_5.put(0, "Significant Strength : Child consistently demonstrates a significant level of self‐ "
                    + "confidence. This consistently supports the child in his/her "
                    + "development and functioning.");
            rating7_5.put(1, "Useful Strength : Child demonstrates a good level of confidence that is of "
                    + "benefit to the child");
            rating7_5.put(2, "Potential Strength : Child shows a mild level of ability in this area. Parents and "
                    + "caregivers are the main supporters of the child in this area "
                    + "and the child needs continued development for this to be a "
                    + "useful strength.");
            rating7_5.put(3, "No Strength Identified : There is no evidence of the child demonstrating confidence.");

            q7_5.setQuestionDescription(desc7_5);
            q7_5.setQuestionToConsider(quesToConsider7_5);
            q7_5.setRatingsDefinition(rating7_5);

            ques7adol.add(q7_5);

            AgeGroupEntity age15 = ageGroupSessionBean.createNewAgeGroupForDomain(strengthadol, domain7.getDomainId());
            for (MainQuestionEntity questions : ques7adol) {
                questionsSessionBean.createMainQuestionForAgeGroup(age15.getAgeGroupId(), questions);
            }

            AgeGroupEntity strengthteen = new AgeGroupEntity("7+");
            List<MainQuestionEntity> ques7teen = new ArrayList<>();

            //create 7.6
            MainQuestionEntity q7_6 = new MainQuestionEntity("7.6", "Optimism");
            List<String> desc7_6 = new ArrayList<>();
            desc7_6.add("This item rates the child’s sense of hope and future orientation.");

            List<String> quesToConsider7_6 = new ArrayList<>();
            quesToConsider7_6.add(" Does the child have "
                    + "a generally positive "
                    + "outlook on things?");
            quesToConsider7_6.add("Does he/she have "
                    + "plans for the "
                    + "future?");
            quesToConsider7_6.add("Is he/she forward‐ "
                    + "looking? Does "
                    + "he/she see "
                    + "him/herself as "
                    + "likely to be "
                    + "successful in the"
                    + "future?");

            Map<Integer, String> rating7_6 = new HashMap<>();
            rating7_6.put(0, "Significant Strength : Child has a strong and stable optimistic outlook on his/her "
                    + "life.");
            rating7_6.put(1, "Useful Strength : Child has a strong and stable optimistic outlook on his/her "
                    + "life. ");
            rating7_6.put(2, "Potential Strength : Child has difficulties maintaining a positive view of "
                    + "him/herself and his/her life. Child may be pessimistic.");
            rating7_6.put(3, "No Strength Identified : There is no evidence of optimism and/or child has "
                    + "difficulties seeing any positives about him/herself or "
                    + "his/her life.");

            q7_6.setQuestionDescription(desc7_6);
            q7_6.setQuestionToConsider(quesToConsider7_6);
            q7_6.setRatingsDefinition(rating7_6);

            ques7teen.add(q7_6);

            //create 7.7
            MainQuestionEntity q7_7 = new MainQuestionEntity("7.7", "Educational");
            List<String> desc7_7 = new ArrayList<>();
            desc7_7.add("This item rates the nature of the school’s relationship with the child and family, as well "
                    + "as the level of support the child receives from school.");
            desc7_7.add("A child who loves and excels at school would also be rated as having this strength");

            List<String> quesToConsider7_7 = new ArrayList<>();
            quesToConsider7_7.add(" Does the child’s "
                    + "school work closely "
                    + "with the "
                    + "parents/home in "
                    + "finding out how to "
                    + "best meet the child’s "
                    + "needs?");
            quesToConsider7_7.add("Is the school an "
                    + "advocate for the "
                    + "child?");
            quesToConsider7_7.add("Does the child like "
                    + "school?");
            quesToConsider7_7.add("Does the child excel "
                    + "in school?");

            Map<Integer, String> rating7_7 = new HashMap<>();
            rating7_7.put(0, "Significant Strength : Child is in school and involved with an educational plan that "
                    + "exceeds expectations. School works exceptionally well with "
                    + "family and caregivers to create a learning environment that "
                    + "meets the child’s needs. "
                    + "For example, someone at school goes above and beyond to take "
                    + "a healthy interest in the educational success of the child.  "
                    + "AND/OR "
                    + "Child excels in school.");
            rating7_7.put(1, "Useful Strength : Child is in school and has an educational plan that appears to be "
                    + "effective. School works fairly well with family and caregivers to "
                    + "ensure appropriate educational development. \n "
                    + "AND/OR\n"
                    + "Child likes school"
                    + "(regardless of academic or social reasons). ");
            rating7_7.put(2, "Potential Strength : Child is in school but educational plan currently does not meet "
                    + "the child’s needs");
            rating7_7.put(3, "No Strength Identified : Child is eligible for school but is not currently in school or is not\n"
                    + "in a school setting that furthers his/her education.  ");
            rating7_7.put(4, "Child is no longer in the schoolsystem and has no plans to return "
                    + "to the school system.");

            q7_7.setQuestionDescription(desc7_7);
            q7_7.setQuestionToConsider(quesToConsider7_7);
            q7_7.setRatingsDefinition(rating7_7);

            ques7teen.add(q7_7);

            //create 7.8
            MainQuestionEntity q7_8 = new MainQuestionEntity("7.8", "Talents/Interest");
            List<String> desc7_8 = new ArrayList<>();
            desc7_8.add("This item rates the child’s hobbies, skills, artistic interests and talents that are "
                    + "positive ways that children can spend time and also gives them pleasure and a positive sense of "
                    + "themselves");
            desc7_8.add("The talent should be positive and not be destructive and/or law‐breaking");
            desc7_8.add("For example, a child who has a talent in art but vandalises school walls by drawing on them should not "
                    + "be rated here. His/her talent in art should be developed in art class and on canvas or paper");

            List<String> quesToConsider7_8 = new ArrayList<>();
            quesToConsider7_8.add(" What are the child’s "
                    + "talents or interests?");
            quesToConsider7_8.add("What are other things "
                    + "that the child does "
                    + "particularly well?");
            quesToConsider7_8.add("What are other things "
                    + "that the child does "
                    + "particularly well?");
            quesToConsider7_8.add("Has the child ever "
                    + "expressed interest in any "
                    + "particular area?");
            quesToConsider7_8.add("Does he/she need help "
                    + "developing the interest "
                    + "into a hobby?");

            Map<Integer, String> rating7_8 = new HashMap<>();
            rating7_8.put(0, "Significant Strength : Child has a significant talent, interest, or hobby that provides "
                    + "him/her with a significant amount of personal benefit from "
                    + "activities surrounding the talent.");
            rating7_8.put(1, "Useful Strength : Child has a notable talent, interest, or hobby with the potential "
                    + "to provide him/her with pleasure and self‐esteem.  "
                    + "For example, a child who is involved in athletics or plays a "
                    + "musical instrument, etc. would be rated here.");
            rating7_8.put(2, "Potential Strength : Child has expressed interest in developing a specific talent or "
                    + "talents even if he/she has not developed the talent to date. "
                    + "He/she would require assistance converting those interests "
                    + "into a talent or hobby");
            rating7_8.put(3, "No Strength Identified : There is no evidence of identified talents, interests or hobbies "
                    + "at this time and/or child requires significant assistance to "
                    + "identify and develop talents and interests");

            q7_8.setQuestionDescription(desc7_8);
            q7_8.setQuestionToConsider(quesToConsider7_8);
            q7_8.setRatingsDefinition(rating7_8);

            ques7teen.add(q7_8);

            //create 7.9
            MainQuestionEntity q7_9 = new MainQuestionEntity("7.9", "Spiritual / Religious");
            List<String> desc7_9 = new ArrayList<>();
            desc7_9.add("This item rates the child’s experience of receiving comfort and support from "
                    + "spiritual or religious beliefs.");
            desc7_9.add("A “0” on this item indicates that the child’s spiritual/religious beliefs and practices are a comfort and "
                    + "significant source of support.");
            desc7_9.add("For example, a child who prays whenever he/she is upset or stressed would be rated “0”");

            List<String> quesToConsider7_9 = new ArrayList<>();
            quesToConsider7_9.add("Is the child religious?");
            quesToConsider7_9.add("Does he/she go to "
                    + "church/temple/mosque?");
            quesToConsider7_9.add("Do the child’s spiritual "
                    + "beliefs provide him/her "
                    + "with comfort?");

            Map<Integer, String> rating7_9 = new HashMap<>();
            rating7_9.put(0, "Significant Strength : Child receives significant comfort and support from "
                    + "religious and/or spiritual beliefs and practices");
            rating7_9.put(1, "Useful Strength : Child receives some comfort and support from "
                    + "religious/spiritual beliefs and practices");
            rating7_9.put(2, "Potential Strength : Child has expressed some interest in "
                    + "religious/spiritual beliefs and practices.");
            rating7_9.put(3, "No Strength Identified : Child has no identified religious/spiritual beliefs or "
                    + "interest in these pursuits.");

            q7_9.setQuestionDescription(desc7_9);
            q7_9.setQuestionToConsider(quesToConsider7_9);
            q7_9.setRatingsDefinition(rating7_9);

            ques7teen.add(q7_9);

            //create 7.10
            MainQuestionEntity q7_10 = new MainQuestionEntity("7.10", "Prosocial Groups");
            List<String> desc7_10 = new ArrayList<>();
            desc7_10.add("This item reflects the child’s connection to prosocial groups/organisations in\n"
                    + "his/her community.");
            desc7_10.add("This includes the child’s voluntary participation in activities in:");
            desc7_10.add("Religious groups/classes (e.g., churches, temples, mosques)");
            desc7_10.add("Community groups (e.g., Community Centres, Family Service Centres)");
            desc7_10.add("Student care centres");
            desc7_10.add("Ethnic based community groups (e.g., MENDAKI, SINDA)");
            desc7_10.add("It also includes co‐curricular activities (CCA) the child may be involved in regularly");

            List<String> quesToConsider7_10 = new ArrayList<>();
            quesToConsider7_10.add("Is the child part of any "
                    + "community organisation "
                    + "or religious group?");
            quesToConsider7_10.add("Does the child feel like "
                    + "he/she is part of any "
                    + "community?");
            quesToConsider7_10.add("Are there activities the "
                    + "child engages in within "
                    + "the community?");

            Map<Integer, String> rating7_10 = new HashMap<>();
            rating7_10.put(0, "Significant Strength : Child is well­integrated into his/her community. "
                    + "He/she is a member of community organisations and "
                    + "has positive ties to the community.");
            rating7_10.put(1, "Useful Strength : Child is involved with his/her community. He/she "
                    + "participates regularly in community activities");
            rating7_10.put(2, "Potential Strength : Child has an identified community but has only "
                    + "limited ties to that community");
            rating7_10.put(3, "No Strength Identified : CThere is no evidence of an identified community or "
                    + "child has no identified community of which he/she is "
                    + "a member");

            q7_10.setQuestionDescription(desc7_10);
            q7_10.setQuestionToConsider(quesToConsider7_10);
            q7_10.setRatingsDefinition(rating7_10);

            ques7teen.add(q7_10);

            //create 7.11
            MainQuestionEntity q7_11 = new MainQuestionEntity("7.11", "Child Involvement with Care");
            List<String> desc7_11 = new ArrayList<>();
            desc7_11.add("This item rates whether the child is aware of his/her own needs "
                    + "and whether he/she plays an active role in ensuring that his/her identified needs are met.");
            desc7_11.add("For example, a child who actively voices his/her needs to the caseworker and participates in his/her "
                    + "care‐planning would be rated as a “0”.");
            desc7_11.add("Younger children are less likely to be involved and are more likely to have a rating of “3” on this item.");
            desc7_11.add("Being involved in his/her own care planning can contribute to the child’s mastery of experiences, which "
                    + "can demonstrate to the child that he/she has the skills needed to succeed. This consequently may "
                    + "increase motivation and activity level, two factors shown to be characteristics of resilient children");

            List<String> quesToConsider7_11 = new ArrayList<>();
            quesToConsider7_11.add(" Is the child actively involved in "
                    + "his/her own treatment or care "
                    + "plan?");
            quesToConsider7_11.add("Does the child participate in "
                    + "treatment planning?");
            quesToConsider7_11.add("Is the child able to voice his/her "
                    + "own views about his/her care "
                    + "plans?");
            quesToConsider7_11.add("Does the child have necessary skills "
                    + "to advocate or participate in "
                    + "his/her treatment/care plans?");

            Map<Integer, String> rating7_11 = new HashMap<>();
            rating7_11.put(0, "Significant Strength : Child is knowledgeable of needs and plays "
                    + "a major role in planning to address them.");
            rating7_11.put(1, "Useful Strength : Child is knowledgeable of needs and "
                    + "participates in planning to address them.");
            rating7_11.put(2, "Potential Strength : Child is at least somewhat knowledgeable "
                    + "of needs but is not willing to participate in "
                    + "plans to address them.");
            rating7_11.put(3, "No Strength Identified : Child is at least somewhat knowledgeable "
                    + "of needs but is not willing to participate in "
                    + "plans to address them.");

            q7_11.setQuestionDescription(desc7_11);
            q7_11.setQuestionToConsider(quesToConsider7_11);
            q7_11.setRatingsDefinition(rating7_11);

            ques7teen.add(q7_11);

            //create 7.12
            MainQuestionEntity q7_12 = new MainQuestionEntity("7.12", "Community Supports");
            List<String> desc7_12 = new ArrayList<>();
            desc7_12.add("This item identifies all other forms of unpaid support/helpers within the\n"
                    + "child’s natural environment");
            desc7_12.add("All family members, aid caregivers and professionals (e.g., social workers, psychologists) are excluded.");

            List<String> quesToConsider7_12 = new ArrayList<>();
            quesToConsider7_12.add("Outside of the child’s "
                    + "family, are there people "
                    + "who provide support to the "
                    + "child?");
            quesToConsider7_12.add("How do these "
                    + "individuals help?");
            quesToConsider7_12.add("How often does the "
                    + "child see them?");
            quesToConsider7_12.add("Is the child involved in any "
                    + "mentorship programme?");

            Map<Integer, String> rating7_12 = new HashMap<>();
            rating7_12.put(0, "Significant Strength : Child has significant community supports who "
                    + "contribute in supporting the child’s healthy "
                    + "development.");
            rating7_12.put(1, "Useful Strength : Child has identified community supports that "
                    + "provide some assistance in supporting the child’s "
                    + "healthy development.");
            rating7_12.put(2, "Potential Strength : Child has identified community supports that "
                    + "provide some assistance in supporting the child’s "
                    + "healthy development.");
            rating7_12.put(3, "No Strength Identified : There is no evidence of community supports or "
                    + "child has no known community supports (outside "
                    + "of family and paid caregivers).");

            q7_12.setQuestionDescription(desc7_12);
            q7_12.setQuestionToConsider(quesToConsider7_12);
            q7_12.setRatingsDefinition(rating7_12);

            ques7teen.add(q7_12);

            //create 7.13
            MainQuestionEntity q7_13 = new MainQuestionEntity("7.13", "Resilience");
            List<String> desc7_13 = new ArrayList<>();
            desc7_13.add("A resilient child has the following characteristics:");
            desc7_13.add("(1) Has strengths, ");
            desc7_13.add("(2) Is able to recognise his/her own strengths ");
            desc7_13.add("(3) Uses his/her strengths to promote personal healthy development.");
            desc7_13.add("For example: Michelle has good problem‐solving skills (strength) and knows how to use it (recognises "
                    + "strength) to get herself out of dangerous situations safely (uses strength for healthy development). ");
            desc7_13.add("Younger children may be less likely to be described as resilient by this definition because they are less "
                    + "likely to be able to recognise their own strengths.");
            desc7_13.add("The concept of resiliency evaluated here is strongly related to supporting the child and youth to "
                    + "problem‐solve for themselves and use their own special skills and talents to advance their healthy "
                    + "development");

            List<String> quesToConsider7_13 = new ArrayList<>();
            quesToConsider7_13.add("What does the child do "
                    + "well?");
            quesToConsider7_13.add("Does he/she recognise "
                    + "these skills or talents as "
                    + "strengths?");
            quesToConsider7_13.add("Is he/she able to use these "
                    + "strengths to help solve "
                    + "problems/excel in certain "
                    + "areas of his/her life?");

            Map<Integer, String> rating7_13 = new HashMap<>();
            rating7_13.put(0, "Significant Strength : Child is able to recognise and use his/her strengths "
                    + "for healthy development and problem solving");
            rating7_13.put(1, "Useful Strength : Child recognises his/her strengths but is not yet "
                    + "able to use them in support of his/her healthy "
                    + "development or with problem solving.");
            rating7_13.put(2, "Potential Strength : Child has limited ability to recognise and use "
                    + "his/her strengths to support healthy development "
                    + "and/or with problem solving");
            rating7_13.put(3, "No Strength Identified : There is no evidence that the child is resilient or "
                    + "child fails to recognise his/her strengths and is "
                    + "therefore unable to utilise them");

            q7_13.setQuestionDescription(desc7_13);
            q7_13.setQuestionToConsider(quesToConsider7_13);
            q7_13.setRatingsDefinition(rating7_13);

            ques7teen.add(q7_13);

            //create 7.14
            MainQuestionEntity q7_14 = new MainQuestionEntity("7.14", "Resourcefulness");
            List<String> desc7_14 = new ArrayList<>();
            desc7_14.add("A resourceful child has the following characteristics:");
            desc7_14.add("(1) Has the ability to recognise his/her external or environmental strengths (e.g., Family, Friends) ");
            desc7_14.add("(2) Uses them to promote healthy development");
            desc7_14.add("For example: As David knows that the school counsellor is a safe person to confide in (recognising "
                    + "external strengths), he approaches his school counsellor whenever he has flashbacks of the abuse (uses "
                    + "external strength for healthy development).");
            desc7_14.add("A child who approachesfamily orfriendsto help him/hersort out important decisions would be described "
                    + "as ‘resourceful’");

            List<String> quesToConsider7_14 = new ArrayList<>();
            quesToConsider7_14.add("Does the child have any "
                    + "external or environmental "
                    + "strengths he/she can tap onto "
                    + "(e.g., family/community "
                    + "support, teachers/mentors)?");
            quesToConsider7_14.add("Does he/she recognise these as "
                    + "strengths?");
            quesToConsider7_14.add("Is he/she able to use these "
                    + "external supports to help solve "
                    + "problems?");

            Map<Integer, String> rating7_14 = new HashMap<>();
            rating7_14.put(0, "Significant Strength : Child is able to identify and use external "
                    + "resources to better him/herself and "
                    + "successfully manage difficult challenges");
            rating7_14.put(1, "Useful Strength : Child is able to identify most of his/her "
                    + "external resources and is able to partially "
                    + "utilise them.");
            rating7_14.put(2, "Potential Strength : Child is able to identify most of his/her "
                    + "external resources and is able to partially "
                    + "utilise them.");
            rating7_14.put(3, "No Strength Identified : There is no evidence that the child is "
                    + "resourceful or child is not yet able to identify "
                    + "external resources.");

            q7_14.setQuestionDescription(desc7_14);
            q7_14.setQuestionToConsider(quesToConsider7_14);
            q7_14.setRatingsDefinition(rating7_14);

            ques7teen.add(q7_14);

            AgeGroupEntity age16 = ageGroupSessionBean.createNewAgeGroupForDomain(strengthteen, domain7.getDomainId());
            for (MainQuestionEntity questions : ques7teen) {
                questionsSessionBean.createMainQuestionForAgeGroup(age16.getAgeGroupId(), questions);
            }

            AgeGroupEntity strengthadult = new AgeGroupEntity("14+");
            List<MainQuestionEntity> ques7adult = new ArrayList<>();

            //create 7.15
            MainQuestionEntity q7_15 = new MainQuestionEntity("7.15", "Vocational");
            List<String> desc7_15 = new ArrayList<>();
            desc7_15.add("This item rates the work experiences of the youth, which includes any part‐time work "
                    + "or internship");
            desc7_15.add("Vocational Strengths are rated independently of functioning (a youth can have considerable strengths "
                    + "but not be doing well at his/her job at the moment).");
            desc7_15.add("Developing vocational skills and having a job are significant indicators of positive outcomes in adult life.");

            List<String> quesToConsider7_15 = new ArrayList<>();
            quesToConsider7_15.add("Does the youth know what "
                    + "he/she wants to do in the "
                    + "future?");
            quesToConsider7_15.add("Has the youth ever worked or "
                    + "gone for an internship?");
            quesToConsider7_15.add("If yes, is the youth’s work "
                    + "experience relevant to "
                    + "his/her future plans?");

            Map<Integer, String> rating7_15 = new HashMap<>();
            rating7_15.put(0, "Significant Strength : Youth has significant vocational skills and "
                    + "relevant work experience");
            rating7_15.put(1, "Useful Strength : Youth has some vocational skills or work "
                    + "experience.");
            rating7_15.put(2, "Potential Strength : Youth has some prevocational skills or "
                    + "vocational interests.");
            rating7_15.put(3, "No Strength Identified : No vocational strengths identified or youth "
                    + "needs significant assistance developing "
                    + "vocational skills");

            q7_15.setQuestionDescription(desc7_15);
            q7_15.setQuestionToConsider(quesToConsider7_15);
            q7_15.setRatingsDefinition(rating7_15);

            ques7adult.add(q7_15);

            AgeGroupEntity age17 = ageGroupSessionBean.createNewAgeGroupForDomain(strengthadult, domain7.getDomainId());
            for (MainQuestionEntity questions : ques7adult) {
                questionsSessionBean.createMainQuestionForAgeGroup(age17.getAgeGroupId(), questions);
            }

            // Create Caregivers Module
            List<String> caredescrip = new ArrayList<>();
            caredescrip.add("Who is a Caregiver?\n"
                    + "• Caregivers would include both parents and other significant caregivers in the child’s life (e.g., "
                    + "grandparents).");
            caredescrip.add("2 Types of Caregivers:\n"
                    + "1. Long­Term Caregiver\n"
                    + "• Caregiver/s with legal custody of the child  \n"
                    + "• Caregiver/s who have been identified as the person whom the child will eventually return "
                    + "to if the child is currently in an out‐of‐home care arrangement\n"
                    + "2. Current Caregiver\n"
                    + "• Caregivers who are appointed temporarily and/or cannot take care of the child in the long "
                    + "term (e.g. foster parents and/or family members)\n"
                    + "• If the child is living with a relative/foster parent/other caregiver who is planning/going to "
                    + "assume custody/long‐term care of the child, please rate this caregiver as Long‐Term "
                    + "Caregiver instead.  ");
            caredescrip.add("HOW TO RATE CAREGIVERS");
            caredescrip.add("STEP 1:   Identify all significant adults who are Long‐Term Caregiver/s or Current Caregiver/s.  \n"
                    + "• If the child has NO identified caregiver, this section can be left blank");
            caredescrip.add("STEP 2:   Rate the CANS for each caregiver separately, if the child has multiple caregivers who are living in "
                    + "the same household or not living together (e.g., parents who are divorced).");
            caredescrip.add("STEP 3: Provide the reason for NOT rating the caregivers who have been identified in Step 1.");

            DomainEntity ca = new DomainEntity("Caregiving Ability", null, true, 1, true);
            AgeGroupEntity caage = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques8a = new ArrayList<>();

            //create 8a.1
            MainQuestionEntity q8a_1 = new MainQuestionEntity("8A.1", "A. Supervision/Discipline");
            List<String> desc8a_1 = new ArrayList<>();
            desc8a_1.add("This item refers to the caregiver’s ability to monitor and discipline the "
                    + "child. ");
            desc8a_1.add("Supervision refers to the caregiver ensuring that the child is supervised by a trusted adult for safety and "
                    + "to prevent risk of harm.");
            desc8a_1.add("Discipline is defined in the broadestsense as all of the things that parents/caregivers can do to promote "
                    + "positive behaviour in their children (e.g., setting appropriate boundaries and rules, reinforcing good "
                    + "behaviour and correcting negative behaviours, etc.).");
            desc8a_1.add("Definition of appropriate discipline* might differ from culture to culture. What constitutes age‐ "
                    + "appropriate discipline for younger children would be different from that of older children ");

            List<String> quesToConsider8a_1 = new ArrayList<>();
            quesToConsider8a_1.add("Does the caregiver monitor "
                    + "and discipline the child? "
                    + "➢ If yes, is it age‐ "
                    + "appropriate?");
            quesToConsider8a_1.add("How would you rate the "
                    + "caregiver’s ability to monitor "
                    + "and manage the child’s "
                    + "behaviour?");
            quesToConsider8a_1.add("Do you feel that he/she needs "
                    + "any help with these issues?");

            Map<Integer, String> rating8a_1 = new HashMap<>();
            rating8a_1.put(0, "Caregiver has good monitoring and supervision skills. "
                    + "Caregiver is able to discipline appropriately.");
            rating8a_1.put(1, "Caregiver provides generally adequate supervision and "
                    + "discipline. May need occasional help or professional "
                    + "assistance. ");
            rating8a_1.put(2, "Caregiver reports difficulties monitoring and/or disciplining "
                    + "the child. Caregiver may use inappropriate disciplinary "
                    + "methods. Caregiver needs assistance to improve "
                    + "supervision/discipline skills.");
            rating8a_1.put(3, "Caregiver is unable to monitor or discipline the child "
                    + "appropriately. Caregiver requires immediate and continuing "
                    + "assistance. Child is at risk of harm due to absence of "
                    + "supervision and/or inappropriate discipline.");

            q8a_1.setQuestionDescription(desc8a_1);
            q8a_1.setQuestionToConsider(quesToConsider8a_1);
            q8a_1.setRatingsDefinition(rating8a_1);

            ques8a.add(q8a_1);

            //create 8a.2
            MainQuestionEntity q8a_2 = new MainQuestionEntity("8A.2", "A. Involvement in Caregiving");
            List<String> desc8a_2 = new ArrayList<>();
            desc8a_2.add("This item refers to the degree to which the caregiver is actively "
                    + "involved with caregiving functions for the child.  ");

            List<String> quesToConsider8a_2 = new ArrayList<>();
            quesToConsider8a_2.add("How involved are "
                    + "caregivers in caregiving "
                    + "functions and services "
                    + "for the child?  ");
            quesToConsider8a_2.add("Is the caregiver an "
                    + "advocate for the child?");
            quesToConsider8a_2.add("How does the caregiver "
                    + "respond when asked to "
                    + "be involved in services "
                    + "for the child?");

            Map<Integer, String> rating8a_2 = new HashMap<>();
            rating8a_2.put(0, "Caregiver is actively involved in planning or implementation of "
                    + "services and is able to act as an effective advocate for child. "
                    + "This requires knowledge of the child, his/her rights, options, and "
                    + "opportunities.");
            rating8a_2.put(1, "Caregiver is generally involved in daily family life but may be "
                    + "occasionally less involved for brief periods of time because of "
                    + "internal stressors and/or external responsibilities. Caregiver is open "
                    + "to receiving support, education, and information but may not be "
                    + "able to serve as advocates for the child yet. ");
            rating8a_2.put(2, "Caregiver is involved in daily family life but only maintains minimal "
                    + "daily interactions for extended periods of time. Caregiver does not "
                    + "participate in services and/or interventions intended to assist the "
                    + "care of the child; "
                    + "OR "
                    + "Caregiver only visits the child occasionally in the alternative care "
                    + "setting");
            rating8a_2.put(3, "Caregiver is mostly uninvolved in daily family life. He/she may not "
                    + "interact with the child on a daily basis. Caregiver may also wish for "
                    + "child to be removed from his/her care; "
                    + "OR "
                    + "Caregiver is not visiting the child in the alternative care setting");

            q8a_2.setQuestionDescription(desc8a_2);
            q8a_2.setQuestionToConsider(quesToConsider8a_2);
            q8a_2.setRatingsDefinition(rating8a_2);

            ques8a.add(q8a_2);

            //create 8a.3
            MainQuestionEntity q8a_3 = new MainQuestionEntity("8A.3", "A. Empathy for Chlid");
            List<String> desc8a_3 = new ArrayList<>();
            desc8a_3.add("This item rates the degree to which the caregiver is attuned to the child’s "
                    + "feelings/emotional needs and his/her capacity in understanding and responding to these needs.");
            desc8a_3.add("Caregivers who are not responsive to their child or are responsive only in certain situations should be "
                    + "rated “2” or “3”.");

            List<String> quesToConsider8a_3 = new ArrayList<>();
            quesToConsider8a_3.add("Does the caregiver show that "
                    + "he/she understands the child’s "
                    + "feelings and emotional needs?");
            quesToConsider8a_3.add("Is the caregiver responsive to the "
                    + "child’s feelings?\n"
                    + "➢ If yes to above, is the caregiver "
                    + "able to support the child’s "
                    + "emotional needs?");

            Map<Integer, String> rating8a_3 = new HashMap<>();
            rating8a_3.put(0, "Caregiver understands how the child is feeling and "
                    + "consistently demonstrates this in interactions with the "
                    + "child.");
            rating8a_3.put(1, "Caregiver has the ability to understand how the child is "
                    + "feeling in most situations and is able to demonstrate "
                    + "support for the child in this area most of the time.");
            rating8a_3.put(2, "Caregiver has some difficulties empathising with the "
                    + "child and at timesthe lack of empathy interferes with the "
                    + "child’s growth and development");
            rating8a_3.put(3, "Caregiver shows no empathy for the child in most "
                    + "situations especially when the child is distressed. "
                    + "Caregiver’s lack of empathy is impeding the child’s "
                    + "development");

            q8a_3.setQuestionDescription(desc8a_3);
            q8a_3.setQuestionToConsider(quesToConsider8a_3);
            q8a_3.setRatingsDefinition(rating8a_3);

            ques8a.add(q8a_3);

            //create 8a.4
            MainQuestionEntity q8a_4 = new MainQuestionEntity("8A.4", "A. Knowledge");
            List<String> desc8a_4 = new ArrayList<>();
            desc8a_4.add("This item rates the caregiver’s comprehensive knowledge* of the child’s basic care "
                    + "needs, development, temperament and strengths/weaknesses");
            desc8a_4.add("Consider the caregiver’s ability to understand the rationale for treatment/management of the child’s "
                    + "problems");
            desc8a_4.add("For example:  \n"
                    + "‐ Two caregivers may have no knowledge about temperament,\n"
                    + "o One caregiver has a child with easy‐going temperament, and experiences no needs in "
                    + "relation to child’s temperament, hence this item is rated “0”.  \n"
                    + "o However, the other caregiver has a child with a feisty temperament and has a need for "
                    + "knowledge to better understand and relate to the child; hence this item is rated “2” or "
                    + "“3”.\n"
                    + "‐ A caregiver of a child who was physically abused in the past may be rated “2” if the caregiver does "
                    + "not understand that the child’s aggression toward others was due to the physical abuse.");
            desc8a_4.add("We recommend thinking of this item in terms of whether if there is information that you made "
                    + "available to the caregiver, he/she could be more effective in working with his/her child");

            List<String> quesToConsider8a_4 = new ArrayList<>();
            quesToConsider8a_4.add("Is the caregiver generally "
                    + "aware of the child’s basic "
                    + "needs, development, "
                    + "temperament and "
                    + "strengths/weaknesses?");
            quesToConsider8a_4.add("Does the caregiver seem to "
                    + "understand what "
                    + "professionals are telling "
                    + "him/her about the child and "
                    + "recognise its developmental "
                    + "significance?  ");
            quesToConsider8a_4.add("Does caregiver require "
                    + "additional information about "
                    + "the child to better care for "
                    + "him/her?");

            Map<Integer, String> rating8a_4 = new HashMap<>();
            rating8a_4.put(0, "Caregiver is highly knowledgeable about basic care needs, "
                    + "development, temperament, strengths and weaknesses of "
                    + "the child.");
            rating8a_4.put(1, "Caregiver is generally knowledgeable about basic care "
                    + "needs, development, temperament, strengths and "
                    + "weaknesses of the child, but may have occasional gaps in "
                    + "knowledge.");
            rating8a_4.put(2, "Caregiver has some knowledge about basic care needs, "
                    + "development, temperament, strengths and weaknesses of "
                    + "the child, but deficits exist in the caregiver’s ability to "
                    + "understand the child’s needs and strengths.");
            rating8a_4.put(3, "Caregiver has little or no knowledge of the child’s current "
                    + "basic care needs, development, temperament, strengths "
                    + "and weaknesses, and significant deficits exist in the "
                    + "caregiver’s ability to understand the child’s needs and "
                    + "strengths.");

            q8a_4.setQuestionDescription(desc8a_4);
            q8a_4.setQuestionToConsider(quesToConsider8a_4);
            q8a_4.setRatingsDefinition(rating8a_4);

            ques8a.add(q8a_4);

            //create 8a.5
            MainQuestionEntity q8a_5 = new MainQuestionEntity("8A.5", "A. Organisation");
            List<String> desc8a_5 = new ArrayList<>();
            desc8a_5.add("This item describesthe caregiver’s ability to organise and manage his/her household "
                    + "needs, home environment and/or put in place regular routines for the child.");
            desc8a_5.add("Caregivers who need help organising themselves and/or their family would be rated a “2” (e.g., poor "
                    + "home organisation that poses inconvenience to caregiver, caregivers who hoard junk in the house that "
                    + "pose a health and fire hazard to the child)");
            desc8a_5.add("Caregivers who occasionally forget appointments or calls would be rated a “1”");

            List<String> quesToConsider8a_5 = new ArrayList<>();
            quesToConsider8a_5.add("Does caregiver need or want help managing "
                    + "his/her household");
            quesToConsider8a_5.add("Does caregiver have difficulty getting to "
                    + "appointments (e.g., the child’s medical "
                    + "appointments)?");
            quesToConsider8a_5.add("Is the caregiver forgetful about appointments or "
                    + "returning calls?");
            quesToConsider8a_5.add("Does caregiver need or want help in "
                    + "implementing regular routines for a child? Is the "
                    + "caregiver able to carry out some routines "
                    + "regularly?");
            quesToConsider8a_5.add("Does the caregiver have trouble getting the child "
                    + "to school on time in general?");

            Map<Integer, String> rating8a_5 = new HashMap<>();
            rating8a_5.put(0, "Caregiver is well­organised and efficient "
                    + "and the child has regular routines.");
            rating8a_5.put(1, "Caregiver has minimal difficulties with "
                    + "organising and managing household "
                    + "responsibilities and routines. ");
            rating8a_5.put(2, "Caregiver has moderate difficulty "
                    + "organising and managing household "
                    + "responsibilities and routines");
            rating8a_5.put(3, "Caregiver is unable to manage "
                    + "household responsibilities and routines.");

            q8a_5.setQuestionDescription(desc8a_5);
            q8a_5.setQuestionToConsider(quesToConsider8a_5);
            q8a_5.setRatingsDefinition(rating8a_5);

            ques8a.add(q8a_5);

            //create 8a.6
            MainQuestionEntity q8a_6 = new MainQuestionEntity("8A.6", "A. Intervention Adherence");
            List<String> desc8a_6 = new ArrayList<>();
            desc8a_6.add("This item rates the parent/caregiver’s adherence to interventions that "
                    + "are necessary for (i) the development (i.e. in the areas of gross/fine motor skills, communication, "
                    + "personalsocial or problem‐solving development); (ii) mental health; or(iii) physical health of the child.");
            desc8a_6.add("Developmental health interventions could include mandatory developmental assessments or "
                    + "interventions such as therapy at hospitals’ Child Development Unit, Learning Support, Development "
                    + "Support or Early Intervention Programme for Infants and Children (EIPIC).  ");
            desc8a_6.add("Mental health interventions for the child’s well‐being could include counselling sessions with a child "
                    + "psychologist");
            desc8a_6.add("Physical health interventions for the child’s well‐being could include follow‐up appointments with a "
                    + "dentist or other medical appointments.");
            desc8a_6.add("Parents’ or caregivers’ adherence to their own appointments would not be rated under this item.  ");

            List<String> quesToConsider8a_6 = new ArrayList<>();
            quesToConsider8a_6.add("Does the "
                    + "parent/caregiver "
                    + "understand the "
                    + "importance of such "
                    + "interventions for the "
                    + "child’s health and "
                    + "well‐being?");
            quesToConsider8a_6.add("Does the "
                    + "parent/caregiver "
                    + "require significant "
                    + "support in ensuring "
                    + "that the child "
                    + "receives these "
                    + "interventions? ");
            quesToConsider8a_6.add("Is the child’s "
                    + "functioning affected "
                    + "due to non‐ "
                    + "adherence to these "
                    + "interventions?");
            quesToConsider8a_6.add("Does caregiver need or want help in "
                    + "implementing regular routines for a child? Is the "
                    + "caregiver able to carry out some routines "
                    + "regularly?");
            quesToConsider8a_6.add("Does the caregiver have trouble getting the child "
                    + "to school on time in general?");

            Map<Integer, String> rating8a_6 = new HashMap<>();
            rating8a_6.put(0, "Caregiver keeps to the child’s interventions as prescribed and "
                    + "without reminders;   "
                    + "OR "
                    + "Child has no need for interventionsfor his/her developmental, mental "
                    + "or physical health.  ");
            rating8a_6.put(1, "Caregiver keeps to the child’s interventions routinely, but sometimes "
                    + "needs reminders to maintain adherence. "
                    + "OR "
                    + "A history of non­adherence is present, but the child has no current "
                    + "need for interventions for his/her developmental, mental or physical "
                    + "health. ");
            rating8a_6.put(2, "Caregiver has difficulty adhering to the child’s interventions and/or "
                    + "the caregiver is non­compliant with the child’s interventions (e.g., "
                    + "missing appointments; failing to provide medication at the proper "
                    + "dosage). Caregiver generally has problems sustaining intervention "
                    + "efforts and/or facilitating the child’s access to the prescribed "
                    + "intervention.");
            rating8a_6.put(3, "Caregiver has difficulty adhering to the child’s interventions and/or "
                    + "the caregiver is non­compliant with the child’s interventions (e.g., "
                    + "missing appointments; failing to provide medication at the proper "
                    + "dosage). Caregiver generally has problems sustaining intervention "
                    + "efforts and/or facilitating the child’s access to the prescribed "
                    + "intervention.");

            q8a_6.setQuestionDescription(desc8a_6);
            q8a_6.setQuestionToConsider(quesToConsider8a_6);
            q8a_6.setRatingsDefinition(rating8a_6);

            ques8a.add(q8a_6);

            //create 8a.7
            MainQuestionEntity q8a_7 = new MainQuestionEntity("8A.7", "A. Caregiving Stress");
            List<String> desc8a_7 = new ArrayList<>();
            desc8a_7.add("Thisitem describesthe stress experienced by the caregiver resulting from taking "
                    + "care of the child’s needs and the impact of the child’s needs on the family system.");
            desc8a_7.add("Consider the impact of the child’s needs on the stress and well‐being of the caregiver. For example, "
                    + "family members of a child with behavioural problems may feel burdened by the impact of the child’s "
                    + "needs on the caregiver.");

            List<String> quesToConsider8a_7 = new ArrayList<>();
            quesToConsider8a_7.add("Does the caregiver find it stressful at "
                    + "times to manage the challenges of "
                    + "dealing with the child’s needs?");
            quesToConsider8a_7.add("Does the stress ever interfere with the "
                    + "caregiver’s ability to care for the child? "
                    + "➢ If yes, does it ever reach a level "
                    + "where the caregiver feels like "
                    + "he/she cannot manage/wants the "
                    + "child removed?");

            Map<Integer, String> rating8a_7 = new HashMap<>();
            rating8a_7.put(0, "Caregiver is able to manage the stress associated "
                    + "with caring for the child’s needs.");
            rating8a_7.put(1, "Caregiver has some problems managing the stress "
                    + "associated with caring for the child’s needs");
            rating8a_7.put(2, "Caregiver has notable problems managing the "
                    + "stress associated with caring for the child’s needs. "
                    + "This stress interferes with his/her capacity to give "
                    + "care");
            rating8a_7.put(3, "Caregiver is unable to manage the stress "
                    + "associated with caring for the child’s needs. This "
                    + "stress prevents caregiver from giving care");

            q8a_7.setQuestionDescription(desc8a_7);
            q8a_7.setQuestionToConsider(quesToConsider8a_7);
            q8a_7.setRatingsDefinition(rating8a_7);

            ques8a.add(q8a_7);

            DomainEntity domain8a = domainSessionBean.createNewDomain(ca);
            AgeGroupEntity age18 = ageGroupSessionBean.createNewAgeGroupForDomain(caage, domain8a.getDomainId());
            for (MainQuestionEntity questions : ques8a) {
                questionsSessionBean.createMainQuestionForAgeGroup(age18.getAgeGroupId(), questions);
            }

            // ---- Caregiver B module -----------
            DomainEntity cb = new DomainEntity("Caregiver Resources", null, true, 1, true);
            AgeGroupEntity cbage = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques8b = new ArrayList<>();

            //create 8b.1
            MainQuestionEntity q8b_1 = new MainQuestionEntity("8B.1", "B. Social Resources");
            List<String> desc8b_1 = new ArrayList<>();
            desc8b_1.add("This item rates the availability of unpaid social resources the caregiver can rely "
                    + "on for caregiving in times of need.");
            desc8b_1.add("Social resources could include friends, family members, neighbours, people that are known and trusted "
                    + "by the child/youth and caregiver");
            desc8b_1.add("Professionals working with the family (e.g., MSF officers, school counsellors) should not be rated here");
            desc8b_1.add("“Actively helping” refers to a stable and reliable source of help that currently participates in caregiving");

            List<String> quesToConsider8b_1 = new ArrayList<>();
            quesToConsider8b_1.add("Does the caregiver have the "
                    + "support of extended family, "
                    + "friends, people from the "
                    + "neighbourhood, i.e. people "
                    + "who can be tapped on to help "
                    + "with taking care of the child?");
            quesToConsider8b_1.add("Can the caregiver call on such "
                    + "individuals to help take care of "
                    + "the child?");

            Map<Integer, String> rating8b_1 = new HashMap<>();
            rating8b_1.put(0, "Caregiver hassignificant family and social networksthat are "
                    + "actively helping in the care of the child.");
            rating8b_1.put(1, "Caregiver has some family or social networks that are "
                    + "actively helping in the care of the child");
            rating8b_1.put(2, "Caregiver has some family or social networks that may be "
                    + "able to help in the care of the child");
            rating8b_1.put(3, "Caregiver has no family orsocial networks that may be able "
                    + "to help in the care of the child");

            q8b_1.setQuestionDescription(desc8b_1);
            q8b_1.setQuestionToConsider(quesToConsider8b_1);
            q8b_1.setRatingsDefinition(rating8b_1);

            ques8b.add(q8b_1);

            //create 8b.2
            MainQuestionEntity q8b_2 = new MainQuestionEntity("8B.2", "B. Housing Stability");
            List<String> desc8b_2 = new ArrayList<>();
            desc8b_2.add("This item refers to the caregiver’s housing status and/or situation");
            desc8b_2.add("A “1” indicates concerns about instability in the immediate future. A family having difficulty paying "
                    + "utilities, rent or a mortgage might be rated as a “1”, which indicatesthat monitoring the family’s housing "
                    + "situation is necessary to prevent future housing disruption.");
            desc8b_2.add("A “3” indicates problems of recent homelessness");

            List<String> quesToConsider8b_2 = new ArrayList<>();
            quesToConsider8b_2.add("Is the family’s housing situation "
                    + "stable?");
            quesToConsider8b_2.add("Are there financial difficulties or "
                    + "relationship tensions that may lead to "
                    + "eviction or loss of housing?");
            quesToConsider8b_2.add("Are there concerns that the caregiver "
                    + "may need to move in the near future?");
            quesToConsider8b_2.add("Has the family lost their housing?");
            quesToConsider8b_2.add("Does the family move often?");

            Map<Integer, String> rating8b_2 = new HashMap<>();
            rating8b_2.put(0, "Caregiver has stable housing for the foreseeable "
                    + "future.");
            rating8b_2.put(1, "Caregiver has relatively stable housing but either "
                    + "has moved in the past three months or there are "
                    + "indications of housing problems that might force "
                    + "him/her to move in the next three months.");
            rating8b_2.put(2, "Caregiver has moved multiple times in the past "
                    + "year. Housing is unstable");
            rating8b_2.put(3, "Caregiver has experienced periods of "
                    + "homelessness in the past six months");

            q8b_2.setQuestionDescription(desc8b_2);
            q8b_2.setQuestionToConsider(quesToConsider8b_2);
            q8b_2.setRatingsDefinition(rating8b_2);

            ques8b.add(q8b_2);

            //create 8b.3
            MainQuestionEntity q8b_3 = new MainQuestionEntity("8B.3", "B. Employment Functioning");
            List<String> desc8b_3 = new ArrayList<>();
            desc8b_3.add("This rates the functioning of the caregiver in work settings.  This can "
                    + "include issues of behaviour, attendance or productivity.  ");
            desc8b_3.add("Caregivers who are employed in illegal activities (e.g., drug sales, illegal moneylending or prostitution) "
                    + "will be rated “3”. ");

            List<String> quesToConsider8b_3 = new ArrayList<>();
            quesToConsider8b_3.add("Is the caregiver currently "
                    + "working?");
            quesToConsider8b_3.add("Does the caregiver have "
                    + "any difficulties holding "
                    + "down a job?\n"
                    + "➢ If yes, does caregiver "
                    + "have a history of "
                    + "frequent job loss?");

            Map<Integer, String> rating8b_3 = new HashMap<>();
            rating8b_3.put(0, "Caregiver is gainfully employed");
            rating8b_3.put(1, "A mild degree of problems with work functioning. Caregiver may "
                    + "have some problems in work environment. Caregiver needs to be "
                    + "monitored and assessed further");
            rating8b_3.put(2, "A moderate degree of school or work problems. Caregiver may "
                    + "have history of frequent job loss or may be recently unemployed."
                    + "Caregiver needs an intervention to address employment");
            rating8b_3.put(3, "A severe degree of school or work problems. Caregiver is "
                    + "chronically unemployed. Caregiver needs immediate intervention.");
            rating8b_3.put(4, "Caregiver is retired or caregiver is not working due to reasons such "
                    + "as studying and/or child care or elder care (e.g., homemakers). ");

            q8b_3.setQuestionDescription(desc8b_3);
            q8b_3.setQuestionToConsider(quesToConsider8b_3);
            q8b_3.setRatingsDefinition(rating8b_3);

            ques8b.add(q8b_3);

            //create 8b.4
            MainQuestionEntity q8b_4 = new MainQuestionEntity("8B.4", "B. Financial Resources");
            List<String> desc8b_4 = new ArrayList<>();
            desc8b_4.add("This item indicates whether the caregiver has enough financial resources to "
                    + "support the child’s needs and whether the caregiver requires financial assistance.");
            desc8b_4.add("Rate caregivers who are currently receiving financial assistance or subsidies as “2”.");

            List<String> quesToConsider8b_4 = new ArrayList<>();
            quesToConsider8b_4.add("Does the caregiver have any "
                    + "financial difficulties?");
            quesToConsider8b_4.add("Does the caregiver have "
                    + "enough financial resources to "
                    + "raise the child?");

            Map<Integer, String> rating8b_4 = new HashMap<>();
            rating8b_4.put(0, "Caregiver hassufficient financialresourcesto raise the child\n"
                    + "(e.g., child rearing)");
            rating8b_4.put(1, "Caregiver has some financial resources that actively help\n"
                    + "with raising the child (e.g., child rearing)");
            rating8b_4.put(2, "Caregiver has limited financial resources to help with some\n"
                    + "aspects of raising the child (e.g., child rearing)");
            rating8b_4.put(3, "Caregiver has no financial resources to raise the child (e.g.,\n"
                    + "child rearing). Caregiver needs financial resources.");

            q8b_4.setQuestionDescription(desc8b_4);
            q8b_4.setQuestionToConsider(quesToConsider8b_4);
            q8b_4.setRatingsDefinition(rating8b_4);

            ques8b.add(q8b_4);

            DomainEntity domain8b = domainSessionBean.createNewDomain(cb);
            AgeGroupEntity age19 = ageGroupSessionBean.createNewAgeGroupForDomain(cbage, domain8b.getDomainId());
            for (MainQuestionEntity questions : ques8b) {
                questionsSessionBean.createMainQuestionForAgeGroup(age19.getAgeGroupId(), questions);
            }

            // ---- Caregiver C module -----------
            DomainEntity cc = new DomainEntity("Caregiver Risk", null, true, 1, true);
            AgeGroupEntity ccage = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques8c = new ArrayList<>();

            //create 8c.1
            MainQuestionEntity q8c_1 = new MainQuestionEntity("8C.1", "C. Mental Health");
            List<String> desc8c_1 = new ArrayList<>();
            desc8c_1.add("This item identifies any mental health difficulties that might limit caregiving "
                    + "capacity.");
            desc8c_1.add("A caregiver with serious mental illness would likely be rated a “2” or even a “3” depending on the impact "
                    + "of the illness.   ");
            desc8c_1.add("A caregiver who has been stable for a length of time and whose mental illnessis currently well controlled "
                    + "by medication might be rated a “1”.");
            desc8c_1.add("A caregiver may not have a formal diagnosis but professionals may have observed that there are some "
                    + "concerns which may indicate mental health difficulties.");

            List<String> quesToConsider8c_1 = new ArrayList<>();
            quesToConsider8c_1.add("Does the caregiver have "
                    + "any mental health needs?\n"
                    + "➢ If yes, has he/she been "
                    + "referred for "
                    + "assessment?  \n"
                    + "➢ Does mental health "
                    + "needs affect "
                    + "caregiver’s parenting "
                    + "ability? To what "
                    + "extent?");

            Map<Integer, String> rating8c_1 = new HashMap<>();
            rating8c_1.put(0, "Caregiver has no mental health needs.");
            rating8c_1.put(1, "Caregiver’s mental health difficulties are well­managed and/or "
                    + "caregiver is in recovery. "
                    + "OR "
                    + "History, suspicion or mild level of mental health difficulties "
                    + "(e.g., worrying or feeling sad on most days, notsleeping well on "
                    + "most days, crying often)");
            rating8c_1.put(2, "Caregiver has mental health difficulties that interfere with "
                    + "his/her capacity to parent or is receiving support from a "
                    + "community mental health agency.");
            rating8c_1.put(3, "Caregiver has mental health difficulties that make it "
                    + "impossible for him/her to parent at this time or require "
                    + "admission to tertiary hospitals for intervention");

            q8c_1.setQuestionDescription(desc8c_1);
            q8c_1.setQuestionToConsider(quesToConsider8c_1);
            q8c_1.setRatingsDefinition(rating8c_1);

            ques8c.add(q8c_1);

            //create 8c.2
            MainQuestionEntity q8c_2 = new MainQuestionEntity("8C.2", "C. Physical Health");
            List<String> desc8c_2 = new ArrayList<>();
            desc8c_2.add("This item refers to medical and/or physical problems that the caregiver may be "
                    + "experiencing that limits or prevents his/her ability to parent the child.");
            desc8c_2.add("For example, a single parent who has mobility or communication issues that limits him/her from "
                    + "caregiving might be rated a “2” or a “3”");
            desc8c_2.add("If the caregiver has recently recovered from a serious illness or injury or if there are some concerns of "
                    + "problems in the immediate future, he/she might be rated a “1”");

            List<String> quesToConsider8c_2 = new ArrayList<>();
            quesToConsider8c_2.add("Is the caregiver healthy?");
            quesToConsider8c_2.add("Does the caregiver have "
                    + "any medical/health "
                    + "problems that may limit "
                    + "his/her ability to care for "
                    + "the child?");

            Map<Integer, String> rating8c_2 = new HashMap<>();
            rating8c_2.put(0, "Caregiver is generally healthy");
            rating8c_2.put(1, "Caregiver is in recovery from medical/physical problems; "
                    + "OR "
                    + "Caregiver has medical/physical health problemsthat do not limit "
                    + "his/her capacity to parent."
                    + "year. Housing is unstable");
            rating8c_2.put(2, "Caregiver has medical/physical problems that interfere with\n"
                    + "his/her capacity to parent.");
            rating8c_2.put(3, "Caregiver has medical/physical problems that make it\n"
                    + "impossible to parent at this time");

            q8c_2.setQuestionDescription(desc8c_2);
            q8c_2.setQuestionToConsider(quesToConsider8c_2);
            q8c_2.setRatingsDefinition(rating8c_2);

            ques8c.add(q8c_2);

            //create 8c.3
            MainQuestionEntity q8c_3 = new MainQuestionEntity("8C.3", "C. Substance Use");
            List<String> desc8c_3 = new ArrayList<>();
            desc8c_3.add("This item describes the impact of any notable substance use on caregivers");
            desc8c_3.add("This includes the use of alcohol and illegal drugs, the misuse of prescription medications and the "
                    + "inhalation of any substance for recreational purposes.  ");
            desc8c_3.add("A “1” indicates a caregiver currently in recovery or a situation where problems of substance use are "
                    + "suspected but not confirmed. ");

            List<String> quesToConsider8c_3 = new ArrayList<>();
            quesToConsider8c_3.add("Does the caregiver have any "
                    + "substance use issues? \n"
                    + "➢ If yes, do the substance use needs "
                    + "affect caregiver’s parenting "
                    + "ability? To what extent?");

            Map<Integer, String> rating8c_3 = new HashMap<>();
            rating8c_3.put(0, "Caregiver has no substance use needs");
            rating8c_3.put(1, "Caregiver is in recovery from substance use "
                    + "difficulties or there are suspicions that the caregiver "
                    + "may have substance use needs.  ");
            rating8c_3.put(2, "Caregiver has substance use difficulties that "
                    + "interfere with his/her capacity to parent.");
            rating8c_3.put(3, "Caregiver has substance use difficulties that make it\n"
                    + "impossible for him/her to parent at this time.");

            q8c_3.setQuestionDescription(desc8c_3);
            q8c_3.setQuestionToConsider(quesToConsider8c_3);
            q8c_3.setRatingsDefinition(rating8c_3);

            ques8c.add(q8c_3);

            //create 8c.4
            MainQuestionEntity q8c_4 = new MainQuestionEntity("8C.4", "C. Intellectual/Developemental Disability");
            List<String> desc8c_4 = new ArrayList<>();
            desc8c_4.add("This item describes the presence of intellectual and/or "
                    + "developmental disabilities in caregivers.  ");
            desc8c_4.add("A caregiver with limited cognitive capacity that interferes with his/her ability to care for the child would "
                    + "be rated here.  ");

            List<String> quesToConsider8c_4 = new ArrayList<>();
            quesToConsider8c_4.add("Does the caregiver have any "
                    + "intellectual/developmental "
                    + "disabilities (e.g., low IQ)?\n"
                    + "➢ If yes, does the "
                    + "intellectual/ "
                    + "developmental disability "
                    + "affect caregiver’s "
                    + "parenting ability? To "
                    + "what extent?");

            Map<Integer, String> rating8c_4 = new HashMap<>();
            rating8c_4.put(0, "Caregiver has no intellectual/developmental disability");
            rating8c_4.put(1, "Caregiver has intellectual/developmental disability but it "
                    + "currently does not interfere with parenting.  ");
            rating8c_4.put(2, "Caregiver has intellectual/developmental disability that "
                    + "interferes with his/her capacity to parent. ");
            rating8c_4.put(3, "Caregiver has severe intellectual/developmental disability "
                    + "that makes it impossible for him/her to parent at this time.");

            q8c_4.setQuestionDescription(desc8c_4);
            q8c_4.setQuestionToConsider(quesToConsider8c_4);
            q8c_4.setRatingsDefinition(rating8c_4);

            ques8c.add(q8c_4);

            //create 8c.5
            MainQuestionEntity q8c_5 = new MainQuestionEntity("8C.5", "C. Legal");
            List<String> desc8c_5 = new ArrayList<>();
            desc8c_5.add("This item describes the caregiver’s level of involvement with the criminal justice system.");
            desc8c_5.add("A rating of “1” would indicate minor traffic offences, such as being summoned to court due to unsettled "
                    + "traffic fines on multiple occasions, or if the caregiver had past legal problems that were not directly "
                    + "related to the rated child’s safety.");
            desc8c_5.add("A rating of “2” or “3” indicates that offences that are/were directly related to the child’s safety.");
            desc8c_5.add("If the siblings or relatives of the child have legal issues that may affect the child, you may want to rate "
                    + "Safety (item 8C.6) instead. ");

            List<String> quesToConsider8c_5 = new ArrayList<>();
            quesToConsider8c_5.add("Has the caregiver had "
                    + "any involvement with the "
                    + "law (e.g., "
                    + "police/prison/criminal "
                    + "justice system, "
                    + "bankruptcy)?\n"
                    + "➢ If yes, what was the "
                    + "nature of the "
                    + "offence/involvement?\n"
                    + "➢ Was the offence "
                    + "related in any way to "
                    + "the child’s safety?");

            Map<Integer, String> rating8c_5 = new HashMap<>();
            rating8c_5.put(0, "Caregiver has no known legal difficulties.");
            rating8c_5.put(1, "Caregiver has a history of legal problems, but is currently not "
                    + "involved with the legal system. Past legal problems are also not "
                    + "directly related to child’s safety (e.g., theft).");
            rating8c_5.put(2, "Caregiver has legal problems, and is currently involved in the "
                    + "legal system;  "
                    + "OR\n"
                    + "Caregiver has past history of legal problems that may be related "
                    + "to child’s safety (e.g., abuse, involvement with drugs).  ");
            rating8c_5.put(3, "Caregiver has serious current or pending legal difficulties that "
                    + "place him/her at risk for incarceration. Caregiver needs an "
                    + "immediate comprehensive, community‐based intervention.");

            q8c_5.setQuestionDescription(desc8c_5);
            q8c_5.setQuestionToConsider(quesToConsider8c_5);
            q8c_5.setRatingsDefinition(rating8c_5);

            ques8c.add(q8c_5);

            //create 8c.6
            MainQuestionEntity q8c_6 = new MainQuestionEntity("8C.6", "C. Safety");
            List<String> desc8c_6 = new ArrayList<>();
            desc8c_6.add("This item describes whether the caregiver is able to provide a safe environment for the child.");
            desc8c_6.add("This item does not describe situations in which the caregiver is unable to prevent a child from hurting "
                    + "him/herself despite well‐intentioned efforts.");
            desc8c_6.add("A “2” or “3” on this item requires the involvement of relevant authorities such as the Child Protective "
                    + "Service.");

            List<String> quesToConsider8c_6 = new ArrayList<>();
            quesToConsider8c_6.add("Is the caregiver able to protect the "
                    + "child from harm in the household?  ");
            quesToConsider8c_6.add("Are there individuals living in the "
                    + "household or visiting the household "
                    + "that may pose harm to the safety of "
                    + "the child?");
            quesToConsider8c_6.add("Are there any safety concerns when "
                    + "the child is with the caregiver in "
                    + "his/her house? (e.g., from a child "
                    + "protection perspective). ");
            quesToConsider8c_6.add("Has MSF/Child Protective Service "
                    + "ever been involved with the "
                    + "caregiver/caregiver’s family?\n"
                    + "➢ How did they get involved?  \n"
                    + "➢ Are they currently involved?");

            Map<Integer, String> rating8c_6 = new HashMap<>();
            rating8c_6.put(0, "Household is safe and secure. Child is at no risk of "
                    + "harm from others.");
            rating8c_6.put(1, "Concerns exist about the safety of the child due to "
                    + "history of others within the vicinity that might be "
                    + "abusive;\n"
                    + "OR\n"
                    + "The household environment poses some concerns of "
                    + "physical danger to the child.");
            rating8c_6.put(2, "Child isin danger from one or more individuals within "
                    + "the household/vicinity, but there are people around "
                    + "to supervise access;\n"
                    + "OR\n"
                    + "The household environment poses some physical "
                    + "danger to the child (e.g., lack of window grilles in "
                    + "high‐rise flats)");
            rating8c_6.put(3, "Child is in immediate danger from one or more "
                    + "individuals with unsupervised access within the "
                    + "household/vicinity;\n"
                    + "OR\n"
                    + "The household environment poses immediate "
                    + "and/or severe physical danger to the child.");

            q8c_6.setQuestionDescription(desc8c_6);
            q8c_6.setQuestionToConsider(quesToConsider8c_6);
            q8c_6.setRatingsDefinition(rating8c_6);

            ques8c.add(q8c_6);

            DomainEntity domain8c = domainSessionBean.createNewDomain(cc);
            AgeGroupEntity age20 = ageGroupSessionBean.createNewAgeGroupForDomain(ccage, domain8c.getDomainId());
            for (MainQuestionEntity questions : ques8c) {
                questionsSessionBean.createMainQuestionForAgeGroup(age20.getAgeGroupId(), questions);
            }

            // ====================== Create Transition To Adulthood Module =================================
            DomainEntity adulthood = new DomainEntity("Transition To AdultHood", null, true, 1, false);
            AgeGroupEntity adultage = new AgeGroupEntity("17+");
            List<MainQuestionEntity> ques9 = new ArrayList<>();

            //create 9.1
            MainQuestionEntity q9_1 = new MainQuestionEntity("9.1", "Placement/Housing");
            List<String> desc9_1 = new ArrayList<>();
            desc9_1.add("This item refers to the stability of the family’s housing. Youths may be living "
                    + "independently or living with caregivers/other family members.");
            desc9_1.add("Rate this item if there are any needs pertaining to housing or placement such as: \n"
                    + "‐ Difficulties paying rent, utilities or mortgage (if youth is living independently/planning to live "
                    + "independently) \n"
                    + "‐ Concerns about the youth’s placement with caregivers/other adults (e.g., conflicts with caregiver "
                    + "that may result in the youth being displaced from the home)");
            desc9_1.add("For youth who are in residential homes, rate this item if there are concerns about the youth’s housing\n"
                    + "or placement after discharge.");

            List<String> quesToConsider9_1 = new ArrayList<>();
            quesToConsider9_1.add("Is the youth’s "
                    + "housing/placement stable?");
            quesToConsider9_1.add("Are there any concerns that "
                    + "the youth may need to move "
                    + "in the near future?");
            quesToConsider9_1.add("For youth currently residing in "
                    + "residential care：\n"
                    + "➢ Has a placement "
                    + "setting/housing been "
                    + "identified?");
            quesToConsider9_1.add("For youth currently residing in "
                    + "residential care：\n"
                    + "➢ Are there any concerns "
                    + "about the identified "
                    + "placement setting/housing?");

            Map<Integer, String> rating9_1 = new HashMap<>();
            rating9_1.put(0, "Youth has stable housing/placement for the foreseeable "
                    + "future.  ");
            rating9_1.put(1, "Youth has relatively stable housing/placement but either has "
                    + "moved in the past three months  \n"
                    + "OR\n"
                    + "There are indications of housing/placement issues that might "
                    + "force him/her to move in the next three months.\n"
                    + "OR\n"
                    + "The youth is having difficulties in paying utilities, rent or a "
                    + "mortgage which indicates that monitoring the youth’s housing "
                    + "situation is necessary to prevent future housing/placement "
                    + "disruption");
            rating9_1.put(2, "Youth has moved multiple times in the past year. "
                    + "Housing/placement is unstable;  \n"
                    + "OR\n"
                    + "Housing/placement has been identified but serious concerns "
                    + "remain. ");
            rating9_1.put(3, "Youth has experienced periods of homelessness in the past six "
                    + "months;\n"
                    + "OR\n"
                    + "No housing/placement has been identified.  ");

            q9_1.setQuestionDescription(desc9_1);
            q9_1.setQuestionToConsider(quesToConsider9_1);
            q9_1.setRatingsDefinition(rating9_1);

            ques9.add(q9_1);

            //create 9.2
            MainQuestionEntity q9_2 = new MainQuestionEntity("9.2", "Financial Resources");
            List<String> desc9_2 = new ArrayList<>();
            desc9_2.add("Thisitem rates whether the youth has sufficient financial resources to support "
                    + "him/herself when living independently.");
            desc9_2.add("Poverty/financial hardship is one of the most common and devastating challenge that a youth can face. "
                    + "This item describes the degree to which financial problems are a current challenge for him/her");
            desc9_2.add("The item is scored ‘0’ to indicate a ‘good enough’ level of financial resources. The youth may not be rich "
                    + "but has enough money to take care of basic needs");
            desc9_2.add("When rating this item, you may want to consider whether the youth has sufficient financial resources "
                    + "for:\n"
                    + "‐ Rent (if no other housing options are available)\n"
                    + "‐ Food  \n"
                    + "‐ Clothing\n"
                    + "‐ Childcare expenses (if the youth has a child)\n"
                    + "‐ Other expenses");

            List<String> quesToConsider9_2 = new ArrayList<>();
            quesToConsider9_2.add("Will the youth be expected to live "
                    + "independently?");
            quesToConsider9_2.add("Does the youth have sufficient "
                    + "financial resources to live "
                    + "independently?  ");
            quesToConsider9_2.add("Is the youth struggling to pay "
                    + "his/her bills, rent or have enough "
                    + "money for food and "
                    + "transportation?");

            Map<Integer, String> rating9_2 = new HashMap<>();
            rating9_2.put(0, "No evidence of financial difficulties. Youth has "
                    + "sufficient financial resources necessary to meet "
                    + "needs. ");
            rating9_2.put(1, "Mild financial difficulties. Youth has financial "
                    + "resources necessary to meet most needs, with "
                    + "careful budgeting, although some limitations "
                    + "exist. The youth has not incurred any debts.");
            rating9_2.put(2, "Moderate financial difficulties. Youth has "
                    + "financial difficulties that limit his/her ability to "
                    + "meetsignificant basic needs. The youth may face "
                    + "difficulties in paying instalments or paying off "
                    + "his/her debts;  \n"
                    + "OR\n"
                    + "The youth is currently receiving financial "
                    + "assistance and/or subsidies to meet his/her "
                    + "needs");
            rating9_2.put(3, "Severe financial difficulties and requires "
                    + "immediate assistance support. Youth is "
                    + "experiencing financial hardship/poverty, and is "
                    + "unable to meet his/her basic needs, even with "
                    + "the current financial assistance provided to "
                    + "him/her.");
            rating9_2.put(4, "Youth is dependent on caregiver(s)’ financial "
                    + "resources to meet his/her needs.  ");

            q9_2.setQuestionDescription(desc9_2);
            q9_2.setQuestionToConsider(quesToConsider9_2);
            q9_2.setRatingsDefinition(rating9_2);

            ques9.add(q9_2);

            //create 9.3
            MainQuestionEntity q9_3 = new MainQuestionEntity("9.3", "Treatment Adherence");
            List<String> desc9_3 = new ArrayList<>();
            desc9_3.add("This rating focuses on the level of a youth’s willingness and participation in "
                    + "taking prescribed medications and/or attending therapy/counselling sessions");

            List<String> quesToConsider9_3 = new ArrayList<>();
            quesToConsider9_3.add("Does the youth "
                    + "require prescription "
                    + "medication or "
                    + "therapy/counselling "
                    + "session/s?");
            quesToConsider9_3.add("Does the youth "
                    + "need reminders to "
                    + "take his/her "
                    + "medication and/or "
                    + "attend his/her "
                    + "therapy/counselling "
                    + "session/s? ");

            Map<Integer, String> rating9_3 = new HashMap<>();
            rating9_3.put(0, "Youth takes the prescribed medications and/or attends "
                    + "therapy/counselling sessions without reminders.");
            rating9_3.put(1, "Youth takes medications and/or attends therapy/counselling "
                    + "sessions routinely but sometimes needs reminders. A history of non‐ "
                    + "compliance with treatment but no current problems would be rated "
                    + "here.");
            rating9_3.put(2, "Youth is somewhat non­compliant. He/she may be resistant to taking "
                    + "medications and/or attending therapy/counselling sessions or may "
                    + "tend to overuse his/her medications. He/she might comply with "
                    + "treatment for periods of time (1‐2 weeks) but generally does not "
                    + "sustain taking medication in prescribed dose or protocol and/or "
                    + "attending therapy/counselling sessions.");
            rating9_3.put(3, "Youth has refused to take prescribed medications and/or attend "
                    + "therapy/counselling sessions during the past 30 days \n"
                    + "OR\n"
                    + "Youth has abused his/her medications to a significant degree (i.e. "
                    + "overdosing or over using medications to a dangerous degree)");
            rating9_3.put(4, "Youth is not currently on any medication and/or attending "
                    + "therapy/counselling sessions.");

            q9_3.setQuestionDescription(desc9_3);
            q9_3.setQuestionToConsider(quesToConsider9_3);
            q9_3.setRatingsDefinition(rating9_3);

            ques9.add(q9_3);

            //create 9.4
            MainQuestionEntity q9_4 = new MainQuestionEntity("9.4", "Relationship with Significant Others");
            List<String> desc9_4 = new ArrayList<>();
            desc9_4.add("This item rates the youth’s current romantic relationships.");
            desc9_4.add("If the youth is currently single, rate this item a “0”");

            List<String> quesToConsider9_4 = new ArrayList<>();
            quesToConsider9_4.add("Is the youth currently "
                    + "in a romantic "
                    + "relationship?");
            quesToConsider9_4.add("Does the youth have "
                    + "any difficulties in "
                    + "his/her relationship?");
            quesToConsider9_4.add("Do these difficulties "
                    + "cause the youth "
                    + "stress/affect his/her "
                    + "functioning?");
            quesToConsider9_4.add("Has the youth’s "
                    + "significant other ever "
                    + "been violent to "
                    + "him/her, or vice versa?");

            Map<Integer, String> rating9_4 = new HashMap<>();
            rating9_4.put(0, "No evidence of problems with partner relationships. Youth has "
                    + "positive partner relationships, or he/she has maintained positive "
                    + "partner relationships in the past but is currently not in a romantic "
                    + "relationship.  ");
            rating9_4.put(1, "Mild difficulties with partner relationships."
                    + "Youth generally has positive partner relationships, but there are "
                    + "some concerns. A youth with a history of problems with partner "
                    + "relationship/s but is currently single would be rated here. "
                    + "For example, youth may be in a relationship that impedes his/her "
                    + "healthy development. Relationship may cause the youth some "
                    + "stress, but not to the extent where it affectsthe youth’sfunctioning.");
            rating9_4.put(2, "Moderate difficulties with partner relationships. "
                    + "For example, youth has had a recent history of being in a "
                    + "domestically violent relationship (either as the victim or "
                    + "perpetrator) and/or a recent history of being in a relationship where "
                    + "he/she was overly dependent on his/her partner.  Relationship may "
                    + "cause the youth a moderate amount of stress and affects the "
                    + "youth’s functioning");
            rating9_4.put(3, "Significant difficulties with partner relationships. "
                    + "For example, youth is involved in a negative or domestically violent "
                    + "relationship and/or a relationship where he/she istotally dependent "
                    + "on his/her partner. Relationship may cause the youth a significant "
                    + "amount of stress and affect the youth’s functioning substantially.");

            q9_4.setQuestionDescription(desc9_4);
            q9_4.setQuestionToConsider(quesToConsider9_4);
            q9_4.setRatingsDefinition(rating9_4);

            ques9.add(q9_4);

            //create 9.5
            MainQuestionEntity q9_5 = new MainQuestionEntity("9.5", "Victimisation");
            List<String> desc9_5 = new ArrayList<>();
            desc9_5.add("This item rates a history of and current risk for victimisation. There must be a power "
                    + "differential between the two parties, with the victim being made to do something negative against "
                    + "his/her will.");

            List<String> quesToConsider9_5 = new ArrayList<>();
            quesToConsider9_5.add("Has the youth’s "
                    + "been victimised in "
                    + "the past year?");
            quesToConsider9_5.add("Is the youth at risk "
                    + "of re‐victimisation?");

            Map<Integer, String> rating9_5 = new HashMap<>();
            rating9_5.put(0, "Youth does not have a history of victimisation. Youth is not "
                    + "presently at risk for re­victimisation.");
            rating9_5.put(1, "Youth has a history of victimisation. Youth may have been a victim "
                    + "of assault or crime on one or more occasion in the past, but no clear "
                    + "pattern of victimisation exists, or there has been no significant "
                    + "victimisation in the past year. This individual should be monitored "
                    + "to assess ongoing risk for potential re‐victimisation.");
            rating9_5.put(2, "Youth has been recently victimised (within the past year). Past "
                    + "victimisations may include physical or sexual abuse, significant "
                    + "psychological abuse by family or friend, extortion or violent crime. "
                    + "There are ongoing concerns about potential future re-victimisation");
            rating9_5.put(3, "Youth has been recently victimised and is in acute risk of revictimisation. "
                    + "Examples include working as a prostitute or living in an abusive "
                    + "relationship.");

            q9_5.setQuestionDescription(desc9_5);
            q9_5.setQuestionToConsider(quesToConsider9_5);
            q9_5.setRatingsDefinition(rating9_5);

            ques9.add(q9_5);

            //create 9.6
            MainQuestionEntity q9_6 = new MainQuestionEntity("9.6", "Employment Functioning");
            List<String> desc9_6 = new ArrayList<>();
            desc9_6.add("This item rates the performance of the youth in work settings. The "
                    + "performance can include issues of behaviours, attendance, attitude or productivity");

            List<String> quesToConsider9_6 = new ArrayList<>();
            quesToConsider9_6.add("Is the youth currently "
                    + "working?");
            quesToConsider9_6.add("Does the youth have "
                    + "any difficulties holding "
                    + "down a job?\n"
                    + "➢ If yes, does the "
                    + "youth have a history "
                    + "of frequent job loss?");

            Map<Integer, String> rating9_6 = new HashMap<>();
            rating9_6.put(0, "No evidence of problems at work. The youth is gainfully "
                    + "employed.");
            rating9_6.put(1, "A mild degree of problems with work functioning. "
                    + "The youth may have some problems in the work environment "
                    + "involving attendance, productivity or relations with others but is "
                    + "able to meet expectations.  \n"
                    + "OR\n"
                    + "The youth has been unemployed for less than 3 months. The "
                    + "youth may change jobs occasionally but is able to keep an "
                    + "employment for a sustainable duration. \n"
                    + "OR\n"
                    + "The youth is holding on to a temporary or part‐time job with no "
                    + "job security");
            rating9_6.put(2, "A moderate degree of school or work problems. "
                    + "The youth presents with disruptive behaviour in work "
                    + "environment and/or difficulties with performing up to "
                    + "expectation. Supervisory personnel are likely to have warned the "
                    + "youth about problems with his/her work performance.  \n"
                    + "OR\n"
                    + "A history of frequent change of employment and significant "
                    + "period of unemployment for 4 to 6 months.");
            rating9_6.put(3, "A severe degree of school or work problems. "
                    + "The youth presents with aggressive behaviour toward others or "
                    + "severe attendance problems. The youth/adult may be recently "
                    + "fired, at very high risk of being fired (e.g., on notice) or currently "
                    + "unemployed due to poor attitude.  \n"
                    + "OR\n"
                    + "The youth is unemployed for more than 6 months and is "
                    + "unmotivated to secure an employment. \n"
                    + "OR\n"
                    + "The youth is depending on unlawful means, (e.g., drug trafficking, "
                    + "loan shark activities etc.), to generate income.");
            rating9_6.put(4, "A youth is not working due to valid reasons such as child care "
                    + "(e.g., homemakers) and schooling OR a youth who is dependent "
                    + "on caregiver/s’ resources");

            q9_6.setQuestionDescription(desc9_6);
            q9_6.setQuestionToConsider(quesToConsider9_6);
            q9_6.setRatingsDefinition(rating9_6);

            ques9.add(q9_6);

            //create 9.7
            MainQuestionEntity q9_7 = new MainQuestionEntity("9.7", "Needs as a Caregiver");
            List<String> desc9_7 = new ArrayList<>();
            desc9_7.add("This item evaluates a youth’s needs that arise as a result of being a caregiver");
            desc9_7.add("Examples of caregiving responsibilities that would be rated here: \n"
                    + "• A youth who is a young parent  \n"
                    + "• A youth who is currently pregnant\n"
                    + "• A youth who is individually responsible for taking care of a sibling, parent or grandparent ");
            desc9_7.add("When rating this item, please consider whether the youth has needs in ANY of the following areas:  \n"
                    + "• Social Resources: Youth has limited or no family or social networks that may be able to help youth "
                    + "with caregiving.\n"
                    + "• Caregiving Stress: Youth has problems managing the stress of caregiving. This stress interferes or "
                    + "prevents the youth from providing care.\n"
                    + "• Basic Care/Daily Living: Youth needs assistance or is not able to provide for the basic needs (e.g. "
                    + "shelter, food, safety, and clothing) of his/her dependents.\n"
                    + "• Safety by the Youth: Youth has difficulties providing a safe environment for his/her dependents. The "
                    + "youth’s dependent is in danger from one or more individuals within the household/vicinity and/or "
                    + "the household environment poses physical danger to the dependents");

            List<String> quesToConsider9_7 = new ArrayList<>();
            quesToConsider9_7.add("Is the youth in any roles "
                    + "where he/she cares for "
                    + "someone else – parent, "
                    + "grandparent, sibling, or "
                    + "his/her own child?\n"
                    + "➢ If yes, how well does "
                    + "he/she fit that role?");
            quesToConsider9_7.add("Does the youth require "
                    + "assistance to develop good "
                    + "caregiving skills?");

            Map<Integer, String> rating9_7 = new HashMap<>();
            rating9_7.put(0, "No evidence that the youth has problems carrying out his/her "
                    + "caregiving responsibilities. ");
            rating9_7.put(1, "Mild difficulties with carrying out his/her caregiving "
                    + "responsibilities. A youth with a history of problems with "
                    + "caregiving responsibilities would be rated here");
            rating9_7.put(2, "Moderate difficulties with carrying out his/her caregiving "
                    + "responsibilities. The youth is struggling with these "
                    + "responsibilities or the caregiving issues are currently "
                    + "interfering with the youth’s functioning in other life domains");
            rating9_7.put(3, "Significant difficulties with carrying out his/her caregiving "
                    + "responsibilities. The youth is not able to meet these "
                    + "responsibilities orthese responsibilities make it impossible for "
                    + "the youth to function in other life domains. The youth has the "
                    + "potential to abuse or be neglectful in his/her responsibilities ");
            rating9_7.put(4, "Youth is not a parent or in any caregiving role.");

            q9_7.setQuestionDescription(desc9_7);
            q9_7.setQuestionToConsider(quesToConsider9_7);
            q9_7.setRatingsDefinition(rating9_7);

            ques9.add(q9_7);

            DomainEntity domain9 = domainSessionBean.createNewDomain(adulthood);
            AgeGroupEntity age21 = ageGroupSessionBean.createNewAgeGroupForDomain(adultage, domain9.getDomainId());
            for (MainQuestionEntity questions : ques9) {
                questionsSessionBean.createMainQuestionForAgeGroup(age21.getAgeGroupId(), questions);
            }

            // ============= Create Residential Care Module =================
            DomainEntity rescare = new DomainEntity("Residential Care", null, true, 1, false);
            AgeGroupEntity rescareage = new AgeGroupEntity("0-20");
            List<MainQuestionEntity> ques10 = new ArrayList<>();

            //create 10.1
            MainQuestionEntity q10_1 = new MainQuestionEntity("10.1", "Community Outings");
            List<String> desc10_1 = new ArrayList<>();
            desc10_1.add("This item examines the extent a child’s outings with volunteers or other "
                    + "individuals (not Home Leave) have been restricted due to his/her social/behavioural problems.");
            desc10_1.add("A rating “2” or “3” will indicate that a child should be referred for intervention services to address the "
                    + "social/behavioural problems");

            List<String> quesToConsider10_1 = new ArrayList<>();
            quesToConsider10_1.add("Does the child go for outings "
                    + "organised by staff or "
                    + "volunteers?\n"
                    + "• If yes, does the child "
                    + "usually behave well "
                    + "during these outings?");

            Map<Integer, String> rating10_1 = new HashMap<>();
            rating10_1.put(0, "Child often or always meets expectations for socially "
                    + "appropriate behaviours during community‐based outings "
                    + "with staff and peers");
            rating10_1.put(1, "Child needs occasional redirection, encouragement or "
                    + "limit setting to ensure acceptable behaviour during "
                    + "community‐based activities with staff and peers.");
            rating10_1.put(2, "Child requires frequent redirection, encouragement or "
                    + "limit­setting to maintain acceptable behaviour during "
                    + "community‐based activities with staff and peers.");
            rating10_1.put(3, "Child’s activities have been greatly restricted due to the "
                    + "high likelihood of risky, unacceptable or disruptive "
                    + "behaviours occurring in the community.");
            rating10_1.put(4, "No community outings are occurring or planned");

            q10_1.setQuestionDescription(desc10_1);
            q10_1.setQuestionToConsider(quesToConsider10_1);
            q10_1.setRatingsDefinition(rating10_1);

            ques10.add(q10_1);

            //create 10.2
            MainQuestionEntity q10_2 = new MainQuestionEntity("10.2", "Home Leave");
            List<String> desc10_2 = new ArrayList<>();
            desc10_2.add("This item indicates the intensity of case management that will be needed in order to "
                    + "maintain the child’s relationship with his/her parent figure/family. It may also indicate the "
                    + "consideration of termination of parental rights.");

            List<String> quesToConsider10_2 = new ArrayList<>();
            quesToConsider10_2.add("Does the child have "
                    + "home leave or home "
                    + "visits?\n"
                    + "• If yes, do you "
                    + "have any "
                    + "concerns when "
                    + "the child is on "
                    + "home leave (e.g., "
                    + "about the child’s "
                    + "safety, parents’ "
                    + "ability to manage "
                    + "child during home "
                    + "leave etc.)?");

            Map<Integer, String> rating10_2 = new HashMap<>();
            rating10_2.put(0, "Home leave is occurring with few or no obstacles");
            rating10_2.put(1, "Home leave is of some concern due to the parent‐child relationship "
                    + "status, transportation arrangements, potentially risky child "
                    + "behaviours away from a highly structured setting, parenting "
                    + "weaknesses, or similar concerns. \n"
                    + "OR\n"
                    + "Home leave has not occurred yet, but there is no cause for concern.");
            rating10_2.put(2, "Home leave is of moderate concern due to parent‐child relationship "
                    + "problems, transportation arrangements, potentially risky child "
                    + "behaviours away from a highly structured setting, parenting deficits, "
                    + "or similar concerns.");
            rating10_2.put(3, "Home leave is of serious concern due to parent‐child relationship "
                    + "problems, transportation obstacles, potentially risky child behaviours "
                    + "away from a highly structured setting, parenting deficits, or similar "
                    + "concerns.\n"
                    + "OR\n"
                    + "No caregiver has been identified. No home leave is occurring or "
                    + "planned.");

            q10_2.setQuestionDescription(desc10_2);
            q10_2.setQuestionToConsider(quesToConsider10_2);
            q10_2.setRatingsDefinition(rating10_2);

            ques10.add(q10_2);

            //create 10.3
            MainQuestionEntity q10_3 = new MainQuestionEntity("10.3", "Caregiver Participation");
            List<String> desc10_3 = new ArrayList<>();
            desc10_3.add("This item describes the extent the caregiver is participating in essential "
                    + "family related treatment that the assessed child has been referred for (e.g., parenting, counselling, "
                    + "family therapy sessions)");

            List<String> quesToConsider10_3 = new ArrayList<>();
            quesToConsider10_3.add("Does the parent "
                    + "participate actively "
                    + "in family related "
                    + "interventions such "
                    + "as family "
                    + "conferences?");

            Map<Integer, String> rating10_3 = new HashMap<>();
            rating10_3.put(0, "Adequate to good participation by caregiver in family related "
                    + "interventions.");
            rating10_3.put(1, "Caregiver occasionally misses family related services but is "
                    + "communicating with staff and is open to receiving support, education, "
                    + "and information");
            rating10_3.put(2, "Caregiver is under­involved with family related treatmentservices or is "
                    + "uncooperative with the child’s treatment programme");
            rating10_3.put(3, "Caregiver is nearly or completely absent from all family related "
                    + "treatment services.    The caregiver is communicating a desire to not "
                    + "participate in the child’s treatment programme. \n"
                    + "OR\n"
                    + "No caregiver or parent figure is currently identified.");

            q10_3.setQuestionDescription(desc10_3);
            q10_3.setQuestionToConsider(quesToConsider10_3);
            q10_3.setRatingsDefinition(rating10_3);

            ques10.add(q10_3);

            //create 10.4
            MainQuestionEntity q10_4 = new MainQuestionEntity("10.4", "Progress Towards Goals");
            List<String> desc10_4 = new ArrayList<>();
            desc10_4.add("This is another item that has implications on the decision to discharge, "
                    + "home leave or visiting arrangements. Please rate thisitem based on the child’s progress toward goals");

            List<String> quesToConsider10_4 = new ArrayList<>();
            quesToConsider10_4.add("Has the child been making "
                    + "good progress in the home?");
            quesToConsider10_4.add("Is the child on track in "
                    + "meeting the home’s\n"
                    + "treatment goals/care plan?");

            Map<Integer, String> rating10_4 = new HashMap<>();
            rating10_4.put(0, "Satisfactory to good rate of progress toward treatment plan "
                    + "goals and objectives.");
            rating10_4.put(1, "Somewhat slow or inconsistent rate of progress, but overall "
                    + "improvement is occurring");
            rating10_4.put(2, "Moderately slow rate of progress in achieving treatment "
                    + "goals and objectives. Overall improvements of any size may "
                    + "be difficult to identify week to week");
            rating10_4.put(3, "No identifiable progress or the child’s functioning is "
                    + "regressing over weeks or months");

            q10_4.setQuestionDescription(desc10_4);
            q10_4.setQuestionToConsider(quesToConsider10_4);
            q10_4.setRatingsDefinition(rating10_4);

            ques10.add(q10_4);

            //create 10.5
            MainQuestionEntity q10_5 = new MainQuestionEntity("10.5", "Preparation For Discharge Placement");
            List<String> desc10_5 = new ArrayList<>();
            desc10_5.add("When rating this item, information from all preceding "
                    + "items can be considered");

            List<String> quesToConsider10_5 = new ArrayList<>();
            quesToConsider10_5.add("Are there any plans for "
                    + "discharge?");
            quesToConsider10_5.add("Do you feel that the child is "
                    + "ready for discharge?");

            Map<Integer, String> rating10_5 = new HashMap<>();
            rating10_5.put(0, "Ready for discharge");
            rating10_5.put(1, "Nearly ready, but some concerns remain");
            rating10_5.put(2, "Not yet ready, but discharge setting identified.    Serious "
                    + "concerns remain");
            rating10_5.put(3, "Not ready, no discharge setting identified. Serious "
                    + "concerns remain.");

            q10_5.setQuestionDescription(desc10_5);
            q10_5.setQuestionToConsider(quesToConsider10_5);
            q10_5.setRatingsDefinition(rating10_5);

            ques10.add(q10_5);

            DomainEntity domain10 = domainSessionBean.createNewDomain(rescare);
            AgeGroupEntity age22 = ageGroupSessionBean.createNewAgeGroupForDomain(rescareage, domain10.getDomainId());
            for (MainQuestionEntity questions : ques10) {
                questionsSessionBean.createMainQuestionForAgeGroup(age22.getAgeGroupId(), questions);
            }
            // ============== Create Trauma SubModule ================
            List<String> trdesc = new ArrayList<>();
            trdesc.add("Rate this module ONLY IF\n"
                    + "Item 5.2. “Adjustment to Trauma” AND/OR Item 6.1. “Traumatic Events”\n"
                    + "was rated “1”, “2” or “3”");
            trdesc.add("All items in this module are static/historical. This means that:\n"
                    + "‐ The 30‐day window does not apply here. Please rate whether the child has experienced these\n"
                    + "negative life events within the child’s lifetime.\n"
                    + "‐ Ratings for these items are not expected to change, with the following exceptions:\n"
                    + "o The rating may increase when there is a new or more severe trauma experience.\n"
                    + "o The rating may decrease when an allegation of abuse has been proven to be a false\n"
                    + "allegation.\n"
                    + "➢ Note down the reason(s) for the change(s) in the notes section.");
            trdesc.add("Trauma refers to any negative life event that has an impact on the child’s functioning");
            trdesc.add("Traumatic events are typically scary, fear‐inducing or life‐threatening and may result in someone "
                    + "getting hurt and/or death.  ");
            trdesc.add("This module does NOT rate trauma symptoms. Trauma symptoms should be captured in item 5.2 "
                    + "“Adjustment to Trauma”");

            SubModuleEntity trmod = new SubModuleEntity("Trauma (TR) Module", trdesc, true);
            List<SubQuestionEntity> quesTR = new ArrayList<>();

            //create TR1
            SubQuestionEntity tr1 = new SubQuestionEntity("TR1", "Neglect");
            List<String> desctr1 = new ArrayList<>();
            desctr1.add("This item rates whether the child has experienced neglect.");
            desctr1.add("Examples of neglect:");
            desctr1.add("Caregiver was emotionally and/or physically unavailable to the infant in the months immediately "
                    + "following birth.");
            desctr1.add("Child was left alone without adult supervision");
            desctr1.add("Caregiver fails to meet the basic needs (e.g., food, shelter, clothing, medical attention) of the child.");

            List<String> quesToConsiderTR1 = new ArrayList<>();
            quesToConsiderTR1.add("Has the child ever "
                    + "been left without "
                    + "adult supervision for "
                    + "very long periods of "
                    + "time?");
            quesToConsiderTR1.add("Has the child’s "
                    + "caregiver ever failed "
                    + "to provide for his/her "
                    + "basic needs?");

            Map<Integer, String> ratingTR1 = new HashMap<>();
            ratingTR1.put(0, "There is no evidence that child has experienced neglect");
            ratingTR1.put(1, "Child has experienced minor or occasional neglect. "
                    + "For example, an infant’s primary caregiver may have experienced "
                    + "some minor or transient stressors which made him/her less "
                    + "available to the infant. A child may have been left at home alone for "
                    + "a short period of time with no adult supervision or there may be "
                    + "occasional failure to provide adequate supervision of and/or basic "
                    + "needs to the child.");
            ratingTR1.put(2, "Child has experienced moderate neglect. "
                    + "For example, an infant’s primary caregiver experienced a moderate "
                    + "level of stress sufficient to make him/her significantly less "
                    + "emotionally and physically available to the infant after birth. A child "
                    + "may have experienced unintended failure by the caregiver to "
                    + "provide adequate supervision of and/or basic needsto the child with "
                    + "corrective action.");
            ratingTR1.put(3, "Child has experienced severe neglect. "
                    + "For example, an infant’s primary caregiver was unavailable to the "
                    + "infant to the extent that the infant’s emotional or physical well‐ "
                    + "being was severely compromised. A child may have experienced "
                    + "failure by the caregiver to provide supervision of and/or basic needs "
                    + "to the child on a regular basis");

            tr1.setQuestionDescription(desctr1);
            tr1.setQuestionToConsider(quesToConsiderTR1);
            tr1.setRatingsDefinition(ratingTR1);

            quesTR.add(tr1);

            //create TR2
            SubQuestionEntity tr2 = new SubQuestionEntity("TR2", "Physical Abuse");
            List<String> desctr2 = new ArrayList<>();
            desctr2.add("This item rates whether the child has been physically harmed by the caregiver (e.g., "
                    + "through the caregiver’s use of harsh physical punishment).");
            desctr2.add("Corporal punishment is rated here except in situations where it is within acceptable limits of the "
                    + "Singaporean culture (e.g., a hand slap when child tries to touch something hot, limited caning/spanking "
                    + "with explanation or as a backup for time out).");
            desctr2.add("When in doubt on whether the caning administered is acceptable, consult other colleagues");
            desctr2.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR2 = new ArrayList<>();
            quesToConsiderTR2.add("Has the child ever been "
                    + "physically harmed by the "
                    + "caregiver?");
            quesToConsiderTR2.add("Has the child ever been caned "
                    + "or physically punished?\n"
                    + "➢ If yes, did the child ever "
                    + "sustain any injuries as a "
                    + "result of the punishment "
                    + "(e.g., scars, bruises)?\n"
                    + "➢ Was the child "
                    + "hospitalized/treated for "
                    + "his/her injuries?\n"
                    + "➢ How frequently did the "
                    + "harsh physical punishment "
                    + "occur?");

            Map<Integer, String> ratingTR2 = new HashMap<>();
            ratingTR2.put(0, "There is no evidence that child has experienced physical "
                    + "abuse.");
            ratingTR2.put(1, "Child has experienced one episode of physical abuse or "
                    + "there is suspicion that child has experienced physical abuse "
                    + "but no confirming evidence");
            ratingTR2.put(2, "Child has experienced a moderate level of physical abuse "
                    + "and/or repeated forms of physical punishment (e.g., hitting, "
                    + "punching)");
            ratingTR2.put(3, "Child has experienced severe and/or repeated physical "
                    + "abuse that causes sufficient physical harm to necessitate "
                    + "hospital treatment");

            tr2.setQuestionDescription(desctr2);
            tr2.setQuestionToConsider(quesToConsiderTR2);
            tr2.setRatingsDefinition(ratingTR2);

            quesTR.add(tr2);

            //create TR3
            SubQuestionEntity tr3 = new SubQuestionEntity("TR3", "Emotional Abuse");
            List<String> desctr3 = new ArrayList<>();
            desctr3.add("This item rates whether the child experienced a repeated pattern of caregiver "
                    + "behaviour or extreme incident(s) that convey to him/her that he/she is worthless, flawed, unloved, "
                    + "unwanted, endangered, and/or only of value in meeting another’s needs");
            desctr3.add("Examples of emotionally abusive behaviours:");
            desctr3.add(" Extended “silent treatment”");
            desctr3.add("Complete ignoring the child’s needs");
            desctr3.add("Verbal insults/demeaning comments/characterisations of the child");
            desctr3.add("Exploiting/corrupting the child");
            desctr3.add("Threatening to hurt the child/threatening to commit suicide");
            desctr3.add("Please rate within the child’s lifetime.");

            List<String> quesToConsiderTR3 = new ArrayList<>();
            quesToConsiderTR3.add("Has the caregiver ever "
                    + "called the child names, or "
                    + "made demeaning "
                    + "comments about him/her?");
            quesToConsiderTR3.add("Has the caregiver ever "
                    + "threatened to hurt the "
                    + "child deliberately?");
            quesToConsiderTR3.add("Has the caregiver ever "
                    + "deliberately ignored the "
                    + "child for extended periods "
                    + "of time?");

            Map<Integer, String> ratingTR3 = new HashMap<>();
            ratingTR3.put(0, "There is no evidence that child has experienced emotional "
                    + "abuse.");
            ratingTR3.put(1, "Child has experienced mild emotional abuse. "
                    + "For example, child may have experienced some insults or is "
                    + "occasionally referred to in a derogatory manner by caregivers.");
            ratingTR3.put(2, "Child has experienced moderate emotional abuse. "
                    + "For example, child may be consistently denied emotional "
                    + "attention from caregivers, insulted or humiliated on an ongoing "
                    + "basis, or intentionally isolated from others");
            ratingTR3.put(3, "Child has experienced single/multiple instances of severe "
                    + "emotional abuse or multiple instances of moderate emotional "
                    + "abuse over an extended period of time (at least one year).");

            tr3.setQuestionDescription(desctr3);
            tr3.setQuestionToConsider(quesToConsiderTR3);
            tr3.setRatingsDefinition(ratingTR3);

            quesTR.add(tr3);

            //create TR4
            SubQuestionEntity tr4 = new SubQuestionEntity("TR4", "Sexual Abuse");
            List<String> desctr4 = new ArrayList<>();
            desctr4.add("This item refers to the child being a victim of sexual perpetration as defined in the\n"
                    + "Children and Young Persons Act");
            desctr4.add("*Child sexual abuse is an interaction between a child and an adult (or another child) in which the child is\n"
                    + "used for the sexual stimulation of the perpetrator or an observer");
            desctr4.add("Sexual abuse can include both touching and non‐touching behaviours:\n"
                    + "• Touching behaviours may involve touching of the genitals, breasts or buttocks, oral‐genital "
                    + "contact and sexual intercourse.\n"
                    + "• Non­touching behaviours can include voyeurism (trying to look at a child’s naked body), "
                    + "exhibitionism (perpetrator exposing his/her private parts to the child), or exposing the child to "
                    + "pornography.");
            desctr4.add("Evidence for suspicion of sexual abuse could include evidence of sexually reactive behaviour and/or\n"
                    + "exposure to a sexualised environment and/or internet predation.");
            desctr4.add("Please rate within the child’s lifetime");
            desctr4.add("If child scores 0, please rate “N/A” for questions 4a, 4b & 4c.");
            desctr4.add("If child scores 1, 2 or 3, please complete questions 4a, 4b & 4c.");

            List<String> quesToConsiderTR4 = new ArrayList<>();
            quesToConsiderTR4.add("Has the child ever been "
                    + "sexually abused?\n"
                    + "➢ If yes, how many "
                    + "times was the child "
                    + "sexually abused?");
            quesToConsiderTR4.add("What type of sexual "
                    + "abuse? (E.g., molest, "
                    + "penetration, rape "
                    + "resulting in injury?)");

            Map<Integer, String> ratingTR4 = new HashMap<>();
            ratingTR4.put(0, "There is no evidence that child has experienced sexual abuse");
            ratingTR4.put(1, "There is suspicion that the child has experienced sexual abuse or "
                    + "the child has experienced mild sexual abuse including but not "
                    + "limited to direct exposure to sexually explicit materials. Children "
                    + "who have experienced secondary sexual abuse (e.g., witnessing a "
                    + "sibling being sexually abused) would be rated here.");
            ratingTR4.put(2, "Child has experienced one or a couple of incidents ofsexual abuse "
                    + "that were not chronic or severe."
                    + "For example, a child who has experienced molestation without\n"
                    + "penetration on a single occasion.  ");
            ratingTR4.put(3, "Child has experienced severe or chronic sexual abuse with "
                    + "multiple episodes orlasting over an extended period oftime.  This "
                    + "abuse may have involved penetration, multiple perpetrators, "
                    + "and/or associated with physical injury.");

            tr4.setQuestionDescription(desctr4);
            tr4.setQuestionToConsider(quesToConsiderTR4);
            tr4.setRatingsDefinition(ratingTR4);

            quesTR.add(tr4);

            //create TR4a
            SubQuestionEntity tr4a = new SubQuestionEntity("TR4a", "Emotional Closeness to Perpetrator (PEER/NON-PEER)");
            List<String> desctr4a = new ArrayList<>();
            desctr4a.add("This item should only be rated if Item 4 (Sexual Abuse) was rated “1”, “2” or “3”.\n"
                    + "Otherwise, rate “NA” if Item 4 (Sexual Abuse) was rated “0”");
            desctr4a.add("Remember to circle/indicate in the notes section whether the alleged Perpetrator is a:\n"
                    + "• Peer (age difference between Perpetrator and victim is less than 5 years) or\n"
                    + "• Non‐Peer (age difference between Perpetrator and victim is more than 5 years).");
            desctr4a.add("For extended family members (relatives such as uncles, aunts, nephews, etc.), a rating “2” should be "
                    + "given if the relative is not living within the same household. ");
            desctr4a.add("If the child has experienced more than 1 incident of sexual abuse with different perpetrators, rate the\n"
                    + "perpetrator who was closest to the child.  ");
            desctr4a.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR4a = new ArrayList<>();
            quesToConsiderTR4a.add("Who was the "
                    + "perpetrator?");
            quesToConsiderTR4a.add("What kind of "
                    + "relationship did the "
                    + "child have with the "
                    + "perpetrator?");

            Map<Integer, String> ratingTR4a = new HashMap<>();
            ratingTR4a.put(0, "Perpetrator was a stranger at the time of the abuse.");
            ratingTR4a.put(1, "Perpetrator was known to the child at the time of event but only as "
                    + "an acquaintance.");
            ratingTR4a.put(2, "Perpetrator had a close relationship with the child (e.g., teacher, "
                    + "coach, mentor, family friend). "
                    + "For example, an extended family member who is not living within "
                    + "the same household.");
            ratingTR4a.put(3, "Perpetrator was a family member with whom the child has a close "
                    + "relationship with (e.g., primary caretaker, parent, or sibling).\n"
                    + "For example, a family member who isstaying in the same household");
            ratingTR4a.put(4, "Item 4 (Sexual Abuse) was rated “0”.");

            tr4a.setQuestionDescription(desctr4a);
            tr4a.setQuestionToConsider(quesToConsiderTR4a);
            tr4a.setRatingsDefinition(ratingTR4a);

            quesTR.add(tr4a);

            //create TR4B
            SubQuestionEntity tr4b = new SubQuestionEntity("TR4b", "Physical Force");
            List<String> desctr4b = new ArrayList<>();
            desctr4b.add("Use of physical force during sexual abuse is associated significantly with the "
                    + "severity of subsequent trauma symptoms developed and experienced by victims.");
            desctr4b.add("This item should only be rated if Item 4 (Sexual Abuse) was rated “1”, “2” or “3”.\n"
                    + "Otherwise, rate “NA” if Item 4 (Sexual Abuse) was rated “0”");
            desctr4b.add("If the child has experienced more than 1 incident of sexual abuse, rate the incident where the most\n"
                    + "physical force was used");
            desctr4b.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR4b = new ArrayList<>();
            quesToConsiderTR4b.add("Did the perpetrator "
                    + "threaten to use physical "
                    + "force during the sexual "
                    + "abuse?");
            quesToConsiderTR4b.add("Did the perpetrator use "
                    + "physical force during the "
                    + "sexual abuse?\n"
                    + "➢ If yes, were there any "
                    + "injuries as a result of "
                    + "the sexual abuse?");

            Map<Integer, String> ratingTR4b = new HashMap<>();
            ratingTR4b.put(0, "No physical force or threat of force occurred during the abuse "
                    + "episode(s)");
            ratingTR4b.put(1, "Sexual abuse was associated with threat of violence but no "
                    + "physical force.");
            ratingTR4b.put(2, "Physical force was used during the sexual abuse");
            ratingTR4b.put(3, "Significant physical force/violence was used during the sexual "
                    + "abuse. Physical injuries occurred as a result of the force.");
            ratingTR4b.put(4, "Item 4 (Sexual Abuse) was rated “0”.");

            tr4b.setQuestionDescription(desctr4b);
            tr4b.setQuestionToConsider(quesToConsiderTR4b);
            tr4b.setRatingsDefinition(ratingTR4b);

            quesTR.add(tr4b);

            //create TR4c
            SubQuestionEntity tr4c = new SubQuestionEntity("TR4c", "Reaction To Disclosure");
            List<String> desctr4c = new ArrayList<>();
            desctr4c.add("This item refers to family members’ reactions to the disclosure of the "
                    + "sexual abuse");
            desctr4c.add("This item should only be rated if Item 4 (Sexual Abuse) was rated “1”, “2” or “3”.\n"
                    + "Otherwise, rate “NA” if Item 4 (Sexual Abuse) was rated “0”");
            desctr4c.add("Poorer prognosis of recovery from trauma is associated with family members’ lack of support during\n"
                    + "and/or after a child’s disclosure of abuse");
            desctr4c.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR4c = new ArrayList<>();
            quesToConsiderTR4c.add("Are family members "
                    + "aware of the sexual "
                    + "abuse?\n"
                    + "➢ If yes, how did "
                    + "family members "
                    + "react to the "
                    + "disclosure?\n"
                    + "➢ Were they "
                    + "supportive of the "
                    + "child?");

            Map<Integer, String> ratingTR4c = new HashMap<>();
            ratingTR4c.put(0, "All significant family members are aware of the abuse and "
                    + "supportive of the child coming forward with the description of "
                    + "his/her abuse experience");
            ratingTR4c.put(1, "Most significant family members are aware of the abuse and "
                    + "supportive of the child for coming forward. One or two family "
                    + "members may be less supportive. Parent may be experiencing "
                    + "anxiety/depression/guilt regarding abuse.");
            ratingTR4c.put(2, "Significant split among family members in terms of their support of "
                    + "the child for coming forward with the description of his/her "
                    + "experience");
            ratingTR4c.put(3, "Significant lack of support from close family members of the child "
                    + "for coming forward with the description of his/her abuse "
                    + "experience. Significant relationship (e.g., parent, care‐giving "
                    + "grandparent) is threatened");
            ratingTR4c.put(4, "Family members were unaware of the sexual abuse \n"
                    + "OR\n"
                    + "Item 4 (Sexual Abuse) was rated “0”");

            tr4c.setQuestionDescription(desctr4c);
            tr4c.setQuestionToConsider(quesToConsiderTR4c);
            tr4c.setRatingsDefinition(ratingTR4c);

            quesTR.add(tr4c);

            //create TR5
            SubQuestionEntity tr5 = new SubQuestionEntity("TR5", "Witness to Domestic Violence");
            List<String> desctr5 = new ArrayList<>();
            desctr5.add("This item refers to violence that occurs within the household or "
                    + "family");
            desctr5.add("This includes witnessing spousal violence or witnessing a sibling being physically abused");
            desctr5.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR5 = new ArrayList<>();
            quesToConsiderTR5.add("Has the child ever "
                    + "witnessed any fights "
                    + "at home (e.g., "
                    + "between mother and "
                    + "father etc.)?\n"
                    + "➢ If yes, did the "
                    + "fights ever result in "
                    + "any injuries? "
                    + "➢ How frequently did\n"
                    + "this occur?");

            Map<Integer, String> ratingTR5 = new HashMap<>();
            ratingTR5.put(0, "There is no evidence that child has witnessed domestic violence");
            ratingTR5.put(1, "Child has witnessed repeated threats of violence in household or "
                    + "witnessed physical violence on a single occasion but the violence did "
                    + "not result in injury (i.e. requiring medical attention). "
                    + "Suspicionsthat child might have witnessed domestic violence would "
                    + "also be rated here.");
            ratingTR5.put(2, "Child has witnessed repeated episodes of domestic violence but the "
                    + "violence did not result in significant injuries (i.e. requiring "
                    + "emergency medical attention). ");
            ratingTR5.put(3, "Child has witnessed repeated and/or severe episode(s) of domestic "
                    + "violence or had to intervene in episodes of domestic violence. "
                    + "Significant injuries have occurred and have been witnessed by the "
                    + "child as a direct result of the violence.");

            tr5.setQuestionDescription(desctr5);
            tr5.setQuestionToConsider(quesToConsiderTR5);
            tr5.setRatingsDefinition(ratingTR5);

            quesTR.add(tr5);

            //create TR6
            SubQuestionEntity tr6 = new SubQuestionEntity("TR6", "Witness/Victim to Criminal Activity");
            List<String> desctr6 = new ArrayList<>();
            desctr6.add("Thisitem rates whetherthe child was exposed to or a victim "
                    + "of significant criminal activity.");
            desctr6.add("Criminal behaviour includes any behaviour which could result in imprisonment including (but not limited "
                    + "to) the following examples: drug dealing, prostitution, assault/battery, violation of a Personal Protection "
                    + "Order.");
            desctr6.add("A child who witnessed his/her sibling sexually abused or saw a parent using drugs would be rated here.");
            desctr6.add("If a child is the victim of physical punishment or witnessed a family member being physically assaulted,\n"
                    + "rate this item as a “0” unless the physical punishment meted out by the perpetrator was severe enough\n"
                    + "to result in the prosecution or incarceration of the perpetrator");
            desctr6.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR6 = new ArrayList<>();
            quesToConsiderTR6.add("Has the child ever "
                    + "been exposed to any "
                    + "form of criminal "
                    + "activity?\n"
                    + "➢ If yes, was the child "
                    + "ever a direct victim "
                    + "of criminal "
                    + "activity?\n"
                    + "➢ How often was the "
                    + "child exposed to "
                    + "criminal activity?");

            Map<Integer, String> ratingTR6 = new HashMap<>();
            ratingTR6.put(0, "There is no evidence that child has witnessed/was a victim of "
                    + "criminal activity");
            ratingTR6.put(1, "There is strong suspicion or evidence that the child has witnessed "
                    + "one instance of criminal activity.");
            ratingTR6.put(2, "Child has been exposed to multiple instances of criminal activity, is "
                    + "a direct victim of criminal activity or witnessed the victimisation of "
                    + "a family or friend. ");
            ratingTR6.put(3, "Child has been exposed to chronic and/or severe instances of "
                    + "criminal activity, is a direct victim of criminal activity that was lifethreatening or caused significant physical harm or child witnessed "
                    + "the death of a loved one due to criminal activity");

            tr6.setQuestionDescription(desctr6);
            tr6.setQuestionToConsider(quesToConsiderTR6);
            tr6.setRatingsDefinition(ratingTR6);

            quesTR.add(tr6);

            //create TR7
            SubQuestionEntity tr7 = new SubQuestionEntity("TR7", "Disruptions in caregiving/attachment losses");
            List<String> desctr7 = new ArrayList<>();
            desctr7.add("This item rates whether the child has been "
                    + "exposed to disruptions in caregiving involving separation from primary attachment figure(s) and/or "
                    + "attachment losses.");
            desctr7.add("If the child was placed with a foster family since birth and the child is still residing with the same foster "
                    + "family, this item would be rated “0”");
            desctr7.add("Children who have had placement changes in out‐of‐home care, for example foster care, children’s "
                    + "homes or institutional setting (e.g., Singapore Boys’/Girls’ Home), should be rated “2” or “3”.");
            desctr7.add("Short term hospital stays or brief detention stays (e.g., remand), during which the child’s caregiver "
                    + "remains the same would not be rated on this item.");
            desctr7.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR7 = new ArrayList<>();
            quesToConsiderTR7.add("Where was the child "
                    + "residing before the "
                    + "current placement?");
            quesToConsiderTR7.add("Were there any other "
                    + "placements before "
                    + "this one?");
            quesToConsiderTR7.add("Were there any "
                    + "changes in the child’s "
                    + "main caregiver and "
                    + "attachment figure "
                    + "during his/her life?");

            Map<Integer, String> ratingTR7 = new HashMap<>();
            ratingTR7.put(0, "There is no evidence that the child has experienced disruptions in "
                    + "caregiving and/or attachment losses.");
            ratingTR7.put(1, "Child may have experienced one disruption in caregiving but was "
                    + "placed with a familiar alternative caregiver, such as a relative (i.e. "
                    + "child shifted from care of biological mother to paternal "
                    + "grandmother). Child may or may not have had ongoing contact with "
                    + "primary attachment figure(s) during this disruption.   Shift in "
                    + "caregiving may be temporary or permanent");
            ratingTR7.put(2, "Child has been exposed to 2 disruptions in caregiving with familiar "
                    + "alternative caregiver/s, "
                    + "OR\n"
                    + "Child has had 1 disruption involving 1 placement with an unknown "
                    + "caregiver (e.g., foster or residential care facilities).   ");
            ratingTR7.put(3, "Child has been exposed to multiple/repeated placement changes "
                    + "(i.e. 3 or more placements with familiar caregiver/s or 2 or more "
                    + "placements with unknown caregiver/s) resulting in caregiving "
                    + "disruptions that has disrupted various domains of a child’s life (i.e. "
                    + "loss of community, school placement, peer group). "
                    + "Examples would include a child in several short‐term unknown "
                    + "placements (i.e. multiple foster care placements) and/or multiple "
                    + "transitions in and out of the family‐of‐origin (i.e. several cycles of "
                    + "removal and reunification).  ");

            tr7.setQuestionDescription(desctr7);
            tr7.setQuestionToConsider(quesToConsiderTR7);
            tr7.setRatingsDefinition(ratingTR7);

            quesTR.add(tr7);

            //create TR8
            SubQuestionEntity tr8 = new SubQuestionEntity("TR8", "Any Other Significant Trauma");
            List<String> desctr8 = new ArrayList<>();
            desctr8.add("This item rates other traumatic events not captured by previous "
                    + "items.");
            desctr8.add("Examples of other significant traumatic events: "
                    + "• intrusive medical procedures that are traumatic to the child \n"
                    + "• natural/manmade disasters,\n"
                    + "• motor vehicle accidents,  \n"
                    + "• serious school violence,  \n"
                    + "• acts of terrorism.");
            desctr8.add("Normal reactions to separation/grief reactions should not be rated here");
            desctr8.add("Please rate within the child’s lifetime");

            List<String> quesToConsiderTR8 = new ArrayList<>();
            quesToConsiderTR8.add("Has the child "
                    + "experienced any other "
                    + "traumatic event that "
                    + "may have impacted "
                    + "his/her functioning?");

            Map<Integer, String> ratingTR8 = new HashMap<>();
            ratingTR8.put(0, "There is no evidence that child has experienced any other forms of "
                    + "trauma.");
            ratingTR8.put(1, "Child has been indirectly affected by other forms of trauma, or has "
                    + "experienced a mild degree of other trauma");
            ratingTR8.put(2, "Child has experienced other forms of trauma which hasimpacted on "
                    + "his/her well­being ");
            ratingTR8.put(3, "Child has experienced life threatening and/or severe other forms of "
                    + "trauma that had a significant/lasting impact on his/her well­being");

            tr8.setQuestionDescription(desctr8);
            tr8.setQuestionToConsider(quesToConsiderTR8);
            tr8.setRatingsDefinition(ratingTR8);

            quesTR.add(tr8);

            List<Long> ids1 = new ArrayList<>();
            ids1.add(questionsSessionBean.retrieveQuestionByCode("5.2").getQuestionId());
            ids1.add(questionsSessionBean.retrieveQuestionByCode("6.1").getQuestionId());

            SubModuleEntity mod1 = subModuleSessionBean.createNewSubModuleWithManyMainQues(trmod, ids1);
            for (SubQuestionEntity questions : quesTR) {
                questionsSessionBean.createSubQuestionForSubModule(mod1.getSubModuleId(), questions);
            }
            // ============ Create Child Skills SubModule ===================
            List<String> csdesc = new ArrayList<>();
            csdesc.add("Rate this module ONLY IF Item 1.1 “Child Skills” was rated “1”, “2” or “3”");
            csdesc.add("*Ensure that the rating for the Item 1.1 corresponds to the HIGHEST rating in this module");

            SubModuleEntity csmod = new SubModuleEntity("Child Skills (CS) Module", csdesc, false);
            List<SubQuestionEntity> quesCS = new ArrayList<>();

            //create cs1
            SubQuestionEntity cs1 = new SubQuestionEntity("CS1", "Play Engagement");
            List<String> desccs1 = new ArrayList<>();
            desccs1.add("This item ratesthe degree to which the infant/child is engaged in age‐appropriate "
                    + "play");
            desccs1.add("Take into account whether the child needs adult support to engage in play.  ");
            desccs1.add("Rate problems with either solitary or group (i.e. parallel) play here.  ");

            List<String> quesToConsiderCS1 = new ArrayList<>();
            quesToConsiderCS1.add("Does the child "
                    + "appear "
                    + "engaged when "
                    + "playing?");
            quesToConsiderCS1.add("Is the child "
                    + "easily engaged "
                    + "in play"
                    + "activities/able "
                    + "to sustain "
                    + "play?");

            Map<Integer, String> ratingCS1 = new HashMap<>();
            ratingCS1.put(0, "No evidence that infant or child has problems engaging in play");
            ratingCS1.put(1, "Child is engaging adequately in play although some problems may exist. "
                    + "Infants may not be easily engaged in play.  Toddlers and pre‐schoolers may "
                    + "seem uninterested and poor in their ability to sustain play.  ");
            ratingCS1.put(2, "Child is having moderate problems engaging in play.  Infants may resist "
                    + "play.  Toddlers and pre‐schoolers may show little enjoyment or interest in "
                    + "activities within or outside the home, and may only be engaged in play "
                    + "activities with ongoing adult interaction and support. ");
            ratingCS1.put(3, "Child has no interest in engaging in play.  Infants may spend most of the "
                    + "time alone and unengaged.  Even with adult encouragement, toddlers and "
                    + "pre‐schoolers may fail to demonstrate enjoyment or use play to further "
                    + "development.   ");

            cs1.setQuestionDescription(desccs1);
            cs1.setQuestionToConsider(quesToConsiderCS1);
            cs1.setRatingsDefinition(ratingCS1);

            quesCS.add(cs1);

            //create cs2
            SubQuestionEntity cs2 = new SubQuestionEntity("CS2", "Communication");
            List<String> desccs2 = new ArrayList<>();
            desccs2.add("This item rates the child’s language skills*, both what the child understands and "
                    + "what he or she can say. Communication may be verbal or non‐verbal in nature, and may be in English "
                    + "or other languages. ");
            desccs2.add("For children under the age of 2 years, please use the corrected age for prematurity.");

            List<String> quesToConsiderCS2 = new ArrayList<>();
            quesToConsiderCS2.add("Is the child "
                    + "exhibiting "
                    + "behaviours that "
                    + "are typical of "
                    + "children in the "
                    + "same age range?");

            Map<Integer, String> ratingCS2 = new HashMap<>();
            ratingCS2.put(0, "No concern or delay in development; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental domains in "
                    + "ASQ‐3 with no concerns about the child’s development in this domain");
            ratingCS2.put(1, "Caregivers/professionals have some concerns about child’s "
                    + "development, but child is meeting developmental milestones; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental domains in "
                    + "ASQ‐3, but caregivers/professionals are concerned about child’s "
                    + "development in this domain.");
            ratingCS2.put(2, "Child is not meeting the average developmental milestones; "
                    + "OR "
                    + "Child scored ‘grey’ on the ASQ‐3, and needs further "
                    + "support/intervention. ");
            ratingCS2.put(3, "Child has lost a skill he/she once had; "
                    + "OR "
                    + "Child scored ‘black’ on the ASQ‐3, and needs further "
                    + "support/intervention. ");

            cs2.setQuestionDescription(desccs2);
            cs2.setQuestionToConsider(quesToConsiderCS2);
            cs2.setRatingsDefinition(ratingCS2);

            quesCS.add(cs2);

            //create cs3
            SubQuestionEntity cs3 = new SubQuestionEntity("CS3", "Motor");
            List<String> desccs3 = new ArrayList<>();
            desccs3.add("This item ratesthe child’s gross motorfunctioning such as moving one’s arms,sitting, crawling "
                    + "and walking as well as fine motor functioning such as grasping a toy");
            desccs3.add("50th percentile*: refers to the 50th percentile norm by age (i.e. it is the milestone that 50% of children "
                    + "of that age would have achieved");
            desccs3.add("Red flag*: Refers to the 90th percentile cut off (i.e. it is the milestone that 90% of children of that age "
                    + "would have achieved). The presence of a red flag is an indication of delay, and there is a need for further "
                    + "evaluation. ");
            desccs3.add("At any time in a child's development, if he/she loses a skill he/she once had, this is a 'red flag' and this "
                    + "child would need immediate referral for further evaluation.");
            desccs3.add("For children under the age of 2 years, please use the corrected age for prematurity. ");

            List<String> quesToConsiderCS3 = new ArrayList<>();
            quesToConsiderCS3.add("Is the child exhibiting "
                    + "behaviours that are "
                    + "typical of children in the "
                    + "same age range?");
            quesToConsiderCS3.add("Does the child exhibit "
                    + "any red flags that may "
                    + "indicate delays in "
                    + "development?");

            Map<Integer, String> ratingCS3 = new HashMap<>();
            ratingCS3.put(0, "No concern or delay in development; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental "
                    + "domainsin ASQ‐3 with no concerns aboutthe child’s development "
                    + "in this domain.");
            ratingCS3.put(1, "Caregivers/professionals have some concerns about child’s "
                    + "development, but child is meeting developmental milestones; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental "
                    + "domains in ASQ‐3, but caregivers/professionals are concerned "
                    + "about child’s development in this domain.");
            ratingCS3.put(2, "Child is not meeting the average developmental milestones; "
                    + "OR "
                    + "Child scored at least one ‘grey’ on the ASQ‐3, and needs further "
                    + "support/intervention");
            ratingCS3.put(3, "Child has red flags or has lost a skill he/she once had; "
                    + "OR "
                    + "Child scored at least one ‘black’ on the ASQ‐3, and needs further "
                    + "support/intervention");

            cs3.setQuestionDescription(desccs3);
            cs3.setQuestionToConsider(quesToConsiderCS3);
            cs3.setRatingsDefinition(ratingCS3);

            quesCS.add(cs3);

            //create cs4
            SubQuestionEntity cs4 = new SubQuestionEntity("CS4", "Problem Solving");
            List<String> desccs4 = new ArrayList<>();
            desccs4.add("This item rates the child’s ability to play with toys and solve problems*. This is "
                    + "related to the child’s cognitive development.");
            desccs4.add("For children under the age of 2 years, please use the corrected age for prematurity.");

            List<String> quesToConsiderCS4 = new ArrayList<>();
            quesToConsiderCS4.add("Is the child "
                    + "exhibiting "
                    + "behaviours that "
                    + "are typical of "
                    + "children in the "
                    + "same age range?");

            Map<Integer, String> ratingCS4 = new HashMap<>();
            ratingCS4.put(0, "No concern or delay in development; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental domains in "
                    + "ASQ‐3 with no concerns about the child’s development in this domain.");
            ratingCS4.put(1, "Caregivers/professionals have some concerns about child’s "
                    + "development, but child is meeting developmental milestones; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental domains in "
                    + "ASQ‐3, but caregivers/professionals are concerned about child’s "
                    + "development in this domain.");
            ratingCS4.put(2, "Child is not meeting the average developmental milestones; "
                    + "OR "
                    + "Child scored ‘grey’ on the ASQ‐3, and needs further "
                    + "support/intervention.  ");
            ratingCS4.put(3, "Child has lost a skill he/she once had; "
                    + "OR "
                    + "Child scored ‘black’ on the ASQ‐3, and needs further "
                    + "support/intervention");

            cs4.setQuestionDescription(desccs4);
            cs4.setQuestionToConsider(quesToConsiderCS4);
            cs4.setRatingsDefinition(ratingCS4);

            quesCS.add(cs4);

            //create cs5
            SubQuestionEntity cs5 = new SubQuestionEntity("CS5", "Personal-Social");
            List<String> desccs5 = new ArrayList<>();
            desccs5.add("This item rates the child’s self‐help skills and interactions with others*. ");
            desccs5.add("For children under the age of 2 years, please use the corrected age for prematurity.");

            List<String> quesToConsiderCS5 = new ArrayList<>();
            quesToConsiderCS5.add("Is the child "
                    + "exhibiting "
                    + "behaviours that "
                    + "are typical of "
                    + "children in the "
                    + "same age range? ");

            Map<Integer, String> ratingCS5 = new HashMap<>();
            ratingCS5.put(0, "No concern or delay in development; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental domains in "
                    + "ASQ‐3 with no concerns about the child’s development in this domain");
            ratingCS5.put(1, "Caregivers/professionals have some concerns about child’s "
                    + "development, but child is meeting developmental milestones; "
                    + "OR "
                    + "Child scored ‘white’ on the corresponding developmental domains in "
                    + "ASQ‐3, but caregivers/professionals are concerned about child’s "
                    + "development in this domain.");
            ratingCS5.put(2, "Child is not meeting the average developmental milestones;  "
                    + "OR "
                    + "Child scored ‘grey’ on the ASQ‐3, and needs further "
                    + "support/intervention.");
            ratingCS5.put(3, "Child has lost a skill he/she once had; "
                    + "OR "
                    + "Child scored ‘black’ on the ASQ‐3, and needs further "
                    + "support/intervention.");

            cs5.setQuestionDescription(desccs5);
            cs5.setQuestionToConsider(quesToConsiderCS5);
            cs5.setRatingsDefinition(ratingCS5);

            quesCS.add(cs5);

            List<Long> ids2 = new ArrayList<>();
            ids2.add(questionsSessionBean.retrieveQuestionByCode("1.1").getQuestionId());

            SubModuleEntity mod2 = subModuleSessionBean.createNewSubModuleWithManyMainQues(csmod, ids2);
            for (SubQuestionEntity questions : quesCS) {
                questionsSessionBean.createSubQuestionForSubModule(mod2.getSubModuleId(), questions);
            }
            // =============== Create PCN SubModule =====================
            List<String> PCNdesc = new ArrayList<>();
            PCNdesc.add("Rate this module ONLY IF Item 1.7 “Pre­school/Childcare” was rated “1”, “2” or “3”");
            PCNdesc.add("*Ensure that the rating for the Item 1.7 corresponds to the HIGHEST rating in this module");

            SubModuleEntity PCNmod = new SubModuleEntity("Pre-School/ Childcare Needs (PCN) Module", PCNdesc, false);
            List<SubQuestionEntity> quesPCN = new ArrayList<>();

            //create PCN1
            SubQuestionEntity pcn1 = new SubQuestionEntity("PCN1", "Suitability of pre-school/childcare");
            List<String> descpcn1 = new ArrayList<>();
            descpcn1.add("This item describes whether the pre‐school/childcare "
                    + "meets the needs of the child during the time the child spends in pre‐school/childcare. For example, "
                    + "this item could rate the consistency of the teachers/educarers in the pre‐school/childcare, and the "
                    + "quality of the relationship between the teacher/educarer and child.");
            descpcn1.add("These needs could include the child’s developmental, learning, safety and emotional needs.");
            descpcn1.add("Some examples of the pre‐school/childcare meeting the needs of the child:\n"
                    + "• A child who is shy/slow to warm up is given time to transition between group activities\n"
                    + "• For a child who is sensitive to the changes in their teacher/educarer, the pre‐school/childcare "
                    + "ensures that the child is being received by a familiar teacher/educarer at the pre‐ "
                    + "school/childcare\n"
                    + "• A child who is more active/a kinaesthetic learner is given more freedom to move around rather "
                    + "than being forced to stay seated all the time");
            descpcn1.add("A rating of ‘2’ or ‘3’ would indicate a mismatch in fit between the pre‐school/childcare and child’s "
                    + "needs.");
            descpcn1.add("If the child’s other needs are met by a different service in addition to the pre‐school/childcare services "
                    + "(e.g. EIPIC), he/she may still be rated a ‘0’ or ‘1’ on this item if the pre‐school/childcare is able to meet "
                    + "most/some of his/her needs");

            List<String> quesToConsiderPCN1 = new ArrayList<>();
            quesToConsiderPCN1.add("Does the pre‐ "
                    + "school/childcare "
                    + "meet the needs of "
                    + "the child?");
            quesToConsiderPCN1.add("s there a good fit "
                    + "between the pre‐ "
                    + "school and childcare "
                    + "and the child?");

            Map<Integer, String> ratingPCN1 = new HashMap<>();
            ratingPCN1.put(0, "Child’s pre‐school/childcare meets most of the child’s needs.");
            ratingPCN1.put(1, "Child’s pre‐school/childcare meets some of the child’s needs");
            ratingPCN1.put(2, "Child’s pre‐school/childcare does not meet the needs of the child in "
                    + "most areas. Caregiving may notsupport the child’s growth or promote "
                    + "further learning.  ");
            ratingPCN1.put(3, "Child’s pre‐school/childcare does not meet the child’s needs and is "
                    + "affecting the child’s development in one or more areas.");
            ratingPCN1.put(4, "Child is not in pre‐school/childcare.");

            pcn1.setQuestionDescription(descpcn1);
            pcn1.setQuestionToConsider(quesToConsiderPCN1);
            pcn1.setRatingsDefinition(ratingPCN1);

            quesPCN.add(pcn1);

            //create PCN2
            SubQuestionEntity pcn2 = new SubQuestionEntity("PCN2", "Child's Functioning in PreSchool/Childcare");
            List<String> descpcn2 = new ArrayList<>();
            descpcn2.add("This item rates how the child is functioning in "
                    + "pre‐school/childcare. This could include the child’s ability to participate actively, follow routines, "
                    + "and engage in positive interactions with adults or peers.");

            List<String> quesToConsiderPCN2 = new ArrayList<>();
            quesToConsiderPCN2.add("How does the pre‐ "
                    + "school/childcare "
                    + "teacher describe "
                    + "behaviour of the "
                    + "child?");
            quesToConsiderPCN2.add("Is the child having "
                    + "any problems in "
                    + "the pre‐ "
                    + "school/childcare?");

            Map<Integer, String> ratingPCN2 = new HashMap<>();
            ratingPCN2.put(0, "No evidence of problems with functioning in current pre‐ "
                    + "school/childcare. ");
            ratingPCN2.put(1, "Mild problems with functioning in current pre‐school/childcare. "
                    + "Child can participate in most activities in the pre‐school/childcare");
            ratingPCN2.put(2, "Moderate problems with functioning in current pre‐ "
                    + "school/childcare. Child can participate in only some activities in the "
                    + "pre‐school/childcare.");
            ratingPCN2.put(3, "Severe problems with functioning in current pre‐school/childcare. "
                    + "Child struggles to participate in most activities in the pre‐ "
                    + "school/childcare");
            ratingPCN2.put(4, "Child is not in pre‐school/childcare.");

            pcn2.setQuestionDescription(descpcn2);
            pcn2.setQuestionToConsider(quesToConsiderPCN2);
            pcn2.setRatingsDefinition(ratingPCN2);

            quesPCN.add(pcn2);

            //create PCN3
            SubQuestionEntity pcn3 = new SubQuestionEntity("PCN3", "Preschool/ Childcare Attendance");
            List<String> descpcn3 = new ArrayList<>();
            descpcn3.add("This item rates attendance of the child in pre‐ "
                    + "school/childcare. ");
            descpcn3.add("Caseworker may override the 30‐days window to the last known attendance if there are valid reasons "
                    + "for the child’s current absence from school (e.g. school closure due to outbreaks, child falling ill) ");

            List<String> quesToConsiderPCN3 = new ArrayList<>();
            quesToConsiderPCN3.add("Does the child "
                    + "attend pre‐ "
                    + "school/childcare "
                    + "regularly?");
            quesToConsiderPCN3.add("Has the child had "
                    + "any past difficulties "
                    + "related to attending "
                    + "pre‐school/ "
                    + "childcare?");

            Map<Integer, String> ratingPCN3 = new HashMap<>();
            ratingPCN3.put(0, "Child attends pre­school/childcare regularly");
            ratingPCN3.put(1, "Child’s school attendance is between 60% to 70% on average in the "
                    + "last 3 months");
            ratingPCN3.put(2, "Child’s school attendance is between 50% to 59% on average in the "
                    + "last 3 months.");
            ratingPCN3.put(3, "Child’s school attendance is below 50% on average in the last 3 "
                    + "months. "
                    + "OR "
                    + "Child is 4­6 years old and not in pre‐school/childcare");
            ratingPCN3.put(4, "Child is below 4 years old and not in pre‐school/childcare");

            pcn3.setQuestionDescription(descpcn3);
            pcn3.setQuestionToConsider(quesToConsiderPCN3);
            pcn3.setRatingsDefinition(ratingPCN3);

            quesPCN.add(pcn3);

            List<Long> ids3 = new ArrayList<>();
            ids3.add(questionsSessionBean.retrieveQuestionByCode("1.7").getQuestionId());

            SubModuleEntity mod3 = subModuleSessionBean.createNewSubModuleWithManyMainQues(PCNmod, ids3);
            for (SubQuestionEntity questions : quesPCN) {
                questionsSessionBean.createSubQuestionForSubModule(mod3.getSubModuleId(), questions);
            }

            // =============== Create Developmental needs SubModule ====================
            List<String> DNdesc = new ArrayList<>();
            DNdesc.add("Rate this module ONLY IF Item 2.5. “Developmental” was rated “1”, “2” or “3”");
            DNdesc.add("*Ensure that the rating for the Item 2.5 corresponds to the HIGHEST rating in this module");

            SubModuleEntity DNmod = new SubModuleEntity("Developmental Needs (DN) Module", DNdesc, false);
            List<SubQuestionEntity> quesDN = new ArrayList<>();

            //create dn1
            SubQuestionEntity dn1 = new SubQuestionEntity("DN1", "Cognitive");
            List<String> descdn1 = new ArrayList<>();
            descdn1.add("This item describes the cognitive functioning of the child");
            descdn1.add("If a child has had a formal IQ assessment within the last 2 years, refer to the report to rate the IQ level "
                    + "accordingly.");
            descdn1.add("If no formal intellectual assessment has been done, and there is reason to believe that the child has "
                    + "problems with intellectual functioning, rate the child as “2” and follow up with a referral for formal "
                    + "intellectual assessment");
            descdn1.add("If IQ assessment results are not yet available, children/youths enrolled in APSN schools will generally be "
                    + "rated as a “2” while children/youths enrolled in MINDS schools will generally be rated as a “3”");

            List<String> quesToConsiderDN1 = new ArrayList<>();
            quesToConsiderDN1.add("Has the child had an IQ assessment? "
                    + "➢ If yes, what is the child’s IQ?\n"
                    + "➢ When was the assessment done?\n"
                    + "➢ If no, what is the child’s estimated "
                    + "level of functioning (borderline, "
                    + "mildly impaired or severely "
                    + "impaired)?");
            quesToConsiderDN1.add("Is the child enrolled in a special school?");

            Map<Integer, String> ratingDN1 = new HashMap<>();
            ratingDN1.put(0, "Child's intellectual functioning appears to be in "
                    + "normal range. There is no reason to believe that "
                    + "the child has any problems with intellectual "
                    + "functioning");
            ratingDN1.put(1, "Child has low IQ (70 to 85) or has identified "
                    + "learning challenges");
            ratingDN1.put(2, "Child has mild mental retardation. IQ is between "
                    + "55 and 69.");
            ratingDN1.put(3, "Child has moderate to profound mental "
                    + "retardation. IQ is less than 55");

            dn1.setQuestionDescription(descdn1);
            dn1.setQuestionToConsider(quesToConsiderDN1);
            dn1.setRatingsDefinition(ratingDN1);

            quesDN.add(dn1);

            //create dn2
            SubQuestionEntity dn2 = new SubQuestionEntity("DN2", "Communication");
            List<String> descdn2 = new ArrayList<>();
            descdn2.add("This item rates the child’s language skills, both what the child understands "
                    + "(receptive) and what he or she can say (expressive). Communication may be verbal or non‐verbal in "
                    + "nature, and may be in English or other languages");
            descdn2.add("Children in child welfare settings have a higher risk of communication needs. If a child has been "
                    + "neglected from a young age and has not heard much adult language, his/her receptive and expressive "
                    + "language may not develop optimally.");

            List<String> quesToConsiderDN2 = new ArrayList<>();
            quesToConsiderDN2.add("Is the child exhibiting behaviours "
                    + "that are typical of children in the "
                    + "same age range?");
            quesToConsiderDN2.add("Has the child ever been diagnosed "
                    + "with having a problem with "
                    + "understanding words or using words "
                    + "to express him/herself?");

            Map<Integer, String> ratingDN2 = new HashMap<>();
            ratingDN2.put(0, "No concern or problems in communication.");
            ratingDN2.put(1, "There are some concerns about the child’s "
                    + "communication skills. "
                    + "For example, the child may struggle with "
                    + "understanding minimal phrases or is not fluent in "
                    + "expressing language.");
            ratingDN2.put(2, "Child has limited receptive and expressive "
                    + "communication skills. Child is not communicating at "
                    + "a level that is typical of his/her age.");
            ratingDN2.put(3, "Child is unable to communicate");

            dn2.setQuestionDescription(descdn2);
            dn2.setQuestionToConsider(quesToConsiderDN2);
            dn2.setRatingsDefinition(ratingDN2);

            quesDN.add(dn2);

            //create dn3
            SubQuestionEntity dn3 = new SubQuestionEntity("DN3", "Social-Emotional Development");
            List<String> descdn3 = new ArrayList<>();
            descdn3.add("This item is rated relative to a child’s peers of a comparable age "
                    + "(chronological).");
            descdn3.add("Although this item seems to be referring to children within the Autism spectrum, it is not exclusively for "
                    + "such children.  Children with attachment issues or children with any significant delay in social‐emotional "
                    + "development can be and ought to be rated");
            descdn3.add("If a child is considered “eccentric” but is otherwise attending school and participating in social activities "
                    + "without any significant issues or disruptions, a rating of “1” can be made for the purpose of further "
                    + "monitoring and preventive actions");
            descdn3.add("Further information may be obtained from measures such as Vineland Adaptive Behavior Scale, Child "
                    + "Behaviour Checklist (either Parent or Teacher reporting form), and the Strengths and Difficulties "
                    + "Questionnaire.");

            List<String> quesToConsiderDN3 = new ArrayList<>();
            quesToConsiderDN3.add("Are there any "
                    + "concerns about the "
                    + "child’s social or "
                    + "emotional "
                    + "development?");
            quesToConsiderDN3.add("Is the child able to "
                    + "empathise with "
                    + "others/able to take "
                    + "turns/interact "
                    + "appropriately with "
                    + "others?");
            quesToConsiderDN3.add("Does the child have "
                    + "any repetitive "
                    + "behaviours (e.g., "
                    + "hand‐flapping, "
                    + "rocking)?");

            Map<Integer, String> ratingDN3 = new HashMap<>();
            ratingDN3.put(0, "Child's social interactions and emotional responses appear within "
                    + "normal range.");
            ratingDN3.put(1, "Some concerns that child’s social interactions and/or emotional "
                    + "responses are not developing normally");
            ratingDN3.put(2, "Clear evidence of impaired social interactions (failure to develop "
                    + "peer reaction to others); "
                    + "AND/OR "
                    + "A lack of emotional reciprocity (failure to express empathy, "
                    + "pleasure, curiosity); "
                    + "AND/OR "
                    + "Repetitive, stereotyped patterns of behaviours or interests (hand "
                    + "flapping, preoccupation with parts of toys rather than playing with "
                    + "toys)");
            ratingDN3.put(3, "Clear evidence of severely impaired social interactions and lack of "
                    + "emotional reciprocity; "
                    + "AND/OR "
                    + "Repetitive, stereotyped patterns of behaviours or interests to the "
                    + "degree that the child is unable to participate in a wide range of age‐ "
                    + "appropriate activities and settings.");

            dn3.setQuestionDescription(descdn3);
            dn3.setQuestionToConsider(quesToConsiderDN3);
            dn3.setRatingsDefinition(ratingDN3);

            quesDN.add(dn3);

            //create dn4
            SubQuestionEntity dn4 = new SubQuestionEntity("DN4", "Daily Functioning Skills");
            List<String> descdn4 = new ArrayList<>();
            descdn4.add("This item identifies developmentally appropriate basic daily living skills "
                    + "and hygiene tasks (e.g., toileting, feeding, grooming) that the child is expected to display for his/her "
                    + "chronological age");
            descdn4.add("Further information may be obtained from scales such as Vineland Adaptive Behavior Scale, Child\n"
                    + "Behaviour Checklist (either Parent or Teacher reporting form), and BASC‐II");
            descdn4.add("Children are expected to be able to perform the following self‐care tasks by the following ages:");
            descdn4.add("By 7 Years:");
            descdn4.add("Able to eat by him/herself "
                    + "without assistance");
            descdn4.add("Able to remove clothes "
                    + "without assistance");
            descdn4.add("Able to bathe him/herself "
                    + "without assistance");
            descdn4.add("Able to use toilet without "
                    + "assistance  ");
            descdn4.add("By 11 years");
            descdn4.add("Able to use money to pay "
                    + "for items");
            descdn4.add("Able to take public "
                    + "transportation");
            descdn4.add("Able to help out with basic "
                    + "chores");
            descdn4.add("Able to prepare basic food "
                    + "such as sandwiches");
            descdn4.add("12 years old and above");
            descdn4.add("Able to help out with "
                    + "advanced household chores "
                    + "(e.g., mopping the house)");
            descdn4.add("Able to cook a simple meal");
            descdn4.add("Able to save his/her money\n"
                    + "for the future ");

            List<String> quesToConsiderDN4 = new ArrayList<>();
            quesToConsiderDN4.add("Does the child require "
                    + "any help with self‐care "
                    + "tasks such as bathing, "
                    + "eating, dressing and "
                    + "toileting?\n"
                    + "➢ If yes, is the "
                    + "amount of help "
                    + "needed more than "
                    + "expected for the "
                    + "child’s age?");
            quesToConsiderDN4.add("Have concerns been "
                    + "expressed about the "
                    + "child's hygiene?");

            Map<Integer, String> ratingDN4 = new HashMap<>();
            ratingDN4.put(0, "Child demonstrates age­appropriate daily­living skills. Reliance on "
                    + "others appropriate for age group");
            ratingDN4.put(1, "Child shows mild or occasional problems in daily living skills for "
                    + "his/her age, but is generally self‐reliant");
            ratingDN4.put(2, "Child demonstrates moderate or routine problems in daily living "
                    + "skills, and relies on others for help more than his/her age group.");
            ratingDN4.put(3, "Child showssevere or almost constant problems in daily living skills, "
                    + "and relies on others for help much more than is expected for his/her "
                    + "age group. "
                    + "OR "
                    + "Child requires assistance on more than one self­care tasks (e.g., "
                    + "eating, bathing, dressing, toileting).");

            dn4.setQuestionDescription(descdn4);
            dn4.setQuestionToConsider(quesToConsiderDN4);
            dn4.setRatingsDefinition(ratingDN4);

            quesDN.add(dn4);

            List<Long> ids4 = new ArrayList<>();
            ids4.add(questionsSessionBean.retrieveQuestionByCode("2.5").getQuestionId());

            SubModuleEntity mod4 = subModuleSessionBean.createNewSubModuleWithManyMainQues(DNmod, ids4);
            for (SubQuestionEntity questions : quesDN) {
                questionsSessionBean.createSubQuestionForSubModule(mod4.getSubModuleId(), questions);
            }

            // Create Pre-disposing risk factors SubModule
            List<String> PRFdesc = new ArrayList<>();
            PRFdesc.add("Rate this module ONLY IF Item 3.4. “Pre­Disposing Risk Factors” was rated “1”, “2” or “3”");
            PRFdesc.add("Please note that these items provide more information on the pre‐disposing risk factors of the child, hence "
                    + "the items generally DO NOT follow the 30‐day rating window");
            PRFdesc.add("*Ensure that the rating for the Item 3.4 corresponds to the HIGHEST rating in this module");

            SubModuleEntity PRFmod = new SubModuleEntity("Pre-Disposing Risk Factors (PRF) Module", PRFdesc, true);
            List<SubQuestionEntity> quesPRF = new ArrayList<>();

            //create prf1
            SubQuestionEntity prf1 = new SubQuestionEntity("PRF1", "Birth Weight");
            List<String> descPRF1 = new ArrayList<>();
            descPRF1.add("This item describes the child’s weight as compared to normal development.");
            descPRF1.add("Average birth weight for infants is 2.5kg");
            descPRF1.add("Very low birth weight would be 1 to 1.5kg");
            descPRF1.add("Extremely low birth weight would be less than 1kg");

            List<String> quesToConsiderPRF1 = new ArrayList<>();
            quesToConsiderPRF1.add("What was the child’s "
                    + "birth weight?");

            Map<Integer, String> ratingPRF1 = new HashMap<>();
            ratingPRF1.put(0, "Child was born with birth weight of 2.5 kilograms or more.");
            ratingPRF1.put(1, "Child was born with borderline low birth weight in the range of 2 to "
                    + "less than 2.5 kilograms");
            ratingPRF1.put(2, "Child was born with low birth weight in the range of 1.5 to less than "
                    + "2 kilograms");
            ratingPRF1.put(3, "Child was born with very/extremely low birth weight of less than 1.5 "
                    + "kilograms");

            prf1.setQuestionDescription(descPRF1);
            prf1.setQuestionToConsider(quesToConsiderPRF1);
            prf1.setRatingsDefinition(ratingPRF1);

            quesPRF.add(prf1);

            //create prf2
            SubQuestionEntity prf2 = new SubQuestionEntity("PRF2", "Antenatal Care");
            List<String> descPRF2 = new ArrayList<>();
            descPRF2.add("This item refers to the health care and birth circumstances experienced by the "
                    + "child in the womb.");

            List<String> quesToConsiderPRF2 = new ArrayList<>();
            quesToConsiderPRF2.add("Did the child’s "
                    + "biological mother "
                    + "receive adequate "
                    + "medical attention "
                    + "during pregnancy?");
            quesToConsiderPRF2.add("Did the child’s "
                    + "biological mother "
                    + "suffer from any form "
                    + "of illness during "
                    + "pregnancy?");

            Map<Integer, String> ratingPRF2 = new HashMap<>();
            ratingPRF2.put(0, "Child’s biological mother had adequate planned antenatal care that "
                    + "began in the first trimester. Child’s mother did not experience any "
                    + "pregnancy­related illnesses.");
            ratingPRF2.put(1, "Child’s biological mother had a mild or well­controlled form of "
                    + "pregnancy­related illness such as gestational diabetes with no "
                    + "medication requirements for control would be rated here");
            ratingPRF2.put(2, "Child’s biological mother received poor or uncertain antenatal care "
                    + "or had a moderate form of pregnancy­related illness that required "
                    + "medication for control. A mother who experienced a high‐risk "
                    + "pregnancy with some complications would be rated here.");
            ratingPRF2.put(3, "Child’s biological mother had no antenatal care or had a severe form "
                    + "of pregnancy­related illnessthat required hospitalisation.  A mother "
                    + "who had toxaemia/preeclampsia would be rated here");

            prf2.setQuestionDescription(descPRF2);
            prf2.setQuestionToConsider(quesToConsiderPRF2);
            prf2.setRatingsDefinition(ratingPRF2);

            quesPRF.add(prf2);

            //create prf3
            SubQuestionEntity prf3 = new SubQuestionEntity("PRF3", "Labour/Delivery");
            List<String> descPRF3 = new ArrayList<>();
            descPRF3.add("This refers to conditions associated with, and consequences arising from, "
                    + "complications in labour and delivery of the child.");

            List<String> quesToConsiderPRF3 = new ArrayList<>();
            quesToConsiderPRF3.add("Were there any "
                    + "unusual "
                    + "circumstances "
                    + "related to the labour "
                    + "and delivery of the "
                    + "child?");

            Map<Integer, String> ratingPRF3 = new HashMap<>();
            ratingPRF3.put(0, "Child and biological mother had normal labour and delivery. ");
            ratingPRF3.put(1, "Child or biological mother had some mild problems during delivery, "
                    + "but there is no history of adverse impact. An emergency C‐section or "
                    + "a delivery‐related physical injury (e.g.,shoulder displacement) to the "
                    + "baby is rated here.");
            ratingPRF3.put(2, "Child or biological mother had problems during delivery that "
                    + "resulted in temporary functional difficulties for the child or mother. "
                    + "Extended foetal distress, postpartum haemorrhage, or uterine "
                    + "rupture would be rated here.");
            ratingPRF3.put(3, "Child had severe problems during delivery that have long­term "
                    + "implications for development (e.g., extensive oxygen deprivation, "
                    + "brain damage). A child who needed immediate or extensive "
                    + "resuscitative measures at birth would be rated here.");

            prf3.setQuestionDescription(descPRF3);
            prf3.setQuestionToConsider(quesToConsiderPRF3);
            prf3.setRatingsDefinition(ratingPRF3);

            quesPRF.add(prf3);

            //create prf4
            SubQuestionEntity prf4 = new SubQuestionEntity("PRF4", "Substance Exposure");
            List<String> descPRF4 = new ArrayList<>();
            descPRF4.add("This item describes the child’s exposure to substance use and abuse, i.e. "
                    + "tobacco, alcohol or drugs, by the child’s biological mother before birth");
            descPRF4.add("This item would be rated “1” if the child’s biological mother was on psychotropic medication during the "
                    + "pregnancy.");

            List<String> quesToConsiderPRF4 = new ArrayList<>();
            quesToConsiderPRF4.add("Did the child’s "
                    + "biological "
                    + "mother abuse "
                    + "substances "
                    + "during "
                    + "pregnancy?");
            quesToConsiderPRF4.add("Has the child "
                    + "been exposed to "
                    + "substances "
                    + "before birth?");

            Map<Integer, String> ratingPRF4 = new HashMap<>();
            ratingPRF4.put(0, "Child had no exposure to substances in the womb.");
            ratingPRF4.put(1, "Child had mild exposure to substances in the womb (e.g., mother used "
                    + "alcohol or tobacco in small amounts during pregnancy). "
                    + "Suspected substance exposure when the child was in the womb would "
                    + "also be rated here.");
            ratingPRF4.put(2, "Child was exposed to substances in the womb. Any ingestion of illegal "
                    + "drugs by the biological mother during pregnancy would be rated here.");
            ratingPRF4.put(3, "Child was exposed to significant substances in the womb and had "
                    + "symptoms of substance withdrawal at birth (e.g., crankiness, feeding "
                    + "problems, etc.)");

            prf4.setQuestionDescription(descPRF4);
            prf4.setQuestionToConsider(quesToConsiderPRF4);
            prf4.setRatingsDefinition(ratingPRF4);

            quesPRF.add(prf4);

            //create prf5
            SubQuestionEntity prf5 = new SubQuestionEntity("PRF5", "Parent/Sibling Cognitive/Physical Capacity");
            List<String> descPRF5 = new ArrayList<>();
            descPRF5.add(" This item describes how the child’s biological "
                    + "parents and siblings have fared/are faring developmentally.");
            descPRF5.add("Developmental problems include, but not limited to, Mental Retardation, Intellectual Disabilities (ID) "
                    + "and Developmental Disabilities (e.g. Autism Spectrum Disorder).");
            descPRF5.add("Behavioural problems include attention deficit, oppositional defiant or conduct disorders");
            descPRF5.add("A formal diagnosis is not required for the item to be rated ‘1’, ‘2’ or ‘3’");

            List<String> quesToConsiderPRF5 = new ArrayList<>();
            quesToConsiderPRF5.add("Do the child’s "
                    + "biological "
                    + "parents or "
                    + "siblings have any "
                    + "developmental "
                    + "disabilities?");

            Map<Integer, String> ratingPRF5 = new HashMap<>();
            ratingPRF5.put(0, "Child’s parents have no known/history of developmental disabilities. "
                    + "Child has no siblings, or existing siblings are not experiencing any "
                    + "developmental or behavioural problems");
            ratingPRF5.put(1, "Child’s parents have no known/history of developmental disabilities. "
                    + "Child has siblings who are experiencing some mild developmental or "
                    + "behavioural problems. "
                    + "Child may have at least one healthy sibling.");
            ratingPRF5.put(2, "Child’s parents have no known/history of developmental disabilities. "
                    + "Child has siblings who are experiencing significant developmental or "
                    + "behavioural problems.");
            ratingPRF5.put(3, "One or both of the child’s parents have been diagnosed with a "
                    + "developmental disability;  "
                    + "OR "
                    + "Child has multiple siblings who are experiencing significant "
                    + "developmental or behavioural problems or all siblings must have some "
                    + "developmental or behavioural problems of varying extent.");

            prf5.setQuestionDescription(descPRF5);
            prf5.setQuestionToConsider(quesToConsiderPRF5);
            prf5.setRatingsDefinition(ratingPRF5);

            quesPRF.add(prf5);

            List<Long> ids5 = new ArrayList<>();
            ids5.add(questionsSessionBean.retrieveQuestionByCode("3.4").getQuestionId());

            SubModuleEntity mod5 = subModuleSessionBean.createNewSubModuleWithManyMainQues(PRFmod, ids5);
            for (SubQuestionEntity questions : quesPRF) {
                questionsSessionBean.createSubQuestionForSubModule(mod5.getSubModuleId(), questions);
            }

            // Create Substance Use Needs SubModule
            List<String> SNdesc = new ArrayList<>();
            SNdesc.add("Rate this module ONLY IF Item 5.8 “Substance Use” was rated “1”, “2” or “3”");
            SNdesc.add("Please note that these items provide more information such as the frequency and severity of the\n"
                    + "behaviour/issue, hence the items DO NOT follow the 30‐day rating window unless otherwise specified.");

            SubModuleEntity SNmod = new SubModuleEntity("Substance Use Needs (SN) Module", SNdesc, true);
            List<SubQuestionEntity> quesSN = new ArrayList<>();

            //create SN1
            SubQuestionEntity sn1 = new SubQuestionEntity("SN1", "Type of Substance(s)");
            List<String> descSN1 = new ArrayList<>();
            descSN1.add("Specify name(s) of substance taken.");

            List<String> quesToConsiderSN1 = new ArrayList<>();

            Map<Integer, String> ratingSN1 = new HashMap<>();

            sn1.setQuestionDescription(descSN1);
            sn1.setQuestionToConsider(quesToConsiderSN1);
            sn1.setRatingsDefinition(ratingSN1);

            quesSN.add(sn1);

            //create SN2
            SubQuestionEntity sn2 = new SubQuestionEntity("SN2", "Frequency of Use: ");
            List<String> descSN2 = new ArrayList<>();
            descSN1.add("Specify frequency of substance use. ");

            List<String> quesToConsiderSN2 = new ArrayList<>();

            Map<Integer, String> ratingSN2 = new HashMap<>();

            sn2.setQuestionDescription(descSN2);
            sn2.setQuestionToConsider(quesToConsiderSN2);
            sn2.setRatingsDefinition(ratingSN2);

            quesSN.add(sn2);

            //create SN3
            SubQuestionEntity sn3 = new SubQuestionEntity("SN3", "Duration of Use: ");
            List<String> descSN3 = new ArrayList<>();
            descSN3.add("Specify the number of weeks, months or years that the child has been using the substance.");

            List<String> quesToConsiderSN3 = new ArrayList<>();

            Map<Integer, String> ratingSN3 = new HashMap<>();

            sn3.setQuestionDescription(descSN3);
            sn3.setQuestionToConsider(quesToConsiderSN3);
            sn3.setRatingsDefinition(ratingSN3);

            quesSN.add(sn3);

            //create sn4
            SubQuestionEntity sn4 = new SubQuestionEntity("SN4", "Readiness To Change");
            List<String> descSN4 = new ArrayList<>();
            descSN4.add("Ratings are based on the Trans‐theoretical Model of Change, whereby change is characterised as a “process "
                    + "involving progress through a series of stages” (Prochaska, 1997). According to this model, a rating of:");
            descSN4.add("“0” would indicate that the child is at the “Maintenance” stage. The child has already changed his/her "
                    + "substance use behaviour more than 6 months ago.  \n"
                    + "• “1” would indicate that the child is in the “Preparation/Ready to Change” stage. The child is ready to "
                    + "change his/her substance use behaviour in the next 30 days.\n"
                    + "• “2” would indicate that the child is in the “Contemplation” stage. The child is intending to change "
                    + "his/her substance use behaviour within the next 6 months. The child may still be ambivalent about "
                    + "making the change which may cause him/her to procrastinate in taking action.\n"
                    + "• “3” would indicate that the child is in the “Pre‐contemplation” stage. The child has no intention of "
                    + "changing his/her substance use behaviour in the near future (within 6 months)");

            List<String> quesToConsiderSN4 = new ArrayList<>();
            quesToConsiderSN4.add("Does the child "
                    + "recognise his/her "
                    + "substance use as "
                    + "being a problem?");
            quesToConsiderSN4.add("Is the child actively "
                    + "trying to stop using "
                    + "the substance?");

            Map<Integer, String> ratingSN4 = new HashMap<>();
            ratingSN4.put(0, "Child is abstinent and able to recognise and avoid risk factors for "
                    + "future substance abuse.");
            ratingSN4.put(1, "Child is ready to try and remain abstinent");
            ratingSN4.put(2, "Child recognises a problem but not willing to take steps for recovery.");
            ratingSN4.put(3, "Child is in denial regarding the existence of any substance use "
                    + "problem.");

            sn4.setQuestionDescription(descSN4);
            sn4.setQuestionToConsider(quesToConsiderSN4);
            sn4.setRatingsDefinition(ratingSN4);

            quesSN.add(sn4);

            //create sn5
            SubQuestionEntity sn5 = new SubQuestionEntity("SN5", "Recovery Environment");
            List<String> descSN5 = new ArrayList<>();
            descSN5.add("This item rates the environment around the child’s current living situation");

            List<String> quesToConsiderSN5 = new ArrayList<>();
            quesToConsiderSN5.add("Is the child exposed to "
                    + "substance use in "
                    + "his/her environment "
                    + "(e.g., in school, when "
                    + "on home leave)?");

            Map<Integer, String> ratingSN5 = new HashMap<>();
            ratingSN5.put(0, "No evidence that the child's environment stimulates or exposes the "
                    + "child to any alcohol or drug use");
            ratingSN5.put(1, "Mild problems in the child's environment that might expose the child "
                    + "to alcohol or drug use.   "
                    + "For example, child lives within the area of places/shops where alcohol "
                    + "or drugs are sold or available.");
            ratingSN5.put(2, "Moderate problems in the child's environment that clearly expose "
                    + "the child to alcohol or drug use. "
                    + "For example, child has peers or extended family members not living "
                    + "with him/her who use alcohol or drugs");
            ratingSN5.put(3, "Severe problems in the child's environment that stimulate the child "
                    + "to engage in alcohol or drug. "
                    + "For example, child has family members or individualsliving within the "
                    + "same home who use alcohol or drugs");

            sn5.setQuestionDescription(descSN5);
            sn5.setQuestionToConsider(quesToConsiderSN5);
            sn5.setRatingsDefinition(ratingSN5);

            quesSN.add(sn5);

            //create sn6
            SubQuestionEntity sn6 = new SubQuestionEntity("SN6", "Relapse Prevention Skills");
            List<String> descSN6 = new ArrayList<>();
            descSN5.add("This item rates the clarity of the child’s relapse prevention plan and "
                    + "whether he/she has the necessary skills to carry out the relapse prevention plan.");

            List<String> quesToConsiderSN6 = new ArrayList<>();
            quesToConsiderSN6.add("Has the child participated in "
                    + "any recovery programmes for "
                    + "his/her substance use?");
            quesToConsiderSN6.add("Does the child have a relapse "
                    + "prevention plan?\n"
                    + "➢ If yes, is the child able to "
                    + "respond well to triggers "
                    + "that may result in "
                    + "substance use?");
            quesToConsiderSN6.add("Does the child have good "
                    + "relapse prevention skills?");

            Map<Integer, String> ratingSN6 = new HashMap<>();
            ratingSN6.put(0, "Child has a clear relapse prevention plan, strong relapse "
                    + "prevention skills, AND is committed to pursuing recovery");
            ratingSN6.put(1, "Child is motivated to pursue recovery but lacks a clear "
                    + "relapse prevention plan and/or relapse prevention skills. "
                    + "Child needs help to develop a clear relapse prevention plan "
                    + "and/or relapse prevention skills.");
            ratingSN6.put(2, "Child has a relapse prevention plan but lacks motivation, "
                    + "knowledge and relapse prevention skills to recognize and "
                    + "effectively respond to triggers");
            ratingSN6.put(3, "Child is not motivated to pursue recovery, and does not have "
                    + "a relapse prevention plan.");

            sn6.setQuestionDescription(descSN6);
            sn6.setQuestionToConsider(quesToConsiderSN6);
            sn6.setRatingsDefinition(ratingSN6);

            quesSN.add(sn6);

            List<Long> ids6 = new ArrayList<>();
            ids6.add(questionsSessionBean.retrieveQuestionByCode("5.8").getQuestionId());

            SubModuleEntity mod6 = subModuleSessionBean.createNewSubModuleWithManyMainQues(SNmod, ids6);
            for (SubQuestionEntity questions : quesSN) {
                questionsSessionBean.createSubQuestionForSubModule(mod6.getSubModuleId(), questions);
            }
            // ================== Create Runaway needs SubModule ===============
            List<String> RNdesc = new ArrayList<>();
            RNdesc.add("Rate this module ONLY IF Item 6.6 “Runaway” was rated “1”, “2” or “3”");
            RNdesc.add("Please note that these items provide more information such as the frequency and severity of the\n"
                    + "behaviour/issue, hence the items DO NOT follow the 30‐day rating window unless otherwise specified.");

            SubModuleEntity RNmod = new SubModuleEntity("Runaway Needs (RN) Module", RNdesc, true);
            List<SubQuestionEntity> quesRN = new ArrayList<>();

            //create RN1
            SubQuestionEntity rn1 = new SubQuestionEntity("RN1", "Frequency of Running: ");
            List<String> descRN1 = new ArrayList<>();
            descRN1.add("Specify how often the child has run away in the past year"
                    + "(e.g. 5 times a year, once a week or at every opportunity) ");

            List<String> quesToConsiderRN1 = new ArrayList<>();

            Map<Integer, String> ratingRN1 = new HashMap<>();

            rn1.setQuestionDescription(descRN1);
            rn1.setQuestionToConsider(quesToConsiderRN1);
            rn1.setRatingsDefinition(ratingRN1);

            quesRN.add(rn1);

            //create RN2
            SubQuestionEntity rn2 = new SubQuestionEntity("RN2", "Duration of Absense: ");
            List<String> descRN2 = new ArrayList<>();
            descRN1.add("Specify the length of time (i.e. how many hours, days, weeks or months) that the child has run away. ");

            List<String> quesToConsiderRN2 = new ArrayList<>();

            Map<Integer, String> ratingRN2 = new HashMap<>();

            rn2.setQuestionDescription(descRN2);
            rn2.setQuestionToConsider(quesToConsiderRN2);
            rn2.setRatingsDefinition(ratingRN2);

            quesRN.add(rn2);

            //create rn3
            SubQuestionEntity rn3 = new SubQuestionEntity("RN3", "Consistency of Destination");
            List<String> descRN3 = new ArrayList<>();
            descRN3.add("This item gives an indication whether the child runs to the same "
                    + "location or whether there is any geographical pattern.");

            List<String> quesToConsiderRN3 = new ArrayList<>();
            quesToConsiderRN3.add("Where does the child "
                    + "usually run to?");

            Map<Integer, String> ratingRN3 = new HashMap<>();
            ratingRN3.put(0, "Child always runs to the same location.");
            ratingRN3.put(1, "Child generally runs to the same location or neighbourhood");
            ratingRN3.put(2, "Child runs to the same community but the specific locations change");
            ratingRN3.put(3, "Child runs to no planned destination");

            rn3.setQuestionDescription(descRN3);
            rn3.setQuestionToConsider(quesToConsiderRN3);
            rn3.setRatingsDefinition(ratingRN3);

            quesRN.add(rn3);

            //create rn4
            SubQuestionEntity rn4 = new SubQuestionEntity("RN4", "Planning");
            List<String> descRN4 = new ArrayList<>();
            descRN4.add("This item indicates whether the running away behaviour is impulsive or planned.");

            List<String> quesToConsiderRN4 = new ArrayList<>();
            quesToConsiderRN4.add("Is there any planning "
                    + "involved when the "
                    + "child runs away?");

            Map<Integer, String> ratingRN4 = new HashMap<>();
            ratingRN4.put(0, "Running behaviour indicates lack of planning (i.e.., completely "
                    + "spontaneous and emotionally impulsive).");
            ratingRN4.put(1, "Running behaviour indicates little planning, with minimal "
                    + "consideration of implications of behaviour.");
            ratingRN4.put(2, "Running behaviour indicates a level of planning that considers "
                    + "possible implications of behaviour.");
            ratingRN4.put(3, "Running behaviour indicates a high level of planning with carefully "
                    + "orchestrated alternative plans in order to maximise likelihood of not "
                    + "being found.");

            rn4.setQuestionDescription(descRN4);
            rn4.setQuestionToConsider(quesToConsiderRN4);
            rn4.setRatingsDefinition(ratingRN4);

            quesRN.add(rn4);

            //create rn5
            SubQuestionEntity rn5 = new SubQuestionEntity("RN5", "Safety of Destination");
            List<String> descRN5 = new ArrayList<>();

            List<String> quesToConsiderRN5 = new ArrayList<>();
            quesToConsiderRN5.add("Is the "
                    + "destination "
                    + "the child runs "
                    + "to safe?");

            Map<Integer, String> ratingRN5 = new HashMap<>();
            ratingRN5.put(0, "Child runs to a safe environment that meets his/her basic needs (e.g., food, "
                    + "shelter).");
            ratingRN5.put(1, "Child runs to generally safe environments; however, they might be "
                    + "somewhat unstable or variable");
            ratingRN5.put(2, "Child runsto generally unsafe environmentsthat cannot meet his/her basic "
                    + "needs. There are some concerns that the child might be victimised.");
            ratingRN5.put(3, "Child runs to very unsafe environments where the likelihood that he/she "
                    + "will be victimised is high");

            rn5.setQuestionDescription(descRN5);
            rn5.setQuestionToConsider(quesToConsiderRN5);
            rn5.setRatingsDefinition(ratingRN5);

            quesRN.add(rn5);

            //create rn6
            SubQuestionEntity rn6 = new SubQuestionEntity("RN6", "Involvement in illegal activities");
            List<String> descRN6 = new ArrayList<>();

            List<String> quesToConsiderRN6 = new ArrayList<>();
            quesToConsiderRN6.add("Does the child "
                    + "engage in any "
                    + "illegal "
                    + "activities "
                    + "during his/her "
                    + "abscondence?");

            Map<Integer, String> ratingRN6 = new HashMap<>();
            ratingRN6.put(0, "Child does not engage in illegal activities while on the run beyond those "
                    + "involved with the running itself");
            ratingRN6.put(1, "Child engages in status offences while on run beyond those involved with "
                    + "the running itself (e.g., curfew violations, underage drinking).");
            ratingRN6.put(2, "Child engages in delinquent activities while on run (e.g. theft, vandalism).");
            ratingRN5.put(3, "Child engages in dangerous delinquent activities while on run (e.g., "
                    + "prostitution, rioting, voluntarily causing hurt)");

            rn6.setQuestionDescription(descRN6);
            rn6.setQuestionToConsider(quesToConsiderRN6);
            rn6.setRatingsDefinition(ratingRN6);

            quesRN.add(rn6);

            //create rn7
            SubQuestionEntity rn7 = new SubQuestionEntity("RN7", "Likelihood of return on own");
            List<String> descRN7 = new ArrayList<>();

            List<String> quesToConsiderRN7 = new ArrayList<>();
            quesToConsiderRN7.add("Is the child "
                    + "likely to return "
                    + "on his/her own "
                    + "after running "
                    + "away?");

            Map<Integer, String> ratingRN7 = new HashMap<>();
            ratingRN7.put(0, "Child will return from run on his/her own without prompting");
            ratingRN7.put(1, "Child will return from run when found but not without being found.");
            ratingRN7.put(2, "Child will make him/herself difficult to find and/or might passively resist "
                    + "return once found.");
            ratingRN7.put(3, "Child makes repeated and concerted efforts to hide so as to not be found "
                    + "and/or actively resists return");

            rn7.setQuestionDescription(descRN7);
            rn7.setQuestionToConsider(quesToConsiderRN7);
            rn7.setRatingsDefinition(ratingRN7);

            quesRN.add(rn7);

            //create rn8
            SubQuestionEntity rn8 = new SubQuestionEntity("RN8", "Involvement with others");
            List<String> descRN8 = new ArrayList<>();
            descRN8.add("This item rates whether the child is influenced by others to run away");

            List<String> quesToConsiderRN8 = new ArrayList<>();
            quesToConsiderRN8.add("Is there "
                    + "anyone else "
                    + "involved when "
                    + "the child runs "
                    + "away?");

            Map<Integer, String> ratingRN8 = new HashMap<>();
            ratingRN8.put(0, "Child runs by him/herself with no involvement of others or others may\n"
                    + "discourage behaviour or encourage child to return from run.  ");
            ratingRN8.put(1, "Others enable child to run by not discouraging child’s behaviou");
            ratingRN8.put(2, "Others involved in encouraging child to run away.");
            ratingRN8.put(3, "Others actively involved by assisting in runaway behaviour");

            rn8.setQuestionDescription(descRN8);
            rn8.setQuestionToConsider(quesToConsiderRN8);
            rn8.setRatingsDefinition(ratingRN8);

            quesRN.add(rn8);

            List<Long> ids7 = new ArrayList<>();
            ids7.add(questionsSessionBean.retrieveQuestionByCode("6.6").getQuestionId());

            SubModuleEntity mod7 = subModuleSessionBean.createNewSubModuleWithManyMainQues(RNmod, ids7);
            for (SubQuestionEntity questions : quesRN) {
                questionsSessionBean.createSubQuestionForSubModule(mod7.getSubModuleId(), questions);
            }
            // ========== Create Juvenile Justice Needs SubModule ==================
            List<String> JJNdesc = new ArrayList<>();
            JJNdesc.add("Rate this module ONLY IF Item 6.7 “Delinquent Behaviour” was rated “1”, “2” or “3”");
            JJNdesc.add("Please note that these items provide more information such as the frequency and severity of the\n"
                    + "behaviour/issue, hence the items DO NOT follow the 30‐day rating window unless otherwise specified.");

            SubModuleEntity JJNmod = new SubModuleEntity("Juvenile Justice Needs (JJN) Module", JJNdesc, true);
            List<SubQuestionEntity> quesJJN = new ArrayList<>();

            //create JJN1
            SubQuestionEntity jjn1 = new SubQuestionEntity("JJN1", "State Type of Behaviour: ");
            List<String> descJJN1 = new ArrayList<>();

            List<String> quesToConsiderJJN1 = new ArrayList<>();

            Map<Integer, String> ratingJJN1 = new HashMap<>();

            jjn1.setQuestionDescription(descJJN1);
            jjn1.setQuestionToConsider(quesToConsiderJJN1);
            jjn1.setRatingsDefinition(ratingJJN1);

            quesJJN.add(jjn1);

            //create JJN2
            SubQuestionEntity jjn2 = new SubQuestionEntity("JJN2", "Frequency of Delinquent Behaviour ");
            List<String> descJJN2 = new ArrayList<>();
            descJJN2.add("This item rates the frequency of the child’s engagement\n"
                    + "in delinquent behaviour in the past.");
            descJJN2.add("Please rate using time frames provided in the anchors.");

            List<String> quesToConsiderJJN2 = new ArrayList<>();
            quesToConsiderJJN2.add("Has the child engaged "
                    + "in delinquent "
                    + "behaviour in the past?");

            Map<Integer, String> ratingJJN2 = new HashMap<>();
            ratingJJN2.put(0, "Current delinquent behaviour is the first known occurrence. ");
            ratingJJN2.put(1, "Child has engaged in multiple delinquent acts in the past one year.");
            ratingJJN2.put(2, "Child has engaged in multiple delinquent acts for more than one "
                    + "year but has had periods of at least 3 months where he/she did "
                    + "not engage in delinquent behaviour");
            ratingJJN2.put(3, "Child has engaged in multiple delinquent acts for more than one "
                    + "year without any period of at least 3 months where he/she did not "
                    + "engage in delinquent behaviour.");

            jjn2.setQuestionDescription(descJJN2);
            jjn2.setQuestionToConsider(quesToConsiderJJN2);
            jjn2.setRatingsDefinition(ratingJJN2);

            quesJJN.add(jjn2);

            //create JJN3
            SubQuestionEntity jjn3 = new SubQuestionEntity("JJN3", "Planning");
            List<String> descJJN3 = new ArrayList<>();
            descJJN3.add("This item indicates whether the delinquent behaviour is impulsive or planned.");

            List<String> quesToConsiderJJN3 = new ArrayList<>();
            quesToConsiderJJN3.add("Is there any planning "
                    + "involved when the child "
                    + "engages in delinquent "
                    + "behaviour?");
            quesToConsiderJJN3.add("Does the child put "
                    + "him/herself in situations "
                    + "where these behaviours are "
                    + "likely to occur (e.g., "
                    + "hanging out at void decks "
                    + "with gang activity)?");

            Map<Integer, String> ratingJJN3 = new HashMap<>();
            ratingJJN3.put(0, "No evidence of any planning.  Delinquent behaviour appears "
                    + "opportunistic or impulsive");
            ratingJJN3.put(1, "Evidence suggests that child places him/herself into "
                    + "situations where the likelihood of delinquent behaviour is "
                    + "enhanced.");
            ratingJJN3.put(2, "Evidence of planning of delinquent behaviour, with some "
                    + "consideration of possible implications of the behaviour.");
            ratingJJN3.put(3, "Considerable evidence of significant planning of delinquent "
                    + "behaviour, with carefully orchestrated alternative plans in "
                    + "order to minimise likelihood of being caught.");

            jjn3.setQuestionDescription(descJJN3);
            jjn3.setQuestionToConsider(quesToConsiderJJN3);
            jjn3.setRatingsDefinition(ratingJJN3);

            quesJJN.add(jjn3);

            //create JJN4
            SubQuestionEntity jjn4 = new SubQuestionEntity("JJN4", "Severity of Deliquent behaviour");
            List<String> descJJN4 = new ArrayList<>();
            descJJN4.add("Thisitem ratesthe severity of the child’s delinquency, which "
                    + "includes whether the child has prior Juvenile Arrest Cases (JAC) and/or the child’s delinquent "
                    + "behaviour poses a danger to the community. ");

            List<String> quesToConsiderJJN4 = new ArrayList<>();
            quesToConsiderJJN4.add("Does the child’s delinquent "
                    + "behaviour put anyone at risk "
                    + "of being harmed?");
            quesToConsiderJJN4.add("Has the child been involved "
                    + "with the juvenile justice "
                    + "system? (e.g., gone to "
                    + "court)?");
            quesToConsiderJJN4.add("Does the child pose any risk "
                    + "to others in the community?");

            Map<Integer, String> ratingJJN4 = new HashMap<>();
            ratingJJN4.put(0, "Child is on a Family Guidance Order only and has only "
                    + "committed minor offences (e.g., curfew, runaway, "
                    + "truancy). "
                    + "OR "
                    + "Child has no known youth court intakes and presents no "
                    + "risk to the community.");
            ratingJJN4.put(1, "Child has engaged in minor delinquent behaviour (e.g., "
                    + "shoplifting, trespassing, minor vandalism) that that may "
                    + "represent a risk to community property. "
                    + "OR "
                    + "Child has history of delinquency, but no youth court intakes "
                    + "in the last 30 days");
            ratingJJN4.put(2, "Child has engaged in significant delinquent behaviour (e.g., "
                    + "extensive theft, minor assault, significant property crime) "
                    + "that places community residents in some danger of "
                    + "physical harm. This danger may be an indirect effect of the "
                    + "child’s behaviour. "
                    + "OR "
                    + "Child has had 1 to 2 youth court intakes in last 30 days");
            ratingJJN4.put(3, "Child has engaged in delinquent behaviour that places "
                    + "community members at risk of significant physical harm. "
                    + "OR "
                    + "Child has more than 2 youth court intakes in last 30 days.");

            jjn4.setQuestionDescription(descJJN4);
            jjn4.setQuestionToConsider(quesToConsiderJJN4);
            jjn4.setRatingsDefinition(ratingJJN4);

            quesJJN.add(jjn4);

            //create JJN5
            SubQuestionEntity jjn5 = new SubQuestionEntity("JJN5", "Peer Influences");
            List<String> descJJN5 = new ArrayList<>();
            descJJN5.add("This item indicates whether the child’s peer social networks have a history of "
                    + "criminal behaviour that could expose or influence the child to engage in delinquent behaviour.");

            List<String> quesToConsiderJJN5 = new ArrayList<>();
            quesToConsiderJJN5.add("Does the child’s main peer "
                    + "group engage in delinquent "
                    + "behaviours?");
            quesToConsiderJJN5.add("Is the child a member of a "
                    + "gang?");
            quesToConsiderJJN5.add("Does the child have friends "
                    + "who are gang members?");

            Map<Integer, String> ratingJJN5 = new HashMap<>();
            ratingJJN5.put(0, "Child's primary peer social network does not engage in "
                    + "delinquent behaviour");
            ratingJJN5.put(1, "Child has peers in his/her primary peer social network "
                    + "who do not engage in delinquent behaviour but has "
                    + "some peers who do.");
            ratingJJN5.put(2, "Child predominantly has peers who engage in delinquent "
                    + "behaviour but child is not a member of a gang.");
            ratingJJN5.put(3, "Child is a member of a gang whose membership "
                    + "encourages or requires illegal behaviour as an aspect of "
                    + "gang membership.");

            jjn5.setQuestionDescription(descJJN5);
            jjn5.setQuestionToConsider(quesToConsiderJJN5);
            jjn5.setRatingsDefinition(ratingJJN5);

            quesJJN.add(jjn5);

            //create JJN6
            SubQuestionEntity jjn6 = new SubQuestionEntity("JJN6", "Parent Criminal Behaviour/Influences");
            List<String> descJJN6 = new ArrayList<>();
            descJJN6.add("This item indicates whether the child’s parent(s) have "
                    + "a history of criminal behaviour that could expose or influence the child to engage in delinquent "
                    + "behaviour");
            descJJN6.add("Please rate using the time frames provided in the anchors");

            List<String> quesToConsiderJJN6 = new ArrayList<>();
            quesToConsiderJJN6.add("Have the child’s parents ever "
                    + "engaged in criminal activity?\n"
                    + "• If yes, does the child have "
                    + "contact with this parent?");

            Map<Integer, String> ratingJJN6 = new HashMap<>();
            ratingJJN6.put(0, "No evidence that child's parents have ever engaged in "
                    + "criminal behaviour.");
            ratingJJN6.put(1, "One of child's parents has history of criminal behaviour but "
                    + "child has not been in contact with this parent for at least one "
                    + "year.");
            ratingJJN6.put(2, "One of child's parents has history of criminal behaviour and "
                    + "child has been in contact with this parent in the past year.");
            ratingJJN6.put(3, "Both of child's parents have history of criminal behaviour.");

            jjn6.setQuestionDescription(descJJN6);
            jjn6.setQuestionToConsider(quesToConsiderJJN6);
            jjn6.setRatingsDefinition(ratingJJN6);

            quesJJN.add(jjn6);

            //create JJN7
            SubQuestionEntity jjn7 = new SubQuestionEntity("JJN7", "Environmental Influences");
            List<String> descJJN7 = new ArrayList<>();
            descJJN7.add("The rating assessesthe extent that the neighbourhood orthe current "
                    + "living environment exposes or influences the child to engage in delinquent behaviour.");
            descJJN7.add("Please rate the environment around the child’s living situation");

            List<String> quesToConsiderJJN7 = new ArrayList<>();
            quesToConsiderJJN7.add("Does the child live in a "
                    + "neighbourhood known for "
                    + "gang activity/criminal "
                    + "activity?");
            quesToConsiderJJN7.add("Is the child exposed to illegal "
                    + "activities within his "
                    + "neighbourhood/environment?");

            Map<Integer, String> ratingJJN7 = new HashMap<>();
            ratingJJN7.put(0, "No evidence that the child's environment stimulates or "
                    + "exposes the child to any delinquent behaviour.");
            ratingJJN7.put(1, "Mild problemsin the child's environment that might expose "
                    + "the child to delinquent behaviour");
            ratingJJN7.put(2, "Moderate problems in the child's environment that clearly "
                    + "expose the child to delinquent behaviour. "
                    + "For example, the child encounters neighbours committing "
                    + "criminal activities (e.g., taking drugs) on a consistent basis.");
            ratingJJN7.put(3, "Severe problems in the child's environment that stimulate "
                    + "the child to engage in delinquent behaviour. "
                    + "For example, the child is encouraged by neighbours to join "
                    + "them in committing criminal activities (e.g., taking drugs)");

            jjn7.setQuestionDescription(descJJN7);
            jjn7.setQuestionToConsider(quesToConsiderJJN7);
            jjn7.setRatingsDefinition(ratingJJN7);

            quesJJN.add(jjn7);

            List<Long> ids8 = new ArrayList<>();
            ids8.add(questionsSessionBean.retrieveQuestionByCode("6.7").getQuestionId());

            SubModuleEntity mod8 = subModuleSessionBean.createNewSubModuleWithManyMainQues(JJNmod, ids8);
            for (SubQuestionEntity questions : quesJJN) {
                questionsSessionBean.createSubQuestionForSubModule(mod8.getSubModuleId(), questions);
            }
            // ================ Create Other Risk Behaviours SubModule ==================
            List<String> ORBdesc = new ArrayList<>();
            ORBdesc.add("Rate this module ONLY IF Item 6.10 “Other Risk Behaviours” was rated “1”, “2” or “3”");
            ORBdesc.add("**Ensure that the rating for the Item 6.10 corresponds to the HIGHEST rating in this module**");

            SubModuleEntity ORBmod = new SubModuleEntity("Other Risk Behaviours (ORB) Module", ORBdesc, false);
            List<SubQuestionEntity> quesORB = new ArrayList<>();

            //create ORB1
            SubQuestionEntity orb1 = new SubQuestionEntity("ORB1", "Other risk-taking behaviours ");
            List<String> descORB1 = new ArrayList<>();
            descORB1.add("This item is used to describe behaviour not covered by either "
                    + "Suicide Risk (Item 6.4) or Self‐Harm (Item 6.5) that places a child at risk of physical injury");
            descORB1.add("Any reckless behaviour that has significant potential to place the child in danger of physical harm would "
                    + "be rated here.");
            descORB1.add("Examples of risk‐taking behaviour include:  "
                    + "• Running across a busy road for thrills "
                    + "• Playing games involving dangerous dares "
                    + "• Having unprotected sex with multiple partners");
            descORB1.add("Note:   "
                    + "• Risk‐taking behaviour may not always be impulsive in nature. "
                    + "• Do not rate this item if child engages in extreme sports/combat sports in a safe and regulated "
                    + "environment.");
            descORB1.add("Please rate using time frames provided in the anchors");

            List<String> quesToConsiderORB1 = new ArrayList<>();
            quesToConsiderORB1.add("Has the child ever "
                    + "engaged in any "
                    + "activity or acted "
                    + "in a way that "
                    + "might be "
                    + "dangerous to "
                    + "him/her?");
            quesToConsiderORB1.add("Is the child ever "
                    + "reckless?");

            Map<Integer, String> ratingORB1 = new HashMap<>();
            ratingORB1.put(0, "No evidence of behaviours, other than suicide orself‐harm, that place the "
                    + "child at risk of physical harm.");
            ratingORB1.put(1, "History of behaviour, other than suicide or self‐harm, that places child at "
                    + "risk of physical harm (but none in the last 30 days). This includes reckless "
                    + "and risk‐taking behaviour that may endanger the child.");
            ratingORB1.put(2, "Engaged in behaviour, other than suicide or self‐harm, that places "
                    + "him/her in danger of physical harm. This includes reckless behaviour or "
                    + "intentional risk‐taking behaviour.");
            ratingORB1.put(3, "Engaged in behaviour, other than suicide or self‐harm, that places "
                    + "him/her at immediate risk of death. This includes reckless behaviour or "
                    + "intentional risk‐taking behaviour");

            orb1.setQuestionDescription(descORB1);
            orb1.setQuestionToConsider(quesToConsiderORB1);
            orb1.setRatingsDefinition(ratingORB1);

            quesORB.add(orb1);

            //create ORB2
            SubQuestionEntity orb2 = new SubQuestionEntity("ORB2", "Danger to Others ");
            List<String> descORB2 = new ArrayList<>();
            descORB2.add("This item rates the child’s violent or aggressive behaviour");
            descORB2.add("The behaviour rated in this item must be intentional and have the potential to cause significant bodily "
                    + "harm.");
            descORB2.add("Unintentional, reckless behaviour that may cause physical harm to others is not rated on this item.  ");
            descORB2.add("A”3” is reserved for a child who is acutely dangerous to others within the past 24 hours. ");
            descORB2.add("For example: \n"
                    + "• A boy who threatens his mother with a knife would be rated a “3” at the time of the incident.\n"
                    + "• If he remains committed to killing or injuring his mother even several days after the threat, he would "
                    + "remain a “3” rating.\n"
                    + "• If he calms down, expresses remorse, and does not have any immediate intention to hurt his "
                    + "mother, he would be reduced to a “2” rating");
            descORB2.add("Please rate using time frames provided in the anchors");

            List<String> quesToConsiderORB2 = new ArrayList<>();
            quesToConsiderORB2.add("Has the child ever injured anyone "
                    + "on purpose?");
            quesToConsiderORB2.add("Does the child get into physical "
                    + "fights?\n"
                    + "➢ If yes, were weapons used in "
                    + "the fights?");
            quesToConsiderORB2.add("Has the child ever threatened to "
                    + "hurt or seriously injure anyone?");

            Map<Integer, String> ratingORB2 = new HashMap<>();
            ratingORB2.put(0, "No evidence of any current violent or aggressive behaviour");
            ratingORB2.put(1, "History of violent thoughts, and/or physical aggression that "
                    + "has put self or others in danger of harm. ");
            ratingORB2.put(2, "Recent violent thoughts or behaviour, and/or physically "
                    + "hurting others during the past 30 days");
            ratingORB2.put(3, "Acute violent thoughts with a plan, and/or command "
                    + "hallucinations that involve harming others, and/or physically "
                    + "hurting others using a weapon (e.g., scissors, pen knifes) "
                    + "occurring during the past 30 days.");

            orb2.setQuestionDescription(descORB2);
            orb2.setQuestionToConsider(quesToConsiderORB2);
            orb2.setRatingsDefinition(ratingORB2);

            quesORB.add(orb2);

            //create ORB3
            SubQuestionEntity orb3 = new SubQuestionEntity("ORB3", "Sexual Aggression");
            List<String> descORB3 = new ArrayList<>();
            descORB3.add("This item includes aggressive sexual behaviour (the use of force or threat) "
                    + "and sexual behaviour in which the child takes advantage of a more vulnerable child through "
                    + "seduction, coercion or force.  ");
            descORB3.add("Please rate using time frames provided in the anchors.");

            List<String> quesToConsiderORB3 = new ArrayList<>();
            quesToConsiderORB3.add("Has the child ever been "
                    + "sexually aggressive to "
                    + "another child?\n"
                    + "➢ If yes, has the child "
                    + "ever targeted younger "
                    + "children or e.g., "
                    + "children with low IQ?  ");
            quesToConsiderORB3.add("Was there any threat or "
                    + "actual use of force?");

            Map<Integer, String> ratingORB3 = new HashMap<>();
            ratingORB3.put(0, "No evidence of any history of sexually aggressive behaviour.");
            ratingORB3.put(1, "History ofsexually aggressive behaviour (but not in past year) "
                    + "OR"
                    + "Mild sexually inappropriate behaviour in the past year."
                    + "For example, occasional sexually inappropriate or harassing"
                    + "language and/or behaviour.");
            ratingORB3.put(2, "Moderate problems with sexually abusive behaviour within "
                    + "the last one year, but not within the last 30 days. "
                    + "For example, frequent inappropriate sexually aggressive or "
                    + "harassing language and/or behaviour. ");
            ratingORB3.put(3, "Child has engaged in sexually aggressive behaviour in the "
                    + "past 30 days. Use of force and threats can be rated here.");

            orb3.setQuestionDescription(descORB3);
            orb3.setQuestionToConsider(quesToConsiderORB3);
            orb3.setRatingsDefinition(ratingORB3);

            quesORB.add(orb3);

            //create ORB4
            SubQuestionEntity orb4 = new SubQuestionEntity("ORB4", "Sanction-Seeking Behaviour");
            List<String> descORB4 = new ArrayList<>();
            descORB4.add("This item describes intentional, obnoxious behaviour the child "
                    + "engages in to intentionally force adults to sanction/punish him/her.");
            descORB4.add("Thisitem hassome intentionality to it, in that the child is intentionally misbehaving in order to force the "
                    + "adult to sanction him/her. Hence, you will need to consider “why” the child is misbehaving for thisitem.");

            List<String> quesToConsiderORB4 = new ArrayList<>();
            quesToConsiderORB4.add("Does the child seem to "
                    + "purposely get into trouble "
                    + "to make adults angry in "
                    + "order to get punished?");
            quesToConsiderORB4.add("Are there any "
                    + "observations of the child "
                    + "using bad language or "
                    + "doing things that are "
                    + "obnoxious or rude on "
                    + "purpose?");

            Map<Integer, String> ratingORB4 = new HashMap<>();
            ratingORB4.put(0, "No evidence of sanction‐seeking behaviour.  ");
            ratingORB4.put(1, "History or mild sanction‐seeking behaviour that forces adults to "
                    + "sanction/punish the child. "
                    + "For example, this may include provocative comments or "
                    + "behaviour aimed at getting a negative response from adults.");
            ratingORB4.put(2, "Moderate sanction‐seeking behaviour."
                    + "For example, child may be intentionally getting into trouble in "
                    + "school, at home or in the community, and the "
                    + "sanctions/punishments or threats of sanctions/punishments "
                    + "are causing problems in the child’s life");
            ratingORB4.put(3, "Severe sanction‐seeking behaviour. "
                    + "For example, there may be frequent, serious inappropriate "
                    + "behaviour motivated by the intention to force adults to seriously "
                    + "and/or repeatedly sanction/punish the child. Sanction‐seeking "
                    + "behaviours are sufficiently severe that they place the child at risk "
                    + "of significant punishments (e.g., expulsion, removal from the "
                    + "community).");

            orb4.setQuestionDescription(descORB4);
            orb4.setQuestionToConsider(quesToConsiderORB4);
            orb4.setRatingsDefinition(ratingORB4);

            quesORB.add(orb4);

            List<Long> ids9 = new ArrayList<>();
            ids9.add(questionsSessionBean.retrieveQuestionByCode("6.10").getQuestionId());

            SubModuleEntity mod9 = subModuleSessionBean.createNewSubModuleWithManyMainQues(ORBmod, ids9);
            for (SubQuestionEntity questions : quesORB) {
                questionsSessionBean.createSubQuestionForSubModule(mod9.getSubModuleId(), questions);
            }

            // ============= Create Caregiving Needs SubModule ======================
            List<String> CNdesc = new ArrayList<>();
            CNdesc.add("Rate this module ONLY IF Item 9.7 “Needs as a Caregiver” was rated “1”, “2” or “3”");
            CNdesc.add("**Ensure that the rating for the Item 9.7 corresponds to the HIGHEST rating in this module**");

            SubModuleEntity CNmod = new SubModuleEntity("CareGiving Needs (CN) Module", CNdesc, false);
            List<SubQuestionEntity> quesCN = new ArrayList<>();

            //create CN1
            SubQuestionEntity cn1 = new SubQuestionEntity("CN1", "State Type of Caregiving Responsibility ");
            List<String> descCN1 = new ArrayList<>();
            descCN1.add("Own Child/Sibling/Parent/Grandparent/Others  \n"
                    + "Please specify if others");

            List<String> quesToConsiderCN1 = new ArrayList<>();

            Map<Integer, String> ratingCN1 = new HashMap<>();

            cn1.setQuestionDescription(descCN1);
            cn1.setQuestionToConsider(quesToConsiderCN1);
            cn1.setRatingsDefinition(ratingCN1);

            quesCN.add(cn1);

            //create CN2
            SubQuestionEntity cn2 = new SubQuestionEntity("CN2", "Social Resources");
            List<String> descCN2 = new ArrayList<>();
            descCN2.add("This item rates the availability of unpaid social resources the youth can rely on for "
                    + "caregiving in times of need");
            descCN2.add("Social resources could include friends, family members, neighbours, people that are known and trusted by "
                    + "the youth.");
            descCN2.add("Professionals working with the family (e.g., MSF officers, school counsellors) should not be rated here");
            descCN2.add("“Actively helping” refers to a stable and reliable source of help that currently participates in caregiving");

            List<String> quesToConsiderCN2 = new ArrayList<>();
            quesToConsiderCN2.add("Does the youth have the "
                    + "support of extended family, "
                    + "friends, people from the "
                    + "neighbourhood i.e. people "
                    + "who can be tapped on to "
                    + "help with taking care of "
                    + "his/her "
                    + "child/sibling/parent/grandparent?");

            Map<Integer, String> ratingCN2 = new HashMap<>();
            ratingCN2.put(0, "The youth has significant family and social networks that are "
                    + "actively helping the youth with caregiving");
            ratingCN2.put(1, "The youth has some family or social networks that are actively "
                    + "helping the youth with caregiving.");
            ratingCN2.put(2, "The youth haslimited family orsocial networksthat may be able "
                    + "to help the youth with caregiving");
            ratingCN2.put(3, "The youth has no family or social networks that may be able to "
                    + "help the youth with caregiving. This lack of resource "
                    + "prevents/hinders the youth from providing care.");

            cn2.setQuestionDescription(descCN2);
            cn2.setQuestionToConsider(quesToConsiderCN2);
            cn2.setRatingsDefinition(ratingCN2);

            quesCN.add(cn2);

            //create CN3
            SubQuestionEntity cn3 = new SubQuestionEntity("CN3", "Caregiving Stress");
            List<String> descCN3 = new ArrayList<>();
            descCN3.add("This item describes the youth’s stress resulting from taking care of the needs of "
                    + "the dependent, and the impact of his/her needs on the youth.");
            descCN3.add("Consider the impact of the caregiving needs on the stress and well‐being of the youth. For example, a "
                    + "youth who is a parent of a child with special needs may feel burdened by the impact of the child’s needs.");

            List<String> quesToConsiderCN3 = new ArrayList<>();
            quesToConsiderCN3.add("Does the youth find it stressful at "
                    + "times to manage the challenges of "
                    + "caregiving?");
            quesToConsiderCN3.add("Does the stress ever interfere with "
                    + "the youth’s caregiving ability? "
                    + "➢ If yes, does it ever reach a level "
                    + "where the youth feels like he/she "
                    + "cannot manage/wants to stop "
                    + "the caregiving?");

            Map<Integer, String> ratingCN3 = new HashMap<>();
            ratingCN3.put(0, "Youth is able to manage the stress of caregiving");
            ratingCN3.put(1, "Youth has some problems managing the stress of "
                    + "caregiving.");
            ratingCN3.put(2, "Youth has notable problems managing the stress of "
                    + "caregiving. This stress interferes with the capacity "
                    + "to give care");
            ratingCN3.put(3, "Youth is unable to manage the stress associated "
                    + "with caregiving. Thisstress preventsthe youth from "
                    + "providing care");

            cn3.setQuestionDescription(descCN3);
            cn3.setQuestionToConsider(quesToConsiderCN3);
            cn3.setRatingsDefinition(ratingCN3);

            quesCN.add(cn3);

            //create CN4
            SubQuestionEntity cn4 = new SubQuestionEntity("CN4", "Basic Care/Daily Living");
            List<String> descCN4 = new ArrayList<>();
            descCN4.add("This item describes the youth’s ability to provide for the basic needs (e.g., "
                    + "shelter, food, safety, and clothing) of his/her child/sibling/parent/grandparent/others.");
            descCN4.add("Note that this item can be culture‐sensitive");
            descCN4.add("In order to minimise the cultural issues, we recommend thinking of this item in terms of whether the "
                    + "dependent is thriving and generally healthy, and whether the youth is receptive to alternative "
                    + "suggestions");

            List<String> quesToConsiderCN4 = new ArrayList<>();
            quesToConsiderCN4.add("Does the youth have the "
                    + "ability to provide for the "
                    + "dependent’s basic needs "
                    + "(e.g., food, shelter, "
                    + "safety, clothes)?");
            quesToConsiderCN4.add("Does the dependent/s "
                    + "usually look well‐cared "
                    + "for?");

            Map<Integer, String> ratingCN4 = new HashMap<>();
            ratingCN4.put(0, "The youth has essential daily living skills needed to care for "
                    + "his/her dependent/s");
            ratingCN4.put(1, "The youth needs verbal prompting to complete the daily living "
                    + "skills required to care for his/her dependent/s");
            ratingCN4.put(2, "The youth needs assistance (physical prompting) to complete the "
                    + "daily living skills required to care for his/her dependent/s");
            ratingCN4.put(3, "The youth does not have the daily living skills required to care for "
                    + "his/her dependent/s. Caregiver needs immediate intervention.");

            cn4.setQuestionDescription(descCN4);
            cn4.setQuestionToConsider(quesToConsiderCN4);
            cn4.setRatingsDefinition(ratingCN4);

            quesCN.add(cn4);

            //create CN5
            SubQuestionEntity cn5 = new SubQuestionEntity("CN5", "Safety By The Youth");
            List<String> descCN5 = new ArrayList<>();
            descCN5.add(". This item describes whether the youth is able to provide a safe environment for "
                    + "his/her dependent/s.");
            descCN5.add("This item does not describe situations in which the youth is unable to prevent the dependent from hurting "
                    + "him/herself despite well‐intentioned efforts");
            descCN5.add("A “2” or “3” on this item requires the involvement of relevant authorities such as the Child Protective\n"
                    + "Service");

            List<String> quesToConsiderCN5 = new ArrayList<>();
            quesToConsiderCN5.add("Are there any safety "
                    + "concerns when the "
                    + "dependent is with the "
                    + "youth in his/her house? "
                    + "(e.g., from a child "
                    + "protection perspective)");
            quesToConsiderCN5.add("Does the household "
                    + "environment pose any "
                    + "physical danger to the "
                    + "dependent?");

            Map<Integer, String> ratingCN5 = new HashMap<>();
            ratingCN5.put(0, "Household is safe and secure. The youth’s dependent is at no risk "
                    + "from others");
            ratingCN5.put(1, "Concerns exist about the safety of the dependent/s due to history "
                    + "or others within the vicinity that might be abusive; "
                    + "OR "
                    + "The household environment poses some concern of physical "
                    + "danger to the dependent/s.");
            ratingCN5.put(2, "The youth’s dependent is in some danger from one or more "
                    + "individuals within the household/vicinity, but there are people "
                    + "around to supervise access; "
                    + "OR "
                    + "The household environment poses some physical danger to the "
                    + "dependent/s.");
            ratingCN5.put(3, "The youth’s dependent is in immediate danger from one or more "
                    + "individuals within the household/vicinity; "
                    + "OR "
                    + "The household environment poses immediate and/or severe "
                    + "danger to the dependent/s.");

            cn5.setQuestionDescription(descCN5);
            cn5.setQuestionToConsider(quesToConsiderCN5);
            cn5.setRatingsDefinition(ratingCN5);

            quesCN.add(cn5);

            List<Long> ids10 = new ArrayList<>();
            ids10.add(questionsSessionBean.retrieveQuestionByCode("9.7").getQuestionId());

            SubModuleEntity mod10 = subModuleSessionBean.createNewSubModuleWithManyMainQues(CNmod, ids10);
            for (SubQuestionEntity questions : quesCN) {
                questionsSessionBean.createSubQuestionForSubModule(mod10.getSubModuleId(), questions);
            }

            // Create Admin
            AdminUserEntity admin = new AdminUserEntity("root@gmail.com", "Tan Wee Kek", new Date(), "Male", true, "password");
            long adminId = adminSessionBean.createNewAdminUser(admin);
            admin = new AdminUserEntity("ongbikjeun@gmail.com", "Ong Bik Jeun", new Date(), "Female", false, "password");
            adminId = adminSessionBean.createNewAdminUser(admin);
            admin = new AdminUserEntity("ooijunhao88@gmail.com", "Ooi Jun Hao", new Date(), "Male", false, "password");
            adminId = adminSessionBean.createNewAdminUser(admin);
            admin = new AdminUserEntity("giantcrabby@gmail.com", "Wu Huiren", new Date(), "Male", false, "password");
            adminId = adminSessionBean.createNewAdminUser(admin);
            admin = new AdminUserEntity("Wangziyue97@gmail.com", "Wang Ziyue", new Date(), "Male", false, "password");
            adminId = adminSessionBean.createNewAdminUser(admin);
            admin = new AdminUserEntity("reubenangwz@gmail.com", "Reuben Ang", new Date(), "Male", false, "password");
            adminId = adminSessionBean.createNewAdminUser(admin);
            admin = new AdminUserEntity("yuntiangu@gmail.com", "Gu Yuntian", new Date(), "Male", false, "password");
            adminId = adminSessionBean.createNewAdminUser(admin);

            // @Ziyue
            // Create all the organisations and associate them with their agency types here, the once done create a "master" assessor similar to below and associate any organisation with it
            OrganisationEntity organisation1 = new OrganisationEntity();
            organisation1.setName("Boys’ Town - Residential");
            organisation1.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisation1.getOrganisationTypes().add(AgencyTypeEnum.TGH);
            organisationSessionBean.createNewOrganisation(organisation1);

            OrganisationEntity organisation2 = new OrganisationEntity();
            organisation2.setName("Boys’ Town - Fostering Service");
            organisation2.getOrganisationTypes().add(AgencyTypeEnum.FA);
            organisationSessionBean.createNewOrganisation(organisation2);

            OrganisationEntity organisation3 = new OrganisationEntity();
            organisation3.setName("Chen Su Lan Methodist Children’s Home");
            organisation3.getOrganisationTypes().add(AgencyTypeEnum.PSGC);
            organisation3.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation3);

            OrganisationEntity organisation4 = new OrganisationEntity();
            organisation4.setName("Child Protection Service - MSF");
            organisation4.getOrganisationTypes().add(AgencyTypeEnum.CPS);
            organisationSessionBean.createNewOrganisation(organisation4);

            OrganisationEntity organisation5 = new OrganisationEntity();
            organisation5.setName("Children’s Aid Society, Melrose Home");
            organisation5.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation5);

            OrganisationEntity organisation6 = new OrganisationEntity();
            organisation6.setName("Clinical and Forensic Psychology Service - MSF");
            organisation6.getOrganisationTypes().add(AgencyTypeEnum.CFPS);
            organisationSessionBean.createNewOrganisation(organisation6);

            OrganisationEntity organisation7 = new OrganisationEntity();
            organisation7.setName("Darul Ihsan Libanat");
            organisation7.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation7);

            OrganisationEntity organisation8 = new OrganisationEntity();
            organisation8.setName("Darul Ihsan Orphanage");
            organisation8.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation8);

            OrganisationEntity organisation9 = new OrganisationEntity();
            organisation9.setName("Dayspring Residential Treatment Centre");
            organisation9.getOrganisationTypes().add(AgencyTypeEnum.TGH);
            organisationSessionBean.createNewOrganisation(organisation9);

            OrganisationEntity organisation10 = new OrganisationEntity();
            organisation10.setName("Epworth Foster Care");
            organisation10.getOrganisationTypes().add(AgencyTypeEnum.FA);
            organisationSessionBean.createNewOrganisation(organisation10);

            OrganisationEntity organisation11 = new OrganisationEntity();
            organisation11.setName("Epworth HSH");
            organisation11.getOrganisationTypes().add(AgencyTypeEnum.TGH);
            organisationSessionBean.createNewOrganisation(organisation11);

            OrganisationEntity organisation12 = new OrganisationEntity();
            organisation12.setName("Fostering Service CIC - MSF");
            organisation12.getOrganisationTypes().add(AgencyTypeEnum.CIC);
            organisationSessionBean.createNewOrganisation(organisation12);

            OrganisationEntity organisation13 = new OrganisationEntity();
            organisation13.setName("Gladiolus Place");
            organisation13.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation13);

            OrganisationEntity organisation14 = new OrganisationEntity();
            organisation14.setName("Jamiyah Children’s Home");
            organisation14.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation14);

            OrganisationEntity organisation15 = new OrganisationEntity();
            organisation15.setName("Marymount Centre, Ahuva Good Shepherd");
            organisation15.getOrganisationTypes().add(AgencyTypeEnum.PSGC);
            organisationSessionBean.createNewOrganisation(organisation15);

            OrganisationEntity organisation16 = new OrganisationEntity();
            organisation16.setName("Muhammadiyah Welfare Home");
            organisation16.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation16);

            OrganisationEntity organisation17 = new OrganisationEntity();
            organisation17.setName("MWS Girls’ Residence");
            organisation17.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation17);

            OrganisationEntity organisation18 = new OrganisationEntity();
            organisation18.setName("Pertapis Centre for Women and Girls");
            organisation18.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation18);

            OrganisationEntity organisation19 = new OrganisationEntity();
            organisation19.setName("Pertapis Children’s Home");
            organisation19.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation19);

            OrganisationEntity organisation20 = new OrganisationEntity();
            organisation20.setName("PPIS Oasis");
            organisation20.getOrganisationTypes().add(AgencyTypeEnum.FA);
            organisationSessionBean.createNewOrganisation(organisation20);

            OrganisationEntity organisation21 = new OrganisationEntity();
            organisation21.setName("Ramakrishna Mission Boys’ Home");
            organisation21.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation21);

            OrganisationEntity organisation22 = new OrganisationEntity();
            organisation22.setName("Sunbeam Place");
            organisation22.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation22);

            OrganisationEntity organisation23 = new OrganisationEntity();
            organisation23.setName("The Salvation Army, Gracehaven");
            organisation23.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation23);

            OrganisationEntity organisation24 = new OrganisationEntity();
            organisation24.setName("The Salvation Army, Gracehaven – Fostering");
            organisation24.getOrganisationTypes().add(AgencyTypeEnum.FA);
            organisationSessionBean.createNewOrganisation(organisation24);

            OrganisationEntity organisation25 = new OrganisationEntity();
            organisation25.setName("The Salvation Army, Haven");
            organisation25.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation25);

            OrganisationEntity organisation26 = new OrganisationEntity();
            organisation26.setName("The TENT (Teenagers Experience New Truth)");
            organisation26.getOrganisationTypes().add(AgencyTypeEnum.VCH);
            organisationSessionBean.createNewOrganisation(organisation26);

            OrganisationEntity organisation27 = new OrganisationEntity();
            organisation27.setName("Youth Residential Service - MSF");
            organisation27.getOrganisationTypes().add(AgencyTypeEnum.YRS);
            organisationSessionBean.createNewOrganisation(organisation27);

            // Create Assessor
            //   AssessorEntity assessor = new AssessorEntity("Ministry of Social and Family Development", "rootassessor@gmail.com", "Root Assessor", false, "password");
            //Boys Town Residence
            AssessorEntity assessor = new AssessorEntity("assessor1@msf.gov.sg", "assessor1", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor2@msf.gov.sg", "assessor2", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor3@msf.gov.sg", "assessor3", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor4@msf.gov.sg", "assessor4", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor5@msf.gov.sg", "assessor5", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor6@msf.gov.sg", "assessor6", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor7@msf.gov.sg", "assessor7", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor8@msf.gov.sg", "assessor8", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor9@msf.gov.sg", "assessor9", false, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor10@msf.gov.sg", "assessor10", false, "password", organisation1);
            AssessorEntity supervisee = assessorSessionBean.retrieveUserById(1l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(2l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(3l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            //Boys Town Fostering
            assessor = new AssessorEntity("assessor11@msf.gov.sg", "assessor11", false, "password", organisation2);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor12@msf.gov.sg", "assessor12", false, "password", organisation2);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor13@msf.gov.sg", "assessor13", false, "password", organisation2);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor14@msf.gov.sg", "assessor14", false, "password", organisation2);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor15@msf.gov.sg", "assessor15", false, "password", organisation2);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor16@msf.gov.sg", "assessor16", false, "password", organisation2);
            supervisee = assessorSessionBean.retrieveUserById(14l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(15l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor17@msf.gov.sg", "assessor17", false, "password", organisation2);
            supervisee = assessorSessionBean.retrieveUserById(11l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(12l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(13l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            //chen su lan
            assessor = new AssessorEntity("assessor18@msf.gov.sg", "assessor18", false, "password", organisation3);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor19@msf.gov.sg", "assessor19", false, "password", organisation3);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor20@msf.gov.sg", "assessor20", false, "password", organisation3);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor21@msf.gov.sg", "assessor21", false, "password", organisation3);
            supervisee = assessorSessionBean.retrieveUserById(18l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(19l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            //cps msf
            assessor = new AssessorEntity("assessor22@msf.gov.sg", "assessor22", false, "password", organisation4);
            assessorSessionBean.createNewAssessor(assessor);

            //melrose
            assessor = new AssessorEntity("assessor23@msf.gov.sg", "assessor23", false, "password", organisation5);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor24@msf.gov.sg", "assessor24", false, "password", organisation5);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor25@msf.gov.sg", "assessor25", false, "password", organisation5);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor26@msf.gov.sg", "assessor26", false, "password", organisation5);
            supervisee = assessorSessionBean.retrieveUserById(22l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(22l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(23l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);
            

            //CFPS msf
            assessor = new AssessorEntity("assessor27@msf.gov.sg", "assessor27", false, "password", organisation6);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor28@msf.gov.sg", "assessor28", false, "password", organisation6);
            supervisee = assessorSessionBean.retrieveUserById(27l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            //darul libanat
            assessor = new AssessorEntity("assessor29@msf.gov.sg", "assessor29", false, "password", organisation7);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor30@msf.gov.sg", "assessor30", false, "password", organisation7);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor31@msf.gov.sg", "assessor31", false, "password", organisation7);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor32@msf.gov.sg", "assessor32", false, "password", organisation7);
            supervisee = assessorSessionBean.retrieveUserById(29l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(30l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(31l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            //darul orphanage
            assessor = new AssessorEntity("assessor33@msf.gov.sg", "assessor33", false, "password", organisation8);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor34@msf.gov.sg", "assessor34", false, "password", organisation8);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor35@msf.gov.sg", "assessor35", false, "password", organisation8);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor36@msf.gov.sg", "assessor36", false, "password", organisation8);
            supervisee = assessorSessionBean.retrieveUserById(33l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(34l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(35l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            //dayspring
            assessor = new AssessorEntity("assessor37@msf.gov.sg", "assessor37", false, "password", organisation9);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor38@msf.gov.sg", "assessor38", false, "password", organisation9);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor39@msf.gov.sg", "assessor39", false, "password", organisation9);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor40@msf.gov.sg", "assessor40", false, "password", organisation9);
            supervisee = assessorSessionBean.retrieveUserById(37l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(38l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            supervisee = assessorSessionBean.retrieveUserById(39l);
            supervisee.setSupervisor(assessor);
            assessor.getSupervisee().add(supervisee);
            assessorSessionBean.createNewAssessor(assessor);

            //epworth foster
            assessor = new AssessorEntity("assessor41@msf.gov.sg", "assessor41", false, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor42@msf.gov.sg", "assessor42", false, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor43@msf.gov.sg", "assessor43", false, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor44@msf.gov.sg", "assessor44", false, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor45@msf.gov.sg", "assessor45", false, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor46@msf.gov.sg", "assessor46", false, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor47@msf.gov.sg", "assessor47", false, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);

            //epworth hsh
            assessor = new AssessorEntity("assessor48@msf.gov.sg", "assessor48", false, "password", organisation11);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor49@msf.gov.sg", "assessor49", false, "password", organisation11);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor50@msf.gov.sg", "assessor50", false, "password", organisation11);
            assessorSessionBean.createNewAssessor(assessor);

            //cic msf
            assessor = new AssessorEntity("assessor51@msf.gov.sg", "assessor51", false, "password", organisation12);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor52@msf.gov.sg", "assessor52", false, "password", organisation12);
            assessorSessionBean.createNewAssessor(assessor);

            //gladiolus place
            assessor = new AssessorEntity("assessor53@msf.gov.sg", "assessor53", false, "password", organisation13);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor54@msf.gov.sg", "assessor54", false, "password", organisation13);
            assessorSessionBean.createNewAssessor(assessor);

            //jamiyah
            assessor = new AssessorEntity("assessor55@msf.gov.sg", "assessor55", false, "password", organisation14);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor56@msf.gov.sg", "assessor56", false, "password", organisation14);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor57@msf.gov.sg", "assessor57", false, "password", organisation14);
            assessorSessionBean.createNewAssessor(assessor);

            //marymount
            assessor = new AssessorEntity("assessor58@msf.gov.sg", "assessor58", false, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor59@msf.gov.sg", "assessor59", false, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor60@msf.gov.sg", "assessor60", false, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor61@msf.gov.sg", "assessor61", false, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor62@msf.gov.sg", "assessor62", false, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor63@msf.gov.sg", "assessor63", false, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor64@msf.gov.sg", "assessor64", false, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);

            //muhammadiyah
            assessor = new AssessorEntity("assessor65@msf.gov.sg", "assessor65", false, "password", organisation16);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor66@msf.gov.sg", "assessor66", false, "password", organisation16);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor67@msf.gov.sg", "assessor67", false, "password", organisation16);
            assessorSessionBean.createNewAssessor(assessor);

            //mws girls
            assessor = new AssessorEntity("assessor68@msf.gov.sg", "assessor68", false, "password", organisation17);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor69@msf.gov.sg", "assessor69", false, "password", organisation17);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor70@msf.gov.sg", "assessor70", false, "password", organisation17);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor71@msf.gov.sg", "assessor71", false, "password", organisation17);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor72@msf.gov.sg", "assessor72", false, "password", organisation17);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor73@msf.gov.sg", "assessor73", false, "password", organisation17);
            assessorSessionBean.createNewAssessor(assessor);

            //pertapis women and girls
            assessor = new AssessorEntity("assessor74@msf.gov.sg", "assessor74", false, "password", organisation18);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor75@msf.gov.sg", "assessor75", false, "password", organisation18);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor76@msf.gov.sg", "assessor76", false, "password", organisation18);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor77@msf.gov.sg", "assessor77", false, "password", organisation18);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor78@msf.gov.sg", "assessor78", false, "password", organisation18);
            assessorSessionBean.createNewAssessor(assessor);

            //pertapis home
            assessor = new AssessorEntity("assessor79@msf.gov.sg", "assessor79", false, "password", organisation19);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor80@msf.gov.sg", "assessor80", false, "password", organisation19);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor81@msf.gov.sg", "assessor81", false, "password", organisation19);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor82@msf.gov.sg", "assessor82", false, "password", organisation19);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor83@msf.gov.sg", "assessor83", false, "password", organisation19);
            assessorSessionBean.createNewAssessor(assessor);

            //ppis
            assessor = new AssessorEntity("assessor84@msf.gov.sg", "assessor84", false, "password", organisation20);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor85@msf.gov.sg", "assessor85", false, "password", organisation20);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor86@msf.gov.sg", "assessor86", false, "password", organisation20);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor87@msf.gov.sg", "assessor87", false, "password", organisation20);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor88@msf.gov.sg", "assessor88", false, "password", organisation20);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor89@msf.gov.sg", "assessor89", false, "password", organisation20);
            assessorSessionBean.createNewAssessor(assessor);

            //ramakrishna
            assessor = new AssessorEntity("assessor90@msf.gov.sg", "assessor90", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor91@msf.gov.sg", "assessor91", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor92@msf.gov.sg", "assessor92", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor93@msf.gov.sg", "assessor93", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor94@msf.gov.sg", "assessor94", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor95@msf.gov.sg", "assessor95", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor96@msf.gov.sg", "assessor96", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor97@msf.gov.sg", "assessor97", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor98@msf.gov.sg", "assessor98", false, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);

            //sunbeam
            assessor = new AssessorEntity("assessor99@msf.gov.sg", "assessor99", false, "password", organisation22);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor100@msf.gov.sg", "assessor100", false, "password", organisation22);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor101@msf.gov.sg", "assessor101", false, "password", organisation22);
            assessorSessionBean.createNewAssessor(assessor);

            //salvation gracehaven
            assessor = new AssessorEntity("assessor102@msf.gov.sg", "assessor102", false, "password", organisation23);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor103@msf.gov.sg", "assessor103", false, "password", organisation23);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor104@msf.gov.sg", "assessor104", false, "password", organisation23);
            assessorSessionBean.createNewAssessor(assessor);

            //salvation gracehaven fostering
            assessor = new AssessorEntity("assessor105@msf.gov.sg", "assessor105", false, "password", organisation24);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor106@msf.gov.sg", "assessor106", false, "password", organisation24);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor107@msf.gov.sg", "assessor107", false, "password", organisation24);
            assessorSessionBean.createNewAssessor(assessor);

            //salvation haven
            assessor = new AssessorEntity("assessor108@msf.gov.sg", "assessor108", false, "password", organisation25);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor109@msf.gov.sg", "assessor109", false, "password", organisation25);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor110@msf.gov.sg", "assessor110", false, "password", organisation25);
            assessorSessionBean.createNewAssessor(assessor);

            //tent
            assessor = new AssessorEntity("assessor111@msf.gov.sg", "assessor111", false, "password", organisation26);
            assessorSessionBean.createNewAssessor(assessor);

            //YRS MSF
            assessor = new AssessorEntity("assessor112@msf.gov.sg", "assessor112", false, "password", organisation27);
            assessorSessionBean.createNewAssessor(assessor);

            assessor = new AssessorEntity("assessor113@msf.gov.sg", "assessor113", false, "password", organisation27);
            assessorSessionBean.createNewAssessor(assessor);
            // long assessorId = assessorSessionBean.createNewAssessor(assessor);

            //Create Root Assessors
            assessor = new AssessorEntity("assessorRoot1@msf.gov.sg", "root1", true, "password", organisation1);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot2@msf.gov.sg", "root2", true, "password", organisation2);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot3@msf.gov.sg", "root3", true, "password", organisation3);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot4@msf.gov.sg", "root4", true, "password", organisation4);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot5@msf.gov.sg", "root5", true, "password", organisation5);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot6@msf.gov.sg", "root6", true, "password", organisation6);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot7@msf.gov.sg", "root7", true, "password", organisation7);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot8@msf.gov.sg", "root8", true, "password", organisation8);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot9@msf.gov.sg", "root9", true, "password", organisation9);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot10@msf.gov.sg", "root10", true, "password", organisation10);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot11@msf.gov.sg", "root11", true, "password", organisation11);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot12@msf.gov.sg", "root12", true, "password", organisation12);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot13@msf.gov.sg", "root13", true, "password", organisation13);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot14@msf.gov.sg", "root14", true, "password", organisation14);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot15@msf.gov.sg", "root15", true, "password", organisation15);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot16@msf.gov.sg", "root16", true, "password", organisation16);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot17@msf.gov.sg", "root17", true, "password", organisation17);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot18@msf.gov.sg", "root18", true, "password", organisation18);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot19@msf.gov.sg", "root19", true, "password", organisation19);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot20@msf.gov.sg", "root20", true, "password", organisation20);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot21@msf.gov.sg", "root21", true, "password", organisation21);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot22@msf.gov.sg", "root22", true, "password", organisation22);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot23@msf.gov.sg", "root23", true, "password", organisation23);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot24@msf.gov.sg", "root24", true, "password", organisation24);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot25@msf.gov.sg", "root25", true, "password", organisation25);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot26@msf.gov.sg", "root26", true, "password", organisation26);
            assessorSessionBean.createNewAssessor(assessor);
            assessor = new AssessorEntity("assessorRoot27@msf.gov.sg", "root27", true, "password", organisation27);
            assessorSessionBean.createNewAssessor(assessor);

            System.out.println("********** Finish Data Initialization **********");
        } catch (UnknownPersistenceException | AdminUserExistsException | InputDataValidationException | AgeGroupExistsException | DomainNotFoundException | AgeGroupNotFoundException | MainQuestionExistsException | SubModuleNotFoundException | SubQuestionExistsException | QuestionNotFoundException | SubModuleExistsException | QuestionTypeInaccurateException | DomainExistsException | OrganisationExistsException | AssessorExistsException | UserNotFoundException ex) {
            Logger.getLogger(DataInitSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

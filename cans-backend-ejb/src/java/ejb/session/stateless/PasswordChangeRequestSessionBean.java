/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminUserEntity;
import entity.AssessorEntity;
import entity.PasswordChangeRequestEntity;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.PasswordChangeRequestNotFoundException;
import util.exception.UserNotFoundException;
import util.security.CryptographicHelper;

/**
 *
 * @author Ooi Jun Hao
 * @author Ong Bik Jeun
 * @author Wang Ziyue
 */
@Stateless
public class PasswordChangeRequestSessionBean implements PasswordChangeRequestSessionBeanLocal {

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    @Resource(mappedName = "jms/queueEmail")
    private Queue queueEmail;
    @Resource(mappedName = "jms/queueEmailFactory")
    private ConnectionFactory queueEmailFactory;

    @EJB
    private AssessorSessionBeanLocal assessorSessionBean;

    @EJB
    private AdminUserSessionBeanLocal adminUserSessionBean;

    private final String salt = CryptographicHelper.getInstance().generateRandomString(32);
    private final int EXPIRY_MINS = 20;

    public PasswordChangeRequestSessionBean() {
    }

    @Override
    public boolean createPasswordChangeRequestAdminSystem(String email) {
        try {
            PasswordChangeRequestEntity token = new PasswordChangeRequestEntity();
            token.setRequestDateTime(new Date());
            token.setRequestToken(UUID.randomUUID().toString());

            AdminUserEntity user = adminUserSessionBean.retrieveUserByEmail(email);
            token.setAdmin(user);

            em.persist(token);
            em.flush();

            sendJMSMessageToQueueEmail(token, email, "oldAdmin"); 
            return true;
        } catch (JMSException | UserNotFoundException ex) {
            System.err.println("An error occured while trying to send email: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean createPasswordChangeRequestAssessorSystem(String email) {
        try {
            PasswordChangeRequestEntity token = new PasswordChangeRequestEntity();
            token.setRequestDateTime(new Date());
            token.setRequestToken(UUID.randomUUID().toString());

            AssessorEntity user = assessorSessionBean.retrieveUserByEmail(email);
            token.setAssessor(user);

            em.persist(token);
            em.flush();

            sendJMSMessageToQueueEmail(token, email, "oldAssessor"); 
            return true;
        } catch (JMSException | UserNotFoundException ex) {
            System.err.println("An error occured while trying to send email: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean newAssessorAccountPasswordReset(String email) {
        try {
            PasswordChangeRequestEntity token = new PasswordChangeRequestEntity();
            token.setRequestDateTime(new Date());
            token.setRequestToken(UUID.randomUUID().toString());
            token.setNewAccount(true);
            
            AssessorEntity user = assessorSessionBean.retrieveUserByEmail(email);
            token.setAssessor(user);

            em.persist(token);
            em.flush();

            sendJMSMessageToQueueEmail(token, email, "newAssessor"); 
            return true;
        } catch (JMSException | UserNotFoundException ex) {
            System.err.println("An error occured while trying to send email: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean newAdminAccountPasswordReset(String email) {
        try {
            PasswordChangeRequestEntity token = new PasswordChangeRequestEntity();
            token.setRequestDateTime(new Date());
            token.setRequestToken(UUID.randomUUID().toString());
            token.setNewAccount(true);

            AdminUserEntity user = adminUserSessionBean.retrieveUserByEmail(email);
            token.setAdmin(user);

            em.persist(token);
            em.flush();

            sendJMSMessageToQueueEmail(token, email, "newAdmin"); 
            return true;
        } catch (JMSException | UserNotFoundException ex) {
            System.err.println("An error occured while trying to send email: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void updatePasswordAdminSystem(String passwordChangeRequestId, String newPassword) throws PasswordChangeRequestNotFoundException {
        Query query = em.createQuery("SELECT a FROM PasswordChangeRequestEntity a WHERE a.requestToken=:id");
        query.setParameter("id", passwordChangeRequestId);
        List<PasswordChangeRequestEntity> requests = query.getResultList();

        if (requests.isEmpty()) {
            throw new PasswordChangeRequestNotFoundException();
        }

        // doing this instead of getting single result because there might be the off chance
        // that two reqeusts generate the same UUID
        PasswordChangeRequestEntity request = requests.get(0);
        if (request.getAdmin() == null) {
            throw new PasswordChangeRequestNotFoundException();
        }

        AdminUserEntity admin = request.getAdmin();
        admin.setPassword(newPassword);
        em.remove(request);
        em.flush();
    }

    @Override
    public void updatePasswordAssessorSystem(String passwordChangeRequestId, String newPassword) throws PasswordChangeRequestNotFoundException {
        Query query = em.createQuery("SELECT a FROM PasswordChangeRequestEntity a WHERE a.requestToken=:id");
        query.setParameter("id", passwordChangeRequestId);
        List<PasswordChangeRequestEntity> requests = query.getResultList();

        if (requests.isEmpty()) {
            throw new PasswordChangeRequestNotFoundException();
        }

        // doing this instead of getting single result because there might be the off chance
        // that two reqeusts generate the same UUID
        PasswordChangeRequestEntity request = requests.get(0);
        if (request.getAssessor() == null) {
            throw new PasswordChangeRequestNotFoundException();
        }

        AssessorEntity admin = request.getAssessor();
        admin.setPassword(newPassword);
        em.remove(request);
        em.flush();
    }

    @Schedule(hour = "*", minute = "*", info = "loginTokenCleaner")
    public void automaticPasswordChangeRequestCleaner() {
        Calendar currentDateTime = Calendar.getInstance();
        System.out.println("********** Starting Password Change Request Cleaning for " + currentDateTime.getTime().toString() + " **********");
        Query query = em.createQuery("SELECT a FROM PasswordChangeRequestEntity a");
        List<PasswordChangeRequestEntity> requests = query.getResultList();
        for (PasswordChangeRequestEntity request : requests) {
            if (request.isNewAccount()) {
                continue;
            }
            Calendar c = Calendar.getInstance();
            c.setTime(request.getRequestDateTime());
            c.add(Calendar.MINUTE, EXPIRY_MINS);
            if (c.before(currentDateTime)) {
                System.out.println("Removing password change request " + request.getRequestToken() + " that has expired");
                em.remove(request);
                em.flush();
            }
        }
        System.out.println("********** Finish Password Change Request Cleaning for " + currentDateTime.getTime().toString() + " **********");
    }

    private void sendJMSMessageToQueueEmail(PasswordChangeRequestEntity token, String email, String type) throws JMSException {
        Connection conn = null;
        Session s = null;
        try {
            conn = queueEmailFactory.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            MessageProducer mp = s.createProducer(queueEmail);

            MapMessage mapMessage = s.createMapMessage();
            mapMessage.setString("token", token.getRequestToken());
            mapMessage.setString("toEmailAddress", email);
            mapMessage.setString("type", type);

            mp.send(mapMessage);

        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}

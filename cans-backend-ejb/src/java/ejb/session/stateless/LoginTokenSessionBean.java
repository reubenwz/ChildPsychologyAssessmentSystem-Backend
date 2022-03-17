/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminUserEntity;
import entity.AssessorEntity;
import entity.LoginTokenEntity;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.LoginTokenNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Stateless
public class LoginTokenSessionBean implements LoginTokenSessionBeanLocal {

    @EJB
    private AssessorSessionBeanLocal assessorSessionBean;

    @EJB
    private AdminUserSessionBeanLocal adminUserSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final int DELAY_MINS = 20;

    @Override
    public LoginTokenEntity createNewLoginTokenForAdminSystem(long userId) throws UserNotFoundException, UnknownPersistenceException {

        AdminUserEntity admin = adminUserSessionBean.retrieveUserById(userId);

        // Calculate expiry date time
        Calendar c = Calendar.getInstance();
        Date currentDateTime = c.getTime();
        c.add(Calendar.MINUTE, DELAY_MINS);
        Date expiryDateTime = c.getTime();

        // Instantiate logintoken
        LoginTokenEntity loginToken = new LoginTokenEntity(currentDateTime, expiryDateTime, UUID.randomUUID().toString());
        loginToken.setAdmin(admin);

        try {
            em.persist(loginToken);
            em.flush();
            return loginToken;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new UnknownPersistenceException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public LoginTokenEntity createNewLoginTokenForAssessorSystem(long userId) throws UserNotFoundException, UnknownPersistenceException {
        AssessorEntity assessor = assessorSessionBean.retrieveUserById(userId);
        // Calculate expiry date time
        Calendar c = Calendar.getInstance();
        Date currentDateTime = c.getTime();
        c.add(Calendar.MINUTE, DELAY_MINS);
        Date expiryDateTime = c.getTime();

        LoginTokenEntity loginToken = new LoginTokenEntity(currentDateTime, expiryDateTime, UUID.randomUUID().toString());
        loginToken.setAssessor(assessor);

        try {
            em.persist(loginToken);
            em.flush();
            return loginToken;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new UnknownPersistenceException();
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public AssessorEntity validateLoginTokenAssessorSystem(String loginTokenId) throws LoginTokenNotFoundException {
        Query query = em.createQuery("SELECT l FROM LoginTokenEntity l WHERE l.tokenId =:id");
        query.setParameter("id", loginTokenId);
        try {
            LoginTokenEntity token = (LoginTokenEntity) query.getSingleResult();
            if (token.getAssessor() == null) {
                throw new LoginTokenNotFoundException();
            }
            if (token.getExpiry().before(new Date())) {
                em.remove(token);
                em.flush();
                throw new LoginTokenNotFoundException();
            } else {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MINUTE, DELAY_MINS);
                Date expiryDateTime = c.getTime();
                token.setExpiry(expiryDateTime);
                em.flush();
                return token.getAssessor();
            }
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new LoginTokenNotFoundException();
        }
    }

    @Override
    public AdminUserEntity validateLoginTokenAdminSystem(String loginTokenId) throws LoginTokenNotFoundException {
        Query query = em.createQuery("SELECT a FROM LoginTokenEntity a WHERE a.tokenId=:id");
        query.setParameter("id", loginTokenId);
        try {
            LoginTokenEntity token = (LoginTokenEntity) query.getSingleResult();
            if (token.getAdmin() == null) {
                throw new LoginTokenNotFoundException();
            }
            if (token.getExpiry().before(new Date())) {
                em.remove(token);
                em.flush();
                throw new LoginTokenNotFoundException();
            } else {
                // refresh the expiry date time
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MINUTE, DELAY_MINS);
                Date expiryDateTime = c.getTime();
                token.setExpiry(expiryDateTime);
                em.flush();
                return token.getAdmin();
            }
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new LoginTokenNotFoundException();
        }
    }

    @Override
    public void removeLoginToken(String loginTokenId) throws LoginTokenNotFoundException {
        Query query = em.createQuery("SELECT a FROM LoginTokenEntity a WHERE a.tokenId=:id");
        query.setParameter("id", loginTokenId);
        try {
            LoginTokenEntity token = (LoginTokenEntity) query.getSingleResult();
            em.remove(token);
            em.flush();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new LoginTokenNotFoundException();
        }
    }

    @Schedule(hour = "*", minute = "*", info = "loginTokenCleaner")
    public void automaticTokenCleaner() {
        Date currentDateTime = new Date();
        System.out.println("********** Starting log In Token Cleaning for " + currentDateTime.toString() + " **********");
        Query query = em.createQuery("SELECT a FROM LoginTokenEntity a");
        List<LoginTokenEntity> loginTokens = query.getResultList();
        for (LoginTokenEntity token : loginTokens) {
            if (token.getExpiry().before(currentDateTime)) {
                System.out.println("Removing token " + token.getTokenId() + " that has expired on " + token.getExpiry().toString());
                em.remove(token);
                em.flush();
            }
        }
        System.out.println("********** Finish log In Token Cleaning for " + currentDateTime.toString() + " **********");
    }

}

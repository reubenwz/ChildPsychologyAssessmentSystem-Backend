/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminUserEntity;
import entity.LoginTokenEntity;
import entity.PasswordChangeRequestEntity;
import java.util.Date;
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
import util.exception.AdminUserExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import util.security.CryptographicHelper;

/**
 *
 * @author Ong Bik Jeun
 * @author Wang Ziyue
 * @author Ooi Jun Hao
 */
@Stateless
public class AdminUserSessionBean implements AdminUserSessionBeanLocal {

    @EJB
    private PasswordChangeRequestSessionBeanLocal passwordChangeRequestSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AdminUserSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public AdminUserEntity retrieveUserById(Long id) throws UserNotFoundException {
        AdminUserEntity user = em.find(AdminUserEntity.class, id);
        if (user != null && user.isActive()) {
            return user;
        } else {
            throw new UserNotFoundException("User with ID: " + id + " does not exist!");
        }
    }

    @Override
    public AdminUserEntity retrieveUserByEmail(String email) throws UserNotFoundException {
        Query query = em.createQuery("SELECT a FROM AdminUserEntity a WHERE a.email = :email and a.active=true");
        query.setParameter("email", email);

        try {
            return (AdminUserEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new UserNotFoundException("User email " + email + " does not exist!");
        }
    }

    @Override
    public long createNewAdminUser(AdminUserEntity admin) throws UnknownPersistenceException, AdminUserExistsException, InputDataValidationException {
        Set<ConstraintViolation<AdminUserEntity>> constraintViolations = validator.validate(admin);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(admin);
                em.flush();
                return admin.getAdminId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AdminUserExistsException();
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
    public AdminUserEntity createNewAdminUserFromSystem(AdminUserEntity admin) throws UnknownPersistenceException, AdminUserExistsException, InputDataValidationException {
        Set<ConstraintViolation<AdminUserEntity>> constraintViolations = validator.validate(admin);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(admin);
                em.flush();
                passwordChangeRequestSessionBean.newAdminAccountPasswordReset(admin.getEmail());
                return admin;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AdminUserExistsException();
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
    public AdminUserEntity adminUserLogin(String email, String password) throws InvalidLoginCredentialException {
        try {
            AdminUserEntity admin = retrieveUserByEmail(email);
            if (!admin.isActive()) {
                throw new InvalidLoginCredentialException();
            }
            String passHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + admin.getSalt()));
            if (admin.getPassword().equals(passHash)) {
                return admin;
            } else {
                throw new InvalidLoginCredentialException();
            }
        } catch (UserNotFoundException ex) {
            throw new InvalidLoginCredentialException();
        }
    }

    @Override
    public void updatePassword(long adminId, String oldPassword, String newPassword) throws UserNotFoundException, InvalidLoginCredentialException {

        AdminUserEntity admin = this.retrieveUserById(adminId);
        String oldPasswordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPassword + admin.getSalt()));
        if (admin.getPassword().equals(oldPasswordHash)) {
            admin.setSalt(CryptographicHelper.getInstance().generateRandomString(32));
            admin.setPassword(newPassword);
        } else {
            throw new InvalidLoginCredentialException("Old password is incorrect");
        }

    }
    
    @Override
    public void updateDetails(long adminId, String gender, Date dob, String name) {
        AdminUserEntity admin = em.find(AdminUserEntity.class, adminId);
        if (gender!=null)
            admin.setDob(dob);
        if (dob!= null)
            admin.setGender(gender);
        if (name!= null)
            admin.setName(name);
        em.flush();
    }
    
    @Override
    public List<AdminUserEntity> retrieveAllAdmins() {
        Query query = em.createQuery("SELECT a FROM AdminUserEntity a WHERE a.active=true");
        return query.getResultList();
    }
    
    @Override
    public void deleteAdminUser(long adminUserId) throws UserNotFoundException {
        AdminUserEntity admin =  this.retrieveUserById(adminUserId);
        if (admin.getDoc().isEmpty()) {
            // remove any existing passwordchangerequesttokens and logintokens            
            Query query = em.createQuery("SELECT p FROM PasswordChangeRequestEntity p WHERE p.admin.adminId=:id");
            query.setParameter("id", adminUserId);
            List<PasswordChangeRequestEntity> req = query.getResultList();
            for (PasswordChangeRequestEntity r: req) {
                em.remove(r);
            }
            Query query2 = em.createQuery("SELECT l FROM LoginTokenEntity l WHERE l.admin.adminId=:id");
            query2.setParameter("id", adminUserId);
            List<LoginTokenEntity> log = query2.getResultList();
            for (LoginTokenEntity l: log) {
                em.remove(l);
            }
            em.remove(admin);
            em.flush();
        } else {
            admin.setActive(false);
            admin.setEmail(admin.getSalt() + "@removed.com.sg"); // set a dummy email
            em.flush();
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AdminUserEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    public void persist(Object object) {
        em.persist(object);
    }

}

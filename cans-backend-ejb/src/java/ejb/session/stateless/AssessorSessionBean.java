/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessorEntity;
import entity.LoginTokenEntity;
import entity.OrganisationEntity;
import entity.PasswordChangeRequestEntity;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
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
import util.exception.AssessorDeletionError;
import util.exception.AssessorExistsException;
import util.exception.AssociationException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OrganisationNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;
import util.security.CryptographicHelper;

/**
 * @author Ong Bik Jeun
 * @author Wang Ziyue
 * @author Ooi Jun Hao
 */
@Stateless
public class AssessorSessionBean implements AssessorSessionBeanLocal {

    @EJB
    private PasswordChangeRequestSessionBeanLocal passwordChangeRequestSessionBean;
    @EJB
    private OrganisationSessionBeanLocal organisationSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    @Resource(mappedName = "jms/queueEmail")
    private Queue queueEmail;
    @Resource(mappedName = "jms/queueEmailFactory")
    private ConnectionFactory queueEmailFactory;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AssessorSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.getValidator();
    }

    // only to be used in data Init, will overload later on with proper arguments
    @Override
    public Long createNewAssessor(AssessorEntity ass) throws UnknownPersistenceException, AssessorExistsException, InputDataValidationException {
        Set<ConstraintViolation<AssessorEntity>> constraintViolations = validator.validate(ass);
        if (constraintViolations.isEmpty()) {
            try {
                ass.getOrganisation().getAssessors().add(ass);
                em.persist(ass);
                em.flush();

                return ass.getAssessorId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AssessorExistsException();
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
    public AssessorEntity createNewAssessor(AssessorEntity ass, Long orgId) throws OrganisationNotFoundException, AssessorExistsException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<AssessorEntity>> constraintViolations = validator.validate(ass);
        if (constraintViolations.isEmpty()) {
            try {
                try {
                    OrganisationEntity org = organisationSessionBean.retrieveOrgById(orgId);
                    em.persist(ass);
                    ass.setOrganisation(org);
                    org.getAssessors().add(ass);

                    em.flush();
                    passwordChangeRequestSessionBean.newAssessorAccountPasswordReset(ass.getEmail());
                    return ass;
                } catch (OrganisationNotFoundException ex) {
                    throw new OrganisationNotFoundException(ex.getMessage());
                }

            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AssessorExistsException();
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
    public AssessorEntity retrieveUserById(Long id) throws UserNotFoundException {
        AssessorEntity user = em.find(AssessorEntity.class, id);
        if (user != null) {
            return user;
        } else {
            throw new UserNotFoundException("User with ID: " + id + " does not exist!");
        }
    }

    @Override
    public AssessorEntity retrieveUserByEmail(String email) throws UserNotFoundException {
        Query query = em.createQuery("SELECT a FROM AssessorEntity a WHERE a.email = :email");
        query.setParameter("email", email);

        try {
            return (AssessorEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new UserNotFoundException("User email " + email + " does not exist!");
        }
    }

    @Override
    public List<AssessorEntity> retrieveAllAssessorsByOrganisation(long orgId) {
        Query query = em.createQuery("SELECT a FROM AssessorEntity a WHERE a.organisation.organisationId = :orgId");
        query.setParameter("orgId", orgId);

        return query.getResultList();
    }

    @Override
    public AssessorEntity assessorLogin(String email, String password) throws InvalidLoginCredentialException {
        try {
            AssessorEntity assessor = retrieveUserByEmail(email);
            String passHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + assessor.getSalt()));

            if (assessor.getPassword().equals(passHash)) {
                return assessor;
            } else {
                throw new InvalidLoginCredentialException();
            }
        } catch (UserNotFoundException ex) {
            throw new InvalidLoginCredentialException();
        }

    }

    @Override
    public void updatePassword(long assessorId, String oldPassword, String newPassword) throws InvalidLoginCredentialException, UserNotFoundException {
        try {
            AssessorEntity assessor = this.retrieveUserById(assessorId);
            String oldPasswordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(oldPassword + assessor.getSalt()));

            if (assessor.getPassword().equals(oldPasswordHash)) {
                assessor.setSalt(CryptographicHelper.getInstance().generateRandomString(32));
                assessor.setPassword(newPassword);
            } else {
                throw new InvalidLoginCredentialException("Old Password is Incorrect");
            }
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    // for use in assessor system
    @Override
    public void updateDetails(long assessorId, String name) throws UserNotFoundException {

        AssessorEntity assessor = this.retrieveUserById(assessorId);
        if (name != null) {
            assessor.setName(name);
        }
        em.flush();
    }

    // for use in admin system
    @Override
    public void updateDetails(long assessorId, String name, String email, long supervisor_id) throws UserNotFoundException {
        AssessorEntity assessor = this.retrieveUserById(assessorId);
        AssessorEntity supervisor = this.retrieveUserById(supervisor_id);
        if (name != null) {
            assessor.setName(name);
        }
        if (email != null) {
            assessor.setEmail(email);
        }

        if (assessor.getSupervisor() != null) {
            assessor.getSupervisor().getSupervisee().remove(assessor);
        }
        assessor.setSupervisor(supervisor);
        supervisor.getSupervisee().add(assessor);

        em.flush();
    }

    @Override
    public void deleteAssessor(long assessorId) throws AssessorDeletionError, UserNotFoundException {
        try {
            AssessorEntity ass = retrieveUserById(assessorId);
            if (ass.isRoot()) {
                throw new AssessorDeletionError("Assessor is an organisation admin. Deletion not advised");
            }
            if (ass.getAssessments().isEmpty() && ass.getClients().isEmpty()) {
                //remove any existing passwordchangerequests or logintokens
                Query query = em.createQuery("SELECT p FROM PasswordChangeRequestEntity p WHERE p.assessor.assessorId=:id");
                query.setParameter("id", ass.getAssessorId());
                List<PasswordChangeRequestEntity> req = query.getResultList();
                for (PasswordChangeRequestEntity r : req) {
                    em.remove(r);
                }
                Query query2 = em.createQuery("SELECT l FROM LoginTokenEntity l WHERE l.assessor.assessorId=:id");
                query2.setParameter("id", ass.getAssessorId());
                List<LoginTokenEntity> log = query2.getResultList();
                for (LoginTokenEntity l : log) {
                    em.remove(l);
                }
                ass.getOrganisation().getAssessors().remove(ass);
                if (ass.getSupervisor() != null) {
                    ass.getSupervisor().getSupervisee().remove(ass);
                }
                if (ass.getSupervisee() != null) {
                    for (AssessorEntity sup : ass.getSupervisee()) {
                        sup.setSupervisor(null);
                    }
                }
                em.remove(ass);
                em.flush();
            } else {
                throw new AssessorDeletionError("Assessor has assessment and client records. Deletion not advised");
            }
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    @Override
    public Boolean updateActiveStatus(long assessorId) throws UserNotFoundException, AssessorDeletionError {
        try {
            AssessorEntity ass = retrieveUserById(assessorId);
            if (ass.isRoot()) {
                throw new AssessorDeletionError("Deactivation is not advised for organisation admin accounts.");
            }
            if (ass.isActive()) {
                if (!ass.getClients().isEmpty()) {
                    throw new AssessorDeletionError("Deactivation is not advised. Please reassign existing clients before deactivating account.");
                }

                ass.setActive(false);

                if (ass.getSupervisor() != null) {
                    ass.getSupervisor().getSupervisee().remove(ass);
                    ass.setSupervisor(null);
                }
                if (ass.getSupervisee() != null) {

                    for (AssessorEntity sup : ass.getSupervisee()) {
                        sup.setSupervisor(null);
                    }
                    ass.getSupervisee().clear();
                }

            } else {
                ass.setActive(true);
            }

            return ass.isActive();
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (AssessorDeletionError ex) {
            throw new AssessorDeletionError(ex.getMessage());
        }

    }

    @Override
    public void assignSupervisorSupervisee(long supervisorId, long superviseeId) throws UserNotFoundException, AssociationException {
        AssessorEntity supervisor = this.retrieveUserById(supervisorId);
        AssessorEntity supervisee = this.retrieveUserById(superviseeId);

        if (supervisor.getOrganisation().equals(supervisee.getOrganisation())) {
            // dissassocate old ones first
            if (supervisee.getSupervisor() != null) {
                supervisee.getSupervisor().getSupervisee().remove(supervisee);//equals() comparison will work here since ID already set
            }
            // associate new
            supervisee.setSupervisor(supervisor);
            supervisor.getSupervisee().add(supervisee);
            em.flush();
        } else {
            throw new AssociationException("Both are from different organisaion!");
        }
    }

    @Override
    public void removeSupervisorSupervisee(long supervisorId, long superviseeId) throws UserNotFoundException, AssociationException {
        AssessorEntity supervisor = this.retrieveUserById(supervisorId);
        AssessorEntity supervisee = this.retrieveUserById(superviseeId);
        if (!supervisee.getSupervisor().getAssessorId().equals(supervisorId)) {
            throw new AssociationException("Supervisor and supervisee given are not accurate");
        }
        supervisor.getSupervisee().remove(supervisee);
        supervisee.setSupervisor(null);
        em.flush();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AssessorEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

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
import entity.LoginTokenEntity;
import entity.OrganisationEntity;
import entity.PasswordChangeRequestEntity;
import entity.ResponseEntity;
import java.util.ArrayList;
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
import util.enumeration.AgencyTypeEnum;
import util.exception.AssessorExistsException;
import util.exception.InputDataValidationException;
import util.exception.OrganisationExistsException;
import util.exception.OrganisationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class OrganisationSessionBean implements OrganisationSessionBeanLocal {

    @EJB
    private PasswordChangeRequestSessionBeanLocal passwordChangeRequestSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public OrganisationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public OrganisationEntity createNewOrganisation(OrganisationEntity organisation) throws UnknownPersistenceException, OrganisationExistsException, InputDataValidationException {
        Set<ConstraintViolation<OrganisationEntity>> constraintViolations = validator.validate(organisation);
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(organisation);
                em.flush();

                return organisation;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new OrganisationExistsException();
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
    public OrganisationEntity createNewOrganisationWithOrgAdmin(OrganisationEntity organisation, AssessorEntity ass) throws AssessorExistsException, UnknownPersistenceException, InputDataValidationException, OrganisationExistsException {
        Set<ConstraintViolation<AssessorEntity>> constraintViolations2 = validator.validate(ass);
        Set<ConstraintViolation<OrganisationEntity>> constraintViolations = validator.validate(organisation);
        if (constraintViolations2.isEmpty()) {
            if (constraintViolations.isEmpty()) {
                try {
                    em.persist(organisation);
                    em.flush();
                } catch (PersistenceException ex) {
                    if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                        if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                            throw new OrganisationExistsException();
                        } else {
                            throw new UnknownPersistenceException(ex.getMessage());
                        }
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                try {
                    em.persist(ass);
                    ass.setOrganisation(organisation);
                    organisation.getAssessors().add(ass);
                    em.flush();
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
                // let the fella reset his email
                passwordChangeRequestSessionBean.newAssessorAccountPasswordReset(ass.getEmail());
                return organisation;
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage2(constraintViolations2));
        }
    }

    @Override
    public List<OrganisationEntity> retrieveAllOrganisation() {
        Query query = em.createQuery("SELECT o FROM OrganisationEntity o");
        return query.getResultList();
    }

    @Override
    public OrganisationEntity retrieveOrgById(Long id) throws OrganisationNotFoundException {
        OrganisationEntity org = em.find(OrganisationEntity.class, id);
        if (org == null) {
            throw new OrganisationNotFoundException("OrganisationNotFoundException: Organisation with " + id + " does not exist!");

        }
        return org;
    }

    @Override
    public List<String> retrieveAllOrganisationNames() {
        List<OrganisationEntity> organisations = retrieveAllOrganisation();
        List<String> names = new ArrayList<>();
        for (OrganisationEntity o : organisations) {
            names.add(o.getName());
        }
        return names;
    }

    @Override
    public List<String> retrieveAllAssessorEmailsByOrganisationName(String name) throws OrganisationNotFoundException {
        OrganisationEntity organisation = retrieveOrganisationByName(name);
        List<String> emails = new ArrayList<>();
        for (AssessorEntity a : organisation.getAssessors()) {
            emails.add(a.getEmail());
        }
        return emails;
    }

    @Override
    public List<ClientEntity> retrieveAllClientsInOrganisation(Long orgId) {
        Query query = em.createQuery("SELECT c FROM ClientEntity c WHERE c.assessor.organisation.organisationId =:id");
        query.setParameter("id", orgId);

        return query.getResultList();
    }

    @Override
    public OrganisationEntity retrieveOrganisationByName(String name) throws OrganisationNotFoundException {
        Query query = em.createQuery("SELECT o FROM OrganisationEntity o WHERE o.name = :name");
        query.setParameter("name", name);

        try {
            return (OrganisationEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new OrganisationNotFoundException("Organisation: " + name + " does not exist!");
        }
    }

    // only for use by RWS
    @Override
    public List<OrganisationEntity> retrieveAllOrganisationByType(String type) {
        type = type.toUpperCase();
        String[] parts = type.split(" ");
        String agency = parts[0];
        AgencyTypeEnum agencyType = AgencyTypeEnum.valueOf(agency);

        List<OrganisationEntity> results = this.retrieveAllOrganisation();
        results.removeIf(org -> !org.getOrganisationTypes().contains(agencyType));

        return results;
    }

    @Override
    public void removeOrganisation(long orgId) throws OrganisationNotFoundException {
        OrganisationEntity org = this.retrieveOrgById(orgId);

        for (AssessorEntity ass : org.getAssessors()) {
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
            for (ClientEntity client : ass.getClients()) {
                em.remove(client);
                for (AssessmentEntity asse : client.getAssessment()) {
                    for (ResponseEntity res : asse.getResponse()) {
                        em.remove(res);
                    }
                    em.remove(asse);
                }
                for (CaretakerEntity caretaker : client.getCaretakers()) {
                    em.remove(caretaker);
                    for (CaretakerAssessmentEntity cta : caretaker.getCaretakerAssessments()) {
                        for (ResponseEntity res : cta.getCaretakerResponses()) {
                            em.remove(res);
                        }
                        em.remove(cta);
                    }
                }
            }
            for (AssessmentEntity assess : ass.getAssessments()) {
                em.remove(assess);
                for (ResponseEntity res : assess.getResponse()) {
                    em.remove(res);
                }
                for (CaretakerAssessmentEntity cta : assess.getCaretakerAssessments()) {
                    em.remove(cta);
                    for (ResponseEntity res : cta.getCaretakerResponses()) {
                        em.remove(res);
                    }
                }
            }
            em.remove(ass);
        }
        em.remove(org);
    }

    @Override
    public List<AssessorEntity> retrieveRootFromOrg(Long orgId) {
        Query query = em.createQuery("SELECT a FROM AssessorEntity a WHERE a.organisation.organisationId = :orgId AND a.root = TRUE");
        query.setParameter("orgId", orgId);

        return query.getResultList();
    }

    @Override
    public List<AssessorEntity> retrieveSupervisorFromOrg(Long orgId) {
        Query query = em.createQuery("SELECT a FROM AssessorEntity a WHERE a.organisation.organisationId = :orgId AND size(a.supervisee) != 0");
        query.setParameter("orgId", orgId);

        return query.getResultList();
    }

    @Override
    public List<AssessorEntity> retrieveCaseworkerFromOrg(Long orgId) {
        Query query = em.createQuery("SELECT a FROM AssessorEntity a WHERE a.organisation.organisationId = :orgId AND size(a.supervisee) = 0");
        query.setParameter("orgId", orgId);

        return query.getResultList();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<OrganisationEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    private String prepareInputDataValidationErrorsMessage2(Set<ConstraintViolation<AssessorEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

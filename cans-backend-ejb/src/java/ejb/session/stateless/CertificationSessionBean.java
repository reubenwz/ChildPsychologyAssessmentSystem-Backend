/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessorEntity;
import entity.CertificationEntity;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CertificationExistsException;
import util.exception.CertificationNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ong Bik Jeun
 */
@Stateless
public class CertificationSessionBean implements CertificationSessionBeanLocal {

    @EJB
    private AssessorSessionBeanLocal assessorSessionBean;

    @PersistenceContext(unitName = "cans-backend-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CertificationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) validatorFactory.getValidator();
    }

    @Override
    public List<CertificationEntity> retrieveAllCertificate(Long orgId) {
        Query query = em.createQuery("SELECT c FROM CertificationEntity c WHERE c.assessor.organisation.organisationId = :orgid");
        query.setParameter("orgid", orgId);

        return query.getResultList();
    }

    @Override
    public List<CertificationEntity> retrieveAllCertificatebyAssessorId(Long assessorId) {
        Query query = em.createQuery("SELECT c FROM CertificationEntity c WHERE c.assessor.assessorId = :assessorId");
        query.setParameter("assessorId", assessorId);

        return query.getResultList();
    }

    @Override
    public CertificationEntity retrieveCertById(Long id) throws CertificationNotFoundException {
        CertificationEntity cert = em.find(CertificationEntity.class, id);
        if (cert == null) {
            throw new CertificationNotFoundException("Cert with ID: " + id + " does not exist!");
        }
        return cert;
    }

    @Override
    public CertificationEntity createCertificate(CertificationEntity cert, Long assessorId) throws CertificationExistsException, UnknownPersistenceException, InputDataValidationException, UserNotFoundException {
        Set<ConstraintViolation<CertificationEntity>> constraintViolations = validator.validate(cert);
        AssessorEntity assessor;
        try {
            assessor = assessorSessionBean.retrieveUserById(assessorId);
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(cert);
                cert.setAssessor(assessor);
                assessor.getCertificates().add(cert);
                em.flush();
                return cert;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CertificationExistsException();
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
    public void updateCertification(long certId, Date dateOfCert, String vignette, double recentScore, int times) throws CertificationNotFoundException {
        try {
            CertificationEntity cert = this.retrieveCertById(certId);
            if (dateOfCert != null) {
                cert.setDateOfCert(dateOfCert);
            }
            if (vignette != null) {
                cert.setVignette(vignette);
            }
            cert.setRecentScore(recentScore);
            cert.setNoOfTimesRecertified(times);

            em.flush();
        } catch (CertificationNotFoundException ex) {
            throw new CertificationNotFoundException(ex.getMessage());
        }
    }

    @Override
    public void deleteCertification(long certId) throws CertificationNotFoundException {
        try {
            CertificationEntity cert = retrieveCertById(certId);
            cert.getAssessor().getCertificates().remove(cert);
            em.remove(cert);
        } catch (CertificationNotFoundException ex) {
            throw new CertificationNotFoundException(ex.getMessage());
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CertificationEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

}

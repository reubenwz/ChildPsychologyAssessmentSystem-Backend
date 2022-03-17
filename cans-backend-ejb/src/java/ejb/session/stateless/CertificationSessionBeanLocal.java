/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CertificationEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CertificationExistsException;
import util.exception.CertificationNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ong Bik Jeun
 */
@Local
public interface CertificationSessionBeanLocal {

    public List<CertificationEntity> retrieveAllCertificatebyAssessorId(Long assessorId);

    public CertificationEntity retrieveCertById(Long id) throws CertificationNotFoundException;

    public CertificationEntity createCertificate(CertificationEntity cert, Long assessorId) throws CertificationExistsException, UnknownPersistenceException, InputDataValidationException, UserNotFoundException;

    public void updateCertification(long certId, Date dateOfCert, String vignette, double recentScore, int times) throws CertificationNotFoundException;

    public void deleteCertification(long certId) throws CertificationNotFoundException;

    public List<CertificationEntity> retrieveAllCertificate(Long orgId);

}

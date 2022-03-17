/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AssessorEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.AssessorDeletionError;
import util.exception.AssessorExistsException;
import util.exception.AssociationException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OrganisationNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface AssessorSessionBeanLocal {

    public Long createNewAssessor(AssessorEntity ass) throws UnknownPersistenceException, AssessorExistsException, InputDataValidationException;

    public AssessorEntity retrieveUserById(Long id) throws UserNotFoundException;

    public AssessorEntity retrieveUserByEmail(String email) throws UserNotFoundException;

    public List<AssessorEntity> retrieveAllAssessorsByOrganisation(long orgId);

    public AssessorEntity assessorLogin(String email, String password) throws InvalidLoginCredentialException;

    public void updatePassword(long assessorId, String oldPassword, String newPassword) throws InvalidLoginCredentialException, UserNotFoundException;

    public void updateDetails(long assessorId, String name) throws UserNotFoundException;

    public void assignSupervisorSupervisee(long supervisorId, long superviseeId) throws UserNotFoundException, AssociationException;

    public AssessorEntity createNewAssessor(AssessorEntity ass, Long orgId) throws OrganisationNotFoundException, AssessorExistsException, UnknownPersistenceException, InputDataValidationException;

    public void deleteAssessor(long assessorId) throws AssessorDeletionError, UserNotFoundException;

    public Boolean updateActiveStatus(long assessorId) throws UserNotFoundException, AssessorDeletionError;

    public void updateDetails(long assessorId, String name, String email, long supervisor_id) throws UserNotFoundException;

    public void removeSupervisorSupervisee(long supervisorId, long superviseeId) throws UserNotFoundException, AssociationException;

}

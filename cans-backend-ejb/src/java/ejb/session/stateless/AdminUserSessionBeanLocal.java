/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminUserEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.AdminUserExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface AdminUserSessionBeanLocal {

    public AdminUserEntity retrieveUserById(Long id) throws UserNotFoundException;

    public AdminUserEntity retrieveUserByEmail(String email) throws UserNotFoundException;

    public long createNewAdminUser(AdminUserEntity admin) throws UnknownPersistenceException, AdminUserExistsException, InputDataValidationException;

    public AdminUserEntity adminUserLogin(String email, String password) throws InvalidLoginCredentialException;

    public void updatePassword(long adminId, String oldPassword, String newPassword) throws UserNotFoundException, InvalidLoginCredentialException;

    public void updateDetails(long adminId, String gender, Date dob, String name);

    public List<AdminUserEntity> retrieveAllAdmins();

    public void deleteAdminUser(long adminUserId) throws UserNotFoundException;

    public AdminUserEntity createNewAdminUserFromSystem(AdminUserEntity admin) throws UnknownPersistenceException, AdminUserExistsException, InputDataValidationException;
    
}

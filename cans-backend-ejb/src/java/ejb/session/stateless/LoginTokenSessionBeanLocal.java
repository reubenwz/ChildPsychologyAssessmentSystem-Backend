/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminUserEntity;
import entity.AssessorEntity;
import entity.LoginTokenEntity;
import javax.ejb.Local;
import util.exception.LoginTokenNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface LoginTokenSessionBeanLocal {

    public AdminUserEntity validateLoginTokenAdminSystem(String loginTokenId) throws LoginTokenNotFoundException;

    public LoginTokenEntity createNewLoginTokenForAdminSystem(long userId) throws UserNotFoundException, UnknownPersistenceException;

    public void removeLoginToken(String loginTokenValue) throws LoginTokenNotFoundException;

    public AssessorEntity validateLoginTokenAssessorSystem(String loginTokenId) throws LoginTokenNotFoundException;

    public LoginTokenEntity createNewLoginTokenForAssessorSystem(long userId) throws UserNotFoundException, UnknownPersistenceException;

}

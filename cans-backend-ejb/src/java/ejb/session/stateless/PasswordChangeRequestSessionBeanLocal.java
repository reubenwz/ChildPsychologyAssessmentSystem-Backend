/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.Local;
import util.exception.PasswordChangeRequestNotFoundException;
import util.exception.UserNotFoundException;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface PasswordChangeRequestSessionBeanLocal {

    public boolean createPasswordChangeRequestAdminSystem(String email);

    public void updatePasswordAdminSystem(String passwordChangeRequestId, String newPassword) throws PasswordChangeRequestNotFoundException;

    public boolean createPasswordChangeRequestAssessorSystem(String email);

    public void updatePasswordAssessorSystem(String passwordChangeRequestId, String newPassword) throws PasswordChangeRequestNotFoundException;

    public boolean newAssessorAccountPasswordReset(String email);

    public boolean newAdminAccountPasswordReset(String email);

}

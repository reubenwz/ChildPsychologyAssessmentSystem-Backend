/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.util.concurrent.Future;
import javax.ejb.Local;

/**
 *
 * @author Ooi Jun Hao
 */
@Local
public interface EmailSessionBeanLocal {

    public Boolean emailPasswordResetSync(String token, String toEmailAddress, String type);

  //  public Future<Boolean> emailPasswordResetAsync(String token, String toEmailAddress) throws InterruptedException;
    
}

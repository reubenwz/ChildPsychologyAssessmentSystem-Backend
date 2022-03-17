/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import util.helper.EmailManager;

/**
 *
 * @author Ooi Jun Hao
 * @author Ong Bik Jeun
 */
@Stateless
public class EmailSessionBean implements EmailSessionBeanLocal {

    // default msf gmail account
    private final String FROM_EMAIL_ADDRESS = "msftesterid04@gmail.com";
    private final String GMAIL_USERNAME = "msftesterid04@gmail.com";
    private final String GMAIL_PASSWORD = "Password123!!!";

    @Override
    public Boolean emailPasswordResetSync(String token, String toEmailAddress, String type) {  // oldAdmin, oldAssessor, newAdmin, newAssessor

        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);

        Boolean result;
        if (type.equals("oldAdmin")) {
            String link = "http://localhost:4200/auth/password-reset/" + token; 
            result = emailManager.emailPasswordReset(link, FROM_EMAIL_ADDRESS, toEmailAddress);
        } else if (type.equals("newAdmin")) {
            String link = "http://localhost:4200/auth/password-reset/" + token; 
            result = emailManager.emailNewPasswordResetAdmin(link, FROM_EMAIL_ADDRESS, toEmailAddress); 
        } else if (type.equals("oldAssessor")) {
            String link = "http://localhost:4401/auth/password-reset/" + token; 
            result = emailManager.emailPasswordReset(link, FROM_EMAIL_ADDRESS, toEmailAddress);
        } else if (type.equals("newAssessor")) {
            String link = "http://localhost:4401/auth/password-reset/" + token; 
            result = emailManager.emailNewPasswordResetAssessor(link, FROM_EMAIL_ADDRESS, toEmailAddress);
        } else {
            System.out.println("****************** ERROR: UNKNOWN TYPE FOR PASSWORD RESET *********************");
            result = false;
        }
        return result;
    }

    //deprecated
//    @Asynchronous
//    @Override
//    public Future<Boolean> emailPasswordResetAsync(String token, String toEmailAddress) throws InterruptedException {
//
//        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
//        String link = "http://localhost:4200/auth/password-reset/" + token; // to be edited
//        Boolean result = emailManager.emailPasswordResetAdmin(link, FROM_EMAIL_ADDRESS, toEmailAddress);
//
//        return new AsyncResult<>(result);
//
//    }
}

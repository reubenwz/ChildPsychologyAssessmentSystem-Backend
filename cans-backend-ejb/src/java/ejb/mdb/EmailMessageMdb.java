/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.mdb;

import ejb.session.stateless.EmailSessionBeanLocal;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author Ong Bik Jeun
 * @author Ooi Jun Hao
 */
// @JMSDestinationDefinition(name = "java:app/jms/queueEmail", interfaceName = "javax.jms.Queue", resourceAdapter = "jmsra", destinationName = "queueEmail")
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/queueEmail"), // original lookup = java:app/jms/queueEmail
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class EmailMessageMdb implements MessageListener {

    @EJB
    private EmailSessionBeanLocal emailSessionBean;
    
    public EmailMessageMdb() {
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            try {
                MapMessage mm = (MapMessage) message;
                String toEmail = mm.getString("toEmailAddress");
                String tokenHash = mm.getString("token");
                String type = mm.getString("type");
                emailSessionBean.emailPasswordResetSync(tokenHash, toEmail, type);
                System.out.println("Email sending to: " + toEmail);
            } catch (JMSException ex) {
                System.err.println("EmailMessageBean.onMessage(): " + ex.getMessage());
            }
        }
    }
    
}

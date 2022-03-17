/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.AdminUserSessionBeanLocal;
import ejb.session.stateless.AgeGroupSessionBeanLocal;
import ejb.session.stateless.AssessmentSessionBeanLocal;
import ejb.session.stateless.AssessorSessionBeanLocal;
import ejb.session.stateless.CaretakerAssessmentSessionBeanLocal;
import ejb.session.stateless.CaretakerSessionBeanLocal;
import ejb.session.stateless.CertificationSessionBeanLocal;
import ejb.session.stateless.ClientSessionBeanLocal;
import ejb.session.stateless.DomainSessionBeanLocal;
import ejb.session.stateless.ExportSessionBeanLocal;
import ejb.session.stateless.LoginTokenSessionBeanLocal;
import ejb.session.stateless.OrganisationSessionBeanLocal;
import ejb.session.stateless.PasswordChangeRequestSessionBeanLocal;
import ejb.session.stateless.UploadSessionBeanLocal;
import ejb.session.stateless.VisualisationSessionBeanLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Ooi Jun Hao
 */
public class SessionBeanLookup {

    CertificationSessionBeanLocal certificationSessionBean = lookupCertificationSessionBeanLocal();

    CaretakerAssessmentSessionBeanLocal caretakerAssessmentSessionBean = lookupCaretakerAssessmentSessionBeanLocal();

    DomainSessionBeanLocal domainSessionBean = lookupDomainSessionBeanLocal();

    AgeGroupSessionBeanLocal ageGroupSessionBean = lookupAgeGroupSessionBeanLocal();

    AssessmentSessionBeanLocal assessmentSessionBean = lookupAssessmentSessionBeanLocal();

    AssessorSessionBeanLocal assessorSessionBean = lookupAssessorSessionBeanLocal();

    CaretakerSessionBeanLocal caretakerSessionBean = lookupCaretakerSessionBeanLocal();

    OrganisationSessionBeanLocal organisationSessionBean = lookupOrganisationSessionBeanLocal();

    ClientSessionBeanLocal clientSessionBean = lookupClientSessionBeanLocal();

    VisualisationSessionBeanLocal visualisationSessionBean = lookupVisualisationSessionBeanLocal();

    ExportSessionBeanLocal exportSessionBean = lookupExportSessionBeanLocal();

    UploadSessionBeanLocal uploadSessionBean = lookupUploadSessionBeanLocal();

    PasswordChangeRequestSessionBeanLocal passwordChangeRequestSessionBean = lookupPasswordChangeRequestSessionBeanLocal();

    LoginTokenSessionBeanLocal loginTokenSessionBean = lookupLoginTokenSessionBeanLocal();

    AdminUserSessionBeanLocal adminUserSessionBean = lookupAdminUserSessionBeanLocal();

    public SessionBeanLookup() {
    }

    private AdminUserSessionBeanLocal lookupAdminUserSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (AdminUserSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/AdminUserSessionBean!ejb.session.stateless.AdminUserSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private LoginTokenSessionBeanLocal lookupLoginTokenSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (LoginTokenSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/LoginTokenSessionBean!ejb.session.stateless.LoginTokenSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private PasswordChangeRequestSessionBeanLocal lookupPasswordChangeRequestSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (PasswordChangeRequestSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/PasswordChangeRequestSessionBean!ejb.session.stateless.PasswordChangeRequestSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private UploadSessionBeanLocal lookupUploadSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (UploadSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/UploadSessionBean!ejb.session.stateless.UploadSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private ExportSessionBeanLocal lookupExportSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (ExportSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/ExportSessionBean!ejb.session.stateless.ExportSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private VisualisationSessionBeanLocal lookupVisualisationSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (VisualisationSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/VisualisationSessionBean!ejb.session.stateless.VisualisationSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private ClientSessionBeanLocal lookupClientSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (ClientSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/ClientSessionBean!ejb.session.stateless.ClientSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private OrganisationSessionBeanLocal lookupOrganisationSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (OrganisationSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/OrganisationSessionBean!ejb.session.stateless.OrganisationSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CaretakerSessionBeanLocal lookupCaretakerSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CaretakerSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/CaretakerSessionBean!ejb.session.stateless.CaretakerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private AssessorSessionBeanLocal lookupAssessorSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (AssessorSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/AssessorSessionBean!ejb.session.stateless.AssessorSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private AssessmentSessionBeanLocal lookupAssessmentSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (AssessmentSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/AssessmentSessionBean!ejb.session.stateless.AssessmentSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private AgeGroupSessionBeanLocal lookupAgeGroupSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (AgeGroupSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/AgeGroupSessionBean!ejb.session.stateless.AgeGroupSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private DomainSessionBeanLocal lookupDomainSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (DomainSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/DomainSessionBean!ejb.session.stateless.DomainSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CaretakerAssessmentSessionBeanLocal lookupCaretakerAssessmentSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CaretakerAssessmentSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/CaretakerAssessmentSessionBean!ejb.session.stateless.CaretakerAssessmentSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CertificationSessionBeanLocal lookupCertificationSessionBeanLocal() {
        try {
            Context c = new InitialContext();
            return (CertificationSessionBeanLocal) c.lookup("java:global/cans-backend/cans-backend-ejb/CertificationSessionBean!ejb.session.stateless.CertificationSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}

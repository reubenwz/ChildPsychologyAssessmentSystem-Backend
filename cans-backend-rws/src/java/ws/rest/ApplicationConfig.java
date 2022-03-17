package ws.rest;

import java.util.Set;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

@javax.ws.rs.ApplicationPath("Resources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);

        resources.add(MultiPartFeature.class);

        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.rest.AdminUserResource.class);
        resources.add(ws.rest.AssessmentManagementAdminSystemResource.class);
        resources.add(ws.rest.AssessmentQuestionManagementResource.class);
        resources.add(ws.rest.AssessmentResource.class);
        resources.add(ws.rest.AssessmentValidationResource.class);
        resources.add(ws.rest.AssessorManagementAdminSystemResource.class);
        resources.add(ws.rest.AssessorManagementAssessorSystemResource.class);
        resources.add(ws.rest.AssessorResource.class);
        resources.add(ws.rest.AssessorVisualisationResource.class);
        resources.add(ws.rest.CertificationResource.class);
        resources.add(ws.rest.ClientManagementAdminSystemResource.class);
        resources.add(ws.rest.ClientManagementAssessorSystemResource.class);
        resources.add(ws.rest.CorsFilter.class);
        resources.add(ws.rest.DashboardResource.class);
        resources.add(ws.rest.DataManagementResource.class);
    }
}

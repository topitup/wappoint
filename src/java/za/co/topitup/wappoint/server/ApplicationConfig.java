package za.co.topitup.wappoint.server;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
       
        //addRestResourceClasses(resources);
        return resources;
    }

    
    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(za.co.topitup.wappoint.server.MyException.class);
        resources.add(za.co.topitup.wappoint.services.Api.class);
        resources.add(za.co.topitup.wappoint.services.Connect.class);
        resources.add(za.co.topitup.wappoint.services.Financial.class);
        resources.add(za.co.topitup.wappoint.services.Server.class);
    }
    
}

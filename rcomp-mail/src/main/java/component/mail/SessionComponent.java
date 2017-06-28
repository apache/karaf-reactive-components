package component.mail;

import java.util.Map;
import java.util.Properties;

import javax.mail.Session;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class SessionComponent {

    private ServiceRegistration<Session> sreg;

    @Activate
    public void activate(Map<String, Object> config, BundleContext context) {
        Session session = create(config);
        sreg = context.registerService(Session.class, session, null);
    }
    
    @Deactivate
    public void deactivate() {
        sreg.unregister();
    }
    
    public static Session create(Map<String, Object> config) {
        Properties props = fromConfig(config);
        return Session.getDefaultInstance(props);
    }
    
    private static Properties fromConfig(Map<String, Object> config) {
        Properties props = new Properties();
        for (String key : config.keySet()) {
            props.put(key, config.get(key));
        }
        return props;
    }
}

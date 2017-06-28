package component.eventadmin;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import component.api.MComponent;

@Component(property="name=eventAdmin")
public class EventAdminComponent implements MComponent<Map<String, ?>> {
    
    private BundleContext context;

    @Reference
    EventAdmin client;
    

    @Activate
    public void activate(BundleContext context) {
        this.context = context;
    }
    
    @Deactivate
    public void deactivate() {
    }

    @Override
    public Publisher<Map<String, ?>> from(String topic) {
        return new EventAdminSource(context, topic);
    }  
    
    @Override
    public Subscriber<Map<String, ?>> to(String topic) {
        return new EventAdminDestination(client, topic);
    }

}

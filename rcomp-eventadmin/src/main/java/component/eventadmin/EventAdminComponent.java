package component.eventadmin;

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
public class EventAdminComponent implements MComponent {
    
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
    public <T> Publisher<T> from(String topic, Class<T> type) {
        return new EventAdminSource<T>(context, topic, type);
    }  
    
    @Override
    public <T> Subscriber<T> to(String topic, Class<T> type) {
        return new EventAdminDestination<T>(client, topic, type);
    }

}

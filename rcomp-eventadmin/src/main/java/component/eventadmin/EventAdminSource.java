package component.eventadmin;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EventAdminSource implements Publisher<Map<String, ?>> {
    private BundleContext context;
    private String topic;
    
    public EventAdminSource(BundleContext context, String topic) {
        this.context = context;
        this.topic = topic;
    }
    
    @Override
    public void subscribe(Subscriber<? super Map<String, ?>> subscriber) {
        subscriber.onSubscribe(new EventAdminSubscription(subscriber));
    }

    public class EventAdminSubscription implements Subscription, EventHandler {
        private AtomicBoolean subScribed;
        private ServiceRegistration<EventHandler> sreg;
        private Subscriber<? super Map<String, ?>> subscriber;
        
        public EventAdminSubscription(Subscriber<? super Map<String, ?>> subscriber) {
            this.subscriber = subscriber;
            this.subScribed = new AtomicBoolean(false);
        }

        @Override
        public void request(long n) {
            if (subScribed.compareAndSet(false, true)) {
                Dictionary<String, Object> props = new Hashtable<>();
                props.put(EventConstants.EVENT_TOPIC, new String[]{topic});
                sreg = context.registerService(EventHandler.class, this, props);
            }
        }

        @Override
        public void cancel() {
            try {
                if (subScribed.compareAndSet(true, false)) {
                    sreg.unregister();
                }
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }
        
        @Override
        public void handleEvent(Event event) {
            this.subscriber.onNext(toMap(event));
        }

        Map<String, ?> toMap(Event event) {
            Map<String, Object> props = new HashMap<>();
            props.put("topic", event.getTopic());
            for (String key : event.getPropertyNames()) {
                props.put(key, event.getProperty(key));
            }
            return props;
            
        }
    }

}

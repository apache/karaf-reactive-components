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

public class EventAdminSource<T> implements Publisher<T> {
    private BundleContext context;
    private String topic;
    
    public EventAdminSource(BundleContext context, String topic, Class<T> type) {
        this.context = context;
        this.topic = topic;
        if (! type.equals(Map.class)) {
            throw new IllegalArgumentException("Curently only Map<String, ?> is supported");
        }
    }
    
    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new EventAdminSubscription(subscriber));
    }

    public class EventAdminSubscription implements Subscription, EventHandler {
        private AtomicBoolean subScribed;
        private ServiceRegistration<EventHandler> sreg;
        private Subscriber<? super T> subscriber;
        
        public EventAdminSubscription(Subscriber<? super T> subscriber) {
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
            this.subscriber.onNext(convertTo(toMap(event)));
        }

        @SuppressWarnings("unchecked")
        private T convertTo(Map<String, ?> map) {
            return (T) map;
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

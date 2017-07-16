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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventAdminSource<T> implements Publisher<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventAdminSource.class);
    private BundleContext context;
    private String topic;
    private Class<T> type;

    public EventAdminSource(BundleContext context, String topic, Class<T> type) {
        this.context = context;
        this.topic = topic;
        if (!(type == Map.class || type == Event.class)) {
            throw new IllegalArgumentException("Curently only Map<String, ?> and Event are supported");
        }
        this.type = type;
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
                subscribe();
            }
        }

        @Override
        public void cancel() {
            if (subScribed.compareAndSet(true, false)) {
                unsubscribe();
            }
        }

        @Override
        public void handleEvent(Event event) {
            this.subscriber.onNext(toType(event));
        }

        private void subscribe() {
            Dictionary<String, Object> props = new Hashtable<>();
            props.put(EventConstants.EVENT_TOPIC, new String[] {
                                                                topic
            });
            sreg = context.registerService(EventHandler.class, this, props);
            LOGGER.info("Subscribed to " + topic);
        }

        private void unsubscribe() {
            try {
                sreg.unregister();
                LOGGER.info("Unsubscribed from " + topic);
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }

        @SuppressWarnings("unchecked")
        private T toType(Event event) {
            if (type == Event.class) {
                return (T)event;
            } else {
                return (T)toMap(event);
            }
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

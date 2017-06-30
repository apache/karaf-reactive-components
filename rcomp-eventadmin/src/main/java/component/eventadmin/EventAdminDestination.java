package component.eventadmin;

import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EventAdminDestination<T> implements Subscriber<T> {
    
    private EventAdmin client;
    private String topic;
    private Subscription subscription;

    public EventAdminDestination(EventAdmin client, String topic, Class<T> type) {
        this.client = client;
        this.topic = topic;
        if (! type.equals(Map.class)) {
            throw new IllegalArgumentException("Curently only Map<String, ?> is supported");
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T payload) {
        try {
            Event event = new Event(topic, convertTo(payload));
            this.client.sendEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> convertTo(T payload) {
        return (Map<String, ?>) payload;
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
    }

}

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
    private Class<T> type;

    public EventAdminDestination(EventAdmin client, String topic, Class<T> type) {
        this.client = client;
        this.topic = topic;
        if (!(type == Map.class || type==Event.class)) {
            throw new IllegalArgumentException("Curently only Map<String, ?> and Event are supported");
        }
        this.type = type;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T payload) {
        try {
            Event event = toEvent(payload);
            this.client.sendEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    @SuppressWarnings("unchecked")
    private Event toEvent(T payload) {
        if (type == Event.class) {
            return (Event)payload;
        } else {
            return new Event(topic, (Map<String, ?>)payload);
        }
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
    }

}

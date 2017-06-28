package component.eventadmin;

import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EventAdminDestination implements Subscriber<Map<String, ?>> {
    
    private EventAdmin client;
    private String topic;
    private Subscription subscription;

    public EventAdminDestination(EventAdmin client, String topic) {
        this.client = client;
        this.topic = topic;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(Map<String, ?> payload) {
        try {
            Event event = new Event(topic, payload);
            this.client.sendEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
    }

}

package component.eventadmin;

import java.util.Map;
import java.util.function.Function;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class EventAdminDestination<T> implements Subscriber<T> {
    
    private EventAdmin client;
    private String topic;
    private Function<T, Map<String, ?>> converter;
    private Subscription subscription;

    public EventAdminDestination(EventAdmin client, String topic, Function<T, Map<String, ?>> converter) {
        this.client = client;
        this.topic = topic;
        this.converter = converter;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T payload) {
        try {
            Event event = new Event(topic, converter.apply(payload));
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

package component.eventadmin;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class EventAdminDestination<T> implements Consumer<T> {
    
    private EventAdmin client;
    private String topic;
    private Function<T, Map<String, ?>> converter;

    public EventAdminDestination(EventAdmin client, String topic, Function<T, Map<String, ?>> converter) {
        this.client = client;
        this.topic = topic;
        this.converter = converter;
    }

    @Override
    public void accept(T payload) {
        try {
            Event event = new Event(topic, converter.apply(payload));
            this.client.sendEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

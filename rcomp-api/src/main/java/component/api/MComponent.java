package component.api;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public interface MComponent {
    <T> Publisher<T> from(String destination, Class<T> type);
    <T> Subscriber<T> to(String destination, Class<T> type);
}

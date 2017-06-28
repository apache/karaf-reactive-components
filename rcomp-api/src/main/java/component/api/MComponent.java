package component.api;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public interface MComponent<T> {
    Publisher<T> from(String destination);
    Subscriber<T> to(String destination);
}

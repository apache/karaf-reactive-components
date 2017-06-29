package component.api;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public interface MComponent {
    <S> Publisher<S> from(String destination, Class<? extends S> type);
    <S> Subscriber<S> to(String destination, Class<? extends S> type);
}

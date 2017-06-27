package component.api;

import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public interface MComponent<F> {
    <T> Publisher<T> from(String destination, Function<F, T> converter) throws Exception;
    <T> Subscriber<T> to(String destination, Function<T, F> converter) throws Exception;
}

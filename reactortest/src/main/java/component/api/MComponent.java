package component.api;

import java.util.function.Consumer;
import java.util.function.Function;

import org.reactivestreams.Publisher;

public interface MComponent<F> {
    <T> Publisher<T> from(String destination, Function<F, T> converter) throws Exception;
    <T> Consumer<T> to(String destination, Function<T, F> converter) throws Exception;
}

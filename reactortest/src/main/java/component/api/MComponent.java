package component.api;

import java.util.function.Consumer;
import java.util.function.Function;

import org.reactivestreams.Publisher;

public interface MComponent {
    <T> Publisher<T> from(String topic, Function<byte[], T> converter) throws Exception;
    <T> Consumer<T> to(String topic, Function<T, byte[]> converter) throws Exception;
}

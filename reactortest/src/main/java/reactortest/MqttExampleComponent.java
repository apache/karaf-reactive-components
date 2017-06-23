package reactortest;

import java.util.function.Consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;

import component.api.MComponent;
import reactor.core.publisher.Flux;
import reactor.math.MathFlux;

@Component(immediate=true)
public class MqttExampleComponent {
    @Reference(target="(name=mqtt)")
    MComponent mqtt;

    @Activate
    public void start() throws Exception {
        Publisher<Integer> fromTopic = mqtt.from("input", ByteArrayConverter::asInteger);
        Consumer<Double> toTopic = mqtt.to("test", DoubleConverter::asByteAr);
        Flux.from(fromTopic)
            .window(2, 1)
            .flatMap(win -> MathFlux.averageDouble(win))
            .subscribe(toTopic);
    }

}

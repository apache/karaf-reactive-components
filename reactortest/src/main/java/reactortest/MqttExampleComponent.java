package reactortest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.api.MComponent;
import reactor.core.publisher.Flux;
import reactor.math.MathFlux;

@Component(immediate=true)
public class MqttExampleComponent {
    Logger LOG = LoggerFactory.getLogger(MqttExampleComponent.class);
    
    @Reference(target="(name=mqtt)")
    MComponent<byte[]> mqtt;

    @Activate
    public void start() throws Exception {
        LOG.info("Starting mqtt test component");
        Publisher<Integer> fromTopic = mqtt.from("input", ByteArrayConverter::asInteger);
        Subscriber<Double> toTopic = mqtt.to("output", DoubleConverter::asByteAr);
        Flux.from(fromTopic)
            .log()
            .window(2, 1)
            .flatMap(win -> MathFlux.averageDouble(win))
            .subscribe(toTopic);
        LOG.info("mqtt test component started");
    }

}

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
public class MqttExample {
    Logger LOG = LoggerFactory.getLogger(MqttExample.class);
    
    @Reference(target="(name=mqtt)")
    MComponent mqtt;

    @Activate
    public void start() throws Exception {
        LOG.info("Starting mqtt test component2");
        Publisher<byte[]> fromTopic = mqtt.from("input", byte[].class);
        Subscriber<byte[]> toTopic = mqtt.to("output", byte[].class);
        Flux.from(fromTopic)
            .map(ByteArrayConverter::asInteger)
            .log()
            .window(2, 1)
            .flatMap(win -> MathFlux.averageDouble(win))
            .map(DoubleConverter::asByteAr)
            .subscribe(toTopic);
        LOG.info("mqtt test component started");
    }

}

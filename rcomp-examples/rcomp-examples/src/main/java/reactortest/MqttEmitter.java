package reactortest;

import java.time.Duration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.api.RComponent;
import reactor.core.publisher.Flux;

@Component(immediate=true)
public class MqttEmitter {
    Logger LOG = LoggerFactory.getLogger(MqttEmitter.class);
    
    @Reference(target="(name=mqtt)")
    RComponent mqtt;

    private Subscriber<byte[]> toTopic;

    @Activate
    public void start() throws Exception {
        toTopic = mqtt.to("input", byte[].class);
        Flux.interval(Duration.ofSeconds(1))
            .map(ByteArrayConverter::fromLong)
            .subscribe(toTopic);
        LOG.info("mqtt test component started4");
    }
    
    @Deactivate
    public void stop() throws Exception {
        LOG.info("mqtt test component stopped");
        toTopic.onComplete();
    }

}
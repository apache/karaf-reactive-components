package reactortest;

import java.util.function.Consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.api.RComponent;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Component(immediate=true)
public class MqttReceiver implements Consumer<Double>{
    Logger LOG = LoggerFactory.getLogger(MqttReceiver.class);
    
    @Reference(target="(name=mqtt)")
    RComponent mqtt;

    private Disposable flux;

    @Activate
    public void start() throws Exception {
        LOG.info("Starting mqtt receiver");
        Publisher<byte[]> fromTopic = mqtt.from("output", byte[].class);
        flux = Flux.from(fromTopic)
            .map(ByteArrayConverter::asDouble)
            .subscribe(this);
    }
    
    @Deactivate
    public void stop() {
        flux.dispose();
    }

    @Override
    public void accept(Double average) {
        LOG.info("Received average value of " + average);
    }

}

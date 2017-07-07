package reactortest;

import java.util.function.Consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.api.MComponent;
import reactor.core.publisher.Flux;

@Component(immediate=true)
public class MqttReceiver implements Consumer<Double>{
    Logger LOG = LoggerFactory.getLogger(MqttReceiver.class);
    
    @Reference(target="(name=mqtt)")
    MComponent mqtt;

    @Activate
    public void start() throws Exception {
        LOG.info("Starting mqtt receiver");
        Publisher<byte[]> fromTopic = mqtt.from("output", byte[].class);
        Flux.from(fromTopic)
            .map(ByteArrayConverter::asDouble)
            .subscribe(this);
    }

    @Override
    public void accept(Double average) {
        System.out.println("Received average value of " + average);
    }

}

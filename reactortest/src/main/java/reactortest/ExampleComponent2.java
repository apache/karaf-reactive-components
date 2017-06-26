package reactortest;

import java.util.Map;
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
public class ExampleComponent2 {
    Logger LOG = LoggerFactory.getLogger(ExampleComponent2.class);
    
    @Reference(target="(name=eventAdmin)")
    MComponent<Map<String, ? >> mqtt;

    @Activate
    public void start() throws Exception {
        LOG.info("Starting component2");
        Publisher<Map<String, ?>> fromTopic = mqtt.from("input", Map2Map::convert);
        Consumer<Map<String, ?>> toTopic = mqtt.to("output", Map2Map::convert);
        Flux.from(fromTopic)
            .log()
            .subscribe();
        LOG.info("component2 started");
    }

}

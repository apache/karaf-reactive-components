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
public class ExampleEventAdmin {
    Logger LOG = LoggerFactory.getLogger(ExampleEventAdmin.class);
    
    @Reference(target="(name=eventAdmin)")
    MComponent<Map<String, ? >> eventAdmin;

    @Activate
    public void start() throws Exception {
        Publisher<Map<String, ?>> fromTopic = eventAdmin.from("input", Map2Map::convert);
        Consumer<Map<String, ?>> toTopic = eventAdmin.to("output", Map2Map::convert);
        Flux.from(fromTopic)
            .log()
            .subscribe(toTopic);
    }

}

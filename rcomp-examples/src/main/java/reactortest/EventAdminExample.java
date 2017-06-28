package reactortest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.api.MComponent;
import reactor.core.publisher.Flux;

@Component(immediate=true)
public class EventAdminExample {
    Logger LOG = LoggerFactory.getLogger(EventAdminExample.class);
    
    @Reference(target="(name=eventAdmin)")
    MComponent<Map<String, ? >> eventAdmin;

    @Activate
    public void start() throws Exception {
        Publisher<Map<String, ?>> fromTopic = eventAdmin.from("input");
        Subscriber<Map<String, ?>> toTopic = eventAdmin.to("output");
        Flux.from(fromTopic)
            .log()
            .subscribe(toTopic);
    }

}

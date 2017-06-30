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
    MComponent eventAdmin;

    @Activate
    public void start() throws Exception {
        Publisher<Map> fromTopic = eventAdmin.from("input", Map.class);
        Subscriber<Map> toTopic = eventAdmin.to("output", Map.class);
        Flux.from(fromTopic)
            .log()
            .subscribe(toTopic);
    }

}

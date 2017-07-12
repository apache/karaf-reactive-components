package component.kafka;

import java.util.Dictionary;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.event.EventConstants;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import component.api.RComponent;

@Component(immediate = true, 
    configurationPolicy = ConfigurationPolicy.REQUIRE,
    property = EventConstants.EVENT_TOPIC //
)
public class KafkaComponent implements RComponent {
    Dictionary<String, Object> rawConfig;
    
    public KafkaComponent() {
    }
    
    public KafkaComponent(Dictionary<String, Object> rawConfig) {
        this.rawConfig = rawConfig;
    }

    @Activate
    public void activate(ComponentContext context) {
        this.rawConfig = context.getProperties();
    }
    
    @Deactivate
    public void close() {
    }

    @Override
    public <T> Publisher<T> from(String topic, Class<T> type) {
        return new KafkaSource<>(ConfigMapper.mapConsumer(rawConfig), topic, type);
    }

    @Override
    public <T> Subscriber<T> to(String topic, Class<T> type) {
        return new KafkaDestination<T>(ConfigMapper.mapProducer(rawConfig), topic, type);
    }

}

package component.eventadmin;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.reactivestreams.Publisher;

import component.api.MComponent;

@Component(property="name=eventAdmin")
public class EventAdminComponent implements MComponent<Map<String, ?>> {
    
    private BundleContext context;

    @Reference
    EventAdmin client;
    

    @Activate
    public void activate(BundleContext context) throws MqttException {
        this.context = context;
    }
    
    @Deactivate
    public void deactivate() throws MqttException {
    }

    @Override
    public <T> Publisher<T> from(String topic, Function<Map<String, ?>, T> converter) throws Exception {
        return new EventAdminSource<T>(context, topic, converter);
    }  
    
    @Override
    public <T> Consumer<T> to(String topic, Function<T, Map<String, ?>> converter) throws Exception {
        return new EventAdminDestination<T>(client, topic, converter);
    }

}

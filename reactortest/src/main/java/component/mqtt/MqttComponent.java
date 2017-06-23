package component.mqtt;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.reactivestreams.Publisher;

import component.api.MComponent;

@Component(property="name=mqtt")
public class MqttComponent implements MComponent {
    
    MqttClient client;

    @ObjectClassDefinition(name = "MQTT config")
    @interface MqttConfig {
        String serverUrl() default "tcp://localhost:1883";
        String clientId();
    }
    
    @Activate
    public void activate(MqttConfig config) throws MqttException {
        client = new MqttClient(config.serverUrl(), MqttClient.generateClientId());
        client.connect();
    }
    
    @Deactivate
    public void deactivate() throws MqttException {
        client.disconnect();
        client.close();
    }

    @Override
    public <T> Publisher<T> from(String topic, Function<byte[], T> converter) throws Exception {
        return new MqttSource<T>(client, topic, converter);
    }  
    
    @Override
    public <T> Consumer<T> to(String topic, Function<T, byte[]> converter) throws Exception {
        return new MqttDestination<T>(client, topic, converter);
    }

}

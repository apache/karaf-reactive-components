package component.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import component.api.MComponent;

@Component(property="name=mqtt")
public class MqttComponent implements MComponent<byte[]> {
    
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
    public Publisher<byte[]> from(String topic) {
        return new MqttSource(client, topic);
    }  
    
    @Override
    public Subscriber<byte[]> to(String topic) {
        return new MqttDestination(client, topic);
    }

}

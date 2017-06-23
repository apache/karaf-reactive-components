package component.mqtt;

import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttDestination<T> implements Consumer<T> {
    
    private MqttClient client;
    private String topic;
    private Function<T, byte[]> converter;

    public MqttDestination(MqttClient client, String topic, Function<T, byte[]> converter) {
        this.client = client;
        this.topic = topic;
        this.converter = converter;
    }

    @Override
    public void accept(T payload) {
        try {
            MqttMessage message = new MqttMessage(converter.apply(payload));
            this.client.publish(topic, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

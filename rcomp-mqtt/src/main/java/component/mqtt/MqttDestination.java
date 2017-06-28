package component.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MqttDestination implements Subscriber<byte[]> {
    
    private MqttClient client;
    private String topic;
    private Subscription subscription;

    public MqttDestination(MqttClient client, String topic) {
        this.client = client;
        this.topic = topic;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(byte[] payload) {
        try {
            MqttMessage message = new MqttMessage(payload);
            this.client.publish(topic, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onerr");
    }

    @Override
    public void onComplete() {
        System.out.println("oncomplete");
    }

}

package component.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MqttDestination<T> implements Subscriber<T> {
    
    private MqttClient client;
    private String topic;
    private Subscription subscription;

    public MqttDestination(MqttClient client, String topic, Class<? extends T> type) {
        this.client = client;
        this.topic = topic;
        if (! type.equals(byte[].class)) {
            throw new IllegalArgumentException("Curently only byte[] is supported");
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T payload) {
        try {
            MqttMessage message = new MqttMessage(convertTo(payload));
            this.client.publish(topic, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    private byte[] convertTo(T payload) {
        return (byte[])payload;
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

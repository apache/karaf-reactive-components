package component.mqtt;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MqttSource implements Publisher<byte[]> {
    private MqttClient client;
    private String topic;
    
    public MqttSource(MqttClient client, String topic) {
        this.client = client;
        this.topic = topic;
    }
    
    @Override
    public void subscribe(Subscriber<? super byte[]> subscriber) {
        subscriber.onSubscribe(new MqttSubscription(subscriber));
    }

    public class MqttSubscription implements IMqttMessageListener, Subscription {
        private AtomicBoolean subScribed;
        private Subscriber<? super byte[]> subscriber;
        
        public MqttSubscription(Subscriber<? super byte[]> subscriber) {
            this.subscriber = subscriber;
            this.subScribed = new AtomicBoolean(false);
        }

        @Override
        public void request(long n) {
            try {
                if (subScribed.compareAndSet(false, true)) {
                    client.subscribe(topic, this);
                }
            } catch (MqttException e) {
                subscriber.onError(e);
            }
        }

        @Override
        public void cancel() {
            try {
                if (subScribed.compareAndSet(true, false)) {
                    client.unsubscribe(topic);
                }
            } catch (MqttException e) {
                subscriber.onError(e);
            }
        }
        
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            this.subscriber.onNext(message.getPayload());
        }

    }

}

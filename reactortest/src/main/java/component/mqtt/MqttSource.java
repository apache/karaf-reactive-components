package component.mqtt;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MqttSource <T> implements Publisher<T> {
    private Function<byte[], T> converter;
    private MqttClient client;
    private String topic;
    
    public MqttSource(MqttClient client, String topic, Function<byte[], T> converter) {
        this.client = client;
        this.topic = topic;
        this.converter = converter;
    }
    
    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new MqttSubscription(subscriber));
    }

    public class MqttSubscription implements IMqttMessageListener, Subscription {
        private AtomicBoolean subScribed;
        private Subscriber<? super T> subscriber;
        
        public MqttSubscription(Subscriber<? super T> subscriber) {
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
            T payLoad = converter.apply(message.getPayload()); 
            this.subscriber.onNext(payLoad);
        }

    }

}

package component.kafka;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@SuppressWarnings("rawtypes")
public class KafkaSource<T> implements Publisher<T> {
    private KafkaConsumer consumer;
    private String topic;
    
    public KafkaSource(Properties config, String topic, Class<? extends T> type) {
        this.consumer = new KafkaConsumer(config);
        this.topic = topic;
        if (!((type.equals(byte[].class) || type.equals(ConsumerRecord.class)))) {
            throw new IllegalArgumentException("Curently only byte[] and ProducerRecord are supported");
        }
    }
    
    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new MqttSubscription(subscriber));
    }

    public class MqttSubscription implements Subscription {
        private AtomicBoolean subScribed;
        private Subscriber<? super T> subscriber;
        
        public MqttSubscription(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
            this.subScribed = new AtomicBoolean(false);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void request(long n) {
            try {
                if (subScribed.compareAndSet(false, true)) {
                    consumer.subscribe(Arrays.asList(topic));
                }
                ConsumerRecords<String, String> records = consumer.poll(1000);
                records.forEach(record -> {subscriber.onNext((T)record); consumer.commitAsync(); });
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }

        @Override
        public void cancel() {
            try {
                if (subScribed.compareAndSet(true, false)) {
                    consumer.unsubscribe();
                    consumer.close();
                }
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }

    }

}

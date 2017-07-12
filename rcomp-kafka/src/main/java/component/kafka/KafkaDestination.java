package component.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class KafkaDestination<T> implements Subscriber<T>, AutoCloseable {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaDestination.class);
    private String topic;
    private Subscription subscription;
    private KafkaProducer producer;

    public KafkaDestination(Properties config, String topic, Class<? extends T> type) {
        this.producer = new KafkaProducer(config);
        this.topic = topic;
        if (!((type.equals(byte[].class) || type.equals(ProducerRecord.class)))) {
            throw new IllegalArgumentException("Curently only byte[] and ProducerRecord are supported");
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onNext(T payload) {
        try {
            ProducerRecord<?, ?> record;
            if (payload instanceof ProducerRecord<?, ?>) {
                
                record = (ProducerRecord<?, ?>)payload;
            } else {
                String key = "dummykey";
                record = new ProducerRecord<>(topic, key, payload);                
            }
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {
                        LOGGER.warn("Can't send event to Kafka broker", e);
                    }
                    subscription.request(1);
                }
            });
            producer.flush();
        } catch (RuntimeException e) {
            LOGGER.warn("Error sending event to kafka", e);
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

    @Override
    public void close() throws Exception {
        if (subscription != null) {
            subscription.cancel();
            subscription = null;
        }
    }
}
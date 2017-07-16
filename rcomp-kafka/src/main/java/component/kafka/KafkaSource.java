package component.kafka;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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
    private Class<? extends T> type;

    public KafkaSource(Properties config, String topic, Class<? extends T> type) {
        this.consumer = new KafkaConsumer(config);
        this.topic = topic;
        if (!(type == String.class || type == ConsumerRecord.class)) {
            throw new IllegalArgumentException("Curently only String and ProducerRecord are supported");
        }
        this.type = type;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new KafkaSubscription(subscriber));
    }

    public class KafkaSubscription implements Subscription {
        private Subscriber<? super T> subscriber;
        private AtomicLong sent;
        private AtomicLong requested;
        private ExecutorService receiveExecutor;
        private AtomicBoolean finished;

        public KafkaSubscription(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
            this.sent = new AtomicLong();
            this.requested = new AtomicLong();
            this.receiveExecutor = Executors.newSingleThreadExecutor();
            this.finished = new AtomicBoolean(false);
            Runnable receiver = new Runnable() {

                @SuppressWarnings("unchecked")
                @Override
                public void run() {
                    consumer.subscribe(Arrays.asList(topic));
                    while (!finished.get()) {
                        try {
                            if (sent.get() < requested.get())  {
                                ConsumerRecords<String, T> records = consumer.poll(100);
                                records.forEach(record -> handleRecord(record));
                            } else {
                                synchronized (this) {
                                    try {
                                        wait(1000);
                                    } catch (InterruptedException e) {
                                        finished.set(true);
                                    }
                                }
                            }
                        } catch (RuntimeException e) {
                            subscriber.onError(e);
                        }

                    }
                    subscriber.onComplete();
                    consumer.close();
                }
            };
            this.receiveExecutor.submit(receiver);
        }

        @Override
        public void request(long n) {
            requested.addAndGet(n);
            synchronized (this) {
                notify();
            }
        }

        @SuppressWarnings("unchecked")
        private void handleRecord(ConsumerRecord<String, T> record) {
            System.out.println("Handling message " + record);
            if (type == ConsumerRecord.class) {
                subscriber.onNext((T)record);
            } else {
                subscriber.onNext((T)record.value());
            }
            consumer.commitAsync();
            sent.incrementAndGet();
        }

        @Override
        public void cancel() {
            finished.set(true);
        }

    }

}

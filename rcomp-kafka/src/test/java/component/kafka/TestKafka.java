package component.kafka;

import java.time.Duration;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings("rawtypes")
public class TestKafka {
    private static final String TOPIC1 = "topic1";
    private static final String TOPIC2 = "topic2";

    @Test
    public void testWithConsumerRecord() throws Exception {
        KafkaComponent kafka = createComponent();
        
        Mono<List<Object>> receive = Flux.from(kafka.from(TOPIC1, ConsumerRecord.class))
            .map(record -> record.value())
            .take(2)
            .collectList();

        Subscriber<ProducerRecord> toTopic = kafka.to(TOPIC1, ProducerRecord.class);
        Flux.just(new ProducerRecord<String, String>(TOPIC1, "1", "test"),
                  new ProducerRecord<String, String>(TOPIC1, "1", "test2"))
            .subscribe(toTopic);

        List<Object> received = receive.block(Duration.ofSeconds(10));
        Assert.assertEquals(2, received.size());
        Assert.assertEquals("test", received.get(0));
    }

    @Test
    public void testWithString() throws Exception {
        KafkaComponent kafka = createComponent();
        
        Mono<List<String>> receive = Flux.from(kafka.from(TOPIC2, String.class))
            .take(2)
            .collectList();
        
        Subscriber<ProducerRecord> toTopic = kafka.to(TOPIC2, ProducerRecord.class);
        Flux.just(new ProducerRecord<String, String>(TOPIC2, "1", "test"),
                  new ProducerRecord<String, String>(TOPIC2, "1", "test2"))
            .subscribe(toTopic);

        List<String> received = receive.block(Duration.ofSeconds(10));
        Assert.assertEquals(2, received.size());
        Assert.assertEquals("test", received.get(0));
        Assert.assertEquals("test2", received.get(1));
    }

    private KafkaComponent createComponent() {
        Dictionary<String, Object> configs = new Hashtable<>();
        configs.put("group.id", "group1");
        KafkaComponent kafka = new KafkaComponent(configs);
        return kafka;
    }
}

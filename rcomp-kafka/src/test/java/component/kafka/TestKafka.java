package component.kafka;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.reactivestreams.Subscriber;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TestKafka {
    Integer result = 0;

    @Test
    public void testMQtt() throws Exception {
        Dictionary<String, Object> configs = new Hashtable<>();
        configs.put("group.id", "group1");
        KafkaComponent kafka = new KafkaComponent(configs);
        Subscriber<ProducerRecord> toTopic = kafka.to("output", ProducerRecord.class);

        Mono.just(new ProducerRecord<String, String>("output", "1", "test"))
            .subscribe(toTopic);

        Disposable mv = Flux.from(kafka.from("output", ConsumerRecord.class))
            .map(record -> record.value()).subscribe(System.out::println);
        Thread.sleep(1000);
        mv.dispose();
        /*
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(ConfigMapper.map(configs, Type.Consumer));
        consumer.subscribe(Arrays.asList("output"));
        ConsumerRecords<String, String> received = consumer.poll(100);
        received.forEach(record -> System.out.println(record.value()));
        consumer.close();
        Assert.assertEquals(1, received.count());
        Assert.assertEquals("test", received.iterator().next().value());
        */
    }
}

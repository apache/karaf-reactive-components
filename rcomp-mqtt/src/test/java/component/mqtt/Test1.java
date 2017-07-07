package component.mqtt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

public class Test1 {
    Integer result = 0;

    @Test
    public void testMQtt() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId(), new MemoryPersistence());
        client.connect();
        MqttComponent mqtt = new MqttComponent();
        mqtt.client = client;
        Publisher<byte[]> fromTopic = mqtt.from("input", byte[].class);
        Subscriber<byte[]> toTopic = mqtt.to("output", byte[].class);
        Flux.from(fromTopic)
            .log()
            .subscribe(toTopic);
        
        client.subscribe("output", (topic, message) -> {
            result = new Integer(new String(message.getPayload()));
            latch.countDown();
        });
        client.publish("input", new MqttMessage(new Integer(2).toString().getBytes()));
        client.publish("input", new MqttMessage(new Integer(2).toString().getBytes()));
        latch.await(100, TimeUnit.SECONDS);
        Assert.assertEquals(2, result, 0.1);
        client.disconnect();
        client.close();
    }
}

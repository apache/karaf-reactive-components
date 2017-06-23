package component.mqtt;

import static java.time.Duration.of;

import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;
import reactor.math.MathFlux;
import reactortest.ByteArrayConverter;
import reactortest.DoubleConverter;

public class Test1 {
    double result = 0;

    @Test
    public void testStream() {
        Flux<Integer> flux = Flux.fromArray(new Integer[]{1,10,5,3,4});
        MathFlux.averageDouble(flux).subscribe(System.out::println);
    }
    
    @Test
    public void testSlidingWindow() throws InterruptedException {
        Flux.interval(of(1, ChronoUnit.MILLIS))
        .transform(averageOfLastTwo())
        .subscribe(System.out::println);
        Thread.sleep(100);
    }
    
    private Function<Flux<Long>, Flux<Double>> averageOfLastTwo() {
        return f -> f.window(2, 1)
            .flatMap(win -> MathFlux.averageDouble(win));
    }

    @Test
    public void testMQtt() throws Exception {
        MqttClient client = new MqttClient("tcp://192.168.0.126:1883", MqttClient.generateClientId());
        client.connect();
        MqttComponent mqtt = new MqttComponent();
        mqtt.client = client;
        Publisher<Integer> fromTopic = mqtt.from("input", ByteArrayConverter::asInteger);
        Consumer<Double> toTopic = mqtt.to("output", DoubleConverter::asByteAr);
        Flux.from(fromTopic)
            .log()
            .window(2, 1)
            .flatMap(win -> MathFlux.averageDouble(win))
            .log()
            .subscribe(toTopic);
        
        client.subscribe("output", (topic, message) -> {
            result = ByteArrayConverter.asDouble(message.getPayload());
        });
        client.publish("input", new MqttMessage(ByteArrayConverter.fromInteger(2)));
        client.publish("input", new MqttMessage(new Integer(2).toString().getBytes()));
        Thread.sleep(100);
        Assert.assertEquals(2, result, 0.1);
        client.disconnect();
        client.close();
    }
}

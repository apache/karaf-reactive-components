/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.rcomp.mqtt;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

public class MqttTest {
    private static final String MQTT_PORT = "9141";
    Integer result = 0;
    private BrokerService broker;
    
    @Before
    public void startBroker() throws Exception {
        broker = new BrokerService();
        broker.setPersistent(false);
        broker.addConnector("mqtt://localhost:" + MQTT_PORT);
        broker.start();
    }

    @Test
    public void testMQtt() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        MqttClient client = new MqttClient("tcp://localhost:" + MQTT_PORT, MqttClient.generateClientId(), new MemoryPersistence());
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
    
    @After
    public void stopBroker() throws Exception {
        broker.stop();
    }
}

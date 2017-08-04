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
package component.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MqttDestination<T> implements Subscriber<T>, AutoCloseable {
    
    private MqttClient client;
    private String topic;
    private Subscription subscription;

    public MqttDestination(MqttClient client, String topic, Class<? extends T> type) {
        this.client = client;
        this.topic = topic;
        if (! type.equals(byte[].class)) {
            throw new IllegalArgumentException("Curently only byte[] is supported");
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T payload) {
        try {
            MqttMessage message = new MqttMessage(convertTo(payload));
            this.client.publish(topic, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    private byte[] convertTo(T payload) {
        return (byte[])payload;
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

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
package org.apache.karaf.rcomp.kafka;

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
            ProducerRecord<?, ?> record = getProducerRecord(payload);
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

    private ProducerRecord<?, ?> getProducerRecord(T payload) {
        ProducerRecord<?, ?> record;
        if (payload instanceof ProducerRecord<?, ?>) {
            record = (ProducerRecord<?, ?>)payload;
        } else {
            // @TODO better way to determine key
            String key = "dummykey";
            record = new ProducerRecord<>(topic, key, payload);                
        }
        return record;
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onerr");
    }

    @Override
    public void onComplete() {
        this.producer.close();
    }

    @Override
    public void close() throws Exception {
        if (subscription != null) {
            subscription.cancel();
            subscription = null;
        }
        this.producer.close();
    }
}

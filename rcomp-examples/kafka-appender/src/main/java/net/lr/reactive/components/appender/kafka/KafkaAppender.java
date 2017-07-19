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
package net.lr.reactive.components.appender.kafka;

import java.util.Dictionary;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.karaf.decanter.api.marshaller.Marshaller;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.api.RComponent;
import reactor.core.publisher.Flux;

@Component(
    name = "appender.kafka",
    immediate = true,
    //configurationPolicy = ConfigurationPolicy.REQUIRE,
    property = EventConstants.EVENT_TOPIC + "=decanter/collect/*",
    service=KafkaAppender.class
)
@SuppressWarnings({
    "rawtypes"
   })
public class KafkaAppender {
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaAppender.class);
    private String topic;
    
    @Reference
    Marshaller marshaller;
    
    @Reference(target="(name=eventAdmin)")
    RComponent eventAdmin;
    
    @Reference(target="(name=kafka)")
    RComponent kafka;
    private Subscriber<ProducerRecord> toKafka;

    @Activate
    public void activate(ComponentContext context) {
        Dictionary<String, Object> config = context.getProperties();
        this.topic = (String)config.get("topic");
        if (topic == null) {
            throw new IllegalArgumentException("Config property topic must be present.");
        }
        String eventTopics = (String)config.get(EventConstants.EVENT_TOPIC);
        Publisher<Event> fromEventAdmin = eventAdmin.from(eventTopics, Event.class);
        toKafka = kafka.to(topic, ProducerRecord.class);
        org.slf4j.MDC.put("inLogAppender", "true");
        Flux.from(fromEventAdmin)
            .doOnEach(event -> org.slf4j.MDC.put("inLogAppender", "true"))
            //.log()
            .map(event->toRecord(event))
            .doOnError(ex -> LOGGER.error(ex.getMessage(), ex))
            .subscribe(toKafka);
        LOGGER.info("Kafka appender started. Listening on topic " + topic);
    }

    public ProducerRecord<String,String> toRecord(Event event) {
        String type = (String)event.getProperty("type");
        String data = marshaller.marshal(event);
        return new ProducerRecord<>(topic, type, data);
    }
    
    @Deactivate
    public void close() {
        toKafka.onComplete();
    }

}

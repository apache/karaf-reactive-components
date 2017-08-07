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

import java.util.HashSet;
import java.util.Set;

import org.apache.karaf.rcomp.api.RComponent;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

@Component(property="name=mqtt")
public class MqttComponent implements RComponent {
    
    MqttClient client;
    private Set<MqttDestination<?>> destinations = new HashSet<>();

    @ObjectClassDefinition(name = "MQTT config")
    @interface MqttConfig {
        String serverUrl() default "tcp://localhost:1883";
        String clientId();
    }
    
    @Activate
    public void activate(MqttConfig config) throws MqttException {
        client = new MqttClient(config.serverUrl(), MqttClient.generateClientId(),
                                new MemoryPersistence());
        client.connect();
    }
    
    @Deactivate
    public void deactivate() throws MqttException {
        for (MqttDestination<?> destination : destinations) {
            try {
                destination.close(); 
            } catch (Exception e) {
            }
        }
        client.disconnect();
        client.close();
    }

    @Override
    public <T> Publisher<T> from(String topic, Class<T> type) {
        return new MqttSource<T>(client, topic, type);
    }  
    
    @Override
    public <T> Subscriber<T> to(String topic, Class<T> type) {
        MqttDestination<T> destination = new MqttDestination<T>(client, topic, type);
        destinations.add(destination);
        return destination;
    }

}

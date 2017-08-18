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

import java.util.Dictionary;

import org.apache.karaf.rcomp.api.ProvComp;
import org.apache.karaf.rcomp.api.RComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

@ProvComp(name="kafka")
@Component(immediate = true, 
    configurationPolicy = ConfigurationPolicy.REQUIRE,
    property = "name=kafka"
)
public class KafkaComponent implements RComponent {
    Dictionary<String, Object> rawConfig;
    
    public KafkaComponent() {
    }
    
    public KafkaComponent(Dictionary<String, Object> rawConfig) {
        this.rawConfig = rawConfig;
    }

    @Activate
    public void activate(ComponentContext context) {
        this.rawConfig = context.getProperties();
    }
    
    @Deactivate
    public void close() {
    }

    @Override
    public <T> Publisher<T> from(String topic, Class<T> type) {
        return new KafkaSource<>(ConfigMapper.mapConsumer(rawConfig), topic, type);
    }

    @Override
    public <T> Subscriber<T> to(String topic, Class<T> type) {
        return new KafkaDestination<T>(ConfigMapper.mapProducer(rawConfig), topic, type);
    }

}

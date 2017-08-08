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
package org.apache.karaf.rcomp.examples.mqtt;

import java.util.function.Consumer;

import org.apache.karaf.rcomp.api.RComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Component(immediate=true)
public class MqttReceiver implements Consumer<Double>{
    Logger LOG = LoggerFactory.getLogger(MqttReceiver.class);
    
    @Reference(target="(name=mqtt)")
    RComponent mqtt;

    private Disposable flux;

    @Activate
    public void start() throws Exception {
        LOG.info("Starting mqtt receiver");
        Publisher<byte[]> fromTopic = mqtt.from("output", byte[].class);
        flux = Flux.from(fromTopic)
            .map(ByteArrayConverter::asDouble)
            .subscribe(this);
    }
    
    @Deactivate
    public void stop() {
        flux.dispose();
    }

    @Override
    public void accept(Double average) {
        LOG.info("Received average value of " + average);
    }

}
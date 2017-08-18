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

import org.apache.karaf.rcomp.api.RComponent;
import org.apache.karaf.rcomp.api.ReqComp;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.math.MathFlux;

@ReqComp("mqtt")
@Component(immediate=true)
public class MqttExample {
    Logger LOG = LoggerFactory.getLogger(MqttExample.class);
    
    @Reference(target="(name=mqtt)")
    RComponent mqtt;

    @Activate
    public void start() throws Exception {
        LOG.info("Starting mqtt test component2");
        Publisher<byte[]> fromTopic = mqtt.from("input", byte[].class);
        Subscriber<byte[]> toTopic = mqtt.to("output", byte[].class);
        Flux.from(fromTopic)
            .map(ByteArrayConverter::asInteger)
            .log()
            .window(2, 1)
            .flatMap(win -> MathFlux.averageDouble(win))
            .map(DoubleConverter::asByteAr)
            .subscribe(toTopic);
        LOG.info("mqtt test component started");
    }

}

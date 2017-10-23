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

import java.time.Duration;

import org.apache.karaf.rcomp.api.RComponent;
import org.apache.karaf.rcomp.api.ReqComp;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;

@ReqComp("mqtt")
@Component(immediate=true)
public class MqttEmitter {
    Logger LOG = LoggerFactory.getLogger(MqttEmitter.class);
    
    @Reference(target="(name=mqtt)")
    RComponent mqtt;

    private Subscriber<byte[]> toTopic;

    @Activate
    public void start() throws Exception {
        toTopic = mqtt.to("input", byte[].class);
        Flux.interval(Duration.ofSeconds(1))
            .map(ByteArrayConverter::fromLong)
            .subscribe(toTopic);
    }
    
    @Deactivate
    public void stop() throws Exception {
        toTopic.onComplete();
    }

}

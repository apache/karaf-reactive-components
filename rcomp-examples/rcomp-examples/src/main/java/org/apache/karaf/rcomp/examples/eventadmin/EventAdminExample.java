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
package org.apache.karaf.rcomp.examples.eventadmin;

import java.util.Map;

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

@ReqComp("eventAdmin")
@Component(immediate=true)
public class EventAdminExample {
    Logger LOG = LoggerFactory.getLogger(EventAdminExample.class);
    
    @Reference(target="(name=eventAdmin)")
    RComponent eventAdmin;

    @SuppressWarnings("rawtypes")
    @Activate
    public void start() throws Exception {
        Publisher<Map> fromTopic = eventAdmin.from("eainput", Map.class);
        Subscriber<Map> toTopic = eventAdmin.to("eaoutput", Map.class);
        Flux.from(fromTopic)
            .log()
            .subscribe(toTopic);
    }

}

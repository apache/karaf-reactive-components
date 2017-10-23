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
package org.apache.karaf.rcomp.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.core.osgi.OsgiServiceRegistry;
import org.apache.karaf.rcomp.api.CloseableSubscriber;
import org.apache.karaf.rcomp.api.CloseableSubscriberAdapter;
import org.apache.karaf.rcomp.api.ProvComp;
import org.apache.karaf.rcomp.api.RComponent;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.reactivestreams.Publisher;

@ProvComp(name = "camel")
@Component(property = "name=camel")
public class CamelComponent implements RComponent {

    private CamelContext camelContext;
    private CamelReactiveStreamsService camelreactive;
    
    @ObjectClassDefinition(name = "Camel config")
    @interface CamelConfig {
        
    }
    
    public CamelComponent() {
    }
    
    CamelComponent(CamelContext context) {
        this.camelContext = context;
        this.camelreactive = CamelReactiveStreams.get(camelContext);
    }
    
    @Activate
    public void activate(BundleContext context, CamelConfig config) throws Exception {
        this.camelContext = new OsgiDefaultCamelContext(context, new OsgiServiceRegistry(context));
        this.camelContext.start();
        this.camelreactive = CamelReactiveStreams.get(camelContext);
    }

    @Deactivate
    public void deactivate() throws Exception {
        this.camelContext.stop();
    }

    @Override
    public <T> Publisher<T> from(String topic, Class<T> type) {
        return camelreactive.from(topic, type);
    }

    @Override
    public <T> CloseableSubscriber<T> to(String topic, Class<T> type) {
        return new CloseableSubscriberAdapter<T>(camelreactive.subscriber(topic, type));
    }
}

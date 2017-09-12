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
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

public class CamelComponentTest {
    
    private CamelComponent camel;
    private CamelContext context;

    @Before
    public void init() throws Exception {
        context = new DefaultCamelContext();
        camel = new CamelComponent(context);
        context.start();
        
    }

    @Test
    public void testTwoMessages() throws Exception {
        MockEndpoint resultEndpoint = context.getEndpoint("mock:test", MockEndpoint.class);
        resultEndpoint.expectedBodiesReceived("test", "test2");
        Subscriber<String> subscriber = camel.to("mock:test", String.class);
        Flux.just("test", "test2").subscribe(subscriber);
        resultEndpoint.assertIsSatisfied();
    }

    @After
    public void stop() throws Exception {
        camel.deactivate();
    }
}

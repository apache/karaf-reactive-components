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
package org.apache.karaf.rcomp.eventadmin;

import org.apache.karaf.rcomp.api.RComponent;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventAdmin;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

@Component(property="name=eventAdmin")
public class EventAdminComponent implements RComponent {
    
    private BundleContext context;

    @Reference
    EventAdmin client;
    

    @Activate
    public void activate(BundleContext context) {
        this.context = context;
    }
    
    @Deactivate
    public void deactivate() {
    }

    @Override
    public <T> Publisher<T> from(String topic, Class<T> type) {
        return new EventAdminSource<T>(context, topic, type);
    }  
    
    @Override
    public <T> Subscriber<T> to(String topic, Class<T> type) {
        return new EventAdminDestination<T>(client, topic, type);
    }

}

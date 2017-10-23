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

import java.util.Map;

import org.apache.karaf.rcomp.api.CloseableSubscriber;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.reactivestreams.Subscription;

public class EventAdminDestination<T> implements CloseableSubscriber<T> {

    private EventAdmin client;
    private String topic;
    private Subscription subscription;
    private Class<T> type;

    public EventAdminDestination(EventAdmin client, String topic, Class<T> type) {
        this.client = client;
        this.topic = topic;
        if (!(type == Map.class || type==Event.class)) {
            throw new IllegalArgumentException("Curently only Map<String, ?> and Event are supported");
        }
        this.type = type;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T payload) {
        try {
            Event event = toEvent(payload);
            this.client.sendEvent(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    @SuppressWarnings("unchecked")
    private Event toEvent(T payload) {
        if (type == Event.class) {
            return (Event)payload;
        } else {
            return new Event(topic, (Map<String, ?>)payload);
        }
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void close() {
        this.subscription.cancel();
    }

}

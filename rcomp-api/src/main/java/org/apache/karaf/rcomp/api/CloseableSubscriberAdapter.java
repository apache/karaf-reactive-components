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
package org.apache.karaf.rcomp.api;

import java.io.Closeable;
import java.io.IOException;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Adapts an arbitrary subscriber implementing Closeable to CloseableSubscriber
 */
public class CloseableSubscriberAdapter<T> implements CloseableSubscriber<T> {
    
    private Subscriber<T> subscriber;

    public CloseableSubscriberAdapter(Subscriber<T> subscriber) {
//        if (!(subscriber instanceof Closeable)) {
//            throw new IllegalArgumentException("Subscriber must implement Closeable");
//        }
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription s) {
        subscriber.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        subscriber.onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        subscriber.onError(t);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    @Override
    public void close() throws IOException {
        if (subscriber instanceof Closeable) {
            ((Closeable)subscriber).close();
        }
    }
    
}
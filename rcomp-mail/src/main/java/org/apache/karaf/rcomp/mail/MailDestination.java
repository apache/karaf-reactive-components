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
package org.apache.karaf.rcomp.mail;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.karaf.rcomp.api.CloseableSubscriber;
import org.reactivestreams.Subscription;

public class MailDestination<T> implements CloseableSubscriber<T> {

    private String destination;
    private Subscription subscription;

    public MailDestination(String destination, Class<? extends T> type) {
        this.destination = destination;
        if (! type.equals(MimeMessage.class)) {
            throw new IllegalArgumentException("Curently only MimeMessage is supported");
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T message) {
        try {
            Address[] addresses = new Address[]{new InternetAddress(destination)};
            Transport.send(convertTo(message), addresses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    private Message convertTo(T message) {
        return (Message) message;
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void close() {
        subscription.cancel();
    }

}

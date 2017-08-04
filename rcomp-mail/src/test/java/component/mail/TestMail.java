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
package component.mail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Ignore;
import org.junit.Test;
import org.reactivestreams.Subscriber;

import reactor.core.publisher.Flux;

public class TestMail {
    double result = 0;

    @Ignore
    @Test
    public void testMail() throws Exception {
        Map<String, Object> config = new HashMap<>();
        config.put("mail.smtp.host", "localhost");
        Session session = SessionComponent.create(config);
        MailComponent mail = new MailComponent();
        mail.session = session;

        Subscriber<MimeMessage> to = mail.to("cschneider@localhost", MimeMessage.class);
        Flux.just("Test").map(txt -> createMessage(session, txt)).subscribe(to);
    }

    private MimeMessage createMessage(Session session, String body) {
        try {
            MimeMessage mesage = new MimeMessage(session);
            mesage.setSubject("Test Mail");
            mesage.setText(body);
            return mesage;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
}

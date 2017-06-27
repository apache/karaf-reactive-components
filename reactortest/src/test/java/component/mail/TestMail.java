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

        Subscriber<String> to = mail.to("cschneider@localhost", txt -> createMessage(session, txt));
        Flux.just("Test").subscribe(to);
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

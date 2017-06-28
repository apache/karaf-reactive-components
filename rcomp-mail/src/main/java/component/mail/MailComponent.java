package component.mail;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import component.api.MComponent;

@Component(property="name=mail")
public class MailComponent implements MComponent<MimeMessage> {
    
    @Reference
    Session session;

    @Override
    public Publisher<MimeMessage> from(String topic) {
        throw new RuntimeException();
    }  
    
    @Override
    public Subscriber<MimeMessage> to(String destination) {
        return new MailDestination(destination);
    }

}

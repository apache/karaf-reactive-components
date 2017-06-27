package component.mail;

import java.util.function.Function;

import javax.mail.MethodNotSupportedException;
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
    public <T> Publisher<T> from(String topic, Function<MimeMessage, T> converter) throws Exception {
        throw new MethodNotSupportedException();
    }  
    
    @Override
    public <T> Subscriber<T> to(String destination, Function<T, MimeMessage> converter) throws Exception {
        return new MailDestination<T>(destination, converter);
    }

}

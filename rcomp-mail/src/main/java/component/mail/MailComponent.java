package component.mail;

import javax.mail.Session;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import component.api.RComponent;

@Component(property="name=mail")
public class MailComponent implements RComponent {
    
    @Reference
    Session session;

    @Override
    public <T> Publisher<T> from(String topic, Class<T> type) {
        throw new RuntimeException();
    }  
    
    @Override
    public <T> Subscriber<T> to(String destination, Class<T> type) {
        return new MailDestination<T>(destination, type);
    }

}

package component.mail;

import java.util.function.Function;

import javax.mail.Address;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MailDestination<T> implements Subscriber<T> {

    private String destination;
    private Function<T, MimeMessage> converter;
    private Subscription subscription;

    public MailDestination(String destination, Function<T, MimeMessage> converter) {
        this.destination = destination;
        this.converter = converter;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(T payload) {
        try {
            MimeMessage message = converter.apply(payload);
            Address[] addresses = new Address[]{new InternetAddress(destination)};
            Transport.send(message, addresses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            subscription.request(1);
        }
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onComplete() {
    }

}

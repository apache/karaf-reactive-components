package component.mail;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MailDestination<T> implements Subscriber<T> {

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

}

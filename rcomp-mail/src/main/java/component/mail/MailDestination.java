package component.mail;

import javax.mail.Address;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MailDestination implements Subscriber<MimeMessage> {

    private String destination;
    private Subscription subscription;

    public MailDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(MimeMessage message) {
        try {
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

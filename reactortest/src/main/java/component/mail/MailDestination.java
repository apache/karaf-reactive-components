package component.mail;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.mail.Address;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailDestination<T> implements Consumer<T> {

    private String destination;
    private Function<T, MimeMessage> converter;

    public MailDestination(String destination, Function<T, MimeMessage> converter) {
        this.destination = destination;
        this.converter = converter;
    }

    @Override
    public void accept(T payload) {
        try {
            MimeMessage message = converter.apply(payload);
            Address[] addresses = new Address[]{new InternetAddress(destination)};
            Transport.send(message, addresses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

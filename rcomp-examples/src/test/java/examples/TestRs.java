package examples;

import static reactor.core.publisher.Mono.fromCompletionStage;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;

import reactor.core.publisher.Mono;

public class TestRs {

    @Test
    public void testGet() throws Exception {
        Server server = createServer();
        server.start();
        WebTarget client1 = ClientBuilder.newClient().target("http://localhost:8384/Hello");
        WebTarget client2 = ClientBuilder.newClient().target("http://localhost:8384/World");
        Mono<String> get1 = fromCompletionStage(client1.request().rx().get(String.class));
        Mono<String> get2 = fromCompletionStage(client2.request().rx().get(String.class));
        List<String> result = Mono
            .from(get1)
            .concatWith(get2)
            .doOnError(ex -> ex.printStackTrace())
            .collectList()
            .block(Duration.ofMillis(1500));
        String resultSt = result.stream().collect(Collectors.joining(" "));
        org.junit.Assert.assertEquals("Hello World", resultSt);
        server.stop();
    }

    private Server createServer() {
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8384);
        server.addConnector(connector);

        ServletHandler handler = new ServletHandler();
        Servlet servlet = new HttpServlet() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
                String path = req.getServletPath().substring(1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                resp.getWriter().append(path);
            }
            
        };
        handler.addServletWithMapping(new ServletHolder(servlet), "/");
        server.setHandler(handler);
        return server;
    }
    
}

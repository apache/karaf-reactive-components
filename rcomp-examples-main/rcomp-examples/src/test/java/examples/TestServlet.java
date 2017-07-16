package examples;

import java.io.IOException;
import java.time.Duration;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;

import reactor.core.publisher.Mono;

public class TestServlet {

    @Test
    public void testServlet() throws Exception {
        Server server = createServer();
        server.start();
        WebTarget client = ClientBuilder.newClient().target("http://localhost:8384/Hello");
        String result = client.request().get(String.class);
        Assert.assertEquals("/Hello", result);
        server.stop();
    }
    
    private final class TestAsyncServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            final AsyncContext ctx = req.startAsync(req, resp);
            Mono.just(req.getServletPath())
               .delayElement(Duration.ofMillis(2000))
               .doOnSuccess(result -> writeResult(ctx, result))
               .log()
               .subscribe();
            System.out.println("Returning from servlet. Request is handled asynchronously");
        }

        private void writeResult(AsyncContext ctx, String result) {
            try {
                ctx.getResponse().getWriter().append(result);
                ctx.complete();
            } catch (IOException e) {

            }
        }
    }
    
    
    private Server createServer() {
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8384);
        server.addConnector(connector);

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new TestAsyncServlet()), "/");
        server.setHandler(handler);
        return server;
    }
}

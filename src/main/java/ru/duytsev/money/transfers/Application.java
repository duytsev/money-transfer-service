package ru.duytsev.money.transfers;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

@Slf4j
public class Application {

    private static Server server;

    public static void start(int port) throws Exception {
        server = new Server(port);

        ServletContextHandler servletContextHandler = new ServletContextHandler(NO_SESSIONS);

        servletContextHandler.setContextPath("/");
        servletContextHandler.setErrorHandler(new ErrorHandler());
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/api/*");
        servletHolder.setInitOrder(0);
        servletHolder.setInitParameter(
                "jersey.config.server.provider.packages",
                "ru.duytsev.money.transfers"
        );
        server.start();
    }

    public static void stop() throws Exception {
        if (server != null) {
            server.stop();
            server.join();
            server.destroy();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            start(Integer.parseInt(args[0]));
            server.join();
        } catch (Exception e) {
            log.error("Error while starting server", e);
            System.exit(1);
        } finally {
            stop();
        }
    }

    static class ErrorHandler extends ErrorPageErrorHandler {
        @Override
        public void handle(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.getWriter()
                    .append("{\"message\":\"HTTP error ")
                    .append(String.valueOf(response.getStatus()))
                    .append("\"}");
        }
    }
}


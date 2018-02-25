package com.decora.http;

import com.decora.http.resource.ResourcePath;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ExceptionHandler;
import io.undertow.servlet.api.ServletContainer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.UnhandledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static io.undertow.Handlers.path;

public class HTTPService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPService.class);

    private final GracefulShutdownHandler gracefulShutdownHandler;

    private final String hostname;

    private final int port;

    private final Undertow undertow;

    public HTTPService(final String theHostname,
                       final int thePort,
                       final DecoraWSApplication application) throws ServletException {

        final PathHandler pathHandler = path();

        final ServletContainer container = ServletContainer.Factory.newInstance();

        final ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setSecurityEnabled(true);
        deployment.setApplication(application);

        final DeploymentInfo deploymentInfo = new UndertowJaxrsServer().undertowDeployment(deployment);
        deploymentInfo.setClassLoader(application.getClass().getClassLoader());
        deploymentInfo.setDeploymentName("DecoraWS Deployment").setContextPath(ResourcePath.ROOT);
        deploymentInfo.setExceptionHandler(new HTTPServiceExceptionHandler());

        final DeploymentManager deploymentManager = container.addDeployment(deploymentInfo);
        deploymentManager.deploy();
        final HttpHandler jaxRSHandler = deploymentManager.start();

        pathHandler.addPrefixPath(deploymentInfo.getContextPath(), jaxRSHandler);

        gracefulShutdownHandler = Handlers.gracefulShutdown(pathHandler);

        undertow = Undertow.builder()
                .addHttpListener(thePort, theHostname)
                .setHandler(gracefulShutdownHandler)
                .build();

        hostname = theHostname;
        port = thePort;
    }

    public final void start() {
        LOGGER.info("Starting...");
        try {
            undertow.start();
        } catch (RuntimeException e) {
            // Force termination of XNIO threads
            stop();
            throw e;
        }
        LOGGER.info("Started at {}:{}", hostname, port);
    }

    public final void stop() {
        gracefulShutdownHandler.shutdown();
        try {
            gracefulShutdownHandler.awaitShutdown();
        } catch (InterruptedException e) {
            LOGGER.warn("Shutdown interrupted", e);
            Thread.currentThread().interrupt();
        }
        undertow.stop();
    }

    private static class HTTPServiceExceptionHandler implements ExceptionHandler {
        @Override
        public boolean handleThrowable(final HttpServerExchange exchange, final ServletRequest request, final ServletResponse response, final Throwable throwable) {
            if (throwable instanceof UnhandledException && ExceptionUtils.getRootCause(throwable) instanceof IOException) {
                LOGGER.warn("Error sending response to {}:{} - {}", request.getRemoteHost(), request.getRemotePort(), throwable.getCause().getMessage());
                return true;
            }
            return false;
        }
    }
}

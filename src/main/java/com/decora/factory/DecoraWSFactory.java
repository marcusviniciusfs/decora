package com.decora.factory;

import com.decora.DecoraWS;
import com.decora.DecoraWSException;
import com.decora.http.HTTPService;
import com.decora.http.DecoraWSApplication;
import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.configuration.ServerConfiguration;
import com.decora.query.FindServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.concurrent.Future;

public final class DecoraWSFactory {

    private DecoraWSFactory() {
    }

    public static DecoraWS create(final DatabaseService databaseService) {
        return new DecoraWSImpl(databaseService);
    }
}

class DecoraWSImpl implements DecoraWS {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecoraWS.class);

    private static final String MESSAGE_ERROR_STARTING = "Error starting ";

    private static final String MESSAGE_ERROR_STOPPING = "Error stopping ";

    private DatabaseService databaseService;
    private final StateMachine stateMachine;
    private HTTPService httpService;
    private ServerConfiguration serverConfiguration;

    DecoraWSImpl(final DatabaseService theDatabaseService) {
        LOGGER.info("Version: {}", getVersion());

        databaseService = theDatabaseService;
        stateMachine = new StateMachine(this);
    }

    @Override
    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    @Override
    public Future<Boolean> start() {
        return stateMachine.change(StateMachine.Command.START);
    }

    final void doStart() throws DecoraWSException {
        LOGGER.info("Starting...");

        startHTTPService();
    }

    private void startHTTPService() throws DecoraWSException {
        try {
            try {
                serverConfiguration = databaseService.submit(new FindServerConfiguration());
            } catch (TransactionException e) {
                throw new DecoraWSException("Error finding server configuration", e);
            }

            httpService = new HTTPService(
                        serverConfiguration.getHttpHostname(),
                        serverConfiguration.getHttpPort(),
                        new DecoraWSApplication(this, serverConfiguration));

        } catch (ServletException e) {
            throw new DecoraWSException(MESSAGE_ERROR_STARTING + HTTPService.class.getSimpleName(), e);
        }
        try {
            httpService.start();
        } catch (RuntimeException e) {
            throw new DecoraWSException(MESSAGE_ERROR_STARTING + HTTPService.class.getSimpleName(), e);
        }
    }

    @Override
    public Future<Boolean> restart() {
        return stateMachine.change(StateMachine.Command.RESTART);
    }

    final void doRestart() throws DecoraWSException {
        LOGGER.info("Restarting...");
        final long beginTime = System.currentTimeMillis();

        doStop();
        doStart();

        final long finishTime = System.currentTimeMillis() - beginTime;
        LOGGER.info("Restart took {} millisecs", finishTime);
    }

    public Future<Boolean> stop() {
        return stateMachine.change(StateMachine.Command.STOP);
    }

    final void doStop() {
        LOGGER.info("Stopping...");

        if (httpService != null) {
            httpService.stop();
        }

        LOGGER.info("Stopped");
    }

    @Override
    public String getVersion() {
        final String version = DecoraWS.class.getPackage().getImplementationVersion();
        if (version == null) {
            return "unknown";
        }
        return version;
    }

    @Override
    public State getState() {
        return stateMachine.state();
    }
}

package com.decora;

import com.decora.persistence.database.DatabaseService;

import java.util.concurrent.Future;

public interface DecoraWS {

    enum State {
        STARTING,
        STARTED,
        RESTARTING,
        STOPPING,
        STOPPED
    }

    DatabaseService getDatabaseService();

    State getState();

    String getVersion();

    Future<Boolean> start();

    Future<Boolean> restart();

    Future<Boolean> stop();
}

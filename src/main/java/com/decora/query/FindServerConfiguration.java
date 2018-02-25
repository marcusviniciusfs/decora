package com.decora.query;

import com.decora.persistence.database.Transaction;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.configuration.ServerConfiguration;

import javax.persistence.EntityManager;

public class FindServerConfiguration implements Transaction<ServerConfiguration> {

    @Override
    public final ServerConfiguration execute(final EntityManager entityManager) throws TransactionException {
        return entityManager.createNamedQuery(ServerConfiguration.FIND_SERVER_CONFIGURATION, ServerConfiguration .class).getSingleResult();
    }
}

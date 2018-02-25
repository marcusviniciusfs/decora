package com.decora.persistence.database;

import com.decora.persistence.database.exception.DatabaseException;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.util.OrderedProperties;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class DatabaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

    private EntityManagerFactory entityManagerFactory;

    private Integer defaultTransactionTimeout;

    public DatabaseService(final String persistenceUnitName, final File propertiesFile) throws DatabaseException {

        final OrderedProperties properties = new OrderedProperties();
        try (final FileInputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new DatabaseException(e);
        }

        start(persistenceUnitName, properties);
    }

    public DatabaseService(final String persistenceUnitName, final Properties properties) throws DatabaseException {
        start(persistenceUnitName, properties);
    }

    public void close() {
        entityManagerFactory.close();
    }

    public static <T> T getSingleResultOrNull(final TypedQuery<T> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace(e.getMessage(), e);
            return null;
        }
    }

    public Integer getDefaultTransactionTimeout() {
        return defaultTransactionTimeout;
    }

    public void setDefaultTransactionTimeout(final Integer seconds) {
        defaultTransactionTimeout = seconds;
    }

    private void start(final String persistenceUnitName, final Properties properties) throws DatabaseException {
        LOGGER.info("Connecting to database {} ...", properties.getProperty("hibernate.connection.url"));
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
        } catch (RuntimeException e) {
            // TODO Find a way to terminate the non-daemon threads
            throw new DatabaseException(e);
        }
        LOGGER.info("Connected");
    }

    public <T> T submit(final Transaction<T> transaction) throws TransactionException {
        return submit(transaction, getDefaultTransactionTimeout());
    }

    public <T> T submit(final Transaction<T> transaction, final Integer timeoutInSeconds) throws TransactionException {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        if (null != timeoutInSeconds) {
            final Session session = entityManager.unwrap(Session.class);
            session.getTransaction().setTimeout(timeoutInSeconds);
        }
        try {
            final EntityTransaction entityTransaction = entityManager.getTransaction();
            try {
                entityTransaction.begin();
                final T t = transaction.execute(entityManager);
                entityTransaction.commit();
                return t;
            } catch (PersistenceException e) {
                throw new TransactionException(e);
            } finally {
                if (entityTransaction.isActive()) {
                    entityTransaction.rollback();
                }
            }
        } finally {
            entityManager.close();
        }
    }
}

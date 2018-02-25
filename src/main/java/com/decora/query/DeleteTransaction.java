package com.decora.query;

import com.decora.persistence.database.Transaction;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.AbstractEntity;

import javax.persistence.EntityManager;

public class DeleteTransaction<T extends AbstractEntity> implements Transaction<T> {

    private final Class<T> entityClass;

    private final int id;

    public DeleteTransaction(final Class<T> theEntityClass, final int entityId) {
        entityClass = theEntityClass;
        id = entityId;
    }

    @Override
    public final T execute(final EntityManager entityManager) throws TransactionException {
        final RetrieveTransaction<T> transaction = new RetrieveTransaction<>(entityClass, id);
        final T managedEntity = transaction.execute(entityManager);
        if (managedEntity != null) {
            entityManager.remove(managedEntity);
        }
        return managedEntity;
    }
}

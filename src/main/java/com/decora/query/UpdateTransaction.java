package com.decora.query;

import com.decora.persistence.database.Transaction;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.AbstractEntity;

import javax.persistence.EntityManager;

public class UpdateTransaction<T extends AbstractEntity> implements Transaction<T> {

    private final Class<T> entityClass;

    private final T t;

    public UpdateTransaction(final Class<T> theEntityClass, final T entity) {
        entityClass = theEntityClass;
        t = entity;
    }

    @Override
    public final T execute(final EntityManager entityManager) throws TransactionException {
        final RetrieveTransaction<T> transaction = new RetrieveTransaction<>(entityClass, t.getId());
        try {
            final T managedEntity = transaction.execute(entityManager);
            if (managedEntity != null) {
                managedEntity.copy(t);
            }
            return managedEntity;
        } catch (ReflectiveOperationException e) {
            throw new TransactionException(e);
        }
    }
}

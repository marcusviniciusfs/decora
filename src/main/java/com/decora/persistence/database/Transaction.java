package com.decora.persistence.database;

import com.decora.persistence.database.exception.TransactionException;

import javax.persistence.EntityManager;

public interface Transaction<T> {
    T execute(EntityManager entityManager) throws TransactionException;
}

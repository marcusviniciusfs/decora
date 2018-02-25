package com.decora.query;

import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.database.Transaction;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.user.SystemUser;

import javax.persistence.EntityManager;

public class FindSystemUser implements Transaction<SystemUser> {

    private String email;

    public FindSystemUser(final String theEmail) {
        email = theEmail;
    }

    @Override
    public final SystemUser execute(final EntityManager entityManager) throws TransactionException {
        return DatabaseService.getSingleResultOrNull(entityManager.createNamedQuery(SystemUser.FIND_BY_EMAIL, SystemUser.class).setParameter("email", email));
    }
}

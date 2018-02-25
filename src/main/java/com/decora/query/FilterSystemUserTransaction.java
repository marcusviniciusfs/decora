package com.decora.query;

import com.decora.persistence.database.Transaction;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.user.SystemUser;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilterSystemUserTransaction implements Transaction<List<SystemUser>> {

    private static final Locale LOCALE = Locale.getDefault();

    private final String name;
    private final String email;
    private final String address;
    private final String phone;
    private final String orderBy;

    private final int startpage;
    private final int pageSize;

    public FilterSystemUserTransaction(final String theName, final String theEmail,
                                       final String theAddress, final String thePhone,
                                       final String theOrderBy, final int theStartPage, final int thePageSize) {
        name = theName;
        email = theEmail;
        address = theAddress;
        phone = thePhone;
        orderBy = theOrderBy;

        startpage = theStartPage;
        pageSize = thePageSize;
    }

    @Override
    public final List<SystemUser> execute(final EntityManager entityManager) throws TransactionException {
        try {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(SystemUser.class);
            final List<Predicate> predicates = new ArrayList<>();

            buildQuery(criteriaBuilder, criteriaQuery, predicates);
            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

            TypedQuery<SystemUser> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setFirstResult(startpage);
            typedQuery.setMaxResults(pageSize);

            final List systemUserList = typedQuery.getResultList();

             return systemUserList;
        } catch (NoResultException e) {
            return null;
        }
    }

    private void buildQuery(final CriteriaBuilder criteriaBuilder, final CriteriaQuery criteriaQuery, final List<Predicate> predicates) {
        final Root<SystemUser> systemUser = criteriaQuery.from(SystemUser.class);
        criteriaQuery.select(systemUser);

        if (orderBy != null && !orderBy.isEmpty()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(systemUser.get(orderBy)));
        }

        if (name != null && !name.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(systemUser.<String>get("name")), "%" + name.toLowerCase(LOCALE) + "%"));
        }

        if (email != null && !email.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(systemUser.<String>get("email")), "%" + email.toLowerCase(LOCALE) + "%"));
        }

        if (address != null && !address.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(systemUser.<String>get("address")), "%" + address.toLowerCase(LOCALE) + "%"));
        }

        if (phone != null && !phone.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(systemUser.<String>get("phone")), "%" + phone.toLowerCase(LOCALE) + "%"));
        }
    }
}

package com.decora.http.resource;

import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.AbstractEntity;
import com.decora.query.RetrieveListTransaction;
import com.decora.query.RetrieveTransaction;
import org.apache.commons.lang3.exception.ExceptionUtils;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
public class AbstractReadOnlyEntityService<T extends AbstractEntity> {

    private final DatabaseService databaseService;

    private final Class<T> entityClass;

    public AbstractReadOnlyEntityService(final Class<T> theEntityClass, final DatabaseService service) {
        entityClass = theEntityClass;
        databaseService = service;
    }

    protected final DatabaseService getDatabaseService() {
        return databaseService;
    }

    protected final Class<T> getEntityClass() {
        return entityClass;
    }

    @GET
    @Path("{id}")
    public final T retrieve(@PathParam("id") final int id) {
        try {
            final T t = getDatabaseService().submit(new RetrieveTransaction<>(getEntityClass(), id));
            if (t == null) {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());
            }
            return t;
        } catch (TransactionException e) {
            final Throwable rootCause = ExceptionUtils.getRootCause(e);
            throw new WebApplicationException(rootCause, Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(rootCause.getMessage()).build());
        }
    }

    @GET
    public final List<T> retrieveList() {
        try {
            return getDatabaseService().submit(new RetrieveListTransaction<T>(getEntityClass()));
        } catch (TransactionException e) {
            final Throwable rootCause = ExceptionUtils.getRootCause(e);
            throw new WebApplicationException(rootCause, Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(rootCause.getMessage()).build());
        }
    }
}

package com.decora.http.resource.filter;

import com.decora.http.resource.Permissions;
import com.decora.http.resource.ResourcePath;
import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.user.SystemUser;
import com.decora.query.FilterSystemUserTransaction;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RolesAllowed({Permissions.SEARCH, Permissions.ADMIN})
@Path(ResourcePath.FILTER_SYSTEM_USER)
public class FilterSystemUserService {

    private final DatabaseService databaseService;

    public FilterSystemUserService(final DatabaseService theDatabaseService) {
        databaseService = theDatabaseService;
    }

    @GET
    @Path(ResourcePath.FIND_BY)
    @Produces(MediaType.APPLICATION_JSON)
    public final Response findBy(@QueryParam("name") final String theName, @QueryParam("email") final String theEmail,
                                         @QueryParam("address") final String theAddress, @QueryParam("phone") final String thePhone,
                                         @QueryParam("orderBy") final String orderBY, @QueryParam("startPage") final int startPage, @QueryParam("pageSize") final int pageSize) {

        try {
            List<SystemUser> systemUserList = databaseService.submit(new FilterSystemUserTransaction(theName, theEmail, theAddress, thePhone, orderBY, startPage, pageSize));
            return Response.status(Response.Status.OK).entity(systemUserList).build();
        } catch (TransactionException e) {
            final Throwable rootCause = ExceptionUtils.getRootCause(e);
            throw new WebApplicationException(rootCause, Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(rootCause.getMessage()).build());
        }
    }

}

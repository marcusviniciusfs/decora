package com.decora.http.resource.systemuser;

import com.decora.http.resource.AbstractEntityService;
import com.decora.http.resource.ResourcePath;
import com.decora.http.resource.Permissions;
import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.database.exception.TransactionException;
import com.decora.persistence.model.user.SystemUser;
import com.decora.query.CreateTransaction;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.Base64;

@Path(ResourcePath.SERVICE_SYSTEM_USER)
@RolesAllowed({Permissions.ADMIN})
@Consumes(MediaType.APPLICATION_JSON)
public class SystemUserService extends AbstractEntityService<SystemUser> {

    public SystemUserService(final DatabaseService service) {
        super(SystemUser.class, service);
    }

    @Override
    @POST
    public final SystemUser create(@Valid @NotNull final SystemUser systemUser) {
        try {
            byte[] password = systemUser.getPassword().getBytes(Charset.forName("UTF-8"));
            systemUser.setPassword(Base64.getEncoder().encodeToString(password));
            return getDatabaseService().submit(new CreateTransaction<SystemUser>(systemUser));
        } catch (TransactionException e) {
            final Throwable rootCause = ExceptionUtils.getRootCause(e);
            throw new WebApplicationException(rootCause, Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(rootCause.getMessage()).build());
        }
    }
}


package com.decora.http.resource.authentication;

import com.decora.DecoraWSException;
import com.decora.authentication.AuthenticationManager;
import com.decora.http.resource.ResourcePath;
import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.model.user.SystemUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@PermitAll
@Path(ResourcePath.AUTHENTICATION)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authentication;
    private final DatabaseService databaseService;

    public AuthenticationService(final DatabaseService theDatabaseService) {
        databaseService = theDatabaseService;
        authentication = new AuthenticationManager(databaseService);
    }

    @GET
    @Path(ResourcePath.LOGIN + "{email}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public final Response login(@PathParam("email") final String email, @PathParam("password") final String password) {

        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            try {
                SystemUser systemUser = authentication.login(email, password);
                if (systemUser != null) {
                    LOGGER.debug("/auth/authentication response: 200 - OK");
                    return Response.ok().entity(systemUser).build();
                } else {
                    LOGGER.debug("/auth/authentication response: 401 - Unauthorized");
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } catch (DecoraWSException e) {
                LOGGER.error("Server error:", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            LOGGER.debug("/auth/authentication response: 401 - Unauthorized");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}

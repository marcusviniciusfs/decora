package com.decora.http.filters;

import com.decora.authentication.AuthenticationManager;
import com.decora.http.resource.ResourcePath;
import com.decora.persistence.database.DatabaseService;
import com.decora.persistence.model.user.SystemUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Base64;
import java.util.List;

/**
 * @author marcus.vinicius
 */
@Provider
@PreMatching
public class Filter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Filter.class);

    private static final String ROLES = "X-Roles";

    private final AuthenticationManager authentication;
    private final DatabaseService databaseService;

    public Filter(final DatabaseService theDatabaseService) {
        databaseService = theDatabaseService;
        authentication = new AuthenticationManager(databaseService);
    }

    @Override
    public final void filter(final ContainerRequestContext requestContext) throws IOException {

        if (requestContext.getUriInfo().getPath().contains(ResourcePath.SERVICE_SYSTEM_USER)
                || requestContext.getUriInfo().getPath().contains(ResourcePath.FILTER_SYSTEM_USER)) {

            String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            SystemUser user;

            if (authorization != null && !authorization.isEmpty()) {
                LOGGER.debug("Validating Basic Authentication");
                try {
                    authorization = authorization.replace("Basic ", "");
                    authorization = new String(Base64.getDecoder().decode(authorization.getBytes(Charset.forName("UTF-8"))), Charset.forName("UTF-8"));
                    StringTokenizer stringTokenizer = new StringTokenizer(authorization, ":");
                    String login = stringTokenizer.nextToken();
                    String password = stringTokenizer.nextToken();

                    user = authentication.login(login, password);
                    if (user == null) {
                        LOGGER.warn("User does not exist");
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity("Authentication failed").build());
                    }

                    final List<String> permissions = Arrays.asList(requestContext.getHeaderString(ROLES).split("/"));
                    final SecurityContext supressedSecurityContext = requestContext.getSecurityContext();
                    requestContext.setSecurityContext(new CustomSecurityContext(supressedSecurityContext, permissions));
                } catch (Exception e) {
                    LOGGER.warn("Error validating Basic Authentication", e);
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity("Authentication failed").build());
                }
            } else {
                LOGGER.warn("No external authentication provided");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).entity("Authentication failed").build());
            }
        }
    }

    private static final class CustomSecurityContext implements SecurityContext {

        private final SecurityContext supressedSecurityContext;

        private List<String> permissions;

        private CustomSecurityContext(final SecurityContext theSupressedSecurityContext, final List<String> thePermissions) {
            supressedSecurityContext = theSupressedSecurityContext;
            permissions = thePermissions;
        }

        @Override
        public Principal getUserPrincipal() {
            return supressedSecurityContext.getUserPrincipal();
        }

        @Override
        public boolean isUserInRole(final String role) {
            return permissions.contains(role);
        }

        @Override
        public boolean isSecure() {
            return supressedSecurityContext.isSecure();
        }

        @Override
        public String getAuthenticationScheme() {
            return supressedSecurityContext.getAuthenticationScheme();
        }
    }
}

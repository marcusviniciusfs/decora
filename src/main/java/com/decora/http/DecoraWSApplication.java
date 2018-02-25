package com.decora.http;

import com.decora.DecoraWS;
import com.decora.http.filters.Filter;
import com.decora.http.resource.authentication.AuthenticationService;
import com.decora.http.resource.filter.FilterSystemUserService;
import com.decora.http.resource.systemuser.SystemUserService;
import com.decora.http.resource.ResourcePath;
import com.decora.persistence.model.configuration.ServerConfiguration;
import com.decora.util.RESTEasyJodaModuleProvider;
import io.swagger.jaxrs.config.BeanConfig;
import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class DecoraWSApplication extends Application {

    private final DecoraWS decoraWS;

    public DecoraWSApplication(final DecoraWS theDecoraWS, final ServerConfiguration serverConfiguration) {

        decoraWS = theDecoraWS;

        final BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(decoraWS.getVersion());
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost(serverConfiguration.getHttpHostname() + ":" + serverConfiguration.getHttpPort());
        beanConfig.setBasePath(ResourcePath.ROOT);
        beanConfig.setResourcePackage(this.getClass().getPackage().getName());
        beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);
    }

    @Override
    public final Set<Class<?>> getClasses() {
        final Set<Class<?>> set = new HashSet();
        set.add(RESTEasyJodaModuleProvider.class);
        set.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        set.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        set.add(WebApplicationExceptionMapper.class);
        set.add(RoleBasedSecurityFeature.class);
        return set;
    }

    @Override
    public final Set<Object> getSingletons() {
        final Set<Object> set = new HashSet<>();

        set.add(new SystemUserService(decoraWS.getDatabaseService()));
        set.add(new AuthenticationService(decoraWS.getDatabaseService()));
        set.add(new FilterSystemUserService(decoraWS.getDatabaseService()));
        set.add(new Filter(decoraWS.getDatabaseService()));

        return set;
    }
}

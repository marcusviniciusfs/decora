package com.decora.persistence.model.configuration;

import com.decora.persistence.model.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Size;

@Entity
@NamedQueries({
        @NamedQuery(
                name = ServerConfiguration.FIND_SERVER_CONFIGURATION,
                query = "from ServerConfiguration where id = 1"
        )
})
public class ServerConfiguration extends AbstractEntity {

    public static final String FIND_SERVER_CONFIGURATION = "ServerConfiguration.findServerConfiguration";

    @Size(min = 1, max = 255)
    private String httpHostname;

    @Size(min = 1, max = 255)
    private String serverDescription;

    private int httpPort;

    protected ServerConfiguration() {
    }

    public ServerConfiguration(final String theHttpHostname, final Integer theHttpPort) {
        setHostname(theHttpHostname);
        setHttpPort(theHttpPort);
    }

    public final String getHttpHostname() {
        return httpHostname;
    }

    public final void setHostname(final String theHttpHostname) {
        httpHostname = theHttpHostname;
    }

    public final String getServerDescrption() {
        return serverDescription;
    }

    public final void setServerDescription(final String theServerDescription) {
        serverDescription = theServerDescription;
    }

    public final int getHttpPort() {
        return httpPort;
    }

    public final void setHttpPort(final int theHttpPort) {
        httpPort = theHttpPort;
    }
}

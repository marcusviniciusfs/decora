package com.decora.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class RESTEasyJodaModuleProvider implements ContextResolver<ObjectMapper> {

    @Override
    public final ObjectMapper getContext(final Class<?> aClass) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        return objectMapper;
    }
}

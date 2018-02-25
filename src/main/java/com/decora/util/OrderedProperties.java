package com.decora.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class OrderedProperties extends Properties {

    private static final long serialVersionUID = 4112578634029874840L;

    @Override
    public final synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<>(keySet()));
    }
}

package com.graph.viewer.model;

import java.util.Map;

public interface Element {
    Map getProperties();

    void addProperty(String name, String value);

    String getName();

    public void appendProperty(String name, String value);

}

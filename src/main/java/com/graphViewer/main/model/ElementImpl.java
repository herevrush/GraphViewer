package com.graphViewer.main.model;

import java.util.HashMap;
import java.util.Map;

public abstract class ElementImpl implements Element {
    private String name;
    private Map<String,String> properties;

    public ElementImpl(String name){
        this.name = name;
        properties = new HashMap<String,String>();
    }

    @Override
    public Map<String,String> getProperties() {
        return properties;
    }

    @Override
    public void addProperty(String name, String value){
        properties.put(name,value);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void appendProperty(String name, String value) {
        String val = properties.get(name);
        properties.put(name,val + "," + value);
    }
}

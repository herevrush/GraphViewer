package com.graphViewer.model;

public enum StatusMessage {

    INFO(""),ERROR("Error: ");


    private String prefix;
    private StatusMessage(final String prefixIn) {
        this.prefix = prefixIn;
    }

    public String getPrefix() {
        return prefix;
    }

}

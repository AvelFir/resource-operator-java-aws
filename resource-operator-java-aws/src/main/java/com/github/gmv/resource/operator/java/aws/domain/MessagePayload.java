package com.github.gmv.resource.operator.java.aws.domain;

import java.util.Map;

public class MessagePayload {
    private String body;
    private Map<String, String> attributes;

    public MessagePayload() { }

    public MessagePayload(String body, Map<String, String> attributes) {
        this.body = body;
        this.attributes = attributes;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}


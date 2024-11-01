package com.github.gmv.resource.operator.java.aws.domain;

import java.util.Map;

public class MessagePayloadRequest {
    private Object body;
    private Map<String, String> attributes;

    public MessagePayloadRequest() { }

    public MessagePayloadRequest(Object body, Map<String, String> attributes) {
        this.body = body;
        this.attributes = attributes;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}


package com.github.gmv.resource.operator.java.aws.domain;

public class ParameterUpdateRequest {

    private String name;
    private String value;

    public ParameterUpdateRequest() {
    }

    public ParameterUpdateRequest(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

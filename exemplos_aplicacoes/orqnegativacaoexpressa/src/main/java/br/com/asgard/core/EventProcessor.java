package br.com.asgard.core;

import java.util.Map;

public interface EventProcessor<T> {

    void process(T data, Map<String, Object> headers);
}

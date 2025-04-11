package br.com.asgard.core;

import java.util.Map;

public interface EventMessagingProducer<T> {
    void publish(T payload, Map<String, Object> headers);
}

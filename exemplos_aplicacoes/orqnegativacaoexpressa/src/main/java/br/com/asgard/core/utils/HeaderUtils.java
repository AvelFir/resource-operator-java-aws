package br.com.asgard.core.utils;

import br.com.asgard.domain.enums.EventType;

import java.util.Map;
import java.util.Optional;

public final class HeaderUtils {

    private static final String EVENT_TYPE = "eventType";

    private HeaderUtils(){ }

    public static String extract(final Map<String, Object> headers, final String headerName){
        return Optional.ofNullable(headers)
                .map(map -> map.get(headerName))
                .map(Object::toString)
                .orElse(null);
    }

    public static EventType extractEventType(final Map<String, Object> headers){
        return EventType.entryOf(extract(headers, EVENT_TYPE));
    }
}

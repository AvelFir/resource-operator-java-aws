package com.github.gmv.resource.operator.java.aws.utils;

import com.amazonaws.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageUtils {

    //TODO repensar essa maneira de implementaçao, tambem repensar como tratar o dataType
    public static Map<String, MessageAttributeValue> convertHeadersToMessageAttributes(final Map<String, String> headers) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();

        headers.forEach((key, value) -> {
            if (key.startsWith("aws-")) {
                String newKey = key.substring(4);
                messageAttributes.put(newKey, new MessageAttributeValue()
                        .withDataType("String")
                        .withStringValue(value)
                );
            }
        });

        return messageAttributes;
    }

    public static Map<String, MessageAttributeValue> convertAttributesToMessageAttributes(Map<String, String> attributes) {
        return attributes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new MessageAttributeValue()
                                .withStringValue(entry.getValue())
                                .withDataType("String")
                ));
    }
}

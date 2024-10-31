package com.github.gmv.resource.operator.java.aws.utils;

import com.amazonaws.services.sqs.model.MessageAttributeValue;

import java.util.HashMap;
import java.util.Map;

public class HeaderUtils {

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
}

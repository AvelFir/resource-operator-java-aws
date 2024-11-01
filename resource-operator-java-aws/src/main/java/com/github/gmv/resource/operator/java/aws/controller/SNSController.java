package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gmv.resource.operator.java.aws.domain.MessagePayloadRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sns")
public class SNSController {

    private final AmazonSNS snsClient;
    private final ObjectMapper objectMapper;

    public SNSController(final AmazonSNS client, ObjectMapper objectMapper) {
        this.snsClient = client;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/publish/{topic}")
    public PublishResult sendSingle(
            @PathVariable final String topicName,
            @RequestBody final MessagePayloadRequest payload
    ) throws JsonProcessingException {

        String topicArn = snsClient.listTopics().getTopics().stream()
                .filter(topic -> topic.getTopicArn().endsWith(":" + topicName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado"))
                .getTopicArn();

        PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(objectMapper.writeValueAsString(payload.getBody()));

        if (payload.getAttributes() != null && !payload.getAttributes().isEmpty()) {
            for (Map.Entry<String, String> entry : payload.getAttributes().entrySet()) {
                publishRequest.addMessageAttributesEntry(entry.getKey(),
                        new MessageAttributeValue().withStringValue(entry.getValue()));
            }
        }

        return snsClient.publish(publishRequest);
    }


}

package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.Topic;  // Importando a classe correta
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

    @PostMapping("/publish/{topicName}")
    public PublishResult sendSingle(
            @PathVariable final String topicName,
            @RequestBody final MessagePayloadRequest payload
    ) throws JsonProcessingException {

        String topicArn = null;
        String nextToken = null;

        do {
            ListTopicsRequest listTopicsRequest = new ListTopicsRequest();
            if (nextToken != null) {
                listTopicsRequest.setNextToken(nextToken);
            }

            ListTopicsResult listTopicsResult = snsClient.listTopics(listTopicsRequest);
            topicArn = listTopicsResult.getTopics().stream()
                    .filter(topic -> topic.getTopicArn().endsWith(":" + topicName))
                    .map(Topic::getTopicArn)
                    .findFirst()
                    .orElse(null);

            nextToken = listTopicsResult.getNextToken();
        } while (nextToken != null && topicArn == null);

        if (topicArn == null) {
            throw new RuntimeException("Tópico não encontrado");
        }

        PublishRequest publishRequest = new PublishRequest()
                .withTopicArn(topicArn)
                .withMessage(objectMapper.writeValueAsString(payload.getBody()));

        if (payload.getAttributes() != null && !payload.getAttributes().isEmpty()) {
            for (Map.Entry<String, String> entry : payload.getAttributes().entrySet()) {
                publishRequest.addMessageAttributesEntry(entry.getKey(),
                        new MessageAttributeValue().withStringValue(entry.getValue()).withDataType("String"));
            }
        }

        return snsClient.publish(publishRequest);
    }
}

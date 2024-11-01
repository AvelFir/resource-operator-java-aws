package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gmv.resource.operator.java.aws.domain.MessagePayloadRequest;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.gmv.resource.operator.java.aws.utils.MessageUtils.convertAttributesToMessageAttributes;

@RestController
@RequestMapping("/sqs")
public class SQSController {

    private final AmazonSQS sqsClient;
    private final ObjectMapper objectMapper;

    public SQSController(final AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
        this.sqsClient = amazonSQSClient;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/send/{queue}")
    public SendMessageResult sendSingle(
            @PathVariable final String queue,
            @RequestBody final MessagePayloadRequest payload
    ) {
        String messageBody = serializePayloadBody(payload);

        SendMessageRequest request = new SendMessageRequest(queue, messageBody)
                .withMessageAttributes(convertAttributesToMessageAttributes(payload.getAttributes()));

        return sqsClient.sendMessage(request);
    }

    @PostMapping("/send-batch/{queue}")
    public SendMessageBatchResult sendBatch(
            @PathVariable final String queue,
            @RequestBody final List<MessagePayloadRequest> payloads
    ) {
        List<SendMessageBatchRequestEntry> entries = IntStream.range(0, payloads.size())
                .mapToObj(i -> {
                    MessagePayloadRequest payload = payloads.get(i);
                    String messageBody = serializePayloadBody(payload);
                    return new SendMessageBatchRequestEntry()
                            .withId(String.valueOf(i))
                            .withMessageBody(messageBody)
                            .withMessageAttributes(convertAttributesToMessageAttributes(payload.getAttributes()));
                })
                .toList();

        SendMessageBatchRequest request = new SendMessageBatchRequest(queue, entries);
        return sqsClient.sendMessageBatch(request);
    }

    @PostMapping("/purge/{queue}")
    public PurgeQueueResult purge(@PathVariable final String queue) {
        PurgeQueueRequest request = new PurgeQueueRequest(queue);
        return sqsClient.purgeQueue(request);
    }

    @GetMapping("/all")
    public ListQueuesResult listAll(
            @RequestParam(required = false) final String prefix
    ) {
        ListQueuesRequest request = new ListQueuesRequest(prefix);
        return sqsClient.listQueues(request);
    }

    //TODO: Estudar comportamento do metodo
    @GetMapping("/receive/{queue}")
    public ReceiveMessageResult receiveMessages(
            @PathVariable final String queue,
            @RequestParam(required = false, defaultValue = "1") final Integer maxMessages
    ) {
        ReceiveMessageRequest request = new ReceiveMessageRequest(queue)
                .withMaxNumberOfMessages(maxMessages);
        return sqsClient.receiveMessage(request);
    }

    @GetMapping("/info/{queue}")
    public GetQueueAttributesResult get(
            @PathVariable final String queue
    ) {
        GetQueueAttributesRequest request = new GetQueueAttributesRequest(queue)
                .withQueueUrl(queue)
                .withAttributeNames(QueueAttributeName.All);
        return sqsClient.getQueueAttributes(request);
    }

    private String serializePayloadBody(MessagePayloadRequest payload) {
        try {
            return objectMapper.writeValueAsString(payload.getBody());
        } catch (Exception e) {
            throw new SerializationFailedException("Falha na serialização do corpo da mensagem", e);
        }
    }
}

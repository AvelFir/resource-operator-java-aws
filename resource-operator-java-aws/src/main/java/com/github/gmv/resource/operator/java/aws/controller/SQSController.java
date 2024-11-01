package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gmv.resource.operator.java.aws.domain.MessagePayloadRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.github.gmv.resource.operator.java.aws.utils.MessageUtils.convertAttributesToMessageAttributes;
import static com.github.gmv.resource.operator.java.aws.utils.MessageUtils.convertHeadersToMessageAttributes;

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
    public ResponseEntity<SendMessageResult> sendSingle(
            @PathVariable final String queue,
            @RequestBody final MessagePayloadRequest payload
    ) throws JsonProcessingException {
        String messageBody = objectMapper.writeValueAsString(payload.getBody());

        SendMessageRequest request = new SendMessageRequest(queue, messageBody)
                .withMessageAttributes(convertAttributesToMessageAttributes(payload.getAttributes()));

        SendMessageResult result = sqsClient.sendMessage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/send-batch/{queue}")
    public SendMessageBatchResult sendBatch(
            @PathVariable final String queue,
            @RequestBody final List<String> payload,
            @RequestHeader final Map<String, String> headers
    ) {
        List<SendMessageBatchRequestEntry> entries = IntStream.range(0, payload.size())
                .mapToObj(i -> new SendMessageBatchRequestEntry()
                        .withId(String.valueOf(i))
                        .withMessageBody(payload.get(i))
                        .withMessageAttributes(convertHeadersToMessageAttributes(headers))
                )
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
            @RequestParam(required = false, defaultValue = "10") final Integer maxMessages
    ) {
        ReceiveMessageRequest request = new ReceiveMessageRequest(queue)
                .withMaxNumberOfMessages(maxMessages);
        return sqsClient.receiveMessage(request);
    }


}

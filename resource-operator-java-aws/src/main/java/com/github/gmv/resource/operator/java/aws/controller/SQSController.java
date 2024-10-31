package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.github.gmv.resource.operator.java.aws.utils.HeaderUtils.convertHeadersToMessageAttributes;

@RestController
@RequestMapping("/sqs")
public class SQSController {

    private final AmazonSQS sqsClient;

    public SQSController(final AmazonSQS amazonSQSClient) {
        this.sqsClient = amazonSQSClient;
    }

    @PostMapping("/send")
    public SendMessageResult sendSingle(
            @RequestParam final String queueUrl,
            @RequestBody final String payload,
            @RequestHeader final Map<String, String> headers
    ) {
        SendMessageRequest request = new SendMessageRequest(queueUrl, payload)
                .withMessageAttributes(convertHeadersToMessageAttributes(headers));
        return sqsClient.sendMessage(request);
    }

    @PostMapping("/send-batch")
    public SendMessageBatchResult sendBatch(
            @RequestParam final String queueUrl,
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
        SendMessageBatchRequest request = new SendMessageBatchRequest(queueUrl, entries);
        return sqsClient.sendMessageBatch(request);
    }

    @DeleteMapping("/purge")
    public PurgeQueueResult purge(@RequestParam final String queueUrl) {
        PurgeQueueRequest request = new PurgeQueueRequest(queueUrl);
        return sqsClient.purgeQueue(request);
    }

    @GetMapping("/all")
    public ListQueuesResult listAll(
            @RequestParam(required = false) final String prefix,
            @RequestHeader final Map<String, String> headers
    ) {
        ListQueuesRequest request = new ListQueuesRequest(prefix);
        return sqsClient.listQueues(request);
    }

    @GetMapping("/receive")
    public ReceiveMessageResult receiveMessages(
            @RequestParam final String queueUrl,
            @RequestParam(required = false, defaultValue = "10") final Integer maxMessages
    ) {
        ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl)
                .withMaxNumberOfMessages(maxMessages);
        return sqsClient.receiveMessage(request);
    }


}

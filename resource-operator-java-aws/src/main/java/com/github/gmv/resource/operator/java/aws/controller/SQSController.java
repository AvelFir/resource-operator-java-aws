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

    @PostMapping("/send/{queue}")
    public SendMessageResult sendSingle(
            @PathVariable final String queue,
            @RequestBody final String payload,
            @RequestHeader final Map<String, String> headers
    ) {
        SendMessageRequest request = new SendMessageRequest(queue, payload)
                .withMessageAttributes(convertHeadersToMessageAttributes(headers));
        return sqsClient.sendMessage(request);
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

package com.github.gmv.resource.operator.java.aws.consumer;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;

import java.util.List;

public class SqsConsumer {
    private static final String QUEUE_URL = "your-queue-url"; // URL da sua fila SQS
    private static final int WAIT_TIME_SECONDS = 10; // Tempo de espera para polling da fila

    public static void main(String[] args) {
        AmazonSQS sqsClient = AmazonSQSClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion("sa-east-1")
                .build();

        System.out.println("Iniciando o consumidor SQS...");

        try {
            while (true) {
                ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest(QUEUE_URL)
                        .withMaxNumberOfMessages(10)
                        .withWaitTimeSeconds(WAIT_TIME_SECONDS)
                        .withMessageAttributeNames("All");

                List<Message> messages = sqsClient.receiveMessage(receiveRequest).getMessages();

                for (Message message : messages) {
                    System.out.println("Mensagem recebida: " + message.getBody());
                    boolean deleteMessage = shouldDeleteMessage(message);

                    if (deleteMessage) {
                        DeleteMessageRequest deleteRequest = new DeleteMessageRequest()
                                .withQueueUrl(QUEUE_URL)
                                .withReceiptHandle(message.getReceiptHandle());
                        sqsClient.deleteMessage(deleteRequest);
                        System.out.println("Mensagem processada e excluída.");
                    } else {
                        System.out.println("Mensagem processada, mas não excluída.");
                    }
                }

                Thread.sleep(1000); // Aguarda 1 segundo entre os polling (ajuste conforme necessário)
            }
        } catch (InterruptedException e) {
            System.err.println("Consumidor interrompido.");
        }
    }

    private static boolean shouldDeleteMessage(Message message) {
        if (message.getMessageAttributes().containsKey("ack")) {
            return Boolean.parseBoolean(message.getMessageAttributes().get("ack").getStringValue());
        } else {
            return true;
        }
    }
}

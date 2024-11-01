package com.github.gmv.resource.operator.java.aws.consumer;

import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Deprecated // Vai ser substituido por metodos no controller
public class SQSConsumer {

    @SqsListener(value = "queueName", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void handle(@Payload final String message, @Headers final Map<String, Object> headers){
        System.out.println("Mensagem Consumida: " + message);
        System.out.println("Headers Consumida: " + headers);

        if("ERRO".equals(headers.get("cenario"))){
            System.out.println("CENARIO DE ERRO");
            throw new RuntimeException("CENARIO DE ERRO");
        }
        System.out.println("Mensagem Processada com sucesso");
    }
}

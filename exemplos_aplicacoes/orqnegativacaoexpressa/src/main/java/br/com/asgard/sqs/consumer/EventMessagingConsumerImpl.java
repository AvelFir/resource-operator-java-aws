package br.com.asgard.sqs.consumer;

import br.com.asgard.core.EventMessagingConsumer;
import br.com.asgard.domain.events.BoletoNegativadoEvent;
import br.com.asgard.sqs.strategy.EventTypeStrategyFactory;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;

import java.beans.Visibility;

public class EventMessagingConsumerImpl implements EventMessagingConsumer<BoletoNegativadoEvent> {

    EventTypeStrategyFactory factory;

    public EventMessagingConsumerImpl(
            final EventTypeStrategyFactory factory
    ){
        this.factory = factory;
    }

    @Override
    @SqsListener(value = "{orqnegativacaoexpressa.sqs.orquestrador.queue-name}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void handler(BoletoNegativadoEvent event, MessageHeaders messageHeaders, Visibility visibility) {
        factory.getStrategy(event.getEventType()).execute(event, messageHeaders);
    }

    @Override
    public void exceptionHandler() {

    }

    @Override
    public void conversionExceptionHandler(MessageConversionException exception) {

    }
}

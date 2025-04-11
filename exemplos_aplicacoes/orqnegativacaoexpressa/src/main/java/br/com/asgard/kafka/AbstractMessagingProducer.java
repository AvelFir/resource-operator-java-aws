package br.com.asgard.kafka;

import br.com.asgard.core.EventMessagingProducer;
import br.com.asgard.domain.events.BoletoNegativadoEvent;

import java.util.Map;

public abstract class AbstractMessagingProducer implements EventMessagingProducer<BoletoNegativadoEvent> {

    @Override
    public void publish(final BoletoNegativadoEvent event, final Map<String, Object> headers){

    }
}

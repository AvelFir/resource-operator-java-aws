package br.com.asgard.sqs.strategy;

import br.com.asgard.domain.enums.EventType;
import br.com.asgard.domain.events.BoletoNegativadoEvent;

import java.util.Map;

public class UnrecognizedStrategyImpl implements EventTypeStrategy {
    @Override
    public void execute(BoletoNegativadoEvent event, Map<String, Object> headers) {

    }

    @Override
    public EventType getStrategyType() {
        return null;
    }
}

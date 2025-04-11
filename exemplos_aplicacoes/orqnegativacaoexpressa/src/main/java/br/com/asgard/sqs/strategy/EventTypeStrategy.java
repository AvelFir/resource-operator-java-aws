package br.com.asgard.sqs.strategy;

import br.com.asgard.domain.enums.EventType;
import br.com.asgard.domain.events.BoletoNegativadoEvent;

import java.util.Map;
import java.util.Objects;

public interface EventTypeStrategy {

    void execute(BoletoNegativadoEvent event, Map<String, Object> headers);

    EventType getStrategyType();
}

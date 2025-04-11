package br.com.asgard.sqs.strategy.boleto;

import br.com.asgard.core.EventProcessor;
import br.com.asgard.domain.enums.EventType;
import br.com.asgard.domain.events.BoletoNegativadoEvent;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.sqs.strategy.EventTypeStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BoletoEmitidoStrategyImpl implements EventTypeStrategy {

    private final EventProcessor<BoletoNegativado> processor;

    public BoletoEmitidoStrategyImpl(
            final @Qualifier("boletoEmitidoEventProcessor") EventProcessor<BoletoNegativado> processor
    ){
        this.processor = processor;
    }

    @Override
    public void execute(BoletoNegativadoEvent event, Map<String, Object> headers) {
        processor.process(event.getBoletoNegativado(), headers);
    }

    @Override
    public EventType getStrategyType() {
        return EventType.BOLETO_LIQUIDADO;
    }
}

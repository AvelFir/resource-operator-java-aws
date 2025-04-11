package br.com.asgard.sqs.strategy.negativacao_core;

import br.com.asgard.core.EventProcessor;
import br.com.asgard.domain.enums.EventType;
import br.com.asgard.domain.events.BoletoNegativadoEvent;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.sqs.strategy.EventTypeStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificacaoSucessoNegativacaoStrategyImpl implements EventTypeStrategy {

    private final EventProcessor<BoletoNegativado> processor;

    public NotificacaoSucessoNegativacaoStrategyImpl(
            final @Qualifier("notificacaoSucessoNegativacaoProcessor") EventProcessor<BoletoNegativado> processor
    ){
        this.processor = processor;
    }

    @Override
    public void execute(BoletoNegativadoEvent event, Map<String, Object> headers) {
        processor.process(event.getBoletoNegativado(), headers);
    }

    @Override
    public EventType getStrategyType() {
        return EventType.NOTIFICACAO_SUCESSO_NEGATIVACAO;
    }
}

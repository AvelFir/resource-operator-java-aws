package br.com.asgard.processor.adapter;

import br.com.asgard.core.AgendamentoProcessor;
import br.com.asgard.core.BoletoNegativadoPersistence;
import br.com.asgard.core.EventMessagingProducer;
import br.com.asgard.core.EventProcessor;
import br.com.asgard.domain.boundedcontext.agendador.Agendamento;
import br.com.asgard.domain.enums.StatusNegativacaoBoleto;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.domain.negativacao_boleto.NegativacaoBoleto;
import br.com.fluentvalidator.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service("boletoEmitidoEventProcessor")
public class BoletoEmitidoProcessorImpl extends BoletoNegativadoAbstractProcessor implements EventProcessor<BoletoNegativado> {

    private final AgendamentoProcessor agendamentoProcessor;

    public BoletoEmitidoProcessorImpl(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            @Qualifier("snsEventMessagingProducer") EventMessagingProducer<BoletoNegativado> notification,
            AgendamentoProcessor agendamentoProcessor
    ) {
        super(validator, persistence, notification);
        this.agendamentoProcessor = agendamentoProcessor;
    }

    @Override
    @Transactional
    public void process(BoletoNegativado boletoNegativadoEvent, Map<String, Object> headers) {
        validateEvent(boletoNegativadoEvent);
        if(isBoletoEmProcessoDeNegativacao(boletoNegativadoEvent)){
            return;
        }

        final Agendamento agendamento = agendarBoleto(boletoNegativadoEvent);
        boletoNegativadoEvent.setNegativacaoBoleto(criarNegativacaoBoleto(agendamento));

        persistirBoletoNegativado(boletoNegativadoEvent);
        notificarTopicoStatus(boletoNegativadoEvent, headers);
    }

    private Agendamento agendarBoleto(BoletoNegativado boletoNegativadoEvent) {
        return agendamentoProcessor.agendar(boletoNegativadoEvent.getBoletoCobranca());
    }

    private NegativacaoBoleto criarNegativacaoBoleto(Agendamento agendamento) {
        return NegativacaoBoleto.builder()
                .statusNegativacaoBoleto(StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_AGENDADA)
                .dataEfetivacaoAgendamento(agendamento.getDataHoraExecucao())
                .idAgendamento(agendamento.getAgendamentoId())
                .criadoEm(LocalDateTime.now())
                .build();
    }

}

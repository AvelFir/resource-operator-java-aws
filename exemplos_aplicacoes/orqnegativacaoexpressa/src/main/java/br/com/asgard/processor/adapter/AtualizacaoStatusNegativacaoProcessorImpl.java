package br.com.asgard.processor.adapter;

import br.com.asgard.core.BoletoNegativadoPersistence;
import br.com.asgard.core.EventMessagingProducer;
import br.com.asgard.core.EventProcessor;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.domain.negativacao_boleto.NegativacaoBoleto;
import br.com.fluentvalidator.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static java.util.Objects.isNull;

@Service("AtualizacaoStatusNegativacaoProcessor")
public class AtualizacaoStatusNegativacaoProcessorImpl extends BoletoNegativadoAbstractProcessor implements EventProcessor<BoletoNegativado> {

    public AtualizacaoStatusNegativacaoProcessorImpl(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            @Qualifier("snsEventMessagingProducer") EventMessagingProducer<BoletoNegativado> notification
    ) {
        super(validator, persistence, notification);
    }

    @Override
    @Transactional
    public void process(BoletoNegativado boletoNegativadoEvent, Map<String, Object> headers) {
        validateEvent(boletoNegativadoEvent);
        BoletoNegativado boletoNegativadoBase = findByBoletoId(boletoNegativadoEvent);

        if(isNull(boletoNegativadoBase)){
            throw new RuntimeException("Boleto nao encontrado na base de dados");
        }

        updateNegativacaoBoleto(boletoNegativadoBase.getNegativacaoBoleto(), boletoNegativadoEvent.getNegativacaoBoleto());

        this.persistirBoletoNegativado(boletoNegativadoBase);
        this.notificarTopicoStatus(boletoNegativadoBase, headers);
    }

    private static void updateNegativacaoBoleto(NegativacaoBoleto target, NegativacaoBoleto source) {
        target.setIdNegativacao(source.getIdNegativacao());
        target.setStatusNegativacaoBoleto(source.getStatusNegativacaoBoleto());
        target.setAlteradoEm(LocalDateTime.now());
    }

}

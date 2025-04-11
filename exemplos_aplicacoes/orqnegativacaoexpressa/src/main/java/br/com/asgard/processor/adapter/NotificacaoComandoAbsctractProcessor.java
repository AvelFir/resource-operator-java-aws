package br.com.asgard.processor.adapter;

import br.com.asgard.core.BoletoNegativadoPersistence;
import br.com.asgard.core.EventMessagingProducer;
import br.com.asgard.core.EventProcessor;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.domain.negativacao_boleto.ComandoNegativacao;
import br.com.asgard.domain.negativacao_boleto.NegativacaoBoleto;
import br.com.asgard.domain.negativacao_boleto.TipoOperacaoComando;
import br.com.fluentvalidator.Validator;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.Objects.isNull;

public abstract class NotificacaoComandoAbsctractProcessor extends BoletoNegativadoAbstractProcessor implements EventProcessor<BoletoNegativado>  {

    public NotificacaoComandoAbsctractProcessor(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            EventMessagingProducer<BoletoNegativado> notification) {
        super(validator, persistence, notification);
    }

    @Transactional
    @Override
    public void process(BoletoNegativado boletoNegativadoEvent, Map<String, Object> headers) {
        validateEvent(boletoNegativadoEvent);
        ComandoNegativacao comandoNegativacaoEvent = boletoNegativadoEvent.getLastComando().orElseThrow();
        BoletoNegativado boletoNegativadoBase = findBoletoByIdComando(boletoNegativadoEvent);

        if(isNull(boletoNegativadoBase)){
            throw new RuntimeException("Boleto nao encontrado na base de dados");
        }

        updateComandoNegativacao(
                boletoNegativadoBase.getComandoById(comandoNegativacaoEvent.getIdComando()).orElse(null),
                comandoNegativacaoEvent
        );

        updateNegativacaoBoleto(boletoNegativadoBase.getNegativacaoBoleto(), comandoNegativacaoEvent.getTipoOperacao());
        persistirBoletoNegativado(boletoNegativadoBase);
        notificarTopicoStatus(boletoNegativadoBase, headers);
    }

    abstract void updateNegativacaoBoleto(NegativacaoBoleto target, TipoOperacaoComando operacaoComando);

    abstract void updateComandoNegativacao(ComandoNegativacao target, ComandoNegativacao source);
}

package br.com.asgard.processor.adapter;

import br.com.asgard.core.BoletoNegativadoPersistence;
import br.com.asgard.core.EventMessagingProducer;
import br.com.asgard.core.EventProcessor;
import br.com.asgard.domain.enums.StatusNegativacaoBoleto;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.domain.negativacao_boleto.ComandoNegativacao;
import br.com.asgard.domain.negativacao_boleto.NegativacaoBoleto;
import br.com.asgard.domain.negativacao_boleto.TipoOperacaoComando;
import br.com.fluentvalidator.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static br.com.asgard.domain.enums.StatusProcessamentoComando.PROCESSADO_FALHA;
import static java.util.Objects.isNull;

@Service("notificacaoFalhaNegativacaoProcessor")
public class NotificacaoFalhaNegativacaoProcessorImpl extends NotificacaoComandoAbsctractProcessor {

    public NotificacaoFalhaNegativacaoProcessorImpl(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            @Qualifier("snsEventMessagingProducer") EventMessagingProducer<BoletoNegativado> notification
    ) {
        super(validator, persistence, notification);
    }

    @Override
    void updateNegativacaoBoleto(NegativacaoBoleto target, TipoOperacaoComando operacaoComando) {
        if(TipoOperacaoComando.INCLUSAO.equals(operacaoComando)){
            target.setStatusNegativacaoBoleto(StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_INCLUSAO_PROCESSADA_FALHA);
        } else if (TipoOperacaoComando.EXCLUSAO.equals(operacaoComando)){
            target.setStatusNegativacaoBoleto(StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_EXCLUSAO_PROCESSADA_FALHA);
        }

        target.setAlteradoEm(LocalDateTime.now());
    }

    @Override
    void updateComandoNegativacao(ComandoNegativacao target, ComandoNegativacao source) {
        target.setStatusComando(PROCESSADO_FALHA);
        target.setRetornadoEm(LocalDateTime.now());
        target.setErros(source.getErros());

    }

}

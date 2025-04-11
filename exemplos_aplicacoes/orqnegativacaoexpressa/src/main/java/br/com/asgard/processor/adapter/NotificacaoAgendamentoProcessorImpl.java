package br.com.asgard.processor.adapter;

import br.com.asgard.core.*;
import br.com.asgard.domain.boleto.BoletoCobranca;
import br.com.asgard.domain.boundedcontext.agendador.Agendamento;
import br.com.asgard.domain.boundedcontext.boleto.BoletoDto;
import br.com.asgard.domain.enums.StatusNegativacaoBoleto;
import br.com.asgard.domain.enums.StatusProcessamentoComando;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.domain.negativacao_boleto.ComandoNegativacao;
import br.com.asgard.domain.negativacao_boleto.TipoOperacaoComando;
import br.com.asgard.kafka.AbstractMessagingProducer;
import br.com.asgard.service.AgendadorService;
import br.com.fluentvalidator.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service("notificacaoAgendamentoNegativacaoProcessor")
public class NotificacaoAgendamentoProcessorImpl extends BoletoNegativadoAbstractProcessor implements EventProcessor<BoletoNegativado> {

    private static final String SITUACAO_BOLETO = "EM_ABERTO";
    private final ConsultaBoletoService consultaBoletoService;
    private final AgendamentoProcessor agendamentoProcessor;
    private final AbstractMessagingProducer incluirNegativacaoProducer;

    public NotificacaoAgendamentoProcessorImpl(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            EventMessagingProducer<BoletoNegativado> notification,
            ConsultaBoletoService consultaBoletoService,
            AgendamentoProcessor agendamentoProcessor,
            @Qualifier("incluir") AbstractMessagingProducer incluirNegativacaoProducer
    ) {
        super(validator, persistence, notification);
        this.consultaBoletoService = consultaBoletoService;
        this.agendamentoProcessor = agendamentoProcessor;
        this.incluirNegativacaoProducer = incluirNegativacaoProducer;
    }

    @Override
    @Transactional
    public void process(BoletoNegativado boletoNegativadoEvent, Map<String, Object> headers) {
        validateEvent(boletoNegativadoEvent);
        BoletoNegativado boletoNegativadoBase = findByBoletoId(boletoNegativadoEvent);

        if(isNull(boletoNegativadoBase)){
            throw new RuntimeException("Boleto nao encontrado na base de dados");
        }

        final BoletoDto boletoCommand = consultaBoletoService.consultarBoleto(
                String.valueOf(boletoNegativadoBase.getBoletoCobranca().getIdBoleto()),
                String.valueOf(boletoNegativadoBase.getBoletoCobranca().getIdBeneficiarioFull())
        ).orElse(null);

        if(isNull(boletoCommand)){
            throw new RuntimeException("Boleto nao encontrada na API command");
        }

        if(!isBoletoEmAberto(boletoCommand)){
            throw new RuntimeException("Situacao boleto nao permite exclusao");
        }

        StatusNegativacaoBoleto statusAtual = boletoNegativadoBase.getNegativacaoBoleto().getStatusNegativacaoBoleto();

        if(isDueDataDifferent(boletoCommand, boletoNegativadoBase.getBoletoCobranca())){
            reagendarBoleto(boletoCommand, boletoNegativadoBase, headers);
        } else {
            publicarEventoNegativacao(boletoCommand, boletoNegativadoBase, headers);
        }

        this.persistirBoletoNegativado(boletoNegativadoBase);
        this.notificarTopicoStatus(boletoNegativadoBase, headers);

    }

    private void publicarEventoNegativacao(BoletoDto boletoCommand, BoletoNegativado boletoNegativadoBase, Map<String, Object> headers) {
        boletoNegativadoBase.setBoletoCobranca(preencheDadosBoletoCobranca(boletoCommand, boletoNegativadoBase.getBoletoCobranca()));
        boletoNegativadoBase.getNegativacaoBoleto().setStatusNegativacaoBoleto(StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_INCLUSAO_ENVIADA_COMANDO);
        boletoNegativadoBase.getNegativacaoBoleto().setAlteradoEm(LocalDateTime.now());
        this.persistirBoletoNegativado(boletoNegativadoBase);
        this.notificarTopicoStatus(boletoNegativadoBase, headers);
    }

    private void reagendarBoleto(BoletoDto boletoCommand, BoletoNegativado boletoNegativadoBase, Map<String, Object> headers) {
        boletoNegativadoBase.getBoletoCobranca()
                .getDadoBoleto()
                .setDataVencimento(boletoCommand.getDataVencimento().orElseThrow());
        boletoNegativadoBase.setBoletoCobranca(preencheDadosBoletoCobranca(boletoCommand, boletoNegativadoBase.getBoletoCobranca()));
        Agendamento agendamento = agendamentoProcessor.agendar(boletoNegativadoBase.getBoletoCobranca());
        boletoNegativadoBase.getNegativacaoBoleto().setIdAgendamento(agendamento.getAgendamentoId());
        boletoNegativadoBase.getNegativacaoBoleto().setDataEfetivacaoAgendamento(agendamento.getDataHoraExecucao());
        boletoNegativadoBase.getNegativacaoBoleto().setStatusNegativacaoBoleto(StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_AGENDADA);
        boletoNegativadoBase.getNegativacaoBoleto().setAlteradoEm(LocalDateTime.now());
        this.persistirBoletoNegativado(boletoNegativadoBase);
        this.notificarTopicoStatus(boletoNegativadoBase, headers);
    }

    private BoletoCobranca preencheDadosBoletoCobranca(BoletoDto boletoCommand, BoletoCobranca boletoCobranca) {
        boletoCobranca.getDadoBoleto().setCodigoEspecie(boletoCommand.getDadoBoleto().getCodigoEspecieBoleto());
        //fazer com os outros
        return boletoCobranca;
    }


    private boolean isBoletoEmAberto(BoletoDto boletoCommand) {
        return boletoCommand.getSituacaoGeralBoleto()
                .map(String::toUpperCase)
                .filter(SITUACAO_BOLETO::equals)
                .isPresent();
    }

    private boolean isDueDataDifferent(final BoletoDto boletoCommand,
                                       final BoletoCobranca boletoCobranca){
        return boletoCommand.getDataVencimento()
                .map(dataVencimento -> !dataVencimento.equals(
                        boletoCobranca.getDadoBoleto().getDataVencimento()
                )).orElse(true);
    }

    private ComandoNegativacao criarComandoInclusao() {
        return ComandoNegativacao.builder()
                .idComando(UUID.randomUUID())
                .statusComando(StatusProcessamentoComando.PROCESSANDO)
                .tipoOperacao(TipoOperacaoComando.INCLUSAO)
                .enviadoEm(LocalDateTime.now())
                .build();
    }
}

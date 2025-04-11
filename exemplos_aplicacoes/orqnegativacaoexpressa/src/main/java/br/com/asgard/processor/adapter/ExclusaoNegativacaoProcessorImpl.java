package br.com.asgard.processor.adapter;

import br.com.asgard.core.BoletoNegativadoPersistence;
import br.com.asgard.core.ConsultaBoletoService;
import br.com.asgard.core.EventMessagingProducer;
import br.com.asgard.core.EventProcessor;
import br.com.asgard.domain.boleto.BoletoCobranca;
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
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service("exclusaoNegativacaoEventProcessor")
public class ExclusaoNegativacaoProcessorImpl extends BoletoNegativadoAbstractProcessor implements EventProcessor<BoletoNegativado> {

    private static final String SITUACAO_LIQUIDADO = "PAGA";
    private static final String SITUACAO_BAIXADO = "BAIXADA";
    private static final Set<String> BOLETO_ENCERRADO = Set.of(SITUACAO_LIQUIDADO, SITUACAO_BAIXADO);

    private static final String NAO_INFORMADA = "Nao Informada";

    private static final EnumSet<StatusNegativacaoBoleto> FALHA_NEGATIVACAO = EnumSet.of(
            StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_INCLUSAO_RECUSADA_BUREAU,
            StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_INCLUSAO_PROCESSADA_FALHA
    );

    private static final EnumSet<StatusNegativacaoBoleto> AGUARDANDO_PROCESSAMENTO_NEGATIVACAO = EnumSet.of(
            StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_INCLUSAO_ENVIADA_COMANDO,
            StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_INCLUSAO_PROCESSADA_SUCESSO
    );


    private final ConsultaBoletoService consultaBoletoService;
    private final AgendadorService agendadorService;
    private final AbstractMessagingProducer excluirNegativacaoProducer;

    public ExclusaoNegativacaoProcessorImpl(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            EventMessagingProducer<BoletoNegativado> notification,
            ConsultaBoletoService consultaBoletoService,
            AgendadorService agendadorService,
            @Qualifier("excluir") AbstractMessagingProducer excluirNegativacaoProducer
    ) {
        super(validator, persistence, notification);
        this.consultaBoletoService = consultaBoletoService;
        this.agendadorService = agendadorService;
        this.excluirNegativacaoProducer = excluirNegativacaoProducer;
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

        if(!isSituacaoBoletoEncerrado(boletoCommand)){
            throw new RuntimeException("Situacao boleto nao permite exclusao");
        }

        StatusNegativacaoBoleto statusAtual = boletoNegativadoBase.getNegativacaoBoleto().getStatusNegativacaoBoleto();

        if(FALHA_NEGATIVACAO.contains(statusAtual)){
            System.out.println("Status Atual da negativacao nao permite delecao, interrompendo fluxo");
            return;
        }

        if(AGUARDANDO_PROCESSAMENTO_NEGATIVACAO.contains(statusAtual)){
            System.out.println("Negativacao ainda nao confirmada, outra tentativa de exclusao sera feita em breve: NAO IMPLEMENTADO");
            return;
        }

        if(StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_AGENDADA.equals(statusAtual)){
            cancelarAgendamento(boletoNegativadoBase);
        } else {
            excluirNegativacao(boletoNegativadoBase, boletoCommand);
        }

        this.persistirBoletoNegativado(boletoNegativadoBase);
        this.notificarTopicoStatus(boletoNegativadoBase, headers);
    }

    private boolean isSituacaoBoletoEncerrado(BoletoDto boletoCommand) {
        return boletoCommand.getSituacaoGeralBoleto()
                .map(String::toUpperCase)
                .filter(BOLETO_ENCERRADO::contains)
                .isPresent();
    }

    private void cancelarAgendamento(BoletoNegativado boletoNegativado) {
        agendadorService.excluir(boletoNegativado.getNegativacaoBoleto().getIdAgendamento());
        alterarNegativacao(boletoNegativado, StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_AGENDAMENTO_EXCLUIDO);
        persistirBoletoNegativado(boletoNegativado);
    }

    private void excluirNegativacao(BoletoNegativado boletoNegativadoBase, BoletoDto boletoCommand) {
        boletoNegativadoBase.setBoletoCobranca(preencheDadosBoletoCobranca(boletoCommand, boletoNegativadoBase.getBoletoCobranca()));
        alterarNegativacao(boletoNegativadoBase, StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_EXCLUSAO_ENVIADA_COMANDO);
        adicionarComandoExclusao(boletoNegativadoBase);
        persistirBoletoNegativado(boletoNegativadoBase);
        enviarComandoExcluirNegativacao(boletoNegativadoBase);
    }

    private BoletoCobranca preencheDadosBoletoCobranca(BoletoDto boletoCommand, BoletoCobranca boletoCobranca) {
        boletoCobranca.getDadoBoleto().setCodigoEspecie(boletoCommand.getDadoBoleto().getCodigoEspecieBoleto());
        //fazer com os outros
        return boletoCobranca;
    }

    private void alterarNegativacao(BoletoNegativado boletoNegativado, StatusNegativacaoBoleto statusNegativacaoBoleto) {
        boletoNegativado.getNegativacaoBoleto().setStatusNegativacaoBoleto(statusNegativacaoBoleto);
        boletoNegativado.getNegativacaoBoleto().setAlteradoEm(LocalDateTime.now());
    }

    private void enviarComandoExcluirNegativacao(final BoletoNegativado boletoNegativado){
        Map<String, Object> headers = Map.of("idComando", boletoNegativado.getLastComandoId().get().toString());
        excluirNegativacaoProducer.publish(boletoNegativado, headers);
    }

    private void adicionarComandoExclusao(final BoletoNegativado boletoNegativado){
        boletoNegativado.adicionarComandoNegativacao(criarComandoExclusao());
    }

    private ComandoNegativacao criarComandoExclusao() {
        return ComandoNegativacao.builder()
                .idComando(UUID.randomUUID())
                .statusComando(StatusProcessamentoComando.PROCESSANDO)
                .tipoOperacao(TipoOperacaoComando.EXCLUSAO)
                .enviadoEm(LocalDateTime.now())
                .build();
    }


}

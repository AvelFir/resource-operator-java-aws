package br.com.asgard.processor.adapter;

import br.com.asgard.core.*;
import br.com.asgard.domain.enums.StatusNegativacaoBoleto;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.asgard.processor.enums.BoletoAlteradoAcoes;
import br.com.asgard.processor.enums.BoletoAlteradoCampos;
import br.com.fluentvalidator.Validator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static br.com.asgard.processor.enums.BoletoAlteradoAcoes.ATUALIZAR_BASE;
import static java.util.Objects.isNull;

@Service(alterarNegativacaoProcessor)
public class AlterarNegativacaoProcessor extends BoletoNegativadoAbstractProcessor implements EventProcessor<BoletoNegativado> {

    private final AcaoProcessamentoBoletoAlteradoStrategyFactory factory;

    public AlterarNegativacaoProcessor(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            EventMessagingProducer<BoletoNegativado> notification,
            ConsultaBoletoService consultaBoletoService,
            NegativacaoProcessor negativacaoProcessor,
            AgendamentoProcessorImpl agendamentoProcessor
    ) {
        super(validator, persistence, notification);
        this.consultaBoletoService = consultaBoletoService;
        this.negativacaoProcessor = negativacaoProcessor;
        this.agendamentoProcessor = agendamentoProcessor;
    }

    @Override
    public void process(BoletoNegativado boletoNegativadoEvent, Map<String, Object> headers) {
        validateEvent(boletoNegativadoEvent);
        BoletoNegativado boletoNegativadoDb = findByBoletoId(boletoNegativadoEvent);

        if(isNull(boletoNegativadoDb)){
            throw new RuntimeException("Boleto nao encontrado na base de dados");
        }

        StatusNegativacaoBoleto status = boletoNegativadoDb.getNegativacaoBoleto().getStatusNegativacaoBoleto();

        BoletoAlteradoAcoes acaoCampo = ATUALIZAR_BASE;

        for(BoletoAlteradoCampos campo: BoletoAlteradoCampos.values()){
            Object valorAntigo = campo.getValue(boletoNegativadoDb);
            Object valorNovo = campo.getValue(boletoNegativadoEvent);
            if(!Objects.equals(valorAntigo, valorNovo)){
                BoletoAlteradoAcoes novaAcao = campo.getAcoes();
                if(novaAcao.getPrioridade() > acaoCampo.getPrioridade()){
                    acaoCampo = novaAcao;
                }
            }
        }

        BoletoNegativadoMapper.INSTANCE.updateAlteracaoFrom(boletoNegativadoDb, boletoNegativadoEvent);
        
        switch (acaoCampo){
            case ATUALIZAR_BASE:
                break;
            case EXCLUIR_NEGATIVACAO:
                if(status != StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_AGENDADA){
                    this.negativacaoProcessor.excluir(boletoNegativadoDb);
                }
            case REAGENDAR_NEGATIVACAO:
                if(status == StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_AGENDADA){
                    this.negativacaoProcessor.excluir(boletoNegativadoDb);
                } else if(status == StatusNegativacaoBoleto.NEGATIVACAO_BOLETO_EXCLUSAO_CONFIRMADA_BUREAU){
                    this.negativacaoProcessor.excluir(boletoNegativadoDb);
                    this.agendamentoProcessor.reagendar(boletoNegativadoDb);
                }

        }

        this.persistirBoletoNegativado(boletoNegativadoDb);
        this.notificarTopicoStatus(boletoNegativadoDb, headers);eita
    }
}

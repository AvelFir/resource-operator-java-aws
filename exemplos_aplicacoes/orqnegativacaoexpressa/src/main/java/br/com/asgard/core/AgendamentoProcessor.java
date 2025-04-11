package br.com.asgard.core;

import br.com.asgard.domain.boundedcontext.agendador.Agendamento;
import br.com.asgard.domain.boleto.BoletoCobranca;
import org.springframework.stereotype.Component;

@Component
public interface AgendamentoProcessor {

    Agendamento agendar(BoletoCobranca boletoCobranca);
}

package br.com.asgard.service;

import br.com.asgard.domain.boleto.BoletoCobranca;
import br.com.asgard.domain.boundedcontext.negativacao.SimulacaoNegativacao;

import java.util.Optional;

public interface NegativacaoService {
    Optional<SimulacaoNegativacao> simularNegativacao(BoletoCobranca boletoCobranca);
}

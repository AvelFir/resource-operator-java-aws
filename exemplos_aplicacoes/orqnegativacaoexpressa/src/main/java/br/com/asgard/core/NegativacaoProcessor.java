package br.com.asgard.core;

import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;

public interface NegativacaoProcessor {
    void excluir(final BoletoNegativado boletoNegativado);

    void incluir(final BoletoNegativado boletoNegativado);
}

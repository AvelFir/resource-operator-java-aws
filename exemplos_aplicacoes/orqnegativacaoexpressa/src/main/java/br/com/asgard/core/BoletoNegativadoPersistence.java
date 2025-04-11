package br.com.asgard.core;

import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;

import java.util.Optional;
import java.util.UUID;

public interface BoletoNegativadoPersistence {

    Optional<BoletoNegativado> findByIdBoleto(UUID idBoleto);

    Optional<BoletoNegativado> findByIdComando(UUID idComando);

    boolean existsByidBoleto(UUID idBoleto);

    BoletoNegativado save(BoletoNegativado boletoNegativado);
}

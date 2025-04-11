package br.com.asgard.core;

import br.com.asgard.domain.boundedcontext.boleto.BoletoDto;

import java.util.Optional;

public interface ConsultaBoletoService {
    Optional<BoletoDto> consultarBoleto(String idBoleto, String idBeneficiario);
}

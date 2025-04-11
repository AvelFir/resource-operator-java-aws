package br.com.asgard.domain.boleto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Beneficiario {
    private UUID idBeneficiario;
    private UUID idConta;
    private String agencia;
    private String conta;
    private String dac;
}

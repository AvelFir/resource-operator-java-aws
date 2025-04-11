package br.com.asgard.domain.boleto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoletoCobranca {
    private UUID idBoleto;
    private Beneficiario beneficiario;
    private DadoBoleto dadoBoleto;

    @JsonIgnore
    public String getIdBeneficiarioFull() {
        return (this.beneficiario != null)
                ? beneficiario.getAgencia() + beneficiario.getConta() + beneficiario.getDac()
                : null;
    }
}

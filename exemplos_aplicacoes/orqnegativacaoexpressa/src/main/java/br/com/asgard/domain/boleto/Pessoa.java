package br.com.asgard.domain.boleto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pessoa {
    private String nomeCompletoPessoa;
    private Character codigoTipoPessoa;
    private String numeroDocumentoIdentificacaoPessoa;
}

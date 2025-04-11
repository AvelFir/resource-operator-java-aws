package br.com.asgard.domain.boundedcontext.boleto;

import br.com.asgard.domain.boleto.CodigoEspecieBoleto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DadoBoleto {
    private String situacaoGeralBoleto;
    private CodigoEspecieBoleto codigoEspecieBoleto;
    private LocalDate dataVencimento;
}

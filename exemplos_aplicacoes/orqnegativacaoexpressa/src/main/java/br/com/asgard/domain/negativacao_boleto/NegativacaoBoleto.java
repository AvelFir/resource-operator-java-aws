package br.com.asgard.domain.negativacao_boleto;

import br.com.asgard.domain.enums.StatusNegativacaoBoleto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NegativacaoBoleto {
    private BigInteger idOrquestracao;
    private UUID idNegativacao;
    private UUID idAgendamento;
    private LocalDateTime dataEfetivacaoAgendamento;
    private LocalDateTime criadoEm;
    private LocalDateTime alteradoEm;
    private StatusNegativacaoBoleto statusNegativacaoBoleto;
}

package br.com.asgard.domain.negativacao_boleto;

import br.com.asgard.domain.enums.StatusProcessamentoComando;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComandoNegativacao {
    private UUID idComando;
    private TipoOperacaoComando tipoOperacao;
    private StatusProcessamentoComando statusComando;
    private LocalDateTime enviadoEm;
    private LocalDateTime retornadoEm;
    private List<Erro> erros;
}

package br.com.asgard.domain.boundedcontext.agendador;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Agendamento {
    private UUID agendamentoId;
    private String produtoDemandante;
    private LocalDateTime solicitadoEm;
    private LocalDateTime dataHoraExecucao;
}

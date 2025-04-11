package br.com.asgard.domain.boundedcontext.negativacao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimulacaoNegativacao {
    private LocalDate dataEfetivacaoNegativacao;
}

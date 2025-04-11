package br.com.asgard.domain.boundedcontext.boleto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoletoDto {
    DadoBoleto dadoBoleto;


    public Optional<String> getSituacaoGeralBoleto(){
        return Optional.of(dadoBoleto).map(DadoBoleto::getSituacaoGeralBoleto);
    }

    public Optional<LocalDate> getDataVencimento(){
        return Optional.of(dadoBoleto).map(DadoBoleto::getDataVencimento);
    }
}

package br.com.asgard.domain.negativacao_boleto;

import br.com.asgard.domain.boleto.BoletoCobranca;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoletoNegativado {

    private BoletoCobranca boletoCobranca;

    private NegativacaoBoleto negativacaoBoleto;

    private ArrayList<ComandoNegativacao> comandosNegativacao = new ArrayList<>();

    public Optional<ComandoNegativacao> getLastComando(){
        return comandosNegativacao.isEmpty() ? Optional.empty()
                : Optional.ofNullable(comandosNegativacao.get(comandosNegativacao.size() - 1));
    }

    public Optional<ComandoNegativacao> getComandoById(final UUID idComando){
        return comandosNegativacao.stream()
                .filter(Objects::nonNull)
                .filter(comandoNegativacao -> nonNull(comandoNegativacao.getIdComando()))
                .filter(comandoNegativacao -> comandoNegativacao.getIdComando().equals(idComando))
                .findFirst();
    }

    public Optional<UUID> getLastComandoId(){
        return this.getLastComando().map(ComandoNegativacao::getIdComando);
    }

    public Optional<UUID> getBoletoId(){
        return Optional.ofNullable(boletoCobranca).map(BoletoCobranca::getIdBoleto);
    }

    public void adicionarComandoNegativacao(final ComandoNegativacao comandoNegativacao){
        if(Objects.nonNull(comandoNegativacao)){
            this.comandosNegativacao.add(comandoNegativacao);
        }
    }

}

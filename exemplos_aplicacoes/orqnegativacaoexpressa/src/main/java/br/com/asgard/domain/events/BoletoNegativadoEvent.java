package br.com.asgard.domain.events;

import br.com.asgard.domain.enums.EventType;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoletoNegativadoEvent extends Event<EventType> {

    private BoletoNegativado boletoNegativado;

    @JsonCreator
    public BoletoNegativadoEvent(@JsonProperty("boletoNegativado") BoletoNegativado boletoNegativado,
                                 @JsonProperty("eventType") final EventType eventType){
        super(eventType);
        this.boletoNegativado = boletoNegativado;
    }
}

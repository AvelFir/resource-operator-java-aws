package br.com.asgard.domain.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Event<T> implements Serializable {

    private T eventType;

    private LocalDateTime dataHoraEvento;

    private UUID correlationId;

    protected Event(final T eventType){
        this.eventType = eventType;
    }

}

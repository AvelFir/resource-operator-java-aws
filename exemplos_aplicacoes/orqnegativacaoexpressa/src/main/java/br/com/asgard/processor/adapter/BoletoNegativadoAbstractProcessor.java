package br.com.asgard.processor.adapter;

import br.com.asgard.core.BoletoNegativadoPersistence;
import br.com.asgard.core.EventMessagingProducer;
import br.com.asgard.domain.boleto.BoletoCobranca;
import br.com.asgard.domain.events.BoletoNegativadoEvent;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.fluentvalidator.Validator;
import br.com.fluentvalidator.context.ValidationResult;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static br.com.asgard.core.utils.HeaderUtils.extractEventType;

public abstract class BoletoNegativadoAbstractProcessor {

    private final Validator<BoletoNegativado> validator;
    private final BoletoNegativadoPersistence persistence;
    private final EventMessagingProducer<BoletoNegativadoEvent> notification;

    public BoletoNegativadoAbstractProcessor(
            Validator<BoletoNegativado> validator,
            BoletoNegativadoPersistence persistence,
            EventMessagingProducer<BoletoNegativadoEvent> notification)
    {
        this.validator = validator;
        this.persistence = persistence;
        this.notification = notification;
    }

    protected void validateEvent(final BoletoNegativado boletoNegativado){
        if(boletoNegativado == null){
            throw new IllegalArgumentException();
        }
        ValidationResult validationResult = validator.validate(boletoNegativado);
        if(!validationResult.isValid()){
            throw new RuntimeException();
        }
    }

    protected void persistirBoletoNegativado(final BoletoNegativado boletoNegativado){
        persistence.save(boletoNegativado);
    }

    protected BoletoNegativado findBoletoByIdComando(final BoletoNegativado boletoNegativado){
        UUID idComando = Optional.ofNullable(boletoNegativado)
                .flatMap(BoletoNegativado::getLastComandoId)
                .orElseThrow();
        return persistence.findByIdComando(idComando).orElse(null);
    }

    protected BoletoNegativado findByBoletoId(final BoletoNegativado boletoNegativado){
        UUID idBoleto = Optional.ofNullable(boletoNegativado)
                .flatMap(BoletoNegativado::getBoletoId)
                .orElseThrow();
        return persistence.findByIdBoleto(idBoleto).orElse(null);
    }

    protected boolean isBoletoEmProcessoDeNegativacao(final BoletoNegativado boletoNegativado){
        return Optional.ofNullable(boletoNegativado)
                .map(BoletoNegativado::getBoletoCobranca)
                .map(BoletoCobranca::getIdBoleto)
                .map(persistence::existsByidBoleto)
                .orElse(false);
    }

    protected void notificarTopicoStatus(final BoletoNegativado boletoNegativado,
                                         final Map<String, Object> headers){
        notification.publish(new BoletoNegativadoEvent(boletoNegativado, extractEventType(headers)), headers);
    }
}

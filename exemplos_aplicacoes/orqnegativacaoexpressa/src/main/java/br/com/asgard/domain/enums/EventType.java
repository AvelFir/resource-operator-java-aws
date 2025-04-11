package br.com.asgard.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

@Getter
@AllArgsConstructor
public enum EventType implements Serializable {

    BOLETO_EMITIDO("BOLETO_EMITIDO"),
    BOLETO_ALTERADO("BOLETO_ALTERADO"),
    BOLETO_INCLUIDO("BOLETO_INCLUIDO"),
    BOLETO_TRANSFERIDO("BOLETO_TRANSFERIDO"),
    TRANSFERENCIA_BOLETO_REVERTIDA("TRANSFERENCIA_BOLETO_REVERTIDA"),
    AGENDAMENTO_INCLUSAO("AGENDAMENTO_INCLUSAO"),
    NOTIFICACAO_SUCESSO_NEGATIVACAO("NOTIFICACAO_SUCESSO_NEGATIVACAO"),
    NOTIFICACAO_FALHA_NEGATIVACAO("NOTIFICACAO_FALHA_NEGATIVACAO"),
    ATUALIZACAO_STATUS_NEGATIVACAO_DIVIDA("ATUALIZACAO_STATUS_NEGATIVACAO_DIVIDA"),
    BOLETO_LIQUIDADO("BOLETO_LIQUIDADO"),
    BOLETO_BAIXADO("BOLETO_BAIXADO"),
    NEGATIVACAO_BOLETO_AGENDADO("NEGATIVACAO_BOLETO_AGENDADO"),
    NEGATIVACAO_BOLETO_INCLUIDA("NEGATIVACAO_BOLETO_INCLUIDA"),
    NEGATIVACAO_BOLETO_EXCLUIDA("NEGATIVACAO_BOLETO_EXCLUIDA")
    ;

    private final String nome;

    private static final Map<String, EventType> NOMES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        for (final EventType eventType : EnumSet.allOf(EventType.class)){
            NOMES.put(eventType.name().toLowerCase(), eventType);
        }
    }

    public static EventType entryOf(final String nome){
        return StringUtils.isEmpty(nome) ? null : NOMES.get(nome.toLowerCase());
    }
}

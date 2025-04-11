package br.com.asgard.processor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.Integer.MIN_VALUE;

@AllArgsConstructor
@Getter
public enum BoletoAlteradoAcoes {
    ATUALIZAR_BASE(MIN_VALUE),
    EXCLUIR_NEGATIVACAO(2),
    REAGENDAR_NEGATIVACAO(3);

    private final int prioridade;
}

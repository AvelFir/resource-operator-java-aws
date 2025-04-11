package br.com.asgard.domain.enums;

import lombok.Getter;

@Getter
public enum StatusProcessamentoComando {
    PROCESSANDO,
    PROCESSADO_SUCESSO,
    PROCESSADO_FALHA;
}

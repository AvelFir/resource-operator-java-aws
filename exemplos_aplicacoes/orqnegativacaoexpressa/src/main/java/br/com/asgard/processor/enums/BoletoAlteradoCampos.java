package br.com.asgard.processor.enums;

import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

import static br.com.asgard.processor.enums.BoletoAlteradoAcoes.*;
import static br.com.asgard.processor.function.BoletoNegativadoFuntion.*;

@AllArgsConstructor
@Getter
public enum BoletoAlteradoCampos {
    CODIGO_CANAL_ORIGEM(getCanalOrigem(), ATUALIZAR_BASE),
    CODIGO_OPERADOR(getCodigoOperador(), EXCLUIR_NEGATIVACAO),
    CODIGO_ESPECIE(getCodigoEspecie(), EXCLUIR_NEGATIVACAO),
    NUMERO_DOCUMENTO_PAGADOR(getNumeroDocumentoPagador(), EXCLUIR_NEGATIVACAO),
    VALOR_DIVIDA(getValorDivida(), EXCLUIR_NEGATIVACAO),
    DATA_VENCIMENTO(getDataVencimento(), REAGENDAR_NEGATIVACAO),
    QUANTIDADE_DIAS_APOS_VENCIMENTO(getQuantidadeDiasAposVencimento(), REAGENDAR_NEGATIVACAO),
    INDICADOR_DIA_UTIL(getIndicadorDiaUtil(), REAGENDAR_NEGATIVACAO)
    ;

    private final Function<BoletoNegativado, ?> getter;
    private final BoletoAlteradoAcoes acoes;

    public Object getValue(BoletoNegativado boletoNegativado) {
        return getter.apply(boletoNegativado);
    }
}

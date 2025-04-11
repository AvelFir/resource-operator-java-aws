package br.com.asgard.processor.function;

import br.com.asgard.domain.boleto.*;
import br.com.asgard.domain.negativacao_boleto.BoletoNegativado;
import br.com.fluentvalidator.function.FunctionBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Function;

public final class BoletoNegativadoFuntion {
    private BoletoNegativadoFuntion(){}

    public static Function<BoletoNegativado, String> getCanalOrigem(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getCodigoCanalOrigem);
    }

    public static Function<BoletoNegativado, LocalDate> getDataVencimento(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getDataVencimento);
    }

    public static Function<BoletoNegativado, BigDecimal> getValorDivida(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getValor);
    }

    public static Function<BoletoNegativado, String> getCodigoOperador(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getCodigoOperador);
    }

    public static Function<BoletoNegativado, CodigoEspecieBoleto> getCodigoEspecie(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getCodigoEspecie);
    }

    public static Function<BoletoNegativado, String> getNumeroDocumentoPagador(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getPagador).andThen(Pagador::getPessoa).andThen(Pessoa::getNumeroDocumentoIdentificacaoPessoa);
    }

    public static Function<BoletoNegativado, Integer> getQuantidadeDiasAposVencimento(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getNegativacao).andThen(Negativacao::getQuantidadeDiaAposVencimento);
    }

    public static Function<BoletoNegativado, Boolean> getIndicadorDiaUtil(){
        return FunctionBuilder.of(BoletoNegativado::getBoletoCobranca).andThen(BoletoCobranca::getDadoBoleto).andThen(DadoBoleto::getNegativacao).andThen(Negativacao::getIndicadorDiaUtil);
    }
}

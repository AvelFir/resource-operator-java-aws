package br.com.asgard.domain.boleto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DadoBoleto {
    private String codigoOperador;
    private String codigoCanalOrigem;
    private String descricaoInstrumentoCobranca = "boleto";
    private LocalDate dataVencimento;
    private BigDecimal valor;
    private String nossoNumero;
    private String numeroCarteira;
    private String digitoVerificadorNossoNumero;
    private Negativacao negativacao;
    private Pagador pagador;
    private String situacaoBoleto;
    private CodigoEspecieBoleto codigoEspecie;
}

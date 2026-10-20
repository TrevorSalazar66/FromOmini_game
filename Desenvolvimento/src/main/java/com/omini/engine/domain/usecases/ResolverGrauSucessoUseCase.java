package com.omini.engine.domain.usecases;

import com.omini.engine.domain.models.GrauSucessoEnum;

/**
 * Caso de Uso: Confronta o valor sorteado contra o valor Alvo dinâmico e resolve nos 5 Graus de Sucesso.
 */
public final class ResolverGrauSucessoUseCase {

    public GrauSucessoEnum executar(double valorSorteado, double valorAlvo) {
        if (valorAlvo <= 0) {
            throw new IllegalArgumentException("O valor alvo deve ser estritamente positivo. Recebido: " + valorAlvo);
        }
        if (valorSorteado < 0) {
            throw new IllegalArgumentException("O valor sorteado não pode ser negativo. Recebido: " + valorSorteado);
        }

        double delta = (valorSorteado - valorAlvo) / valorAlvo;

        if (delta <= -0.50) {
            return GrauSucessoEnum.FALHA_CRITICA;
        }
        if (delta <= -0.15) {
            return GrauSucessoEnum.FALHA;
        }
        if (delta < 0.00) {
            return GrauSucessoEnum.SUCESSO_PARCIAL;
        }
        if (delta < 0.20) {
            return GrauSucessoEnum.SUCESSO_TOTAL;
        }

        return GrauSucessoEnum.SUCESSO_EXTREMO;
    }
}

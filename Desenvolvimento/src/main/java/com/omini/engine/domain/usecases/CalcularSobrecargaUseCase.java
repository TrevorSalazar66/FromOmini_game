package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.EstadoSobrecargaVO;
import com.omini.engine.domain.models.NivelSobrecargaEnum;

/**
 * Caso de Uso responsável por avaliar o estado de carga e sobrecarga de uma criatura
 * com base no peso total transportado e em seu atributo de Força.
 */
public final class CalcularSobrecargaUseCase {

    private static final double FATOR_CAPACIDADE_CONFORTAVEL = 5.0; // kg por ponto de Força
    private static final double FATOR_CAPACIDADE_MAXIMA = 10.0;     // kg por ponto de Força

    public EstadoSobrecargaVO executar(double pesoTotalCarregado, int atributoForca) {
        if (atributoForca < 1) {
            throw new IllegalArgumentException("O atributo Força deve ser no mínimo 1. Informado: " + atributoForca);
        }
        if (pesoTotalCarregado < 0.0) {
            throw new IllegalArgumentException("O peso total carregado não pode ser negativo. Informado: " + pesoTotalCarregado);
        }

        double capacidadeConfortavel = atributoForca * FATOR_CAPACIDADE_CONFORTAVEL;
        double capacidadeMaxima = atributoForca * FATOR_CAPACIDADE_MAXIMA;
        double taxaCarga = capacidadeConfortavel > 0 ? (pesoTotalCarregado / capacidadeConfortavel) : 1.0;
        boolean estaSobrecarregado = pesoTotalCarregado > capacidadeConfortavel;

        NivelSobrecargaEnum nivel;
        if (pesoTotalCarregado <= capacidadeConfortavel * 0.50) {
            nivel = NivelSobrecargaEnum.LEVE;
        } else if (pesoTotalCarregado <= capacidadeConfortavel) {
            nivel = NivelSobrecargaEnum.CONFORTAVEL;
        } else if (pesoTotalCarregado <= capacidadeConfortavel * 1.50) {
            nivel = NivelSobrecargaEnum.PESADA;
        } else if (pesoTotalCarregado <= capacidadeMaxima) {
            nivel = NivelSobrecargaEnum.CRITICA;
        } else {
            nivel = NivelSobrecargaEnum.INCAPACITADO;
        }

        return new EstadoSobrecargaVO(
                pesoTotalCarregado,
                capacidadeConfortavel,
                capacidadeMaxima,
                taxaCarga,
                estaSobrecarregado,
                nivel.getPenalidadeAPPercentual(),
                nivel.getPenalidadeEsquivaPercentual(),
                nivel
        );
    }
}

package com.omini.engine.domain.usecases;

/**
 * Caso de Uso: Calcula proceduralmente o peso/massa de um item a partir da sua área em slots e da densidade do material.
 * Fórmula: Peso = (Largura * Altura) * DensidadeMaterial
 */
public final class CalcularMassaProceduralUseCase {

    public double executar(int larguraSlots, int alturaSlots, double densidadeMaterial) {
        if (larguraSlots < 1) {
            throw new IllegalArgumentException("A largura em slots deve ser de pelo menos 1. Recebido: " + larguraSlots);
        }
        if (alturaSlots < 1) {
            throw new IllegalArgumentException("A altura em slots deve ser de pelo menos 1. Recebido: " + alturaSlots);
        }
        if (densidadeMaterial <= 0) {
            throw new IllegalArgumentException("A densidade do material deve ser maior que zero. Recebido: " + densidadeMaterial);
        }

        int areaSlots = larguraSlots * alturaSlots;
        return areaSlots * densidadeMaterial;
    }
}

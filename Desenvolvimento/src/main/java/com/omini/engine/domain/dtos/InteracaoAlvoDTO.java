package com.omini.engine.domain.dtos;

/**
 * DTO com os parâmetros necessários para calcular a Classe de Dificuldade (Alvo) dinâmica de uma interação.
 */
public record InteracaoAlvoDTO(
    double durezaMaterial,
    int nivelAlvo,
    double multiplicadorFerramenta
) {
    public InteracaoAlvoDTO {
        if (durezaMaterial <= 0) {
            throw new IllegalArgumentException("A dureza do material deve ser positiva. Recebido: " + durezaMaterial);
        }
        if (nivelAlvo < 1) {
            throw new IllegalArgumentException("O nível da entidade alvo deve ser pelo menos 1. Recebido: " + nivelAlvo);
        }
        if (multiplicadorFerramenta <= 0) {
            throw new IllegalArgumentException("O multiplicador da ferramenta deve ser maior que zero. Recebido: " + multiplicadorFerramenta);
        }
    }
}

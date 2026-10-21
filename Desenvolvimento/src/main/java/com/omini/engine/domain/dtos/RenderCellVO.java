package com.omini.engine.domain.dtos;

/**
 * Value Object representando uma célula renderizada na viewport para exibição gráfica 2D.
 */
public record RenderCellVO(
        int mundoX,
        int mundoY,
        int telaX,
        int telaY,
        char glifo,
        String corHex,
        String camada,
        boolean visivelNoFov,
        boolean jaExplorado
) {}

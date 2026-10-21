package com.omini.engine.domain.dtos;

/**
 * Value Object representando uma ação do menu contextual radial (estilo Shattered Pixel Dungeon).
 */
public record AcaoContextualVO(
        String id,
        String rotulo,
        String iconeSvg,
        boolean habilitado,
        int custoAP
) {
    public static AcaoContextualVO criar(String id, String rotulo, String iconeSvg, int custoAP) {
        return new AcaoContextualVO(id, rotulo, iconeSvg, true, custoAP);
    }

    public static AcaoContextualVO desabilitada(String id, String rotulo, String iconeSvg) {
        return new AcaoContextualVO(id, rotulo, iconeSvg, false, 0);
    }
}

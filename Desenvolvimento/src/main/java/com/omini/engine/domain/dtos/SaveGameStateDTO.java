package com.omini.engine.domain.dtos;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object que encapsula o snapshot completo do estado do mundo para salvamento/carregamento.
 */
public record SaveGameStateDTO(
        String versaoEngine,
        long timestampGravacao,
        int relogioTicks,
        int jogadorX,
        int jogadorY,
        Map<String, Integer> jogadorStats,
        Map<String, Double> jogadorSobrevivencia,
        Map<String, Double> jogadorDorEFoco,
        List<Map<String, Object>> jogadorPartesAnatomia,
        List<Map<String, Object>> jogadorItensInventario,
        int mapaLargura,
        int mapaAltura,
        List<String> mapaTilesExplorados,
        List<Map<String, Object>> entidadesMundo
) {}

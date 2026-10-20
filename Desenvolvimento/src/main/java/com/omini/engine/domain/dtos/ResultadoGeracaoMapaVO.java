package com.omini.engine.domain.dtos;

import com.omini.engine.domain.models.TileMap;

import java.util.Collections;
import java.util.List;

/**
 * Resultado da geração procedural de mapa contendo o mapa gerado e coordenadas estratégicas.
 */
public final class ResultadoGeracaoMapaVO {

    private final TileMap mapa;
    private final CoordenadaVO pontoInicial;
    private final List<CoordenadaVO> centrosSalas;

    public ResultadoGeracaoMapaVO(TileMap mapa, CoordenadaVO pontoInicial, List<CoordenadaVO> centrosSalas) {
        this.mapa = mapa;
        this.pontoInicial = pontoInicial;
        this.centrosSalas = centrosSalas != null ? Collections.unmodifiableList(centrosSalas) : Collections.emptyList();
    }

    public TileMap getMapa() {
        return mapa;
    }

    public CoordenadaVO getPontoInicial() {
        return pontoInicial;
    }

    public List<CoordenadaVO> getCentrosSalas() {
        return centrosSalas;
    }
}

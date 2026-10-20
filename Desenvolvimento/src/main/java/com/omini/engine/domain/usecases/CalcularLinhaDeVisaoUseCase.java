package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.models.TileMap;

import java.util.HashSet;
import java.util.Set;

/**
 * Caso de Uso responsável pelo cálculo determinístico de Linha de Visão (Field of View / Line of Sight),
 * utilizando traçado de raios (Raycasting via algoritmo de Bresenham) com respeito a obstáculos e fumaça.
 */
public final class CalcularLinhaDeVisaoUseCase {

    public static final int RAIO_VISAO_PADRAO = 25;

    public boolean temLinhaDeVisao(TileMap mapa, CoordenadaVO origem, CoordenadaVO destino, int raioMaximo) {
        if (origem == null || destino == null) return false;
        return temLinhaDeVisao(mapa, origem.getX(), origem.getY(), destino.getX(), destino.getY(), raioMaximo);
    }

    public boolean temLinhaDeVisao(TileMap mapa, CoordenadaVO origem, CoordenadaVO destino) {
        return temLinhaDeVisao(mapa, origem, destino, RAIO_VISAO_PADRAO);
    }

    public boolean temLinhaDeVisao(TileMap mapa, int x0, int y0, int x1, int y1, int raioMaximo) {
        if (mapa == null) {
            throw new IllegalArgumentException("O mapa não pode ser nulo.");
        }
        if (!mapa.isDentroDosLimites(x0, y0) || !mapa.isDentroDosLimites(x1, y1)) {
            return false;
        }

        // Se origem e destino forem o mesmo tile
        if (x0 == x1 && y0 == y1) {
            return true;
        }

        // Verificação de alcance máximo euclidiano
        int dxAbs = Math.abs(x1 - x0);
        int dyAbs = Math.abs(y1 - y0);
        if (Math.sqrt(dxAbs * dxAbs + dyAbs * dyAbs) > raioMaximo) {
            return false;
        }

        // Algoritmo de Bresenham para traçado de raio discreto
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;

        int currX = x0;
        int currY = y0;

        while (true) {
            // Se chegamos ao destino, o destino é visível
            if (currX == x1 && currY == y1) {
                return true;
            }

            // Não verifica bloqueio no tile de origem (o próprio observador)
            if (!(currX == x0 && currY == y0)) {
                if (mapa.isBloqueiaVisao(currX, currY)) {
                    return false; // Raio bloqueado por obstáculo intermediário
                }
            }

            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                currX += sx;
            }
            if (e2 <= dx) {
                err += dx;
                currY += sy;
            }
        }
    }

    public Set<CoordenadaVO> calcularCampoVisao(TileMap mapa, int origemX, int origemY, int raio) {
        Set<CoordenadaVO> visiveis = new HashSet<>();
        if (mapa == null || !mapa.isDentroDosLimites(origemX, origemY) || raio <= 0) {
            return visiveis;
        }

        int minX = Math.max(0, origemX - raio);
        int maxX = Math.min(mapa.getLargura() - 1, origemX + raio);
        int minY = Math.max(0, origemY - raio);
        int maxY = Math.min(mapa.getAltura() - 1, origemY + raio);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (temLinhaDeVisao(mapa, origemX, origemY, x, y, raio)) {
                    visiveis.add(new CoordenadaVO(x, y));
                }
            }
        }

        return visiveis;
    }
}

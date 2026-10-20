package com.omini.engine.domain.models;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.ecs.Entity;

import java.util.*;

/**
 * Representação espacial bidimensional do mapa do mundo, contendo os tiles de terreno e entidades localizadas.
 */
public final class TileMap {

    private final int largura;
    private final int altura;
    private final TileTypeEnum[][] tiles;
    private final Map<CoordenadaVO, List<Entity>> entidadesPorCoordenada;

    public TileMap(int largura, int altura, TileTypeEnum preenchimentoPadrao) {
        if (largura <= 0 || altura <= 0) {
            throw new IllegalArgumentException("Dimensões do mapa devem ser estritamente positivas.");
        }
        this.largura = largura;
        this.altura = altura;
        this.tiles = new TileTypeEnum[largura][altura];
        this.entidadesPorCoordenada = new HashMap<>();

        TileTypeEnum padrao = preenchimentoPadrao != null ? preenchimentoPadrao : TileTypeEnum.CHAO_PEDRA;
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                tiles[x][y] = padrao;
            }
        }
    }

    public TileMap(int largura, int altura) {
        this(largura, altura, TileTypeEnum.CHAO_PEDRA);
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public boolean isDentroDosLimites(int x, int y) {
        return x >= 0 && x < largura && y >= 0 && y < altura;
    }

    public TileTypeEnum getTile(int x, int y) {
        if (!isDentroDosLimites(x, y)) {
            return TileTypeEnum.PAREDE_GRANITO;
        }
        return tiles[x][y];
    }

    public void setTile(int x, int y, TileTypeEnum tipo) {
        if (!isDentroDosLimites(x, y)) {
            throw new IndexOutOfBoundsException("Coordenada (" + x + ", " + y + ") fora dos limites do mapa " + largura + "x" + altura);
        }
        this.tiles[x][y] = tipo != null ? tipo : TileTypeEnum.CHAO_PEDRA;
    }

    public boolean isPassavel(int x, int y) {
        if (!isDentroDosLimites(x, y)) return false;
        return tiles[x][y].isPassavel();
    }

    public boolean isBloqueiaVisao(int x, int y) {
        if (!isDentroDosLimites(x, y)) return true;
        // Paredes bloqueiam visão
        if (tiles[x][y].isBloqueiaVisao()) return true;

        // Entidades ou campos no tile (como fumaça densa) podem bloquear visão
        List<Entity> lista = entidadesPorCoordenada.get(new CoordenadaVO(x, y));
        if (lista != null) {
            for (Entity e : lista) {
                if (e.hasTag("bloqueia_visao") || e.hasTag("field_smoke")) {
                    return true;
                }
            }
        }
        return false;
    }

    public double getCustoMovimento(int x, int y) {
        if (!isDentroDosLimites(x, y)) return Double.MAX_VALUE;
        return tiles[x][y].getCustoMovimento();
    }

    public void adicionarEntidade(int x, int y, Entity entidade) {
        if (!isDentroDosLimites(x, y) || entidade == null) return;
        CoordenadaVO coord = new CoordenadaVO(x, y);
        entidadesPorCoordenada.computeIfAbsent(coord, k -> new ArrayList<>()).add(entidade);
    }

    public void removerEntidade(int x, int y, Entity entidade) {
        CoordenadaVO coord = new CoordenadaVO(x, y);
        List<Entity> lista = entidadesPorCoordenada.get(coord);
        if (lista != null) {
            lista.remove(entidade);
            if (lista.isEmpty()) {
                entidadesPorCoordenada.remove(coord);
            }
        }
    }

    public List<Entity> getEntidadesNoTile(int x, int y) {
        CoordenadaVO coord = new CoordenadaVO(x, y);
        List<Entity> lista = entidadesPorCoordenada.get(coord);
        return lista != null ? Collections.unmodifiableList(lista) : Collections.emptyList();
    }
}

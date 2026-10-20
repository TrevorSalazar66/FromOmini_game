package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.dtos.ParametrosGeracaoMapaDTO;
import com.omini.engine.domain.dtos.ResultadoGeracaoMapaVO;
import com.omini.engine.domain.models.TileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Caso de uso responsável pela geração procedural determinística de mapas estilo masmorra/instalação.
 * Posiciona salas retangulares não sobrepostas e as conecta via corredores ortogonais.
 */
public final class GerarMapaProceduralUseCase {

    private static final class SalaRetangular {
        final int x;
        final int y;
        final int largura;
        final int altura;

        SalaRetangular(int x, int y, int largura, int altura) {
            this.x = x;
            this.y = y;
            this.largura = largura;
            this.altura = altura;
        }

        boolean intersecta(SalaRetangular outra, int margem) {
            return (this.x - margem < outra.x + outra.largura &&
                    this.x + this.largura + margem > outra.x &&
                    this.y - margem < outra.y + outra.altura &&
                    this.y + this.altura + margem > outra.y);
        }

        CoordenadaVO getCentro() {
            return new CoordenadaVO(x + largura / 2, y + altura / 2);
        }
    }

    public ResultadoGeracaoMapaVO executar(ParametrosGeracaoMapaDTO params) {
        if (params == null) {
            throw new IllegalArgumentException("Parâmetros de geração não podem ser nulos.");
        }

        TileMap mapa = new TileMap(params.getLargura(), params.getAltura(), params.getTipoParede());
        Random random = new Random(params.getSeed());

        List<SalaRetangular> salas = new ArrayList<>();
        List<CoordenadaVO> centrosSalas = new ArrayList<>();

        int tentativasMaximas = params.getMaxSalas() * 10;
        int tentativas = 0;

        while (salas.size() < params.getMaxSalas() && tentativas < tentativasMaximas) {
            tentativas++;

            int deltaW = params.getTamanhoMaxSala() - params.getTamanhoMinSala();
            int deltaH = params.getTamanhoMaxSala() - params.getTamanhoMinSala();

            int w = params.getTamanhoMinSala() + (deltaW > 0 ? random.nextInt(deltaW + 1) : 0);
            int h = params.getTamanhoMinSala() + (deltaH > 0 ? random.nextInt(deltaH + 1) : 0);

            int limiteX = params.getLargura() - w - 1;
            int limiteY = params.getAltura() - h - 1;

            if (limiteX <= 1 || limiteY <= 1) {
                continue;
            }

            int x = 1 + random.nextInt(limiteX);
            int y = 1 + random.nextInt(limiteY);

            SalaRetangular novaSala = new SalaRetangular(x, y, w, h);

            boolean colide = false;
            for (SalaRetangular existente : salas) {
                if (novaSala.intersecta(existente, 1)) {
                    colide = true;
                    break;
                }
            }

            if (!colide) {
                // Escavar a nova sala
                for (int sx = novaSala.x; sx < novaSala.x + novaSala.largura; sx++) {
                    for (int sy = novaSala.y; sy < novaSala.y + novaSala.altura; sy++) {
                        mapa.setTile(sx, sy, params.getTipoChao());
                    }
                }

                CoordenadaVO centroNovo = novaSala.getCentro();

                // Conectar com a sala anterior se existir
                if (!salas.isEmpty()) {
                    CoordenadaVO centroAnterior = salas.get(salas.size() - 1).getCentro();
                    conectarCentrosComCorredor(mapa, centroAnterior, centroNovo, random, params);
                }

                salas.add(novaSala);
                centrosSalas.add(centroNovo);
            }
        }

        CoordenadaVO pontoInicial = centrosSalas.isEmpty() ? new CoordenadaVO(1, 1) : centrosSalas.get(0);

        return new ResultadoGeracaoMapaVO(mapa, pontoInicial, centrosSalas);
    }

    private void conectarCentrosComCorredor(TileMap mapa, CoordenadaVO c1, CoordenadaVO c2, Random random, ParametrosGeracaoMapaDTO params) {
        boolean horizontalPrimeiro = random.nextBoolean();

        if (horizontalPrimeiro) {
            escavarTunelHorizontal(mapa, c1.getX(), c2.getX(), c1.getY(), params);
            escavarTunelVertical(mapa, c1.getY(), c2.getY(), c2.getX(), params);
        } else {
            escavarTunelVertical(mapa, c1.getY(), c2.getY(), c1.getX(), params);
            escavarTunelHorizontal(mapa, c1.getX(), c2.getX(), c2.getY(), params);
        }
    }

    private void escavarTunelHorizontal(TileMap mapa, int x1, int x2, int y, ParametrosGeracaoMapaDTO params) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        for (int x = minX; x <= maxX; x++) {
            if (mapa.isDentroDosLimites(x, y)) {
                mapa.setTile(x, y, params.getTipoChao());
            }
        }
    }

    private void escavarTunelVertical(TileMap mapa, int y1, int y2, int x, ParametrosGeracaoMapaDTO params) {
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        for (int y = minY; y <= maxY; y++) {
            if (mapa.isDentroDosLimites(x, y)) {
                mapa.setTile(x, y, params.getTipoChao());
            }
        }
    }
}

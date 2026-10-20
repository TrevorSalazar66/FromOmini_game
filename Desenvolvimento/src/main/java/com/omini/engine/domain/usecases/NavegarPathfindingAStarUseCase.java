package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.models.TileMap;

import java.util.*;

/**
 * Caso de Uso responsável por encontrar a rota mais curta e viável em grade usando o algoritmo A* (A-Star).
 * Considera passabilidade, custos variáveis de terreno e evita corte de quinas em paredes.
 */
public final class NavegarPathfindingAStarUseCase {

    private static final int[][] DIRECOES = {
            {0, -1},  // Norte
            {1, 0},   // Leste
            {0, 1},   // Sul
            {-1, 0},  // Oeste
            {1, -1},  // Nordeste
            {1, 1},   // Sudeste
            {-1, 1},  // Sudoeste
            {-1, -1}  // Noroeste
    };

    private static final class Node implements Comparable<Node> {
        final CoordenadaVO coord;
        final double gScore;
        final double fScore;

        Node(CoordenadaVO coord, double gScore, double fScore) {
            this.coord = coord;
            this.gScore = gScore;
            this.fScore = fScore;
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(this.fScore, o.fScore);
        }
    }

    public List<CoordenadaVO> encontrarCaminho(TileMap mapa, int xOrigem, int yOrigem, int xDestino, int yDestino) {
        if (mapa == null) {
            throw new IllegalArgumentException("O mapa não pode ser nulo.");
        }
        if (!mapa.isDentroDosLimites(xOrigem, yOrigem) || !mapa.isDentroDosLimites(xDestino, yDestino)) {
            return Collections.emptyList();
        }

        CoordenadaVO inicio = new CoordenadaVO(xOrigem, yOrigem);
        CoordenadaVO destino = new CoordenadaVO(xDestino, yDestino);

        if (inicio.equals(destino)) {
            return Collections.emptyList();
        }

        if (!mapa.isPassavel(xDestino, yDestino)) {
            return Collections.emptyList(); // Destino intransponível
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<CoordenadaVO, CoordenadaVO> cameFrom = new HashMap<>();
        Map<CoordenadaVO, Double> gScores = new HashMap<>();

        gScores.put(inicio, 0.0);
        openSet.add(new Node(inicio, 0.0, inicio.distanciaEuclidiana(destino)));

        while (!openSet.isEmpty()) {
            Node atual = openSet.poll();
            CoordenadaVO atualCoord = atual.coord;

            if (atualCoord.equals(destino)) {
                return reconstruirCaminho(cameFrom, atualCoord);
            }

            for (int[] dir : DIRECOES) {
                int nx = atualCoord.getX() + dir[0];
                int ny = atualCoord.getY() + dir[1];

                if (!mapa.isDentroDosLimites(nx, ny) || !mapa.isPassavel(nx, ny)) {
                    continue;
                }

                // Prevenção de corte de quina em movimentos diagonais
                boolean isDiagonal = dir[0] != 0 && dir[1] != 0;
                if (isDiagonal) {
                    if (!mapa.isPassavel(atualCoord.getX() + dir[0], atualCoord.getY()) ||
                        !mapa.isPassavel(atualCoord.getX(), atualCoord.getY() + dir[1])) {
                        continue; // Quina bloqueada
                    }
                }

                double custoTerreno = mapa.getCustoMovimento(nx, ny);
                double fatorDistancia = isDiagonal ? 1.4142 : 1.0;
                double novoGScore = gScores.getOrDefault(atualCoord, Double.MAX_VALUE) + (custoTerreno * fatorDistancia);

                CoordenadaVO vizinhoCoord = new CoordenadaVO(nx, ny);
                if (novoGScore < gScores.getOrDefault(vizinhoCoord, Double.MAX_VALUE)) {
                    cameFrom.put(vizinhoCoord, atualCoord);
                    gScores.put(vizinhoCoord, novoGScore);
                    double fScore = novoGScore + vizinhoCoord.distanciaEuclidiana(destino);
                    openSet.add(new Node(vizinhoCoord, novoGScore, fScore));
                }
            }
        }

        return Collections.emptyList(); // Nenhum caminho encontrado
    }

    public List<CoordenadaVO> buscarCaminho(TileMap mapa, CoordenadaVO origem, CoordenadaVO destino) {
        return encontrarCaminho(mapa, origem, destino);
    }

    public List<CoordenadaVO> encontrarCaminho(TileMap mapa, CoordenadaVO origem, CoordenadaVO destino) {
        if (origem == null || destino == null) return Collections.emptyList();
        return encontrarCaminho(mapa, origem.getX(), origem.getY(), destino.getX(), destino.getY());
    }

    private List<CoordenadaVO> reconstruirCaminho(Map<CoordenadaVO, CoordenadaVO> cameFrom, CoordenadaVO atual) {
        List<CoordenadaVO> caminho = new ArrayList<>();
        CoordenadaVO curr = atual;
        while (curr != null) {
            caminho.add(curr);
            curr = cameFrom.get(curr);
        }
        Collections.reverse(caminho);
        return caminho;
    }
}

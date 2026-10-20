package com.omini.engine.unit;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TileTypeEnum;
import com.omini.engine.domain.usecases.CalcularLinhaDeVisaoUseCase;
import com.omini.engine.domain.usecases.NavegarPathfindingAStarUseCase;

import java.util.List;

public final class GridEPathfindingTest {

    public static void main(String[] args) {
        GridEPathfindingTest test = new GridEPathfindingTest();
        test.deveCalcularDistanciasCorretasEmCoordenadas();
        test.deveValidarLinhaDeVisaoEmTerrenoAbertoEComObstaculos();
        test.deveBloquearLinhaDeVisaoComFumaca();
        test.deveEncontrarCaminhoOtimoComAStar();
        test.deveRetornarVazioQuandoDestinoInacessivel();
        System.out.println("✅ GridEPathfindingTest: Todos os testes passaram!");
    }

    public void deveCalcularDistanciasCorretasEmCoordenadas() {
        CoordenadaVO c1 = new CoordenadaVO(0, 0);
        CoordenadaVO c2 = new CoordenadaVO(3, 4);

        assert c1.distanciaManhattan(c2) == 7 : "Distância Manhattan incorreta";
        assert Math.abs(c1.distanciaEuclidiana(c2) - 5.0) < 0.0001 : "Distância Euclidiana incorreta";
        assert c1.distanciaChebyshev(c2) == 4 : "Distância Chebyshev incorreta";
        assert c1.equals(new CoordenadaVO(0, 0)) : "Igualdade de coordenadas falhou";
    }

    public void deveValidarLinhaDeVisaoEmTerrenoAbertoEComObstaculos() {
        TileMap mapa = new TileMap(10, 10, TileTypeEnum.CHAO_PEDRA);
        CalcularLinhaDeVisaoUseCase fovUseCase = new CalcularLinhaDeVisaoUseCase();

        // Linha limpa de (1, 1) até (1, 5)
        assert fovUseCase.temLinhaDeVisao(mapa, new CoordenadaVO(1, 1), new CoordenadaVO(1, 5)) : "Deveria ter visão limpa";

        // Adicionar parede em (1, 3)
        mapa.setTile(1, 3, TileTypeEnum.PAREDE_GRANITO);
        assert !fovUseCase.temLinhaDeVisao(mapa, new CoordenadaVO(1, 1), new CoordenadaVO(1, 5)) : "Parede deveria bloquear a visão";

        // Ponto adjacente à parede ainda é visível
        assert fovUseCase.temLinhaDeVisao(mapa, new CoordenadaVO(1, 1), new CoordenadaVO(1, 2)) : "Ponto antes da parede deve ser visível";
    }

    public void deveBloquearLinhaDeVisaoComFumaca() {
        TileMap mapa = new TileMap(10, 10, TileTypeEnum.CHAO_PEDRA);
        CalcularLinhaDeVisaoUseCase fovUseCase = new CalcularLinhaDeVisaoUseCase();

        // Adicionar entidade de fumaça em (2, 2)
        Entity fumaca = new Entity("fumaca_01", "Fumaça Densa");
        fumaca.addTag("field_smoke");
        mapa.adicionarEntidade(2, 2, fumaca);

        assert !fovUseCase.temLinhaDeVisao(mapa, new CoordenadaVO(2, 0), new CoordenadaVO(2, 4)) : "Fumaça densa deve bloquear visão";
    }

    public void deveEncontrarCaminhoOtimoComAStar() {
        TileMap mapa = new TileMap(7, 7, TileTypeEnum.CHAO_PEDRA);
        // Criar uma parede horizontal em Y=3, de X=1 a X=5
        for (int x = 1; x <= 5; x++) {
            mapa.setTile(x, 3, TileTypeEnum.PAREDE_GRANITO);
        }

        NavegarPathfindingAStarUseCase aStar = new NavegarPathfindingAStarUseCase();
        CoordenadaVO inicio = new CoordenadaVO(3, 1);
        CoordenadaVO destino = new CoordenadaVO(3, 5);

        List<CoordenadaVO> caminho = aStar.buscarCaminho(mapa, inicio, destino);

        assert !caminho.isEmpty() : "Deveria encontrar caminho contornando a parede";
        assert caminho.get(0).equals(inicio) : "Início do caminho incorreto";
        assert caminho.get(caminho.size() - 1).equals(destino) : "Destino do caminho incorreto";

        // Garantir que nenhum passo passa por uma parede
        for (CoordenadaVO passo : caminho) {
            assert mapa.isPassavel(passo.getX(), passo.getY()) : "Caminho atravessou parede em " + passo;
        }
    }

    public void deveRetornarVazioQuandoDestinoInacessivel() {
        TileMap mapa = new TileMap(5, 5, TileTypeEnum.CHAO_PEDRA);
        // Cercar (2, 2) completamente com paredes
        mapa.setTile(1, 1, TileTypeEnum.PAREDE_GRANITO);
        mapa.setTile(2, 1, TileTypeEnum.PAREDE_GRANITO);
        mapa.setTile(3, 1, TileTypeEnum.PAREDE_GRANITO);
        mapa.setTile(1, 2, TileTypeEnum.PAREDE_GRANITO);
        mapa.setTile(3, 2, TileTypeEnum.PAREDE_GRANITO);
        mapa.setTile(1, 3, TileTypeEnum.PAREDE_GRANITO);
        mapa.setTile(2, 3, TileTypeEnum.PAREDE_GRANITO);
        mapa.setTile(3, 3, TileTypeEnum.PAREDE_GRANITO);

        NavegarPathfindingAStarUseCase aStar = new NavegarPathfindingAStarUseCase();
        List<CoordenadaVO> caminho = aStar.buscarCaminho(mapa, new CoordenadaVO(0, 0), new CoordenadaVO(2, 2));
        assert caminho.isEmpty() : "Caminho deveria ser vazio para destino inacessível";
    }
}

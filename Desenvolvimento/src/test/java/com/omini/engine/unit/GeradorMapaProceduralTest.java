package com.omini.engine.unit;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.dtos.ParametrosGeracaoMapaDTO;
import com.omini.engine.domain.dtos.ResultadoGeracaoMapaVO;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TileTypeEnum;
import com.omini.engine.domain.usecases.GerarMapaProceduralUseCase;
import com.omini.engine.domain.usecases.NavegarPathfindingAStarUseCase;

import java.util.List;

public final class GeradorMapaProceduralTest {

    public static void main(String[] args) {
        GeradorMapaProceduralTest test = new GeradorMapaProceduralTest();
        test.deveGerarMapaComDeterminismoPorSeed();
        test.deveConectarSalasPermitindoNavegacao();
        test.deveValidarFailFastParametrosInvalidos();
        System.out.println("✅ GeradorMapaProceduralTest: Todos os testes passaram!");
    }

    public void deveGerarMapaComDeterminismoPorSeed() {
        GerarMapaProceduralUseCase useCase = new GerarMapaProceduralUseCase();
        ParametrosGeracaoMapaDTO params1 = new ParametrosGeracaoMapaDTO(40, 40, 6, 4, 8, 12345L);
        ParametrosGeracaoMapaDTO params2 = new ParametrosGeracaoMapaDTO(40, 40, 6, 4, 8, 12345L);

        ResultadoGeracaoMapaVO res1 = useCase.executar(params1);
        ResultadoGeracaoMapaVO res2 = useCase.executar(params2);

        TileMap mapa1 = res1.getMapa();
        TileMap mapa2 = res2.getMapa();

        assert mapa1.getLargura() == 40 && mapa1.getAltura() == 40 : "Dimensões incorretas";
        assert res1.getPontoInicial().equals(res2.getPontoInicial()) : "Ponto inicial determinístico divergiu";
        assert res1.getCentrosSalas().size() == res2.getCentrosSalas().size() : "Contagem de salas divergiu";

        // Comparar cada tile
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                assert mapa1.getTile(x, y) == mapa2.getTile(x, y) : "Divergência de tile em (" + x + "," + y + ")";
            }
        }
    }

    public void deveConectarSalasPermitindoNavegacao() {
        GerarMapaProceduralUseCase useCase = new GerarMapaProceduralUseCase();
        ParametrosGeracaoMapaDTO params = new ParametrosGeracaoMapaDTO(50, 50, 5, 5, 10, 99999L);
        ResultadoGeracaoMapaVO resultado = useCase.executar(params);

        List<CoordenadaVO> centros = resultado.getCentrosSalas();
        assert centros.size() >= 2 : "Deveria ter gerado pelo menos 2 salas conectadas";

        TileMap mapa = resultado.getMapa();
        NavegarPathfindingAStarUseCase aStar = new NavegarPathfindingAStarUseCase();

        // Deve existir caminho navegável entre a primeira sala e a última sala gerada
        CoordenadaVO inicio = centros.get(0);
        CoordenadaVO fim = centros.get(centros.size() - 1);

        assert mapa.isPassavel(inicio.getX(), inicio.getY()) : "Centro inicial não é passável";
        assert mapa.isPassavel(fim.getX(), fim.getY()) : "Centro final não é passável";

        List<CoordenadaVO> caminho = aStar.buscarCaminho(mapa, inicio, fim);
        assert !caminho.isEmpty() : "Deve haver caminho entre as salas geradas proceduralmente";
    }

    public void deveValidarFailFastParametrosInvalidos() {
        try {
            new ParametrosGeracaoMapaDTO(4, 4, 2, 2, 3, 1L);
            assert false : "Deveria falhar para dimensões <= 5";
        } catch (IllegalArgumentException ignored) {}

        try {
            new ParametrosGeracaoMapaDTO(20, 20, 2, 5, 3, 1L);
            assert false : "Deveria falhar para minSala > maxSala";
        } catch (IllegalArgumentException ignored) {}

        try {
            new ParametrosGeracaoMapaDTO(20, 20, 0, 3, 5, 1L);
            assert false : "Deveria falhar para maxSalas <= 0";
        } catch (IllegalArgumentException ignored) {}
    }
}

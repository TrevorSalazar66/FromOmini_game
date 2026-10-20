package com.omini.engine.integration;

import com.omini.engine.data.loader.CascadeModLoader;
import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.dtos.ParametrosGeracaoMapaDTO;
import com.omini.engine.domain.dtos.ResultadoGeracaoMapaVO;
import com.omini.engine.domain.dtos.ResultadoReacaoVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TipoAcaoEnum;
import com.omini.engine.domain.usecases.*;

import java.io.File;
import java.util.List;

public final class MapaTurnosAlquimiaIntegrationTest {

    public static void main(String[] args) {
        MapaTurnosAlquimiaIntegrationTest test = new MapaTurnosAlquimiaIntegrationTest();
        test.executarFluxoCompletoDeIntegracao();
        System.out.println("🌟 MapaTurnosAlquimiaIntegrationTest: Todos os testes de integração da Fase 4 passaram!");
    }

    public void executarFluxoCompletoDeIntegracao() {
        System.out.println("--- [FASE 4 INTEGRATION] Inicializando CascadeModLoader e Catálogos ---");

        File diretorioMods = new File("Desenvolvimento/base_game");
        if (!diretorioMods.exists()) {
            diretorioMods = new File("base_game");
        }

        CascadeModLoader loader = new CascadeModLoader();
        loader.carregarTodosOsMods(diretorioMods.getParentFile());

        assert loader.getReactionRepository().buscarReacao("surface_fire", "surface_water").isPresent() : "Reação Fogo + Água não carregada";
        assert loader.getReactionRepository().buscarReacao("surface_fire", "flammable_gas").isPresent() : "Reação Fogo + Gás não carregada";

        System.out.println("--- [FASE 4 INTEGRATION] Gerando Masmorra Procedural Determinística ---");
        GerarMapaProceduralUseCase gerador = new GerarMapaProceduralUseCase();
        ParametrosGeracaoMapaDTO params = new ParametrosGeracaoMapaDTO(40, 40, 5, 5, 8, 424242L);
        ResultadoGeracaoMapaVO resultadoMapa = gerador.executar(params);

        TileMap mapa = resultadoMapa.getMapa();
        CoordenadaVO spawn = resultadoMapa.getPontoInicial();
        List<CoordenadaVO> centrosSalas = resultadoMapa.getCentrosSalas();

        assert centrosSalas.size() >= 2 : "Deveria ter gerado pelo menos duas salas conectadas";
        CoordenadaVO destino = centrosSalas.get(1);

        System.out.println("Spawn no centro da Sala 0: " + spawn + " | Destino na Sala 1: " + destino);

        System.out.println("--- [FASE 4 INTEGRATION] Criando Entidade Sobrevivente ---");
        Entity sobrevivente = new Entity("sobrevivente_alpha", "Sobrevivente Alpha");
        sobrevivente.addComponent(new PositionComponent(spawn.getX(), spawn.getY()));
        sobrevivente.addComponent(new StatsComponent(10, 12, 10, 10)); // Força 10, Destreza 12
        sobrevivente.addComponent(new TurnEnergyComponent(100)); // Começa pronto para agir
        sobrevivente.addComponent(new InventoryGridComponent(10, 10));

        AnatomyComponent anatomia = new AnatomyComponent();
        anatomia.adicionarParte(new BodyPartStateVO("torso", "Tronco", 100.0, 100.0, true, false, false, 0.0));
        anatomia.adicionarParte(new BodyPartStateVO("right_leg", "Perna Direita", 35.0, 35.0, false, false, false, 0.0));
        anatomia.adicionarParte(new BodyPartStateVO("left_leg", "Perna Esquerda", 35.0, 35.0, false, false, false, 0.0));
        sobrevivente.addComponent(anatomia);

        mapa.adicionarEntidade(spawn.getX(), spawn.getY(), sobrevivente);

        System.out.println("--- [FASE 4 INTEGRATION] Navegação A* e Consumo de AP ---");
        NavegarPathfindingAStarUseCase aStar = new NavegarPathfindingAStarUseCase();
        CalcularCustoAcaoAPUseCase calculadorAP = new CalcularCustoAcaoAPUseCase();
        MotorTurnosAPUseCase motorTurnos = new MotorTurnosAPUseCase();

        List<CoordenadaVO> trajeto = aStar.buscarCaminho(mapa, spawn, destino);
        assert !trajeto.isEmpty() : "Trajeto entre salas deve existir";
        assert trajeto.size() > 1 : "Trajeto deve conter passos intermediários";

        // Sobrevivente executa o primeiro passo
        CoordenadaVO proximoPasso = trajeto.get(1);
        int custoAP = calculadorAP.executar(sobrevivente, TipoAcaoEnum.MOVER);
        assert custoAP == 100 : "Custo base inicial de movimento deve ser 100 AP";

        // Movimenta sobrevivente
        mapa.removerEntidade(spawn.getX(), spawn.getY(), sobrevivente);
        sobrevivente.getComponent(PositionComponent.class).get().setX(proximoPasso.getX());
        sobrevivente.getComponent(PositionComponent.class).get().setY(proximoPasso.getY());
        mapa.adicionarEntidade(proximoPasso.getX(), proximoPasso.getY(), sobrevivente);

        motorTurnos.registrarAcaoExecutada(sobrevivente, custoAP);
        assert !sobrevivente.getComponent(TurnEnergyComponent.class).get().podeAgir() : "Energia deveria estar esgotada após o passo";

        System.out.println("--- [FASE 4 INTEGRATION] Alquimia Elemental & Bloqueio Dinâmico de FOV ---");
        CalcularLinhaDeVisaoUseCase fov = new CalcularLinhaDeVisaoUseCase();

        // Antes do fogo e fumaça, deve haver linha de visão para tiles alinhados
        CoordenadaVO alvoVisao = trajeto.get(trajeto.size() - 1); // destino final

        // Posicionar fogo e água em um ponto do corredor adiante
        CoordenadaVO pontoInterseccao = trajeto.get(2);
        Entity chamas = new Entity("chamas_corredor", "Chamas no Chão");
        chamas.addTag("surface_fire");
        chamas.addComponent(new SurfaceFieldComponent("surface_fire", 5));

        Entity agua = new Entity("poca_corredor", "Poça de Água");
        agua.addTag("surface_water");
        agua.addComponent(new SurfaceFieldComponent("surface_water", 10));

        mapa.adicionarEntidade(pontoInterseccao.getX(), pontoInterseccao.getY(), chamas);
        mapa.adicionarEntidade(pontoInterseccao.getX(), pontoInterseccao.getY(), agua);

        // Processa reação elemental
        ProcessarReacaoAmbientalUseCase alquimia = new ProcessarReacaoAmbientalUseCase();
        ResultadoReacaoVO resultadoAlquimia = alquimia.executar(mapa, pontoInterseccao.getX(), pontoInterseccao.getY(), loader.getReactionRepository());

        assert resultadoAlquimia.isOcorreuReacao() : "Deveria ocorrer reação de extinção";
        assert "extinguish_and_spawn".equalsIgnoreCase(resultadoAlquimia.getAcao()) : "Deveria gerar vapor e fumaça";
        assert mapa.isBloqueiaVisao(pontoInterseccao.getX(), pontoInterseccao.getY()) : "Tile com fumaça deve agora bloquear visão";

        // Testar FOV: Linha de visão do sobrevivente em proximoPasso passando por pontoInterseccao até o tile seguinte deve ser bloqueada
        CoordenadaVO aposFumaca = trajeto.get(3);
        boolean podeVerAposFumaca = fov.temLinhaDeVisao(mapa, proximoPasso, aposFumaca);
        assert !podeVerAposFumaca : "A linha de visão deve ser bloqueada pela fumaça gerada pela reação química";

        System.out.println("--- [FASE 4 INTEGRATION] Impacto de Trauma Anatômico no Custo de Movimento ---");
        // Simular que o sobrevivente fratura a perna direita
        anatomia.removerParte("right_leg");
        anatomia.adicionarParte(new BodyPartStateVO("right_leg", "Perna Direita Fraturada", 15.0, 35.0, false, true, false, 0.0));

        int custoAPComFratura = calculadorAP.executar(sobrevivente, TipoAcaoEnum.MOVER);
        assert custoAPComFratura == 200 : "Custo de movimento com perna fraturada deveria dobrar para 200 AP, mas foi: " + custoAPComFratura;

        System.out.println("Integração concluída com sucesso! Masmorra, A*, Alquimia de Fumaça, FOV e AP validados em harmonia.");
    }
}

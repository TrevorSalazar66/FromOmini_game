package com.omini.engine.unit;

import com.omini.engine.controllers.GameLoopController;
import com.omini.engine.controllers.InteractionController;
import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TileTypeEnum;
import com.omini.engine.domain.usecases.*;
import com.omini.engine.ui.views.ContextMenuView;
import com.omini.engine.ui.views.HUDView;
import com.omini.engine.ui.views.MapView;
import com.omini.engine.ui.views.ViewportCamera;

import java.util.List;

public final class GameLoopControllerTest {

    public static void main(String[] args) {
        GameLoopControllerTest test = new GameLoopControllerTest();
        test.deveProcessarTurnosEInteracoesDeMonstro();
        test.deveDeclararGameOverAoDestruirOrgaoVital();
        System.out.println("✅ GameLoopControllerTest: Todos os testes passaram!");
    }

    public void deveProcessarTurnosEInteracoesDeMonstro() {
        TileMap mapa = new TileMap(20, 20, TileTypeEnum.CHAO_PEDRA);

        Entity jogador = new Entity("p1", "Jogador");
        jogador.addTag("player");
        jogador.addComponent(new PositionComponent(5, 5));
        jogador.addComponent(new TurnEnergyComponent(100)); // Pronto para agir
        jogador.addComponent(new StatsComponent(10, 10, 10, 10, 10));

        AnatomyComponent anatomy = new AnatomyComponent("humanoid", 100.0);
        anatomy.adicionarParte(new BodyPartStateVO("head", "Cabeça", 75.0, 75.0, true, false, 0.0, false, false));
        anatomy.adicionarParte(new BodyPartStateVO("torso", "Tronco", 100.0, 100.0, true, false, 0.0, false, false));
        jogador.addComponent(anatomy);

        Entity monstro = new Entity("m1", "Zumbi");
        monstro.addTag("monstro");
        monstro.addComponent(new PositionComponent(5, 6)); // Adjacente ao jogador
        monstro.addComponent(new TurnEnergyComponent(150)); // Mais pronto que o jogador

        mapa.adicionarEntidade(5, 5, jogador);
        mapa.adicionarEntidade(5, 6, monstro);

        ViewportCamera camera = new ViewportCamera();
        MapView mapView = new MapView(camera, mapa);
        ContextMenuView contextMenu = new ContextMenuView();
        InteractionController interaction = new InteractionController(
                mapView, contextMenu, jogador,
                new NavegarPathfindingAStarUseCase(),
                new CalcularLinhaDeVisaoUseCase(),
                new CalcularCustoAcaoAPUseCase(),
                new MotorTurnosAPUseCase()
        );

        GameLoopController loop = new GameLoopController(
                mapa, jogador, List.of(monstro),
                new MotorTurnosAPUseCase(),
                new ProcessarCicloTurnoFisiologicoUseCase(),
                new ProcessarReacaoAmbientalUseCase(),
                interaction,
                new HUDView(),
                new NavegarPathfindingAStarUseCase(),
                new AplicarDanoLocalizadoUseCase(),
                new SortearMembroAtingidoUseCase(limite -> 0)
        );

        // O monstro com 150 de energia deve agir primeiro e atacar o jogador adjacente
        boolean rodadaContinua = loop.processarRodada();
        assert rodadaContinua : "Jogo deve continuar";
        // O tronco do jogador sofreu 12 de impacto + 1.8 de sangramento ativo no ciclo fisiológico do turno = 86.2 HP
        BodyPartStateVO torso = anatomy.getParte("torso").orElseThrow();
        assert Math.abs(torso.getHpAtual() - 86.2) < 0.001 : "Tronco deveria ter 86.2 HP (12 impacto + 1.8 hemorragia)";
        assert Math.abs(torso.getSangramentoPorTurno() - 1.8) < 0.001 : "Tronco deveria ter 1.8 de sangramento por turno";
        assert !loop.isGameOver();
    }

    public void deveDeclararGameOverAoDestruirOrgaoVital() {
        TileMap mapa = new TileMap(10, 10, TileTypeEnum.CHAO_PEDRA);
        Entity jogador = new Entity("p1", "Jogador");
        jogador.addTag("player");
        jogador.addComponent(new PositionComponent(2, 2));
        jogador.addComponent(new TurnEnergyComponent(50));

        AnatomyComponent anatomy = new AnatomyComponent("humanoid", 100.0);
        // Tronco à beira da morte (5 HP)
        anatomy.adicionarParte(new BodyPartStateVO("torso", "Tronco", 5.0, 100.0, true, false, 0.0, false, false));
        jogador.addComponent(anatomy);

        Entity monstro = new Entity("m1", "Zumbi Assassino");
        monstro.addTag("monstro");
        monstro.addComponent(new PositionComponent(2, 3));
        monstro.addComponent(new TurnEnergyComponent(120)); // Pronto para atacar

        GameLoopController loop = new GameLoopController(
                mapa, jogador, List.of(monstro),
                new MotorTurnosAPUseCase(),
                new ProcessarCicloTurnoFisiologicoUseCase(),
                new ProcessarReacaoAmbientalUseCase(),
                null,
                new HUDView(),
                new NavegarPathfindingAStarUseCase(),
                new AplicarDanoLocalizadoUseCase(),
                new SortearMembroAtingidoUseCase(limite -> 0)
        );

        loop.processarRodada();

        // O golpe de 12 zera o tronco (5 - 12 = 0)
        assert loop.isGameOver() : "Deveria ter declarado Game Over após destruição de membro vital";
        assert jogador.hasTag("morto");
        assert anatomy.isMorto();
    }
}

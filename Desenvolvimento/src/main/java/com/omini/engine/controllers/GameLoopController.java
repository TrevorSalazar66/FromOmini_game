package com.omini.engine.controllers;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TipoAcaoEnum;
import com.omini.engine.domain.usecases.*;
import com.omini.engine.ui.views.HUDView;
import com.omini.engine.ui.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Controlador do Loop Central de Jogo.
 * Orquestra turnos discretos, avanço de iniciativa por AP, IA de monstros,
 * ciclo fisiológico periódico e condições de Game Over.
 */
public final class GameLoopController {

    private final TileMap mapa;
    private final Entity jogador;
    private final List<Entity> monstros;
    private final MotorTurnosAPUseCase turnEngine;
    private final ProcessarCicloTurnoFisiologicoUseCase cicloFisiologicoUseCase;
    private final ProcessarReacaoAmbientalUseCase reacaoAmbientalUseCase;
    private final InteractionController interactionController;
    private final HUDView hudView;
    private final NavegarPathfindingAStarUseCase pathfindingUseCase;
    private final AplicarDanoLocalizadoUseCase danoUseCase;
    private final SortearMembroAtingidoUseCase sortearMembroUseCase;

    private boolean gameOver;
    private int turnosCompletados;

    public GameLoopController(
            TileMap mapa,
            Entity jogador,
            List<Entity> monstros,
            MotorTurnosAPUseCase turnEngine,
            ProcessarCicloTurnoFisiologicoUseCase cicloFisiologicoUseCase,
            ProcessarReacaoAmbientalUseCase reacaoAmbientalUseCase,
            InteractionController interactionController,
            HUDView hudView,
            NavegarPathfindingAStarUseCase pathfindingUseCase,
            AplicarDanoLocalizadoUseCase danoUseCase,
            SortearMembroAtingidoUseCase sortearMembroUseCase
    ) {
        if (mapa == null || jogador == null) {
            throw new IllegalArgumentException("Mapa e jogador não podem ser nulos.");
        }
        this.mapa = mapa;
        this.jogador = jogador;
        this.monstros = new ArrayList<>(monstros != null ? monstros : Collections.emptyList());
        this.turnEngine = turnEngine != null ? turnEngine : new MotorTurnosAPUseCase();
        this.cicloFisiologicoUseCase = cicloFisiologicoUseCase != null ? cicloFisiologicoUseCase : new ProcessarCicloTurnoFisiologicoUseCase();
        this.reacaoAmbientalUseCase = reacaoAmbientalUseCase != null ? reacaoAmbientalUseCase : new ProcessarReacaoAmbientalUseCase();
        this.interactionController = interactionController;
        this.hudView = hudView != null ? hudView : new HUDView();
        this.pathfindingUseCase = pathfindingUseCase != null ? pathfindingUseCase : new NavegarPathfindingAStarUseCase();
        this.danoUseCase = danoUseCase != null ? danoUseCase : new AplicarDanoLocalizadoUseCase();
        this.sortearMembroUseCase = sortearMembroUseCase != null ? sortearMembroUseCase : new SortearMembroAtingidoUseCase(limite -> 0);

        this.gameOver = false;
        this.turnosCompletados = 0;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getTurnosCompletados() {
        return turnosCompletados;
    }

    public List<Entity> getMonstros() {
        return Collections.unmodifiableList(monstros);
    }

    public void adicionarMonstro(Entity monstro) {
        if (monstro != null && !monstros.contains(monstro)) {
            monstros.add(monstro);
        }
    }

    /**
     * Processa uma iteração de turno no mundo.
     * Retorna se a rodada avançou e o jogador pode agir novamente.
     */
    public boolean processarRodada() {
        if (gameOver) return false;

        List<Entity> todasEntidades = new ArrayList<>();
        todasEntidades.add(jogador);
        todasEntidades.addAll(monstros);

        Optional<Entity> prontaOpt = turnEngine.obterProximaEntidadePronta(todasEntidades);
        if (prontaOpt.isEmpty()) {
            return false;
        }

        Entity pronta = prontaOpt.get();

        // Se for a vez do jogador: aguarda ação do usuário
        if (pronta.equals(jogador)) {
            return true;
        }

        // Se for um monstro: executa IA tática
        executarTurnoMonstro(pronta);

        // Avança ciclo fisiológico ao completar um ciclo de ticks
        cicloFisiologicoUseCase.executar(jogador, 1);
        turnosCompletados++;

        // Checagem de morte vital
        var anatomyOpt = jogador.getComponent(AnatomyComponent.class);
        if (jogador.hasTag("morto") || (anatomyOpt.isPresent() && anatomyOpt.get().isMorto())) {
            gameOver = true;
            jogador.addTag("morto");
            hudView.adicionarMensagemLog("☠ GAME OVER: O sobrevivente sucumbiu aos ferimentos vitais!");
        }

        return !gameOver;
    }

    private void executarTurnoMonstro(Entity monstro) {
        if (monstro.hasTag("morto")) return;

        PositionComponent posMonstro = monstro.getComponent(PositionComponent.class).orElse(null);
        PositionComponent posJogador = jogador.getComponent(PositionComponent.class).orElse(null);
        if (posMonstro == null || posJogador == null) return;

        int distChebyshev = Math.max(Math.abs(posMonstro.getX() - posJogador.getX()), Math.abs(posMonstro.getY() - posJogador.getY()));

        // Adjacente: ataca o jogador
        if (distChebyshev <= 1) {
            String membro = "torso";
            var defOpt = jogador.getComponent(AnatomyComponent.class);
            if (defOpt.isPresent()) {
                // Sorteia membro ou ataca tronco
                membro = defOpt.get().getParte("torso").isPresent() ? "torso" : "head";
            }
            danoUseCase.executar(jogador, membro, 12.0, "blunt");
            hudView.adicionarMensagemLog("⚔ " + monstro.getName() + " atacou seu " + membro + " causando 12 de dano!");

            monstro.getComponent(TurnEnergyComponent.class).ifPresent(e -> e.consumirEnergia(100));
            return;
        }

        // Não adjacente: move-se em direção ao jogador via A*
        CoordenadaVO origem = new CoordenadaVO(posMonstro.getX(), posMonstro.getY());
        CoordenadaVO destino = new CoordenadaVO(posJogador.getX(), posJogador.getY());
        List<CoordenadaVO> caminho = pathfindingUseCase.encontrarCaminho(mapa, origem, destino);

        if (!caminho.isEmpty() && caminho.size() > 1) {
            CoordenadaVO passo = caminho.get(1);
            if (mapa.isPassavel(passo.getX(), passo.getY()) && mapa.getEntidadesNoTile(passo.getX(), passo.getY()).isEmpty()) {
                mapa.removerEntidade(posMonstro.getX(), posMonstro.getY(), monstro);
                posMonstro.setLocation(passo.getX(), passo.getY());
                mapa.adicionarEntidade(passo.getX(), passo.getY(), monstro);
            }
        }

        monstro.getComponent(TurnEnergyComponent.class).ifPresent(e -> e.consumirEnergia(100));
    }
}

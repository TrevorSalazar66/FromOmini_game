package com.omini.engine.controllers;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.PositionComponent;
import com.omini.engine.domain.ecs.components.TurnEnergyComponent;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TileTypeEnum;
import com.omini.engine.domain.models.TipoAcaoEnum;
import com.omini.engine.domain.usecases.*;
import com.omini.engine.ui.views.ContextMenuView;
import com.omini.engine.ui.views.MapView;

import java.util.List;
import java.util.Set;

/**
 * Controlador de Interação Touch-First (estilo Shattered Pixel Dungeon).
 * Mapeia eventos de toque na tela para pathfinding automático, abertura de menus radiais
 * contextuais e consumo de AP do jogador.
 */
public final class InteractionController {

    private final MapView mapView;
    private final ContextMenuView contextMenu;
    private final Entity jogador;
    private final NavegarPathfindingAStarUseCase pathfindingUseCase;
    private final CalcularLinhaDeVisaoUseCase fovUseCase;
    private final CalcularCustoAcaoAPUseCase custoApUseCase;
    private final MotorTurnosAPUseCase turnEngine;

    public InteractionController(
            MapView mapView,
            ContextMenuView contextMenu,
            Entity jogador,
            NavegarPathfindingAStarUseCase pathfindingUseCase,
            CalcularLinhaDeVisaoUseCase fovUseCase,
            CalcularCustoAcaoAPUseCase custoApUseCase,
            MotorTurnosAPUseCase turnEngine
    ) {
        if (mapaNuloOuInvalido(mapView, contextMenu, jogador)) {
            throw new IllegalArgumentException("Dependências do InteractionController não podem ser nulas.");
        }
        this.mapView = mapView;
        this.contextMenu = contextMenu;
        this.jogador = jogador;
        this.pathfindingUseCase = pathfindingUseCase != null ? pathfindingUseCase : new NavegarPathfindingAStarUseCase();
        this.fovUseCase = fovUseCase != null ? fovUseCase : new CalcularLinhaDeVisaoUseCase();
        this.custoApUseCase = custoApUseCase != null ? custoApUseCase : new CalcularCustoAcaoAPUseCase();
        this.turnEngine = turnEngine != null ? turnEngine : new MotorTurnosAPUseCase();
    }

    private static boolean mapaNuloOuInvalido(MapView mv, ContextMenuView cm, Entity j) {
        return mv == null || cm == null || j == null;
    }

    public MapView getMapView() {
        return mapView;
    }

    public ContextMenuView getContextMenu() {
        return contextMenu;
    }

    public Entity getJogador() {
        return jogador;
    }

    /**
     * Processa um toque nas coordenadas de tela (pixels) do smartphone.
     * Retorna a ação executada em formato de mensagem legível.
     */
    public String processarToqueTela(int telaX, int telaY) {
        if (jogador.hasTag("morto")) {
            return "O jogador está morto. Não é possível agir.";
        }

        // Se o menu contextual já estiver aberto e for clicado novamente, fecha
        if (contextMenu.isVisivel()) {
            contextMenu.fechar();
            return "Menu contextual fechado.";
        }

        CoordenadaVO coordDestino = mapView.processarToqueTela(telaX, telaY);
        TileMap mapa = mapView.getMapa();
        if (mapa == null || !mapa.isDentroDosLimites(coordDestino.x(), coordDestino.y())) {
            return "Toque fora dos limites do mapa.";
        }

        PositionComponent posJogador = jogador.getComponent(PositionComponent.class)
                .orElseThrow(() -> new IllegalStateException("Jogador não possui PositionComponent."));

        int distChebyshev = Math.max(Math.abs(posJogador.getX() - coordDestino.x()), Math.abs(posJogador.getY() - coordDestino.y()));
        boolean adjacente = distChebyshev <= 1;

        // 1. Toque sobre o próprio jogador: abre menu pessoal (Inventário, Primeiros Socorros)
        if (posJogador.getX() == coordDestino.x() && posJogador.getY() == coordDestino.y()) {
            contextMenu.abrirParaEntidade(jogador, telaX, telaY, true);
            return "Menu do jogador aberto.";
        }

        // 2. Toque sobre entidade interativa no tile (itens, monstros, baús)
        List<Entity> entidadesNoTile = mapa.getEntidadesNoTile(coordDestino.x(), coordDestino.y());
        if (!entidadesNoTile.isEmpty()) {
            Entity entidadeFoco = entidadesNoTile.get(0);
            contextMenu.abrirParaEntidade(entidadeFoco, telaX, telaY, adjacente);
            return "Menu contextual aberto para: " + entidadeFoco.getName();
        }

        // 3. Toque sobre porta
        TileTypeEnum tile = mapa.getTile(coordDestino.x(), coordDestino.y());
        if (tile == TileTypeEnum.PORTA_FECHADA || tile == TileTypeEnum.PORTA_ABERTA) {
            contextMenu.abrirParaTile(tile, telaX, telaY, adjacente);
            return "Menu contextual aberto para Porta.";
        }

        // 4. Toque sobre tile transitável: navegação A*
        if (mapa.isPassavel(coordDestino.x(), coordDestino.y())) {
            CoordenadaVO origem = new CoordenadaVO(posJogador.getX(), posJogador.getY());
            List<CoordenadaVO> caminho = pathfindingUseCase.encontrarCaminho(mapa, origem, coordDestino);

            if (caminho.isEmpty() || caminho.size() <= 1) {
                return "Caminho intransponível até o destino.";
            }

            // Move 1 passo em direção ao destino (o índice 0 é a origem, índice 1 é o próximo passo)
            CoordenadaVO proximoPasso = caminho.get(1);
            return moverJogadorPara(proximoPasso);
        }

        return "Destino bloqueado.";
    }

    public String moverJogadorPara(CoordenadaVO destino) {
        TileMap mapa = mapView.getMapa();
        PositionComponent pos = jogador.getComponent(PositionComponent.class).orElseThrow();

        // Remove do tile anterior e adiciona no novo tile
        mapa.removerEntidade(pos.getX(), pos.getY(), jogador);
        pos.setLocation(destino.x(), destino.y());
        mapa.adicionarEntidade(destino.x(), destino.y(), jogador);

        // Atualiza a câmera centrando no jogador
        mapView.getCamera().centralizarEm(destino.x(), destino.y());

        // Consumo de AP
        int custoAP = custoApUseCase.executar(jogador, TipoAcaoEnum.MOVER);
        jogador.getComponent(TurnEnergyComponent.class).ifPresent(energy -> energy.consumirEnergia(custoAP));

        // Atualiza FOV e neblina de guerra
        Set<CoordenadaVO> fov = fovUseCase.calcularCampoVisao(mapa, destino.x(), destino.y(), 8);
        mapView.atualizarFov(fov);

        return "Movimentou para (" + destino.x() + ", " + destino.y() + ") [Custo: " + custoAP + " AP]";
    }
}

package com.omini.engine.ui.views;

import com.omini.engine.domain.dtos.AcaoContextualVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.InventoryGridComponent;
import com.omini.engine.domain.models.TileTypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Menu contextual radial (estilo Shattered Pixel Dungeon) que inspeciona a entidade ou tile
 * tocado e gera a lista de ações táteis possíveis baseando-se nos componentes ECS presentes.
 */
public final class ContextMenuView {

    private boolean visivel;
    private int telaX;
    private int telaY;
    private Entity entidadeFoco;
    private List<AcaoContextualVO> acoesAtivas;

    public ContextMenuView() {
        this.visivel = false;
        this.acoesAtivas = new ArrayList<>();
    }

    public boolean isVisivel() {
        return visivel;
    }

    public int getTelaX() {
        return telaX;
    }

    public int getTelaY() {
        return telaY;
    }

    public Entity getEntidadeFoco() {
        return entidadeFoco;
    }

    public List<AcaoContextualVO> getAcoesAtivas() {
        return Collections.unmodifiableList(acoesAtivas);
    }

    public void fechar() {
        this.visivel = false;
        this.entidadeFoco = null;
        this.acoesAtivas.clear();
    }

    public void abrirParaEntidade(Entity entidade, int telaX, int telaY, boolean adjacenteAoJogador) {
        if (entidade == null) {
            fechar();
            return;
        }

        this.entidadeFoco = entidade;
        this.telaX = telaX;
        this.telaY = telaY;
        this.acoesAtivas = gerarAcoes(entidade, adjacenteAoJogador);
        this.visivel = !this.acoesAtivas.isEmpty();
    }

    public void abrirParaTile(TileTypeEnum tile, int telaX, int telaY, boolean adjacenteAoJogador) {
        this.entidadeFoco = null;
        this.telaX = telaX;
        this.telaY = telaY;
        this.acoesAtivas = new ArrayList<>();

        if (tile == TileTypeEnum.PORTA_FECHADA || tile == TileTypeEnum.PORTA_ABERTA) {
            if (adjacenteAoJogador) {
                if (tile == TileTypeEnum.PORTA_FECHADA) {
                    acoesAtivas.add(AcaoContextualVO.criar("abrir_porta", "Abrir Porta", "door_open.svg", 50));
                    acoesAtivas.add(AcaoContextualVO.criar("arrombar", "Arrombar", "axe.svg", 150));
                } else {
                    acoesAtivas.add(AcaoContextualVO.criar("fechar_porta", "Fechar Porta", "door_closed.svg", 50));
                }
            } else {
                acoesAtivas.add(AcaoContextualVO.desabilitada("muito_longe", "Muito Longe", "distance.svg"));
            }
        }
        acoesAtivas.add(AcaoContextualVO.criar("examinar", "Examinar", "search.svg", 0));
        this.visivel = !this.acoesAtivas.isEmpty();
    }

    private List<AcaoContextualVO> gerarAcoes(Entity e, boolean adjacente) {
        List<AcaoContextualVO> lista = new ArrayList<>();

        // Se for o próprio jogador
        if (e.hasTag("player")) {
            lista.add(AcaoContextualVO.criar("abrir_inventario", "Inventário", "backpack.svg", 0));
            lista.add(AcaoContextualVO.criar("primeiros_socorros", "Primeiros Socorros", "bandage.svg", 0));
            lista.add(AcaoContextualVO.criar("descansar", "Esperar Turno", "clock.svg", 100));
            return lista;
        }

        // Se for um item ou container no chão
        if (e.hasTag("item")) {
            if (adjacente) {
                lista.add(AcaoContextualVO.criar("pegar", "Pegar Item", "hand_grab.svg", 50));
                if (e.hasComponent(InventoryGridComponent.class)) {
                    lista.add(AcaoContextualVO.criar("abrir_container", "Abrir Mochila", "chest.svg", 50));
                }
            } else {
                lista.add(AcaoContextualVO.desabilitada("aproximar", "Muito Longe", "distance.svg"));
            }
        }

        // Se for um monstro ou inimigo
        if (e.hasTag("monstro")) {
            if (adjacente) {
                lista.add(AcaoContextualVO.criar("atacar", "Atacar", "sword.svg", 100));
            } else {
                lista.add(AcaoContextualVO.criar("disparar", "Mirar / Atirar", "crosshair.svg", 120));
            }
        }

        // Ação universal de inspeção
        lista.add(AcaoContextualVO.criar("examinar", "Examinar", "search.svg", 0));

        return lista;
    }
}

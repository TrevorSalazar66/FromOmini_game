package com.omini.engine.controllers;

import com.omini.engine.domain.dtos.PositionedItemVO;
import com.omini.engine.domain.dtos.ResultadoInsercaoGradeVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.InventoryGridComponent;
import com.omini.engine.domain.ecs.components.ItemComponent;
import com.omini.engine.domain.usecases.InserirItemGradeUseCase;
import com.omini.engine.domain.usecases.RemoverItemGradeUseCase;

import java.util.Optional;

/**
 * Controlador de Gestos do Inventário (estilo Resident Evil 4 / Shattered Pixel Dungeon).
 * Gerencia o estado de toque, arrastar e soltar (drag-and-drop) e rotação de 90° de itens na grade.
 */
public final class InventoryController {

    private final InserirItemGradeUseCase inserirUseCase;
    private final RemoverItemGradeUseCase removerUseCase;

    // Estado do item sendo arrastado pelo toque do usuário
    private Entity itemEmArrasto;
    private InventoryGridComponent gridOrigem;
    private int origemX;
    private int origemY;
    private boolean rotacionado90;

    public InventoryController(InserirItemGradeUseCase inserirUseCase, RemoverItemGradeUseCase removerUseCase) {
        this.inserirUseCase = inserirUseCase != null ? inserirUseCase : new InserirItemGradeUseCase();
        this.removerUseCase = removerUseCase != null ? removerUseCase : new RemoverItemGradeUseCase();
    }

    public InventoryController() {
        this(new InserirItemGradeUseCase(), new RemoverItemGradeUseCase());
    }

    public boolean isArrastando() {
        return itemEmArrasto != null;
    }

    public Entity getItemEmArrasto() {
        return itemEmArrasto;
    }

    public boolean isRotacionado90() {
        return rotacionado90;
    }

    /**
     * Inicia a operação de arrasto ao tocar e segurar um item na grade.
     */
    public boolean iniciarArrasto(InventoryGridComponent grid, int x, int y) {
        if (grid == null || !grid.isDentroDosLimites(x, y) || !grid.isSlotOcupado(x, y)) {
            return false;
        }

        String itemInstanceId = grid.getItemIdNoSlot(x, y).orElse(null);
        if (itemInstanceId == null) {
            return false;
        }

        Optional<PositionedItemVO> posItemOpt = grid.getItemPosicionado(itemInstanceId);
        if (posItemOpt.isEmpty()) {
            return false;
        }

        PositionedItemVO posItem = posItemOpt.get();
        // Remove temporariamente da grade para liberar os slots enquanto é arrastado
        removerUseCase.executar(grid, itemInstanceId);

        this.itemEmArrasto = new Entity(posItem.getItemInstanceId(), posItem.getItemComponent().getNome());
        this.itemEmArrasto.addComponent(posItem.getItemComponent());
        this.gridOrigem = grid;
        this.origemX = posItem.getGridX();
        this.origemY = posItem.getGridY();
        this.rotacionado90 = posItem.isRotacionado90();

        return true;
    }

    /**
     * Gesto de rotação em 90° ao tocar com segundo dedo durante o arrasto.
     */
    public boolean alternarRotacao() {
        if (!isArrastando()) {
            return false;
        }
        ItemComponent itemComp = itemEmArrasto.getComponent(ItemComponent.class).orElse(null);
        if (itemComp == null || !itemComp.isPodeRotacionar()) {
            return false;
        }
        this.rotacionado90 = !this.rotacionado90;
        return true;
    }

    /**
     * Solta o item na posição de destino. Se houver colisão ou transbordo, restaura na posição de origem.
     */
    public ResultadoInsercaoGradeVO soltar(InventoryGridComponent gridDestino, int destinoX, int destinoY) {
        if (!isArrastando()) {
            return ResultadoInsercaoGradeVO.falha("Nenhum item está sendo arrastado.");
        }

        InventoryGridComponent destino = gridDestino != null ? gridDestino : gridOrigem;
        ResultadoInsercaoGradeVO resultado = inserirUseCase.executar(destino, itemEmArrasto, destinoX, destinoY, rotacionado90);

        if (resultado.isSucesso()) {
            limparEstadoArrasto();
            return resultado;
        }

        // Falha no destino: restaura o item na posição de origem
        inserirUseCase.executar(gridOrigem, itemEmArrasto, origemX, origemY, rotacionado90);
        limparEstadoArrasto();
        return resultado;
    }

    /**
     * Cancela a operação e devolve o item para os slots de onde saiu.
     */
    public void cancelarArrasto() {
        if (isArrastando()) {
            inserirUseCase.executar(gridOrigem, itemEmArrasto, origemX, origemY, rotacionado90);
            limparEstadoArrasto();
        }
    }

    private void limparEstadoArrasto() {
        this.itemEmArrasto = null;
        this.gridOrigem = null;
        this.origemX = 0;
        this.origemY = 0;
        this.rotacionado90 = false;
    }
}

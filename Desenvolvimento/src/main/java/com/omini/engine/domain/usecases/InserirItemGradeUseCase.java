package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.PositionedItemVO;
import com.omini.engine.domain.dtos.ResultadoInsercaoGradeVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.InventoryGridComponent;
import com.omini.engine.domain.ecs.components.ItemComponent;

import java.util.Optional;

/**
 * Caso de Uso responsável por validar e inserir geometricamente um item dentro da grade de inventário.
 * Aplica regras estritas estilo Tetris: limites de matriz, rotação de 90° e colisão de slots.
 */
public final class InserirItemGradeUseCase {

    public ResultadoInsercaoGradeVO executar(InventoryGridComponent grid, Entity itemEntity, int gridX, int gridY, boolean rotacionar90) {
        if (grid == null) {
            throw new IllegalArgumentException("A grade de inventário não pode ser nula.");
        }
        if (itemEntity == null) {
            throw new IllegalArgumentException("A entidade do item não pode ser nula.");
        }

        Optional<ItemComponent> itemCompOpt = itemEntity.getComponent(ItemComponent.class);
        if (itemCompOpt.isEmpty()) {
            return ResultadoInsercaoGradeVO.falha("A entidade informada não possui o componente ItemComponent.");
        }

        ItemComponent itemComp = itemCompOpt.get();

        if (rotacionar90 && !itemComp.isPodeRotacionar()) {
            return ResultadoInsercaoGradeVO.falha("O item '" + itemComp.getNome() + "' não permite rotação de 90 graus.");
        }

        int larguraEfetiva = rotacionar90 ? itemComp.getAlturaSlots() : itemComp.getLarguraSlots();
        int alturaEfetiva = rotacionar90 ? itemComp.getLarguraSlots() : itemComp.getAlturaSlots();

        // 1. Validação de coordenadas negativas
        if (gridX < 0 || gridY < 0) {
            return ResultadoInsercaoGradeVO.falha("Coordenadas de inserção (" + gridX + ", " + gridY + ") não podem ser negativas.");
        }

        // 2. Validação de transbordo (Out of Bounds)
        if (gridX + larguraEfetiva > grid.getLargura() || gridY + alturaEfetiva > grid.getAltura()) {
            return ResultadoInsercaoGradeVO.falha("O item com dimensão " + larguraEfetiva + "x" + alturaEfetiva +
                    " transborda os limites da grade (" + grid.getLargura() + "x" + grid.getAltura() +
                    ") na posição (" + gridX + ", " + gridY + ").");
        }

        // 3. Validação de colisão com outros itens
        for (int x = gridX; x < gridX + larguraEfetiva; x++) {
            for (int y = gridY; y < gridY + alturaEfetiva; y++) {
                if (grid.isSlotOcupado(x, y)) {
                    String idOcupante = grid.getItemIdNoSlot(x, y).orElse("desconhecido");
                    return ResultadoInsercaoGradeVO.falha("Colisão de inventário no slot (" + x + ", " + y +
                            ") já ocupado pelo item de ID '" + idOcupante + "'.");
                }
            }
        }

        // 4. Inserção bem-sucedida
        PositionedItemVO posItem = new PositionedItemVO(
                itemEntity.getId(),
                itemComp,
                gridX,
                gridY,
                rotacionar90
        );

        grid.registrarItem(posItem);
        return ResultadoInsercaoGradeVO.sucesso(posItem);
    }

    public ResultadoInsercaoGradeVO executar(InventoryGridComponent grid, Entity itemEntity, int gridX, int gridY) {
        return executar(grid, itemEntity, gridX, gridY, false);
    }
}

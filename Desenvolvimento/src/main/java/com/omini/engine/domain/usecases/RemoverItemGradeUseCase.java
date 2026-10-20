package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.PositionedItemVO;
import com.omini.engine.domain.ecs.components.InventoryGridComponent;

import java.util.Optional;

/**
 * Caso de Uso responsável por desalocar e remover um item da grade de inventário,
 * liberando todos os slots matriciais ocupados anteriormente.
 */
public final class RemoverItemGradeUseCase {

    public Optional<PositionedItemVO> executar(InventoryGridComponent grid, String itemInstanceId) {
        if (grid == null) {
            throw new IllegalArgumentException("A grade de inventário não pode ser nula.");
        }
        if (itemInstanceId == null || itemInstanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da instância do item não pode ser nulo ou vazio.");
        }

        return grid.removerItem(itemInstanceId.trim());
    }
}

package com.omini.engine.domain.repositories;

import com.omini.engine.domain.models.ItemModel;

import java.util.Collection;
import java.util.Optional;

/**
 * Contrato de repositório para acesso aos templates e dados cadastrais de itens do jogo.
 */
public interface IItemRepository {

    Optional<ItemModel> buscarPorId(String id);

    Collection<ItemModel> listarPorCategoria(String categoria);

    Collection<ItemModel> listarTodos();
}

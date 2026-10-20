package com.omini.engine.domain.repositories;

import com.omini.engine.domain.models.MaterialModel;

import java.util.Collection;
import java.util.Optional;

/**
 * Contrato de repositório para acesso aos dados de materiais físicos.
 */
public interface IMaterialRepository {

    Optional<MaterialModel> buscarPorId(String id);

    Collection<MaterialModel> listarTodos();
}

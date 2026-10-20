package com.omini.engine.domain.repositories;

import com.omini.engine.domain.models.AnatomyDefinitionModel;

import java.util.Collection;
import java.util.Optional;

/**
 * Contrato de repositório para acesso aos templates e definições de anatomia de criaturas.
 */
public interface IAnatomyRepository {

    Optional<AnatomyDefinitionModel> buscarPorId(String id);

    Collection<AnatomyDefinitionModel> listarTodos();
}

package com.omini.engine.domain.repositories;

import com.omini.engine.domain.models.DamageTypeModel;

import java.util.Collection;
import java.util.Optional;

/**
 * Contrato de repositório para acesso aos tipos de dano do motor.
 */
public interface IDamageTypeRepository {

    Optional<DamageTypeModel> buscarPorId(String id);

    Collection<DamageTypeModel> listarTodos();
}

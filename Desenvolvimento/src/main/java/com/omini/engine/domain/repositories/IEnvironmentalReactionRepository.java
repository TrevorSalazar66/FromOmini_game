package com.omini.engine.domain.repositories;

import com.omini.engine.domain.models.EnvironmentalReactionModel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de repositório para acesso às regras de alquimia e reações do ambiente.
 */
public interface IEnvironmentalReactionRepository {

    Optional<EnvironmentalReactionModel> buscarReacaoParaTags(List<String> tagsPresentes);

    default Optional<EnvironmentalReactionModel> buscarReacao(String... tags) {
        if (tags == null || tags.length == 0) return Optional.empty();
        return buscarReacaoParaTags(java.util.Arrays.asList(tags));
    }

    Collection<EnvironmentalReactionModel> listarTodas();
}

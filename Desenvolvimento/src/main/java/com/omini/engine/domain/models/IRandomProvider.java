package com.omini.engine.domain.models;

@FunctionalInterface
public interface IRandomProvider {
    /**
     * Retorna um número long no intervalo [0, limiteSuperiorExclusivo).
     */
    long proximoLong(long limiteSuperiorExclusivo);
}

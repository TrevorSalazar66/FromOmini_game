package com.omini.engine.domain.dtos;

import java.util.Objects;

/**
 * Value Object imutável que representa uma coordenada discreta (x, y) no espaço bidimensional.
 */
public final class CoordenadaVO {

    private final int x;
    private final int y;

    public CoordenadaVO(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int distanciaManhattan(CoordenadaVO outro) {
        if (outro == null) return Integer.MAX_VALUE;
        return Math.abs(this.x - outro.x) + Math.abs(this.y - outro.y);
    }

    public double distanciaEuclidiana(CoordenadaVO outro) {
        if (outro == null) return Double.MAX_VALUE;
        int dx = this.x - outro.x;
        int dy = this.y - outro.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int distanciaChebyshev(CoordenadaVO outro) {
        if (outro == null) return Integer.MAX_VALUE;
        return Math.max(Math.abs(this.x - outro.x), Math.abs(this.y - outro.y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordenadaVO that = (CoordenadaVO) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

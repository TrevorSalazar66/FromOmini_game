package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que armazena a localização espacial 2D de uma entidade no mundo de jogo.
 */
public final class PositionComponent implements Component {

    private int x;
    private int y;

    public PositionComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "PositionComponent{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

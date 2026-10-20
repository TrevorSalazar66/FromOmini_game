package com.omini.engine.domain.dtos;

import com.omini.engine.domain.ecs.components.ItemComponent;
import java.util.Objects;

/**
 * Value Object que representa um item posicionado espacialmente dentro de uma grade de inventário.
 */
public final class PositionedItemVO {

    private final String itemInstanceId;
    private final ItemComponent itemComponent;
    private final int gridX;
    private final int gridY;
    private final boolean rotacionado90;

    public PositionedItemVO(String itemInstanceId, ItemComponent itemComponent, int gridX, int gridY, boolean rotacionado90) {
        if (itemInstanceId == null || itemInstanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da instância do item não pode ser nulo ou vazio.");
        }
        if (itemComponent == null) {
            throw new IllegalArgumentException("O componente do item não pode ser nulo.");
        }
        if (gridX < 0 || gridY < 0) {
            throw new IllegalArgumentException("As coordenadas da grade não podem ser negativas.");
        }
        if (rotacionado90 && !itemComponent.isPodeRotacionar()) {
            throw new IllegalArgumentException("Este item não permite rotação de 90 graus.");
        }
        this.itemInstanceId = itemInstanceId.trim();
        this.itemComponent = itemComponent;
        this.gridX = gridX;
        this.gridY = gridY;
        this.rotacionado90 = rotacionado90;
    }

    public String getItemInstanceId() {
        return itemInstanceId;
    }

    public ItemComponent getItemComponent() {
        return itemComponent;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public boolean isRotacionado90() {
        return rotacionado90;
    }

    public int getLarguraEfetiva() {
        return rotacionado90 ? itemComponent.getAlturaSlots() : itemComponent.getLarguraSlots();
    }

    public int getAlturaEfetiva() {
        return rotacionado90 ? itemComponent.getLarguraSlots() : itemComponent.getAlturaSlots();
    }

    public boolean ocupaSlot(int x, int y) {
        return x >= gridX && x < gridX + getLarguraEfetiva() &&
               y >= gridY && y < gridY + getAlturaEfetiva();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionedItemVO that = (PositionedItemVO) o;
        return Objects.equals(itemInstanceId, that.itemInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemInstanceId);
    }

    @Override
    public String toString() {
        return "PositionedItemVO{" +
                "id='" + itemInstanceId + '\'' +
                ", item=" + itemComponent.getNome() +
                ", pos=(" + gridX + "," + gridY + ")" +
                ", dimEfetiva=" + getLarguraEfetiva() + "x" + getAlturaEfetiva() +
                ", rotacionado=" + rotacionado90 +
                '}';
    }
}

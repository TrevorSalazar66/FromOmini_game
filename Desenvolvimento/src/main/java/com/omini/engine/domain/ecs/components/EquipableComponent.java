package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que define se e onde um item pode ser equipado no corpo.
 */
public final class EquipableComponent implements Component {

    private final SlotEquipamentoEnum slotCompativel;

    public EquipableComponent(SlotEquipamentoEnum slotCompativel) {
        if (slotCompativel == null) {
            throw new IllegalArgumentException("O slot de equipamento não pode ser nulo.");
        }
        this.slotCompativel = slotCompativel;
    }

    public SlotEquipamentoEnum getSlotCompativel() {
        return slotCompativel;
    }

    @Override
    public String toString() {
        return "EquipableComponent{" +
                "slotCompativel=" + slotCompativel +
                '}';
    }
}

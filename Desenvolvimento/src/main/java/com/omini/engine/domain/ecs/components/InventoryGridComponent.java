package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.dtos.PositionedItemVO;
import com.omini.engine.domain.ecs.Component;

import java.util.*;

/**
 * Componente que adiciona a uma entidade uma grade bidimensional de inventário (estilo Tetris/Resident Evil).
 * Controla os slots da matriz e as instâncias de itens posicionados nela.
 */
public final class InventoryGridComponent implements Component {

    private final int largura;
    private final int altura;
    private final String[][] slots; // slots[x][y] armazena o itemInstanceId ou null
    private final Map<String, PositionedItemVO> itensAlocados;

    public InventoryGridComponent(int largura, int altura) {
        if (largura <= 0 || altura <= 0) {
            throw new IllegalArgumentException("A largura e altura da grade devem ser estritamente positivas.");
        }
        this.largura = largura;
        this.altura = altura;
        this.slots = new String[largura][altura];
        this.itensAlocados = new LinkedHashMap<>();
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public int getTotalSlots() {
        return largura * altura;
    }

    public boolean isDentroDosLimites(int x, int y) {
        return x >= 0 && x < largura && y >= 0 && y < altura;
    }

    public boolean isSlotOcupado(int x, int y) {
        if (!isDentroDosLimites(x, y)) {
            throw new IndexOutOfBoundsException("Coordenada (" + x + ", " + y + ") fora dos limites da grade " + largura + "x" + altura);
        }
        return slots[x][y] != null;
    }

    public Optional<String> getItemIdNoSlot(int x, int y) {
        if (!isDentroDosLimites(x, y)) {
            return Optional.empty();
        }
        return Optional.ofNullable(slots[x][y]);
    }

    public Optional<PositionedItemVO> getItemPosicionado(String itemInstanceId) {
        if (itemInstanceId == null) return Optional.empty();
        return Optional.ofNullable(itensAlocados.get(itemInstanceId));
    }

    public Collection<PositionedItemVO> getItensAlocados() {
        return Collections.unmodifiableCollection(itensAlocados.values());
    }

    public int getQuantidadeItens() {
        return itensAlocados.size();
    }

    public double getPesoTotalItens() {
        double total = 0.0;
        for (PositionedItemVO posItem : itensAlocados.values()) {
            total += posItem.getItemComponent().getPesoKg();
        }
        return total;
    }

    public int getSlotsOcupadosContagem() {
        int count = 0;
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                if (slots[x][y] != null) count++;
            }
        }
        return count;
    }

    public int getSlotsLivresContagem() {
        return getTotalSlots() - getSlotsOcupadosContagem();
    }

    /**
     * Aloca os slots matriciais ocupados pelo item e registra a instância.
     * Este método assume que a validação geométrica prévia foi realizada pelo caso de uso.
     */
    public void registrarItem(PositionedItemVO posItem) {
        if (posItem == null) {
            throw new IllegalArgumentException("PositionedItemVO não pode ser nulo.");
        }
        String instanceId = posItem.getItemInstanceId();
        int l = posItem.getLarguraEfetiva();
        int a = posItem.getAlturaEfetiva();
        int gx = posItem.getGridX();
        int gy = posItem.getGridY();

        for (int x = gx; x < gx + l; x++) {
            for (int y = gy; y < gy + a; y++) {
                slots[x][y] = instanceId;
            }
        }
        itensAlocados.put(instanceId, posItem);
    }

    public void inserirItem(com.omini.engine.domain.ecs.Entity item, int gridX, int gridY, int larguraSlots, int alturaSlots) {
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        ItemComponent itemComp = item.getComponent(ItemComponent.class)
                .orElse(new ItemComponent(item.getId(), item.getName(), larguraSlots, alturaSlots, 1.0, true));
        registrarItem(new PositionedItemVO(item.getId(), itemComp, gridX, gridY, false));
    }

    /**
     * Remove o item especificado liberando todos os seus slots matriciais.
     */
    public Optional<PositionedItemVO> removerItem(String itemInstanceId) {
        if (itemInstanceId == null || !itensAlocados.containsKey(itemInstanceId)) {
            return Optional.empty();
        }
        PositionedItemVO posItem = itensAlocados.remove(itemInstanceId);
        int l = posItem.getLarguraEfetiva();
        int a = posItem.getAlturaEfetiva();
        int gx = posItem.getGridX();
        int gy = posItem.getGridY();

        for (int x = gx; x < gx + l; x++) {
            for (int y = gy; y < gy + a; y++) {
                if (Objects.equals(slots[x][y], itemInstanceId)) {
                    slots[x][y] = null;
                }
            }
        }
        return Optional.of(posItem);
    }

    @Override
    public String toString() {
        return "InventoryGridComponent{" +
                "dimensao=" + largura + "x" + altura +
                ", itens=" + itensAlocados.size() +
                ", pesoTotal=" + getPesoTotalItens() + "kg" +
                ", slotsLivres=" + getSlotsLivresContagem() + "/" + getTotalSlots() +
                '}';
    }
}

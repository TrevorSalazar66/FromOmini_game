package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.ecs.Component;

/**
 * Componente que identifica uma entidade como um Item manuseável,
 * definindo suas dimensões matriciais (estilo Tetris), peso e propriedades de rotação.
 */
public final class ItemComponent implements Component {

    private final String itemId;
    private final String nome;
    private final int larguraSlots;
    private final int alturaSlots;
    private final double pesoKg;
    private final boolean podeRotacionar;

    public ItemComponent(String itemId, String nome, int larguraSlots, int alturaSlots, double pesoKg, boolean podeRotacionar) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do item não pode ser nulo ou vazio.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do item não pode ser nulo ou vazio.");
        }
        if (larguraSlots <= 0 || alturaSlots <= 0) {
            throw new IllegalArgumentException("Largura e altura em slots devem ser estritamente positivas.");
        }
        if (pesoKg < 0.0) {
            throw new IllegalArgumentException("O peso do item não pode ser negativo.");
        }
        this.itemId = itemId.trim();
        this.nome = nome.trim();
        this.larguraSlots = larguraSlots;
        this.alturaSlots = alturaSlots;
        this.pesoKg = pesoKg;
        this.podeRotacionar = podeRotacionar;
    }

    public ItemComponent(String itemId, String nome, int larguraSlots, int alturaSlots, double pesoKg) {
        this(itemId, nome, larguraSlots, alturaSlots, pesoKg, true);
    }

    public String getItemId() {
        return itemId;
    }

    public String getNome() {
        return nome;
    }

    public int getLarguraSlots() {
        return larguraSlots;
    }

    public int getAlturaSlots() {
        return alturaSlots;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public boolean isPodeRotacionar() {
        return podeRotacionar;
    }

    public int getAreaSlots() {
        return larguraSlots * alturaSlots;
    }

    @Override
    public String toString() {
        return "ItemComponent{" +
                "itemId='" + itemId + '\'' +
                ", nome='" + nome + '\'' +
                ", slots=" + larguraSlots + "x" + alturaSlots +
                ", pesoKg=" + pesoKg +
                ", podeRotacionar=" + podeRotacionar +
                '}';
    }
}

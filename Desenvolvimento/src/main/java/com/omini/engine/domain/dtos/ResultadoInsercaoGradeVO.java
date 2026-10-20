package com.omini.engine.domain.dtos;

import java.util.Optional;

/**
 * Value Object com o resultado da tentativa de inserção de um item na grade de inventário.
 */
public final class ResultadoInsercaoGradeVO {

    private final boolean sucesso;
    private final String mensagem;
    private final PositionedItemVO itemInserido;

    private ResultadoInsercaoGradeVO(boolean sucesso, String mensagem, PositionedItemVO itemInserido) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.itemInserido = itemInserido;
    }

    public static ResultadoInsercaoGradeVO sucesso(PositionedItemVO item) {
        return new ResultadoInsercaoGradeVO(true, "Item inserido com sucesso na grade.", item);
    }

    public static ResultadoInsercaoGradeVO falha(String motivo) {
        return new ResultadoInsercaoGradeVO(false, motivo, null);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Optional<PositionedItemVO> getItemInserido() {
        return Optional.ofNullable(itemInserido);
    }

    @Override
    public String toString() {
        return "ResultadoInsercaoGradeVO{" +
                "sucesso=" + sucesso +
                ", mensagem='" + mensagem + '\'' +
                ", item=" + (itemInserido != null ? itemInserido.getItemInstanceId() : "nenhum") +
                '}';
    }
}

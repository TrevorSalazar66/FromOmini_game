package com.omini.engine.ui.views;

import com.omini.engine.domain.dtos.EstadoSobrecargaVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.usecases.CalcularSobrecargaUseCase;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * HUD Minimalista Touch-First.
 * Exibe o relógio de ação (AP), medidor tático de sobrecarga de peso,
 * barras dinâmicas de sobrevivência e buffer de log de eventos.
 */
public final class HUDView {

    public record EstadoHudDTO(
            int energiaAP,
            boolean prontoParaAgir,
            double pesoTransportadoKg,
            double capacidadeConfortavelKg,
            String sobrecargaNivel,
            String sobrecargaCorHex,
            double estamina,
            double fome,
            double sede,
            double dorEfetiva,
            double focoAtual,
            double tetoFoco,
            List<String> logEventosRecentes
    ) {}

    private final CalcularSobrecargaUseCase sobrecargaUseCase;
    private final Deque<String> bufferLog;
    private static final int MAX_MENSAGENS_LOG = 10;

    public HUDView(CalcularSobrecargaUseCase sobrecargaUseCase) {
        this.sobrecargaUseCase = sobrecargaUseCase != null ? sobrecargaUseCase : new CalcularSobrecargaUseCase();
        this.bufferLog = new ArrayDeque<>();
    }

    public HUDView() {
        this(new CalcularSobrecargaUseCase());
    }

    public void adicionarMensagemLog(String mensagem) {
        if (mensagem != null && !mensagem.isBlank()) {
            if (bufferLog.size() >= MAX_MENSAGENS_LOG) {
                bufferLog.pollFirst();
            }
            bufferLog.addLast(mensagem.trim());
        }
    }

    public void limparLog() {
        bufferLog.clear();
    }

    public EstadoHudDTO extrairEstado(Entity jogador) {
        if (jogador == null) {
            return new EstadoHudDTO(0, false, 0, 0, "N/A", "#9E9E9E", 0, 0, 0, 0, 0, 0, List.of());
        }

        // 1. AP / Energia
        int ap = 0;
        boolean pronto = false;
        var energyOpt = jogador.getComponent(TurnEnergyComponent.class);
        if (energyOpt.isPresent()) {
            ap = energyOpt.get().getEnergia();
            pronto = energyOpt.get().podeAgir();
        }

        // 2. Sobrecarga e Peso
        double pesoTotal = 0.0;
        double capConfortavel = 50.0;
        String nivelStr = "Confortável";
        String corCarga = "#4CAF50"; // Verde

        var gridOpt = jogador.getComponent(InventoryGridComponent.class);
        var statsOpt = jogador.getComponent(StatsComponent.class);
        if (gridOpt.isPresent() && statsOpt.isPresent()) {
            pesoTotal = gridOpt.get().getPesoTotalItens();
            int forca = statsOpt.get().getForca();
            EstadoSobrecargaVO estadoSobrecarga = sobrecargaUseCase.executar(pesoTotal, forca);
            capConfortavel = estadoSobrecarga.getCapacidadeConfortavel();
            nivelStr = estadoSobrecarga.getNivelSobrecarga().getDescricao();

            corCarga = switch (estadoSobrecarga.getNivelSobrecarga()) {
                case LEVE, CONFORTAVEL -> "#4CAF50"; // Verde
                case PESADA -> "#FFEB3B";            // Amarelo
                case CRITICA -> "#FF9800";           // Laranja
                case INCAPACITADO -> "#F44336";      // Vermelho
            };
        }

        // 3. Sobrevivência
        double estamina = 100.0;
        double fome = 0.0;
        double sede = 0.0;
        var survOpt = jogador.getComponent(SurvivalComponent.class);
        if (survOpt.isPresent()) {
            estamina = survOpt.get().getEstamina();
            fome = survOpt.get().getFome();
            sede = survOpt.get().getSede();
        }

        // 4. Dor e Foco
        double dor = 0.0;
        double foco = 100.0;
        double tetoFoco = 100.0;
        var painOpt = jogador.getComponent(PainFocusComponent.class);
        if (painOpt.isPresent()) {
            dor = painOpt.get().getDorEfetiva();
            foco = painOpt.get().getFocoAtual();
            tetoFoco = painOpt.get().getTetoFoco();
        }

        return new EstadoHudDTO(
                ap,
                pronto,
                pesoTotal,
                capConfortavel,
                nivelStr,
                corCarga,
                estamina,
                fome,
                sede,
                dor,
                foco,
                tetoFoco,
                new ArrayList<>(bufferLog)
        );
    }
}

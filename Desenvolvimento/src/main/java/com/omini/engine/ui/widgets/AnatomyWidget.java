package com.omini.engine.ui.widgets;

import com.omini.engine.domain.dtos.AcaoContextualVO;
import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.domain.models.TipoTratamentoEnum;
import com.omini.engine.domain.usecases.TratarFerimentoUseCase;

import java.util.*;

/**
 * Widget responsável pela renderização da silhueta anatômica do sobrevivente
 * em 14 membros com gradiente dinâmico de cores por lesão e painel de primeiros socorros.
 */
public final class AnatomyWidget {

    public record MembroVisualDTO(
            String id,
            String nome,
            double hpAtual,
            double hpMaximo,
            double porcentagemHp,
            String corHex,
            boolean vital,
            boolean fraturado,
            boolean sangrando,
            boolean enfaixado,
            boolean imobilizadoComTala
    ) {}

    private final TratarFerimentoUseCase tratarFerimentoUseCase;

    public AnatomyWidget(TratarFerimentoUseCase tratarFerimentoUseCase) {
        this.tratarFerimentoUseCase = tratarFerimentoUseCase != null ? tratarFerimentoUseCase : new TratarFerimentoUseCase();
    }

    public AnatomyWidget() {
        this(new TratarFerimentoUseCase());
    }

    /**
     * Extrai a representação gráfica de cada membro da silhueta corporal.
     */
    public List<MembroVisualDTO> extrairSilhueta(Entity paciente) {
        if (paciente == null) return Collections.emptyList();

        AnatomyComponent anatomy = paciente.getComponent(AnatomyComponent.class).orElse(null);
        if (anatomy == null) return Collections.emptyList();

        List<MembroVisualDTO> resultado = new ArrayList<>();
        for (BodyPartStateVO parte : anatomy.getTodasPartes()) {
            double hpAtual = parte.getHpAtual();
            double hpMax = parte.getHpMaximo();
            double pct = hpMax > 0 ? (hpAtual / hpMax) : 0.0;

            String corHex;
            if (parte.isFraturado() || hpAtual <= 0.0) {
                corHex = "#212121"; // Cinza escuro / Preto (Fraturado / Inutilizado)
            } else if (pct < 0.40) {
                corHex = "#F44336"; // Vermelho (Crítico)
            } else if (pct < 0.70) {
                corHex = "#FF9800"; // Laranja (Moderado)
            } else if (pct < 0.99) {
                corHex = "#FFEB3B"; // Amarelo (Leve)
            } else {
                corHex = "#4CAF50"; // Verde (Íntegro)
            }

            resultado.add(new MembroVisualDTO(
                    parte.getPartId(),
                    parte.getName(),
                    hpAtual,
                    hpMax,
                    pct,
                    corHex,
                    parte.isVital(),
                    parte.isFraturado(),
                    parte.getSangramentoPorTurno() > 0.0,
                    parte.isEnfaixado(),
                    parte.isImobilizadoComTala()
            ));
        }

        return resultado;
    }

    /**
     * Retorna as opções médicas táteis disponíveis ao tocar em determinado membro.
     */
    public List<AcaoContextualVO> obterAcoesTratamento(Entity paciente, String membroId) {
        if (paciente == null || membroId == null) return Collections.emptyList();

        AnatomyComponent anatomy = paciente.getComponent(AnatomyComponent.class).orElse(null);
        if (anatomy == null) return Collections.emptyList();

        Optional<BodyPartStateVO> parteOpt = anatomy.getParte(membroId);
        if (parteOpt.isEmpty()) return Collections.emptyList();

        BodyPartStateVO parte = parteOpt.get();
        List<AcaoContextualVO> acoes = new ArrayList<>();

        if (parte.getSangramentoPorTurno() > 0.0 || (parte.getTaxaSangramentoOriginal() > 0.0 && !parte.isEnfaixado())) {
            acoes.add(AcaoContextualVO.criar("curativo", "Aplicar Curativo", "bandage.svg", 50));
        }

        if (parte.isFraturado() && !parte.isImobilizadoComTala()) {
            acoes.add(AcaoContextualVO.criar("tala", "Imobilizar com Tala", "splint.svg", 80));
        }

        acoes.add(AcaoContextualVO.criar("analgesico", "Tomar Analgésico", "pill.svg", 30));
        return acoes;
    }

    /**
     * Executa a intervenção médica tátil no paciente.
     */
    public boolean aplicarTratamento(Entity paciente, String membroId, String tipoAcaoId) {
        return switch (tipoAcaoId.toLowerCase()) {
            case "curativo" -> tratarFerimentoUseCase.executar(paciente, membroId, TipoTratamentoEnum.CURATIVO_SANGRAMENTO);
            case "tala" -> tratarFerimentoUseCase.executar(paciente, membroId, TipoTratamentoEnum.TALA_FRATURA);
            case "analgesico" -> tratarFerimentoUseCase.executar(paciente, membroId, TipoTratamentoEnum.ANALGESICO, 30.0);
            default -> false;
        };
    }
}

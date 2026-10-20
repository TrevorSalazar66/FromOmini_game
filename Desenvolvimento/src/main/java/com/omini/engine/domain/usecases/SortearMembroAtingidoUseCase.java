package com.omini.engine.domain.usecases;

import com.omini.engine.domain.dtos.AtaqueContextoDTO;
import com.omini.engine.domain.models.BodyPartDefinitionModel;
import com.omini.engine.domain.models.IRandomProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de Uso responsável por sortear estocasticamente qual parte corporal de um alvo é atingida,
 * levando em conta a área de superfície (hit_weight), coberturas físicas e mira cirúrgica assistida por Foco.
 */
public final class SortearMembroAtingidoUseCase {

    private final IRandomProvider randomProvider;

    public SortearMembroAtingidoUseCase(IRandomProvider randomProvider) {
        if (randomProvider == null) {
            throw new IllegalArgumentException("O provedor de aleatoriedade não pode ser nulo.");
        }
        this.randomProvider = randomProvider;
    }

    public String executar(AtaqueContextoDTO contexto) {
        if (contexto == null) {
            throw new IllegalArgumentException("O contexto de ataque não pode ser nulo.");
        }

        List<BodyPartDefinitionModel> partes = new ArrayList<>(contexto.getAnatomia().getAllBodyParts());
        int[] pesosCalculados = new int[partes.size()];
        int poolTotal = 0;

        for (int i = 0; i < partes.size(); i++) {
            BodyPartDefinitionModel part = partes.get(i);
            int peso = part.getHitWeight();

            // 1. Modificador de cobertura (anula pernas e pés se houver cobertura de trincheira/mureta)
            if (contexto.isCoberturaMembrosInferiores() && isMembroInferior(part)) {
                peso = 0;
            }

            // 2. Modificador de Mira Cirúrgica com Foco
            if (contexto.getMembroMiradoId() != null &&
                contexto.getMembroMiradoId().equalsIgnoreCase(part.getId()) &&
                contexto.getFocoGasto() > 0.0) {
                double multiplicador = 1.0 + (contexto.getFocoGasto() / 10.0);
                peso = (int) Math.round(peso * multiplicador);
            }

            pesosCalculados[i] = Math.max(0, peso);
            poolTotal += pesosCalculados[i];
        }

        if (poolTotal <= 0) {
            // Fallback de segurança se tudo estiver coberto
            return "torso";
        }

        int sorteado = (int) randomProvider.proximoLong(poolTotal);
        int acumulador = 0;

        for (int i = 0; i < partes.size(); i++) {
            acumulador += pesosCalculados[i];
            if (sorteado < acumulador) {
                return partes.get(i).getId();
            }
        }

        return partes.get(partes.size() - 1).getId();
    }

    private boolean isMembroInferior(BodyPartDefinitionModel part) {
        String slot = part.getArmorSlot();
        String id = part.getId().toLowerCase();
        return "legs".equals(slot) || "feet".equals(slot) ||
               id.contains("thigh") || id.contains("shin") || id.contains("foot");
    }
}

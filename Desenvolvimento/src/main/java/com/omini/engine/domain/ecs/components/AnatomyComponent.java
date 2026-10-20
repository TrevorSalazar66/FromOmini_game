package com.omini.engine.domain.ecs.components;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.ecs.Component;
import com.omini.engine.domain.models.AnatomyDefinitionModel;
import com.omini.engine.domain.models.BodyPartDefinitionModel;

import java.util.*;

/**
 * Componente que armazena a integridade física anatômica de uma entidade (criatura ou jogador).
 */
public final class AnatomyComponent implements Component {

    private final String anatomyId;
    private final double baseMaxHp;
    private final Map<String, BodyPartStateVO> partes;

    public AnatomyComponent(String anatomyId, double baseMaxHp, Map<String, BodyPartStateVO> partes) {
        if (anatomyId == null || anatomyId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da anatomia não pode ser nulo ou vazio.");
        }
        if (baseMaxHp <= 0.0) {
            throw new IllegalArgumentException("O HP base deve ser positivo. Recebido: " + baseMaxHp);
        }
        if (partes == null || partes.isEmpty()) {
            throw new IllegalArgumentException("O componente de anatomia deve possuir ao menos uma parte.");
        }
        this.anatomyId = anatomyId.trim();
        this.baseMaxHp = baseMaxHp;
        this.partes = new LinkedHashMap<>(partes);
    }

    public static AnatomyComponent aPartirDeDefinicao(AnatomyDefinitionModel def, double baseMaxHp) {
        if (def == null) {
            throw new IllegalArgumentException("A definição anatômica não pode ser nula.");
        }
        Map<String, BodyPartStateVO> mapa = new LinkedHashMap<>();
        for (BodyPartDefinitionModel partDef : def.getAllBodyParts()) {
            double hpMaxParte = baseMaxHp * partDef.getBaseHpRatio();
            mapa.put(partDef.getId().toLowerCase(), BodyPartStateVO.criarSaudavel(
                    partDef.getId(),
                    partDef.getName(),
                    hpMaxParte,
                    partDef.isVital()
            ));
        }
        return new AnatomyComponent(def.getId(), baseMaxHp, mapa);
    }

    public String getAnatomyId() {
        return anatomyId;
    }

    public double getBaseMaxHp() {
        return baseMaxHp;
    }

    public Optional<BodyPartStateVO> getParte(String partId) {
        if (partId == null) return Optional.empty();
        return Optional.ofNullable(partes.get(partId.trim().toLowerCase()));
    }

    public void atualizarParte(BodyPartStateVO novaParte) {
        if (novaParte == null) {
            throw new IllegalArgumentException("A parte anatômica não pode ser nula.");
        }
        partes.put(novaParte.getPartId().toLowerCase(), novaParte);
    }

    public Collection<BodyPartStateVO> getTodasPartes() {
        return Collections.unmodifiableCollection(partes.values());
    }

    public boolean isMorto() {
        for (BodyPartStateVO parte : partes.values()) {
            if (parte.isVital() && parte.isDestruido()) {
                return true;
            }
        }
        return false;
    }

    public double getSangramentoTotal() {
        double total = 0.0;
        for (BodyPartStateVO parte : partes.values()) {
            total += parte.getSangramentoPorTurno();
        }
        return total;
    }

    public int getContagemFraturas() {
        int count = 0;
        for (BodyPartStateVO parte : partes.values()) {
            if (parte.isFraturado()) count++;
        }
        return count;
    }

    public boolean isMembroFraturado(String partId) {
        return getParte(partId).map(BodyPartStateVO::isFraturado).orElse(false);
    }

    public double getHpTotalAtual() {
        double total = 0.0;
        for (BodyPartStateVO parte : partes.values()) {
            total += parte.getHpAtual();
        }
        return total;
    }

    public double getHpTotalMaximo() {
        double total = 0.0;
        for (BodyPartStateVO parte : partes.values()) {
            total += parte.getHpMaximo();
        }
        return total;
    }

    @Override
    public String toString() {
        return "AnatomyComponent{" +
                "anatomyId='" + anatomyId + '\'' +
                ", hpTotal=" + String.format("%.1f/%.1f", getHpTotalAtual(), getHpTotalMaximo()) +
                ", sangramento=" + getSangramentoTotal() +
                ", fraturas=" + getContagemFraturas() +
                ", morto=" + isMorto() +
                '}';
    }
}

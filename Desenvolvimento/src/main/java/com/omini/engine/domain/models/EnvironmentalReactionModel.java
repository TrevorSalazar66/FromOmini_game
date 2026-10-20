package com.omini.engine.domain.models;

import java.util.Collections;
import java.util.List;

/**
 * Modelo de domínio imutável que representa uma regra de reação química/elemental do ambiente.
 */
public final class EnvironmentalReactionModel {

    private final List<String> triggerTags;
    private final String action;
    private final String spawnEntity;
    private final int spawnRadius;
    private final int durationTurns;
    private final int explosionPower;
    private final int soundVolume;
    private final double damagePerTurn;
    private final String description;

    public EnvironmentalReactionModel(List<String> triggerTags,
                                      String action,
                                      String spawnEntity,
                                      int spawnRadius,
                                      int durationTurns,
                                      int explosionPower,
                                      int soundVolume,
                                      double damagePerTurn,
                                      String description) {
        this.triggerTags = triggerTags != null ? Collections.unmodifiableList(triggerTags) : Collections.emptyList();
        this.action = action != null ? action.trim().toLowerCase() : "none";
        this.spawnEntity = spawnEntity;
        this.spawnRadius = spawnRadius;
        this.durationTurns = durationTurns;
        this.explosionPower = explosionPower;
        this.soundVolume = soundVolume;
        this.damagePerTurn = damagePerTurn;
        this.description = description != null ? description.trim() : "";
    }

    public List<String> getTriggerTags() {
        return triggerTags;
    }

    public String getAction() {
        return action;
    }

    public String getSpawnEntity() {
        return spawnEntity;
    }

    public int getSpawnRadius() {
        return spawnRadius;
    }

    public int getDurationTurns() {
        return durationTurns;
    }

    public int getExplosionPower() {
        return explosionPower;
    }

    public int getSoundVolume() {
        return soundVolume;
    }

    public double getDamagePerTurn() {
        return damagePerTurn;
    }

    public String getDescription() {
        return description;
    }

    public boolean todasTagsPresentes(List<String> tagsDisponiveis) {
        if (tagsDisponiveis == null || triggerTags.isEmpty()) return false;
        return tagsDisponiveis.containsAll(triggerTags);
    }
}

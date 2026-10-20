package com.omini.engine.domain.factories;

import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.models.ItemModel;
import com.omini.engine.domain.models.MaterialModel;
import com.omini.engine.domain.repositories.IMaterialRepository;
import com.omini.engine.domain.usecases.CalcularMassaProceduralUseCase;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fábrica de domínio para criação de entidades de itens a partir de modelos de template (ItemModel).
 * Calcula a massa procedural correlacionando slots e densidade do material.
 */
public final class ItemEntityFactory {

    private final IMaterialRepository materialRepository;
    private final CalcularMassaProceduralUseCase calcularMassaUseCase;

    public ItemEntityFactory(IMaterialRepository materialRepository, CalcularMassaProceduralUseCase calcularMassaUseCase) {
        this.materialRepository = materialRepository;
        this.calcularMassaUseCase = calcularMassaUseCase != null ? calcularMassaUseCase : new CalcularMassaProceduralUseCase();
    }

    public ItemEntityFactory(IMaterialRepository materialRepository) {
        this(materialRepository, new CalcularMassaProceduralUseCase());
    }

    public Entity criarInstancia(ItemModel template) {
        if (template == null) {
            throw new IllegalArgumentException("O modelo do item não pode ser nulo.");
        }

        String instanceId = UUID.randomUUID().toString();
        Entity entity = new Entity(instanceId, template.getName());
        entity.addTag("item");
        entity.addTag("category:" + template.getCategory());

        // 1. Obter material e calcular peso (override manual ou massa procedural)
        double densidade = 1.0;
        double dureza = 10.0;
        if (materialRepository != null) {
            Optional<MaterialModel> matOpt = materialRepository.buscarPorId(template.getMaterialId());
            if (matOpt.isPresent()) {
                densidade = matOpt.get().getDensidade();
                dureza = matOpt.get().getDurezaBase();
                entity.addComponent(new MaterialComponent(matOpt.get().getId(), densidade, dureza));
            }
        }

        final double densidadeFinal = densidade;
        double pesoFinal = template.getWeightKgOverride().orElseGet(() ->
                calcularMassaUseCase.executar(template.getWidthSlots(), template.getHeightSlots(), densidadeFinal)
        );

        // 2. Componente de Item
        ItemComponent itemComp = new ItemComponent(
                template.getId(),
                template.getName(),
                template.getWidthSlots(),
                template.getHeightSlots(),
                pesoFinal,
                template.isCanRotate()
        );
        entity.addComponent(itemComp);

        // 3. Componente Equipável (se aplicável)
        template.getEquipSlot().ifPresent(slotStr -> {
            try {
                SlotEquipamentoEnum slot = SlotEquipamentoEnum.valueOf(slotStr.toUpperCase());
                entity.addComponent(new EquipableComponent(slot));
            } catch (IllegalArgumentException ignored) {
                // Se não mapear diretamente para o enum, omite EquipableComponent
            }
        });

        // 4. Se for container, anexa o InventoryGridComponent interno
        if ("container".equalsIgnoreCase(template.getCategory())) {
            Map<String, Object> props = template.getProperties();
            int gw = 3;
            int gh = 3;
            if (props.containsKey("grid_width") && props.get("grid_width") instanceof Number n) {
                gw = n.intValue();
            }
            if (props.containsKey("grid_height") && props.get("grid_height") instanceof Number n) {
                gh = n.intValue();
            }
            entity.addComponent(new InventoryGridComponent(gw, gh));
        }

        return entity;
    }
}

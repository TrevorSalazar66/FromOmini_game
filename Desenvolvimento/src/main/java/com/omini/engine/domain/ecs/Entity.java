package com.omini.engine.domain.ecs;

import java.util.*;

/**
 * Entidade central da arquitetura ECS (Entity-Component-System).
 * Representa qualquer objeto do mundo de jogo (criatura, item, obstáculo, projétil),
 * sendo identificada unicamente e composta por um conjunto flexível de componentes.
 */
public final class Entity {

    private final String id;
    private String name;
    private final Set<String> tags;
    private final Map<Class<? extends Component>, Component> components;

    public Entity(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da entidade não pode ser nulo ou vazio.");
        }
        this.id = id.trim();
        this.name = name != null ? name.trim() : "Entidade Desconhecida";
        this.tags = new HashSet<>();
        this.components = new HashMap<>();
    }

    public Entity(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim().toLowerCase());
        }
    }

    public boolean hasTag(String tag) {
        if (tag == null) return false;
        return this.tags.contains(tag.trim().toLowerCase());
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public <T extends Component> Entity addComponent(T component) {
        if (component == null) {
            throw new IllegalArgumentException("Componente não pode ser nulo.");
        }
        this.components.put(component.getClass(), component);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> getComponent(Class<T> componentClass) {
        if (componentClass == null) {
            return Optional.empty();
        }
        Component component = this.components.get(componentClass);
        if (componentClass.isInstance(component)) {
            return Optional.of((T) component);
        }
        return Optional.empty();
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return componentClass != null && this.components.containsKey(componentClass);
    }

    public <T extends Component> Optional<T> removeComponent(Class<T> componentClass) {
        if (componentClass == null) {
            return Optional.empty();
        }
        Component removed = this.components.remove(componentClass);
        if (componentClass.isInstance(removed)) {
            return Optional.of(componentClass.cast(removed));
        }
        return Optional.empty();
    }

    public Collection<Component> getAllComponents() {
        return Collections.unmodifiableCollection(this.components.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", componentsCount=" + components.size() +
                '}';
    }
}

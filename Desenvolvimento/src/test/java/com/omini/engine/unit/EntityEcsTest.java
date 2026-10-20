package com.omini.engine.unit;

import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.MaterialComponent;
import com.omini.engine.domain.ecs.components.PositionComponent;

public final class EntityEcsTest {

    public static void main(String[] args) {
        EntityEcsTest test = new EntityEcsTest();
        test.deveGerenciarComponentesETags();
        test.deveValidarFailFastEmEntidades();
        System.out.println("✅ EntityEcsTest: Todos os testes passaram!");
    }

    public void deveGerenciarComponentesETags() {
        Entity entity = new Entity("entidade_01", "Guerreiro");
        assert "entidade_01".equals(entity.getId()) : "ID incorreto";
        assert "Guerreiro".equals(entity.getName()) : "Nome incorreto";

        // Adicionar tags
        entity.addTag("player");
        entity.addTag("humanoide");
        assert entity.hasTag("player") : "Tag player ausente";
        assert entity.hasTag("HUMANOIDE") : "Tag humanoide case-insensitive ausente";
        assert !entity.hasTag("monstro") : "Tag monstro não deveria existir";

        // Adicionar PositionComponent
        PositionComponent pos = new PositionComponent(10, 20);
        entity.addComponent(pos);
        assert entity.hasComponent(PositionComponent.class) : "PositionComponent ausente";
        assert entity.getComponent(PositionComponent.class).orElseThrow().getX() == 10 : "Posição X incorreta";

        // Adicionar MaterialComponent
        MaterialComponent mat = new MaterialComponent("flesh", 1.0, 5.0);
        entity.addComponent(mat);
        assert entity.hasComponent(MaterialComponent.class) : "MaterialComponent ausente";
        assert "flesh".equals(entity.getComponent(MaterialComponent.class).orElseThrow().getMaterialId()) : "Material ID incorreto";

        // Verificar contagem total de componentes
        assert entity.getAllComponents().size() == 2 : "Deveria ter 2 componentes";

        // Remover PositionComponent
        var removido = entity.removeComponent(PositionComponent.class);
        assert removido.isPresent() : "Componente removido deveria estar presente";
        assert removido.get().getX() == 10 : "Coordenada X divergente";
        assert !entity.hasComponent(PositionComponent.class) : "PositionComponent ainda presente";
        assert entity.getAllComponents().size() == 1 : "Deveria ter 1 componente";
    }

    public void deveValidarFailFastEmEntidades() {
        try {
            new Entity("", "Teste");
            assert false : "Deveria lançar exceção para ID vazio";
        } catch (IllegalArgumentException ignored) {}

        try {
            new Entity(null, "Teste");
            assert false : "Deveria lançar exceção para ID nulo";
        } catch (IllegalArgumentException ignored) {}

        Entity e = new Entity("valido", "Teste");
        try {
            e.addComponent(null);
            assert false : "Deveria lançar exceção para componente nulo";
        } catch (IllegalArgumentException ignored) {}
    }
}

package com.omini.engine.unit;

import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.*;
import com.omini.engine.domain.models.TipoAcaoEnum;
import com.omini.engine.domain.usecases.CalcularCustoAcaoAPUseCase;
import com.omini.engine.domain.usecases.MotorTurnosAPUseCase;

import java.util.List;
import java.util.Optional;

public final class MotorTurnosAPTest {

    public static void main(String[] args) {
        MotorTurnosAPTest test = new MotorTurnosAPTest();
        test.deveRetornarCustosBasePadrao();
        test.deveAumentarCustoAPComSobrecargaDeInventario();
        test.deveDobrarCustoMovimentoComPernaFraturada();
        test.deveGerenciarIniciativaEConsumoDeEnergia();
        System.out.println("✅ MotorTurnosAPTest: Todos os testes passaram!");
    }

    public void deveRetornarCustosBasePadrao() {
        CalcularCustoAcaoAPUseCase useCase = new CalcularCustoAcaoAPUseCase();
        Entity ator = new Entity("ator_limpo", "Sobrevivente Padrão");

        assert useCase.executar(ator, TipoAcaoEnum.MOVER) == 100 : "Custo base MOVER incorreto";
        assert useCase.executar(ator, TipoAcaoEnum.ATACAR) == 120 : "Custo base ATACAR incorreto";
        assert useCase.executar(ator, TipoAcaoEnum.USAR_ITEM) == 80 : "Custo base USAR_ITEM incorreto";
        assert useCase.executar(ator, TipoAcaoEnum.RECARREGAR) == 150 : "Custo base RECARREGAR incorreto";
        assert useCase.executar(ator, TipoAcaoEnum.ESPERAR) == 100 : "Custo base ESPERAR incorreto";
    }

    public void deveAumentarCustoAPComSobrecargaDeInventario() {
        CalcularCustoAcaoAPUseCase useCase = new CalcularCustoAcaoAPUseCase();
        Entity ator = new Entity("ator_pesado", "Sobrevivente Carregado");
        ator.addComponent(new StatsComponent(10, 10, 10, 10)); // Força 10 -> Confortável até 50kg, Pesada até 75kg (+15% AP), Incapacitado > 100kg (+100% AP)

        InventoryGridComponent inv = new InventoryGridComponent(10, 10);
        // Colocar 60kg no inventário (Carga Pesada: +15% AP -> custo 115)
        Entity chumbo = new Entity("chumbo", "Bloco de Chumbo");
        chumbo.addComponent(new ItemComponent("chumbo", "Bloco de Chumbo", 1, 1, 60.0, true));
        inv.inserirItem(chumbo, 0, 0, 1, 1);
        ator.addComponent(inv);

        int custoMoverPesada = useCase.executar(ator, TipoAcaoEnum.MOVER);
        assert custoMoverPesada == 115 : "Custo deveria ser 115 com carga pesada (+15%), mas foi: " + custoMoverPesada;

        // Adicionar mais 55kg (total 115kg -> Incapacitado por excesso de peso: +100% AP -> custo 200)
        Entity chumbo2 = new Entity("chumbo2", "Bloco de Chumbo Extra");
        chumbo2.addComponent(new ItemComponent("chumbo2", "Bloco de Chumbo Extra", 1, 1, 55.0, true));
        inv.inserirItem(chumbo2, 1, 0, 1, 1);

        int custoMoverIncapacitado = useCase.executar(ator, TipoAcaoEnum.MOVER);
        assert custoMoverIncapacitado == 200 : "Custo deveria ser 200 com incapacitado (+100%), mas foi: " + custoMoverIncapacitado;
    }

    public void deveDobrarCustoMovimentoComPernaFraturada() {
        CalcularCustoAcaoAPUseCase useCase = new CalcularCustoAcaoAPUseCase();
        Entity ator = new Entity("ator_machucado", "Sobrevivente Mancando");
        ator.addComponent(new StatsComponent(10, 10, 10, 10));

        AnatomyComponent anatomia = new AnatomyComponent();
        BodyPartStateVO perna = new BodyPartStateVO("left_shin", "Canela Esquerda", 20.0, 20.0, false, true, false, 0.0);
        anatomia.adicionarParte(perna);
        ator.addComponent(anatomia);

        int custoMover = useCase.executar(ator, TipoAcaoEnum.MOVER);
        // Fratura sem tala dobra (+100%) o custo base de locomoção: 100 * 2 = 200
        assert custoMover == 200 : "Custo deveria ser 200 com perna fraturada, mas foi: " + custoMover;

        // Atacar não deve ser penalizado pela perna fraturada
        int custoAtacar = useCase.executar(ator, TipoAcaoEnum.ATACAR);
        assert custoAtacar == 120 : "Custo de atacar não deveria dobrar com perna quebrada";

        // Se colocarmos tala na perna, penalidade cai para +40%
        anatomia.removerParte("left_shin");
        BodyPartStateVO pernaComTala = new BodyPartStateVO("left_shin", "Canela Esquerda", 20.0, 20.0, false, true, true, 0.0);
        anatomia.adicionarParte(pernaComTala);

        int custoMoverComTala = useCase.executar(ator, TipoAcaoEnum.MOVER);
        assert custoMoverComTala == 140 : "Custo com tala deveria ser 140, mas foi: " + custoMoverComTala;
    }

    public void deveGerenciarIniciativaEConsumoDeEnergia() {
        MotorTurnosAPUseCase motor = new MotorTurnosAPUseCase();

        Entity agil = new Entity("agil", "Sobrevivente Ágil");
        agil.addComponent(new StatsComponent(10, 15, 10, 10)); // DES 15 -> velocidade 130
        agil.addComponent(new TurnEnergyComponent(0));

        Entity lento = new Entity("lento", "Criatura Lenta");
        lento.addComponent(new StatsComponent(10, 5, 10, 10)); // DES 5 -> velocidade 110
        lento.addComponent(new TurnEnergyComponent(0));

        List<Entity> combatentes = List.of(agil, lento);

        Optional<Entity> primeira = motor.obterProximaEntidadePronta(combatentes);
        assert primeira.isPresent() : "Deveria haver uma entidade pronta";
        assert primeira.get().getId().equals("agil") : "A entidade ágil deveria agir primeiro";
        assert motor.getRelogioGlobalTicks() > 0 : "O relógio deve ter avançado ticks";

        // Consumir 100 AP da ação
        motor.registrarAcaoExecutada(agil, 100);
        assert !agil.getComponent(TurnEnergyComponent.class).get().podeAgir() : "Ágil não deve mais poder agir imediatamente";
    }
}

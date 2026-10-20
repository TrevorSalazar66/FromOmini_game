package com.omini.engine.integration;

import com.omini.engine.data.loader.CascadeModLoader;
import com.omini.engine.data.repositories.JsonAnatomyRepository;
import com.omini.engine.data.repositories.JsonDamageTypeRepository;
import com.omini.engine.data.repositories.JsonItemRepository;
import com.omini.engine.data.repositories.JsonMaterialRepository;
import com.omini.engine.domain.dtos.AtaqueContextoDTO;
import com.omini.engine.domain.dtos.BodyPartStateVO;
import com.omini.engine.domain.dtos.EstadoDorVO;
import com.omini.engine.domain.dtos.ResultadoDanoLocalizadoVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.AnatomyComponent;
import com.omini.engine.domain.ecs.components.PainFocusComponent;
import com.omini.engine.domain.ecs.components.StatsComponent;
import com.omini.engine.domain.ecs.components.SurvivalComponent;
import com.omini.engine.domain.models.AnatomyDefinitionModel;
import com.omini.engine.domain.models.TipoTratamentoEnum;
import com.omini.engine.domain.usecases.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Random;

public final class FisiologiaAnatomiaIntegrationTest {

    public static void main(String[] args) throws IOException {
        FisiologiaAnatomiaIntegrationTest test = new FisiologiaAnatomiaIntegrationTest();
        test.deveCarregarAnatomiaHumanoidStandard();
        test.deveExecutarSimulacaoCompletaFisiologiaCombateSocorros();
        System.out.println("✅ FisiologiaAnatomiaIntegrationTest: Todos os testes passaram!");
    }

    private CascadeModLoader inicializarModLoader(JsonMaterialRepository matRepo,
                                                  JsonDamageTypeRepository dmgRepo,
                                                  JsonItemRepository itemRepo,
                                                  JsonAnatomyRepository anatomyRepo) throws IOException {
        CascadeModLoader loader = new CascadeModLoader(matRepo, dmgRepo, itemRepo, anatomyRepo);
        Path raizJogo = Paths.get("Desenvolvimento");
        loader.carregar(raizJogo, Collections.emptyList());
        return loader;
    }

    public void deveCarregarAnatomiaHumanoidStandard() throws IOException {
        JsonMaterialRepository matRepo = new JsonMaterialRepository();
        JsonDamageTypeRepository dmgRepo = new JsonDamageTypeRepository();
        JsonItemRepository itemRepo = new JsonItemRepository();
        JsonAnatomyRepository anatomyRepo = new JsonAnatomyRepository();
        inicializarModLoader(matRepo, dmgRepo, itemRepo, anatomyRepo);

        var humanoideOpt = anatomyRepo.buscarPorId("humanoid_standard");
        assert humanoideOpt.isPresent() : "Anatomia humanoid_standard deve estar presente";
        AnatomyDefinitionModel def = humanoideOpt.get();
        assertEquals(14, def.getAllBodyParts().size());

        var headOpt = def.getBodyPart("head");
        assert headOpt.isPresent();
        assert headOpt.get().isVital();
        assertEquals(10, headOpt.get().getHitWeight());
        assert Math.abs(headOpt.get().getBaseHpRatio() - 0.75) < 0.001;

        var torsoOpt = def.getBodyPart("torso");
        assert torsoOpt.isPresent();
        assert torsoOpt.get().isVital();
        assertEquals(40, torsoOpt.get().getHitWeight());
        assert Math.abs(torsoOpt.get().getBaseHpRatio() - 1.0) < 0.001;
    }

    public void deveExecutarSimulacaoCompletaFisiologiaCombateSocorros() throws IOException {
        JsonMaterialRepository matRepo = new JsonMaterialRepository();
        JsonDamageTypeRepository dmgRepo = new JsonDamageTypeRepository();
        JsonItemRepository itemRepo = new JsonItemRepository();
        JsonAnatomyRepository anatomyRepo = new JsonAnatomyRepository();
        inicializarModLoader(matRepo, dmgRepo, itemRepo, anatomyRepo);

        AnatomyDefinitionModel anatomyDef = anatomyRepo.buscarPorId("humanoid_standard").orElseThrow();

        // 1. Instanciar Sobrevivente
        Entity sobrevivente = new Entity("player_01", "Guerreiro Sobrevivente");
        sobrevivente.addTag("player");
        sobrevivente.addComponent(AnatomyComponent.aPartirDeDefinicao(anatomyDef, 100.0));
        sobrevivente.addComponent(new PainFocusComponent(0.0, 100.0));
        sobrevivente.addComponent(new SurvivalComponent(0.0, 0.0, 100.0, 0.0));
        sobrevivente.addComponent(new StatsComponent(12, 14, 10, 10, 10));

        AnatomyComponent anatomy = sobrevivente.getComponent(AnatomyComponent.class).orElseThrow();
        PainFocusComponent painFocus = sobrevivente.getComponent(PainFocusComponent.class).orElseThrow();
        SurvivalComponent survival = sobrevivente.getComponent(SurvivalComponent.class).orElseThrow();

        assert !anatomy.isMorto();
        assert anatomy.getTodasPartes().size() == 14;
        assert Math.abs(anatomy.getParte("head").orElseThrow().getHpAtual() - 75.0) < 0.001;
        assert Math.abs(anatomy.getParte("torso").orElseThrow().getHpAtual() - 100.0) < 0.001;
        assert Math.abs(anatomy.getParte("forearm_l").orElseThrow().getHpAtual() - 35.0) < 0.001;

        // 2. Testar Sorteio com Cobertura Inferior
        Random rng = new Random(42);
        SortearMembroAtingidoUseCase sortearUseCase = new SortearMembroAtingidoUseCase(limite -> rng.nextInt((int) limite));

        AtaqueContextoDTO ataqueComCobertura = new AtaqueContextoDTO(anatomyDef, true, null, 0.0);
        for (int i = 0; i < 200; i++) {
            String membro = sortearUseCase.executar(ataqueComCobertura);
            assert !membro.contains("thigh") && !membro.contains("shin") && !membro.contains("foot") :
                    "Membro inferior atingido apesar de estar atrás de cobertura: " + membro;
        }

        // 3. Testar Mira Cirúrgica com Foco na Cabeça
        AtaqueContextoDTO ataqueMiraCabeca = new AtaqueContextoDTO(anatomyDef, false, "head", 80.0);
        int tirosCabeca = 0;
        for (int i = 0; i < 100; i++) {
            if ("head".equals(sortearUseCase.executar(ataqueMiraCabeca))) {
                tirosCabeca++;
            }
        }
        assert tirosCabeca > 40 : "Mira com 80 de foco deveria elevar a frequência de acertos na cabeça";

        // 4. Aplicar Dano no Antebraço Esquerdo (HP 35.0) com 38 de dano -> Fratura e Sangramento
        AplicarDanoLocalizadoUseCase danoUseCase = new AplicarDanoLocalizadoUseCase();
        ResultadoDanoLocalizadoVO resBraco = danoUseCase.executar(sobrevivente, "forearm_l", 38.0, "blunt");
        assert resBraco.getHpRestante() == 0.0;
        assert resBraco.isCausouFratura() : "Antebraço deveria ter fraturado";
        assert !resBraco.isCausouMorte() : "Fratura no braço não é letal";
        assert resBraco.getDorGerada() > 0.0;

        // Verificar dor e teto de foco
        CalcularPenalidadesDorUseCase penalidadesDorUseCase = new CalcularPenalidadesDorUseCase();
        EstadoDorVO estadoDor = penalidadesDorUseCase.executar(painFocus);
        assert painFocus.getDorEfetiva() > 30.0;
        assert painFocus.getTetoFoco() < 70.0;
        assert estadoDor.getPenalidadeRolagensPercentual() >= 0.15;

        // 5. Passagem de 3 turnos: o membro sangra continuamente e os parâmetros de sobrevivência avançam
        double sangramentoAntes = anatomy.getSangramentoTotal();
        assert sangramentoAntes > 0.0;

        ProcessarCicloTurnoFisiologicoUseCase cicloTurnoUseCase = new ProcessarCicloTurnoFisiologicoUseCase();
        cicloTurnoUseCase.executar(sobrevivente, 3);

        assert survival.getSede() > 0.0;
        assert survival.getFome() > 0.0;

        // 6. Tratamento Médico (Primeiros Socorros): Curativo + Tala + Analgésico
        TratarFerimentoUseCase tratarUseCase = new TratarFerimentoUseCase();

        // 6.1 Curativo
        boolean curativoSucesso = tratarUseCase.executar(sobrevivente, "forearm_l", TipoTratamentoEnum.CURATIVO_SANGRAMENTO);
        assert curativoSucesso : "Curativo deveria estancar o sangramento";
        assert anatomy.getSangramentoTotal() == 0.0 : "Sangramento ativo deveria ser 0 após curativo";

        // 6.2 Tala
        boolean talaSucesso = tratarUseCase.executar(sobrevivente, "forearm_l", TipoTratamentoEnum.TALA_FRATURA);
        assert talaSucesso : "Tala deveria imobilizar o antebraço fraturado";
        BodyPartStateVO antebraco = anatomy.getParte("forearm_l").orElseThrow();
        assert antebraco.isImobilizadoComTala();

        // 6.3 Analgésico
        double dorAntesRemedio = painFocus.getDorEfetiva();
        boolean remedioSucesso = tratarUseCase.executar(sobrevivente, null, TipoTratamentoEnum.ANALGESICO, 35.0);
        assert remedioSucesso;
        assert painFocus.getDorEfetiva() < dorAntesRemedio : "Dor efetiva deveria ter diminuído com analgésico";
        assert painFocus.getTetoFoco() > 50.0 : "Teto de foco deveria ter se recuperado";

        // 7. Teste de Letalidade Vital: Dano fulminante no Tronco (vital)
        ResultadoDanoLocalizadoVO resTronco = danoUseCase.executar(sobrevivente, "torso", 110.0, "pierce");
        assert resTronco.isCausouMorte() : "Dano de 110 no tronco vital de 100 HP deve causar morte imediata";
        assert anatomy.isMorto();
        assert sobrevivente.hasTag("morto");
    }

    private static void assertEquals(Object expected, Object actual) {
        assert java.util.Objects.equals(expected, actual) : "Esperado [" + expected + "], mas foi [" + actual + "]";
    }
}

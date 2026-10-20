package com.omini.engine.integration;

import com.omini.engine.data.loader.CascadeModLoader;
import com.omini.engine.data.repositories.JsonDamageTypeRepository;
import com.omini.engine.data.repositories.JsonMaterialRepository;
import com.omini.engine.domain.dtos.InteracaoAlvoDTO;
import com.omini.engine.domain.dtos.RangeTesteVO;
import com.omini.engine.domain.dtos.TabelaPesosVO;
import com.omini.engine.domain.models.DamageTypeModel;
import com.omini.engine.domain.models.GrauSucessoEnum;
import com.omini.engine.domain.models.MaterialModel;
import com.omini.engine.domain.usecases.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public final class NucleoMotorIntegrationTest {

    public static void main(String[] args) throws IOException {
        NucleoMotorIntegrationTest test = new NucleoMotorIntegrationTest();
        test.executarCenarioIntegradoDeMinacaoECombate();
        System.out.println("✅ NucleoMotorIntegrationTest (Camada 2): Todos os testes de integração passaram!");
    }

    public void executarCenarioIntegradoDeMinacaoECombate() throws IOException {
        // 1. Inicializar Repositórios e Carregador em Cascata
        JsonMaterialRepository materialRepo = new JsonMaterialRepository();
        JsonDamageTypeRepository damageRepo = new JsonDamageTypeRepository();
        CascadeModLoader modLoader = new CascadeModLoader(materialRepo, damageRepo);

        // Caminho dos dados do jogo base
        Path baseDir = Paths.get("Desenvolvimento");
        modLoader.carregar(baseDir, Collections.emptyList());

        // 2. Verificar se os materiais foram indexados
        MaterialModel granito = materialRepo.buscarPorId("stone_granite")
            .orElseThrow(() -> new AssertionError("Material stone_granite não foi carregado."));
        DamageTypeModel picareta = damageRepo.buscarPorId("mining_pickaxe")
            .orElseThrow(() -> new AssertionError("Tipo de dano mining_pickaxe não foi carregado."));

        assert granito.getDensidade() == 5.0;
        assert granito.getDurezaBase() == 45.0;
        assert granito.getModificadorDano("mining_pickaxe") == 2.0;
        assert granito.getModificadorDano("slash") == 0.05;

        // 3. Cenário: Parede de Granito Nível 60 sendo quebrada com Picareta
        CalcularAlvoDificuldadeUseCase alvoUseCase = new CalcularAlvoDificuldadeUseCase();
        InteracaoAlvoDTO interacaoPicareta = new InteracaoAlvoDTO(
            granito.getDurezaBase(),
            60,
            granito.getModificadorDano("mining_pickaxe")
        );
        double alvoPicareta = alvoUseCase.executar(interacaoPicareta);
        // Alvo = (45 * 60) / 2.0 = 2700 / 2.0 = 1350
        assert Math.abs(alvoPicareta - 1350.0) < 0.001 : "Alvo esperado 1350.0, obtido " + alvoPicareta;

        // 4. Cenário Impossível: Tentar quebrar Parede de Granito com Espada de Corte
        InteracaoAlvoDTO interacaoEspada = new InteracaoAlvoDTO(
            granito.getDurezaBase(),
            60,
            granito.getModificadorDano("slash")
        );
        double alvoEspada = alvoUseCase.executar(interacaoEspada);
        // Alvo = 2700 / 0.05 = 54000.0
        assert Math.abs(alvoEspada - 54000.0) < 0.001;

        // Mesmo um jogador no ápice com Atributo 100 (Max roll 1000)
        ResolverGrauSucessoUseCase resolverUseCase = new ResolverGrauSucessoUseCase();
        GrauSucessoEnum grauEspada = resolverUseCase.executar(1000, alvoEspada);
        // 1000 contra 54000 dá delta -0.98 <= -0.50 -> FALHA CRÍTICA garantida
        assert grauEspada == GrauSucessoEnum.FALHA_CRITICA : "Espada contra pedra deve ser sempre Falha Crítica";

        // 5. Testar Resolução Dinâmica com Picareta para Personagem Forte (Atributo 150 -> Range 150 a 1500)
        CalcularRangeTesteUseCase rangeUseCase = new CalcularRangeTesteUseCase();
        CalcularDistribuicaoPesosUseCase pesosUseCase = new CalcularDistribuicaoPesosUseCase();
        ExecutarSorteioPonderadoUseCase sorteioUseCase = new ExecutarSorteioPonderadoUseCase();

        RangeTesteVO rangeJogador = rangeUseCase.executar(150);
        assert rangeJogador.minimo() == 150;
        assert rangeJogador.maximo() == 1500;

        // Grão-Mestre (Nível 70) com Picareta: rola consistentemente no topo do range (1450-1500)
        TabelaPesosVO pesosGraoMestre = pesosUseCase.executar(rangeJogador, 70);
        int resultadoSorteado = sorteioUseCase.executar(pesosGraoMestre);

        assert resultadoSorteado >= 1400 : "Grão-Mestre deveria rolar acima de 1400. Obtido: " + resultadoSorteado;
        GrauSucessoEnum grauPicareta = resolverUseCase.executar(resultadoSorteado, alvoPicareta);

        // Se o resultado for 1450 contra alvo 1350: (1450 - 1350)/1350 = +7.4% -> SUCESSO TOTAL
        // Se passar de +20% (>= 1620) seria extremo
        assert grauPicaoOuTotal(grauPicareta) : "Resultado esperado Sucesso Total ou Extremo, obtido " + grauPicareta;
    }

    private boolean grauPicaoOuTotal(GrauSucessoEnum grau) {
        return grau == GrauSucessoEnum.SUCESSO_TOTAL || grau == GrauSucessoEnum.SUCESSO_EXTREMO;
    }
}

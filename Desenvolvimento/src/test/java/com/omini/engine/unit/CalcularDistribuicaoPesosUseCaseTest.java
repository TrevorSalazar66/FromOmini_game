package com.omini.engine.unit;

import com.omini.engine.domain.dtos.RangeTesteVO;
import com.omini.engine.domain.dtos.TabelaPesosVO;
import com.omini.engine.domain.usecases.CalcularDistribuicaoPesosUseCase;

public final class CalcularDistribuicaoPesosUseCaseTest {

    public static void main(String[] args) {
        CalcularDistribuicaoPesosUseCaseTest test = new CalcularDistribuicaoPesosUseCaseTest();
        test.deveGerarRampaDecrescenteParaNivelZero();
        test.deveGerarMonteDeAreiaContinuoParaNiveisSuperiores();
        test.deveGarantirQueGraoMestre1SuperaMestre10();
        test.deveGarantirQueNenhumPesoSejaMenorQueUm();
        System.out.println("✅ CalcularDistribuicaoPesosUseCaseTest: Todos os testes passaram!");
    }

    public void deveGerarRampaDecrescenteParaNivelZero() {
        CalcularDistribuicaoPesosUseCase useCase = new CalcularDistribuicaoPesosUseCase();
        RangeTesteVO range = new RangeTesteVO(10, 100);

        TabelaPesosVO tabela = useCase.executar(range, 0);

        long pesoMinimo = tabela.getPeso(10);
        long pesoMaximo = tabela.getPeso(100);
        long pesoMeio = tabela.getPeso(55);

        assert pesoMinimo == 100L : "Peso do mínimo esperado 100, obtido " + pesoMinimo;
        assert pesoMaximo == 1L : "Peso do máximo esperado 1, obtido " + pesoMaximo;
        assert pesoMinimo > pesoMeio : "Rampa deve ser estritamente decrescente";
        assert pesoMeio > pesoMaximo : "Rampa deve ser estritamente decrescente";
    }

    public void deveGerarMonteDeAreiaContinuoParaNiveisSuperiores() {
        CalcularDistribuicaoPesosUseCase useCase = new CalcularDistribuicaoPesosUseCase();
        RangeTesteVO range = new RangeTesteVO(100, 1000);

        // Aprendiz (Nível 1): foco perto de 10% a 20% do range (casa dos 190-250)
        TabelaPesosVO tabelaAprendiz = useCase.executar(range, 1);
        long pesoAprendizBaixo = tabelaAprendiz.getPeso(190);
        long pesoAprendizTopo = tabelaAprendiz.getPeso(950);
        assert pesoAprendizBaixo > pesoAprendizTopo : "Aprendiz deve focar no terço inferior do range";

        // Grão-Mestre (Nível 70): foco absoluto perto do limite superior (casa dos 950-1000)
        TabelaPesosVO tabelaGraoMestre = useCase.executar(range, 70);
        long pesoGraoMestreBaixo = tabelaGraoMestre.getPeso(190);
        long pesoGraoMestreTopo = tabelaGraoMestre.getPeso(980);
        assert pesoGraoMestreTopo > pesoGraoMestreBaixo * 100 : "Grão-mestre deve puxar quase todo peso para o topo";
    }

    public void deveGarantirQueGraoMestre1SuperaMestre10() {
        CalcularDistribuicaoPesosUseCase useCase = new CalcularDistribuicaoPesosUseCase();
        RangeTesteVO range = new RangeTesteVO(100, 1000);

        // Mestre 10 = Nível 60
        TabelaPesosVO tabelaMestre10 = useCase.executar(range, 60);

        // Grão-Mestre 1 = Nível 61
        TabelaPesosVO tabelaGraoMestre1 = useCase.executar(range, 61);

        // Encontrar valor do pico (onde o peso é máximo) em ambos
        int picoMestre = encontrarValorPico(tabelaMestre10, range);
        int picoGraoMestre = encontrarValorPico(tabelaGraoMestre1, range);

        assert picoGraoMestre >= picoMestre :
            "Herança contínua falhou: Grão-Mestre 1 (pico " + picoGraoMestre +
            ") deve ter pico maior ou igual a Mestre 10 (pico " + picoMestre + ")";
    }

    public void deveGarantirQueNenhumPesoSejaMenorQueUm() {
        CalcularDistribuicaoPesosUseCase useCase = new CalcularDistribuicaoPesosUseCase();
        RangeTesteVO range = new RangeTesteVO(50, 500);

        for (int nivel = 0; nivel <= 70; nivel += 10) {
            TabelaPesosVO tabela = useCase.executar(range, nivel);
            for (int val = range.minimo(); val <= range.maximo(); val++) {
                assert tabela.getPeso(val) >= 1L : "Nenhum valor pode ter peso nulo ou negativo";
            }
        }
    }

    private int encontrarValorPico(TabelaPesosVO tabela, RangeTesteVO range) {
        int melhorValor = range.minimo();
        long maiorPeso = -1;
        for (int v = range.minimo(); v <= range.maximo(); v++) {
            long p = tabela.getPeso(v);
            if (p > maiorPeso) {
                maiorPeso = p;
                melhorValor = v;
            }
        }
        return melhorValor;
    }
}

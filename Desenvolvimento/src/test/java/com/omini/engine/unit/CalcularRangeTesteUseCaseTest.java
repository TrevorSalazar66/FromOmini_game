package com.omini.engine.unit;

import com.omini.engine.domain.dtos.RangeTesteVO;
import com.omini.engine.domain.usecases.CalcularRangeTesteUseCase;

public final class CalcularRangeTesteUseCaseTest {

    public static void main(String[] args) {
        CalcularRangeTesteUseCaseTest test = new CalcularRangeTesteUseCaseTest();
        test.deveCalcularRangePadraoMultiplicador10();
        test.deveCalcularRangeComMultiplicadorCustomizado();
        test.deveLancarExcecaoParaAtributoMenorQueUm();
        System.out.println("✅ CalcularRangeTesteUseCaseTest: Todos os testes passaram!");
    }

    public void deveCalcularRangePadraoMultiplicador10() {
        CalcularRangeTesteUseCase useCase = new CalcularRangeTesteUseCase();

        RangeTesteVO range10 = useCase.executar(10);
        assert range10.minimo() == 10 : "Minimo esperado 10, obtido " + range10.minimo();
        assert range10.maximo() == 100 : "Maximo esperado 100, obtido " + range10.maximo();
        assert range10.getTamanhoIntervalo() == 91 : "Tamanho esperado 91, obtido " + range10.getTamanhoIntervalo();

        RangeTesteVO range50 = useCase.executar(50);
        assert range50.minimo() == 50 : "Minimo esperado 50, obtido " + range50.minimo();
        assert range50.maximo() == 500 : "Maximo esperado 500, obtido " + range50.maximo();

        RangeTesteVO range100 = useCase.executar(100);
        assert range100.minimo() == 100 : "Minimo esperado 100, obtido " + range100.minimo();
        assert range100.maximo() == 1000 : "Maximo esperado 1000, obtido " + range100.maximo();
    }

    public void deveCalcularRangeComMultiplicadorCustomizado() {
        CalcularRangeTesteUseCase useCase = new CalcularRangeTesteUseCase();
        RangeTesteVO range = useCase.executar(20, 5);

        assert range.minimo() == 20;
        assert range.maximo() == 100;
        assert range.getTamanhoIntervalo() == 81;
    }

    public void deveLancarExcecaoParaAtributoMenorQueUm() {
        CalcularRangeTesteUseCase useCase = new CalcularRangeTesteUseCase();
        try {
            useCase.executar(0);
            assert false : "Deveria ter lançado IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            // Sucesso
        }
    }
}

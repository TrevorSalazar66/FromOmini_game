package com.omini.engine.unit;

import com.omini.engine.domain.usecases.CalcularMassaProceduralUseCase;

public final class CalcularMassaProceduralUseCaseTest {

    public static void main(String[] args) {
        CalcularMassaProceduralUseCaseTest test = new CalcularMassaProceduralUseCaseTest();
        test.deveCalcularMassaProceduralCorretamente();
        test.deveRejeitarDimensoesOuDensidadesInvalidas();
        System.out.println("✅ CalcularMassaProceduralUseCaseTest: Todos os testes passaram!");
    }

    public void deveCalcularMassaProceduralCorretamente() {
        CalcularMassaProceduralUseCase useCase = new CalcularMassaProceduralUseCase();

        // Travesseiro de Penas: 2x2 = 4 slots. Densidade penas = 0.1 -> Peso = 0.4
        double massaTravesseiro = useCase.executar(2, 2, 0.1);
        assert Math.abs(massaTravesseiro - 0.4) < 0.001 : "Esperado 0.4, obtido " + massaTravesseiro;

        // Porrete de Madeira: 1x3 = 3 slots. Densidade madeira maciça = 2.0 -> Peso = 6.0
        double massaPorrete = useCase.executar(1, 3, 2.0);
        assert Math.abs(massaPorrete - 6.0) < 0.001 : "Esperado 6.0, obtido " + massaPorrete;

        // Cano de Chumbo: 1x3 = 3 slots. Densidade chumbo = 11.3 -> Peso = 33.9
        double massaCano = useCase.executar(1, 3, 11.3);
        assert Math.abs(massaCano - 33.9) < 0.001 : "Esperado 33.9, obtido " + massaCano;

        // Bloco de Ouro Maciço: 2x2 = 4 slots. Densidade ouro = 19.3 -> Peso = 77.2
        double massaOuro = useCase.executar(2, 2, 19.3);
        assert Math.abs(massaOuro - 77.2) < 0.001 : "Esperado 77.2, obtido " + massaOuro;
    }

    public void deveRejeitarDimensoesOuDensidadesInvalidas() {
        CalcularMassaProceduralUseCase useCase = new CalcularMassaProceduralUseCase();
        try {
            useCase.executar(0, 2, 1.0);
            assert false : "Deveria rejeitar largura zero";
        } catch (IllegalArgumentException e) {
            // Sucesso
        }
        try {
            useCase.executar(2, 2, 0.0);
            assert false : "Deveria rejeitar densidade zero";
        } catch (IllegalArgumentException e) {
            // Sucesso
        }
    }
}

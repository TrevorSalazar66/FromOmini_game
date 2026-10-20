package com.omini.engine.unit;

import com.omini.engine.domain.models.GrauSucessoEnum;
import com.omini.engine.domain.usecases.ResolverGrauSucessoUseCase;

public final class ResolverGrauSucessoUseCaseTest {

    public static void main(String[] args) {
        ResolverGrauSucessoUseCaseTest test = new ResolverGrauSucessoUseCaseTest();
        test.deveClassificarCorretamenteOsCincoGrausDeSucesso();
        test.deveRejeitarAlvoInvalido();
        System.out.println("✅ ResolverGrauSucessoUseCaseTest: Todos os testes passaram!");
    }

    public void deveClassificarCorretamenteOsCincoGrausDeSucesso() {
        ResolverGrauSucessoUseCase useCase = new ResolverGrauSucessoUseCase();
        double alvo = 400.0;

        // Falha Crítica: <= -50% (<= 200)
        assert useCase.executar(150, alvo) == GrauSucessoEnum.FALHA_CRITICA;
        assert useCase.executar(200, alvo) == GrauSucessoEnum.FALHA_CRITICA;

        // Falha: -15% a -49% (201 a 340)
        assert useCase.executar(250, alvo) == GrauSucessoEnum.FALHA;
        assert useCase.executar(340, alvo) == GrauSucessoEnum.FALHA;

        // Sucesso Parcial: quase no alvo (341 a 399)
        assert useCase.executar(350, alvo) == GrauSucessoEnum.SUCESSO_PARCIAL;
        assert useCase.executar(399, alvo) == GrauSucessoEnum.SUCESSO_PARCIAL;

        // Sucesso Total: no alvo ou até +19% (400 a 479)
        assert useCase.executar(400, alvo) == GrauSucessoEnum.SUCESSO_TOTAL;
        assert useCase.executar(450, alvo) == GrauSucessoEnum.SUCESSO_TOTAL;

        // Sucesso Extremo: >= +20% (>= 480)
        assert useCase.executar(480, alvo) == GrauSucessoEnum.SUCESSO_EXTREMO;
        assert useCase.executar(600, alvo) == GrauSucessoEnum.SUCESSO_EXTREMO;
    }

    public void deveRejeitarAlvoInvalido() {
        ResolverGrauSucessoUseCase useCase = new ResolverGrauSucessoUseCase();
        try {
            useCase.executar(100, 0);
            assert false : "Deveria ter lançado exceção para alvo zero";
        } catch (IllegalArgumentException e) {
            // Sucesso
        }
    }
}

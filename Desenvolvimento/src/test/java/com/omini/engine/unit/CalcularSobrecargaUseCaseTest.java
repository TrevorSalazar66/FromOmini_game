package com.omini.engine.unit;

import com.omini.engine.domain.dtos.EstadoSobrecargaVO;
import com.omini.engine.domain.models.NivelSobrecargaEnum;
import com.omini.engine.domain.usecases.CalcularSobrecargaUseCase;

public final class CalcularSobrecargaUseCaseTest {

    public static void main(String[] args) {
        CalcularSobrecargaUseCaseTest test = new CalcularSobrecargaUseCaseTest();
        test.deveCalcularCapacidadesBaseadasNaForca();
        test.deveClassificarCargaLeve();
        test.deveClassificarCargaConfortavel();
        test.deveClassificarCargaPesada();
        test.deveClassificarCargaCritica();
        test.deveClassificarIncapacitado();
        test.deveLancarExcecaoParaEntradasInvalidas();
        System.out.println("✅ CalcularSobrecargaUseCaseTest: Todos os testes passaram!");
    }

    public void deveCalcularCapacidadesBaseadasNaForca() {
        CalcularSobrecargaUseCase useCase = new CalcularSobrecargaUseCase();
        // Força 10 -> Confortável: 50kg, Máximo: 100kg
        EstadoSobrecargaVO vo = useCase.executar(10.0, 10);
        assert Math.abs(vo.getCapacidadeConfortavel() - 50.0) < 0.001 : "Confortável deveria ser 50kg";
        assert Math.abs(vo.getCapacidadeMaxima() - 100.0) < 0.001 : "Máximo deveria ser 100kg";
        assert !vo.isEstaSobrecarregado() : "Não deveria estar sobrecarregado";
        assert vo.getNivelSobrecarga() == NivelSobrecargaEnum.LEVE : "Deveria ser LEVE";
    }

    public void deveClassificarCargaLeve() {
        CalcularSobrecargaUseCase useCase = new CalcularSobrecargaUseCase();
        // Força 10 (confortável 50kg). 25kg é exatamente 50%
        EstadoSobrecargaVO vo = useCase.executar(25.0, 10);
        assert vo.getNivelSobrecarga() == NivelSobrecargaEnum.LEVE : "Deveria ser LEVE";
        assert !vo.isEstaSobrecarregado();
        assert vo.getPenalidadeAPPercentual() == 0;
        assert Math.abs(vo.getPenalidadeEsquivaPercentual()) < 0.001;
    }

    public void deveClassificarCargaConfortavel() {
        CalcularSobrecargaUseCase useCase = new CalcularSobrecargaUseCase();
        // Força 10 -> 40kg transportados
        EstadoSobrecargaVO vo = useCase.executar(40.0, 10);
        assert vo.getNivelSobrecarga() == NivelSobrecargaEnum.CONFORTAVEL : "Deveria ser CONFORTAVEL";
        assert !vo.isEstaSobrecarregado();
        assert vo.getPenalidadeAPPercentual() == 0;
        assert Math.abs(vo.getPenalidadeEsquivaPercentual()) < 0.001;
    }

    public void deveClassificarCargaPesada() {
        CalcularSobrecargaUseCase useCase = new CalcularSobrecargaUseCase();
        // Força 10 (confortável 50kg). 65kg transportados
        EstadoSobrecargaVO vo = useCase.executar(65.0, 10);
        assert vo.getNivelSobrecarga() == NivelSobrecargaEnum.PESADA : "Deveria ser PESADA";
        assert vo.isEstaSobrecarregado() : "Deveria estar sobrecarregado";
        assert vo.getPenalidadeAPPercentual() == 15 : "Penalidade AP incorreta";
        assert Math.abs(vo.getPenalidadeEsquivaPercentual() - 0.20) < 0.001 : "Penalidade esquiva incorreta";
    }

    public void deveClassificarCargaCritica() {
        CalcularSobrecargaUseCase useCase = new CalcularSobrecargaUseCase();
        // Força 10 (confortável 50kg, máximo 100kg). 90kg transportados
        EstadoSobrecargaVO vo = useCase.executar(90.0, 10);
        assert vo.getNivelSobrecarga() == NivelSobrecargaEnum.CRITICA : "Deveria ser CRITICA";
        assert vo.isEstaSobrecarregado();
        assert vo.getPenalidadeAPPercentual() == 40;
        assert Math.abs(vo.getPenalidadeEsquivaPercentual() - 0.50) < 0.001;
    }

    public void deveClassificarIncapacitado() {
        CalcularSobrecargaUseCase useCase = new CalcularSobrecargaUseCase();
        // Força 10 (máximo 100kg). 105kg transportados
        EstadoSobrecargaVO vo = useCase.executar(105.0, 10);
        assert vo.getNivelSobrecarga() == NivelSobrecargaEnum.INCAPACITADO : "Deveria ser INCAPACITADO";
        assert vo.isEstaSobrecarregado();
        assert vo.getPenalidadeAPPercentual() == 100;
        assert Math.abs(vo.getPenalidadeEsquivaPercentual() - 1.00) < 0.001;
    }

    public void deveLancarExcecaoParaEntradasInvalidas() {
        CalcularSobrecargaUseCase useCase = new CalcularSobrecargaUseCase();
        try {
            useCase.executar(10.0, 0);
            assert false : "Deveria ter lançado IllegalArgumentException para Força 0";
        } catch (IllegalArgumentException ignored) {}

        try {
            useCase.executar(-5.0, 10);
            assert false : "Deveria ter lançado IllegalArgumentException para peso negativo";
        } catch (IllegalArgumentException ignored) {}
    }
}

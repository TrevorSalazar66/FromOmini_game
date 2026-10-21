package com.omini.engine.unit;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.ui.views.ViewportCamera;

public final class ViewportCameraTest {

    public static void main(String[] args) {
        ViewportCameraTest test = new ViewportCameraTest();
        test.deveConverterCoordenadasMundoETelaCorretamente();
        test.deveAplicarZoomEScalarTamanhoTile();
        test.deveDetectarVisibilidadeNaViewport();
        System.out.println("✅ ViewportCameraTest: Todos os testes passaram!");
    }

    public void deveConverterCoordenadasMundoETelaCorretamente() {
        ViewportCamera camera = new ViewportCamera(1000, 1000, 50);
        camera.centralizarEm(10.0, 10.0);

        // O centro do mundo (10, 10) deve estar exatamente no centro da tela (500, 500)
        int[] posCentro = camera.mundoParaTela(10.0, 10.0);
        assert posCentro[0] == 500 : "Esperado telaX 500, mas foi " + posCentro[0];
        assert posCentro[1] == 500 : "Esperado telaY 500, mas foi " + posCentro[1];

        // 1 tile para a direita (11, 10) com tileSize 50 deve ir para (550, 500)
        int[] posDireita = camera.mundoParaTela(11.0, 10.0);
        assert posDireita[0] == 550 : "Esperado telaX 550, mas foi " + posDireita[0];
        assert posDireita[1] == 500;

        // Conversão reversa: tela (500, 500) deve mapear de volta para tile (10, 10)
        CoordenadaVO coord = camera.telaParaMundo(500, 500);
        assert coord.x() == 10 : "Esperado x=10, obtido " + coord.x();
        assert coord.y() == 10 : "Esperado y=10, obtido " + coord.y();
    }

    public void deveAplicarZoomEScalarTamanhoTile() {
        ViewportCamera camera = new ViewportCamera(800, 600, 40);
        assert Math.abs(camera.getTamanhoTileEfetivo() - 40.0) < 0.001;

        camera.setZoom(2.0);
        assert Math.abs(camera.getTamanhoTileEfetivo() - 80.0) < 0.001;

        // Limite superior de zoom (3.0)
        camera.setZoom(5.0);
        assert Math.abs(camera.getZoom() - 3.0) < 0.001;

        // Limite inferior de zoom (0.5)
        camera.setZoom(0.1);
        assert Math.abs(camera.getZoom() - 0.5) < 0.001;
    }

    public void deveDetectarVisibilidadeNaViewport() {
        ViewportCamera camera = new ViewportCamera(400, 400, 50);
        camera.centralizarEm(0.0, 0.0);

        assert camera.isVisivelNaTela(0, 0) : "Centro deveria estar visível";
        assert camera.isVisivelNaTela(2, 2) : "(2, 2) deveria estar visível";
        assert !camera.isVisivelNaTela(100, 100) : "(100, 100) muito distante não deveria estar visível";
    }
}

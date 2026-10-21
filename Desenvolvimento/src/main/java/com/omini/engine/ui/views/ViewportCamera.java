package com.omini.engine.ui.views;

import com.omini.engine.domain.dtos.CoordenadaVO;

/**
 * Câmera 2D desacoplada responsável pelo enquadramento, zoom e projeção
 * entre coordenadas do mundo (tiles) e coordenadas da tela touch do dispositivo móvel.
 */
public final class ViewportCamera {

    private int larguraTelaPixels;
    private int alturaTelaPixels;
    private int tamanhoTileBasePixels;
    private double zoom;
    private double centroMundoX;
    private double centroMundoY;

    public ViewportCamera(int larguraTelaPixels, int alturaTelaPixels, int tamanhoTileBasePixels) {
        if (larguraTelaPixels <= 0 || alturaTelaPixels <= 0) {
            throw new IllegalArgumentException("As dimensões da tela devem ser estritamente positivas.");
        }
        if (tamanhoTileBasePixels <= 0) {
            throw new IllegalArgumentException("O tamanho base do tile deve ser positivo.");
        }
        this.larguraTelaPixels = larguraTelaPixels;
        this.alturaTelaPixels = alturaTelaPixels;
        this.tamanhoTileBasePixels = tamanhoTileBasePixels;
        this.zoom = 1.0;
        this.centroMundoX = 0.0;
        this.centroMundoY = 0.0;
    }

    public ViewportCamera() {
        this(1080, 1920, 48); // Resolução padrão mobile portrait
    }

    public void centralizarEm(double mundoX, double mundoY) {
        this.centroMundoX = mundoX;
        this.centroMundoY = mundoY;
    }

    public void redimensionarTela(int largura, int altura) {
        if (largura > 0 && altura > 0) {
            this.larguraTelaPixels = largura;
            this.alturaTelaPixels = altura;
        }
    }

    public void setZoom(double zoom) {
        this.zoom = Math.max(0.5, Math.min(3.0, zoom));
    }

    public double getZoom() {
        return zoom;
    }

    public double getTamanhoTileEfetivo() {
        return tamanhoTileBasePixels * zoom;
    }

    public int getLarguraTelaPixels() {
        return larguraTelaPixels;
    }

    public int getAlturaTelaPixels() {
        return alturaTelaPixels;
    }

    public double getCentroMundoX() {
        return centroMundoX;
    }

    public double getCentroMundoY() {
        return centroMundoY;
    }

    /**
     * Converte uma coordenada do mundo (tile x, y) para a posição em pixels na tela.
     */
    public int[] mundoParaTela(double mundoX, double mundoY) {
        double tileSize = getTamanhoTileEfetivo();
        double deltaX = (mundoX - centroMundoX) * tileSize;
        double deltaY = (mundoY - centroMundoY) * tileSize;

        int telaX = (int) Math.round((larguraTelaPixels / 2.0) + deltaX);
        int telaY = (int) Math.round((alturaTelaPixels / 2.0) + deltaY);

        return new int[]{telaX, telaY};
    }

    /**
     * Converte o ponto de toque na tela (pixels) para a coordenada de tile do mundo.
     */
    public CoordenadaVO telaParaMundo(int telaX, int telaY) {
        double tileSize = getTamanhoTileEfetivo();
        double deltaPixelsX = telaX - (larguraTelaPixels / 2.0);
        double deltaPixelsY = telaY - (alturaTelaPixels / 2.0);

        double mundoX = centroMundoX + (deltaPixelsX / tileSize);
        double mundoY = centroMundoY + (deltaPixelsY / tileSize);

        return new CoordenadaVO((int) Math.floor(mundoX), (int) Math.floor(mundoY));
    }

    /**
     * Verifica se determinado tile está dentro da área visível da viewport atual.
     */
    public boolean isVisivelNaTela(int mundoX, int mundoY) {
        int[] posTela = mundoParaTela(mundoX, mundoY);
        double tileSize = getTamanhoTileEfetivo();

        return posTela[0] + tileSize >= 0 && posTela[0] <= larguraTelaPixels &&
               posTela[1] + tileSize >= 0 && posTela[1] <= alturaTelaPixels;
    }
}

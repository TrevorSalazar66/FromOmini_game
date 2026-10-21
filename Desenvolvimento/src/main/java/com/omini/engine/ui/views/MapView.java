package com.omini.engine.ui.views;

import com.omini.engine.domain.dtos.CoordenadaVO;
import com.omini.engine.domain.dtos.RenderCellVO;
import com.omini.engine.domain.ecs.Entity;
import com.omini.engine.domain.ecs.components.SurfaceFieldComponent;
import com.omini.engine.domain.models.TileMap;
import com.omini.engine.domain.models.TileTypeEnum;

import java.util.*;

/**
 * Renderizador de tela desacoplado em camadas para o mapa do jogo.
 * Suporta neblina de guerra (FOV), superfícies elementais, itens no chão e criaturas.
 */
public final class MapView {

    private final ViewportCamera camera;
    private TileMap mapa;
    private final Set<CoordenadaVO> tilesExplorados;
    private Set<CoordenadaVO> tilesVisiveisFov;

    public MapView(ViewportCamera camera, TileMap mapa) {
        if (camera == null) {
            throw new IllegalArgumentException("A câmera da viewport não pode ser nula.");
        }
        this.camera = camera;
        this.mapa = mapa;
        this.tilesExplorados = new HashSet<>();
        this.tilesVisiveisFov = Collections.emptySet();
    }

    public MapView(ViewportCamera camera) {
        this(camera, null);
    }

    public void setMapa(TileMap mapa) {
        this.mapa = mapa;
        this.tilesExplorados.clear();
        this.tilesVisiveisFov = Collections.emptySet();
    }

    public TileMap getMapa() {
        return mapa;
    }

    public ViewportCamera getCamera() {
        return camera;
    }

    public void atualizarFov(Set<CoordenadaVO> novoFov) {
        this.tilesVisiveisFov = novoFov != null ? new HashSet<>(novoFov) : Collections.emptySet();
        this.tilesExplorados.addAll(this.tilesVisiveisFov);
    }

    public boolean isExplorado(int x, int y) {
        return tilesExplorados.contains(new CoordenadaVO(x, y));
    }

    public boolean isVisivelFov(int x, int y) {
        return tilesVisiveisFov.contains(new CoordenadaVO(x, y));
    }

    public CoordenadaVO processarToqueTela(int telaX, int telaY) {
        return camera.telaParaMundo(telaX, telaY);
    }

    /**
     * Renderiza e extrai as células ativas visíveis dentro da área da câmera.
     */
    public List<RenderCellVO> extrairQuadroRenderizacao() {
        if (mapa == null) {
            return Collections.emptyList();
        }

        List<RenderCellVO> buffer = new ArrayList<>();
        int largura = mapa.getLargura();
        int altura = mapa.getAltura();

        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                if (!camera.isVisivelNaTela(x, y)) {
                    continue;
                }

                CoordenadaVO coord = new CoordenadaVO(x, y);
                boolean explorado = tilesExplorados.contains(coord);
                boolean visivel = tilesVisiveisFov.contains(coord);

                if (!explorado && !visivel) {
                    continue; // Oculto pela escuridão total
                }

                int[] posTela = camera.mundoParaTela(x, y);
                int tx = posTela[0];
                int ty = posTela[1];

                // 1. Resolver camada superior de entidade ou terreno
                char glifo = '.';
                String corHex = visivel ? "#9E9E9E" : "#424242"; // Cinza claro vs Cinza escuro da memória
                String camada = "TERRAIN";

                TileTypeEnum tile = mapa.getTile(x, y);
                if (tile == TileTypeEnum.PAREDE_GRANITO) {
                    glifo = '#';
                    corHex = visivel ? "#BDBDBD" : "#303030";
                } else if (tile == TileTypeEnum.PORTA_FECHADA) {
                    glifo = '+';
                    corHex = visivel ? "#8D6E63" : "#3E2723";
                } else if (tile == TileTypeEnum.PORTA_ABERTA) {
                    glifo = '\'';
                    corHex = visivel ? "#A1887F" : "#4E342E";
                } else if (tile == TileTypeEnum.AGUA_RASA) {
                    glifo = '~';
                    corHex = visivel ? "#29B6F6" : "#01579B";
                } else if (tile == TileTypeEnum.GRAMA) {
                    glifo = '"';
                    corHex = visivel ? "#66BB6A" : "#1B5E20";
                }

                // 2. Se visível no FOV, sobrepõe superfícies elementais, itens e atores
                if (visivel) {
                    List<Entity> entidadesNoTile = mapa.getEntidadesNoTile(x, y);
                    for (Entity e : entidadesNoTile) {
                        // Superfícies reativas (fogo, fumaça)
                        if (e.hasComponent(SurfaceFieldComponent.class)) {
                            SurfaceFieldComponent surface = e.getComponent(SurfaceFieldComponent.class).get();
                            String tag = surface.getFieldTag();
                            if ("surface_fire".equalsIgnoreCase(tag)) {
                                glifo = '^';
                                corHex = "#FF5722"; // Laranja/Fogo
                                camada = "SURFACE";
                            } else if ("field_smoke".equalsIgnoreCase(tag)) {
                                glifo = '%';
                                corHex = "#9E9E9E"; // Fumaça
                                camada = "SURFACE";
                            } else if ("surface_electric".equalsIgnoreCase(tag)) {
                                glifo = 'z';
                                corHex = "#FFEB3B"; // Raio elétrico
                                camada = "SURFACE";
                            }
                        }

                        // Itens no chão
                        if (e.hasTag("item")) {
                            glifo = '?';
                            corHex = "#FFC107"; // Dourado
                            camada = "ITEM";
                        }

                        // Criaturas vivas e jogador
                        if (e.hasTag("monstro")) {
                            glifo = 'Z';
                            corHex = "#E53935"; // Vermelho
                            camada = "ACTOR";
                        } else if (e.hasTag("player")) {
                            glifo = '@';
                            corHex = "#00E676"; // Verde neon do jogador
                            camada = "ACTOR";
                        }
                    }
                }

                buffer.add(new RenderCellVO(x, y, tx, ty, glifo, corHex, camada, visivel, explorado));
            }
        }

        return buffer;
    }

    /**
     * Gera uma representação em texto ASCII para auditoria e testes unitários.
     */
    public String renderizarAsciiString() {
        if (mapa == null) return "";
        char[][] matriz = new char[mapa.getLargura()][mapa.getAltura()];
        for (char[] row : matriz) {
            Arrays.fill(row, ' ');
        }

        for (RenderCellVO cell : extrairQuadroRenderizacao()) {
            matriz[cell.mundoX()][cell.mundoY()] = cell.glifo();
        }

        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < mapa.getAltura(); y++) {
            for (int x = 0; x < mapa.getLargura(); x++) {
                sb.append(matriz[x][y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

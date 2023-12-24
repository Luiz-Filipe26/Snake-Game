
package com.mycompany.snakegame.core;

import java.util.HashMap;
import java.util.Map;

import com.mycompany.snakegame.controle.ApplicationController;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class DesenhoCampoJogo {
	private static DesenhoCampoJogo desenhoCampoJogo;
    private final ApplicationController applicationController;
    
    private final String CAMINHO_SNAKE_GRAPHICS = "/com/mycompany/snakegame/snake-graphics.png";
    
    // Direções possíveis da cobrinha
    public final Point2D DIREITA;
    public final Point2D ESQUERDA;
    public final Point2D CIMA;
    public final Point2D BAIXO;
    
    // Variáveis relacionadas às dimensões do jogo
    private double unidadeLargura;
    private double unidadeAltura;
    private double larguraJogo;
    private double alturaJogo;
    private double xMargem;
    private double yMargem;
    
    //Objetos para desenho
    private GraphicsContext gc;
    private Canvas canvasBuffer;
    private GraphicsContext gBuffer;
    
    //Imagem completa de sprites da cobrinha
    private final Image imagemCompleta;
    
    private Map<String, String> mapaCauda;
    private Map<Point2D, String> mapaCaudaPonta;
    private Map<Point2D, String> mapaCabeca;
    private Map<String, Image> mapaImagens;
    
    // Método para obter a instância única da classe
    public static synchronized DesenhoCampoJogo getInstancia() {
        return getInstancia(null);
    }
    
    public static synchronized DesenhoCampoJogo getInstancia(GraphicsContext graphicsContext) {
        if (desenhoCampoJogo == null) {
            desenhoCampoJogo = new DesenhoCampoJogo(graphicsContext);
        }
        return desenhoCampoJogo;
    }
    
    // Construtor privado para garantir apenas uma instância
    private DesenhoCampoJogo(GraphicsContext graphicsContext) {
        gc = graphicsContext;
        applicationController = ApplicationController.getInstancia();
        
        unidadeLargura = applicationController.getUnidadeLargura();
        unidadeAltura = applicationController.getUnidadeAltura();
        xMargem = applicationController.getXMargem();
        yMargem = applicationController.getYMargem();
        larguraJogo = applicationController.getCanvasLargura() - 2 * xMargem;
        alturaJogo = applicationController.getCanvasAltura() - 2 * yMargem;
        
        DIREITA = new Point2D(unidadeLargura, 0);
        ESQUERDA = new Point2D(-unidadeLargura, 0);
        CIMA = new Point2D(0, -unidadeAltura);
        BAIXO = new Point2D(0, unidadeAltura);
        
        imagemCompleta = new Image(getClass().getResourceAsStream(CAMINHO_SNAKE_GRAPHICS));
        
        inicializarMaps();
        inicializarDesenho();
    }
    
    // Método para inicializar os mapas que mapeiam relações de direção e partes da cobrinha
    private void inicializarMaps() {
    	String direita = DIREITA.toString();
    	String esquerda = ESQUERDA.toString();
    	String cima = CIMA.toString();
    	String baixo = BAIXO.toString();
    	
        mapaCauda = new HashMap<>(Map.of(
        		direita + direita, criarPonto(1, 0),
        		cima + cima, criarPonto(2, 1),
        		esquerda + cima, criarPonto(0, 1),
        		cima + direita, criarPonto(0, 0),
        		direita + baixo, criarPonto(2, 0),
        		baixo + esquerda, criarPonto(2, 2)
        		));
        
        mapaCaudaPonta = new HashMap<>(Map.of(
        		CIMA, criarPonto(3, 2),
        		DIREITA, criarPonto(4, 2),
        		ESQUERDA, criarPonto(3, 3),
        		BAIXO, criarPonto(4, 3)
        		));

        mapaCabeca = new HashMap<>(Map.of(
        		CIMA, criarPonto(3, 0),
        		DIREITA, criarPonto(4, 0),
    			ESQUERDA, criarPonto(3, 1),
    			BAIXO, criarPonto(4, 1)
        ));
        
        popularImagens();
    }
    
    private void popularImagens() {
        mapaImagens = new HashMap<>();
        // Itera sobre a imagem original para criar imagens seções de imagens redimensionadas e as armazena no mapa
        for (int x = 0; x < imagemCompleta.getWidth(); x += 64) {
            for (int y = 0; y < imagemCompleta.getHeight(); y += 64) {
            	int posImagemX = x/64;
            	int posImagemY = y/64;
            	String coordenada = criarPonto(posImagemX, posImagemY);
                if(posImagemX==1 && posImagemY==2) {
                    Image subImagem = cortarImagem(imagemCompleta, x, y, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, xMargem, yMargem);
                    mapaImagens.put(coordenada, imagemRedimensionada);
                }
                else if(posImagemX==0 && posImagemY==2) {
                    Image subImagem = cortarImagem(imagemCompleta, x, y+64, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, unidadeLargura*2, unidadeAltura*2);
                    mapaImagens.put(coordenada, imagemRedimensionada);
                }
                else {
                    Image subImagem = cortarImagem(imagemCompleta, x, y, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, unidadeLargura, unidadeAltura);
                    mapaImagens.put(coordenada, imagemRedimensionada);
                }
            }
        }
    }
    
    private String criarPonto(int x, int y) {
    	return "(" + x + ", " + y + ")";
    }
    
    private Image cortarImagem(Image imagemOriginal, int x, int y, int dx, int dy) {
        PixelReader pixelReader = imagemOriginal.getPixelReader();
        Image imagemCortada = new WritableImage(pixelReader, x, y, dx, dy);
        return imagemCortada;
    }
    
    private Image redimensionarImagem(Image imagemOriginal, double novaLargura, double novaAltura) {
        ImageView imageView = new ImageView(imagemOriginal);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(novaLargura);
        imageView.setFitHeight(novaAltura);
        
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        Image imagemRedimensionada = imageView.snapshot(sp, null);
        
        return imagemRedimensionada;
    }
    
    //Cria uma imagem com fundo limpo com o desenho da margem em volta
    private void inicializarDesenho() {
        double largura = 2*xMargem + larguraJogo;
        double altura = 2*yMargem + alturaJogo;
        //Cria o canvas que vai servir de buffer para fazer o pré-processamento do desenho
        canvasBuffer = new Canvas(larguraJogo, alturaJogo);
        gBuffer = canvasBuffer.getGraphicsContext2D();
        
        //Dois loops para desenhar a margem do jogo
        Image imagemFundo = mapaImagens.get(criarPonto(1, 2));
        for(int x=0; x<largura; x+=xMargem) {
            gc.drawImage(imagemFundo, x, 0);
            gc.drawImage(imagemFundo, x, altura-yMargem);
        }

        for(int y=0; y<altura; y+=yMargem) {
            gc.drawImage(imagemFundo, 0, y);
            gc.drawImage(imagemFundo, largura-xMargem, y);
        }

        gBuffer.setFill(Color.WHITE);
        gBuffer.fillRect(0, 0, larguraJogo, alturaJogo);
        gc.drawImage(canvasBuffer.snapshot(null, null), xMargem, yMargem);
    }
    
    //Limpa a tela ao criar um novo jogo
    public void novoJogo() {
        gBuffer.setFill(Color.WHITE);
        gBuffer.fillRect(0, 0, (unidadeLargura*larguraJogo), (unidadeAltura*alturaJogo));
        
        Platform.runLater(() -> {
            gc.drawImage(canvasBuffer.snapshot(null, null), xMargem, yMargem);
        });
    }
    
    //É chamado cada vez que é necessário atualizar o desenho do jogo
    public synchronized void desenharJogo(Cobrinha cobrinha, boolean crescendo, Maca maca) {   
        
        //Quando a cobrinha está andando sem crescer, é necessário limpar a antiga ponta da cauda dela da tela
        if (!crescendo) {
            desenhaPonto(cobrinha.getPontoRemovido());
        }
        
        if(maca.comeu()) {
            for(Point2D p : maca.limparMacaComida()) {
                desenhaPonto(p);
            }
        }
        
        if(maca.temMaca()) {
            if(!maca.isMacaGrande()) {
                desenhaImageNoPonto(mapaImagens.get(criarPonto(0, 3)), maca.getPosicaoMaca());
            }
            else {
                desenhaImageNoPonto(mapaImagens.get(criarPonto(0, 2)), maca.getPosicaoMaca());
            }
        }
        
        //Início - desenha a cobrinha
        desenhaImageNoPonto(getCabecaImagem(cobrinha.getCabecaDirecao()), cobrinha.getCabeca());

        desenhaPonto(cobrinha.getPrimeiraCauda());
        
        Point2D direcao1 = cobrinha.getPrimeiraCaudaDirecao();
        Point2D direcao2 = cobrinha.getCabecaDirecao();
        desenhaImageNoPonto(getCaudaImagem(direcao1, direcao2), cobrinha.getPrimeiraCauda());
        
        desenhaPonto(cobrinha.getCaudaPonta());
        desenhaImageNoPonto(getCaudaPontaImagem(cobrinha.getPenultimaCaudaDirecao()), cobrinha.getCaudaPonta());
        //Fim - desenha a cobrinha
        
        //desenhaImageNoPonto(mapaImagens.get(criarPonto(1, 4)), new Point2D(90, 150));
        //desenhaImageNoPonto(mapaImagens.get(criarPonto(1, 4)), new Point2D(360, 90));
        
        //Atualiza a tela com o desenho do buffer
        Platform.runLater(() -> {
            gc.drawImage(canvasBuffer.snapshot(null, null), xMargem, yMargem);
        });
    }
    
    
    private void desenhaImageNoPonto(Image im, Point2D p) {
        gBuffer.drawImage(im, p.getX(), p.getY());
    }
    
    private void desenhaPonto(Point2D p) {
        gBuffer.fillRect(p.getX(), p.getY(), unidadeLargura, unidadeAltura);
    }
    
    private Image getCabecaImagem(Point2D direcao) {        
        String lugarImagem = mapaCabeca.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaImagem(Point2D direcao1, Point2D direcao2) {
        String lugarImagem = mapaCauda.get(direcao1.toString() + direcao2.toString());
        
        boolean precisaInverter = (lugarImagem == null);
        if (precisaInverter) {
        	direcao1 = new Point2D(0, 0).subtract(direcao1);
        	direcao2 = new Point2D(0, 0).subtract(direcao2);
            lugarImagem = mapaCauda.get(direcao2.toString() + direcao1.toString());
        }

        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaPontaImagem(Point2D direcao) {
        String lugarImagem = mapaCaudaPonta.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
}

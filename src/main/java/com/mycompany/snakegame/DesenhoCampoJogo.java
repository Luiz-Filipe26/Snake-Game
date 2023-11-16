
package com.mycompany.snakegame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Luiz
 */
public class DesenhoCampoJogo {
    private static DesenhoCampoJogo desenhoCampoJogo;
    private SnakeController snakeController;
    
    
    private double unidadeLargura;
    private double unidadeAltura;
    private double larguraJogo;
    private double alturaJogo;
    private double xMargem;
    private double yMargem;
    
    public final Point2D DIREITA;
    public final Point2D ESQUERDA;
    public final Point2D CIMA;
    public final Point2D BAIXO;
    
    private GraphicsContext gc;
    private Canvas buffer;
    private GraphicsContext gBuffer;
    private Image imagemCompleta;
    
    private Map<List<Point2D>, Ponto> mapaCauda ;
    private Map<Point2D, Ponto> mapaCaudaPonta;
    private Map<Point2D, Ponto> mapaCabeca;
    private Map<Ponto, Image> mapaImagens;
    
    
    public static synchronized DesenhoCampoJogo getInstancia() {
        return getInstancia(null);
    }
    
    public static synchronized DesenhoCampoJogo getInstancia(GraphicsContext graphicsContext) {
        if (desenhoCampoJogo == null) {
            desenhoCampoJogo = new DesenhoCampoJogo(graphicsContext);
        }
        return desenhoCampoJogo;
    }
    
    public void setGraphicsContext(GraphicsContext graphicsContext) {
        gc = graphicsContext;
    }
    
    private DesenhoCampoJogo(GraphicsContext graphicsContext) {
        gc = graphicsContext;
        snakeController = SnakeController.getInstancia();
        
        unidadeLargura = snakeController.getUnidadeLargura();
        unidadeAltura = snakeController.getUnidadeAltura();
        xMargem = snakeController.getXMargem();
        yMargem = snakeController.getYMargem();
        larguraJogo = snakeController.getCanvasLargura() - 2 * xMargem;
        alturaJogo = snakeController.getCanvasAltura() - 2 * yMargem;
        
        DIREITA = new Point2D(unidadeLargura, 0);
        ESQUERDA = new Point2D(-unidadeLargura, 0);
        CIMA = new Point2D(0, -unidadeAltura);
        BAIXO = new Point2D(0, unidadeAltura);
        
        imagemCompleta = new Image(getClass().getResourceAsStream("snake-graphics.png"));
        
        inicializarMaps();
        inicializarDesenho();
    }
    
    public void inicializarMaps() {
        mapaCauda = new HashMap<>();
        Object[][] equivalenciasCauda = {
                {DIREITA, DIREITA, new Ponto(1, 0)},
                {CIMA, CIMA, new Ponto(2, 1)},
                {ESQUERDA, CIMA, new Ponto(0, 1)},
                {CIMA, DIREITA, new Ponto(0, 0)},
                {DIREITA, BAIXO, new Ponto(2, 0)},
                {BAIXO, ESQUERDA, new Ponto(2, 2)}
        };

        for (Object[] equivalencia : equivalenciasCauda) {
            List<Point2D> linhaPonto = new ArrayList<>();
            linhaPonto.add((Point2D) equivalencia[0]);
            linhaPonto.add((Point2D) equivalencia[1]);
            mapaCauda.put(linhaPonto, (Ponto) equivalencia[2]);
        }
        
        mapaCaudaPonta = new HashMap<>();
        mapaCaudaPonta.put(CIMA, new Ponto(3, 2));
        mapaCaudaPonta.put(DIREITA, new Ponto(4, 2));
        mapaCaudaPonta.put(ESQUERDA, new Ponto(3, 3));
        mapaCaudaPonta.put(BAIXO, new Ponto(4, 3));

        mapaCabeca = new HashMap<>();
        mapaCabeca.put(CIMA, new Ponto(3, 0));
        mapaCabeca.put(DIREITA, new Ponto(4, 0));
        mapaCabeca.put(ESQUERDA, new Ponto(3, 1));
        mapaCabeca.put(BAIXO, new Ponto(4, 1));

        mapaImagens = new HashMap<>();
        
        for (int x = 0; x < imagemCompleta.getWidth(); x += 64) {
            for (int y = 0; y < imagemCompleta.getHeight(); y += 64) {
                Ponto coordenadas = new Ponto(x/64, y/64);
                if(coordenadas.x==1 && coordenadas.y==2) {
                    Image subImagem = cortarImagem(imagemCompleta, x, y, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, xMargem, yMargem);
                    mapaImagens.put(coordenadas, imagemRedimensionada);
                }
                else if(coordenadas.x==0 && coordenadas.y==2) {
                    Image subImagem = cortarImagem(imagemCompleta, x, y+64, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, unidadeLargura*2, unidadeAltura*2);
                    mapaImagens.put(coordenadas, imagemRedimensionada);
                }
                else {
                    Image subImagem = cortarImagem(imagemCompleta, x, y, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, unidadeLargura, unidadeAltura);
                    mapaImagens.put(coordenadas, imagemRedimensionada);
                }
            }
        }
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
        
        Image imagemRedimensionada = imageView.snapshot(null, null);
        return imagemRedimensionada;
    }
    
    private void inicializarDesenho() {
        double largura = 2*xMargem + larguraJogo;
        double altura = 2*yMargem + alturaJogo;
        buffer = new Canvas(larguraJogo, alturaJogo);
        gBuffer = buffer.getGraphicsContext2D();
        
        
        Image imagemFundo = mapaImagens.get(new Ponto(1, 2));
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
        gc.drawImage(buffer.snapshot(null, null), xMargem, yMargem);
    }
    
    public void novoJogo() {
        gBuffer.setFill(Color.WHITE);
        gBuffer.fillRect(0, 0, (unidadeLargura*larguraJogo), (unidadeAltura*alturaJogo));
        
        Platform.runLater(() -> {
            gc.drawImage(buffer.snapshot(null, null), xMargem, yMargem);
        });
    }
    
    public synchronized void desenharJogo(Cobrinha cobrinha, boolean crescendo, List<Point2D> posicoesMacaComida, List<Point2D> posicoesMaca) {   
                
        if (!crescendo) {
            desenhaPonto(cobrinha.getPontoRemovido());
        }
        
        if(posicoesMacaComida != null) {
            for(Point2D p : posicoesMacaComida) {
                desenhaPonto(p);
            }
        }
        
        if(posicoesMaca != null) {
            if(posicoesMaca.size() == 1) {
                desenhaPontoImagem(mapaImagens.get(new Ponto(0, 3)), posicoesMaca.get(0));
            }
            else if(posicoesMaca.size() == 4) {
                desenhaPontoImagem(mapaImagens.get(new Ponto(0, 2)), posicoesMaca.get(0));
            }
        }

        desenhaPontoImagem(getCabecaImagem(cobrinha.getCabecaDirecao()), cobrinha.getCabeca());
        
        desenhaPonto(cobrinha.getPrimeiraCauda());
        desenhaPontoImagem(getCaudaImagem(
                new ArrayList(){{
                    add(cobrinha.getPrimeiraCaudaDirecao());
                    add(cobrinha.getCabecaDirecao());
                }}), cobrinha.getPrimeiraCauda());
        
        desenhaPonto(cobrinha.getCaudaPonta());
        desenhaPontoImagem(getCaudaPontaImagem(cobrinha.getPenultimaCaudaDirecao()), cobrinha.getCaudaPonta());
        
        Platform.runLater(() -> {
            gc.drawImage(buffer.snapshot(null, null), xMargem, yMargem);
        });
    }
    
    private void desenhaPontoImagem(Image im, Point2D p) {
        gBuffer.drawImage(im, p.getX(), p.getY());
    }
    
    private void desenhaPonto(Point2D p) {
        gBuffer.fillRect(p.getX(), p.getY(), unidadeLargura, unidadeAltura);
    }
    
    private Image getCabecaImagem(Point2D direcao) {        
        Ponto lugarImagem = mapaCabeca.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaImagem(List<Point2D> direcoes) {
        Ponto lugarImagem = mapaCauda.get(direcoes);
        if(lugarImagem == null) {
            lugarImagem = mapaCauda.get(new ArrayList() {{
                add(new Point2D(-direcoes.get(1).getX(), -direcoes.get(1).getY()));
                add(new Point2D(-direcoes.get(0).getX(), -direcoes.get(0).getY()));
            }});
        }
        
        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaPontaImagem(Point2D direcao) {
        Ponto lugarImagem = mapaCaudaPonta.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
}

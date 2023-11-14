
package com.mycompany.snakegame;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;

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
    
    private HashMap<ArrayList<Point2D>, Point> mapaCauda ;
    private HashMap<Point2D, Point> mapaCaudaPonta;
    private HashMap<Point2D, Point> mapaCabeca;
    private HashMap<Point, Image> mapaImagens;
    
    
    public static synchronized DesenhoCampoJogo getInstancia() {
        if (desenhoCampoJogo == null) {
            desenhoCampoJogo = new DesenhoCampoJogo();
        }
        return desenhoCampoJogo;
    }
    
    public void setGraphicsContext(GraphicsContext graphicsContext) {
        gc = graphicsContext;
        gc.setFill(Color.BLUE);
    }
    
    private DesenhoCampoJogo() {
        
        snakeController = SnakeController.getInstancia();
        
        unidadeLargura = snakeController.getUnidadeLargura();
        unidadeAltura = snakeController.getUnidadeAltura();
        xMargem = 20;
        yMargem = 20;
        larguraJogo = snakeController.getCanvasLargura() - (snakeController.getCanvasLargura() % (2 * xMargem));
        alturaJogo = snakeController.getCanvasAltura() - (snakeController.getCanvasAltura() % (2 * yMargem));
        
        DIREITA = new Point2D(unidadeLargura, 0);
        ESQUERDA = new Point2D(-unidadeLargura, 0);
        CIMA = new Point2D(0, -unidadeAltura);
        BAIXO = new Point2D(0, unidadeAltura);
        
        imagemCompleta = new Image(getClass().getResourceAsStream("snake-graphics.png"));
        buffer = new Canvas(larguraJogo, alturaJogo);
        
        inicializarMaps();
        inicializarDesenho();
    }
    
    public void inicializarMaps() {
        mapaCauda = new HashMap<>();
        Object[][] equivalenciasCauda = {
                {DIREITA, DIREITA, new Point(1, 0)},
                {CIMA, CIMA, new Point(2, 1)},
                {ESQUERDA, CIMA, new Point(0, 1)},
                {CIMA, DIREITA, new Point(0, 0)},
                {DIREITA, BAIXO, new Point(2, 0)},
                {BAIXO, ESQUERDA, new Point(2, 2)}
        };

        for (Object[] equivalencia : equivalenciasCauda) {
            ArrayList<Point2D> linhaPonto = new ArrayList<>();
            linhaPonto.add((Point2D) equivalencia[0]);
            linhaPonto.add((Point2D) equivalencia[1]);
            mapaCauda.put(linhaPonto, (Point) equivalencia[2]);
        }
        
        mapaCaudaPonta = new HashMap<>();
        mapaCaudaPonta.put(CIMA, new Point(3, 2));
        mapaCaudaPonta.put(DIREITA, new Point(4, 2));
        mapaCaudaPonta.put(ESQUERDA, new Point(3, 3));
        mapaCaudaPonta.put(BAIXO, new Point(4, 3));

        mapaCabeca = new HashMap<>();
        mapaCabeca.put(CIMA, new Point(3, 0));
        mapaCabeca.put(DIREITA, new Point(4, 0));
        mapaCabeca.put(ESQUERDA, new Point(3, 1));
        mapaCabeca.put(BAIXO, new Point(4, 1));

        mapaImagens = new HashMap<>();
        PixelReader pixelReader = imagemCompleta.getPixelReader();
        
        for (int x = 0; x < imagemCompleta.getWidth(); x += 64) {
            for (int y = 0; y < imagemCompleta.getHeight(); y += 64) {
                Point coordenadas = new Point(x/64, y/64);
                if(coordenadas.x==1 && coordenadas.y==2) {
                    WritableImage subImagem = new WritableImage(pixelReader, x, y, 64, 64);
                    Image imagemRedimensionada = new WritableImage(subImagem.getPixelReader(), (int) xMargem, (int) yMargem);
                    mapaImagens.put(coordenadas, imagemRedimensionada);
                }
                else if(coordenadas.x==0 && coordenadas.y==2) {
                    WritableImage subImagem = new WritableImage(pixelReader, x, y+64, 64, 64);
                    Image imagemRedimensionada = new WritableImage(subImagem.getPixelReader(), (int) unidadeLargura*2, (int) unidadeAltura*2);
                    mapaImagens.put(coordenadas, imagemRedimensionada);
                }
                else {
                    WritableImage subImagem = new WritableImage(pixelReader, x, y, 64, 64);
                    Image imagemRedimensionada = new WritableImage(subImagem.getPixelReader(), (int) unidadeLargura, (int) unidadeAltura);
                    mapaImagens.put(coordenadas, imagemRedimensionada);
                }
            }
        }
    }
    
     private void inicializarDesenho() {
        double largura = 2*xMargem + larguraJogo * unidadeLargura;
        double altura = 2*yMargem + alturaJogo * unidadeAltura;
        gBuffer = buffer.getGraphicsContext2D();
        
        
        Image imagemFundo = mapaImagens.get(new Point(1, 2));
        for(int x=0; x<largura; x+=xMargem) {
            gc.drawImage(imagemFundo, x, 0);
            gc.drawImage(imagemFundo, x, altura-yMargem);
        }

        for(int y=0; y<altura; y+=yMargem) {
            gc.drawImage(imagemFundo, 0, y);
            gc.drawImage(imagemFundo, largura-xMargem, y);
        }

        gBuffer.setFill(Color.WHITE);
        gBuffer.fillRect(0, 0, largura- 2*xMargem, altura- 2*yMargem);
        gc.drawImage(buffer.snapshot(null, null), xMargem, yMargem);
    }
    
    public void novoJogo() {
        gBuffer.setFill(Color.WHITE);
        gBuffer.fillRect(0, 0, (unidadeLargura*larguraJogo), (unidadeAltura*alturaJogo));
        gc.drawImage(buffer.snapshot(null, null), xMargem, yMargem);
    }
    
    public synchronized void desenharJogo(Cobrinha cobrinha, boolean crescendo, List<Point2D> posicoesMacaComida, List<Point2D> posicoesMaca) {   
        List<Point2D> corpoCobrinha = cobrinha.getCorpoCobrinha();
        Map<Point2D, Point2D> direcoesCobrinha = cobrinha.getDirecoesCobrinha();
        
        Point2D cabeca = corpoCobrinha.get(0);
        Point2D cauda = corpoCobrinha.get(1);
        Point2D caudaPosterior = corpoCobrinha.get(corpoCobrinha.size() - 2);
        Point2D caudaPonta = corpoCobrinha.get(corpoCobrinha.size() - 1);
        
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
                desenhaPontoImagem(mapaImagens.get(new Point(0, 3)), posicoesMaca.get(0));
            }
            else if(posicoesMaca.size() == 4) {
                desenhaPontoImagem(mapaImagens.get(new Point(0, 2)), posicoesMaca.get(0));
            }
        }

        desenhaPontoImagem(getCabecaImagem(direcoesCobrinha.get(cabeca)), cabeca);
        
        desenhaPonto(cauda);
        desenhaPontoImagem(getCaudaImagem(
                new ArrayList(){{
                    add(direcoesCobrinha.get(cauda));
                    add(direcoesCobrinha.get(cabeca));
                }}), cauda);
        
        desenhaPonto(caudaPonta);
        desenhaPontoImagem(getCaudaPontaImagem(direcoesCobrinha.get(caudaPosterior)), caudaPonta);
        
        gc.drawImage(buffer.snapshot(null, null), xMargem, yMargem);
    }
    
    private void desenhaPontoImagem(Image im, Point2D p) {
        gBuffer.drawImage(im, p.getX(), p.getY());
    }
    
    private void desenhaPonto(Point2D p) {
        gBuffer.fillRect(p.getX(), p.getY(), unidadeLargura, unidadeAltura);
    }
    
    private Image getCabecaImagem(Point2D direcao) {        
        Point lugarImagem = mapaCabeca.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaImagem(List<Point2D> direcoes) {
        Point lugarImagem = mapaCauda.get(direcoes);
        if(lugarImagem == null) {
            lugarImagem = mapaCauda.get(new ArrayList() {{
                add(new Point2D(-direcoes.get(1).getX(), -direcoes.get(1).getY()));
                add(new Point2D(-direcoes.get(0).getX(), -direcoes.get(0).getY()));
            }});
        }
        
        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaPontaImagem(Point2D direcao) {
        Point lugarImagem = mapaCaudaPonta.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
}

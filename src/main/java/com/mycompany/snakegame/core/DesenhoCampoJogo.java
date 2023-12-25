
package com.mycompany.snakegame.core;

import java.util.Map;

import com.mycompany.snakegame.controle.ApplicationController;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class DesenhoCampoJogo {
	private static DesenhoCampoJogo desenhoCampoJogo;
    
    // Variáveis relacionadas às dimensões do jogo
    private final double unidadeLargura;
    private final double unidadeAltura;
    private final double larguraJogo;
    private final double alturaJogo;
    private final double xMargem;
    private final double yMargem;
    
    //Objetos para desenho
    private GraphicsContext gc;
    private Canvas canvasBuffer;
    private GraphicsContext gBuffer;
    

    private final Map<String, Image> mapaImagens;
    private final Map<String, String> mapaCauda;
    private final Map<Point2D, String> mapaCaudaPonta;
    private final Map<Point2D, String> mapaCabeca;
    
    public static synchronized DesenhoCampoJogo getInstancia() {
        if (desenhoCampoJogo == null) {
            desenhoCampoJogo = new DesenhoCampoJogo();
        }
        return desenhoCampoJogo;
    }
    
    // Construtor privado para garantir apenas uma instância
	@SuppressWarnings("unchecked")
	private DesenhoCampoJogo() {
        
        CarregaValores cv = CarregaValores.getInstancia();
        
        unidadeLargura = cv.getUnidadeLargura();
        unidadeAltura = cv.getUnidadeAltura();
        xMargem = cv.getXMargem();
        yMargem = cv.getYMargem();
        larguraJogo = cv.getCanvasLargura() - 2 * xMargem;
        alturaJogo = cv.getCanvasAltura() - 2 * yMargem;
        
        mapaImagens = cv.getMapas()[0];
        mapaCauda = cv.getMapas()[1];
        mapaCaudaPonta = cv.getMapas()[2];
        mapaCabeca = cv.getMapas()[3];
    }
    

    private String criarPonto(int x, int y) {
    	return "(" + x + ", " + y + ")";
    }
    //Cria uma imagem com fundo limpo com o desenho da margem em volta
    public void inicializarDesenho(GraphicsContext gc) {
    	this.gc = gc;
    	
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

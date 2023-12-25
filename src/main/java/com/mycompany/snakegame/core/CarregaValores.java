package com.mycompany.snakegame.core;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class CarregaValores {
	private static CarregaValores carregaValores;
	
	private final String CAMINHO_SNAKE_GRAPHICS = "/com/mycompany/snakegame/snake-graphics.png";
    
    private final Image imagemCompleta;

    private Map<String, Image> mapaImagens;
    private Map<String, String> mapaCauda;
    private Map<Point2D, String> mapaCaudaPonta;
    private Map<Point2D, String> mapaCabeca;
    
    private final double UNIDADE_LARGURA = 30;
    private final double UNIDADE_ALTURA = 30;
    
    private final double X_MARGEM = 20;
    private final double Y_MARGEM = 20;
    
    private double canvasLargura = 0;
    private double canvasAltura = 0;
    
    public final Point2D DIREITA = new Point2D(UNIDADE_LARGURA, 0);
    public final Point2D ESQUERDA = new Point2D(-UNIDADE_LARGURA, 0);
    public final Point2D CIMA = new Point2D(0, -UNIDADE_ALTURA);
    public final Point2D BAIXO = new Point2D(0, UNIDADE_ALTURA);
	
	
	public static synchronized CarregaValores getInstancia() {
		if(carregaValores == null) {
			carregaValores = new CarregaValores();
		}
		return carregaValores;
	}
	
	private CarregaValores() {
		imagemCompleta = new Image(getClass().getResourceAsStream(CAMINHO_SNAKE_GRAPHICS));
		inicializarMaps();
        popularImagens();
	};

	public void setCanvasLarguraAltura(double canvasLargura, double canvasAltura) {
		this.canvasLargura = canvasLargura;
		this.canvasAltura = canvasAltura;
	}
	
	@SuppressWarnings("rawtypes")
	public Map[] getMapas() {
		return new Map[] { mapaImagens, mapaCauda, mapaCaudaPonta, mapaCabeca};
	}

	public double getUnidadeLargura() {
		return UNIDADE_LARGURA;
	}

	public double getUnidadeAltura() {
		return UNIDADE_ALTURA;
	}

	public double getXMargem() {
		return X_MARGEM;
	}

	public double getYMargem() {
		return Y_MARGEM;
	}
	
	public double getLarguraJogo() {
		return (canvasAltura - 2*Y_MARGEM) / UNIDADE_ALTURA;
	}
	
	public double getAlturaJogo() {
		return (canvasLargura - 2*X_MARGEM) / UNIDADE_LARGURA;
	}
	
	public double getCanvasLargura() {
		return canvasLargura;
	}

	public double getCanvasAltura() {
		return canvasAltura;
	}
	
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
                    Image imagemRedimensionada = redimensionarImagem(subImagem, X_MARGEM, Y_MARGEM);
                    mapaImagens.put(coordenada, imagemRedimensionada);
                }
                else if(posImagemX==0 && posImagemY==2) {
                    Image subImagem = cortarImagem(imagemCompleta, x, y+64, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, UNIDADE_LARGURA*2, UNIDADE_ALTURA*2);
                    mapaImagens.put(coordenada, imagemRedimensionada);
                }
                else {
                    Image subImagem = cortarImagem(imagemCompleta, x, y, 64, 64);
                    Image imagemRedimensionada = redimensionarImagem(subImagem, UNIDADE_LARGURA, UNIDADE_ALTURA);
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
}

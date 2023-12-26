package com.mycompany.snakegame.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.snakegame.controle.ApplicationController;
import com.mycompany.snakegame.entidades.Cobrinha;
import com.mycompany.snakegame.entidades.Maca;
import com.mycompany.snakegame.util.CarregaValores;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;

public class GerenciadorJogo extends Thread {

    private final ApplicationController applicationController = ApplicationController.getInstancia();
    private SnakeLogic snakeLogic;
    private final Object lock = new Object();
    private final Queue<Point2D> direcoesTeclado = new LinkedList<>();

    private final double UNIDADE_LARGURA = CarregaValores.getInstancia().getUnidadeLargura();
    private final double UNIDADE_ALTURA = CarregaValores.getInstancia().getUnidadeAltura();
    
    public final Point2D DIREITA  = new Point2D(UNIDADE_LARGURA, 0);
    public final Point2D ESQUERDA = new Point2D(-UNIDADE_LARGURA, 0);
    public final Point2D CIMA = new Point2D(0, -UNIDADE_ALTURA);
    public final Point2D BAIXO = new Point2D(0, UNIDADE_ALTURA);
    
    private final float VELOCIDADE_PADRAO = 5;
    private final float DIFICULDADE_PADRAO = 5;
    private final float NIVEIS_SUBIDOS_EM_UM_JOGO = 3;
    private final float DIFERENCA_VELOCIDADE_POR_DIFICULDADE = 0.5f;
    
    private float taxaAumento;
	private boolean atravessarBordas;
    private float dificuldade;
    
    private double areaJogo;
    
    private boolean jogoFechado;
    private boolean viewFechada;
    private long ultimoTempo = 0;
    
    public enum EstadosAceleracao {
        ACELERANDO, ACELERADO, DESACELERANDO, DESACELERADO;
    }

    public GerenciadorJogo() {
    	
    	CarregaValores cv = CarregaValores.getInstancia();
    	
        double alturaJogo = cv.getAlturaJogo();
        double larguraJogo = cv.getLarguraJogo();
        areaJogo = larguraJogo * alturaJogo;

        taxaAumento = (float) (DIFERENCA_VELOCIDADE_POR_DIFICULDADE * NIVEIS_SUBIDOS_EM_UM_JOGO / areaJogo);

        jogoFechado = true;
        viewFechada = false;
        
    }
    
    @Override
    public void run() {


        while (!viewFechada) {

            esperarAtualizacaoJogo();
            if(viewFechada) {
                break;
            }
            
            applicationController.atualizarStatus(0, 0);
            
            snakeLogic = inicializarGameLogic();
            gameLoop();
            
            
            applicationController.notificaNovoRecordeSeFor(snakeLogic.getPontos());
            if (snakeLogic.getCobrinhaTamanho() == areaJogo) {
                applicationController.venceuOJogo();
            }
        }
    }
    
    private SnakeLogic inicializarGameLogic() {
    	Cobrinha cobrinha = new Cobrinha();
        cobrinha.setPodeAtravessarBordas(atravessarBordas);
    	Maca maca = new Maca(cobrinha);
    	float velocidadeInicial = VELOCIDADE_PADRAO + DIFERENCA_VELOCIDADE_POR_DIFICULDADE * (dificuldade - DIFICULDADE_PADRAO);
    	 	
    	SnakeLogic snakeLogic = new SnakeLogic(cobrinha, velocidadeInicial, maca, taxaAumento, areaJogo);
    	return snakeLogic;
    }
    
    private void gameLoop() {

        long umSegundo = 1_000_000_000;
        
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                long tempoDecorrido = now - ultimoTempo;
                float tempoEsperar = umSegundo / snakeLogic.getVelocidade();

                if (tempoDecorrido < tempoEsperar) return;
                
                ultimoTempo = now;

                snakeLogic.setDirecaoAtual(obterDirecaoTeclado());
                boolean continuar = snakeLogic.executar();
                
                if (!jogoFechado && continuar) return;

            	ultimoTempo = 0;
            	stop();
            }
        }.start();
        
    }

    
    private Point2D obterDirecaoTeclado() {
    	
    	Point2D direcaoAtual = snakeLogic.getDirecaoAtual();
        if(direcaoAtual == null) {
            return DIREITA;
        }
        
        if (direcoesTeclado.isEmpty()) {
            return direcaoAtual;
        }
        Point2D direcaoTeclado = direcoesTeclado.poll();

        if (direcaoAtual.getX() == -direcaoTeclado.getX() && direcaoAtual.getY() == -direcaoTeclado.getY()) {
            return direcaoAtual;
        }
        
        return direcaoTeclado;
    }

    private void esperarAtualizacaoJogo() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(GerenciadorJogo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void keyPressed(String keyCode) {
        switch (keyCode) {
            case "UP", "W" -> direcoesTeclado.offer(CIMA);
            case "DOWN", "S" -> direcoesTeclado.offer(BAIXO);
            case "LEFT", "A" -> direcoesTeclado.offer(ESQUERDA);
            case "RIGHT", "D" -> direcoesTeclado.offer(DIREITA);
        }
        if (direcoesTeclado.size() == 3) {
            direcoesTeclado.poll();
        }
        if(keyCode.equals("SPACE")) {
            snakeLogic.espacoEvento(true);
        }
    }

    public void keyReleased(String keyCode) {
        if(keyCode.equals("SPACE")) {
        	snakeLogic.espacoEvento(false);
        }
    }

    public void novoJogo(float dificuldade, boolean atravessarBordas) {
        direcoesTeclado.clear();
        direcoesTeclado.offer(new Point2D(UNIDADE_LARGURA, 0));
        jogoFechado = false;
        this.dificuldade = dificuldade;
        this.atravessarBordas = atravessarBordas;
        synchronized (lock) {
            lock.notify();
        }
    }

    public void fecharJogo() {
        jogoFechado = true;
    }

    public void viewFechada() {
        jogoFechado = true;
        viewFechada = true;
        synchronized (lock) {
            lock.notify();
        }
    }
}

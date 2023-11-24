package com.mycompany.snakegame.core;

import com.mycompany.snakegame.util.ViewObserver;
import com.mycompany.snakegame.controle.SnakeController;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;

public class SnakeLogic extends Thread implements ViewObserver {

    private final SnakeController snakeController;
    private final Object lock = new Object();
    private final Queue<Point2D> direcoesTeclado = new LinkedList();
    private Cobrinha cobrinha;
    
    public final Point2D DIREITA;
    public final Point2D ESQUERDA;
    public final Point2D CIMA;
    public final Point2D BAIXO;
    
    private final float VELOCIDADE_PADRAO;
    private final float DIFICULDADE_PADRAO;
    private final float NIVEIS_SUBIDOS_EM_UM_JOGO;
    private final float DIFERENCA_VELOCIDADE_POR_DIFICULDADE;
    private float taxaAumento;
    private float dificuldade;
    
    private double unidadeLargura;
    private double unidadeAltura;
    private double larguraJogo;
    private double alturaJogo;
    private double areaJogo;
    private double xMargem;
    private double yMargem;
    
    private boolean atravessarBordas;
    private boolean jogoFechado;
    private boolean viewFechada;
    
    private final Random random = new Random();
    
    private float taxaAceleracao;
    
    private enum EstadosAceleracao {
        ACELERANDO, ACELERADO, DESACELERANDO, DESACELERADO;
    }
    private final EstadosAceleracao estadoAcelerando = EstadosAceleracao.ACELERANDO;
    private final EstadosAceleracao estadoAcelerado = EstadosAceleracao.ACELERADO;
    private final EstadosAceleracao estadoDesacelerando = EstadosAceleracao.DESACELERANDO;
    private final EstadosAceleracao estadoDesacelerado = EstadosAceleracao.DESACELERADO;
    private EstadosAceleracao estadoAceleracao;

    public SnakeLogic() {
        snakeController = SnakeController.getInstancia();
        unidadeLargura = 30;
        unidadeAltura = 30;
        xMargem = 20;
        yMargem = 20;
        alturaJogo = (snakeController.getCanvasAltura() - 2*xMargem) / unidadeAltura;
        larguraJogo = (snakeController.getCanvasLargura() - 2*yMargem) / unidadeLargura;
        
        jogoFechado = true;
        viewFechada = false;
        areaJogo = larguraJogo * alturaJogo;
        
        NIVEIS_SUBIDOS_EM_UM_JOGO = 3;
        DIFERENCA_VELOCIDADE_POR_DIFICULDADE = 0.5f;
        VELOCIDADE_PADRAO = 5;
        DIFICULDADE_PADRAO = 5;
        
        DIREITA = new Point2D(unidadeLargura, 0);
        ESQUERDA = new Point2D(-unidadeLargura, 0);
        CIMA = new Point2D(0, -unidadeAltura);
        BAIXO = new Point2D(0, unidadeAltura);
        
        taxaAceleracao = 1.3f;
    }
    
    public double getUnidadeLargura() {
        return unidadeLargura;
    }
    
    public double getUnidadeAltura() {
        return unidadeAltura;
    }

    public double getXMargem() {
        return xMargem;
    }

    public double getYMargem() {
        return yMargem;
    }

    @Override
    public void run() {
        
        taxaAumento = (float) (DIFERENCA_VELOCIDADE_POR_DIFICULDADE * NIVEIS_SUBIDOS_EM_UM_JOGO / areaJogo);
        int pontos;

        while (!viewFechada) {

            esperarAtualizacaoJogo();
            if(viewFechada) {
                break;
            }
            
            pontos = gameLoop();
            
            snakeController.notificaNovoRecordeSeFor(pontos);
            if (cobrinha.getTamanho() == areaJogo) {
                snakeController.venceuOJogo();
            }
        }
    }
    
    private int gameLoop() {
        estadoAceleracao = estadoDesacelerado;
        float velocidade = VELOCIDADE_PADRAO + DIFERENCA_VELOCIDADE_POR_DIFICULDADE * (dificuldade - DIFICULDADE_PADRAO);
        long tempoEspera;

        boolean macaGrande = false;
        int qtdCrescer = 0;
        int pontos = 0;


        cobrinha = new Cobrinha();
        cobrinha.setAtravessarBordas(atravessarBordas);
        Point2D direcaoAtual = DIREITA;

        Maca maca = new Maca(cobrinha, unidadeLargura, unidadeAltura, larguraJogo, alturaJogo);
        snakeController.atualizarNumMaca(0);

        while (!jogoFechado && cobrinha.getTamanho() < areaJogo) {
            direcaoAtual = obterDirecaoTeclado(direcaoAtual);

            if(!cobrinha.moverCobrinha(direcaoAtual)) {
                snakeController.perdeuOJogo();
                break;
            }

            if (maca.comeu()) {
                pontos += macaGrande ? 4 : 1;
                qtdCrescer += macaGrande ? 4 : 1;
                velocidade += taxaAumento;
                snakeController.atualizarNumMaca(pontos);
                
                if(maca.podeGerarMacaGrande()) {
                    macaGrande = random.nextInt(5) == 0;
                }
                else {
                    macaGrande = false;
                }
                maca.gerarPosicaoMaca(macaGrande);
            }

            if (qtdCrescer > 0) {
                cobrinha.crescerCobrinha();
            }
            
            snakeController.desenharJogo(cobrinha, qtdCrescer > 0, maca);
            
            if(qtdCrescer > 0) {
                qtdCrescer--;
            }
            
            if(estadoAceleracao == estadoAcelerando) {
                velocidade *= taxaAceleracao;
                estadoAceleracao = estadoAcelerado;
            }
            else if(estadoAceleracao == estadoDesacelerando) {
                velocidade /= taxaAceleracao;
                estadoAceleracao = estadoDesacelerado;
            }
            
            tempoEspera = (long) (1000 / velocidade);
            esperar(tempoEspera);
        }
        
        return pontos;
    }
    
    private Point2D obterDirecaoTeclado(Point2D direcaoAtual) {
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
    
    private void esperar(long tempoEspera) {
        try {
            Thread.sleep(tempoEspera);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SnakeLogic.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private void esperarAtualizacaoJogo() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(SnakeLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void keyPressed(String keyCode) {
        switch (keyCode) {
            case "UP", "W" -> direcoesTeclado.offer(CIMA);
            case "DOWN", "S" -> direcoesTeclado.offer(BAIXO);
            case "LEFT", "A" -> direcoesTeclado.offer(ESQUERDA);
            case "RIGHT", "D" -> direcoesTeclado.offer(DIREITA);
        }
        if(keyCode.equals("SPACE") && estadoAceleracao == estadoDesacelerado) {
            estadoAceleracao = estadoAcelerando;
        }
        if (direcoesTeclado.size() == 3) {
            direcoesTeclado.poll();
        }
    }

    @Override
    public void keyReleased(String keyCode) {
        if(keyCode.equals("SPACE") && estadoAceleracao == estadoAcelerado) {
            estadoAceleracao = estadoDesacelerando;
        }
    }

    @Override
    public void novoJogo(float dificuldade, boolean atravessarBordas) {
        direcoesTeclado.clear();
        direcoesTeclado.offer(new Point2D(unidadeLargura, 0));
        jogoFechado = false;
        this.dificuldade = dificuldade;
        this.atravessarBordas = atravessarBordas;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void fecharJogo() {
        jogoFechado = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void viewFechada() {
        jogoFechado = true;
        viewFechada = true;
        synchronized (lock) {
            lock.notify();
        }
    }
}

package com.mycompany.snakegame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

public class SnakeLogic extends Thread implements ViewObserver {

    private final SnakeController snakeController;
    private final Object lock;
    private final Queue<Point2D> direcoesTeclado = new LinkedList();
    private Cobrinha cobrinha;
    
    private double unidadeLargura;
    private double unidadeAltura;
    private double larguraJogo;
    private double alturaJogo;
    private double areaJogo;
    private double xMargem;
    private double yMargem;
    
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
    
    private boolean atravessarBordas;
    private boolean jogoFechado;
    private boolean novoJogo;
    private boolean viewFechada;
    
    private final Random random = new Random();

    public SnakeLogic() {
        lock = new Object();
        snakeController = SnakeController.getInstancia();
        unidadeLargura = 30;
        unidadeAltura = 30;
        xMargem = 20;
        yMargem = 20;
        alturaJogo = (snakeController.getCanvasAltura() - 2*xMargem) / unidadeAltura;
        larguraJogo = (snakeController.getCanvasLargura() - 2*yMargem) / unidadeLargura;
        
        jogoFechado = true;
        novoJogo = false;
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
            
            if (cobrinha.getTamanho() == areaJogo) {
                snakeController.venceuOJogo();
            }
            
            snakeController.notificaNovoRecordeSeFor(pontos);
        }
    }
    
    private int gameLoop() {
        float velocidade = VELOCIDADE_PADRAO + DIFERENCA_VELOCIDADE_POR_DIFICULDADE * (dificuldade - DIFICULDADE_PADRAO);
        long tempoEspera = (long) (1000 / velocidade);

        boolean macaGrande = false;
        int qtdCrescer = 0;
        int pontos = 0;


        cobrinha = new Cobrinha();
        cobrinha.setAtravessarBordas(atravessarBordas);
        Point2D direcaoAtual = DIREITA;
        List<Point2D> posicoesMacaComida;

        List<Point2D> posicoesMaca = gerarPosicaoMaca(false);
        snakeController.atualizarNumMaca(0);

        while (!jogoFechado && cobrinha.getTamanho() < areaJogo) {
            direcaoAtual = obterDirecaoTeclado(direcaoAtual);

            if(!cobrinha.moverCobrinha(direcaoAtual)) {
                snakeController.perdeuOJogo();
                break;
            }
            

            if (posicoesMaca.contains(cobrinha.getCabeca())) {
                pontos += macaGrande ? 4 : 1;
                qtdCrescer += macaGrande ? 4 : 1;
                snakeController.atualizarNumMaca(pontos);
                posicoesMacaComida = posicoesMaca;
                
                macaGrande = random.nextInt(5) == 0;
                posicoesMaca = gerarPosicaoMaca(macaGrande);
            }
            else {
                posicoesMacaComida = null;
            }

            if (qtdCrescer > 0) {
                cobrinha.crescerCobrinha();
            }
            
            snakeController.desenharJogo(cobrinha, qtdCrescer > 0, posicoesMacaComida, posicoesMaca);
            
            if(qtdCrescer > 0) {
                qtdCrescer--;
            }
            
            if(posicoesMacaComida != null) {
                velocidade += taxaAumento;
                tempoEspera = (long) (1000 / velocidade);
            }
            
            esperar(tempoEspera);
        }
        
        return pontos;
    }
    
    private void esperar(long tempoEspera) {
        try {
            Thread.sleep(tempoEspera);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(SnakeLogic.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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

    private void esperarAtualizacaoJogo() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(SnakeLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        novoJogo = false;
    }

    private List<Point2D> gerarPosicaoMaca(boolean macaGrande) {
        double x, y;
        Point2D posMaca;
        List<Point2D> posicoesMaca;

        do {
            x = unidadeLargura * random.nextInt((int) larguraJogo - (macaGrande ? 1 : 0));
            y = unidadeAltura * random.nextInt((int) alturaJogo - (macaGrande ? 1 : 0));
            posMaca = new Point2D(x, y);
            
            posicoesMaca = new ArrayList();
            posicoesMaca.add(posMaca);
            if (macaGrande) {
                posicoesMaca.add(new Point2D(posMaca.getX() + unidadeLargura, posMaca.getY()));
                posicoesMaca.add(new Point2D(posMaca.getX(), posMaca.getY() + unidadeAltura));
                posicoesMaca.add(new Point2D(posMaca.getX() + unidadeLargura, posMaca.getY() + unidadeAltura));
            }
        } while (!isPosicaoMacaValida(posicoesMaca));

        return posicoesMaca;
    }

    private boolean isPosicaoMacaValida(List<Point2D> posicoesMaca) {
        for (Point2D pm : posicoesMaca) {
            if (cobrinha.checaColisaoPonto(pm)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void keyPressed(KeyCode keyCode) {
        switch (keyCode) {
            case UP, W -> direcoesTeclado.offer(CIMA);
            case DOWN, S -> direcoesTeclado.offer(BAIXO);
            case LEFT, A -> direcoesTeclado.offer(ESQUERDA);
            case RIGHT, D -> direcoesTeclado.offer(DIREITA);
        }
        if (direcoesTeclado.size() == 3) {
            direcoesTeclado.poll();
        }
    }

    @Override
    public void novoJogo(float dificuldade, boolean atravessarBordas) {
        direcoesTeclado.clear();
        direcoesTeclado.offer(new Point2D(unidadeLargura, 0));
        jogoFechado = false;
        this.dificuldade = dificuldade;
        this.atravessarBordas = atravessarBordas;
        novoJogo = true;
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

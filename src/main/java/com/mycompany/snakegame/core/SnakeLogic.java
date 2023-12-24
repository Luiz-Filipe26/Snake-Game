package com.mycompany.snakegame.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.mycompany.snakegame.controle.ApplicationController;
import com.mycompany.snakegame.core.GerenciadorJogo.EstadosAceleracao;

import javafx.geometry.Point2D;

public class SnakeLogic {
	ApplicationController applicationController = ApplicationController.getInstancia();

	private Cobrinha cobrinha;
    private Maca maca;
	
	private Random random = new Random();
	
	private EstadosAceleracao estadoAceleracao;
    private final EstadosAceleracao estadoAcelerando = EstadosAceleracao.ACELERANDO;
    private final EstadosAceleracao estadoAcelerado = EstadosAceleracao.ACELERADO;
    private final EstadosAceleracao estadoDesacelerando = EstadosAceleracao.DESACELERANDO;
    private final EstadosAceleracao estadoDesacelerado = EstadosAceleracao.DESACELERADO;
	
	
    private final int UNIDADE_PONTUACAO = 20;
    private final float TEMPO_PARA_PONTUACAO_COMPLETA = 16.0f;
    private final float TAXA_ACELERACAO = 1.3f;

    private double areaJogo;
	private float taxaAumento;
	private float velocidade;

	private int pontos = 0;
	private int macasComidas = 0;
	private int deltaTempoUltimaMaca = -1;
	
	private Point2D direcaoAtual;
	private Queue<Point2D> pontosCrescer = new LinkedList<>();
	private boolean crescerCobrinha = false;
    
    
    public SnakeLogic(Cobrinha cobrinha, float velocidadeInicial, Maca maca, float taxaAumento, double areaJogo) {
    	this.cobrinha = cobrinha;
    	this.velocidade = velocidadeInicial;
    	this.maca = maca;
    	this.taxaAumento = taxaAumento;
    	this.areaJogo = areaJogo;
    	
    	estadoAceleracao = estadoDesacelerado;
    }
    
    public void setDirecaoAtual(Point2D direcaoAtual) {
    	this.direcaoAtual = direcaoAtual;
    }
    
    public Point2D getDirecaoAtual() {
    	return direcaoAtual;
    }
    
    public float getVelocidade() {
    	return velocidade;
    }
    
    public int getPontos() {
    	return pontos;
    }
    
    public int getCobrinhaTamanho() {
    	return cobrinha.getTamanho();
    }
    
    public void espacoEvento(boolean espacoPressionado) {
    	if(espacoPressionado && estadoAceleracao == estadoDesacelerado) {
            estadoAceleracao = estadoAcelerando;
        }
    	else if(!espacoPressionado && estadoAceleracao == estadoAcelerado) {
            estadoAceleracao = estadoDesacelerando;
    	}
    }
    
    public boolean executar() {

        boolean moveu = cobrinha.moverCobrinha(direcaoAtual);
        if(!moveu) {
            applicationController.perdeuOJogo();
            return false;
        }
        
        crescerCobrinha = cobrinha.getPontoRemovido().equals(pontosCrescer.peek());
        
    	if(crescerCobrinha) {
    		pontosCrescer.poll();
    		cobrinha.crescerCobrinha();
    	}
    	
    	deltaTempoUltimaMaca++;
        if (maca.comeu()) {

        	macasComidas++;
            velocidade += taxaAumento;
        	
            float proporcaoTempoRestante = ((TEMPO_PARA_PONTUACAO_COMPLETA - deltaTempoUltimaMaca)
										/ TEMPO_PARA_PONTUACAO_COMPLETA);
            
			int pontosGanhar = (int) (UNIDADE_PONTUACAO * maca.getTamanho() * proporcaoTempoRestante);

            deltaTempoUltimaMaca = 0;
        	
            pontos += Math.max(pontosGanhar, 1);
            
            applicationController.atualizarStatus(macasComidas, pontos);
            
        	
            pontosCrescer.addAll(Collections.nCopies(maca.getTamanho(), cobrinha.getCabeca()));
            
            boolean macaGrande = maca.podeGerarMacaGrande() && (macasComidas % 3 == 0 && random.nextInt(2) == 0);
            
            maca.gerarPosicaoMaca(macaGrande);
        }
        
        applicationController.desenharJogo(cobrinha, crescerCobrinha, maca);
        
        if(estadoAceleracao == estadoAcelerando) {
            velocidade *= TAXA_ACELERACAO;
            estadoAceleracao = estadoAcelerado;
        }
        else if(estadoAceleracao == estadoDesacelerando) {
            velocidade /= TAXA_ACELERACAO;
            estadoAceleracao = estadoDesacelerado;
        }
    	
    	if(cobrinha.getTamanho() >= areaJogo) {
    		return false;
    	}
        
        return true;
    }
}

package com.mycompany.snakegame.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mycompany.snakegame.util.CarregaValores;
import com.mycompany.snakegame.util.ChecadorColisao;

import javafx.geometry.Point2D;

public class Maca {
    private List<Point2D> posicoesMaca;
    private List<Point2D> posicoesMacaComida;
    
    private final Random random = new Random();
    private final Cobrinha cobrinha;
    private final double unidadeLargura;
    private final double unidadeAltura;
    private final double larguraJogo;
    private final double alturaJogo;
    
    public Maca(Cobrinha cobrinha) {
    	CarregaValores cv = CarregaValores.getInstancia();
        this.unidadeLargura = cv.getUnidadeLargura();
        this.unidadeAltura = cv.getUnidadeAltura();
        this.larguraJogo = cv.getLarguraJogo();
        this.alturaJogo = cv.getAlturaJogo();
        this.cobrinha = cobrinha;
        gerarPosicaoMaca(false);
    }
    
    // Limpa a maçã após ser comida
    public List<Point2D> limparMacaComida() {
    	if(posicoesMacaComida != null) {
            List<Point2D> pmc = posicoesMacaComida;
            posicoesMacaComida = null;
            return pmc;
        }
        return null;
    }
    
    // Obtém a posição atual da maçã
    public List<Point2D> getPosicoesMaca() {
        return posicoesMaca;
    }
        
    // Obtém a posição principal da maçã
    public Point2D getPosicaoMaca() {
        return posicoesMaca.get(0);
    }
    
    public boolean comeu() {
        if(posicoesMacaComida != null) {
            return true;
        }
        if(ChecadorColisao.checar(posicoesMaca, cobrinha.getCabeca())) {
            posicoesMacaComida = posicoesMaca;
            return true;
        }
        return false;
    }
    
    public boolean isMacaGrande() {
        return posicoesMaca.size() == 4;
    }
    
    public int getTamanho() {
        return posicoesMaca.size();
    }
    
    public boolean temMaca() {
        return posicoesMaca != null;
    }
    
    //Checa se há espaço na área de jogo para gerar uma maçã grande
    public boolean podeGerarMacaGrande() {
        //Testa se a cobrinha é pequena o suficiente para certamente poder
        if(cobrinha.getTamanho() < (larguraJogo/2) * (alturaJogo - 1) - 1) {
            return true;
        }
        
        //Array que indica com true as posições equivalentes à cobrinha
        boolean[][] campoJogo = new boolean[(int) alturaJogo][(int) larguraJogo];
        for(Point2D p : cobrinha.getCorpoCobrinha()) {
            campoJogo[(int) (p.getY()/unidadeAltura)][(int) (p.getX()/unidadeLargura)] = true;
        }
        
        //Testa se há um quadrado vazio de tamanho 2
        for(int i=0; i<campoJogo.length-1; i++) {
            for(int j=0; j<campoJogo[0].length-1; j++) {
                //Quando for false nas quatro posições
                if(!campoJogo[i][j] && !campoJogo[i][j + 1] && !campoJogo[i + 1][j] && !campoJogo[i + 1][j + 1]) {
                    return true;
                }
                //Quando for verdadeiro em uma posição da lateral direita, pular duas posições de uma vez
                if(campoJogo[i][j + 1] || campoJogo[i + 1][j + 1]) {
                    j++;
                }
            }
        }
        
        //Caso o loop anterior não tenha retornado verdadeiro, retorna falso
        return false;
    }
    
    // Gera uma nova posição para a maçã
    public void gerarPosicaoMaca(boolean macaGrande) {
        double x, y;
        Point2D posMaca;
        List<Point2D> posicoesMacaAux;

        do {
            // Gera coordenadas aleatórias para a maçã
            x = unidadeLargura * random.nextInt((int) larguraJogo - (macaGrande ? 1 : 0));
            y = unidadeAltura * random.nextInt((int) alturaJogo - (macaGrande ? 1 : 0));
            
            posMaca = new Point2D(x, y);
            
            // Cria a lista de posições da maçã
            posicoesMacaAux = new ArrayList<>(List.of(posMaca));
            if (macaGrande) {
            	posicoesMacaAux.addAll(List.of(
        			posMaca.add(unidadeLargura, 0),
        			posMaca.add(0, unidadeAltura),
        			posMaca.add(unidadeLargura, unidadeAltura)));
            }
        } while (isPosicaoMacaInvalida(posicoesMacaAux));

        posicoesMaca = posicoesMacaAux;
    }
    
    // Verifica se a posição gerada para a maçã é válida
    private boolean isPosicaoMacaInvalida(List<Point2D> posicoesMacaAux) {
        return ChecadorColisao.checar(cobrinha.getCorpoCobrinha(), posicoesMacaAux);
    }
}

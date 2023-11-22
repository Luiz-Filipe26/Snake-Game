package com.mycompany.snakegame.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    
    public Maca(Cobrinha cobrinha, double unidadeLargura, double unidadeAltura, double larguraJogo, double alturaJogo) {
        this.unidadeLargura = unidadeLargura;
        this.unidadeAltura = unidadeAltura;
        this.larguraJogo = larguraJogo;
        this.alturaJogo = alturaJogo;
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
        if(posicoesMaca.contains(cobrinha.getCabeca())) {
            posicoesMacaComida = posicoesMaca;
            return true;
        }
        return false;
    }
    
    public boolean isMacaGrande() {
        return posicoesMaca.size() == 4;
    }
    
    public boolean temMaca() {
        return posicoesMaca != null;
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
            posicoesMacaAux = new ArrayList();
            posicoesMacaAux.add(posMaca);
            if (macaGrande) {
                posicoesMacaAux.add(new Point2D(posMaca.getX() + unidadeLargura, posMaca.getY()));
                posicoesMacaAux.add(new Point2D(posMaca.getX(), posMaca.getY() + unidadeAltura));
                posicoesMacaAux.add(new Point2D(posMaca.getX() + unidadeLargura, posMaca.getY() + unidadeAltura));
            }
        } while (!isPosicaoMacaValida(posicoesMacaAux));

        posicoesMaca = posicoesMacaAux;
    }
    
    // Verifica se a posição gerada para a maçã é válida
    private boolean isPosicaoMacaValida(List<Point2D> posicoesMacaAux) {
        for (Point2D pmx : posicoesMacaAux) {
            if (cobrinha.checaColisaoPonto(pmx)) {
                return false;
            }
        }
        return true;
    }
}

package com.mycompany.snakegame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;

public class Cobrinha {
    private List<Point2D> corpoCobrinha;
    private Map<Point2D, Point2D> direcoesCobrinha;
    
    private final double unidadeLargura;
    private final double unidadeAltura;
    private final double larguraJogo;
    private final double alturaJogo;
    
    public final Point2D DIREITA;
    public final Point2D ESQUERDA;
    public final Point2D CIMA;
    public final Point2D BAIXO;
    
    private Point2D pontoRemovido;
    private Point2D direcaoPontoRemovido;
    
    private boolean atravessarBordas;
    
    public Cobrinha() {
        direcoesCobrinha = new HashMap();
        
        SnakeController snakeController = SnakeController.getInstancia();
        
        unidadeLargura = snakeController.getUnidadeLargura();
        unidadeAltura = snakeController.getUnidadeAltura();
        larguraJogo = snakeController.getCanvasLargura() / unidadeLargura;
        alturaJogo = snakeController.getCanvasLargura() / unidadeAltura;
        
        DIREITA = new Point2D(unidadeLargura, 0);
        ESQUERDA = new Point2D(-unidadeLargura, 0);
        CIMA = new Point2D(0, -unidadeAltura);
        BAIXO = new Point2D(0, unidadeAltura);
        
        criarCobrinhaInicial();
    }
    
    public void setAtravessarBordas(boolean atravessarBordas) {
        this.atravessarBordas = atravessarBordas;
    }
    
    public List<Point2D> getCorpoCobrinha() {
        return corpoCobrinha;
    }
    
    public Point2D getCabeca() {
        return corpoCobrinha.get(0);
    }
    
    public Map<Point2D, Point2D> getDirecoesCobrinha() {
        return direcoesCobrinha;
    }
    
    public Point2D getPontoRemovido() {
        return pontoRemovido;
    }
    
    public int getTamanho() {
        return corpoCobrinha.size();
    }
    
    public boolean checaColisaoPonto(Point2D ponto) {
        return corpoCobrinha.contains(ponto);
    }
    
    public boolean moverCobrinha(Point2D direcao) {
        Point2D novaCabeca = new Point2D(corpoCobrinha.get(0).getX() + direcao.getX(), corpoCobrinha.get(0).getY() + direcao.getY());
        if (corpoCobrinha.contains(novaCabeca)) {
            return false;
        } else if (atravessouBorda(novaCabeca)) {
            if (!atravessarBordas) {
                return false;
            }
            novaCabeca = novaDirecaoAlemDaBorda(novaCabeca);
        }
        corpoCobrinha.add(0, novaCabeca);
        direcoesCobrinha.put(novaCabeca, direcao);
        
        pontoRemovido = corpoCobrinha.remove(corpoCobrinha.size()-1);
        direcaoPontoRemovido = direcoesCobrinha.remove(pontoRemovido);
        
        return true;
    }
    
    public void crescerCobrinha() {
        if(pontoRemovido != null) {
            corpoCobrinha.add(pontoRemovido);
            direcoesCobrinha.put(pontoRemovido, direcaoPontoRemovido);
            pontoRemovido = null;
            direcaoPontoRemovido = null;
        }
    }
    
    private boolean atravessouBorda(Point2D p) {
        return p.getX() < 0 || p.getX() > unidadeLargura * (larguraJogo - 1) || p.getY() < 0 || p.getY() > unidadeAltura * (alturaJogo - 1);
    }
    
    private Point2D novaDirecaoAlemDaBorda(Point2D p) {
        double x = (p.getX() + unidadeLargura * larguraJogo) % (unidadeLargura * larguraJogo);
        double y = (p.getY() + unidadeAltura * alturaJogo) % (unidadeAltura * alturaJogo);
        p = new Point2D(x, y);
        return p;
    }
    
    private void criarCobrinhaInicial() {
        corpoCobrinha = new ArrayList();
        corpoCobrinha.add(new Point2D(unidadeLargura * larguraJogo / 2, unidadeAltura * alturaJogo / 2));
        corpoCobrinha.add(new Point2D(corpoCobrinha.get(0).getX() - unidadeLargura, corpoCobrinha.get(0).getY()));
        corpoCobrinha.add(new Point2D(corpoCobrinha.get(1).getX() - unidadeLargura, corpoCobrinha.get(0).getY()));
        
        direcoesCobrinha = new HashMap();
        direcoesCobrinha.put(corpoCobrinha.get(0), DIREITA);
        direcoesCobrinha.put(corpoCobrinha.get(1), DIREITA);
        direcoesCobrinha.put(corpoCobrinha.get(2), DIREITA);
    }
}

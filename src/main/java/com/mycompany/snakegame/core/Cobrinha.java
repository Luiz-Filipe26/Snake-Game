
package com.mycompany.snakegame.core;

import com.mycompany.snakegame.controle.SnakeController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;

public class Cobrinha {
    private List<Point2D> corpoCobrinha;
    private Map<Point2D, Point2D> direcoesCobrinha;
    
    public final Point2D DIREITA;
    public final Point2D ESQUERDA;
    public final Point2D CIMA;
    public final Point2D BAIXO;
    
    private final double unidadeLargura;
    private final double unidadeAltura;
    private final double larguraJogo;
    private final double alturaJogo;
    
    // Armazena o ponto removido durante o movimento da cobrinha
    private Point2D pontoRemovido;
    
    // Armazena a direção do ponto removido
    private Point2D direcaoPontoRemovido;
    
    // Indica se a cobrinha pode atravessar as bordas do jogo
    private boolean atravessarBordas;
    
    public Cobrinha() {
        direcoesCobrinha = new HashMap();
        
        SnakeController snakeController = SnakeController.getInstancia();
        
        unidadeLargura = snakeController.getUnidadeLargura();
        unidadeAltura = snakeController.getUnidadeAltura();
        larguraJogo = (snakeController.getCanvasLargura() - 2 * snakeController.getXMargem()) / unidadeLargura;
        alturaJogo = (snakeController.getCanvasAltura() - 2 * snakeController.getYMargem() ) / unidadeAltura;
        
        DIREITA = new Point2D(unidadeLargura, 0);
        ESQUERDA = new Point2D(-unidadeLargura, 0);
        CIMA = new Point2D(0, -unidadeAltura);
        BAIXO = new Point2D(0, unidadeAltura);
        
        criarCobrinhaInicial();
    }
    
    // Inicializa a cobrinha com três partes no meio do campo
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
    
    // Define se a cobrinha pode atravessar as bordas do campo
    public void setAtravessarBordas(boolean atravessarBordas) {
        this.atravessarBordas = atravessarBordas;
    }
    
    public boolean checaColisaoPonto(Point2D ponto) {
        return corpoCobrinha.contains(ponto);
    }
    
    // Faz a cobrinha crescer, adicionando a parte removida durante o movimento
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
    
    // Calcula a nova posição da cobrinha quando ela atravessa as bordas do campo
    private Point2D novaDirecaoAlemDaBorda(Point2D p) {
        double x = (p.getX() + unidadeLargura * larguraJogo) % (unidadeLargura * larguraJogo);
        double y = (p.getY() + unidadeAltura * alturaJogo) % (unidadeAltura * alturaJogo);
        return new Point2D(x, y);
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc="getters da cobrinha">
    public List<Point2D> getCorpoCobrinha() {
        return corpoCobrinha;
    }
    
    public Map<Point2D, Point2D> getDirecoesCobrinha() {
        return direcoesCobrinha;
    }
    
    public Point2D getCabeca() {
        return corpoCobrinha.get(0);
    }
    
    public Point2D getPrimeiraCauda() {
        return corpoCobrinha.get(1);
    }
    
    public Point2D getPenultimaCauda() {
        return corpoCobrinha.get(corpoCobrinha.size()-2);
    }
    
    public Point2D getCaudaPonta() {
        return corpoCobrinha.get(corpoCobrinha.size()-1);
    }
    
    public Point2D getCabecaDirecao() {
        return direcoesCobrinha.get(corpoCobrinha.get(0));
    }
    
    public Point2D getPrimeiraCaudaDirecao() {
        return direcoesCobrinha.get(corpoCobrinha.get(1));
    }
    
    public Point2D getPenultimaCaudaDirecao() {
        return direcoesCobrinha.get(corpoCobrinha.get(corpoCobrinha.size()-2));
    }
    
    public Point2D getCaudaPontaDirecao() {
        return direcoesCobrinha.get(corpoCobrinha.get(corpoCobrinha.size()-1));
    }
    
    public Point2D getDirecao(Point2D ponto) {
        return direcoesCobrinha.get(ponto);
    }
    
    public Point2D getPontoRemovido() {
        return pontoRemovido;
    }
    
    public int getTamanho() {
        return corpoCobrinha.size();
    }
    // </editor-fold>

}

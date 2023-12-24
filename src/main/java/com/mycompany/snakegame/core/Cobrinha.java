
package com.mycompany.snakegame.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.snakegame.controle.ApplicationController;

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
    
    private boolean podeAtravessarBordas;
    
    public Cobrinha() {
        direcoesCobrinha = new HashMap<>();
        
        ApplicationController applicationController = ApplicationController.getInstancia();
        
        unidadeLargura = applicationController.getUnidadeLargura();
        unidadeAltura = applicationController.getUnidadeAltura();
        larguraJogo = (applicationController.getCanvasLargura() - 2 * applicationController.getXMargem());
        alturaJogo = (applicationController.getCanvasAltura() - 2 * applicationController.getYMargem());
        
        DIREITA = new Point2D(unidadeLargura, 0);
        ESQUERDA = new Point2D(-unidadeLargura, 0);
        CIMA = new Point2D(0, -unidadeAltura);
        BAIXO = new Point2D(0, unidadeAltura);
        
        criarCobrinhaInicial();
    }
    
    // Inicializa a cobrinha com três partes no meio do campo
    private void criarCobrinhaInicial() {
        Point2D cabeca = new Point2D(larguraJogo / 2, alturaJogo / 2);
        Point2D cauda1 = cabeca.subtract(unidadeLargura, 0);
        Point2D cauda2 = cauda1.subtract(unidadeLargura, 0);

        corpoCobrinha = new ArrayList<>(List.of(cabeca, cauda1, cauda2));

        direcoesCobrinha = new HashMap<>();
        corpoCobrinha.forEach(parteCorpo -> direcoesCobrinha.put(parteCorpo, DIREITA));
    }
    
    public void setPodeAtravessarBordas(boolean podeAtravessarBordas) {
        this.podeAtravessarBordas = podeAtravessarBordas;
    }
    
    // Faz a cobrinha crescer, adicionando a parte removida durante o movimento
    public boolean moverCobrinha(Point2D direcao) {
        Point2D novaCabeca = getCabeca().add(direcao.getX(), direcao.getY());
        
        if (ChecadorColisao.checar(corpoCobrinha, novaCabeca)) {
            return false;
        } else if (atravessouBorda(novaCabeca)) {
            if (!podeAtravessarBordas) {
                return false;
            }
            novaCabeca = novoPontoAlemDaBorda(novaCabeca);
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
        return ChecadorColisao.checarMargem(p);
    }
    
    private Point2D novoPontoAlemDaBorda(Point2D p) {
        double x = (p.getX() + larguraJogo) % larguraJogo;
        double y = (p.getY() + alturaJogo) % alturaJogo;
        return new Point2D(x, y);
    }
    
    
    //getters da cobrinha
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

}

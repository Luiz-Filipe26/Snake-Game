
package com.mycompany.snakegame.controle;

import com.mycompany.snakegame.core.Cobrinha;
import com.mycompany.snakegame.core.DesenhoCampoJogo;
import com.mycompany.snakegame.banco.GerenciadorRecordes;
import com.mycompany.snakegame.core.Maca;
import com.mycompany.snakegame.core.SnakeLogic;
import com.mycompany.snakegame.util.ViewObserver;
import com.mycompany.snakegame.view.SnakeFXMLController;
import java.util.ArrayList;
import java.util.List;

public class SnakeController {
    
    private static SnakeController snakeController;
    private SnakeFXMLController snakeFxmlController;
    private DesenhoCampoJogo desenhoCampoJogo;
    private SnakeLogic snakeLogic;
    
    private final List<ViewObserver> observers;
    
    
    public static SnakeController getInstancia() {
        if(snakeController == null) {
            snakeController = new SnakeController();
        }
        return snakeController;
    }
    
    private SnakeController() {
        observers = new ArrayList<>();
    }
    
    public void adicionarSnakeFXMLController(SnakeFXMLController snakeFxmlController) {
        this.snakeFxmlController = snakeFxmlController;
    }
    
    public void adicionarDesenhoCampoJogo(DesenhoCampoJogo desenhoCampoJogo) {
        this.desenhoCampoJogo = desenhoCampoJogo;
    }
    
    public void adicionarObserver(ViewObserver observer) {
        observers.add(observer);
        if(observer instanceof SnakeLogic) {
            snakeLogic = (SnakeLogic) observer;
        }
    }

    public void removerObserver(ViewObserver observer) {
        observers.remove(observer);
        if(observer instanceof SnakeLogic) {
            snakeLogic = null;
        }
    }
    
    public double getCanvasLargura() {
        return snakeFxmlController.getCanvasLargura();
    }
    
    public double getCanvasAltura() {
        return snakeFxmlController.getCanvasAltura();
    }
    
    public double getUnidadeLargura() {
        return snakeLogic.getUnidadeLargura();
    }
    
    public double getUnidadeAltura() {
        return snakeLogic.getUnidadeAltura();
    }
    
    public double getXMargem() {
        return snakeLogic.getXMargem();
    }
    
    public double getYMargem() {
        return snakeLogic.getYMargem();
    }
    
    public void keyPressed(String keyCode) {
        for(ViewObserver o: observers) {
            o.keyPressed(keyCode);
        }
    }
    
    public void keyReleased(String keyCode) {
        for(ViewObserver o: observers) {
            o.keyReleased(keyCode);
        }
    }

    public void novoJogo(int dificuldade, boolean atravessarBordas) {
        for(ViewObserver o: observers) {
            o.novoJogo(dificuldade, atravessarBordas);
        }
    }

    public void fecharJogo() {
        for(ViewObserver o: observers) {
            o.fecharJogo();
        }
    }

    public void viewFechada() {
        for(ViewObserver o: observers) {
            o.viewFechada();
        }
    }

    public void novoJogoCampo() {
        desenhoCampoJogo.novoJogo();
    }

    public void desenharJogo(Cobrinha cobrinha, boolean crescendo, Maca maca) {
        desenhoCampoJogo.desenharJogo(cobrinha, crescendo, maca);
    }

    public void venceuOJogo() {
        snakeFxmlController.venceuOJogo();
    }

    public void atualizarNumMaca(int comidas) {
        snakeFxmlController.atualizarNumMaca(comidas);
    }

    public void perdeuOJogo() {
        snakeFxmlController.perdeuOJogo();
    }

    public void inserirRecorde(String nome, int pontos) {
        GerenciadorRecordes.getInstancia().inserirRecorde(nome, pontos);
    }

    public void notificaNovoRecordeSeFor(int pontos) {
        if (GerenciadorRecordes.getInstancia().isNovoRecorde(pontos)) {
            snakeFxmlController.notificaNovoRecorde(pontos);
        }
    }
}

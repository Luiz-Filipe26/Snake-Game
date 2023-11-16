/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.snakegame;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

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
    
    public void adicionarDesenhoCampoJogo(DesenhoCampoJogo desenhoCampoJogo) {
        this.desenhoCampoJogo = desenhoCampoJogo;
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
    
    public void keyPressed(KeyCode keyCode) {
        for(ViewObserver o: observers) {
            o.keyPressed(keyCode);
        }
    }

    public void fecharJogo() {
        for(ViewObserver o: observers) {
            o.fecharJogo();
        }
    }

    public void novoJogoCampo() {
        desenhoCampoJogo.novoJogo();
    }

    public void novoJogo(int dificuldade, boolean atravessarBordas) {
        for(ViewObserver o: observers) {
            o.novoJogo(dificuldade, atravessarBordas);
        }
    }

    public void inserirRecorde(String nome, int pontos) {
        GerenciadorRecordes.getInstancia().inserirRecorde(nome, pontos);
    }

    public void venceuOJogo() {
        snakeFxmlController.venceuOJogo();
    }

    public void notificaNovoRecordeSeFor(int pontos) {
        if (GerenciadorRecordes.getInstancia().isNovoRecorde(pontos)) {
            snakeFxmlController.notificaNovoRecorde(pontos);
        }
    }

    public void atualizarNumMaca(int comidas) {
        snakeFxmlController.atualizarNumMaca(comidas);
    }

    public void perdeuOJogo() {
        snakeFxmlController.perdeuOJogo();
    }

    public void desenharJogo(Cobrinha cobrinha, boolean crescendo, List<Point2D> posicoesMacaComida, List<Point2D> posicoesMaca) {
        desenhoCampoJogo.desenharJogo(cobrinha, crescendo, posicoesMacaComida, posicoesMaca);
    }

    public void viewFechada() {
        for(ViewObserver o: observers) {
            o.viewFechada();
        }
    }
}

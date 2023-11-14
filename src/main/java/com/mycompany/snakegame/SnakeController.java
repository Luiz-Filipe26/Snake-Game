/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.snakegame;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

public class SnakeController {
    
    private static SnakeController snakeController;
    private static FXMLController fxmlController;
    private static DesenhoCampoJogo desenhoCampoJogo;
    
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
    
    public void adicionarFXMLController(FXMLController fxmlController) {
        this.fxmlController = fxmlController;
    }
    
    public void adicionarObserver(ViewObserver observer) {
        observers.add(observer);
    }
    
    public double getCanvasLargura() {
        return fxmlController.getCanvasLargura();
    }
    
    public double getCanvasAltura() {
        return fxmlController.getCanvasAltura();
    }
    
    public double getUnidadeLargura() {
        return ((SnakeLogic) observers.get(0)).getUnidadeLargura();
    }
    
    public double getUnidadeAltura() {
        return ((SnakeLogic) observers.get(0)).getUnidadeAltura();
    }

    public void removerObserver(ViewObserver observer) {
        observers.remove(observer);
    }
    
    public void keyPressed(int keyCode) {
        for(ViewObserver o: observers) {
            o.keyPressed(keyCode);
        }
    }
    

    void fecharJogo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void novoJogoCampo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void novoJogo(int dificuldade, boolean selected) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void inserirRecorde(String text, int pontos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void venceuOJogo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void notificaNovoRecordeSeFor(int pontos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void atualizarNumMaca(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void perdeuOJogo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void desenharJogo(Cobrinha cobrinha, boolean b, List<Point2D> posicoesMacaComida, List<Point2D> posicoesMaca) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

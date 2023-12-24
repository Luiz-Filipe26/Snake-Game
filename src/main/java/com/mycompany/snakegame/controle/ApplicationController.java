
package com.mycompany.snakegame.controle;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.snakegame.banco.GerenciadorRecordes;
import com.mycompany.snakegame.core.Cobrinha;
import com.mycompany.snakegame.core.DesenhoCampoJogo;
import com.mycompany.snakegame.core.Maca;
import com.mycompany.snakegame.core.GerenciadorJogo;
import com.mycompany.snakegame.view.SnakeFXMLController;

public class ApplicationController {
    
    private static ApplicationController applicationController;
    private SnakeFXMLController snakeFxmlController;
    private GerenciadorJogo gerenciadorJogo;
    private DesenhoCampoJogo desenhoCampoJogo;
    
    
    public static ApplicationController getInstancia() {
        if(applicationController == null) {
            applicationController = new ApplicationController();
        }
        return applicationController;
    }
    
    private ApplicationController() {}
    
    public void adicionarSnakeFXMLController(SnakeFXMLController snakeFxmlController) {
        this.snakeFxmlController = snakeFxmlController;
    }
    
    public void adicionarDesenhoCampoJogo(DesenhoCampoJogo desenhoCampoJogo) {
        this.desenhoCampoJogo = desenhoCampoJogo;
    }
    
    public void setSnakeLogic(GerenciadorJogo gerenciadorJogo) {
        this.gerenciadorJogo = gerenciadorJogo;
    }
    
    public double getCanvasLargura() {
        return snakeFxmlController.getCanvasLargura();
    }
    
    public double getCanvasAltura() {
        return snakeFxmlController.getCanvasAltura();
    }
    
    public double getUnidadeLargura() {
        return gerenciadorJogo.getUnidadeLargura();
    }
    
    public double getUnidadeAltura() {
        return gerenciadorJogo.getUnidadeAltura();
    }
    
    public double getXMargem() {
        return gerenciadorJogo.getXMargem();
    }
    
    public double getYMargem() {
        return gerenciadorJogo.getYMargem();
    }
    
    public void keyPressed(String keyCode) {
        gerenciadorJogo.keyPressed(keyCode);
    }
    
    public void keyReleased(String keyCode) {
    	gerenciadorJogo.keyReleased(keyCode);
    }

    public void novoJogo(int dificuldade, boolean atravessarBordas) {
    	gerenciadorJogo.novoJogo(dificuldade, atravessarBordas);
    }

    public void fecharJogo() {
    	gerenciadorJogo.fecharJogo();
    }

    public void viewFechada() {
    	gerenciadorJogo.viewFechada();
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

    public void atualizarStatus(int macasComidas, int pontos) {
        snakeFxmlController.atualizarStatus(macasComidas, pontos);
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


package com.mycompany.snakegame.controle;

import com.mycompany.snakegame.banco.GerenciadorRecordes;
import com.mycompany.snakegame.core.DesenhoCampoJogo;
import com.mycompany.snakegame.core.GerenciadorJogo;
import com.mycompany.snakegame.entidades.Cobrinha;
import com.mycompany.snakegame.entidades.Maca;

import fxmlController.SnakeClassicoView;

public class ApplicationController {
    
    private static ApplicationController applicationController;
    private SnakeClassicoView snakeFxmlController;
    private GerenciadorJogo gerenciadorJogo;
    private DesenhoCampoJogo desenhoCampoJogo;
    
    
    public static ApplicationController getInstancia() {
        if(applicationController == null) {
            applicationController = new ApplicationController();
        }
        return applicationController;
    }
    
    private ApplicationController() {}
    
    public void setSnakeFXMLController(SnakeClassicoView snakeFxmlController) {
        this.snakeFxmlController = snakeFxmlController;
    }
    
    public void setDesenhoCampoJogo(DesenhoCampoJogo desenhoCampoJogo) {
        this.desenhoCampoJogo = desenhoCampoJogo;
    }
    
    public void setGerenciadorJogo(GerenciadorJogo gerenciadorJogo) {
        this.gerenciadorJogo = gerenciadorJogo;
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

    public void jogoClassicoFechado() {
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

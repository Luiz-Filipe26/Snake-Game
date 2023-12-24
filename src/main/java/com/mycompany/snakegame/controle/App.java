package com.mycompany.snakegame.controle;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.mycompany.snakegame.core.ChecadorColisao;
import com.mycompany.snakegame.core.DesenhoCampoJogo;
import com.mycompany.snakegame.core.GerenciadorJogo;
import com.mycompany.snakegame.view.SnakeFXMLController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Classe principal que inicia a aplicação JavaFX Snake Game.
 */
public class App extends Application {

    private ApplicationController applicationController = ApplicationController.getInstancia();
    private HashSet<KeyCode> teclasValidas;
    
    /**
     * Método principal que inicia a aplicação JavaFX.
     * @param stage O palco principal da aplicação.
     * @throws IOException Exceção de entrada/saída, caso ocorra um problema ao carregar o arquivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mycompany/snakegame/SnakeView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Snake Game");
        
        inicializarController(root, fxmlLoader.getController());
        ChecadorColisao.inicializarValores();
        criarListeners(scene, stage);
        
        stage.show();
    }
    

    // Configura o controlador da cobra (ApplicationController) e adiciona ouvintes
    private void inicializarController(Parent root, SnakeFXMLController snakeFXMLControlelr) {
    	applicationController.adicionarSnakeFXMLController(snakeFXMLControlelr);
        GerenciadorJogo gerenciadorJogo = new GerenciadorJogo();
        applicationController.setSnakeLogic(gerenciadorJogo);
        GraphicsContext gc = ((Canvas) root.lookup("#canvasGrafico")).getGraphicsContext2D();
        DesenhoCampoJogo desenhoCampoJogo = DesenhoCampoJogo.getInstancia(gc);
        applicationController.adicionarDesenhoCampoJogo(desenhoCampoJogo);
        gerenciadorJogo.start();
    }
    
    private void criarListeners(Scene scene, Stage stage) {
        teclasValidas = new HashSet<>(Set.of(
                KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT,
                KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D, KeyCode.SPACE));
        
        // Ouvinte para eventos de tecla pressionada
        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if(teclasValidas.contains(keyCode)) {
                applicationController.keyPressed(keyCode.toString());
            }
        });
        
        // Ouvinte para eventos de tecla despressionada
        scene.setOnKeyReleased(event -> {
            KeyCode keyCode = event.getCode();
            if(teclasValidas.contains(keyCode)) {
                applicationController.keyReleased(keyCode.toString());
            }
        });
        
        // Ouvinte para tela fechada
        stage.setOnCloseRequest(event -> {
            applicationController.viewFechada();
            Platform.exit();
        });
    }
}
 
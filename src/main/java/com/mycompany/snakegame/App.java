package com.mycompany.snakegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

/**
 * JavaFX App
 */
public class App extends Application {

    private HashSet<KeyCode> direcoesValidas;
    private SnakeController snakeController;
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SnakeView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Snake Game");
        stage.show();
        
        snakeController = SnakeController.getInstancia();
        criarListeners(scene, stage);
        
        snakeController.adicionarSnakeFXMLController(fxmlLoader.getController());
        SnakeLogic snakeLogic = new SnakeLogic();
        snakeController.adicionarObserver(snakeLogic);
        GraphicsContext gc = ((Canvas) root.lookup("#canvasGrafico")).getGraphicsContext2D();
        DesenhoCampoJogo desenhoCampoJogo = DesenhoCampoJogo.getInstancia(gc);
        snakeController.adicionarDesenhoCampoJogo(desenhoCampoJogo);
        snakeLogic.start();
    }
    
    public void criarListeners(Scene scene, Stage stage) {
        direcoesValidas = new HashSet(){{
            add(KeyCode.UP); add(KeyCode.DOWN); add(KeyCode.LEFT); add(KeyCode.RIGHT);
            add(KeyCode.W); add(KeyCode.S); add(KeyCode.A); add(KeyCode.D);
        }};
        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if(direcoesValidas.contains(keyCode)) {
                snakeController.keyPressed(keyCode);
            }
        });
        
        stage.setOnCloseRequest(event -> {
            snakeController.viewFechada();
            Platform.exit();
        });
    }
}
 
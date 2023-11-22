package com.mycompany.snakegame.controle;

import com.mycompany.snakegame.core.DesenhoCampoJogo;
import com.mycompany.snakegame.core.SnakeLogic;
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
 * Classe principal que inicia a aplicação JavaFX Snake Game.
 */
public class App extends Application {

    private HashSet<KeyCode> teclasValidas;
    private SnakeController snakeController;
    
    /**
     * Método principal que inicia a aplicação JavaFX.
     * @param stage O palco principal da aplicação.
     * @throws IOException Exceção de entrada/saída, caso ocorra um problema ao carregar o arquivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o arquivo FXML e configura a cena
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mycompany/snakegame/SnakeView.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Snake Game");
        
        // Configura o controlador da cobra (SnakeController) e adiciona ouvintes
        snakeController = SnakeController.getInstancia();
        criarListeners(scene, stage);
        
        // Adiciona o controlador FXML da cobra e configurações iniciais
        snakeController.adicionarSnakeFXMLController(fxmlLoader.getController());
        SnakeLogic snakeLogic = new SnakeLogic();
        snakeController.adicionarObserver(snakeLogic);
        GraphicsContext gc = ((Canvas) root.lookup("#canvasGrafico")).getGraphicsContext2D();
        DesenhoCampoJogo desenhoCampoJogo = DesenhoCampoJogo.getInstancia(gc);
        snakeController.adicionarDesenhoCampoJogo(desenhoCampoJogo);
        snakeLogic.start();
        
        // Exibe o palco
        stage.show();
    }
    
    /**
     * Cria ouvintes para eventos de tecla pressionada e fechamento da janela.
     * @param scene A cena JavaFX.
     * @param stage O palco JavaFX.
     */
    public void criarListeners(Scene scene, Stage stage) {
        // Configuração das teclas válidas para controle da cobra
        teclasValidas = new HashSet(){{
            add(KeyCode.UP); add(KeyCode.DOWN); add(KeyCode.LEFT); add(KeyCode.RIGHT);
            add(KeyCode.W); add(KeyCode.S); add(KeyCode.A); add(KeyCode.D);
            add(KeyCode.SPACE);
        }};
        
        // Ouvinte para eventos de tecla pressionada
        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if(teclasValidas.contains(keyCode)) {
                snakeController.keyPressed(keyCode.toString());
            }
        });
        
        // Ouvinte para eventos de tecla despressionada
        scene.setOnKeyReleased(event -> {
            KeyCode keyCode = event.getCode();
            if(teclasValidas.contains(keyCode)) {
                snakeController.keyReleased(keyCode.toString());
            }
        });
        
        // Ouvinte para eventos de tecla pressionada
        stage.setOnCloseRequest(event -> {
            snakeController.viewFechada();
            Platform.exit();
        });
    }
}
 
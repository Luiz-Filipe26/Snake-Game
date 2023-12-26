package com.mycompany.snakegame.controle;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mycompany.snakegame.core.DesenhoCampoJogo;
import com.mycompany.snakegame.core.GerenciadorJogo;
import com.mycompany.snakegame.util.ChecadorColisao;

import fxmlController.SnakeClassicoView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Classe principal que inicia a aplicação JavaFX Snake Game.
 */
public class App extends Application {

    private ApplicationController applicationController = ApplicationController.getInstancia();
    private HashSet<KeyCode> teclasValidas;
    private Stage stage;
    
    String CAMINHO_VIEWS = "/com/mycompany/snakegame/";
    private String viewAtual = "MainMenu.fxml";
    
    private Map<String, String> buttonViewLink = new HashMap<>(Map.of(
    		"#buttonModoClassico", "SnakeClassico.fxml",
    		"#buttonVoltar", "MainMenu.fxml",
    		"#buttonMostrarRecordes", "Recordes.fxml",
    		"#buttonVoltarJogoNiveis", "MainMenu.fxml",
    		"#buttonModoNiveis", "SnakeNiveis.fxml",
    		"#buttonVoltarJogoClassico", "SnakeClassico.fxml"));
    
    private Map<String, String[]> buttonsDaView = new HashMap<>(Map.of(
    		"MainMenu.fxml", new String[]{"#buttonModoClassico", "#buttonModoNiveis"},
    		"SnakeClassico.fxml", new String[]{"#buttonVoltar", "#buttonMostrarRecordes"},
    		"Recordes.fxml", new String[] {"#buttonVoltarJogoClassico"},
    		"SnakeNiveis.fxml", new String[] {"#buttonVoltarJogoNiveis"}));
    
    /**
     * Método principal que inicia a aplicação JavaFX.
     * @param stage O palco principal da aplicação.
     * @throws IOException Exceção de entrada/saída, caso ocorra um problema ao carregar o arquivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CAMINHO_VIEWS + viewAtual));;
        Parent root = fxmlLoader.load();

        stage.sceneProperty().addListener((o, os, ns) -> gerenciarTrocaTelas());
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Snake Game");
        
        stage.show();
    }
    
    private void gerenciarTrocaTelas() {
    	String[] buttonsId = buttonsDaView.get(viewAtual);
    	
    	for(String buttonId : buttonsId) {
    		Button button = (Button) stage.getScene().lookup(buttonId);

        	button.setOnAction(e -> botaoTrocarTela(buttonId));
    	}
    }
    
    private void botaoTrocarTela(String buttonId) {
    	if(viewAtual.equals("SnakeClassico.fxml")) {
    		applicationController.jogoClassicoFechado();
    	}
    	
    	viewAtual = buttonViewLink.get(buttonId);
    	
    	Parent root = null;
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(CAMINHO_VIEWS + viewAtual));
    	try {
			root = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

    	Scene scene = new Scene(root);
        stage.setScene(scene);
        if(viewAtual.equals("SnakeClassico.fxml")) {
        	inicializarController(root, fxmlLoader.getController());
            ChecadorColisao.inicializarValores();
            criarListenersJogoClassico(scene, stage);
        }
    }
    

    // Configura o controlador da cobra (ApplicationController) e adiciona ouvintes
    private void inicializarController(Parent root, SnakeClassicoView snakeClassicoView) {
    	applicationController.setSnakeFXMLController(snakeClassicoView);
    	
        GerenciadorJogo gerenciadorJogo = new GerenciadorJogo();
        applicationController.setGerenciadorJogo(gerenciadorJogo);
        GraphicsContext gc = ((Canvas) root.lookup("#canvasGrafico")).getGraphicsContext2D();
        DesenhoCampoJogo desenhoCampoJogo = DesenhoCampoJogo.getInstancia();
        desenhoCampoJogo.inicializarDesenho(gc);
        applicationController.setDesenhoCampoJogo(desenhoCampoJogo);
        gerenciadorJogo.start();
    }
    
    private void criarListenersJogoClassico(Scene scene, Stage stage) {
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
            applicationController.jogoClassicoFechado();
            Platform.exit();
        });
    }
}
 
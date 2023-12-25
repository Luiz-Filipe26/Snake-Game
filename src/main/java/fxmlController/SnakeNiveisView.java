package fxmlController;

import java.net.URL;
import java.util.ResourceBundle;

import com.mycompany.snakegame.controle.ApplicationController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SnakeNiveisView implements Initializable{

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button buttonMostrarRecordes;
    @FXML
    private Button buttonNovoJogo;
    @FXML
    private Button buttonSalvarRecorde;
    @FXML
    private Button buttonVoltarJogoNiveis;
    @FXML
    private Canvas canvasGrafico;
    @FXML
    private Label labelMensagem;
    @FXML
    private TextField textFieldNome;
    
    private ApplicationController applicationController;
    private int pontos;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        applicationController = ApplicationController.getInstancia();
        anchorPane.requestFocus();
        buttonSalvarRecorde.setVisible(false);
    }

    @FXML
    public void salvarRecorde(ActionEvent event) {
        applicationController.inserirRecorde(textFieldNome.getText(), pontos);
        buttonSalvarRecorde.setVisible(false);
        pontos = 0;
        anchorPane.requestFocus();
    }

    @FXML
    public void novoJogoPressionado(ActionEvent event) {
        pontos = 0;

        labelMensagem.setText("");
        buttonNovoJogo.setVisible(false);
        buttonSalvarRecorde.setVisible(false);
        
        anchorPane.requestFocus();
        
        applicationController.novoJogoCampo();

        
        //-----------------------------------
        //      olhar aqui					|
        //-----------------------------------
        applicationController.novoJogo(0, true);
    }
    
    
    @FXML
    public void focarCanvas(MouseEvent event) {
        anchorPane.requestFocus();
    }
    
    public double getCanvasLargura() {
        return canvasGrafico.getWidth();
    }
    
    public double getCanvasAltura() {
        return canvasGrafico.getHeight();
    }
    
    public void notificaNovoRecorde(int pontos) {
        this.pontos = pontos;
        Platform.runLater(() -> {
    		labelMensagem.setText("Novo recorde de " + pontos + " pontos!");
            buttonSalvarRecorde.setVisible(true);
        });
    }
    
    public void perdeuOJogo() {
        applicationController.fecharJogo();
        Platform.runLater(() -> {
            buttonNovoJogo.setVisible(true);
            labelMensagem.setText("Você perdeu! " + labelMensagem.getText());
        });
    }
    
    public void venceuOJogo() {
        applicationController.fecharJogo();
        Platform.runLater(() -> {
        	if(labelMensagem.getText().contains("recorde")) {
        		labelMensagem.setText(labelMensagem.getText() + " Você venceu!");        		
        	}
        	else {
                labelMensagem.setText("Você venceu!");
        	}
            buttonNovoJogo.setVisible(true);
        });
    }
    
    public void atualizarStatus(int macasComidas, int pontos) {
        Platform.runLater(() -> {
            labelMensagem.setText("Maçãs comidas: " + macasComidas + " Pontos: " + pontos);
        });
    }

}

package fxmlController;

import java.net.URL;
import java.util.ResourceBundle;

import com.mycompany.snakegame.controle.ApplicationController;
import com.mycompany.snakegame.util.CarregaValores;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SnakeClassicoView implements Initializable{

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button buttonFecharJogo;
    @FXML
    private Button buttonMostrarRecordes;
    @FXML
    private Button buttonNovoJogo;
    @FXML
    private Button buttonSalvarRecorde;
    @FXML
    private Button buttonVoltar;
    @FXML
    private Canvas canvasGrafico;
    @FXML
    private CheckBox checkBoxAtravessarBordas;
    @FXML
    private Label labelDificuldade;
    @FXML
    private Label labelMensagem;
    @FXML
    private Slider sliderDificuldade;
    @FXML
    private TextField textFieldNome;
    
    private ApplicationController applicationController;
    private int dificuldade;
    private int pontos;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	CarregaValores cv = CarregaValores.getInstancia();
    	canvasGrafico.setWidth(cv.getCanvasLargura());
    	canvasGrafico.setHeight(cv.getCanvasAltura());
        
        applicationController = ApplicationController.getInstancia();
        anchorPane.requestFocus();
        buttonFecharJogo.setVisible(false);
        buttonSalvarRecorde.setVisible(false);
        dificuldade = 5;
        
        
        sliderDificuldade.valueProperty().addListener((ob, ov, novoValor) -> {
            dificuldade = novoValor.intValue();
            labelDificuldade.setText("Dificuldade: " + dificuldade);
        });
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
        buttonFecharJogo.setVisible(true);
        buttonNovoJogo.setVisible(false);
        buttonSalvarRecorde.setVisible(false);
        
        anchorPane.requestFocus();
        
        applicationController.novoJogoCampo();

        applicationController.novoJogo(dificuldade, checkBoxAtravessarBordas.isSelected());
    }
    
    @FXML
    public void fecharJogoPressionado(ActionEvent event) {
        applicationController.fecharJogo();
        buttonFecharJogo.setVisible(false);
        buttonNovoJogo.setVisible(true);
    }
    
    @FXML
    public void focarCanvas(MouseEvent event) {
        anchorPane.requestFocus();
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
            buttonFecharJogo.setVisible(false);
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
            buttonFecharJogo.setVisible(false);
            buttonNovoJogo.setVisible(true);
        });
    }
    
    public void atualizarStatus(int macasComidas, int pontos) {
        Platform.runLater(() -> {
            labelMensagem.setText("Maçãs comidas: " + macasComidas + " Pontos: " + pontos);
        });
    }

}

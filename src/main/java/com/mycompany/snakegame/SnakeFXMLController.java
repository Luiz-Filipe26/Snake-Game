package com.mycompany.snakegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SnakeFXMLController implements Initializable{

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
    
    private SnakeController snakeController;
    private int dificuldade;
    private int pontos;
    private boolean jogoFechado;
    
    @FXML
    public void fecharJogoPressionado(ActionEvent event) {
        jogoFechado = true;
        snakeController.fecharJogo();
        buttonFecharJogo.setVisible(false);
        buttonNovoJogo.setVisible(true);
    }

    @FXML
    public void mostrarRecordes(ActionEvent event) {
        try {
            // Carregar o arquivo FXML da nova janela
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RecordesView.fxml"));
            Parent root = loader.load();
            RecordesFXMLController rfc = loader.getController();
            

            // Criar uma nova cena com a nova janela
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Recordes");
            stage.initStyle(StageStyle.UNDECORATED);

            // Ocultar a janela principal
            Stage mainStage = (Stage) anchorPane.getScene().getWindow();
            mainStage.hide();
            rfc.setMainStage(mainStage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void novoJogoPressionado(ActionEvent event) {
        if(jogoFechado) {
            jogoFechado = false;
            pontos = 0;
            
            labelMensagem.setText("");
            buttonFecharJogo.setVisible(true);
            buttonNovoJogo.setVisible(false);
            buttonSalvarRecorde.setVisible(false);

            snakeController.novoJogoCampo();

            snakeController.novoJogo(dificuldade, checkBoxAtravessarBordas.isSelected());
        }
    }

    @FXML
    public void salvarRecorde(ActionEvent event) {
        snakeController.inserirRecorde(textFieldNome.getText(), pontos);
        buttonSalvarRecorde.setVisible(false);
        pontos = 0;
    }
    
    @FXML
    public void sliderDificuldadeMouseSaiu(MouseEvent event) {
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
        jogoFechado = true;
        snakeController.fecharJogo();
        Platform.runLater(() -> {
            buttonFecharJogo.setVisible(false);
            buttonNovoJogo.setVisible(true);
            labelMensagem.setText("Você perdeu! " + labelMensagem.getText());
        });
    }
    public void venceuOJogo() {
        snakeController.fecharJogo();
        Platform.runLater(() -> {
            labelMensagem.setText("Você venceu!");
            buttonFecharJogo.setVisible(false);
            buttonNovoJogo.setVisible(true);
        });
    }
    
    public void atualizarNumMaca(int comidas) {
        Platform.runLater(() -> {
            labelMensagem.setText("Maçãs comidas: " + comidas);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        snakeController = SnakeController.getInstancia();
        anchorPane.requestFocus();
        buttonFecharJogo.setVisible(false);
        buttonSalvarRecorde.setVisible(false);
        jogoFechado = true;
        dificuldade = 5;
        
        
        sliderDificuldade.valueProperty().addListener((observable, oldValue, newValue) -> {
            dificuldade = newValue.intValue();
            labelDificuldade.setText("Dificuldade: " + dificuldade);
        });
    }

}

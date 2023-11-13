package com.mycompany.snakegame;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class FXMLController implements Initializable{

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
    void fecharJogoPressionado(ActionEvent event) {
        jogoFechado = true;
        snakeController.fecharJogo();
        buttonFecharJogo.setVisible(false);
        buttonNovoJogo.setVisible(true);
    }

    @FXML
    void mostrarRecordes(ActionEvent event) {
        //RecordesView recordesView = new RecordesView();
        //recordesView.setVisible(true);
    }

    @FXML
    void novoJogoPressionado(ActionEvent event) {
        if(jogoFechado) {
            jogoFechado = false;
            labelMensagem.setText("");
            buttonFecharJogo.setVisible(true);
            buttonNovoJogo.setVisible(false);
            pontos = 0;
            buttonSalvarRecorde.setVisible(false);
            
            snakeController.novoJogoCampo();
            
            snakeController.novoJogo(dificuldade, checkBoxAtravessarBordas.isSelected());
        }
    }

    @FXML
    void salvarRecorde(ActionEvent event) {
        //snakeController.inserirRecorde(textFieldNome.getText(), pontos);
        buttonSalvarRecorde.setVisible(false);
        pontos = 0;
    }
    public void notificaNovoRecorde(int pontos) {
        this.pontos = pontos;
        labelMensagem.setText("Novo recorde de " + pontos + " pontos!");
        buttonSalvarRecorde.setVisible(true);
    }
    
    public void perdeuOJogo() {
        jogoFechado = true;
        snakeController.fecharJogo();
        buttonFecharJogo.setVisible(false);
        buttonNovoJogo.setVisible(true);
        labelMensagem.setText("Você perdeu! " + labelMensagem.getText());
    }
    public void venceuOJogo() {
        labelMensagem.setText("Você venceu!");
        snakeController.fecharJogo();
        buttonFecharJogo.setVisible(false);
        buttonNovoJogo.setVisible(true);
    }
    
    public void atualizarNumMaca(int comidas) {
        labelMensagem.setText("Maçãs comidas: " + comidas);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buttonFecharJogo.setVisible(false);
        buttonSalvarRecorde.setVisible(false);
        jogoFechado = true;
        
        sliderDificuldade.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Lógica para lidar com a mudança no valor do Slider
                dificuldade = newValue.intValue();
            }
        });
        snakeController = SnakeController.getInstancia();
    }

}

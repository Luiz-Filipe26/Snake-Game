
package com.mycompany.snakegame.view;

import com.mycompany.snakegame.banco.GerenciadorRecordes;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;


public class RecordesFXMLController implements Initializable {
    
    @FXML
    private ListView<String> listViewRecordes;
    
    private Stage mainStage;
    
    @FXML
    public void voltarMenu(ActionEvent event) {
        Stage stage = (Stage) listViewRecordes.getScene().getWindow();
        stage.close();
        mainStage.show();
    }
    
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<String> recordes = GerenciadorRecordes.getInstancia().lerRecordes();
        listViewRecordes.getItems().addAll(recordes);
    }
    
}

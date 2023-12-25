
package fxmlController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.mycompany.snakegame.banco.GerenciadorRecordes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;


public class RecordesView implements Initializable {
	
    @FXML
    private Button buttonVoltarJogoClassico;
    
    @FXML
    private ListView<String> listViewRecordes;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<String> recordes = GerenciadorRecordes.getInstancia().lerRecordes();
        listViewRecordes.getItems().addAll(recordes);
    }
    
}

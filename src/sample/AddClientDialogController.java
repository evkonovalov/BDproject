package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AddClientDialogController {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfAddress;

    @FXML
    void btnAddPersonClicked(ActionEvent event) {
        String name = tfName.getText().trim();
        String address = tfAddress.getText().trim();
        if(address.equals("") || name.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("One of the fields is empty. Nothing created.");
            alert.showAndWait();
        } else {
            String[] cmd = {
                    "python3",
                    "/home/cyberogg/IdeaProjects/TBD/db.py",
                    "library",
                    "postgres",
                    "postgres",
                    "5432",
                    "add_client",
                    "0",
                    name,
                    address
            };
            try {
                Process p = Runtime.getRuntime().exec(cmd);
                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(p.getInputStream()));
                String s = stdInput.readLine();
                boolean res = Boolean.valueOf(s);
                if(!res){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Client added");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Database error");
                    alert.showAndWait();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeStage(event);
    }

    @FXML
    void btnCancelClicked(ActionEvent event) {
        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

}

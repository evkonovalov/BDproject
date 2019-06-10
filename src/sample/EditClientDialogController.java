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

public class EditClientDialogController {

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfnName;
    @FXML
    private TextField tfAddress;

    @FXML
    void btnAddPersonClicked(ActionEvent event) {
        String name = tfName.getText().trim();
        String nName = tfnName.getText().trim();
        String address = tfAddress.getText().trim();
        if(name.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Old Name field is empty. Nothing created.");
            alert.showAndWait();
            return;
        } else if(nName.equals("") && address.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("New Name and New Address fields are empty. Nothing created.");
            alert.showAndWait();
            return;
        } else if(nName.equals("")){
            String[] cmd = {
                    "python3",
                    "/home/cyberogg/IdeaProjects/TBD/db.py",
                    "library",
                    "postgres",
                    "postgres",
                    "5432",
                    "edit_client",
                    "1",
                    name,
                    address
            };
            runPython(cmd);
        }
        else if(address.equals("")){
            String[] cmd = {
                    "python3",
                    "/home/cyberogg/IdeaProjects/TBD/db.py",
                    "library",
                    "postgres",
                    "postgres",
                    "5432",
                    "edit_client",
                    "0",
                    name,
                    nName
            };
            runPython(cmd);
        } else {
            String[] cmd = {
                    "python3",
                    "/home/cyberogg/IdeaProjects/TBD/db.py",
                    "library",
                    "postgres",
                    "postgres",
                    "5432",
                    "edit_client",
                    "2",
                    name,
                    nName,
                    address
            };
            runPython(cmd);
        }
        closeStage(event);
    }

    void runPython(String[] cmd){
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
                alert.setContentText("Client changed");
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

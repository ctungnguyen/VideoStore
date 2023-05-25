package com.example.lastassessment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    Label name;
    @FXML
    Label pass;
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane anchorPane;

     Stage stage;
     Scene scene;

@FXML
    public void logout(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");
        alert.setHeaderText("You are about to log out");
        alert.setContentText("Do you really want to save before log out?: ");

        if (alert.showAndWait().get() == ButtonType.OK){

//            stage = (Stage) anchorPane.getScene().getWindow();
//            System.out.println("Log out");
//            stage.close();

            Parent root = FXMLLoader.load(getClass().getResource("loginStage.fxml"));
            stage = (Stage)anchorPane.getScene().getWindow();
            System.out.println("Log out");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    public void moveToListStage(ActionEvent e) throws IOException {

//            stage = (Stage) anchorPane.getScene().getWindow();
//            System.out.println("Log out");
//            stage.close();

            Parent root = FXMLLoader.load(getClass().getResource("listStage.fxml"));
            stage = (Stage)anchorPane.getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }



    public void displayName(String username){
        name.setText("Hello: " + username);
    }

    public void displayPassword(String password){
        pass.setText("Password: " + password);
    }

}

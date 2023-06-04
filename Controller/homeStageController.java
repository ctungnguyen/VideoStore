package com.example.lastassessment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class homeStageController {

        private Stage stage;
        private Scene scene;
        @FXML
        private AnchorPane anchorPane;

        @FXML
        public void exit(ActionEvent e) throws IOException {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText("You are about to exit");
            alert.setContentText("Do you really want to save before log out?: ");

            if (alert.showAndWait().get() == ButtonType.OK) {
                stage = (Stage) anchorPane.getScene().getWindow();
                stage.setScene(scene);
                stage.close();

            }
        }
    }


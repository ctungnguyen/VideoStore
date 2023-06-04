package com.example.lastassessment;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 826, 559);
        stage.setTitle("Genie's Video Store Application");

        Image icon = new Image("com/example/lastassessment/FXML pics/icon.png");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> {
            e.consume();
            logout(stage);
        });
    }
    public void logout(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");
        alert.setHeaderText("You are about to log out");
        alert.setContentText("Do you really want to save before log out?: ");

        if (alert.showAndWait().get() == ButtonType.OK){
            System.out.println("Log out");
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}


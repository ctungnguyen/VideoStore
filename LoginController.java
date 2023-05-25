package com.example.lastassessment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    TextField usernameTextField;
    @FXML
    TextField passTextField;
    @FXML
    Button loginButton;
    @FXML
    Button cancelButton;
    @FXML
    Button signupButton;
    @FXML
    Button moveToPassButton;
    @FXML
    Label invalidText;

    BufferedReader reader;
    private Stage stage;
    private Scene scene;
    private Parent root;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

    }

    @FXML
    public void login(ActionEvent e) {

        if (usernameTextField.getText().isBlank() == false && passTextField.getText().isBlank() == false){
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();

            String verifyLogin = "select count(1) from user_account where username = '" + usernameTextField.getText() + "'and password ='" + passTextField.getText() + "'";

            try {
                Statement statement = connectDB.createStatement();
                ResultSet queryResult = statement.executeQuery(verifyLogin);

                while(queryResult.next()){
                    if(queryResult.getInt(1) == 1){
                        invalidText.setText("Congratulation!");
                        String username = usernameTextField.getText();
                        String password = passTextField.getText();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("homeStage.fxml"));
                        root = loader.load();
                        HomeController homeController = loader.getController();
                        homeController.displayName(username);
                        homeController.displayPassword(password);

                        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    }   else {
                        invalidText.setText("Invalid Username or Password!");
                    }
                }
            } catch (Exception event) {
                event.printStackTrace();
                event.getCause();
            }
        }   else {
            invalidText.setText("Please enter Username or Password");
        }
    }

    @FXML
    public void cancel(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit");

        if (alert.showAndWait().get() == ButtonType.OK) {

            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            System.out.println("Exit");
            stage.close();
        }
    }

    @FXML
    public void moveToSignupStage(ActionEvent e) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("signupStage.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void moveToForgetPasswordStage(ActionEvent e) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("forgetPasswordStage.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}





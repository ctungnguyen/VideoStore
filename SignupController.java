package com.example.lastassessment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SignupController {
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private TextField passTextField;
    @FXML
    private TextField passconfirmTextField;
    @FXML
    private Label passconfirmLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label usernameExist;


    public void signupButton(ActionEvent e){
        if (passTextField.getText().equals(passconfirmTextField.getText())){
            passconfirmLabel.setText("matching");
            try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            PreparedStatement checkUserExist = connectDB.prepareStatement("select * from user_account where username = ?");
            checkUserExist.setString(1, usernameTextField.getText());
                ResultSet resultSet = checkUserExist.executeQuery();

                if (resultSet.isBeforeFirst()){
                    usernameExist.setText("Already exist!");
                }   else{
                    registerUser();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Congratulation!");
                    alert.setHeaderText("You have signed up successfully!");
                    if (alert.showAndWait().get() == ButtonType.OK) {
                        System.out.println("Log out");
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }



        }   else {
            passconfirmLabel.setText(" not matching");
        }

    }

    public void registerUser(){
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        String name = nameTextField.getText();
        String address = addressTextField.getText();
        String phone = phoneTextField.getText();
        String username = usernameTextField.getText();
        String password = passTextField.getText();

        String insertFields = "insert into user_account (name, address, phone, username, password) values ('";
        String insertValues = name + "','" + address + "','" + phone + "','" + username + "','" + password + "')";
        String insertToRegister = insertFields + insertValues;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Congratulation");
            alert.setHeaderText("You have signed up successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }


    @FXML
    public void moveToLoginStage(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("loginStage.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

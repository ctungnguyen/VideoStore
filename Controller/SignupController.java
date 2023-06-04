package com.example.lastassessment;

import com.example.lastassessment.headers.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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



    public void signupButton(ActionEvent e) throws IOException{
        if (passTextField.getText().equals(passconfirmTextField.getText())){
            passconfirmLabel.setText("matching");
            try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            PreparedStatement checkUserExist = connectDB.prepareStatement("select * from user_account where username = ?");
            checkUserExist.setString(1, usernameTextField.getText());
                ResultSet resultSet = checkUserExist.executeQuery();

                if (resultSet.isBeforeFirst()){
                    passconfirmLabel.setText("This Username is already existed");
                }   else{
                    registerUser();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Congratulation!");
                    alert.setHeaderText("You have signed up successfully!");
                    if (alert.showAndWait().get() == ButtonType.OK) {
                        Parent root = FXMLLoader.load(getClass().getResource("loginStage.fxml"));
                        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }



        }   else {
            passconfirmLabel.setText("Password is not matching");
        }

    }

    public void registerUser(){
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();


        try {
            String query = "insert into user_account (name, address, phoneNumber, username, password, accountStyle) values (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connectDB.prepareStatement(query);
            pstmt.setString(1, nameTextField.getText());
            pstmt.setString(2, addressTextField.getText());
            pstmt.setString(3, phoneTextField.getText());
            pstmt.setString(4, usernameTextField.getText());
            pstmt.setString(5, passTextField.getText());
            pstmt.setString(6, "Guest");
            pstmt.executeUpdate();
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

    public void cancel(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root, 826, 559);
            stage.setScene(scene);
            stage.show();;
        }
    }
}

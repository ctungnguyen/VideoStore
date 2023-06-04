package com.example.lastassessment;

import com.example.lastassessment.headers.Customer;
import com.example.lastassessment.headers.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
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
    @FXML
    AnchorPane anchorPane;

    private Stage stage;
    private Scene scene;
    private Parent root;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }



        @FXML
        public void login (ActionEvent e){


            if (usernameTextField.getText().isBlank() == false && passTextField.getText().isBlank() == false) {
                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();

                String verifyLogin = "select count(1) from user_account where username = '" + usernameTextField.getText() + "'and password ='" + passTextField.getText() + "'";
                String verifyAdmin = "select count(1) from admin where username = '" + usernameTextField.getText() + "'and password ='" + passTextField.getText() + "'";
                try {
                    Statement statement = connectDB.createStatement();
                    Statement st = connectDB.createStatement();
                    ResultSet queryResult = statement.executeQuery(verifyLogin);
                    ResultSet resultSet = st.executeQuery(verifyAdmin);

                    while (queryResult.next() && resultSet.next()) {
                        if (resultSet.getInt(1) == 1) {
                            Parent root = FXMLLoader.load(getClass().getResource("adminStage.fxml"));
                            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        }
                        if (queryResult.getInt(1) == 1) {
                            Customer customer = new Customer();
                            customer.setUsername(usernameTextField.getText());
                            System.out.println(customer.getUsername());

                            invalidText.setText("Congratulation!");
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu2.fxml"));
                            root = loader.load();

                            HomeController homeController = loader.getController();
                           homeController.setCustomer(customer);



                            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            scene = new Scene(root, 826, 559);
                            stage.setScene(scene);
                            stage.show();
                        } else {
                            invalidText.setText("Invalid Username or Password!");
                        }
                    }
                } catch (Exception event) {
                    event.printStackTrace();
                    event.getCause();
                }
            } else {
                invalidText.setText("Please enter Username or Password");
            }
        }


    @FXML
    public void cancel(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root, 826, 559);
            stage.setScene(scene);
            stage.show();
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



}





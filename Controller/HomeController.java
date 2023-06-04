package com.example.lastassessment;

import com.example.lastassessment.headers.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.util.ResourceBundle;
import java.net.URL;
import java.io.IOException;

public class HomeController implements Initializable {
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button About;
    @FXML
    private Button Home;
    @FXML
    private BorderPane borderPane;

    private Stage stage;
    private Scene scene;

    private Parent root;
    private Customer customer;
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }




    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (customer != null) {
            String username = customer.getUsername();
            System.out.println(username);
        } else {
            System.out.println("Customer is null");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("homeStage.fxml"));
        AnchorPane view = null;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        borderPane.setCenter(view);
        // TODO
    }

    @FXML
    public void logout(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");
        alert.setHeaderText("You are about to log out");
        alert.setContentText("Do you really want to save before log out?: ");

        if (alert.showAndWait().get() == ButtonType.OK) {

//            stage = (Stage) anchorPane.getScene().getWindow();
//            System.out.println("Log out");
//            stage.close();

            root = FXMLLoader.load(getClass().getResource("menu.fxml"));
            stage = (Stage) anchorPane.getScene().getWindow();
            System.out.println("Log out");
            scene = new Scene(root, 826, 559);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void moveToHomeStage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("homeStage.fxml"));
        AnchorPane view = loader.load();
        borderPane.setCenter(view);
    }

    @FXML
    private void moveToAboutUsStage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutUsStage.fxml"));
        AnchorPane view = loader.load();
        borderPane.setCenter(view);
    }

    @FXML
    public void moveToListStage(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("listStage.fxml"));
        AnchorPane view = loader.load();
        ListStageController listStageController = loader.getController();
        listStageController.setUsername(customer.getUsername());
        borderPane.setCenter(view);
//        stage = (Stage) anchorPane.getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
    }

    @FXML
    public void moveToProfileStage(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("profileStage.fxml"));
        AnchorPane view = loader.load();
        ProfileStageController profileStageController = loader.getController();
        profileStageController.setUsername(customer.getUsername());
        borderPane.setCenter(view);
        profileStageController.showdata();
    }

    @FXML
    public void moveToOutOfStockStage(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("outOfStockStage.fxml"));
        AnchorPane view = loader.load();
        borderPane.setCenter(view);
    }

    @FXML
    public void moveToHelpStage(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("helpStage.fxml"));
        AnchorPane view = loader.load();
        borderPane.setCenter(view);
    }

    @FXML
    public void moveToLoginStage(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("loginStage.fxml"));
        stage = (Stage) anchorPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void moveToSignupStage(ActionEvent e) throws IOException {
        root = FXMLLoader.load(getClass().getResource("signupStage.fxml"));
        stage = (Stage) anchorPane.getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}






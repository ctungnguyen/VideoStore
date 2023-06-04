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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChangeProfileStageController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private  Label passconfirmLabel;
    @FXML
    private TextField confirmPassTextField;
    private String username;


    public void setUsername(String username) {
        System.out.println(username);
        this.username = username;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void update(ActionEvent e) {

        if (passwordTextField.getText().equals(confirmPassTextField.getText())) {
            passconfirmLabel.setText("matching");
                System.out.println(username);
                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
//            String query = "UPDATE product SET Title = '" + titleTextField.getText() + "', RentalType = '" + rentalTypeChoiceBox.getItems() + "', Genre = '" + genreChoiceBox.getItems() + "', LoanType = '" + loanTypeChoiceBox.getItems() + "', copiesLeft = '" + copiesLeftTextField.getText() + "', RentalFee = '" + rentalFeeTextFiled.getText() + "', RentalStatus = '" + rentalStatusChoiceBox.getItems() + "'";
//            executeQuery(query);

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Update Customer");
                dialog.setHeaderText("Do you really want to update this Customer?");
                ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == updateButtonType) {
                        return updateButtonType;
                    }
                    return null;
                });

                Optional<ButtonType> result = dialog.showAndWait();

                result.ifPresent(pair -> {
                    try {


                        Statement statement = connectDB.createStatement();
                        ResultSet rs = statement.executeQuery("select account_id from user_account where username = '" + username + "'");
                        if (rs.next()) {
                            PreparedStatement checkUserExist = connectDB.prepareStatement("select * from user_account where account_id = ?");
                            checkUserExist.setString(1, rs.getString("account_id"));
                            ResultSet resultSet = checkUserExist.executeQuery();
                            if (resultSet.next()) {
                                String query = "UPDATE user_account SET name = ?, address = ?, phoneNumber = ?, username = ?, password = ? WHERE account_id = ?";
                                PreparedStatement ps = connectDB.prepareStatement(query);
                                ps.setString(1, nameTextField.getText());
                                ps.setString(2, addressTextField.getText());
                                ps.setString(3, phoneTextField.getText());
                                ps.setString(4, usernameTextField.getText());
                                ps.setString(5, passwordTextField.getText());
                                ps.setString(6, rs.getString("account_id"));
                                System.out.println(ps);
                                ps.execute();

                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText(null);
                                alert.setContentText("Customer updated successfully!");
                                if (alert.showAndWait().get() == ButtonType.OK) {
                                    String username = usernameTextField.getText();
                                    Customer customer = new Customer();
                                    customer.setUsername(username);

                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("menu2.fxml"));
                                    root = loader.load();
                                    HomeController homeController = loader.getController();
                                    homeController.setCustomer(customer);
                                    stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                    scene = new Scene(root, 826, 559);
                                    stage.setScene(scene);
                                    stage.show();
                                }
                            }

                        }
                        } catch (SQLException | IOException ex) {
                        ex.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("This Username is already existed!");
                        alert.showAndWait();
                    }
                });
        } else {
            passconfirmLabel.setText("Password is not matching");
        }
    }

    @FXML
    public void showinfo() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        try {
            System.out.println("select username, password, name, phoneNumber, address from user_account where username = '" + username + "'");
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery("select username, password, name, phoneNumber, address from user_account where username = '" + username + "'");
            if (resultSet.next()) {
                String queryUsername = resultSet.getString("username");
                usernameTextField.setText(queryUsername);
                passwordTextField.setText(resultSet.getString("password"));
                nameTextField.setText(resultSet.getString("name"));
                addressTextField.setText(resultSet.getString("address"));
                phoneTextField.setText(resultSet.getString("phoneNumber"));
                System.out.println(resultSet.getString("phoneNumber"));

            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancel(ActionEvent e) throws IOException {
        System.out.println(username);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel");
        alert.setHeaderText("You are about to cancel");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println(username);
            Customer customer = new Customer();
            customer.setUsername(username);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu2.fxml"));
            root = loader.load();
            HomeController homeController = loader.getController();
            homeController.setCustomer(customer);
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root, 826, 559);
            stage.setScene(scene);
            stage.show();
        }
    }

}

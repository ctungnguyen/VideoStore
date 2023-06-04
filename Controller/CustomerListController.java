package com.example.lastassessment;

import com.example.lastassessment.headers.Customer;
import com.example.lastassessment.headers.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CustomerListController implements Initializable {
    private String[] accountStyle = {"Guest", "Regular", "Vip"};
    @FXML
    private TextField nameTextField;
    @FXML
    private Label nameExist;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ChoiceBox<String> accountStyleChoiceBox;
    @FXML
    private Button insertButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private javafx.scene.control.TableView<Customer> customerList;
    @FXML
    private TableColumn<Customer, Integer> customerIDColumn;
    @FXML
    private TableColumn<Customer, String> usernameColumn;
    @FXML
    private TableColumn<Customer, String> passwordColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> accountStyleColumn;
    @FXML
    private TableColumn<Customer, String> phoneNumberColumn;
    @FXML
    private TextField searchTextField;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private AnchorPane anchorPane;

    ObservableList<Customer> listModelObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accountStyleChoiceBox.getItems().addAll(accountStyle);

        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        //SQL query - execute in the backend database
        String productViewQuery = "select account_id, name, address, phoneNumber, username, password, accountStyle from user_account";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(productViewQuery);

            while (queryOutput.next()) {
                String queryCustomerID = String.format("%03d", queryOutput.getInt("account_id"));
                String querryusername = queryOutput.getString("username");
                String querrypassword = queryOutput.getString("password");
                String querryname = queryOutput.getString("name");
                String querryaddress = queryOutput.getString("address");
                String queryphoneNumber = queryOutput.getString("phoneNumber");
                String querryaccountStyle = queryOutput.getString("accountStyle");

                //populate the Observation
                listModelObservableList.add(new Customer("C" + queryCustomerID, querryname, querryaddress, queryphoneNumber, querryusername, querrypassword, querryaccountStyle));
            }

            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("account_id"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            accountStyleColumn.setCellValueFactory(new PropertyValueFactory<>("accountStyle"));
            phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

            customerList.setItems(listModelObservableList);
            search();



            customerList.setOnMouseClicked(event ->{
                if (event.getClickCount() == 2) {
                    Customer selectedItem = customerList.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        showdata();

                    }
                }
            });




        } catch (SQLException e) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

    }

    @FXML
    private void insert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Create Customer");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to create " + usernameTextField.getText() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                PreparedStatement checkUserExist = connectDB.prepareStatement("select * from user_account where username = ?");
                checkUserExist.setString(1, usernameTextField.getText());
                ResultSet resultSet = checkUserExist.executeQuery();

                if (resultSet.isBeforeFirst()){
                    nameExist.setText("Already exist!");
                }   else {
                    nameExist.setText(null);
                    Statement statement = connectDB.createStatement();
                    String query = "insert into user_account (name, address, phoneNumber, username, password, accountStyle) values (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = connectDB.prepareStatement(query);
                    pstmt.setString(1, nameTextField.getText());
                    pstmt.setString(2, addressTextField.getText());
                    pstmt.setString(3, phoneNumberTextField.getText());
                    pstmt.setString(4, usernameTextField.getText());
                    pstmt.setString(5, passwordTextField.getText());
                    pstmt.setString(6, accountStyleChoiceBox.getValue());
                    pstmt.executeUpdate();
                    String productViewQuery = "select account_id, name, address, phoneNumber, username, password, accountStyle from user_account";
                    statement.executeQuery(productViewQuery);

                    String queryCustomerID = String.format("%03d", getNextId());
                    String querryusername = usernameTextField.getText();
                    String querrypassword = passwordTextField.getText();
                    String querryname = nameTextField.getText();
                    String querryaddress = addressTextField.getText();
                    String queryphoneNumber = phoneNumberTextField.getText();
                    String querryaccountStyle = accountStyleChoiceBox.getValue();

                    //populate the Observation
                    listModelObservableList.add(new Customer("C" + queryCustomerID, querryname, querryaddress, queryphoneNumber, querryusername, querrypassword, querryaccountStyle));

                    customerList.setItems(listModelObservableList);
                    search();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void update() {

        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();


//            String query = "UPDATE product SET Title = '" + titleTextField.getText() + "', RentalType = '" + rentalTypeChoiceBox.getItems() + "', Genre = '" + genreChoiceBox.getItems() + "', LoanType = '" + loanTypeChoiceBox.getItems() + "', copiesLeft = '" + copiesLeftTextField.getText() + "', RentalFee = '" + rentalFeeTextFiled.getText() + "', RentalStatus = '" + rentalStatusChoiceBox.getItems() + "'";
//            executeQuery(query);

        Customer selectedItem = customerList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
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
                    PreparedStatement checkUserExist = connectDB.prepareStatement("select * from user_account where account_id = ?");
                    checkUserExist.setString(1, selectedItem.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
                    ResultSet resultSet = checkUserExist.executeQuery();

                    if (resultSet.next()) {
                        String query = "UPDATE user_account SET name = ?, address = ?, phoneNumber = ?, username = ?, password = ?, accountStyle = ? WHERE account_id = ?";
                        PreparedStatement ps = connectDB.prepareStatement(query);
                        ps.setString(1, nameTextField.getText());
                        ps.setString(2, addressTextField.getText());
                        ps.setString(3, phoneNumberTextField.getText());
                        ps.setString(4, usernameTextField.getText());
                        ps.setString(5, passwordTextField.getText());
                        ps.setString(6, accountStyleChoiceBox.getValue());
                        ps.setString(7, selectedItem.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
                        System.out.println(ps);
                        ps.execute();

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Customer updated successfully!");
                        alert.showAndWait();

                        String queryaccount_id = selectedItem.getAccount_id().replaceAll("[^0-9]*([0-9]+).*", "$1");
                        String queryname = nameTextField.getText();
                        String queryaddress = addressTextField.getText();
                        String queryphoneNumber = phoneNumberTextField.getText();
                        String queryusername = usernameTextField.getText();
                        String querypassword = passwordTextField.getText();
                        String queryaccountStyle = accountStyleChoiceBox.getValue();


                        //populate the Observation
                        listModelObservableList.add(new Customer("C" + queryaccount_id, queryname, queryaddress, queryphoneNumber, queryusername, querypassword, queryaccountStyle));
                        listModelObservableList.remove(selectedItem);
                        listModelObservableList.sorted();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("This Username is already existed.");
                    alert.showAndWait();
                }
            });
        }
    }

    @FXML
    private void delete() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Customer selectedItem = customerList.getSelectionModel().getSelectedItem();
        if (customerList != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete " + selectedItem.getUsername() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    PreparedStatement statement = connectDB.prepareStatement("DELETE FROM user_account WHERE account_id = ?");
                    statement.setString(1, selectedItem.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
                    statement.executeUpdate();
                    listModelObservableList.remove(selectedItem);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @FXML
    public void moveToItemList(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("adminStage.fxml"));
        stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logout(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");
        alert.setHeaderText("You are about to log out");
        alert.setContentText("Do you really want to save before log out?: ");

        if (alert.showAndWait().get() == ButtonType.OK){

            Parent root = FXMLLoader.load(getClass().getResource("loginStage.fxml"));
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    private int getNextId() {
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();
            ResultSet rs;
            rs = statement.executeQuery("SELECT MAX(account_id) FROM user_account");
            if (rs.next()) {
                return Integer.parseInt(rs.getString(1));
            }
            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void search() {
        FilteredList<Customer> filteredData = new FilteredList<>(listModelObservableList, b -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(Customer -> {
                //if no search value then display all records or whatever records it current have. no changes.
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                if (Customer.getUsername().toLowerCase().contains(searchKeyword)) {
                    return true;    //we found a match in Title
                }   else if (Customer.getAccount_id().toLowerCase().contains(searchKeyword)){
                    return true;
                }else {
                    return false;   //no match found
                }
            });
        });
        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        //bind sorted result with table view
        sortedData.comparatorProperty().bind(customerList.comparatorProperty());
        //apply filtered and sorted data to the table view
        customerList.setItems(sortedData);

    }

    private void showdata() {
        DatabaseConnection connection = new DatabaseConnection();
        connection.getConnection();
        Customer selectedCustomer = customerList.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            nameTextField.setText(selectedCustomer.getName());
            accountStyleChoiceBox.setValue(selectedCustomer.getAccountStyle());
            addressTextField.setText(selectedCustomer.getAddress());
            passwordTextField.setText(selectedCustomer.getPassword());
            phoneNumberTextField.setText(selectedCustomer.getPhoneNumber());
            usernameTextField.setText(selectedCustomer.getUsername());
        }
    }
}




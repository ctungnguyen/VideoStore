package com.example.lastassessment;

import com.example.lastassessment.headers.Customer;
import com.example.lastassessment.headers.DatabaseConnection;
import com.example.lastassessment.headers.Item;
import com.example.lastassessment.headers.Rental;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileStageController implements Initializable {
    @FXML
    private Label IDLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label accountStyleLabel;
    @FXML
    private Label returnAmountLabel;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private TableView<Item> itemList;
    @FXML
    private TableColumn<Item, String> productIDColumn;
    @FXML
    private TableColumn<Item, String> titleColumn;
    @FXML
    private TableColumn<Item, String> rentalTypeColumn;
    @FXML
    private TableColumn<Item, String> genreColumn;
    @FXML
    private TableColumn<Item, String> loanTypeColumn;
    @FXML
    private TableColumn<Item, String> rentalFeeColumn;
    @FXML
    private TextField searchTextField;
    @FXML
    private Label welcomenameLabel;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private AnchorPane anchorPane1;
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
    private TextField confirmPassTextField;
    private String username;

    public void setUsername(String username) {
        System.out.println(username);
        this.username = username;
    }

    ObservableList<Item> listModelObservableList = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void moveToChangeProfileStage(ActionEvent e) throws IOException {
        Customer customer = new Customer();
        customer.setUsername(username);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("changeProfileStage.fxml"));
        root = loader.load();
        ChangeProfileStageController changeProfileStageController = loader.getController();
        changeProfileStageController.setUsername(username);

        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        changeProfileStageController.showinfo();
        System.out.println(username);
    }

    public void logout(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");
        alert.setHeaderText("You are about to log out");
        alert.setContentText("Do you really want to save before log out?: ");

        if (alert.showAndWait().get() == ButtonType.OK) {
            root = FXMLLoader.load(getClass().getResource("menu.fxml"));
            stage = (Stage) anchorPane.getScene().getWindow();
            System.out.println("Log out");
            scene = new Scene(root, 826, 559);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void returnButton(ActionEvent event) {
        if (itemList == null) {
            return;
        }
        // Retrieve the selected item in the table view
        Item selectedItem = itemList.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Return Item");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to return " + selectedItem.getTitle() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                try {
                    Customer customer = new Customer();
                    Statement statement = connectDB.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from user_account where username = '" + username + "'");
                    if (resultSet.next()) {
                        customer.setAccount_id(resultSet.getString("account_id"));
                        customer.setUsername(resultSet.getString("username"));
                        customer.setPassword(resultSet.getString("password"));
                        customer.setAccountStyle(resultSet.getString("accountStyle"));
                        customer.setName(resultSet.getString("name"));
                        customer.setAddress(resultSet.getString("address"));
                        customer.setPhoneNumber(resultSet.getString("phoneNumber"));
                        customer.setReturnAmount(resultSet.getInt("returnAmount"));

                        // Create a new rental object and return the item
                        Rental rental = new Rental(selectedItem, customer);
                        rental.returnItem(connectDB);
                        listModelObservableList.remove(selectedItem);
                        showdata();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void showdata() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

//        SQL query - execute in the backend database
        try {
            Statement st = connectDB.createStatement();
            ResultSet rs = st.executeQuery("select account_id, username, password, name, address, phoneNumber, accountStyle, returnAmount from user_account where username = '" + username + "'");
            if (rs.next()) {
                String id = String.format("%03d", Integer.parseInt(rs.getString("account_id")));
                IDLabel.setText("C" + id);
                welcomenameLabel.setText(rs.getString("username"));
                usernameLabel.setText(rs.getString("username"));
                passwordLabel.setText(rs.getString("password"));
                accountStyleLabel.setText(rs.getString("accountStyle"));
                nameLabel.setText(rs.getString("name"));
                addressLabel.setText(rs.getString("address"));
                phoneLabel.setText(rs.getString("phoneNumber"));
                returnAmountLabel.setText(rs.getString("returnAmount"));
            }
        } catch (SQLException e) {
            Logger.getLogger(ProfileStageController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }

        try {
            String str = IDLabel.getText();
            String id = str.replaceAll("[^0-9\\.]+", "");
            String productViewQuery = "SELECT product.ProductID, product.Title, product.RentalType, product.Genre, product.LoanType, product.RentalFee, product.publishedYear FROM rental JOIN product ON product.ProductID = rental.ProductID WHERE rental.account_id = '" + id + "'and rental.Status = 'Not Returned'";
            System.out.println(productViewQuery);
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(productViewQuery);
            while (queryOutput.next()) {
                String queryProductID = String.format("%03d", queryOutput.getInt("ProductID"));
                String querryTitle = queryOutput.getString("Title");
                String querryRentalType = queryOutput.getString("RentalType");
                String querryGenre = queryOutput.getString("Genre");
                String querryLoanType = queryOutput.getString("LoanType");
                Double querryRentalFee = queryOutput.getDouble("RentalFee");

                //populate the Observation
                listModelObservableList.add(new Item("I" + queryProductID + "-" + queryOutput.getInt("publishedYear"), querryTitle, querryRentalType, querryGenre, querryLoanType, querryRentalFee + "USD"));
            }

            productIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            rentalTypeColumn.setCellValueFactory(new PropertyValueFactory<>("rentalType"));
            genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
            loanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("loanType"));
            rentalFeeColumn.setCellValueFactory(new PropertyValueFactory<>("rentalFee"));

            itemList.setItems(listModelObservableList);
            search();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void search() {
        FilteredList<Item> filteredData = new FilteredList<>(listModelObservableList, b -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                //if no search value then display all records or whatever records it current have. no changes.
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                if (item.getTitle().toLowerCase().contains(searchKeyword)) {
                    return true;    //we found a match in Title
                } else {
                    return false;   //no match found
                }
            });
        });
        SortedList<Item> sortedData = new SortedList<>(filteredData);
        //bind sorted result with table view
        sortedData.comparatorProperty().bind(itemList.comparatorProperty());
        //apply filtered and sorted data to the table view
        itemList.setItems(sortedData);
    }
}

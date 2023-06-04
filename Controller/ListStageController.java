package com.example.lastassessment;

import com.example.lastassessment.headers.Customer;
import com.example.lastassessment.headers.DatabaseConnection;
import com.example.lastassessment.headers.Item;
//import com.example.lastassessment.a.Rental;
import com.example.lastassessment.headers.Rental;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListStageController implements Initializable {
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
private TableColumn<Item, String> copiesLeftColumn;
@FXML
private TableColumn<Item, String> rentalFeeColumn;
@FXML
private TableColumn<Item, String> rentalStatusColumn;
@FXML
private TextField searchTextField;


private String status;
private String username;


    public void setUsername(String username) {
        System.out.println(username);
        this.username = username;
    }


public String getStatus(){
    return  status;
}
 public void setStatus(String a){
    this.status = a;
}

ObservableList<Item> listModelObservableList = FXCollections.observableArrayList();


@Override
    public void initialize(URL url, ResourceBundle resourceBundle){
    DatabaseConnection connection = new DatabaseConnection();
    Connection connectDB = connection.getConnection();

    //SQL query - execute in the backend database
    String productViewQuery = "select ProductID, Title, RentalType, Genre, LoanType, CopiesLeft, RentalFee, RentalStatus, publishedYear from product where CopiesLeft != 0";

    try {
        Statement statement = connectDB.createStatement();
        ResultSet queryOutput = statement.executeQuery(productViewQuery);

        while (queryOutput.next()){
            String queryProductID = String.format("%03d", queryOutput.getInt("ProductID"));
            String querryTitle = queryOutput.getString("Title");
            String querryRentalType = queryOutput.getString("RentalType");
            String querryGenre = queryOutput.getString("Genre");
            String querryLoanType = queryOutput.getString("LoanType");
            Integer queryCopiesLeft = queryOutput.getInt("CopiesLeft");
            Double querryRentalFee = queryOutput.getDouble("RentalFee");
            String querryRentalStatus = queryOutput.getString("RentalStatus");

            //populate the Observation
            listModelObservableList.add(new Item("I" + queryProductID + "-" + queryOutput.getInt("publishedYear"), querryTitle, querryRentalType, querryGenre, querryLoanType, queryCopiesLeft, querryRentalFee + "USD", querryRentalStatus));
        }

        productIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        rentalTypeColumn.setCellValueFactory(new PropertyValueFactory<>("rentalType"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        loanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("loanType"));
        copiesLeftColumn.setCellValueFactory(new PropertyValueFactory<>("copiesLeft"));
        rentalFeeColumn.setCellValueFactory(new PropertyValueFactory<>("rentalFee"));
        rentalStatusColumn.setCellValueFactory(new PropertyValueFactory<>("rentalStatus"));

        itemList.setItems(listModelObservableList);

        FilteredList<Item> filteredData = new FilteredList<>(listModelObservableList, b -> true);

        searchTextField.textProperty().addListener((observable,oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                //if no search value then display all records or whatever records it current have. no changes.
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                if (item.getTitle().toLowerCase().contains(searchKeyword)) {
                    return true;    //we found a match in Title
                } else if (item.getProductID().toLowerCase().contains(searchKeyword)) {
                    return true;   //no match found
                }   else if(item.getCopiesLeft().toString().contains(searchKeyword)){
                    return true;
                }else{
                    return false;
                   //no match found
                }
            });
        });

        SortedList<Item> sortedData = new SortedList<>(filteredData);

        //bind sorted result with table view
        sortedData.comparatorProperty().bind(itemList.comparatorProperty());

        //apply filtered and sorted data to the table view
        itemList.setItems(sortedData);
    } catch (SQLException e) {
        Logger.getLogger(ListStageController.class.getName()).log(Level.SEVERE, null, e);
        e.printStackTrace();
    }

}


    @FXML
    private void rent(ActionEvent event) {
    System.out.println("select * from user_account where username = '" + username + "'");
        if (itemList == null) {
            System.out.println("no");
            return;
        }

        // rest of the code here
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Item selectedItem = itemList.getSelectionModel().getSelectedItem();


        if (itemList != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rent Item");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to rent " + selectedItem.getTitle() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try  {
                    Statement statement = connectDB.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from user_account where username = '" + username + "'");
                    if (resultSet.next()) {
                        Customer customer = new Customer();
                        customer.setAccount_id(resultSet.getString("account_id"));
                        customer.setUsername(resultSet.getString("username"));
                        customer.setPassword(resultSet.getString("password"));
                        customer.setAccountStyle(resultSet.getString("accountStyle"));
                        customer.setName(resultSet.getString("name"));
                        customer.setAddress(resultSet.getString("address"));
                        customer.setPhoneNumber(resultSet.getString("phoneNumber"));
                        if (customer != null) {
                            Rental rental = new Rental(selectedItem, customer);
                            rental.rentItem(connectDB);
                        }

                    }
                    Alert al = new Alert(Alert.AlertType.CONFIRMATION);
                    al.setTitle("Congratulation!");
                    al.setHeaderText(null);
                    al.setContentText("You have rent " + selectedItem.getTitle() + "successfully.");


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

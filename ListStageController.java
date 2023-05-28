package com.example.lastassessment;

import com.example.lastassessment.OOP.Item;
import com.example.lastassessment.a.Customer;
import com.example.lastassessment.a.DatabaseConnection;
import com.example.lastassessment.a.ListModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListStageController implements Initializable {
@FXML
private TableView<ListModel> itemList;
@FXML
private TableColumn<ListModel, String> productIDColumn;
@FXML
private TableColumn<ListModel, String> titleColumn;
@FXML
private TableColumn<ListModel, String> rentalTypeColumn;
@FXML
private TableColumn<ListModel, String> genreColumn;
@FXML
private TableColumn<ListModel, String> loanTypeColumn;
@FXML
private TableColumn<ListModel, String> copiesLeftColumn;
@FXML
private TableColumn<ListModel, String> rentalFeeColumn;
@FXML
private TableColumn<ListModel, String> rentalStatusColumn;
@FXML
private TextField searchTextField;
private ListModel item;
private Customer customer;
private String status;
public ListModel getItem() {
    return item;
}
private Customer getCustomer() {
    return customer;
}
public String getStatus(){
    return  status;
}
 public void setStatus(String a){
    this.status = a;
}

ObservableList<ListModel> listModelObservableList = FXCollections.observableArrayList();


@Override
    public void initialize(URL url, ResourceBundle resourceBundle){
    DatabaseConnection connection = new DatabaseConnection();
    Connection connectDB = connection.getConnection();

    //SQL query - execute in the backend database
    String productViewQuery = "select ProductID, Title, RentalType, Genre, LoanType, CopiesLeft, RentalFee, RentalStatus from product";

    try {
        Statement statement = connectDB.createStatement();
        ResultSet queryOutput = statement.executeQuery(productViewQuery);

        while (queryOutput.next()){
            String queryProductID = "I" + String.format("%03d", queryOutput.getInt("ProductID")) + "-" + String.format("%04d", queryOutput.getInt("ProductID"));
            String querryTitle = queryOutput.getString("Title");
            String querryRentalType = queryOutput.getString("RentalType");
            String querryGenre = queryOutput.getString("Genre");
            String querryLoanType = queryOutput.getString("LoanType");
            Integer queryCopiesLeft = queryOutput.getInt("CopiesLeft");
            String querryRentalFee = queryOutput.getDouble("RentalFee") + " USD";
            String querryRentalStatus = queryOutput.getString("RentalStatus");

                //populate the Observation
            listModelObservableList.add(new ListModel(queryProductID, querryTitle, querryRentalType, querryGenre, querryLoanType, queryCopiesLeft, querryRentalFee, querryRentalStatus));
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

        FilteredList<ListModel> filteredData = new FilteredList<>(listModelObservableList, b -> true);

        searchTextField.textProperty().addListener((observable,oldValue, newValue) -> {
            filteredData.setPredicate(listModel -> {
                //if no search value then display all records or whatever records it current have. no changes.
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null){
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                if (listModel.getTitle().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;    //we found a match in Title
                }     else {
                    return false;   //no match found
                }
            });
        });

        SortedList<ListModel> sortedData = new SortedList<>(filteredData);

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
    private void rent(ActionEvent event) throws IOException {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        ListModel selectedItem = itemList.getSelectionModel().getSelectedItem();
        if (itemList != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rent Item");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to rent " + selectedItem.getTitle() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try  {
                    // Check if there are any copies of the item available
                    String select = "SELECT Copiesleft FROM product WHERE ProductID = ?";
                    PreparedStatement psSelect = connectDB.prepareStatement(select);
                    psSelect.setString(1, itemList.getId());
                    ResultSet rsSelect = psSelect.executeQuery();
                    int leftCopies = 0;
                    if (rsSelect.next()) {
                        leftCopies = rsSelect.getInt("CopiesLeft");
                    }

                    if (leftCopies == 0) {
                        System.out.println("No copy of " + selectedItem.getTitle() + " are available for rent.");
                        return;
                    }

                    if (item.getLoanType().equals("2-day") && customer.getAccountStyle().equals("Guest")) {
                        System.out.println("Guest accounts cannot rent 2-day items.");
                        return;
                    }

                    // Check if the customer has reached the maximum number of rentals
                    String countSql = "SELECT COUNT(*) AS RentalCount FROM rental WHERE account_id = ? AND Status = 'Not Returned'";
                    PreparedStatement psCount = connectDB.prepareStatement(countSql);
                    psCount.setInt(1, Integer.parseInt(customer.getAccount_id()));
                    ResultSet rsCount = psCount.executeQuery();
                    int rentalCount = 0;
                    if (rsCount.next()) {
                        rentalCount = rsCount.getInt("RentalCount");
                    }

                    if (rentalCount >= customer.getMaxRental()) {
                        System.out.println("You have reached the maximum number of rentals.");
                        return;
                    }

                    if ((customer.getAccountStyle().equals("VIP")) && (customer.getPoints() >= 100) && (rentalCount == 0)) {
                        System.out.println("You have a free rent. ");
                    }

                    // Update the left copy of the item
                    String updateSql = "UPDATE product SET CopiesLeft = CopiesLeft - 1 WHERE ProductID = ?";
                    PreparedStatement psUpdate = connectDB.prepareStatement(updateSql);
                    psUpdate.setString(1, item.getProductID());
                    psUpdate.executeUpdate();

                    // Insert a new row into the rental table
                    String insertSql = "INSERT INTO rental (ProductID, account_id, Status) VALUES (?, ?, ?)";
                    PreparedStatement psInsert = connectDB.prepareStatement(insertSql);
                    psInsert.setString(1, item.getProductID());
                    psInsert.setInt(2, Integer.parseInt(customer.getAccount_id()));
                    psInsert.setString(3, "Not Returned");
                    psInsert.executeUpdate();
                    setStatus("Not Returned");

                    System.out.println("You have successfully rented " + item.getTitle() + ".");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

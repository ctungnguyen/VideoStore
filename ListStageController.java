package com.example.lastassessment;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
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
            Integer queryProductID = queryOutput.getInt("ProductID");
            String querryTitle = queryOutput.getString("Title");
            String querryRentalType = queryOutput.getString("RentalType");
            String querryGenre = queryOutput.getString("Genre");
            String querryLoanType = queryOutput.getString("LoanType");
            Integer queryCopiesLeft = queryOutput.getInt("CopiesLeft");
            Double querryRentalFee = queryOutput.getDouble("RentalFee");
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

}

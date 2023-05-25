package com.example.lastassessment;

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
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;



public class AdminController implements Initializable {
        @FXML
        private ChoiceBox<com.example.lastassessment.AdminController> rentalTypeChoiceBox;
        @FXML
        private ChoiceBox<com.example.lastassessment.AdminController> genreChoiceBox;
        @FXML
        private ChoiceBox<com.example.lastassessment.AdminController> loanTypeChoiceBox;
        @FXML
        private ChoiceBox<com.example.lastassessment.AdminController> rentalStatusChoiceBox;
        @FXML
        private TextField titleTextField;
        @FXML
        private TextField publishedYearTextField;
        @FXML
        private TextField copiesLeftTextField;
        @FXML
        private TextField rentalFeeTextFiled;
        @FXML
        private Button insertButton;
        @FXML
        private Button updateButton;
        @FXML
        private Button deleteButton;
        @FXML
        private javafx.scene.control.TableView<ListModel> itemList;
        @FXML
        private TableColumn<ListModel, Integer> productIDColumn;
        @FXML
        private TableColumn<ListModel, String> titleColumn;
        @FXML
        private TableColumn<ListModel, String> rentalTypeColumn;
        @FXML
        private TableColumn<ListModel, String> genreColumn;
        @FXML
        private TableColumn<ListModel, String> loanTypeColumn;
        @FXML
        private TableColumn<ListModel, Integer> copiesLeftColumn;
        @FXML
        private TableColumn<ListModel, Double> rentalFeeColumn;
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


        @FXML
        private void insertButton(ActionEvent e) {
            String query = "insert into product (Title, RentalType, Genre, LoanType, CopiesLeft, RentalFee, RentalStatus) values ('" + titleTextField.getText() + "','" + rentalTypeChoiceBox.getItems() + "','"+ genreChoiceBox.getItems() + "','" + loanTypeChoiceBox.getItems() + "','" + copiesLeftTextField.getText() + "','" + rentalFeeTextFiled.getText() + "','" + rentalStatusChoiceBox.getItems() + "')";
            executeQuery(query);

        }


        @FXML
        private void updateButton() {
            String query = "UPDATE product SET Title='"+titleTextField.getText()+"',Author='"+authorField.getText()+"',Year="+yearField.getText()+",Pages="+pagesField.getText()+" WHERE ID="+idField.getText()+"";
            executeQuery(query);

        }

        @FXML
        private void deleteButton() {
            String query = "DELETE FROM product WHERE ID="+idField.getText()+"";
            executeQuery(query);

        }

        public void executeQuery(String query) {
            try {
                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        @Override
        public void initialize(URL location, ResourceBundle resources) {
            showBooks();
        }

        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        public ObservableList<ListModel> getBooksList(){
            ObservableList<Books> booksList = FXCollections.observableArrayList();
            Connection connection = getConnection();
            String query = "SELECT * FROM product ";
            Statement st;
            ResultSet rs;

            try {
                st = connection.createStatement();
                rs = st.executeQuery(query);
                Books books;
                while(rs.next()) {
                    books = new Books(rs.getInt("Id"),rs.getString("Title"),rs.getString("Author"),rs.getInt("Year"),rs.getInt("Pages"));
                    booksList.add(books);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return booksList;
        }

        // I had to change ArrayList to ObservableList I didn't find another option to do this but this works :)
        public void showBooks() {
            ObservableList<Books> list = getBooksList();

            idColumn.setCellValueFactory(new PropertyValueFactory<Books,Integer>("id"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<Books,String>("title"));
            authorColumn.setCellValueFactory(new PropertyValueFactory<Books,String>("author"));
            yearColumn.setCellValueFactory(new PropertyValueFactory<Books,Integer>("year"));
            pagesColumn.setCellValueFactory(new PropertyValueFactory<Books,Integer>("pages"));

            TableView.setItems(list);
        }

    }
}

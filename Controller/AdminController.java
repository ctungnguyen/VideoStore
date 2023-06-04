package com.example.lastassessment;

import com.example.lastassessment.headers.DatabaseConnection;
import com.example.lastassessment.headers.Item;
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


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;



public class AdminController implements Initializable {
    private String[] rentalType = {"Video Game", "DVD", "Old movie record"};
    private String[] genre = {"Action", "Horror", "Drama", "Comedy", ""};
    private String[] loanType = {"2-day", "1-week"};
    private String[] rentalStatus = {"not available", "available"};
    @FXML
    private ChoiceBox<String> rentalTypeChoiceBox;
    @FXML
    private ChoiceBox<String> genreChoiceBox;
    @FXML
    private ChoiceBox<String> loanTypeChoiceBox;
    @FXML
    private ChoiceBox<String> rentalStatusChoiceBox;
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
    private javafx.scene.control.TableView<Item> itemList;
    @FXML
    private TableColumn<Item, Integer> productIDColumn;
    @FXML
    private TableColumn<Item, String> titleColumn;
    @FXML
    private TableColumn<Item, String> rentalTypeColumn;
    @FXML
    private TableColumn<Item, String> genreColumn;
    @FXML
    private TableColumn<Item, String> loanTypeColumn;
    @FXML
    private TableColumn<Item, Integer> copiesLeftColumn;
    @FXML
    private TableColumn<Item, Double> rentalFeeColumn;
    @FXML
    private TableColumn<Item, String> rentalStatusColumn;
    @FXML
    private TextField searchTextField;
    private Stage stage;
    private Scene scene;
    @FXML
    private AnchorPane anchorPane;


    ObservableList<Item> listModelObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        rentalTypeChoiceBox.getItems().addAll(rentalType);
        genreChoiceBox.getItems().addAll(genre);
        loanTypeChoiceBox.getItems().addAll(loanType);
        rentalStatusChoiceBox.getItems().addAll(rentalStatus);

        //SQL query - execute in the backend database
        String productViewQuery = "select ProductID, Title, RentalType, Genre, LoanType, CopiesLeft, RentalFee, RentalStatus, publishedYear from product";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(productViewQuery);

            while (queryOutput.next()) {
                String queryProductID = String.format("%03d", queryOutput.getInt("ProductID"));
                String querryTitle = queryOutput.getString("Title");
                String querryRentalType = queryOutput.getString("RentalType");
                String querryGenre = queryOutput.getString("Genre");
                String querryLoanType = queryOutput.getString("LoanType");
                Integer queryCopiesLeft = queryOutput.getInt("CopiesLeft");
                Double querryRentalFee = queryOutput.getDouble("RentalFee");
                String querryRentalStatus = queryOutput.getString("RentalStatus");

                //populate the Observation
                listModelObservableList.add(new Item("I" + queryProductID + "-" + queryOutput.getInt("publishedYear"), querryTitle, querryRentalType, querryGenre, querryLoanType, queryCopiesLeft, querryRentalFee + " USD", querryRentalStatus));
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
            search();



            itemList.setOnMouseClicked(event ->{
                if (event.getClickCount() == 2) {
                    Item selectedItem = itemList.getSelectionModel().getSelectedItem();
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
    private void insertButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Create Item");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to create " + titleTextField.getText() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DatabaseConnection connection = new DatabaseConnection();
                Connection connectDB = connection.getConnection();
                Statement statement = connectDB.createStatement();
                String query = "insert into product (Title, RentalType, Genre, LoanType, CopiesLeft, RentalFee, RentalStatus, publishedYear) values ('" + titleTextField.getText() + "','" + rentalTypeChoiceBox.getValue() + "','" + genreChoiceBox.getValue() + "','" + loanTypeChoiceBox.getValue() + "','" + copiesLeftTextField.getText() + "','" + rentalFeeTextFiled.getText() + "','" + rentalStatusChoiceBox.getValue() + "', '" + publishedYearTextField.getText() + "')";
                statement.execute(query);
                String productViewQuery = "select ProductID, Title, RentalType, Genre, LoanType, CopiesLeft, RentalFee, RentalStatus from product";
                statement.executeQuery(productViewQuery);

                Integer queryPublishedYear = Integer.valueOf(publishedYearTextField.getText());
                String queryProductID = String.format("%03d", getNextId());
                String querryTitle = titleTextField.getText();
                String querryRentalType = rentalTypeChoiceBox.getValue();
                String querryGenre = genreChoiceBox.getValue();
                String querryLoanType = loanTypeChoiceBox.getValue();
                Integer queryCopiesLeft = Integer.valueOf(copiesLeftTextField.getText());
                Double querryRentalFee = Double.parseDouble(rentalFeeTextFiled.getText());
                String querryRentalStatus = rentalStatusChoiceBox.getValue();

                //populate the Observation
                listModelObservableList.add(new Item("I" + queryProductID + "-" + queryPublishedYear, querryTitle, querryRentalType, querryGenre, querryLoanType, queryCopiesLeft, querryRentalFee + " USD", querryRentalStatus));

                itemList.setItems(listModelObservableList);
                search();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateButton() {
        String productViewQuery = "select ProductID, Title, RentalType, Genre, LoanType, CopiesLeft, RentalFee, RentalStatus, publishedYear from product";

        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();


//            String query = "UPDATE product SET Title = '" + titleTextField.getText() + "', RentalType = '" + rentalTypeChoiceBox.getItems() + "', Genre = '" + genreChoiceBox.getItems() + "', LoanType = '" + loanTypeChoiceBox.getItems() + "', copiesLeft = '" + copiesLeftTextField.getText() + "', RentalFee = '" + rentalFeeTextFiled.getText() + "', RentalStatus = '" + rentalStatusChoiceBox.getItems() + "'";
//            executeQuery(query);

                Item selectedItem = itemList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setTitle("Edit Item");
                    dialog.setHeaderText(null);
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
                            ResultSet queryOutput = statement.executeQuery(productViewQuery);

                            String query = "UPDATE product SET Title = ?, RentalType = ?, Genre = ?, LoanType = ?, copiesLeft = ?, RentalFee = ?, RentalStatus = ?, publishedYear = ? WHERE ProductID = ?";
                            PreparedStatement ps = connectDB.prepareStatement(query);
                            ps.setString(1, titleTextField.getText());
                            ps.setString(2, rentalTypeChoiceBox.getValue());
                            ps.setString(3, genreChoiceBox.getValue());
                            ps.setString(4, loanTypeChoiceBox.getValue());
                            ps.setString(5, copiesLeftTextField.getText());
                            ps.setString(6, rentalFeeTextFiled.getText());
                            ps.setString(7, rentalStatusChoiceBox.getValue());
                            ps.setString(8,publishedYearTextField.getText());
                            ps.setString(9, selectedItem.getProductID().replaceAll("[^1-9]*([0-9]+).*", "$1"));
                            System.out.println(ps);
                            ps.execute();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("Item updated successfully!");
                            alert.showAndWait();

                            String queryProductID = selectedItem.getProductID().replaceAll("[^0-9]*([0-9]+).*", "$1");
                            String querryTitle = titleTextField.getText();
                            String querryRentalType = rentalTypeChoiceBox.getValue();
                            String querryGenre = genreChoiceBox.getValue();
                            String querryLoanType = loanTypeChoiceBox.getValue();
                            Integer queryCopiesLeft = Integer.valueOf(copiesLeftTextField.getText());
                            Double querryRentalFee = Double.valueOf(rentalFeeTextFiled.getText());
                            String querryRentalStatus = rentalStatusChoiceBox.getValue();
                            String querryPublidhedYear = publishedYearTextField.getText();


                            //populate the Observation
                            listModelObservableList.add(new Item("I" + queryProductID + "-" + querryPublidhedYear, querryTitle, querryRentalType, querryGenre, querryLoanType, queryCopiesLeft, querryRentalFee + "USD", querryRentalStatus));
                            listModelObservableList.remove(selectedItem);
                            listModelObservableList.sorted();

                        } catch (SQLException e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("An error occurred while updating the item.");
                            alert.showAndWait();
                        }
                    });
                }
        }

    @FXML
    private void deleteButton() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();
        Item selectedItem = itemList.getSelectionModel().getSelectedItem();
        if (itemList != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Item");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete " + selectedItem.getTitle() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    PreparedStatement statement = connectDB.prepareStatement("DELETE FROM product WHERE ProductID = ?");
                    statement.setString(1, selectedItem.getProductID().replaceAll("[^1-9]*([0-9]+).*", "$1"));
                    statement.executeUpdate();
                    listModelObservableList.remove(selectedItem);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        }

    }

    @FXML
    public void moveToCustomerList(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("customerListStage.fxml"));
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
            rs = statement.executeQuery("SELECT MAX(ProductID) FROM product");
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

    private void showdata() {

        DatabaseConnection connection = new DatabaseConnection();
        connection.getConnection();
        Item selectedItem = itemList.getSelectionModel().getSelectedItem();



        if (selectedItem != null) {
            String str = selectedItem.getRentalFee();
            String value = str.replaceAll("[^0-9\\.]+", "");

            titleTextField.setText(selectedItem.getTitle());
            rentalTypeChoiceBox.setValue(selectedItem.getRentalType());
            genreChoiceBox.setValue(selectedItem.getGenre());
            loanTypeChoiceBox.setValue(selectedItem.getLoanType());
            copiesLeftTextField.setText(String.valueOf(selectedItem.getCopiesLeft()));
            rentalFeeTextFiled.setText(value);
            rentalStatusChoiceBox.setValue(selectedItem.getRentalStatus());
            publishedYearTextField.setText(selectedItem.getProductID().substring(selectedItem.getProductID().length() - 4));
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
                } else if (item.getProductID().toLowerCase().contains(searchKeyword)) {
                    return true;
                }   else if(item.getCopiesLeft().toString().contains(searchKeyword)){
                    return true;
                }else{
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

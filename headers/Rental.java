package com.example.lastassessment.headers;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Rental {
    private int rentalId;
    private Item item;
    private Customer customer;
    private String status;
    private static int nextRentalID = 1;

    public Rental(Item item, Customer customer) {
        this.rentalId = nextRentalID;
        nextRentalID++;
        this.item = item;
        this.customer = customer;
    }

    public int calculateRewardPoints() {
        if (customer.getAccountStyle().equals("VIP")) {
            customer.setPoints(customer.getPoints() + 10);
        }else System.out.println("Your account is not a VIP account");
        return customer.getPoints();
    }

    public int getrentalID(){
        return rentalId;
    }

    public String getStatus(){
        return  status;
    }

    public void setStatus(String a){
        this.status = a;
    }

    public Item getItem(){
        return item;
    }
    public Customer getCustomer(){
        return customer;
    }

    public void rentItem(Connection connectDB) throws SQLException {

        if (item.getCopiesLeft().equals(0) || item.getRentalStatus().equals("not available")) {
            System.out.println("No copies of " + item.getTitle() + " are available for rent.");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("You cannot rent this item because it is not available!");
            alert.showAndWait();
            return;
        }

        if (item.getLoanType().equals("2-day") && customer.getAccountStyle().equals("Guest")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("You cannot rent this item!");
            alert.showAndWait();
            return;
        }

        if (customer.getRentals().size() >= customer.getMaxRental()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Error!");
            alert.setHeaderText("You cannot rent anymore, please return the old item for new renting!");
            alert.showAndWait();
            return;
        } else {
            // Create a new rental transaction in the database
            String rentalSql = "INSERT INTO rental(account_id, ProductID, Status) VALUES(?, ?, ?)";
            PreparedStatement psRental = connectDB.prepareStatement(rentalSql);
            psRental.setString(1, customer.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
            psRental.setString(2, item.getProductID().replaceAll("[^1-9]*([0-9]+).*", "$1"));
            psRental.setString(3, "Not Returned");
            psRental.executeUpdate();


            // Update the left copy of the item
            String updateSql = "UPDATE product SET CopiesLeft = CopiesLeft - 1 WHERE ProductID = ?";
            PreparedStatement psUpdate = connectDB.prepareStatement(updateSql);
            psUpdate.setString(1, item.getProductID().replaceAll("[^1-9]*([0-9]+).*", "$1"));
            psUpdate.executeUpdate();

            // Retrieve the rental history from the database
            String rentalHistorySql = "SELECT * FROM rental WHERE account_id = ?";
            PreparedStatement psRentalHistory = connectDB.prepareStatement(rentalHistorySql);
            psRentalHistory.setString(1, customer.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
            ResultSet rsRentalHistory = psRentalHistory.executeQuery();


            // Add the rental to the customer's list of rentals
            while (rsRentalHistory.next()) {
                Rental rental = new Rental(item, customer);
                customer.getRentals().add(rental);
                setStatus("Not Returned");
                customer.getRentalHistory().add(rental);
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Congratulation!");
            alert.setHeaderText("You have rented " + item.getTitle() + " successfully!");
            alert.showAndWait();
        }
    }

    public void returnItem(Connection connectDB) throws SQLException {
        // Update the rental transaction status in the database
        String rentalStatusSql = "UPDATE rental SET Status = ? WHERE account_id = ? AND ProductID = ?";
        PreparedStatement psRentalStatus = connectDB.prepareStatement(rentalStatusSql);
        psRentalStatus.setString(1, "Returned");
        psRentalStatus.setString(2, customer.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
        psRentalStatus.setString(3, item.getProductID().replaceAll("[^1-9]*([0-9]+).*", "$1"));
        psRentalStatus.executeUpdate();

        // Update the number of copies left for the item
        String updateSql = "UPDATE product SET CopiesLeft = CopiesLeft + 1 WHERE ProductID = ?";
        PreparedStatement psUpdate = connectDB.prepareStatement(updateSql);
        psUpdate.setString(1, item.getProductID().replaceAll("[^1-9]*([0-9]+).*", "$1"));
        psUpdate.executeUpdate();

        String plusSql = "UPDATE user_account SET returnAmount = returnAmount + 1 WHERE account_id = ?";
        PreparedStatement update = connectDB.prepareStatement(plusSql);
        update.setString(1, customer.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
        update.executeUpdate();

        if (customer.getAccountStyle().equals("Guest") && Integer.toString(customer.getReturnAmount()).equals("3")){
            String guestsql = "UPDATE user_account SET accountStyle = 'Regular', returnAmount = '0' WHERE account_id = ?";
            PreparedStatement upgradeRegular = connectDB.prepareStatement(guestsql);
            upgradeRegular.setString(1, customer.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
            upgradeRegular.executeUpdate();
        }

        if (customer.getAccountStyle().equals("Regular") && Integer.toString(customer.getReturnAmount()).equals("5")){
            String guestsql = "UPDATE user_account SET accountStyle = 'Vip', returnAmount = '0' WHERE account_id = ?";
            PreparedStatement upgradeRegular = connectDB.prepareStatement(guestsql);
            upgradeRegular.setString(1, customer.getAccount_id().replaceAll("[^1-9]*([0-9]+).*", "$1"));
            upgradeRegular.executeUpdate();
        }

        // Update the rental status of the item and remove it from the customer's list of rentals
        for (Rental rental : customer.getRentals()) {
            if (rental.getItem().getProductID().equals(item.getProductID())) {
                rental.setStatus("Returned");
                customer.getRentals().remove(rental);
                break;
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulation!");
        alert.setHeaderText("You have returned " + item.getTitle() + " successfully!");
        alert.showAndWait();
    }
}


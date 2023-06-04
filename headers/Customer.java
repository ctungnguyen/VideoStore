package com.example.lastassessment.headers;

import java.util.ArrayList;

public class Customer {
    private String account_id;
    private String name;
    private String address;
    private String phoneNumber;
    private String username;
    private String password;
    private String accountStyle;
    private Integer returnAmount;
    private int points;
    private String borrowedItem;
    private ArrayList<Rental> rentals;
    private ArrayList<Item> returnedItems;
    private ArrayList<Rental> rentalHistory;



    public ArrayList<Rental> getRentals() {
        if (rentals == null) {
            rentals = new ArrayList<Rental>();
        }
        return rentals;
    }

    public void setRentals(ArrayList<Rental> rentals) {
        if ( rentals != null) {
            this.rentals = rentals;
        }
    }

    public ArrayList<Item> getReturnedItems() {
        return returnedItems;
    }

    public void setReturnedItems(ArrayList<Item> returnedItems) {
        this.returnedItems = returnedItems;
    }

    public ArrayList<Rental> getRentalHistory() {
        if (rentalHistory == null){
            rentalHistory = new ArrayList<Rental>();
        }
        return rentalHistory;
    }

    public void setRentalHistory(ArrayList<Rental> rentalHistory) {
        if (rentalHistory != null){
        this.rentalHistory = rentalHistory;
    }}

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getBorrowedItem() {
        return borrowedItem;
    }

    public void setBorrowedItem(String borrowedItem) {
        this.borrowedItem = borrowedItem;
    }

    public String getRentedItem() {
        return rentedItem;
    }

    public void setRentedItem(String rentedItem) {
        this.rentedItem = rentedItem;
    }

    private String rentedItem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountStyle() {
        return accountStyle;
    }

    public void setAccountStyle(String accountStyle) {
        this.accountStyle = accountStyle;
    }

    public Integer getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(Integer returnAmount) {
        this.returnAmount = returnAmount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void updateAccount() {
        int successfulRentals = 0;
        for (Rental rental : rentalHistory) {
            if (rental.getStatus().equals("Returned")) {
                successfulRentals++;
            }
        }

        if (accountStyle.equals("Guest") && successfulRentals > 3) {
            accountStyle = "Regular";
            System.out.println("You have been promoted to a Regular account.");
        } else if (accountStyle.equals("Regular") && successfulRentals > 5) {
            accountStyle = "VIP";
            System.out.println("You have been promoted to a VIP account.");
        }
    }

    public int getMaxRental(){
        if (accountStyle.equals("Guest")) {
            return 2;
        }else return 100000000;
    }

    public Customer() {
    }

    public Customer(String account_id, String name, String address, String phoneNumber, String username, String password, String accountStyle) {
        this.account_id = account_id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.accountStyle = accountStyle;
    }
}

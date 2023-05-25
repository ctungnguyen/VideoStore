package com.example.lastassessment;


public class ListModel {
    Integer productID;
    String title;
    String rentalType;
    String genre;
    String loanType;
    Integer copiesLeft;
    Double rentalFee;
    String rentalStatus;

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRentalType() {
        return rentalType;
    }

    public void setRentalType(String rentalType) {
        this.rentalType = rentalType;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Integer getCopiesLeft() {
        return copiesLeft;
    }

    public void setCopiesLeft(Integer copiesLeft) {
        this.copiesLeft = copiesLeft;
    }

    public Double getRentalFee() {
        return rentalFee;
    }

    public void setRentalFee(Double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public ListModel(Integer productID, String title, String rentalType, String genre, String loanType, Integer copiesLeft, Double rentalFee, String rentalStatus) {
        this.productID = productID;
        this.title = title;
        this.rentalType = rentalType;
        this.genre = genre;
        this.loanType = loanType;
        this.copiesLeft = copiesLeft;
        this.rentalFee = rentalFee;
        this.rentalStatus = rentalStatus;
    }
}




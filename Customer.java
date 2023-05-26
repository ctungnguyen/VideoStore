import java.util.ArrayList;

public class Customer {
    private int userId;
    private String name;
    private String address;
    private String phoneNumber;
    private ArrayList<Rental> rentals;
    private String username;
    private String password;
    private String accountStyle;
    private double returnAmount;
    private int points;
    private ArrayList<Item> returnedItems;
    private ArrayList<Rental> rentalHistory;

    public Customer(int userId, String name, String address, String phoneNumber,
                    String username, String password, String accountStyle, double returnAmount, int points) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.accountStyle = accountStyle;
        this.returnAmount = returnAmount;
        this.points = points;
    }
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<Rental> getRentals() {
        return rentals;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountStyle() {
        return accountStyle;
    }

    public double getReturnAmount() {
        return returnAmount;
    }

    public int getPoints() {
        return points;
    }

    public ArrayList<Rental> getRentalHistory(){
        return rentalHistory;
    }

    public void setPoints(int a){
        points = a;
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
}

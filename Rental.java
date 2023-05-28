import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Rental {
    private Item item;
    private Customer customer;
    private String status;

    // Define the connection URL
    private static final String connectionUrl = "jdbc:mysql://localhost:3306/group;" +
            "database=yourdatabase;" +
            "user=yourusername@yourserver;" +
            "password=yourpassword";

    public Rental(Item item, Customer customer) {
        this.item = item;
        this.customer = customer;
    }

    public int calculateRewardPoints() {
        if (customer.getAccountStyle().equals("VIP")) {
            customer.setPoints(customer.getPoints() + 10);
        }else System.out.println("Your account is not a VIP account");
        return customer.getPoints();
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

    public void rentItem(Customer customer, Item item) {
        // Connect to SQL database and execute queries
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

             String sql = "INSERT INTO rentals (item_id, customer_id, status) VALUES (?, ?, ?)";
             PreparedStatement statement = connection.prepareStatement(sql);
             statement.setInt(1, item.getId());
             statement.setInt(2, customer.getId());
             statement.setString(3, "Not Returned");
             int rows = statement.executeUpdate();

            // Update the left_copy column of the items table
             sql = "UPDATE items SET left_copy = left_copy - 1 WHERE id = ?";
             statement = connection.prepareStatement(sql);
             statement.setInt(1, item.getId());
             rows = statement.executeUpdate();

            // Close the statement and the connection
             statement.close();
             connection.close();

             // Create a new Rental object and add it to the customer's rentals and rental history lists
             Rental rental = new Rental(item, customer);
             customer.getRentals().add(rental);
             customer.getRentalHistory().add(rental);

             System.out.println("You have successfully rented " + item.getTitle() + ".");
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnItem(Customer customer, Item item) {
        // Connect to SQL database and execute queries
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

            // Check if the customer has rented the item
            String selectSql = "SELECT * FROM Rental WHERE ItemID = ? AND CustomerID = ? AND Status = 'Not Returned'";
            PreparedStatement psSelect = connection.prepareStatement(selectSql);
            psSelect.setString(1, item.getId());
            psSelect.setInt(2, customer.getUserId());
            ResultSet rsSelect = psSelect.executeQuery();
            if (!rsSelect.next()) {
                System.out.println("You did not rent " + item.getTitle() + ".");
                return;
            }

            // Update the left copy of the item
            String updateSql = "UPDATE Item SET LeftCopy = LeftCopy + 1 WHERE ID = ?";
            PreparedStatement psUpdate = connection.prepareStatement(updateSql);
            psUpdate.setString(1, item.getId());
            psUpdate.executeUpdate();

            // Update the status of the rental
            String updateStatusSql = "UPDATE Rental SET Status = 'Returned' WHERE ItemID = ? AND CustomerID = ?";
            PreparedStatement psUpdateStatus = connection.prepareStatement(updateStatusSql);
            psUpdateStatus.setString(1, item.getId());
            psUpdateStatus.setInt(2, customer.getUserId());
            psUpdateStatus.executeUpdate();

            setStatus("Returned");

            System.out.println("You have successfully returned " + item.getTitle() + ".");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

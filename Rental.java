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
    private static final String connectionUrl = "jdbc:sqlserver://yourserver.database.windows.net:1433;" +
            "database=yourdatabase;" +
            "user=yourusername@yourserver;" +
            "password=yourpassword;" +
            "encrypt=true;" +
            "trustServerCertificate=false;" +
            "loginTimeout=30;";

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

            // Check if there are any copies of the item available
            String selectSql = "SELECT LeftCopy FROM Item WHERE ID = ?";
            PreparedStatement psSelect = connection.prepareStatement(selectSql);
            psSelect.setString(1, item.getId());
            ResultSet rsSelect = psSelect.executeQuery();
            int leftCopy = 0;
            if (rsSelect.next()) {
                leftCopy = rsSelect.getInt("LeftCopy");
            }

            if (leftCopy == 0) {
                System.out.println("No copies of " + item.getTitle() + " are available for rent.");
                return;
            }

            if (item.getType().equals("2-day") && customer.getAccountStyle().equals("Guest")) {
                System.out.println("Guest accounts cannot rent 2-day items.");
                return;
            }

            // Check if the customer has reached the maximum number of rentals
            String countSql = "SELECT COUNT(*) AS RentalCount FROM Rental WHERE CustomerID = ? AND Status = 'Not Returned'";
            PreparedStatement psCount = connection.prepareStatement(countSql);
            psCount.setInt(1, customer.getUserId());
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
            String updateSql = "UPDATE Item SET LeftCopy = LeftCopy - 1 WHERE ID = ?";
            PreparedStatement psUpdate = connection.prepareStatement(updateSql);
            psUpdate.setString(1, item.getId());
            psUpdate.executeUpdate();

            // Insert a new row into the rental table
            String insertSql = "INSERT INTO Rental (ItemID, CustomerID, Status) VALUES (?, ?, ?)";
            PreparedStatement psInsert = connection.prepareStatement(insertSql);
            psInsert.setString(1, item.getId());
            psInsert.setInt(2, customer.getUserId());
            psInsert.setString(3, "Not Returned");
            psInsert.executeUpdate();
            setStatus("Not Returned");

            System.out.println("You have successfully rented " + item.getTitle() + ".");
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
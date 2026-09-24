import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ActivateAdmin {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/inventory_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection connection = DriverManager.getConnection(url, "root", "dahuu");
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE users SET disabled = false WHERE LOWER(username) = LOWER('admin')");
            try (ResultSet result = statement.executeQuery("SELECT username, disabled, role FROM users WHERE LOWER(username) = LOWER('admin')")) {
                if (result.next()) {
                    System.out.println("admin status: username=" + result.getString("username")
                            + ", disabled=" + result.getBoolean("disabled")
                            + ", role=" + result.getString("role"));
                } else {
                    System.out.println("admin account was not found");
                }
            }
        }
    }
}

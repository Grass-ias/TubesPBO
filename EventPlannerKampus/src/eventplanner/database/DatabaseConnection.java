package eventplanner.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage the database connection.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_event_kampus?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "12edd3aD";

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Driver Database Tidak Ditemukan: " + e.getMessage(), "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}

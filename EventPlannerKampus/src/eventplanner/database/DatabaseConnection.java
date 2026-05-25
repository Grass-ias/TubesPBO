package eventplanner.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class to manage the database connection and initialize the tables.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    // Connection URLs: first connect to MySQL server, then initialize the database
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_event_kampus?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Ferdiana028";

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to server and create database if not exists
            try (Connection tempConn = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
                 Statement stmt = tempConn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS db_event_kampus");
            }
            
            // Now connect to the database to initialize schema
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
                initializeSchema(conn);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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

    private void initializeSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Table: Event
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tabel_event (" +
                    "id_event VARCHAR(50) PRIMARY KEY," +
                    "nama_event VARCHAR(100) NOT NULL," +
                    "total_budget DOUBLE NOT NULL," +
                    "tanggal_mulai VARCHAR(20)," +
                    "tanggal_selesai VARCHAR(20)," +
                    "waktu_mulai VARCHAR(20)," +
                    "waktu_selesai VARCHAR(20)" +
                    ")");

            // Table: Divisi
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tabel_divisi (" +
                    "id_divisi VARCHAR(50) PRIMARY KEY," +
                    "nama_divisi VARCHAR(100) NOT NULL," +
                    "allocated_budget DOUBLE NOT NULL," +
                    "id_event VARCHAR(50)," +
                    "FOREIGN KEY (id_event) REFERENCES tabel_event(id_event) ON DELETE CASCADE" +
                    ")");

            // Table: Panitia
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tabel_panitia (" +
                    "id_panitia VARCHAR(50) PRIMARY KEY," +
                    "nama_panitia VARCHAR(100) NOT NULL," +
                    "max_capacity INT NOT NULL," +
                    "current_workload INT NOT NULL DEFAULT 0," +
                    "id_event VARCHAR(50)," +
                    "FOREIGN KEY (id_event) REFERENCES tabel_event(id_event) ON DELETE CASCADE" +
                    ")");

            // Table: Tugas
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tabel_tugas (" +
                    "id_tugas VARCHAR(50) PRIMARY KEY," +
                    "nama_tugas VARCHAR(100) NOT NULL," +
                    "difficulty INT NOT NULL," +
                    "task_cost DOUBLE NOT NULL," +
                    "id_event VARCHAR(50)," +
                    "id_panitia VARCHAR(50)," +
                    "id_divisi VARCHAR(50)," +
                    "deadline VARCHAR(20)," +
                    "priority VARCHAR(20) DEFAULT 'Sedang'," +
                    "status VARCHAR(20) DEFAULT 'Direncanakan'," +
                    "completed_at VARCHAR(30)," +
                    "FOREIGN KEY (id_event) REFERENCES tabel_event(id_event) ON DELETE CASCADE," +
                    "FOREIGN KEY (id_panitia) REFERENCES tabel_panitia(id_panitia) ON DELETE SET NULL" +
                    ")");

            ensureColumn(conn, "tabel_tugas", "id_divisi", "id_divisi VARCHAR(50)");
            ensureColumn(conn, "tabel_tugas", "deadline", "deadline VARCHAR(20)");
            ensureColumn(conn, "tabel_tugas", "priority", "priority VARCHAR(20) DEFAULT 'Sedang'");
            ensureColumn(conn, "tabel_tugas", "status", "status VARCHAR(20) DEFAULT 'Direncanakan'");
            ensureColumn(conn, "tabel_tugas", "completed_at", "completed_at VARCHAR(30)");

            stmt.executeUpdate("UPDATE tabel_tugas SET status = 'Selesai', completed_at = COALESCE(completed_at, 'Data lama') " +
                    "WHERE id_panitia IS NOT NULL AND (status IS NULL OR status = 'Direncanakan')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureColumn(Connection conn, String tableName, String columnName, String definition) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet columns = metaData.getColumns(conn.getCatalog(), null, tableName, null)) {
            while (columns.next()) {
                String existingColumn = columns.getString("COLUMN_NAME");
                if (columnName.equalsIgnoreCase(existingColumn)) {
                    return;
                }
            }
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + definition);
        }
    }
}

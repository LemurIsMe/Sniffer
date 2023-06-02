package DatabaseConnectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreate {


    public static void create() {
        Connection connection = null;

        try {
            // JDBC'yi kaydet
            Class.forName("org.postgresql.Driver");

            // Veritabanina Baglan
            connection = DriverManager.getConnection(DatabaseGlobals.DB_URL, DatabaseGlobals.DB_USER, DatabaseGlobals.DB_PASSWORD);

            // Komutu gonder
            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS " + DatabaseGlobals.TABLE_NAME + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "source_ip VARCHAR(20) NOT NULL," +
                    "destination_ip VARCHAR(20) NOT NULL," +
                    "source_port INT NOT NULL," +
                    "destination_port INT NOT NULL," +
                    "protocol VARCHAR(10) NOT NULL," +
                    "timestamp TIMESTAMP NOT NULL" +
                    ")";
            statement.executeUpdate(sql);

            System.out.println("Table created successfully!");

            System.out.println("Connected to the PostgreSQL database!");

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        } finally {
            // Bağlantıyı kapa
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close the connection: " + e.getMessage());
                }
            }
        }
    }
}

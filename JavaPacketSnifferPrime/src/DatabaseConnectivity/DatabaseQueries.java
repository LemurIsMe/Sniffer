package DatabaseConnectivity;

import javafx.scene.control.TableView;

import java.sql.*;

import static DatabaseConnectivity.DatabaseGlobals.*;

public class DatabaseQueries {

    public static void insertPacketProperties(Connection connection, String sourceIp, String destinationIp,
                                              int sourcePort, int destinationPort, String protocol) throws SQLException {
        String sql = "INSERT INTO " + DatabaseGlobals.TABLE_NAME + " (source_ip, destination_ip, source_port, destination_port, protocol, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, current_timestamp);";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sourceIp);
            statement.setString(2, destinationIp);
            statement.setInt(3, sourcePort);
            statement.setInt(4, destinationPort);
            statement.setString(5, protocol);

            statement.executeUpdate();
        }
    }

    public static void populateTableFromDatabase(TableView<Packet> tableView) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT id, source_ip, destination_ip, source_port, destination_port, protocol, timestamp FROM " + TABLE_NAME;
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String sourceIP = resultSet.getString("source_ip");
                String destIP = resultSet.getString("destination_ip");
                int sourcePort = resultSet.getInt("source_port");
                int destPort = resultSet.getInt("destination_port");
                String protocol = resultSet.getString("protocol");
                String timestamp = resultSet.getString("timestamp");

                Packet packet = new Packet(id, sourceIP, destIP, sourcePort, destPort, protocol, timestamp);
                tableView.getItems().add(packet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetTable(TableView<Packet> tableView) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME;
        tableView.getItems().clear();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String sourceIP = resultSet.getString("source_ip");
                String destIP = resultSet.getString("destination_ip");
                int sourcePort = resultSet.getInt("source_port");
                int destPort = resultSet.getInt("destination_port");
                String protocol = resultSet.getString("protocol");
                String timestamp = resultSet.getString("timestamp");

                Packet packet = new Packet(id, sourceIP, destIP, sourcePort, destPort, protocol, timestamp);
                tableView.getItems().add(packet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.refresh();
    }

    public static void filterTable(TableView<Packet> tableView, String filterColumn, String filterWord) throws SQLException {

        String operator = " = '";

        switch (filterColumn){
            case "Number":
                filterColumn = "id";
                break;
            case "Source IP":
                filterColumn = "source_ip";
                break;
            case "Destination IP":
                filterColumn = "destination_ip";
                break;
            case "Source Port":
                filterColumn = "source_port";
                break;
            case "Destination Port":
                filterColumn = "destination_port";
                break;
            case "Protocol":
                filterColumn = "protocol";
                break;
            case "Timestamp":
                filterColumn = "timestamp";
                operator = " > '";
                System.out.println(filterWord);
        }

        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + filterColumn + operator + filterWord + "';";
        tableView.getItems().clear();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String sourceIP = resultSet.getString("source_ip");
                String destIP = resultSet.getString("destination_ip");
                int sourcePort = resultSet.getInt("source_port");
                int destPort = resultSet.getInt("destination_port");
                String protocol = resultSet.getString("protocol");
                String timestamp = resultSet.getString("timestamp");

                Packet packet = new Packet(id, sourceIP, destIP, sourcePort, destPort, protocol, timestamp);
                tableView.getItems().add(packet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.refresh();
    }


}


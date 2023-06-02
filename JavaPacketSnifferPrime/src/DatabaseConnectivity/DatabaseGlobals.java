package DatabaseConnectivity;

public class DatabaseGlobals {
    public static String DB_URL = "jdbc:postgresql://localhost:5432/test_network";
    public static String DB_USER = "postgres";
    public static String DB_PASSWORD = "root";

    public static String deviceName = "Unknown_Source";
    public static int deviceToUse = 0;
    public static String TABLE_NAME = "network_packets_"+ deviceName;
}

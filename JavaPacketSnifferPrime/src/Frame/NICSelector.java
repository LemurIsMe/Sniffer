package Frame;

import DatabaseConnectivity.DatabaseCreate;
import DatabaseConnectivity.DatabaseGlobals;
import DatabaseConnectivity.DatabaseQueries;
import DatabaseConnectivity.Packet;
import Sniffing.RetrieveNICInfo;
import Sniffing.SnifferMain;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class NICSelector extends Application {

    Thread thread = null;
    private TableView<NICCard> nicTable;
    private ComboBox<String> nicComboBox;
    private Button confirmButton;

    @Override
    public void start(Stage primaryStage) throws Exception {


        // NIC Tablosu burada yaratiliyor
        nicTable = new TableView<>();
        TableColumn<NICCard, Integer> numberCol = new TableColumn<>("No");
        numberCol.prefWidthProperty().bind(nicTable.widthProperty().multiply(0.04));
        TableColumn<NICCard, String> nameCol = new TableColumn<>("Name");
        nameCol.prefWidthProperty().bind(nicTable.widthProperty().multiply(0.4));
        TableColumn<NICCard, String> locationCol = new TableColumn<>("Location");
        locationCol.prefWidthProperty().bind(nicTable.widthProperty().multiply(0.55));

        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        nicTable.getColumns().addAll(numberCol, nameCol, locationCol);

        // Create the NIC combo box and confirm button
        nicComboBox = new ComboBox<>();
        confirmButton = new Button("Confirm");
        HBox bottomBox = new HBox(nicComboBox, confirmButton);
        bottomBox.setSpacing(10);
        bottomBox.setPadding(new Insets(10));

        // Layout'u yaratip elementleri ustune ekle
        BorderPane root = new BorderPane();
        root.setCenter(nicTable);
        root.setBottom(bottomBox);

        // ekrani ayarla
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // NIC tablosunu doldur
        populateNICTable();



        confirmButton.setOnAction(event -> {
            FrameGlobals.selectedNIC = nicComboBox.getValue();
            System.out.println(FrameGlobals.selectedNIC);
            ObservableList<NICCard> nicData = nicTable.getItems();


            // secili karti bulana kadar don
            for (NICCard nicCard : nicData) {
                if (nicCard.getName().equals(FrameGlobals.selectedNIC)) {
                    FrameGlobals.selectedLocation = nicCard.getLocation();
                    DatabaseGlobals.deviceToUse = nicCard.getNumber() - 1;
                    System.out.println(DatabaseGlobals.deviceToUse);
                    DatabaseGlobals.deviceName = nicCard.getName();
                    System.out.println(DatabaseGlobals.deviceName);
                    DatabaseGlobals.TABLE_NAME = "network_packets_"+ DatabaseGlobals.deviceToUse;
                    System.out.println(DatabaseGlobals.TABLE_NAME);
                    DatabaseCreate.create();
                    break;
                }
            }

            //Tabloyu yarat
            TableView<Packet> packetTable = new TableView<>();

            if (FrameGlobals.selectedLocation != null) {
                // Create a new JavaFX window for displaying captured network packets
                Stage packetStage = new Stage();
                packetStage.setTitle(DatabaseGlobals.deviceName + " as " + DatabaseGlobals.TABLE_NAME);


                // Paketler icin tablo olustur
                TableColumn<Packet, Integer> noCol = new TableColumn<>("Number");
                noCol.prefWidthProperty().bind(packetTable.widthProperty().multiply(0.07));
                TableColumn<Packet, String> sourceIpCol = new TableColumn<>("Source IP");
                sourceIpCol.prefWidthProperty().bind(packetTable.widthProperty().multiply(0.17));
                TableColumn<Packet, String> destIpCol = new TableColumn<>("Destination IP");
                destIpCol.prefWidthProperty().bind(packetTable.widthProperty().multiply(0.17));
                TableColumn<Packet, String> sourcePortCol = new TableColumn<>("Source Port");
                sourcePortCol.prefWidthProperty().bind(packetTable.widthProperty().multiply(0.17));
                TableColumn<Packet, String> destPortCol = new TableColumn<>("Destination Port");
                destPortCol.prefWidthProperty().bind(packetTable.widthProperty().multiply(0.17));
                TableColumn<Packet, String> protocolCol = new TableColumn<>("Protocol");
                protocolCol.prefWidthProperty().bind(packetTable.widthProperty().multiply(0.07));
                TableColumn<Packet, String> timestampCol = new TableColumn<>("Timestamp");
                timestampCol.prefWidthProperty().bind(packetTable.widthProperty().multiply(0.18));

                // Datayi respektif kolonlara bagla
                noCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                sourceIpCol.setCellValueFactory(new PropertyValueFactory<>("sourceIp"));
                destIpCol.setCellValueFactory(new PropertyValueFactory<>("destIp"));
                sourcePortCol.setCellValueFactory(new PropertyValueFactory<>("sourcePort"));
                destPortCol.setCellValueFactory(new PropertyValueFactory<>("destPort"));
                protocolCol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
                timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

                // Kolonlari ekrana ekle
                packetTable.getColumns().addAll(noCol, sourceIpCol, destIpCol, sourcePortCol, destPortCol, protocolCol, timestampCol);


                //Kaydet
                TextField fileNameField = new TextField();

                Button saveButton = new Button("Save");

                saveButton.setOnAction(event4 -> {
                    String fileNameTemp = fileNameField.getText() + ".xss";
                    System.out.println(fileNameTemp);
                });

                Button switchButton = new Button("Switch");
                switchButton.setStyle("-fx-background-color: green;");


                switchButton.setOnAction(e -> {
                    if (switchButton.getStyle().equals("-fx-background-color: red;")) {
                        // çalışıyorsa thread'i durdur
                        if (thread != null && thread.isAlive()) {
                            thread.interrupt();
                        }
                        switchButton.setStyle("-fx-background-color: green;");
                    } else {
                        // thread'i başlat
                        thread = new Thread(() -> {
                            while (!Thread.currentThread().isInterrupted()) {
                                System.out.println("Thread is running...");
                                try {
                                    SnifferMain.sniffer();
                                    DatabaseQueries.populateTableFromDatabase(packetTable);
                                } catch (SQLException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        });
                        thread.start();
                        switchButton.setStyle("-fx-background-color: red;");
                    }
                });

//                switchButton.setOnAction(e -> {
//                    if (switchButton.getStyle().equals("-fx-background-color: red;")) {
//                        switchButton.setStyle("-fx-background-color: green;");
//                        DatabaseQueries.populateTableFromDatabase(packetTable);
//                    } else {
//                        switchButton.setStyle("-fx-background-color: red;");
//                        System.out.println("Started to capture");
//                        try {
//                            SnifferMain.sniffer();
//                        } catch (SQLException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                    }
//                });


                Button resetButton = new Button("Reset");
                resetButton.setOnAction(event3 -> {
                    try {
                        DatabaseQueries.resetTable(packetTable);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Filtre alanini yarat
                TextField filterTextField = new TextField();


                ComboBox<String> columnComboBox = new ComboBox<>();
                columnComboBox.getItems().addAll("Number", "Source IP", "Destination IP", "Source Port", "Destination Port", "Protocol", "Timestamp");
                columnComboBox.getSelectionModel().selectFirst();

                // filtre butonunu yarat
                Button filterButton = new Button("Filter");
                filterButton.setOnAction(event2 -> {

                    String filterWord = filterTextField.getText();
                    System.out.println(filterWord);
                    String filterColumn = columnComboBox.getValue();
                    System.out.println(filterColumn);

                    try {
                        DatabaseQueries.filterTable(packetTable, filterColumn, filterWord);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });



                HBox filterBox = new HBox(10, columnComboBox, filterTextField, filterButton, resetButton);

                HBox filterBox2 = new HBox(10, switchButton, fileNameField, saveButton);

                // filtre butonlarini hizala
                VBox packetLayout = new VBox(20, filterBox, packetTable, filterBox2);

                packetLayout.setMaxWidth(Double.MAX_VALUE);
                packetLayout.setMaxHeight(Double.MAX_VALUE);
                VBox.setVgrow(packetTable, Priority.ALWAYS);

                packetLayout.setPadding(new Insets(10));

                // Pencere yaratılıp kullanılmak üzere belirleniyor
                Scene packetScene = new Scene(packetLayout, 1200, 800);
                packetStage.setScene(packetScene);
                packetStage.show();
            } else {
                System.out.println("No location found for the selected NIC.");
            }
            //                SnifferMain.sniffer();
            DatabaseQueries.populateTableFromDatabase(packetTable);
        });

    }

    private void populateNICTable() {
        List<NICCard> nicCards = RetrieveNICInfo.listNICs();
        ObservableList<NICCard> nicData = FXCollections.observableArrayList(nicCards);
        nicTable.setItems(nicData);

        ObservableList<String> descriptions = FXCollections.observableArrayList();
        for (NICCard nicCard : nicCards) {
            descriptions.add(nicCard.getName());
        }
        nicComboBox.setItems(descriptions);
        nicComboBox.getSelectionModel().selectFirst();
    }

    public static void main(String[] args) {
        launch(args);
    }


    // NIC kartlari icin obje olustur
    public static class NICCard {

        private int number;
        private String name;
        private String location;

        public NICCard(int number, String name, String location) {
            this.number = number;
            this.name = name;
            this.location = location;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}

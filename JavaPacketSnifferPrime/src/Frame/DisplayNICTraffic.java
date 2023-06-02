package Frame;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DisplayNICTraffic extends Application {

    private ToggleButton captureToggle;
    private Label packetInfoLabel;
    private ComboBox<String> filterColumnComboBox;
    private TextField filterTextField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Packet Capture");

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20));

        GridPane gridPane = createGridPane();
        addFilter(gridPane);
        addCaptureToggle(gridPane);
        addPacketInfoLabel(gridPane);

        borderPane.setTop(gridPane);

        Scene scene = new Scene(borderPane, 1200, 600);
        scene.setFill(Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        return gridPane;
    }

    private void addFilter(GridPane gridPane) {
        filterColumnComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Number", "Timestamp", "Source IP", "Destination IP", "Protocol Type"));
        filterColumnComboBox.setPromptText("Select Column");
        filterColumnComboBox.setPrefWidth(150);

        filterTextField = new TextField();
        filterTextField.setPromptText("Enter Filter Text");
        filterTextField.setPrefWidth(150);

        Button applyFilterButton = new Button("Apply Filter");
        applyFilterButton.setOnAction(event -> applyFilter());

        gridPane.add(filterColumnComboBox, 0, 0);
        gridPane.add(filterTextField, 1, 0);
        gridPane.add(applyFilterButton, 2, 0);
    }

    private void addCaptureToggle(GridPane gridPane) {
        captureToggle = new ToggleButton("Start Capture");
        captureToggle.setOnAction(event -> {
            if (captureToggle.isSelected()) {
                startCapture();
                captureToggle.setText("Stop Capture");
            } else {
                stopCapture();
                captureToggle.setText("Start Capture");
            }
        });
        gridPane.add(captureToggle, 0, 1);
        GridPane.setColumnSpan(captureToggle, 3);
    }

    private void addPacketInfoLabel(GridPane gridPane) {
        packetInfoLabel = new Label("Packet information will be displayed here");
        packetInfoLabel.setFont(Font.font(14));
        packetInfoLabel.setWrapText(true);
        gridPane.add(packetInfoLabel, 0, 2, 3, 1);
        GridPane.setColumnSpan(packetInfoLabel, 3);
    }

    private void applyFilter() {
        String selectedColumn = filterColumnComboBox.getValue();
        String filterText = filterTextField.getText();
        packetInfoLabel.setText("Filter Applied: Column - " + selectedColumn + ", Text - " + filterText);
    }

    private void startCapture() {
        // placeholder
    }

    private void stopCapture() {
        // placeholder
    }

    public static void main(String[] args) {
        launch(args);
    }
}

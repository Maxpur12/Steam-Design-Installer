package app.steam.design;

/**
 * Created by Max on 31.07.2017.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.io.*;


public class Admin extends Application {
    TableView<Design> table = new TableView<Design>(); //Tabelle um Design Informationen einzutragen

    //Liste um Designs in der Tabelle zu speichern
    private final ObservableList<Design> data = FXCollections.observableArrayList(
          //  new Design("Bing", "Bong")
  );

    public Admin(){
    }

    public void start(Stage primaryStage){
        VBox center = new VBox();
        HBox bottom = new HBox();
        table.setEditable(true);
        //--------------- Tabbellenköpfe ------------------
        TableColumn<Design, String> designName = new TableColumn<>("Design");
        designName.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        designName.setCellValueFactory(new PropertyValueFactory<>("designName"));

        designName.setCellFactory(TextFieldTableCell.<Design>forTableColumn());
        designName.setOnEditCommit(
                (TableColumn.CellEditEvent<Design, String> t) -> {
                    ((Design) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setDesignName(t.getNewValue());
                }
        );

        TableColumn<Design, String> designURL = new TableColumn<>("URL");
        designURL.prefWidthProperty().bind(table.widthProperty().multiply(0.7));
        designURL.setCellValueFactory(new PropertyValueFactory<>("urlLink"));

        designURL.setCellFactory(TextFieldTableCell.<Design>forTableColumn());
        designURL.setOnEditCommit(
                (TableColumn.CellEditEvent<Design, String> t) -> {
                    ((Design) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setUrlLink(t.getNewValue());
                }
        );

        table.getColumns().addAll(designName, designURL);
        table.setItems(data);
        // -----------------------------------------------------------------
        //Eintragen der Daten
        final TextField addDesign = new TextField();
        addDesign.setPromptText("Designname");
        addDesign.prefWidthProperty().bind(designName.widthProperty().multiply(0.5));
        final TextField addURL = new TextField();
        addURL.setPromptText("URL");
        addURL.prefWidthProperty().bind(designURL.widthProperty().multiply(0.7));


        //Hinzufügen der Daten in die Liste die dann von der Tabelle angezeigt werden
        final Button addButton = new Button("Hinzufügen");
        addButton.setOnAction(event -> {
            data.add(new Design(addDesign.getText(), addURL.getText()));
            addDesign.clear();
            addURL.clear();
        });
        /**
         * Löschen der Daten in der Liste, wird nicht gespeichert
         */
        final Button deleteButton = new Button("Löschen");
        deleteButton.setOnAction(event -> {
            Design selectedItem = table.getSelectionModel().getSelectedItem();
            table.getItems().remove(selectedItem);
        });
        /**
         * Speichern der Tabelle (die in der Liste steht) in einer Datei
         * Daten werden Spalte für Spalte in die Datei geschrieben, 2 Zeilen in der Datei stellen 1 Zeile in der Tabelle dar
         */
        final Button speichern = new Button("Speichern");
        speichern.setOnAction(event -> {
            try {
                final File datei = new File("design.txt");
                final FileWriter fwriter = new FileWriter(datei);
                final BufferedWriter writer = new BufferedWriter(fwriter);
                for(int i = 0; i < data.size(); i++){
                        writer.write(data.get(i).getDesignName());
                        writer.newLine();
                        writer.write(data.get(i).getUrlLink());
                        writer.newLine();
                }
                writer.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        });
        /**
         * Daten werden aus der Datei gelesen
         * umkehrung der Speicherung
         * Selber Code wird in "Main" verwendet
         */
        final Button laden = new Button("Laden");
        laden.setOnAction(event -> {
            table.getItems().clear();
            LadeDatei(data);
        });

        primaryStage.setTitle("Designliste anpassen");
        primaryStage.setResizable(false);

        BorderPane root = new BorderPane();

        bottom.setPadding(new Insets(10, 10, 10, 10));
        bottom.setSpacing(5);
        root.setCenter(center);
        root.setBottom(bottom);

        center.getChildren().addAll(table);
        bottom.getChildren().addAll(addDesign, addURL, addButton, deleteButton, speichern, laden);
        root.getChildren().addAll();

        primaryStage.setScene(new Scene(root, 850, 400));
        primaryStage.show();
    }

    /**
     * Laden der Datei mir gespeicherten Inhalten
     * @param data
     */
    public static void LadeDatei(ObservableList<Design> data){

        try{
            String name, url;
            final File datei = new File("design.txt");
            final FileReader reader = new FileReader(datei);
            final BufferedReader buffreader = new BufferedReader(reader);
            String zeile = null;
            while((zeile = buffreader.readLine()) != null){
                name = zeile;
                url = buffreader.readLine();
                data.add(new Design(name, url));
            }
            buffreader.close();
        }
        catch(Exception e){
            Design.DesignError(e);
        }
    }


}

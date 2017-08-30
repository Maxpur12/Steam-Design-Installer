package app.steam.design;

        import app.steam.web.OpenBrowser;
        import javafx.application.Application;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.geometry.Insets;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.scene.layout.BorderPane;
        import javafx.scene.layout.HBox;
        import javafx.scene.layout.VBox;
        import javafx.scene.paint.Color;
        import javafx.stage.Stage;

        import java.io.File;

/**
 * @author Max
 */
public class Main extends Application {
    private String version = "0.3.0";

    public Design aktuell = new Design(null, null);
    private File designDatei = new File("design.txt");

    public static ProgressBar pb = new ProgressBar(0.0);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

       final ListView<Design> list = new ListView<Design>();
        ObservableList<Design> design = FXCollections.observableArrayList(

        );
        list.setItems(design);
        list.getSelectionModel()
                                .selectedItemProperty()
                                .addListener((observable, oldValue, newValue) -> aktuell = new Design(newValue.getDesignName(), newValue.getUrlLink()));
        //Designliste laden falls vorhanden
        if(designDatei.exists()) {
            if (list.getItems().isEmpty()) {
                Admin.LadeDatei(design);
            }
        }

        final Button laden = new Button("Designliste aktualisieren");
        laden.setOnAction(event -> {

                list.getItems().clear();
                Admin.LadeDatei(design);
        });

        /**
         * Fenster für Administrator zum erstellen von Downloadfile
         */
        Button admin = new Button("Designliste anpassen");
        admin.setOnAction(event -> {
            Stage stage = new Stage();
            Admin fensterAdmin = new Admin();
            fensterAdmin.start(stage);
        });
        /**
         * Installation
         */

        pb.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        Button install = new Button("Installieren");
            install.setOnAction(event -> {

                // System.out.println(cb.getValue());
                System.out.println(aktuell.getUrlLink());
              //  aktuell.Install(aktuell.getUrlLink());
                aktuell.start();  //Als Thread Starten
               // System.out.println(aktuell.getInstallPath());

            });

        /**
         * WebBrowser um Steam Herungerzuladen
         */

        Button webButton = new Button("Steam herunterladen");
        webButton.setOnAction(event -> {
            Stage stage = new Stage();
            OpenBrowser webSteam = new OpenBrowser("http://store.steampowered.com/about/", "Steam herunterladen");
            webSteam.start(stage);
        });

        Label versionInfo = new Label("Version: " + version);
        Label bsInfo = new Label(System.getProperty("os.name") + " wurde erkannt");


        Label error = new Label();
        if(!System.getProperty("os.name").contains("Windows")){
            install.setDisable(true);
            // cb.setDisable(true);
            error.setText(System.getProperty("os.name") + " wird nicht unterstützt!");
            error.setTextFill(Color.RED);
        }

        BorderPane root = new BorderPane();

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5, 5, 5, 10));
        vbox.setSpacing(5);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 10, 0, 10));
        hbox.setSpacing(10);

        root.setTop(hbox);
        root.setCenter(vbox);


        hbox.getChildren().addAll(bsInfo);
        //vbox.getChildren().addAll(versionInfo,error, cb, install);
        vbox.getChildren().addAll(versionInfo,error, admin,  list, install, laden, pb, webButton);

        Scene scene = new Scene(root, 250, 300);
        primaryStage.setTitle("Steam Design Installer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}

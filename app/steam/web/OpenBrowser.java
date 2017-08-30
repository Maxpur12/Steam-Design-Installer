package app.steam.web;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class OpenBrowser extends Application {

    private String url;
    private String title;


    public OpenBrowser(String url, String title){
        this.url = url;
        this.title = title;
    }

    public void start (Stage primaryStage){

        final Button downloadWindows = new Button("Download für Windows");
        final Button downloadLinux = new Button("Download für Linux");
        final Button downloadMacOSX = new Button("Download für MacOSX");

        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load(url);

        downloadWindows.setOnAction(event -> DownloadFile.Download("https://steamcdn-a.akamaihd.net/client/installer/SteamSetup.exe","SteamSetup.exe"));

        downloadLinux.setDisable(true);
        downloadLinux.setOnAction(event -> DownloadFile.Download("https://steamcdn-a.akamaihd.net/client/installer/steam.deb", "steam_latest.deb"));

        downloadMacOSX.setDisable(true);
        downloadMacOSX.setOnAction(event -> DownloadFile.Download("https://steamcdn-a.akamaihd.net/client/installer/steam.dmg","steam.dmg"));

        BorderPane borderPane = new BorderPane();

        HBox top = new HBox();
        top.setSpacing(10);
        top.getChildren().addAll(downloadWindows, downloadLinux, downloadMacOSX);


        borderPane.setTop(top);
        borderPane.setCenter(browser);

        Scene scene = new Scene(borderPane, 1200,800);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}

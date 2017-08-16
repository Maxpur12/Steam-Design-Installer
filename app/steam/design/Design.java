package app.steam.design;

/**
 * Created by Max on 31.07.2017.
 */

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.application.Platform;

public class Design extends Thread {

    private String designName;
    private URL url;
    private String urlLink;
    private String link = "";
    private String installPath;

    public static double PROGRESS = 0.0;
  //  private String _skin = null;

    public Design(String designName, String urlLink) {
        this.designName = designName;
        this.urlLink = urlLink;
    }

    public  String getDesignName() {
        return designName;
    }


    public String getUrlLink() {
        return urlLink;
    }

    public void setDesignName(String designName ){
        this.designName = designName;
    }

    public void setUrlLink(String urlLink){
        this.urlLink = urlLink;
    }

    /**
     * Ausgewähltes Design wird heruntergeladen
     * Je nach verwendeten Betriebssystem, wird die Installation anders ausgeführt
     */
    public void Install(String url1) {
        try {
            PROGRESS = 0.0;
            Main.pb.setProgress(PROGRESS);

            FileUtils.deleteDirectory(new File("ziel/"));
            url = new URL(url1);

            System.out.println(url.getProtocol()); //Debugging

            File dest = new File("ziel/designdownload.zip"); //Ordner und Ausgabedatei f�r Download definieren

            HttpURLConnection con = (HttpURLConnection) url.openConnection();	//Verbindung herstellen

            con.setRequestProperty("REFERER", link);

            FileUtils.copyInputStreamToFile(con.getInputStream(), dest);  //Datei herunterladen und in Ausgabedatei speichern

            PROGRESS = 0.25;
            Main.pb.setProgress(PROGRESS);
            if(System.getProperty("os.name").contains("Windows")){
                InstallWindows();
            }


        } catch (Exception e) {
           DesignError(e);


        }
    }

    public String getInstallPath() {
        return installPath;
    }

    public String toString() {
        return designName;
    }

    /**
     * Wenn Windows als Betriebssystem ausgewählt wird
     * 1.Registrypfad Pfad der Steam Installation finden, 2 Pfade sind möglich
     * 2.Wenn Registrywert "SkinV4" nicht existiert, "SkinV4" Wert erstellen; Nur Notwendig wenn noch nie ein Skin vorher installiert wurde. Muss extra gemacht werden, hat in Tests nicht anders funktioniert
     * 3.Heruntergeladenes ZIPFile entpacken, dafür wurde "Zip4J" verwendet, da leicht nutzbar
     * 4.Entpackter Ordner wird nach Zielwort (DesignName) durchsucht, fehleranfällig
     * wenn entpackter Ordner anders heißt. EDIT: Da Downloadordner immer neu erstellt
     * wird und immer nur ein Design Ordner vorhanden ist, ist Ordnername = fileDesignName
     * 5.Design in Steam Ordner verschieben und Registrywert ändern
     * 6.Heruntergeladenen Inhalt löschen
     */
    private void InstallWindows(){

        if (System.getenv("ProgramFiles(x86)") != null) {  //Installationspfad �ber Registry finden
            installPath = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "Software\\Wow6432Node\\Valve\\Steam", "InstallPath");  //64 Bit
        } else {
            installPath = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "Software\\Valve\\Steam", "InstallPath");	//32 Bit
        }


        if(!Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam", "SkinV4")){	//Wenn Registry Eintrag nicht existert
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam", "SkinV4", designName);	//Erstelle Registryeintrag
        }

        PROGRESS = 0.5;
        Main.pb.setProgress(PROGRESS);
       // System.out.println( "Skin: " +_skin);	//Debuggen

        try {

            final ZipFile zip = new ZipFile("ziel/designdownload.zip");	//Zu entpackene Datei ausw�hlen

            zip.extractAll("ziel");	//Alle Daten aus dem Zip Archiv in Ornder "ziel" entpacken

            File f = new File("ziel"); //Ordner
            File f2 = new File("ziel/" + designName); //Ordner + neuer Ordnername
            File[] fileArray = f.listFiles();
            if (fileArray != null) {
                for (int a = 0; a < fileArray.length; a++) {
                    if (fileArray[a].isDirectory()) {
                        System.out.println(fileArray[a].getName());
                        String fileDesignName = fileArray[a].getName();
                        if (fileArray[a].getName().contains(fileDesignName)) {		//Ordner nach Designname durchsuchen
                            fileArray[a].renameTo(f2);							//Ordner umbenennen
                        }
                    }
                }
                PROGRESS = 0.75;
                Main.pb.setProgress(PROGRESS);

                FileUtils.copyDirectory(new File("ziel/" + designName), new File(String.valueOf(installPath) + "/skins/" + designName)); //Skin Ordner in Steam Ordner kopieren
                Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam", "SkinV4", designName);	//Registryeintrag �ndern
                FileUtils.deleteDirectory(new File("ziel/"));	//Ordner l�schen
              //  installDone("Das ausgewählte Design ist nun Installiert. Starten Sie STEAM neu.");
                PROGRESS = 1.0;
                Main.pb.setProgress(PROGRESS);
            }
        } catch (Exception e) {
            DesignError(e);
        }
    }

    /**
     * Fehleranzeige über Alert Dialog
     * @param e
     */

    public static void DesignError(Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Da gab es wohl einen Fehler ¯\\_(ツ)_/¯ ");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        //expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

// Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();

    }

    private void installDone(String doneText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Done");
        alert.setHeaderText("DONE");
        alert.setContentText(doneText);
        alert.show();
    }
    /**
     * Als Thread Paralelisieren
     * UI Thread muss über Platform.runLater() erfolgen sonst --> Fehler
     */
    public void run(){
        Install(getUrlLink());

        Platform.runLater(() -> {
            installDone("Das ausgewählte Design ist nun Installiert. Starten Sie STEAM neu.");
        });

    }

    public void InstallLinux(){

    }

    public void InstallMacOSX(){

    }
}


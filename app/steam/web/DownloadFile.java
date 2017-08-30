package app.steam.web;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile {

    public  static void Download(String url1, String downloadName) {

        URL url;
        try {
            /*-----------------------------*/


            /*-----------------------------*/
            //FileUtils.deleteDirectory(new File(downloadName));,
            FileUtils.deleteQuietly(new File(downloadName));
            url = new URL(url1);

            System.out.println(url.getProtocol()); //Debugging

            File dest = new File(downloadName); //Ordner und Ausgabedatei f�r Download definieren

            HttpURLConnection con = (HttpURLConnection) url.openConnection();	//Verbindung herstellen

            con.setRequestProperty("REFERER", "");

            FileUtils.copyInputStreamToFile(con.getInputStream(), dest);  //Datei herunterladen und in Ausgabedatei speichern
            /*-----------------------------*/

            /*-----------------------------*/
            Runtime.getRuntime().exec(downloadName);

        } catch (Exception e) {
            app.steam.design.Design.DesignError(e);


        }
    }


}

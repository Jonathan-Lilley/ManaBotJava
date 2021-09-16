/**                                                   DOWNLOAD MANAGER                                                */
/**
 * This file manages downloading and updating the database
 *
 */

//package ManaBot.managers;
package ManaTests.Managers;

// import jdk.incubator.http.HttpClient;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;

public class DownloadManager {

    Dotenv dotenv = Dotenv.load()
            .directory("../")
            .load();
    
    private String dataHashUrl = dotenv.get("DATA_HASH_URL");
    private String dataUrl = dotenv.get("DATA_URL");
    private String hashPath = dotenv.get("HASH_PATH");
    private String newHash;
    private String oldHash;

    private void checkUpdate() {
        System.out.println("Getting old hash...");
        Scanner old = new Scanner(FileInputStream(hashPath));
        oldHash = old.hasNext() ? old.next() : "";

        System.out.println("Getting new hash...");
        URL url = new URL(dataHashUrl);
        Scanner urlscan = new Scanner(url.openStream()).useDelimiter("\\A");
        newHash = urlscan.hasNext() ? urlscan.next() : "";

        if(oldHash != newHash) {
            System.out.println("Hashes different\nUpdating...");
            BufferedWriter writeHash = new BufferedWriter(new FileWriter(hashPath));
            writeHash.write(newHash);
            writer.close();
            System.out.println("Updated...");
        }
        System.out.println("Done!");
    }
}
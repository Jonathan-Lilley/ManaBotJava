package Helpers;

//import Helpers.JSONCardParser;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URL;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


public class Downloader {
    final String hashFile = "./src/main/resources/hashfile.txt";
    final String dataFile = "./src/main/resources/AllPrintings.json";
    final String imageDir = "./src/main/resources/CardImages/";
    final String dataUrl = "https://mtgjson.com/api/v5/AllPrintings.json";
    final String picURL1 = "https://gatherer.wizards.com/Handlers/Image.ashx?name=";
    // insert cardname with spaces as %20 between these two strings;
    final String picURL2 = "&type=card";
    String dataHash;
    private boolean updateSuccessful = false;

    public Downloader() {
        this.startDownload(false);
    }

    public Downloader(boolean clearHash) {
        this.startDownload(clearHash);
    }

    private void startDownload(boolean clearHash) {

        // make sure resources path exists
        File resourcePath = new File("./src/main/resources/");
        if(!resourcePath.exists()) {
            resourcePath.mkdir();
        }
        // Initialize stuff
        //JSONCardParser parser;


        if(clearHash) {
            // clear hash
            // doing this so we can test the process, redo it every time
            try {
                this.clearHash();
            } catch (FileNotFoundException e) {
                System.out.println("File NOt Found exception caught clearing hash");
            } catch (IOException e) {
                System.out.println("IO exception caught clearing hash");
            }
            System.out.println("Hash cleared. Running mandatory update.");
        }
        else {
            System.out.println("Hash not cleared. Only updating if necessary.");
        }

        // update hash and data
        try {
            this.updateHash();
            this.updateSuccessful = true;
            //parser = new JSONCardParser(dataFile);

            /*

            // **Test image updater later**

            if(this.updateStatus.get("attempted")) {
                // update all the images
                try {
                    this.updateImages(parser);
                } catch (Exception e) {
                    System.out.println("Updating images failed");
                }
            }
            */
        }
        catch(MalformedURLException e) {
            System.out.println("URL exception caught updating hash");
        }
        catch(FileNotFoundException e) {
            System.out.println("File Not Found exception caught updating hash");
        }
        catch(IOException e){
            System.out.println("IO exception caught updating hash");
        }

        System.out.println("Downloader complete.");

    }

    public boolean getUpdateStatus() {
        return this.updateSuccessful;
    }

    // Delete the hash so we can start over and repeat testing
    private void clearHash() throws IOException {
        FileWriter writeHash = new FileWriter(hashFile);
        writeHash.write("None");
        writeHash.close();
    }

    private void updateHash() throws IOException {
        try {
            Scanner URLScan = new Scanner(new URL(dataUrl + ".sha256").openStream());
            if(!new File(hashFile).exists()) {
                FileWriter hashF = new FileWriter(hashFile);
                hashF.write("None");
            }
            Scanner hashFScan = new Scanner(new File(hashFile));
            String hashURLScanned = URLScan.hasNext() ? URLScan.next() : "";
            String hashFScanned = hashFScan.hasNext() ? hashFScan.next() : "";
            if(!hashURLScanned.equals(hashFScanned)) {
                System.out.println("Hash update found. Running update...");
                try {
                    FileWriter writeHash = new FileWriter(hashFile);
                    writeHash.write(hashURLScanned);
                    writeHash.close();
                    try {
                        this.downloadJSON();
                    }
                    catch(Exception e) {
                        System.out.println("Error caught: updating json");
                    }
                }
                catch(IOException e) {
                    System.out.println("Error caught: can't write file");
                }
            }
        }
        catch(MalformedURLException e) {
            System.out.println("Error caught: failed to get new hash URL");
        }
        Scanner hashF = new Scanner(new File(hashFile));
        dataHash =  hashF.hasNext() ? hashF.next() : "";
    }


    private void downloadJSON() throws IOException {
        // Download the zip as bytes
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(dataUrl+".zip").openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(dataFile+".zip");
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (MalformedURLException e) {
            System.out.println("URL exception");
        } catch (IOException e){
            System.out.println("IO exception");
        }
        System.out.println("Successfully downloaded JSON.");
        // Unzip
        try {
            ZipFile zipFile = new ZipFile(dataFile+".zip");
            zipFile.extractAll("./src/main/resources/");
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    // a later project
    /*



    private void updateImages(JSONCardParser parser) throws MalformedURLException, IOException {
        try {
            JSONObject cardSets = parser.parseJSON(dataFile);
            Iterator<String> cardSetsKeys = cardSets.keySet().iterator();
            while(cardSetsKeys.hasNext()) {
                String cardSetKey = cardSetsKeys.next();
                JSONObject allCardSets = (JSONObject)cardSets.get(cardSetKey);
                Iterator<String> cardKeys = allCardSets.keySet().iterator();
                while(cardKeys.hasNext()) {
                    String cardKey = cardKeys.next();
                    File cardDir = new File(cardSetKey);
                    if (!cardDir.exists()){
                        cardDir.mkdirs();
                    }
                    try {
                        URL imageUrl = new URL(picURL1+cardKey+picURL2);
                        InputStream inImage = new BufferedInputStream(imageUrl.openStream());
                        ByteArrayOutputStream outImage = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int n = 0;
                        while (-1!=(n=inImage.read(buf))) {
                            outImage.write(buf, 0, n);
                        }
                        outImage.close();
                        inImage.close();
                        byte[] response = outImage.toByteArray();
                        FileOutputStream fos = new FileOutputStream(cardSetKey+"/"+cardKey+".jgp");
                        fos.write(response);
                        fos.close();
                    } catch(IOException e) {
                        System.out.println("fffff");
                    }
                }
            }
        }
        catch(Exception e) {
            System.out.println("IM TIRED OF FORCED EXCEPTION HANDLING, FUCK");
        }
    }
    */
}

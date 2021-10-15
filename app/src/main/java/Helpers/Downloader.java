package Helpers;

import Helpers.JSONCardParser;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;
import java.net.MalformedURLException;
import java.net.URL;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;


public class Downloader {
    final String hashFile = "./src/main/resources/hashfile.txt";
    final String dataFile = "./src/main/resources/AllPrintings.json";
    final String imageDir = "./src/main/resources/CardImages/";
    final String dataUrl = "https://mtgjson.com/api/v5/AllPrintings.json";
    final String picURL1 = "https://gatherer.wizards.com/Handlers/Image.ashx?name=";
    // insert cardname with spaces as %20 between these two strings;
    final String picURL2 = "&type=card";
    String dataHash;
    final private Map<String,Boolean> updateStatus = new HashMap<String,Boolean>();

    public Downloader() {
        // Initialize stuff
        this.updateStatus.put("attempted",false);
        this.updateStatus.put("successful",false);
        JSONCardParser parser;

        // update hash and data
        try {
            this.updateHash();
            this.updateStatus.replace("attempted",true);
            parser = new JSONCardParser(dataFile);

            // update all the images
            try {
                this.updateImages(parser);
            }
            catch(Exception e){
                System.out.println("Updating images failed");
            }
        }
        catch(MalformedURLException e) {
            System.out.println("URL exception caught updating hash");
        }
        catch(FileNotFoundException e) {
            System.out.println("File NOt Found exception caught updating hash");
        }
        catch(IOException e){
            System.out.println("IO exception caught updating hash");
        }

        // clear hash
        try {
            this.clearHash();
        }
        catch(FileNotFoundException e) {
            System.out.println("File NOt Found exception caught clearing hash");
        }
        catch(IOException e){
            System.out.println("IO exception caught clearing hash");
        }

    }

    public Map<String,Boolean> getUpdateStatus() {
        return this.updateStatus;
    }

    private void clearHash() throws FileNotFoundException, IOException {
        if(!new File(hashFile).exists()) {
            FileWriter hashF = new FileWriter(hashFile);
            hashF.write("None");
        }
        FileWriter writeHash = new FileWriter(hashFile);
        writeHash.write("None");
        writeHash.close();
    }

    private void updateHash() throws MalformedURLException, FileNotFoundException, IOException {
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
                try {
                    FileWriter writeHash = new FileWriter(hashFile);
                    writeHash.write(hashURLScanned);
                    writeHash.close();
                    try {
                        this.updateJSON();
                    }
                    catch(Exception e) {
                        System.out.println("Error caught: updating json");
                    }
                    try {
                        this.updateImages();
                    }
                    catch(Exception e) {
                        System.out.println("Error caught: updating images");
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

    private void updateJSON() throws MalformedURLException, IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(dataUrl+".zip").openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(dataFile+".zip")) {
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
        try {
            ZipFile zipFile = new ZipFile(dataFile+".zip");
            zipFile.extractAll("./src/main/resources/");
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }


    private void updateImages(JSONCardParser parser) throws MalformedURLException, IOException {
        try {
            JSONObject cards = parser.parseJSON(dataFile);
            for(String card: cards) {
                
            }
        }
        catch(Exception e) {
            System.out.println("IM TIRED OF FORCED EXCEPTION HANDLING, FUCK");
        }
    }

}

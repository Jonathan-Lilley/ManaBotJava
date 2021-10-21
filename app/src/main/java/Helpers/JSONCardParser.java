package Helpers;

import java.io.*;
import java.util.Iterator;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import javax.sound.midi.SysexMessage;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class JSONCardParser {

    public JSONCardParser(String JSONFile) {
        try {
            this.writeSimpleJSON(JSONFile);
        }
        catch(IOException e){
            System.out.println("Failed to simplify JSON");
        }
    }

    public JSONObject parseJSON(String JSONFile) throws IOException {
        JSONParser parser = new JSONParser();
        try {
            Scanner JSONScan = new Scanner(new File(JSONFile));
            String JSONFull = JSONScan.hasNext() ? JSONScan.next() : "";
            try {
                return (JSONObject)parser.parse(JSONFull);
            }
            catch(Exception e){
                System.out.println("Failed to parse JSON");
            }
        }
        catch(IOException e) {
            System.out.println("Error in reading JSON file");
        }
        return null;
    }

    private void writeSimpleJSON(String JSONFile) throws IOException {
        JSONObject allCards = new JSONObject();
        try {
            JSONObject topLevel = this.parseJSON(JSONFile);
            // get the data thing cuz that's all we need
            JSONObject secondLevel = (JSONObject) topLevel.get("data");
            Iterator<String> secondLevelKeys = secondLevel.keySet().iterator();
            while(secondLevelKeys.hasNext()) {
                String cardSetKey = secondLevelKeys.next();
                // Set up a JSON object for each cardset
                JSONObject getSet = new JSONObject();
                JSONObject cardSet = (JSONObject)secondLevel.get(cardSetKey);
                Iterator<String> cardSetKeys = cardSet.keySet().iterator();
                while(cardSetKeys.hasNext()) {
                    String cardKey = cardSetKeys.next();
                    JSONObject card = (JSONObject)cardSet.get(cardKey);
                    JSONObject info = new JSONObject();
                    // This gets all the *important* items of the cards, gets it as a JSONObject
                    String[] items = {
                                "colorIdentity",
                                "colors",
                                "convertedManaCost",
                                "power",
                                "subtypes",
                                "supertypes",
                                "text",
                                "toughness",
                                "type"      };
                    for(String item: items) {
                        info.put(item,card.get(item).toString());
                    }
                    // put each card into the set
                    getSet.put(card.toString(),info);
                }
                // put each set into the full JSONObject
                allCards.put(cardSet.toString(),getSet);
            }
        }
        catch(Exception e) {
            System.out.println("Failed to parse or read JSON JSON");
        }
        try {
            FileWriter saveFile = new FileWriter(JSONFile);
            saveFile.write(allCards.toJSONString());
            System.out.println("Successfully wrote JSON");
        }
        catch(IOException e){
            System.out.println("Failed to write JSON");
        }
    }

}

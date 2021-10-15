package Helpers;

import java.io.*;
import java.text.ParseException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
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

    public Object parseJSON(String JSONFile) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        try {
            Scanner JSONScan = new Scanner(new File(JSONFile));
            String JSONFull = JSONScan.hasNext() ? JSONScan.next() : "";
            try {
                return parser.parse(JSONFull);
            }
            catch(ParseException e){
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
            JSONObject topLevel = (JSONOBject) this.parseJSON(JSONFile);
            JSONObject secondLevel = (JSONObject) topLevel.get("data");
            for(String cardSet: secondLevel){
                for(String card: secondLevel.get("cardSet")){
                    JSONObject info = new JSONObject();
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
                        info.put(item,card.get(item));
                    }
                    allCards.put(card,info);
                }
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

package Helpers;

import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Scanner;
import java.util.Stack;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

enum colors {
    WHITE, BLACK;
}

public class SelectiveJSON {


    public static JSONObject selectiveJSON(String[] whitelist, String[] blacklist, String filename) {
        JSONObject mainObj = new JSONObject();
        Stack<JSONObject> objStack = new Stack<>();
        Stack<JSONArray> arrStack = new Stack<>();
        Stack<String> lastStack = new Stack<>();
        lastStack.push(null);
        String currs;
        colors whiteblack;
        boolean haswhite; // true if there was a whitelisted item, false otherwise
        String[][] WLBL = {whitelist, blacklist};
        try {
            Scanner file = new Scanner(new File(filename));
            while (file.hasNext()) {
                currs = file.next();
                whiteblack = colors.WHITE;
                for (String[] currList : WLBL) {
                    haswhite = false;
                    for (String listed : currList) {
                        String fullStr = "\"" + listed + "\"";
                        Pattern pattern = Pattern.compile(fullStr);
                        Matcher matcher = pattern.matcher(currs);
                        if ((matcher.find() && whiteblack == colors.WHITE) ||
                                (!matcher.find() && whiteblack == colors.BLACK)) {
                            if (whiteblack == colors.WHITE) {
                                haswhite = true;
                            }
                            if (currs.charAt(0) == '{') {
                                objStack.push(new JSONObject());
                            } else if (currs.charAt(0) == '[') {
                                arrStack.push(new JSONArray());
                            }
                            if (currs.charAt(currs.length() - 1) == ':') {
                                lastStack.push(fullStr);
                                continue;
                            } else if (arrStack.empty()) {
                                objStack.peek().put(lastStack.pop(), currs);
                            } else {
                                arrStack.peek().add(currs);
                            }
                            for(int i = 0; i < currs.length(); i++) {
                                if(currs.charAt(i) == ']') {
                                    objStack.peek().put(lastStack.pop(),arrStack.pop());
                                }
                                else if((currs.charAt(i) == '}') {
                                    JSONObject obj = objStack.pop();
                                    objStack.peek().put(lastStack.pop(),obj);
                                }
                            }
                        }
                        whiteblack = colors.BLACK;
                        if (haswhite) {
                            break;
                        }
                    }
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        mainObj.put(lastStack.pop(),objStack.pop());
        return mainObj;
    }

}

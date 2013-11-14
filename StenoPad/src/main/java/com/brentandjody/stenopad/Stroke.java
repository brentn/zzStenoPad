package com.brentandjody.stenopad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Stoke object
 * represents a sequence of steno keys
 */
public class Stroke {

    private static final String STOKE_DELIMITER="/";
    private static final String IMPLICIT_HYPHENS=".*[AOEU\\*50].*";

    private static final Hashtable<String,String> NUMBER_KEYS = new Hashtable<String, String>() {{
        put("S-", "1-");
        put("T-", "2-");
        put("P-", "3-");
        put("H-", "4-");
        put("A-", "5-");
        put("O-", "0-");
        put("-F", "-6");
        put("-P", "-7");
        put("-L", "-8");
        put("-T", "-9");
    }};

    private static final Hashtable<String, Integer> STENO_KEYS = new Hashtable<String, Integer>() {{
        put("#", 0);
        put("S-", 1);
        put("T-", 2);
        put("K-", 3);
        put("P-", 4);
        put("W-", 5);
        put("H-", 6);
        put("R-", 7);
        put("A-", 8);
        put("O-", 9);
        put("*", 10);
        put("-E", 11);
        put("-U", 12);
        put("-F", 13);
        put("-R", 14);
        put("-P", 15);
        put("-B", 16);
        put("-L", 17);
        put("-G", 18);
        put("-T", 19);
        put("-S", 20);
        put("-D", 21);
        put("-Z", 22);
    }};

    public Stroke() {
//TODO:
    }

    public String[] normalize(String input) {
        String[] strokes = input.split(STOKE_DELIMITER);
        List<String> normalized_strokes = new ArrayList<String>();
        for (String stroke : strokes) {
            if (stroke.contains("#")) {
                stroke = stroke.replace("#", "");
                if (stroke.matches(".*[0-9].*")) {
                    stroke = "#"+stroke;
                }
            }
            if (stroke.matches(IMPLICIT_HYPHENS)) {
                stroke.replace("-", "");
            }
            if (stroke.endsWith("-")) {
                stroke = stroke.substring(0,stroke.length() -1);
            }
            normalized_strokes.add(stroke);
        }
        String[] result = new String[normalized_strokes.size()];
        return normalized_strokes.toArray(result);
    }
}


    private List<String> convertNumbers(List<String> chord) {
        List<String> result = new ArrayList<String>();
        Boolean numeral = false;
        for (String thisKey : chord) {
            if (NUMBER_KEYS.containsKey(thisKey)) {
                result.add(NUMBER_KEYS.get(thisKey));
                numeral = true;
            } else {
                result.add(thisKey);
            }
        }
        if (numeral) {
            result.remove("#");
        }
        return result;

    }

    private String constructStroke(List<String> chord) {
        String result = "";
        String suffix = "";
        if (! Collections.disjoint(chord, Arrays.asList("A-", "O-", "5-", "0-", "-E", "-U", "*"))) {
            for (String key : chord) {
                result += key.replace("-","");
            }
        } else {
            for (String key : chord) {
                if (key.equals("#") || key.charAt(key.length()-1) == '-') {
                    result += key.replace("-", "");
                }
                if (key.charAt(0) == '-') {
                    suffix += key.replace("-", "");
                }
            }
            if (! suffix.isEmpty()) {
                result += "-"+suffix;
            }
        }
        return result;
    }

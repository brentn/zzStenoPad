package com.brentandjody.stenopad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Stoke object - represents a single steno stroke (ie, no "/")
 * represents a sequence of steno keys
 * Accepts only keys in STENO_KEYS and rejects everything else
 */
public class Stroke {

    private static final Set<String> IMPLICIT_HYPHENS = new HashSet<String>() {{
        add("A-"); add("O-"); add("5-"); add("0-"); add("-E"); add("U"); add("*");
    }};
    private static final HashMap<String,String> NUMBER_KEYS = new HashMap<String, String>() {{
        put("S-", "1-"); put("T-", "2-"); put("P-", "3-"); put("H-", "4-"); put("A-", "5-");
        put("O-", "0-"); put("-F", "-6"); put("-P", "-7"); put("-L", "-8"); put("-T", "-9");
    }};
    private static final HashMap<String, Integer> STENO_KEYS = new LinkedHashMap<String, Integer>() {{
        put("#", 0); put("S-", 1); put("T-", 2); put("K-", 3); put("P-", 4); put("W-", 5); put("H-", 6);
        put("R-", 7); put("A-", 8); put("O-", 9); put("*", 10); put("-E", 11); put("-U", 12);
        put("-F", 13); put("-R", 14); put("-P", 15); put("-B", 16); put("-L", 17); put("-G", 18);
        put("-T", 19); put("-S", 20); put("-D", 21); put("-Z", 22);
    }};

    public static String combine(Stroke[] strokes) {
        if (strokes == null || strokes.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (Stroke s : strokes) {
            sb.append(s.rtfcre());
            sb.append('/');
        }
        if (sb.charAt(sb.length()-1) == '/') {
            sb.deleteCharAt((sb.length()-1));
        }
        return sb.toString();
    }

    public static Stroke[] separate(String outline) {
        if (outline == null || outline.isEmpty()) return null;
        List<Stroke> list = new LinkedList<Stroke>();
        for (String s : outline.split("/")) {
            list.add(new Stroke(s));
        }
        Stroke[] result = new Stroke[list.size()];
        return list.toArray(result);
    }


    public static String normalize(String input) {
        List<String> keys = new ArrayList<String>();
        boolean rightSide = false;
        for (int i=0; i<input.length(); i++) {
            switch (input.charAt(i)) {
                case '#':keys.add("#");
                    break;
                case '*':keys.add("*");
                    break;
                case '-':
                    if (i < (input.length()-1)) {
                        rightSide=true;
                        keys.add("-"+input.charAt(i+1));
                        i++;
                    } // otherwize it is a spurious trailing hyphen, ignore it
                    break;
                case 'E':case 'U':case 'F':case 'B':case 'L':case 'G':case 'D':case 'Z':
                    //keys that only exist on the right side
                    rightSide=true;
                    keys.add("-"+input.charAt(i));
                    break;
                case 'K':case 'W':case 'H':case 'A':case 'O':
                    //keys that only exist on the left side
                    keys.add(input.charAt(i)+"-");
                    if ((i < (input.length()-1)) && (input.charAt(i+1) == '-'))
                        i++;
                    break;
                default:
                    // these keys exist on both sides, or are invalid
                    if ("STPR".indexOf(input.charAt(i))>=0) {
                        if ((i < (input.length()-1)) && (input.charAt(i+1) == '-')) {
                            keys.add(input.charAt(i)+"-");
                            i++;
                            break;
                        }
                        if (rightSide) { //if we have already had characters on the right, assume right side
                            keys.add("-"+input.charAt(i));
                            break;
                        }
                        // else assume left side
                        keys.add(input.charAt(i)+"-");
                    } // else invalid key
            }
        }
        Set keyset = new HashSet<String>(keys);
        Stroke converted = new Stroke(keyset);
        return converted.rtfcre;
    }

    private String raw;
    private final String rtfcre;
    private final boolean isCorrection;

    public Stroke(Set<String> keys) {
        //sort and remove invalid and duplicate keys
        raw = "";
        for (String k : keys) {
            raw += k;
        }
        List<String> stroke_keys= new LinkedList<String>();
        for (String key : STENO_KEYS.keySet()) {
            if (keys.contains(key)) {
                stroke_keys.add(key);
            }
        }
        rtfcre = constructStroke(convertNumbers(stroke_keys));
        isCorrection = (rtfcre.equals("*"));
    }

    public Stroke(String keyString) {
        raw=keyString;
        keyString = keyString.split("/")[0];
        rtfcre=normalize(keyString);
        isCorrection = (rtfcre.equals("*"));
    }

    public String rtfcre() {
        return rtfcre;
    }

    public Stroke[] asArray() {
        Stroke[] result = new Stroke[1];
        result[0] = this;
        return result;
    }

    public boolean isCorrection() {
        return isCorrection;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Stroke)) return false;
        return this.rtfcre.equals(((Stroke) o).rtfcre());
    }

    @Override
    public String toString() {
        String result = "Stroke(";
        if (isCorrection) result = "*Stroke(";
        result += rtfcre + " : " + raw + ")";
        return result;
    }

    private List<String> convertNumbers(List<String> keys) {
        // convert appropriate letters to numbers if the rtfcre contains '#'
        if ((keys==null) || (!keys.contains("#"))) return keys;
        List<String> result = new LinkedList<String>();
        boolean numeral = false;
        for (String key : keys) {
            if (NUMBER_KEYS.containsKey(key)) {
                result.add(NUMBER_KEYS.get(key));
                numeral = true;
            } else {
                result.add(key);
            }
        }
        if (numeral) {
            result.remove("#");
        }
        return result;
    }

    private String constructStroke(List<String> chord) {
        if (chord==null) return "";
        String result = "";
        String suffix = "";
        if (! Collections.disjoint(chord, IMPLICIT_HYPHENS)) {
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
}

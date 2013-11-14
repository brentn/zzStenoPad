package com.brentandjody.stenopad;

import java.util.ArrayList;
import java.util.List;

/**
 * Stoke object
 * represents a sequence of steno keys
 */
public class Stroke {

    private static final String STOKE_DELIMITER="/";
    private static final String IMPLICIT_HYPHENS=".*[AOEU\\*50].*";

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

package com.brentandjody.stenopad;

/**
 * Created by brent on 13/11/13.
 * This is the main translation class that converts a stream of strokes
 * into english text.
 */
public class Translation {

    private Stroke[] strokes;
    private String english;
    private String[] replaced;
    private int[] formatting;

    public Translation(Stroke[] outline, String translation) {
        // outline: a series of stroke objects
        // translation: a translation for the outline, or null
        strokes = outline;
        english = translation;
    }

}


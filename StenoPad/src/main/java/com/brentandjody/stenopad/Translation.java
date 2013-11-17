package com.brentandjody.stenopad;


import java.util.List;

/**
 * Created by brent on  13/11/13.
 * This is the main translation class
 * and represents a group of strokes, and the translation they represents
 */
public class Translation {

    private final Stroke[] strokes;
    private final String english;
    private int[] formatting;

    public interface TranslationPlayer {
        public void playTranslation(List<Translation> undo, List<Translation> play, Translation state, String staging);
    }

    @Override
    public String toString() {
        return "Translation(" + rtfcre() + " : " + english + ")";
    }

    public Translation(Stroke[] outline, String translation) {
        // outline: a series of stroke objects
        // translation: a translation for the outline, or null
        strokes = outline;
        english = translation;
    }

    public Translation copy() {
        Translation duplicate = new Translation(strokes, english);
        duplicate.setFormatting(formatting);
        return duplicate;
    }

    public Stroke[] strokes() {
        return strokes;
    }

    public String english() {
        return english;
    }

    public String rtfcre() {
        return Stroke.combine(strokes);
    }

    public void setFormatting(int[] f) {
        formatting = f;
    }

    public int[] getFormatting() {
        return formatting;
    }

}


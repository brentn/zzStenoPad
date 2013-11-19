package com.brentandjody.stenopad.Display;

/**
 * Created by brentn on 18/11/13.
 * An object that encapsulates the information required to display text to the screen
 */
public class DisplayItem {

    private int backspaces;
    private String text;
    private String preview;

    public DisplayItem(int bs, String txt, String pre) {
        backspaces = bs;
        text = txt;
        preview = pre;
    }

    public int getBackspaces() {
        return backspaces;
    }

    public String getText() {
        return text;
    }

    public String getPreview() {
        return preview;
    }
}

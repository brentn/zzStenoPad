package com.brentandjody.stenopad.Display;

/**
 * Created by brentn on 18/11/13.
 * An object that encapsulates the information required to display text to the screen
 */
public class DisplayItem {

    private int backspaces;
    private String text;


    public DisplayItem(int bs, String txt) {
        backspaces = bs;
        text = txt;
    }


    public int getBackspaces() {
        return backspaces;
    }

    public String getText() {
        return text;
    }

}

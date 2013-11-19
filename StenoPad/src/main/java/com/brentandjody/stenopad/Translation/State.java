package com.brentandjody.stenopad.Translation;

/**
 * Created by brentn on 18/11/13.
 * a class to store the state of the translation stream
 */
public class State {

    public static final int NORMAL=0;
    public static final int CAPITAL=1;
    public static final int LOWER=2;

    private boolean glue=false;
    private int capitalization=NORMAL;
    private boolean attachStart=false;
    private boolean attachEnd=false;
    private int backspaces=0;

    public State() {
    }

    public void setGlue() {
        glue = true;
    }
    public void setCapitalize() {
        capitalization = CAPITAL;
    }
    public void setLowercase() {
        capitalization = LOWER;
    }
    public void attachStart() {
        attachStart=true;
    }
    public void attachEnd() {
        attachEnd=true;
    }
    public void addBackspaces(int x) {
        backspaces+=x;
    }


    public boolean hasGlue() {
        return glue;
    }
    public boolean capitalized() {
        return capitalization==CAPITAL;
    }
    public boolean hasLowercase() {
        return capitalization==LOWER;
    }
    public boolean isAttachedStart() {
        return attachStart;
    }
    public boolean isAttachedEnd() {
        return attachEnd;
    }
    public int getBackspaces() {
        return backspaces;
    }
}

package com.brentandjody.stenopad.Input;

import java.util.Set;

/**
 * Created by brentn on 22/11/13.
 * An abstract class for implementing various steno machine hardware
 */
public abstract class StenoMachine {

    public static String CONNECTED_INTENT="3003";
    public static enum STATE {DISCONNECTED, INITIALIZING, CONNECTED, ERROR};
    public static enum TYPE {VIRTUAL, KEYBOARD, TXBOLT };

    public interface OnStrokeListener {
        public void onStroke(Set<String> keys);
    }
    public interface OnStateChangeListener {
        public void onStateChange(String state);
    }

    public abstract void setOnStrokeListener(OnStrokeListener listener);
    public abstract void setOnStateChangeListener(OnStateChangeListener listener);
    public abstract String getState();
}

package com.brentandjody.stenopad.Input;

import java.util.Set;

/**
 * Created by brentn on 22/11/13.
 * An abstract class for implementing various steno machine hardware
 */
public abstract class StenoMachine {

    public static enum STATE {DISCONNECTED, INITIALIZING, CONNECTED, ERROR};

    private OnStrokeListener onStrokeListener;
    private OnStateChangeListener onStateChangeListener;

    public interface OnStrokeListener {
        public void onStroke(Set<String> keys);
    }
    public void setOnStrokeListener(OnStrokeListener listener) {
        onStrokeListener = listener;
    }

    public interface OnStateChangeListener {
        public void onStateChange(String state);
    }
    public void setOnStateChangeListener(OnStateChangeListener listener) {
        onStateChangeListener = listener;
    }

    public abstract String getState();
}

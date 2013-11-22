package com.brentandjody.stenopad.Input;

/**
 * Created by brentn on 22/11/13.
 * Accept input from an N-Key Rollover (or any) keyboard such as MS Sidewinder
 */
public class NKeyRolloverMachine extends StenoMachine {

    private OnStrokeListener onStrokeListener;
    private OnStateChangeListener onStateChangeListener;
    private STATE current_state;

    @Override
    public void setOnStrokeListener(OnStrokeListener listener) {
        onStrokeListener = listener;
    }

    @Override
    public void setOnStateChangeListener(OnStateChangeListener listener) {
        onStateChangeListener = listener;
    }

    @Override
    public String getState() {
        return current_state.name();
    }
}

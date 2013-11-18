package com.brentandjody.stenopad.Input;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by brent on 17/11/13.
 */
public abstract class InputDevice {

    private Boolean connected = false;
    private Boolean running = false;

    public InputDevice() {
    }

    private OnStrokeCompleteListener onStrokeCompleteListener;

    public interface OnStrokeCompleteListener {
        public void onStrokeCompleteListener(Set<String> keys);
    }
    public void setOnStrokeCompleteListener(OnStrokeCompleteListener listener) {
        onStrokeCompleteListener = listener;
    }

    public abstract void connect();
    public abstract void startCapture();
    public abstract void stopCapture();

}

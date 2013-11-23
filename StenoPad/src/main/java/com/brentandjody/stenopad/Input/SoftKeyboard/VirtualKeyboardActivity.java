package com.brentandjody.stenopad.Input.SoftKeyboard;

import android.app.Activity;
import android.os.Bundle;

import com.brentandjody.stenopad.R;


/**
 * Created by brentn on 21/11/13.
 * Implement a soft-keyboard, mostly for testing
 */
public class VirtualKeyboardActivity extends Activity {

    private TouchLayer keyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard);
        keyboard = (TouchLayer) findViewById(R.id.touch_layer);

    }

    public void connect() {
    //TODO:
    }

    public void startCapture() {
    //TODO:
    }

    public void stopCapture() {
    //TODO:
    }
}

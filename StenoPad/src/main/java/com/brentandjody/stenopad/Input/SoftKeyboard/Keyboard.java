package com.brentandjody.stenopad.Input.SoftKeyboard;

import android.app.Activity;
import android.os.Bundle;

import com.brentandjody.stenopad.R;


/**
 * Created by brentn on 21/11/13.
 * Implement a soft-keyboard, mostly for testing
 */
public class Keyboard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard);
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

package com.brentandjody.stenopad.Input.SoftKeyboard;

import android.content.Context;
import android.content.Intent;

import com.brentandjody.stenopad.Input.StenoMachine;

/**
 * Created by brentn on 22/11/13.
 */
public class VirtualKeyboard extends StenoMachine {

    private OnStrokeListener onStrokeListener;
    Context context;
    private STATE state;

    public VirtualKeyboard(Context c) {
        context = c;
        state=STATE.CONNECTED;
        Intent intent = new Intent(context, VirtualKeyboardActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void setOnStrokeListener(OnStrokeListener listener) {
        onStrokeListener = listener;
    }

    @Override
    public void setOnStateChangeListener(OnStateChangeListener listener) {

    }

    @Override
    public String getState() {
        return state.name();
    }
}

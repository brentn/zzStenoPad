package com.brentandjody.stenopad;

import com.brentandjody.stenopad.Display.Screen;
import com.brentandjody.stenopad.Input.StenoMachine;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Set;


public class StenoPad extends Activity implements StenoMachine.OnStrokeListener{

    private DeviceConnectedReceiver deviceConnectedReceiver;
    private Translator translator;
    private StenoMachine inputDevice;
    private Screen displayDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up display device
        setContentView(R.layout.stenopad);
        translator = new Translator(StenoPad.this);
        final TextView main_view = (TextView) findViewById(R.id.main_text);
        final TextView preview = (TextView) findViewById(R.id.preview);
        displayDevice = new Screen(main_view, preview);
        //listen for new machine connection
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deviceConnectedReceiver == null) deviceConnectedReceiver = new DeviceConnectedReceiver();
        IntentFilter intentFilter = new IntentFilter(StenoMachine.CONNECTED_INTENT);
        registerReceiver(deviceConnectedReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceConnectedReceiver != null) unregisterReceiver(deviceConnectedReceiver);
    }


    @Override
    public void onStroke(Set<String> keys) {
        Stroke stroke = new Stroke(keys);
        translator.translate(stroke, displayDevice);
    }

    private static class DeviceConnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(StenoMachine.CONNECTED_INTENT)) {
//                inputDevice = machine;
//                inputDevice.setOnStrokeListener(this);
            }
        }
    }
}

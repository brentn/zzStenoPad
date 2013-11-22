package com.brentandjody.stenopad;

import com.brentandjody.stenopad.Display.DisplayDevice;
import com.brentandjody.stenopad.Input.ConnectionNotifier;
import com.brentandjody.stenopad.Input.StenoMachine;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Set;


public class StenoPad extends Activity implements ConnectionNotifier.OnDeviceConnectedListener, StenoMachine.OnStrokeListener{

    private final Translator translator = new Translator(this);
    private StenoMachine inputDevice;
    private DisplayDevice display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //listen for new machine connection
        ConnectionNotifier.getInstance().setOnDeviceConnectedListener(this);
        //set up display device
        setContentView(R.layout.stenopad);
        final TextView main_view = (TextView) findViewById(R.id.main_text);
        final TextView preview = (TextView) findViewById(R.id.preview);
        display = new DisplayDevice(main_view, preview);
    }

    @Override
    public void deviceConnected(StenoMachine machine) {
        inputDevice = machine;
        inputDevice.setOnStrokeListener(this);
    }

    @Override
    public void onStroke(Set<String> keys) {
        Stroke stroke = new Stroke(keys);
        translator.translate(stroke, display);
    }
}

package com.brentandjody.stenopad;

import com.brentandjody.stenopad.Display.Screen;
import com.brentandjody.stenopad.Input.SoftKeyboard.VirtualKeyboard;
import com.brentandjody.stenopad.Input.StenoMachine;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Set;


public class StenoPad extends Activity implements StenoMachine.OnStrokeListener{

    private static final String TAG = "StenoPad";

    private Translator translator;
    private StenoMachine inputDevice;
    private Screen displayDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up display device
        setContentView(R.layout.stenopad);
        translator = new Translator(StenoPad.this);
        final TextView main_view = (TextView) findViewById(R.id.main_text);
        final TextView preview = (TextView) findViewById(R.id.preview);
        displayDevice = new Screen(main_view, preview);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            Log.d("onResume", "intent: " + intent.toString());
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                UsbManager mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
                if(mUsbManager == null) {
                    Log.d(TAG, "mUsbManager is null");
                } else {
                    //TODO:registerMachine();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onStroke(Set<String> keys) {
        Stroke stroke = new Stroke(keys);
        translator.translate(stroke, displayDevice);
    }

    public void registerMachine(StenoMachine machine) {
        inputDevice = machine;
        inputDevice.setOnStrokeListener(this);
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                Log.d(TAG, "mUSBReceiver: received detached event");
            }
        }
    };

}

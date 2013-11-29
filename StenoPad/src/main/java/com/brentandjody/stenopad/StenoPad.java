package com.brentandjody.stenopad;

import com.brentandjody.stenopad.Display.Screen;
import com.brentandjody.stenopad.Input.SoftKeyboard.TouchLayer;
import com.brentandjody.stenopad.Input.StenoMachine;
import com.brentandjody.stenopad.Translation.Dictionary;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class StenoPad extends Activity implements StenoMachine.OnStrokeListener, TouchLayer.OnStrokeCompleteListener, Dictionary.OnDictionaryLoadedListener {
    private static final String TAG = "StenoPad";
    private static final String ACTION_USB_PERMISSION = "com.brentandjody.stenopad.USB_PERMISSION";

    private Dictionary dictionary;
    private Translator translator;
    private StenoMachine inputDevice;
    private Screen displayDevice;
    private TouchLayer keyboard;
    private PendingIntent mPermissionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up display device
        setContentView(R.layout.stenopad);
        final TextView main_view = (TextView) findViewById(R.id.main_text);
        final TextView preview = (TextView) findViewById(R.id.preview);
        displayDevice = new Screen(main_view, preview);
        // set up dictionary / translator
        dictionary = new Dictionary(getApplicationContext());
        dictionary.loadDefault();
        translator = new Translator(dictionary);
        // register listeners
        dictionary.setOnDictionaryLoadedListener(this);
        // add soft-keyboard until hardware keyboard is plugged in
        ViewGroup parent = (ViewGroup) main_view.getParent();
        LinearLayout softKbd = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard, parent, false);
        if (softKbd != null) {
            ViewGroup.LayoutParams layout = softKbd.getLayoutParams();
            addContentView(softKbd, layout);
            keyboard = (TouchLayer) findViewById(R.id.keyboard);
            keyboard.setLoading();
            keyboard.setOnStrokeCompleteListener(this);
            findViewById(R.id.candidates_area).setVisibility(View.GONE);
        }
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            Log.d("onResume", "intent: " + intent.toString());
            if (intent.getAction()!= null && intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                UsbManager mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
                if(mUsbManager == null) {
                    Log.d(TAG, "mUsbManager is null");
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            Log.d("onResume", "intent: " + intent.toString());
            if (intent.getAction()!= null && intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                UsbManager mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
                if(mUsbManager == null) {
                    Log.d(TAG, "mUsbManager is null");
                } else {
                   // UsbDevice device = chooseUsbDevice(mUsbManager.getDeviceList().keySet());
                   // UsbDeviceConnection connection = mUsbManager.openDevice(device);
                  //  Log.d(TAG, device.getDeviceClass() + ":"+device.getDeviceSubclass());
                  //  switch (device.getDeviceProtocol()) {

                   // }
                    //TODO:registerMachine();
                }
            }
        }
        IntentFilter filter = new IntentFilter();
        //filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        //registerReceiver(mUsbReceiver, filter);

    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
    }




    @Override
    public void onStroke(Set<String> keys) {
        Stroke stroke = new Stroke(keys);
        translator.translate(stroke, displayDevice);
    }

    @Override
    public void onStrokeComplete(Stroke stroke) {
        translator.translate(stroke, displayDevice);
    }

    public void registerMachine(StenoMachine machine) {
        inputDevice = machine;
        inputDevice.setOnStrokeListener(this);
    }

    @Override
    public void onDictionaryLoaded() {
        if (keyboard != null) {
            keyboard.clearLoading();
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d(TAG, "mUSBReceiver: received detached event");
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "mUSBReceiver: device attached");
            }
            if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
                Log.d(TAG, "mUSBReceiver: accessory attached");
            }
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            ((UsbManager) getSystemService(Context.USB_SERVICE)).requestPermission(device, mPermissionIntent);//call method to set up device communication
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

}

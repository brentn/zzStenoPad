package com.brentandjody.stenopad;

import com.brentandjody.stenopad.Display.Screen;
import com.brentandjody.stenopad.Input.SoftKeyboard.TouchLayer;
import com.brentandjody.stenopad.Input.StenoMachine;
import com.brentandjody.stenopad.Translation.Dictionary;
import com.brentandjody.stenopad.Translation.Stroke;
import com.brentandjody.stenopad.Translation.Translator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;


public class StenoPad extends Activity implements StenoMachine.OnStrokeListener, TouchLayer.OnStrokeCompleteListener, Dictionary.OnDictionaryLoadedListener {
    private static final String TAG = "StenoPad";
    private static final String ACTION_USB_PERMISSION = "com.brentandjody.stenopad.USB_PERMISSION";

    private Dictionary dictionary;
    private Translator translator;
    private StenoMachine inputDevice;
    private Screen displayDevice;
    private LinearLayout virtual_keyboard;
    private TouchLayer keyboard;
    private PendingIntent mPermissionIntent;
    private UsbDevice usbDevice;
    private TextView main_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set up display device
        setContentView(R.layout.stenopad);
        main_view = (TextView) findViewById(R.id.main_text);
        final TextView preview = (TextView) findViewById(R.id.preview);
        displayDevice = new Screen(main_view, preview);
        // set up dictionary / translator
        dictionary = new Dictionary(getApplicationContext());
        dictionary.loadDefault();
        translator = new Translator(dictionary);
        // register listeners
        dictionary.setOnDictionaryLoadedListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // get the device that initiated the activity
        if (intent != null) {
            Log.d("onResume", "intent: " + intent.toString());
            if (intent.getAction()!= null && intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (usbDevice == null) {
                    Log.w(TAG, "Plugged in USB device not found");
                }
                Log.d(TAG, "UsbDevice: "+usbDevice.toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // add soft-keyboard until/unless hardware keyboard is plugged in
        if ( usbDevice == null ) {
            if (virtual_keyboard == null) {
                launchVirtualKeyboard();
                // register receiver for usb attached event
                try {unregisterReceiver(mUsbReceiver);} catch(Exception e){}
                mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(mUsbReceiver, filter);
            }
        } else { // there is an external device
            // remove any virtual keyboard
            removeVirtualKeyboard();
            // set up the connection
            UsbManager mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
            if(mUsbManager == null) {
                Log.d(TAG, "mUsbManager is null");
            } else {
            }
            // register receiver for unplug event
            try {unregisterReceiver(mUsbReceiver);} catch(Exception e){}
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(mUsbReceiver, filter);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        try {unregisterReceiver(mUsbReceiver);} catch(Exception e){}
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

    private void launchVirtualKeyboard() {
        ViewGroup parent = (ViewGroup) main_view.getParent();
        virtual_keyboard = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboard, parent, false);
        if (virtual_keyboard != null) {
            ViewGroup.LayoutParams layout = virtual_keyboard.getLayoutParams();
            addContentView(virtual_keyboard, layout);
            keyboard = (TouchLayer) findViewById(R.id.keyboard);
            //TODO: replace this with non-virtualkbd specific one:
            if (dictionary.isLoading()) {
                keyboard.setLoading();
            } else {
                keyboard.clearLoading();
            }
            keyboard.setOnStrokeCompleteListener(this);
            findViewById(R.id.candidates_area).setVisibility(View.GONE);
            virtual_keyboard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
            parent.invalidate();
        }
    }

    private void removeVirtualKeyboard() {
        ViewGroup parent = (ViewGroup) virtual_keyboard.getParent();
        if (virtual_keyboard != null) {
            virtual_keyboard.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down));
            if (virtual_keyboard.getParent().equals(parent)) {
                ((FrameLayout)parent).removeView(virtual_keyboard);
            }
            keyboard = null;
            virtual_keyboard = null;
            parent.invalidate();
        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d(TAG, "mUSBReceiver: received detached event");
                launchVirtualKeyboard();
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

package com.brentandjody.stenopad.Input;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import com.brentandjody.stenopad.Input.Tests.SerialDevice;

/**
 * Created by brentn on 22/11/13.
 * Accept input from an N-Key Rollover (or any) keyboard such as MS Sidewinder
 */
public class NKeyRolloverMachine extends SerialDevice implements StenoMachine {

    private OnStrokeListener onStrokeListener;

    public NKeyRolloverMachine(UsbDevice device, UsbDeviceConnection connection) {
        super(device, connection);
    }

    @Override
    public void setOnStrokeListener(OnStrokeListener listener) {
        onStrokeListener = listener;
    }

}

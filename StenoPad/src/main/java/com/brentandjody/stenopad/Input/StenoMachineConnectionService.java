package com.brentandjody.stenopad.Input;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

/**
 * Created by brentn on 21/11/13.
 * Launched by USB Plugged intent
 * Determines if the plugged device is a recognized steno machine
 * and if so, returns a connected StenoMachine object of the correct subclass
 */
public class StenoMachineConnectionService extends IntentService{

    private OnUsbDeviceAttachedListener onUsbDeviceAttachedListener;
    private Intent intent;

    public StenoMachineConnectionService() {
        super("Steno Machine Connector");
    }

    public interface OnUsbDeviceAttachedListener {
        public void onUsbDeviceAttached(StenoMachine machine);
    }

    public void setOnUsbDeviceAttached(OnUsbDeviceAttachedListener listener) {
        onUsbDeviceAttachedListener = listener;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UsbDevice usbdevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        UsbManager manager = (UsbManager) getSystemService(this.USB_SERVICE);
        UsbDeviceConnection connection = manager.openDevice(usbdevice);
        UsbInterface iface = usbdevice.getInterface(0);
        StenoMachine machine = getStenoMachineByType(iface);
        onUsbDeviceAttachedListener.onUsbDeviceAttached(machine);
    }

    private StenoMachine getStenoMachineByType(UsbInterface iface) {
        //TODO: logic to determine which machine to create
        return new TXBoltMachine();
    }
}

package com.brentandjody.stenopad.Input;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

/**
 * Created by brentn on 21/11/13.
 * Launched by USB Plugged intent
 * Determines if the plugged device is a recognized steno machine
 * and if so, returns a connected StenoMachine object of the correct subclass
 */
public class StenoMachineConnectionService extends IntentService{

    private Intent intent;

    public StenoMachineConnectionService() {
        super("Steno Machine Connector");
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
        ConnectionNotifier.getInstance().registerConnectedDevice(machine);
    }

    private StenoMachine getStenoMachineByType(UsbInterface iface) {
        //TODO: logic to determine which machine to create
        return new TXBoltMachine();
    }
}

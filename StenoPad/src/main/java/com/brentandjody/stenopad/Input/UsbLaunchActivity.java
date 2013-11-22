package com.brentandjody.stenopad.Input;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

/**
 * Created by brentn on 21/11/13.
 */
public class UsbLaunchActivity extends Activity {

    private OnUsbDeviceAttachedListener onUsbDeviceAttachedListener;

    public interface OnUsbDeviceAttachedListener {
        public void onUsbDeviceAttached(UsbDeviceConnection connection, UsbInterface iface);
    }

    public void setOnUsbDeviceAttached(OnUsbDeviceAttachedListener listener) {
        onUsbDeviceAttachedListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UsbDevice usbdevice = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        UsbManager manager = (UsbManager) getSystemService(this.USB_SERVICE);
        UsbDeviceConnection connection = manager.openDevice(usbdevice);
        UsbInterface iface = usbdevice.getInterface(0);
        onUsbDeviceAttachedListener.onUsbDeviceAttached(connection, iface);
    }
}

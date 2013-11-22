package com.brentandjody.stenopad.Input;

/**
 * Created by brentn on 22/11/13.
 * singleton class to allow connectionservice to notify main activity of new connections
 */
public class ConnectionNotifier {

    private static ConnectionNotifier mInstance;
    private OnDeviceConnectedListener mListener;

    public interface OnDeviceConnectedListener {
        public void deviceConnected(StenoMachine machine);
    }

    public static ConnectionNotifier getInstance() {
        return mInstance;
    }

    public void setOnDeviceConnectedListener(OnDeviceConnectedListener listener) {
        mListener = listener;
    }

    public void registerConnectedDevice(StenoMachine machine) {
        mListener.deviceConnected(machine);
    }

}

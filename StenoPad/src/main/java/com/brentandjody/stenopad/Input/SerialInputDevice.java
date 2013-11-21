package com.brentandjody.stenopad.Input;

/**
 * Created by brentn on 21/11/13.
 */
public class SerialInputDevice extends InputProtocol {

    private boolean finished;
    private boolean connected;
    private SerialProtocol protocol;

    public void SerialInputDevice() {
        finished=false;
        connected=false;
    }

    @Override
    public void connect() {
        //TODO: more logic to really connect
        connected=true;
    }

    @Override
    public void startCapture() {
        finished=false;
        while (! finished) {

        }
    }

    @Override
    public void stopCapture() {
        finished=true;
    }

    public boolean isRunning() {
        return !finished;
    }

}

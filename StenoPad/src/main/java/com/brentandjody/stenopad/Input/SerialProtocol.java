package com.brentandjody.stenopad.Input;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by brentn on 21/11/13.
 */
public class SerialProtocol extends InputProtocol {

    public enum ProtocolType {TXBolt, Stentura}

    private static final float TIMEOUT = 0.1f; //seconds

    private ProtocolType protocol_name;
    private int stroke_state;
    private boolean finished;
    private boolean connected;
    private Set<String> keys = new LinkedHashSet<String>();

    public SerialProtocol (ProtocolType pname) {
        protocol_name=pname;
        finished=true;
        connected=false;
        reset_stroke();
    }

    @Override
    public void connect() {
        connected=true;
    }

    @Override
    public void startCapture() {
        finished=false;
        while (! finished) {
            //TODO:
        }
    }

    @Override
    public void stopCapture() {
        finished=true;
    }

    private void reset_stroke() {
        stroke_state=0;
        keys.clear();
    }

}

package com.brentandjody.stenopad.Input;

import java.util.Set;

/**
 * Created by brentn on 22/11/13.
 */
public class TXBoltMachine extends StenoMachine {

    private STATE current_state;

    @Override
    public String getState() {
        return current_state.name();
    }
}

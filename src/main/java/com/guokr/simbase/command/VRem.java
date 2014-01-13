package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class VRem extends SimCommand {

    @Override
    public String signature() {
        return "si";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, int vecid, SimCallback callback) {
        engine.vrem(callback, vkey, vecid);
    }

}

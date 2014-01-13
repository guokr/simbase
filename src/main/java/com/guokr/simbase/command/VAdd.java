package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class VAdd extends SimCommand {

    @Override
    public String signature() {
        return "siF";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, int vecid, float[] distr, SimCallback callback) {
        engine.vadd(callback, vkey, vecid, distr);
    }

}

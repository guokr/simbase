package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class VAcc extends SimCommand {

    @Override
    public String signature() {
        return "slF";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, long vecid, float[] distr, SimCallback callback) {
        engine.vacc(callback, vkey, vecid, distr);
    }

}

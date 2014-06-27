package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class IAcc extends SimCommand {

    @Override
    public String signature() {
        return "slI";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, long vecid, int[] distr, SimCallback callback) {
        engine.iacc(callback, vkey, vecid, distr);
    }

}

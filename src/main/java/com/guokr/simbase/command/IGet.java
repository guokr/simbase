package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class IGet extends SimCommand {

    @Override
    public String signature() {
        return "sl";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, long vecid, SimCallback callback) {
        engine.iget(callback, vkey, vecid);
    }

}

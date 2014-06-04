package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class VLen extends SimCommand {

    @Override
    public String signature() {
        return "s";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, SimCallback callback) {
        engine.vlen(callback, vkey);
    }

}

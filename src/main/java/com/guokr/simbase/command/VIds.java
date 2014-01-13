package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class VIds extends SimCommand {

    @Override
    public String signature() {
        return "s";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, SimCallback callback) {
        engine.vids(callback, vkey);
    }

}

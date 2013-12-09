package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class VMk extends SimCommand {

    @Override
    public String signature() {
        return "sss";
    }

    @Override
    public void invoke(SimEngine engine, String bkey, String vkey, String type, SimCallback callback) {
        engine.vmk(callback, bkey, vkey, type);
        callback.flip();
        callback.response();
    }

}

package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class VGet extends SimCommand {

    @Override
    public String signature() {
        return "si";
    }

    @Override
    public void invoke(SimEngine engine, String vkey, int vecid, SimCallback callback) {
        engine.vget(callback, vkey, vecid);
        callback.flip();
        callback.response();
    }

}

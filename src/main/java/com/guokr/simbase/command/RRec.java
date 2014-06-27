package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class RRec extends SimCommand {

    @Override
    public String signature() {
        return "sls";
    }

    @Override
    public void invoke(SimEngine engine, String vkeySource, long vecid, String vkeyTarget, SimCallback callback) {
        engine.rrec(callback, vkeySource, vecid, vkeyTarget);
    }

}

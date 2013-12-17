package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class RMk extends SimCommand {

    @Override
    public String signature() {
        return "ss";
    }

    @Override
    public void invoke(SimEngine engine, String vkeySource, String vkeyTarget, SimCallback callback) {
        engine.rmk(callback, vkeySource, vkeyTarget);
        callback.response();
    }

}

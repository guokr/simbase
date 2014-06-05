package com.guokr.simbase.command;

import com.guokr.simbase.SimEngine;
import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;

public class Info extends SimCommand {

    @Override
    public String signature() {
        return "";
    }

    @Override
    public void invoke(SimEngine engine, SimCallback callback) {
        callback.stringList(engine.info());
        callback.response();
    }

}

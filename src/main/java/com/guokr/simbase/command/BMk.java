package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class BMk extends SimCommand {

    @Override
    public void invoke(SimEngine engine, Object bkey, Object base, SimCallback callback) {
        engine.bmk(callback, (String) bkey, (String[]) base);
        callback.flip();
        callback.response();
    }

}

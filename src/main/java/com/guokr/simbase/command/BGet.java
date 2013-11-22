package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class BGet extends SimCommand {

    @Override
    public void invoke(SimEngine engine, Object bkey, SimCallback callback) {
        engine.bget(callback, (String) bkey);
        callback.flip();
        callback.response();
    }

}

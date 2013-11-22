package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class BList extends SimCommand {

    @Override
    public void invoke(SimEngine engine, SimCallback callback) {
        engine.blist(callback);
        callback.flip();
        callback.response();
    }

}

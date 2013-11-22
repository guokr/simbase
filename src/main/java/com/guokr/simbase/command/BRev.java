package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class BRev extends SimCommand {

    @Override
    public void invoke(SimEngine engine, Object bkey, Object base, SimCallback callback) {
        engine.brev(callback, (String) bkey, (String[]) base);
        callback.flip();
        callback.response();
    }

}

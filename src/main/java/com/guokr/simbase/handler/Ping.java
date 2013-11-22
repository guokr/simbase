package com.guokr.simbase.handler;

import com.guokr.simbase.SimEngine;
import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;

public class Ping extends SimCommand {

    @Override
    public void invoke(SimEngine engine, SimCallback callback) {
        callback.stringValue("pong");
        callback.flip();
        callback.response();
    }

}

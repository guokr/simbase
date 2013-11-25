package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class BMk extends SimCommand {

    @Override
    public String signature() {
        return "sS";
    }

    @Override
    public void invoke(SimEngine engine, String bkey, String[] base, SimCallback callback) {
        engine.bmk(callback, bkey, base);
    }

}

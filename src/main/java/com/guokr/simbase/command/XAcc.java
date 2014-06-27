package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class XAcc extends SimCommand {

    @Override
    public String signature() {
        return "slsl";
    }

    @Override
    public void invoke(SimEngine engine, String vkeyTarget, long vecidTarget, String vkeyOperand, long vecidOperand,
            SimCallback callback) {
        engine.xacc(callback, vkeyTarget, vecidTarget, vkeyOperand, vecidOperand);
    }

}

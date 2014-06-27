package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class XPrd extends SimCommand {

    @Override
    public String signature() {
        return "slsL";
    }

    @Override
    public void invoke(SimEngine engine, String vkeyTarget, long vecidTarget, String vkeyOperand, long[] vecidOperands,
            SimCallback callback) {
        engine.xprd(callback, vkeyTarget, vecidTarget, vkeyOperand, vecidOperands);
    }

}

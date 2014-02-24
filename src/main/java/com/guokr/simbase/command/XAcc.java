package com.guokr.simbase.command;

import com.guokr.simbase.SimCallback;
import com.guokr.simbase.SimCommand;
import com.guokr.simbase.SimEngine;

public class XAcc extends SimCommand {

    @Override
    public String signature() {
        return "sisi";
    }

    @Override
    public void invoke(SimEngine engine, String vkeyTarget, int vecidTarget, String vkeyOperand, int vecidOperand,
            SimCallback callback) {
        engine.xacc(callback, vkeyTarget, vecidTarget, vkeyOperand, vecidOperand);
    }

}

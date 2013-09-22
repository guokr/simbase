package com.guokr.simbase;

public interface SimEngineCallback {

    void sendOK();

    void sendError(String string);

    void sendCfg(String string);

    void sendInterger(int i);

    void sendFloat(float vlaue);

    void sendString(String string);

}

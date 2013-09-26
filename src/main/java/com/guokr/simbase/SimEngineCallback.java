package com.guokr.simbase;

public interface SimEngineCallback {

    void sendOK();

    void sendError(String msg);

    void sendInteger(int val);

    void sendIntegerList(int[] list);

    void sendFloat(float val);

    void sendFloatList(float[] list);

    void sendString(String val);

    void sendStringList(String[] list);

}

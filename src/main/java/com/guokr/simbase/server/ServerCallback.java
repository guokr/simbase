package com.guokr.simbase.server;

import com.guokr.simbase.SimCallback;

public class ServerCallback extends SimCallback {
    
    public RespCallback response;

    public ServerCallback(RespCallback cb) {
        this.response = cb;
    }

}

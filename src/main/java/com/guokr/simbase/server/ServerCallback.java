package com.guokr.simbase.server;

import com.guokr.simbase.SimCallback;

public class ServerCallback extends SimCallback {
    
    public RespCallback response;

    public ServerCallback(RespCallback cb) {
        this.response = cb;
    }

    public void response() {
        if (this.buffer == null) {
            this.error("Unknown server error!");
        }
        this.response.run(this.buffer);
    }

}

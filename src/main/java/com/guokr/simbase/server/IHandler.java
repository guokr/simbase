package com.guokr.simbase.server;

public interface IHandler {

    void handle(RedisRequests request, RespCallback callback);

    public void clientClose(AsyncChannel channel, int status);

    // close any resource with this handler
    void close(int timeoutMs);

}

package com.guokr.simbase.server;

public class RedisAtta extends ServerAtta {

    public final RedisDecoder decoder;

    public RedisRequests requests;

    public RedisAtta() {
        decoder = new RedisDecoder();
    }
}

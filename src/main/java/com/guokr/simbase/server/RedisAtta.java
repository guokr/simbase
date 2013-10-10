package com.guokr.simbase.server;


public class RedisAtta extends ServerAtta {

    public RedisAtta() {
        decoder = new RedisDecoder();
    }

    public final RedisDecoder decoder;
}

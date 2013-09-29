package com.guokr.simbase.server;


public class RedisAtta extends ServerAtta {

    public RedisAtta(int maxBody, int maxLine) {
        decoder = new RedisDecoder(maxBody, maxLine);
    }

    public final RedisDecoder decoder;
}

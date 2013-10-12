package com.guokr.simbase;

public interface SimRequest {

    String name();

    int argsize();

    Object arg(int idx);

}

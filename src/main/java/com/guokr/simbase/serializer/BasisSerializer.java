package com.guokr.simbase.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.store.Basis;

public class BasisSerializer extends Serializer<Basis> {

    @Override
    public Basis read(Kryo kyro, Input input, Class<Basis> type) {
        return null;
    }

    @Override
    public void write(Kryo kyro, Output output, Basis basis) {
    }

}

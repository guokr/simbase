package com.guokr.simbase.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.store.DenseVectorSet;

public class DenseVectorSetSerializer extends Serializer<DenseVectorSet> {

    @Override
    public DenseVectorSet read(Kryo kyro, Input input, Class<DenseVectorSet> type) {
        return null;
    }

    @Override
    public void write(Kryo kyro, Output output, DenseVectorSet vectorSet) {
    }

}

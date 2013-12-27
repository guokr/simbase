package com.guokr.simbase.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.store.SparseVectorSet;

public class SparseVectorSetSerializer extends Serializer<SparseVectorSet> {

    @Override
    public SparseVectorSet read(Kryo kyro, Input input, Class<SparseVectorSet> type) {
        return null;
    }

    @Override
    public void write(Kryo kyro, Output output, SparseVectorSet vectorSet) {
    }

}

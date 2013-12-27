package com.guokr.simbase.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.store.Recommendation;

public class RecommendationSerializer extends Serializer<Recommendation> {

    @Override
    public Recommendation read(Kryo kyro, Input input, Class<Recommendation> type) {
        return null;
    }

    @Override
    public void write(Kryo kyro, Output output, Recommendation recommend) {
    }

}

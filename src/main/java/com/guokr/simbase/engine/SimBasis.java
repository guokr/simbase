package com.guokr.simbase.engine;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.SimContext;
import com.guokr.simbase.events.BasisListener;
import com.guokr.simbase.events.RecommendationListener;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.store.Basis;
import com.guokr.simbase.store.DenseVectorSet;
import com.guokr.simbase.store.Recommendation;
import com.guokr.simbase.store.SparseVectorSet;
import com.guokr.simbase.store.VectorSet;

public class SimBasis implements KryoSerializable {

    private SimContext                  context;
    private Basis                       base;
    private Map<String, VectorSet>      vectorSets      = new HashMap<String, VectorSet>();
    private Map<String, Recommendation> recommendations = new HashMap<String, Recommendation>();

    public SimBasis(SimContext context, Basis base) {
        this.context = context;
        this.base = base;
    }

    private String rkey(String vkeySource, String vkeyTarget) {
        return new StringBuilder().append(vkeySource).append("_").append(vkeyTarget).toString();
    }

    public String[] bget() {
        return this.base.get();
    }

    public void brev(String[] base) {
        this.base.revise(base);
    }

    public void vmk(String vkey) {
        String type = context.retriveString("sparse", vkey, "type");
        float accumuFactor = context.retriveFloat(0.01f, vkey, "accumuFactor");
        int sparseFactor = context.retriveInt(4096, vkey, "sparseFactor");

        if (type.equals("dense")) {
            this.vectorSets.put(vkey, new DenseVectorSet(this.base, accumuFactor, sparseFactor));
        } else if (type.equals("sparse")) {
            this.vectorSets.put(vkey, new SparseVectorSet(this.base, accumuFactor, sparseFactor));
        } else {
            throw new IllegalArgumentException("wrong type of vector set in config!");
        }
    }

    public float[] vget(String vkey, int vecid) {
        return this.vectorSets.get(vkey).get(vecid);
    }

    public void vadd(String vkey, int vecid, float[] distr) {
        this.vectorSets.get(vkey).add(vecid, distr);
    }

    public void vset(String vkey, int vecid, float[] distr) {
        this.vectorSets.get(vkey).set(vecid, distr);
    }

    public void vacc(String vkey, int vecid, float[] distr) {
        this.vectorSets.get(vkey).accumulate(vecid, distr);
    }

    public void vrem(String vkey, int vecid) {
        this.vectorSets.get(vkey).remove(vecid);
    }

    public int[] iget(String vkey, int vecid) {
        return this.vectorSets.get(vkey)._get(vecid);
    }

    public void iadd(String vkey, int vecid, int[] pairs) {
        this.vectorSets.get(vkey)._add(vecid, pairs);
    }

    public void iset(String vkey, int vecid, int[] pairs) {
        this.vectorSets.get(vkey)._set(vecid, pairs);
    }

    public void iacc(String vkey, int vecid, int[] pairs) {
        this.vectorSets.get(vkey)._accumulate(vecid, pairs);
    }

    public void rmk(String vkeySource, String vkeyTarget) {
        VectorSet source = vectorSets.get(vkeySource);
        VectorSet target = vectorSets.get(vkeyTarget);
        Recommendation rec = new Recommendation(source, target, "cos", "desc", 20);

        String rkey = rkey(vkeySource, vkeyTarget);
        this.recommendations.put(rkey, rec);

        source.addListener(rec);
        target.addListener(rec);
    }

    public String rget(String vkeySource, int vecid, String vkeyTarget) {
        return this.recommendations.get(rkey(vkeySource, vkeyTarget)).get(vecid);
    }

    public int[] rrec(String vkeySource, int vecid, String vkeyTarget) {
        return this.recommendations.get(rkey(vkeySource, vkeyTarget)).rec(vecid);
    }

    @Override
    public void read(Kryo arg0, Input arg1) {
    }

    @Override
    public void write(Kryo arg0, Output arg1) {
    }

    public void addListener(BasisListener listener) {
        base.addListener(listener);
    }

    public void addListener(String vkey, VectorSetListener listener) {
        vectorSets.get(vkey).addListener(listener);
    }

    public void addListener(String srcVkey, String tgtVkey, RecommendationListener listener) {
        recommendations.get(rkey(srcVkey, tgtVkey)).addListener(listener);
    }

}

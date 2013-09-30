package com.guokr.simbase.engine;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.SimContext;
import com.guokr.simbase.store.Basis;
import com.guokr.simbase.store.Recommendation;
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
        this.vectorSets.put(vkey, new VectorSet(context.getSub(vkey), this.base));
    }

    public float[] vget(String vkey, int vecid) {
        return this.vectorSets.get(vkey).get(vecid);
    }

    public void vset(String vkey, int vecid, float[] distr) {
        this.vectorSets.get(vkey).add(vecid, distr);
    }

    public void vacc(String vkey, int vecid, float[] distr) {
        this.vectorSets.get(vkey).accumulate(vecid, distr);
    }

    public void vrem(String vkey, int vecid) {
        this.vectorSets.get(vkey).remove(vecid);
    }

    public String jget(String vkey, int vecid) {
        return null;
    }

    public void jset(String vkey, int vecid, String jsonlike) {
    }

    public void jacc(String vkey, int vecid, String jsonlike) {
    }

    public int[] iget(String vkey, int vecid) {
        return null;
    }

    public void iset(String vkey, int vecid, int[] pairs) {
    }

    public void iacc(String vkey, int vecid, int[] pairs) {
    }

    public void rmk(String vkeySource, String vkeyTarget) {
        String rkey = rkey(vkeySource, vkeyTarget);
        this.recommendations.put(rkey, new Recommendation(context.getSub(rkey)));
    }

    public String rget(String vkeySource, int vecid, String vkeyTarget) {
        return this.recommendations.get(rkey(vkeySource, vkeyTarget)).jsonize(vecid);
    }

    public int[] rrec(String vkeySource, int vecid, String vkeyTarget) {
        return this.recommendations.get(rkey(vkeySource, vkeyTarget)).ids(vecid);
    }

    @Override
    public void read(Kryo arg0, Input arg1) {
    }

    @Override
    public void write(Kryo arg0, Output arg1) {
    }

}

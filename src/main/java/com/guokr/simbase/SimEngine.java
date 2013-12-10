package com.guokr.simbase;


public interface SimEngine {

    public abstract void cfg(SimCallback callback, String key);

    public abstract void cfg(SimCallback callback, String key, String val);

    public abstract void load(SimCallback callback, String bkey);

    public abstract void save(SimCallback callback, String bkey);

    public abstract void xincr(SimCallback callback, String vkey, String key);

    public abstract void xget(SimCallback callback, String vkey, String key);

    public abstract void xlookup(SimCallback callback, String vkey, int vecid);

    public abstract void del(SimCallback callback, String key);

    public abstract void blist(SimCallback callback);

    public abstract void bmk(SimCallback callback, String bkey, String[] base);

    public abstract void brev(SimCallback callback, String bkey, String[] base);

    public abstract void bget(SimCallback callback, String bkey);

    public abstract void vlist(SimCallback callback, String bkey);

    public abstract void vmk(SimCallback callback, String bkey, String vkey);

    public abstract void vget(SimCallback callback, String vkey, int vecid);

    public abstract void vset(SimCallback callback, String vkey, int vecid, float[] distr);

    public abstract void vacc(SimCallback callback, String vkey, int vecid, float[] distr);

    public abstract void vrem(SimCallback callback, String vkey, int vecid);

    // Internal use for client-side sparsification
    public abstract void iget(SimCallback callback, String vkey, int vecid);

    // Internal use for client-side sparsification
    public abstract void iset(SimCallback callback, String vkey, int vecid, int[] pairs);

    // Internal use for client-side sparsification
    public abstract void iacc(SimCallback callback, String vkey, int vecid, int[] pairs);

    // Internal use for client-side sparsification
    public abstract void irem(SimCallback callback, String vkey, int vecid);

    public abstract void rlist(SimCallback callback, String vkey);

    public abstract void rmk(SimCallback callback, String vkeySource, String vkeyTarget);

    public abstract void rget(SimCallback callback, String vkeySource, int vecid, String vkeyTarget);

    public abstract void rrec(SimCallback callback, String vkeySource, int vecid, String vkeyTarget);

}
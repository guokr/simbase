package com.guokr.simbase.util;

import java.util.Map;

public abstract class AbstractVectorSet {
    private Map<String, Object> config;
    private Basis               basis;

    public AbstractVectorSet(Map<String, Object> config, Basis basis) {
        this.config = config;
        this.setBasis(basis);
    }

    /*
     * 添加新的vector item
     */
    abstract public void set(int vecid, float[] distr) throws Exception;

    abstract public void add(int vectid, String[] comps, float[] distr) throws Exception;

    /*
     * 更新item数据
     */
    abstract public void update(int vecid, float[] distr);

    abstract public void update(int vecid, String[] comps, float[] distr);

    /*
     * 根据配置，按权重更新item数值
     */
    abstract public void accumulate(int vecid, float[] distr);

    /*
     * 返回item数值
     */
    abstract public float[] get(int vecid);

    /*
     * 删除
     */
    abstract public void remove(int vecid);

    public Basis getBasis() {
        return basis;
    }

    public void setBasis(Basis basis) {
        this.basis = basis;
    }
}

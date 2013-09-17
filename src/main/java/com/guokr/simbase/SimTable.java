package com.guokr.simbase;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.util.Base;
import com.guokr.simbase.util.Recommendation;
import com.guokr.simbase.util.VectorSet;

public class SimTable implements KryoSerializable {

	private Map<String, Object> context;
	private Base base;
	private Map<String, VectorSet> vectorSets = new HashMap<String, VectorSet>();
	private Map<String, Recommendation> recommendations = new HashMap<String, Recommendation>();

	public SimTable(Map<String, Object> context, Base base) {
		this.context = context;
		this.base = base;
	}

	private void validateKeyFormat(String key) throws IllegalArgumentException {
		if (key.indexOf('_') > -1) {
			throw new IllegalArgumentException("Invalid key format:" + key);
		}
	}

	private void validateKey(String key) throws IllegalArgumentException {
		if (!this.vectorSets.containsKey(key)) {
			throw new IllegalArgumentException(
					"The vector set can not be found:" + key);
		}
	}

	private String rkey(String vkeySource, String vkeyTarget) {
		this.validateKey(vkeySource);
		this.validateKey(vkeyTarget);
		return new StringBuilder().append(vkeySource).append("_")
				.append(vkeyTarget).toString();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getConfig(String key) {
		if (this.context.containsKey(key)) {
			return (Map<String, Object>) this.context.get(key);
		} else {
			return new HashMap<String, Object>();
		}
	}

	public String[] bget() {
		return this.base.get();
	}

	public void brev(String[] base) {
		this.base.revise(base);
	}

	public void vmk(String vkey) {
		this.validateKeyFormat(vkey);
		this.vectorSets.put(vkey, new VectorSet(getConfig(vkey), this.base));
	}

	public void vadd(String vkey, int vecid, float[] distr) {
		this.validateKey(vkey);
		this.vectorSets.get(vkey).add(vecid, distr);
	}

	public void vacc(String vkey, int vecid, String json) {
		this.validateKey(vkey);
		this.vectorSets.get(vkey).accumulate(vecid, json);
	}

	public void vrem(String vkey, int vecid) {
		this.validateKey(vkey);
		this.vectorSets.get(vkey).remove(vecid);
	}

	public float[] vget(String vkey, int vecid) {
		this.validateKey(vkey);
		return this.vectorSets.get(vkey).get(vecid);
	}

	public void rmk(String vkeySource, String vkeyTarget) {
		String key = rkey(vkeySource, vkeyTarget);
		this.recommendations.put(key, new Recommendation(getConfig(key)));
	}

	public String[] rget(String vkeySource, String vkeyTarget) {
		return this.recommendations.get(rkey(vkeySource, vkeyTarget)).get();
	}

	public int[] rrec(String vkeySource, String vkeyTarget) {
		return this.recommendations.get(rkey(vkeySource, vkeyTarget)).recommend();
	}

	@Override
	public void read(Kryo arg0, Input arg1) {
	}

	@Override
	public void write(Kryo arg0, Output arg1) {
	}

}

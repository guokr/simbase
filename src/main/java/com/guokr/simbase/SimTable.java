package com.guokr.simbase;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SimTable implements KryoSerializable{
   

	public static SortedSet<Map.Entry<Integer, Float>> entriesSortedByValues(
			Map<Integer, Float> map) {
		SortedSet<Map.Entry<Integer, Float>> sortedEntries = new TreeSet<Map.Entry<Integer, Float>>(
				new Comparator<Map.Entry<Integer, Float>>() {
					@Override
					public int compare(Map.Entry<Integer, Float> e1,
							Map.Entry<Integer, Float> e2) {
						return (int) Math.signum(e2.getValue() - e1.getValue());
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	private int maxlimit = 20;
	private TFloatList probs = new TFloatArrayList();
	private TIntIntMap indexer = new TIntIntHashMap();
	private TIntObjectHashMap<SortedMap<Integer, Float>> scores = new TIntObjectHashMap<SortedMap<Integer, Float>>();

	private void addScore(int src, int tgt, float score) {
		SortedMap<Integer, Float> range = scores.get(src);
		if (range == null) {
			range = new TreeMap<Integer, Float>();
			scores.put(src, range);
		}
		if (src != tgt) {
			range.put(tgt, score);
		}
		while (range.size() > maxlimit) {
			SortedSet<Map.Entry<Integer, Float>> entries = entriesSortedByValues(range);
			range.remove(entries.last().getKey());
		}
	}

	public void add(int docid, float[] distr) {
		float length = 0;
		int start;
		if (indexer.containsKey(docid)) {
			start = indexer.get(docid);
			int cursor = start;
			for (float val : distr) {
				probs.set(cursor, val);
				length += val * val;
				cursor++;
			}
			probs.set(cursor++, (float) (docid + 1));
			probs.set(cursor, length);
		} else {
			start = probs.size();
			indexer.put(docid, start);
			for (float val : distr) {
				probs.add(val);
				length += val * val;
			}
			probs.add((float) (docid + 1));
			probs.add(length);
		}
		int end = probs.size();

		float score = 0;
		int base = 0;
		for (int offset = 0; offset < end; offset++) {
			float val = probs.get(offset);
			if (val >= 0) {
				if (val < 1) {
					int idx = offset - base;
					if (idx < end - start - 1) {
						float another = distr[idx];
						score += another * val;
					}
				} else {
					float cosine = score * score / length
							/ probs.get(offset + 1);
					addScore(docid, (int) val - 1, cosine);
					addScore((int) val - 1, docid, cosine);
					score = 0;
					offset = offset + 1;
					base = offset + 1;
				}
			}
		}
	}

	public void update(int docid, float[] distr) {
		add(docid, distr);
	}

	public void delete(int docid) {
		if (indexer.containsKey(docid)) {
			int cursor = indexer.get(docid);
			while (true) {
				float val = probs.get(cursor);
				if ((val < 0f) && (val > 1f)) {
					break;
				}
				probs.set(cursor, - val);
			}
		}
		indexer.remove(docid);
	}

	public SortedSet<Map.Entry<Integer, Float>> retrieve(int docid) {
		if (scores.containsKey(docid)) {
			return entriesSortedByValues(scores.get(docid));
		} else {
			return entriesSortedByValues(new TreeMap<Integer, Float>());
		}
	}

	public float similarity(int docid1, int docid2) {
		return scores.get(docid1).get(docid2);
	}

	public SimTable clone() {
		SimTable peer = new SimTable();
		peer.probs = new TFloatArrayList(this.probs);
		peer.indexer = new TIntIntHashMap(this.indexer);
		peer.scores = new TIntObjectHashMap<SortedMap<Integer, Float>>(this.scores);
		return peer;
	}
	
	public void reload(SimTable table) {
		this.probs = table.probs;
		this.indexer = table.indexer;
		this.scores = table.scores;
	}

	@Override//重载序列化代码
	public void read(Kryo kryo, Input input) {
		System.out.println("loading...");
		this.probs = kryo.readObject(input, TFloatArrayList.class);
		this.indexer = kryo.readObject(input, TIntIntHashMap.class);
		
		int scoresize = kryo.readObject(input, int.class);
		while(scoresize > 0){
			Integer docid = kryo.readObject(input, Integer.class);	
			SortedMap<Integer, Float> range = null;
			range = new TreeMap<Integer, Float>();
			this.scores.put(docid, range);
			int Listsize = kryo.readObject(input,int.class);
			while(Listsize >0){
				Integer key = kryo.readObject(input,Integer.class);
				Float score = kryo.readObject(input,Float.class);
				range.put(key, score);
				Listsize--;
			}
			scoresize--;
		}
		System.out.println("load finish");
	}

	@Override
	public void write(Kryo kryo, Output output) {
		kryo.writeObject(output, this.probs);
		kryo.writeObject(output, this.indexer);
		TIntIterator iter =  this.scores.keySet().iterator();
		kryo.writeObject(output, this.scores.size());
		while(iter.hasNext()){
			Integer docid = iter.next();
			kryo.writeObject(output, docid);
			SortedMap<Integer, Float> scoreList = scores.get(docid);
			kryo.writeObject(output, scoreList.size());
			for(Integer key : scoreList.keySet()){
				Float score = scoreList.get(key);
				kryo.writeObject(output, key);
				kryo.writeObject(output, score);
			}
		}
	}

		
	
}

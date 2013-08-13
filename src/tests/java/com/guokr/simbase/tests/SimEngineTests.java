package com.guokr.simbase.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.junit.Test;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.SimTable;

public class SimEngineTests {

	private int[] getDocids(SortedSet<Entry<Integer, Float>> set) {
		int[] docids = new int[set.size()];
		Iterator<Entry<Integer, Float>> iter = set.iterator();
		int i = 0;
		while (iter.hasNext()) {
			docids[i] = iter.next().getKey();
			i++;
		}
		return docids;
	}

	private void validator(SortedSet<Entry<Integer, Float>> set) {
		Iterator<Entry<Integer, Float>> iter = set.iterator();
		float beforeValue = 1.0f;
		while (iter.hasNext()) {
			float thisValue = iter.next().getValue();
			assertTrue(beforeValue >= thisValue);
			beforeValue = thisValue;
		}
	}

	private void delay(int second) {

		try {
			Thread.currentThread();
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
		}
	}
	
	private void printSimBase(SimBase simbase,String key) {
		int count =0;
		while (count < 24) {
			int [] docids = getDocids(simbase.retrieve(key, count));
			if (docids.length!=0){
				for (int docid : docids) {
					System.out.print(docid + ",");
				}
				System.out.println();
			}
			else{
				System.out.println("empty");
			}
			count++;
		}
		System.out.println();
	}
	
	private SimTable initTable() {
		// 测试用引擎
		SimTable engine = new SimTable();
		engine.add(0, new float[] { 0.18257418583505536f, 0.3651483716701107f,
				0.5477225575051661f, 0.7302967433402214f });
		engine.add(1, new float[] { 0.18257418583505536f, 0.3651483716701107f,
				0.7302967433402214f, 0.5477225575051661f });
		engine.add(2, new float[] { 0.18257418583505536f, 0.5477225575051661f,
				0.3651483716701107f, 0.7302967433402214f });
		engine.add(3, new float[] { 0.18257418583505536f, 0.5477225575051661f,
				0.7302967433402214f, 0.3651483716701107f });
		engine.add(4, new float[] { 0.18257418583505536f, 0.7302967433402214f,
				0.3651483716701107f, 0.5477225575051661f });
		engine.add(5, new float[] { 0.18257418583505536f, 0.7302967433402214f,
				0.5477225575051661f, 0.3651483716701107f });
		engine.add(6, new float[] { 0.3651483716701107f, 0.18257418583505536f,
				0.5477225575051661f, 0.7302967433402214f });
		engine.add(7, new float[] { 0.3651483716701107f, 0.18257418583505536f,
				0.7302967433402214f, 0.5477225575051661f });
		engine.add(8, new float[] { 0.3651483716701107f, 0.5477225575051661f,
				0.18257418583505536f, 0.7302967433402214f });
		engine.add(9, new float[] { 0.3651483716701107f, 0.5477225575051661f,
				0.7302967433402214f, 0.18257418583505536f });
		engine.add(10, new float[] { 0.3651483716701107f, 0.7302967433402214f,
				0.18257418583505536f, 0.5477225575051661f });
		engine.add(11, new float[] { 0.3651483716701107f, 0.7302967433402214f,
				0.5477225575051661f, 0.18257418583505536f });
		engine.add(12, new float[] { 0.5477225575051661f, 0.18257418583505536f,
				0.3651483716701107f, 0.7302967433402214f });
		engine.add(13, new float[] { 0.5477225575051661f, 0.18257418583505536f,
				0.7302967433402214f, 0.3651483716701107f });
		engine.add(14, new float[] { 0.5477225575051661f, 0.3651483716701107f,
				0.18257418583505536f, 0.7302967433402214f });
		engine.add(15, new float[] { 0.5477225575051661f, 0.3651483716701107f,
				0.7302967433402214f, 0.18257418583505536f });
		engine.add(16, new float[] { 0.5477225575051661f, 0.7302967433402214f,
				0.18257418583505536f, 0.3651483716701107f });
		engine.add(17, new float[] { 0.5477225575051661f, 0.7302967433402214f,
				0.3651483716701107f, 0.18257418583505536f });
		engine.add(18, new float[] { 0.7302967433402214f, 0.18257418583505536f,
				0.3651483716701107f, 0.5477225575051661f });
		engine.add(19, new float[] { 0.7302967433402214f, 0.18257418583505536f,
				0.5477225575051661f, 0.3651483716701107f });
		engine.add(20, new float[] { 0.7302967433402214f, 0.3651483716701107f,
				0.18257418583505536f, 0.5477225575051661f });
		engine.add(21, new float[] { 0.7302967433402214f, 0.3651483716701107f,
				0.5477225575051661f, 0.18257418583505536f });
		engine.add(22, new float[] { 0.7302967433402214f, 0.5477225575051661f,
				0.18257418583505536f, 0.3651483716701107f });
		engine.add(23, new float[] { 0.7302967433402214f, 0.5477225575051661f,
				0.3651483716701107f, 0.18257418583505536f });
		return engine;
	}

	private SimBase initSimBase(String key) {
		SimBase simbase = new SimBase();
		simbase.add(key, 0, new float[] { 0.18257418583505536f,
				0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f });
		simbase.add(key, 1, new float[] { 0.18257418583505536f,
				0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f });
		simbase.add(key, 2, new float[] { 0.18257418583505536f,
				0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f });
		simbase.add(key, 3, new float[] { 0.18257418583505536f,
				0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f });
		simbase.add(key, 4, new float[] { 0.18257418583505536f,
				0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f });
		simbase.add(key, 5, new float[] { 0.18257418583505536f,
				0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f });
		simbase.add(key, 6,
				new float[] { 0.3651483716701107f, 0.18257418583505536f,
						0.5477225575051661f, 0.7302967433402214f });
		simbase.add(key, 7,
				new float[] { 0.3651483716701107f, 0.18257418583505536f,
						0.7302967433402214f, 0.5477225575051661f });
		simbase.add(key, 8,
				new float[] { 0.3651483716701107f, 0.5477225575051661f,
						0.18257418583505536f, 0.7302967433402214f });
		simbase.add(key, 9,
				new float[] { 0.3651483716701107f, 0.5477225575051661f,
						0.7302967433402214f, 0.18257418583505536f });
		simbase.add(key, 10,
				new float[] { 0.3651483716701107f, 0.7302967433402214f,
						0.18257418583505536f, 0.5477225575051661f });
		simbase.add(key, 11,
				new float[] { 0.3651483716701107f, 0.7302967433402214f,
						0.5477225575051661f, 0.18257418583505536f });
		simbase.add(key, 12,
				new float[] { 0.5477225575051661f, 0.18257418583505536f,
						0.3651483716701107f, 0.7302967433402214f });
		simbase.add(key, 13,
				new float[] { 0.5477225575051661f, 0.18257418583505536f,
						0.7302967433402214f, 0.3651483716701107f });
		simbase.add(key, 14,
				new float[] { 0.5477225575051661f, 0.3651483716701107f,
						0.18257418583505536f, 0.7302967433402214f });
		simbase.add(key, 15,
				new float[] { 0.5477225575051661f, 0.3651483716701107f,
						0.7302967433402214f, 0.18257418583505536f });
		simbase.add(key, 16,
				new float[] { 0.5477225575051661f, 0.7302967433402214f,
						0.18257418583505536f, 0.3651483716701107f });
		simbase.add(key, 17,
				new float[] { 0.5477225575051661f, 0.7302967433402214f,
						0.3651483716701107f, 0.18257418583505536f });
		simbase.add(key, 18,
				new float[] { 0.7302967433402214f, 0.18257418583505536f,
						0.3651483716701107f, 0.5477225575051661f });
		simbase.add(key, 19,
				new float[] { 0.7302967433402214f, 0.18257418583505536f,
						0.5477225575051661f, 0.3651483716701107f });
		simbase.add(key, 20,
				new float[] { 0.7302967433402214f, 0.3651483716701107f,
						0.18257418583505536f, 0.5477225575051661f });
		simbase.add(key, 21,
				new float[] { 0.7302967433402214f, 0.3651483716701107f,
						0.5477225575051661f, 0.18257418583505536f });
		simbase.add(key, 22,
				new float[] { 0.7302967433402214f, 0.5477225575051661f,
						0.18257418583505536f, 0.3651483716701107f });
		simbase.add(key, 23,
				new float[] { 0.7302967433402214f, 0.5477225575051661f,
						0.3651483716701107f, 0.18257418583505536f });
		return simbase;

	}

	@Test
	public void test() {
		SimTable engine = new SimTable();
		engine.add(2, new float[] { 0.9f, 0.1f, 0f });
		engine.add(3, new float[] { 0.9f, 0f, 0.1f });
		engine.add(5, new float[] { 0.1f, 0.9f, 0f });
		engine.add(7, new float[] { 0.1f, 0f, 0.9f });
		engine.add(11, new float[] { 0f, 0.9f, 0.1f });
		engine.add(13, new float[] { 0f, 0.1f, 0.9f });
		// System.out.println(engine.retrieve(2));
		// System.out.println(engine.retrieve(3));
		// System.out.println(engine.retrieve(5));
		// System.out.println(engine.retrieve(7));
		// System.out.println(engine.retrieve(11));
		// System.out.println(engine.retrieve(13));

		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
				.similarity(5, 11)));
		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
				.similarity(7, 13)));
		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
				.similarity(3, 7)));
		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
				.similarity(11, 13)));
		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
				.similarity(3, 5)));
		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
				.similarity(7, 11)));
		assertTrue((int) (1000 * engine.similarity(2, 11)) == (int) (1000 * engine
				.similarity(3, 13)));
		assertTrue((int) (1000 * engine.similarity(2, 13)) == (int) (1000 * engine
				.similarity(5, 7)));

		engine.add(2, new float[] { 0f, 0.1f, 0.9f });
		engine.add(3, new float[] { 0.1f, 0f, 0.9f });
		engine.add(5, new float[] { 0f, 0.9f, 0.1f });
		engine.add(7, new float[] { 0.9f, 0f, 0.1f });
		engine.add(11, new float[] { 0.1f, 0.9f, 0f });
		engine.add(13, new float[] { 0.9f, 0.1f, 0f });

		// System.out.println(engine.retrieve(2));
		// System.out.println(engine.retrieve(3));
		// System.out.println(engine.retrieve(5));
		// System.out.println(engine.retrieve(7));
		// System.out.println(engine.retrieve(11));
		// System.out.println(engine.retrieve(13));

		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
				.similarity(5, 11)));
		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
				.similarity(7, 13)));
		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
				.similarity(3, 7)));
		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
				.similarity(11, 13)));
		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
				.similarity(3, 5)));
		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
				.similarity(7, 11)));
		assertTrue((int) (1000 * engine.similarity(2, 11)) == (int) (1000 * engine
				.similarity(3, 13)));
		assertTrue((int) (1000 * engine.similarity(2, 13)) == (int) (1000 * engine
				.similarity(5, 7)));

	}

	@Test
	public void test2() {
		SimTable engine = initTable();
		int count = 0;
		while (count < 24) {
			// System.out.println(engine.retrieve(count));
			count++;
		}
		assertTrue((int) (1000 * engine.similarity(0, 1)) == (int) (1000 * engine
				.similarity(2, 4)));
		count = 0;
		while (count < 24) {
			SortedSet<Entry<Integer, Float>> result = engine.retrieve(count);
			validator(result);
			count++;
		}
	}

	@Test
	public void test_del_clean() {
		String key = "test2";
		SimBase simbase = initSimBase("test2");
		delay(1);// 等待加载完毕
		// simbase.delete(key, 2);
		printSimBase(simbase,key);
		simbase.delete(key, 1);
//		simbase.delete(key, 2);
		delay(1);
		assertTrue(simbase.retrieve(key, 1).size()==0);
		simbase.clear();
		delay(1);
		assertTrue(simbase.retrieve(key, 1).size()==0);
		printSimBase(simbase,key);
		simbase.add(key, 7,
				new float[] { 0.3651483716701107f, 0.18257418583505536f,
						0.7302967433402214f, 0.5477225575051661f });
		delay(1);
		printSimBase(simbase,key);
		simbase.add(key, 1, new float[] { 0.18257418583505536f,
				0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f });
		delay(1);
		printSimBase(simbase,key);
		
	}

}

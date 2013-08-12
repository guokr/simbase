package com.guokr.simbase.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.junit.Test;

import com.guokr.simbase.SimTable;

public class SimEngineTests {


	private int [] getDocids(SortedSet<Entry<Integer, Float>> set) {
		int [] docids = new int[set.size()];
		Iterator<Entry<Integer, Float>> iter = set.iterator();
		int i = 0;
		while (iter.hasNext()){
			docids[i]=iter.next().getKey();
			i++;
		}
		return docids.clone();
	}
	
	private void validator(SortedSet<Entry<Integer, Float>> set) {
		Iterator<Entry<Integer, Float>> iter = set.iterator();
		float beforeValue=1.0f;
		while (iter.hasNext()){
			float thisValue = iter.next().getValue();
			assertTrue(beforeValue>=thisValue);
			beforeValue=thisValue;
		}
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
//		System.out.println(engine.retrieve(2));
//		System.out.println(engine.retrieve(3));
//		System.out.println(engine.retrieve(5));
//		System.out.println(engine.retrieve(7));
//		System.out.println(engine.retrieve(11));
//		System.out.println(engine.retrieve(13));

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

		System.out.println(engine.retrieve(2));
		System.out.println(engine.retrieve(3));
		System.out.println(engine.retrieve(5));
		System.out.println(engine.retrieve(7));
		System.out.println(engine.retrieve(11));
		System.out.println(engine.retrieve(13));

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
		int count =0;
		while (count<24){
			System.out.println(engine.retrieve(count));
			count++;
		}
		assertTrue((int) (1000 * engine.similarity(0, 1)) == (int) (1000 * engine
				.similarity(2, 4)));
		count = 0;
		while (count<24){
			SortedSet<Entry<Integer, Float>> result = engine.retrieve(count);
			validator(result);
			count++;
		}
		int [] resultTuple;
		resultTuple = new int []{1, 2, 6, 7, 4, 3, 12, 8, 5, 14, 10, 13, 18, 9, 20, 19, 15, 11, 16, 17};
		assertArrayEquals(getDocids(engine.retrieve(0)),resultTuple);
		resultTuple = new int []{0, 7, 3, 6, 5, 2, 9, 13, 15, 4, 11, 12, 19, 8, 10, 18, 14, 21, 17, 20};
		assertArrayEquals(getDocids(engine.retrieve(1)),resultTuple);
		resultTuple = new int []{4, 0, 8, 10, 5, 1, 6, 14, 3, 12, 7, 16, 11, 20, 18, 9, 22, 17, 13, 19};
		assertArrayEquals(getDocids(engine.retrieve(2)),resultTuple);
		resultTuple = new int []{5, 1, 9, 11, 4, 0, 15, 7, 2, 13, 6, 17, 10, 21, 19, 8, 23, 16, 12, 18};
		assertArrayEquals(getDocids(engine.retrieve(3)),resultTuple);
		resultTuple = new int []{2, 10, 5, 8, 16, 3, 0, 11, 17, 1, 9, 14, 22, 6, 7, 20, 12, 23, 15, 18};
		assertArrayEquals(getDocids(engine.retrieve(4)),resultTuple);
		resultTuple = new int []{3, 11, 4, 9, 1, 10, 17, 2, 0, 16, 8, 15, 7, 23, 6, 13, 21, 22, 14, 20};
		assertArrayEquals(getDocids(engine.retrieve(5)),resultTuple);
		resultTuple = new int []{7, 0, 12, 1, 18, 2, 14, 13, 19, 8, 20, 3, 4, 15, 5, 10, 9, 21, 22, 23};
		assertArrayEquals(getDocids(engine.retrieve(6)),resultTuple);
		resultTuple = new int []{6, 1, 13, 0, 19, 15, 12, 3, 9, 18, 21, 2, 5, 14, 4, 20, 8, 11, 23, 10};
		assertArrayEquals(getDocids(engine.retrieve(7)),resultTuple);
		resultTuple = new int []{2, 14, 10, 4, 20, 16, 0, 12, 22, 6, 5, 18, 17, 1, 7, 3, 11, 23, 19, 21};
		assertArrayEquals(getDocids(engine.retrieve(8)),resultTuple);
		resultTuple = new int []{3, 11, 15, 5, 1, 21, 17, 13, 7, 23, 4, 19, 16, 0, 10, 6, 2, 22, 18, 20};
		assertArrayEquals(getDocids(engine.retrieve(9)),resultTuple);
		resultTuple = new int []{4, 16, 8, 2, 5, 17, 14, 22, 11, 20, 0, 23, 3, 12, 1, 18, 6, 9, 21, 19};
		assertArrayEquals(getDocids(engine.retrieve(10)),resultTuple);
		resultTuple = new int []{5, 9, 17, 3, 4, 23, 16, 15, 10, 21, 1, 22, 2, 13, 7, 19, 0, 8, 20, 18};
		assertArrayEquals(getDocids(engine.retrieve(11)),resultTuple);
		resultTuple = new int []{18, 6, 14, 20, 19, 7, 0, 8, 13, 2, 1, 22, 21, 10, 4, 15, 23, 16, 3, 5};
		assertArrayEquals(getDocids(engine.retrieve(12)),resultTuple);
		resultTuple = new int []{19, 7, 15, 21, 18, 6, 9, 1, 12, 3, 0, 23, 20, 11, 5, 14, 22, 17, 2, 4};
		assertArrayEquals(getDocids(engine.retrieve(13)),resultTuple);
		resultTuple = new int []{8, 12, 20, 18, 10, 22, 6, 2, 16, 0, 4, 19, 23, 7, 1, 13, 21, 17, 5, 11};
		assertArrayEquals(getDocids(engine.retrieve(14)),resultTuple);
		resultTuple = new int []{13, 9, 21, 19, 7, 23, 3, 11, 1, 17, 18, 5, 22, 6, 20, 0, 12, 16, 4, 10};
		assertArrayEquals(getDocids(engine.retrieve(15)),resultTuple);
		resultTuple = new int []{22, 10, 17, 23, 4, 11, 8, 20, 5, 14, 2, 21, 9, 18, 19, 3, 12, 15, 0, 1};
		assertArrayEquals(getDocids(engine.retrieve(16)),resultTuple);
		resultTuple = new int []{23, 16, 11, 22, 5, 10, 21, 9, 4, 15, 20, 3, 8, 19, 18, 2, 13, 14, 1, 7};
		assertArrayEquals(getDocids(engine.retrieve(17)),resultTuple);
		resultTuple = new int []{12, 20, 19, 14, 22, 21, 6, 13, 23, 7, 15, 8, 16, 0, 1, 10, 2, 17, 9, 4};
		assertArrayEquals(getDocids(engine.retrieve(18)),resultTuple);
		resultTuple = new int []{13, 21, 18, 15, 20, 7, 23, 12, 6, 22, 9, 14, 1, 17, 0, 3, 11, 16, 8, 5};
		assertArrayEquals(getDocids(engine.retrieve(19)),resultTuple);
		resultTuple = new int []{18, 22, 14, 12, 19, 23, 8, 16, 21, 10, 6, 17, 13, 2, 7, 4, 0, 15, 11, 5};
		assertArrayEquals(getDocids(engine.retrieve(20)),resultTuple);
		resultTuple = new int []{19, 15, 23, 13, 18, 22, 17, 9, 20, 11, 7, 16, 3, 12, 5, 1, 6, 14, 10, 4};
		assertArrayEquals(getDocids(engine.retrieve(21)),resultTuple);
		resultTuple = new int []{16, 20, 23, 17, 18, 21, 14, 10, 19, 8, 11, 12, 15, 4, 5, 2, 13, 9, 6, 7};
		assertArrayEquals(getDocids(engine.retrieve(22)),resultTuple);
		resultTuple = new int []{17, 22, 21, 16, 20, 19, 15, 11, 18, 9, 10, 13, 14, 5, 4, 3, 12, 8, 7, 1};
		assertArrayEquals(getDocids(engine.retrieve(23)),resultTuple);
		
	}

}

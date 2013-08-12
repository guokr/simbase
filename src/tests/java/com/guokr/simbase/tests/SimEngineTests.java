package com.guokr.simbase.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.guokr.simbase.SimTable;

public class SimEngineTests {

	@Test
	public void test() {
		SimTable engine = new SimTable();
		engine.add(0, new float[] {0.18257418583505536f, 0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f});
		engine.add(1, new float[] {0.18257418583505536f, 0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f});
		engine.add(2, new float[] {0.18257418583505536f, 0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f});
		engine.add(3, new float[] {0.18257418583505536f, 0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f});
		engine.add(4, new float[] {0.18257418583505536f, 0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f});
		engine.add(5, new float[] {0.18257418583505536f, 0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f});
		engine.add(6, new float[] {0.3651483716701107f, 0.18257418583505536f, 0.5477225575051661f, 0.7302967433402214f});
		engine.add(7, new float[] {0.3651483716701107f, 0.18257418583505536f, 0.7302967433402214f, 0.5477225575051661f});
		engine.add(8, new float[] {0.3651483716701107f, 0.5477225575051661f, 0.18257418583505536f, 0.7302967433402214f});
		engine.add(9, new float[] {0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f, 0.18257418583505536f});
		engine.add(10, new float[] {0.3651483716701107f, 0.7302967433402214f, 0.18257418583505536f, 0.5477225575051661f});
		engine.add(11, new float[] {0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f, 0.18257418583505536f});
		engine.add(12, new float[] {0.5477225575051661f, 0.18257418583505536f, 0.3651483716701107f, 0.7302967433402214f});
		engine.add(13, new float[] {0.5477225575051661f, 0.18257418583505536f, 0.7302967433402214f, 0.3651483716701107f});
		engine.add(14, new float[] {0.5477225575051661f, 0.3651483716701107f, 0.18257418583505536f, 0.7302967433402214f});
		engine.add(15, new float[] {0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f, 0.18257418583505536f});
		engine.add(16, new float[] {0.5477225575051661f, 0.7302967433402214f, 0.18257418583505536f, 0.3651483716701107f});
		engine.add(17, new float[] {0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f, 0.18257418583505536f});
		engine.add(18, new float[] {0.7302967433402214f, 0.18257418583505536f, 0.3651483716701107f, 0.5477225575051661f});
		engine.add(19, new float[] {0.7302967433402214f, 0.18257418583505536f, 0.5477225575051661f, 0.3651483716701107f});
		engine.add(20, new float[] {0.7302967433402214f, 0.3651483716701107f, 0.18257418583505536f, 0.5477225575051661f});
		engine.add(21, new float[] {0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f, 0.18257418583505536f});
		engine.add(22, new float[] {0.7302967433402214f, 0.5477225575051661f, 0.18257418583505536f, 0.3651483716701107f});
		engine.add(23, new float[] {0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f, 0.18257418583505536f});
		System.out.println(engine.retrieve(0));
		System.out.println(engine.retrieve(1));
		System.out.println(engine.retrieve(2));
		System.out.println(engine.retrieve(3));
		System.out.println(engine.retrieve(4));
		System.out.println(engine.retrieve(5));
		System.out.println(engine.retrieve(6));
		System.out.println(engine.retrieve(7));
		System.out.println(engine.retrieve(8));
		System.out.println(engine.retrieve(9));
		System.out.println(engine.retrieve(10));
		System.out.println(engine.retrieve(11));
		System.out.println(engine.retrieve(12));
		System.out.println(engine.retrieve(13));
		System.out.println(engine.retrieve(14));
		System.out.println(engine.retrieve(15));
		System.out.println(engine.retrieve(16));
		System.out.println(engine.retrieve(17));
		System.out.println(engine.retrieve(18));
		System.out.println(engine.retrieve(19));
		System.out.println(engine.retrieve(20));
		System.out.println(engine.retrieve(21));
		System.out.println(engine.retrieve(22));
		System.out.println(engine.retrieve(23));
//		engine.add(2, new float[] { 0.9f, 0.1f, 0f });
//		engine.add(3, new float[] { 0.9f, 0f, 0.1f });
//		engine.add(5, new float[] { 0.1f, 0.9f, 0f });
//		engine.add(7, new float[] { 0.1f, 0f, 0.9f });
//		engine.add(11, new float[] { 0f, 0.9f, 0.1f });
//		engine.add(13, new float[] { 0f, 0.1f, 0.9f });
//		System.out.println(engine.retrieve(2));
//		System.out.println(engine.retrieve(3));
//		System.out.println(engine.retrieve(5));
//		System.out.println(engine.retrieve(7));
//		System.out.println(engine.retrieve(11));
//		System.out.println(engine.retrieve(13));
//
//		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
//				.similarity(5, 11)));
//		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
//				.similarity(7, 13)));
//		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
//				.similarity(3, 7)));
//		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
//				.similarity(11, 13)));
//		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
//				.similarity(3, 5)));
//		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
//				.similarity(7, 11)));
//		assertTrue((int) (1000 * engine.similarity(2, 11)) == (int) (1000 * engine
//				.similarity(3, 13)));
//		assertTrue((int) (1000 * engine.similarity(2, 13)) == (int) (1000 * engine
//				.similarity(5, 7)));
//
//		engine.add(2, new float[] { 0f, 0.1f, 0.9f });
//		engine.add(3, new float[] { 0.1f, 0f, 0.9f });
//		engine.add(5, new float[] { 0f, 0.9f, 0.1f });
//		engine.add(7, new float[] { 0.9f, 0f, 0.1f });
//		engine.add(11, new float[] { 0.1f, 0.9f, 0f });
//		engine.add(13, new float[] { 0.9f, 0.1f, 0f });
//
//		System.out.println(engine.retrieve(2));
//		System.out.println(engine.retrieve(3));
//		System.out.println(engine.retrieve(5));
//		System.out.println(engine.retrieve(7));
//		System.out.println(engine.retrieve(11));
//		System.out.println(engine.retrieve(13));
//
//		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
//				.similarity(5, 11)));
//		assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 * engine
//				.similarity(7, 13)));
//		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
//				.similarity(3, 7)));
//		assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 * engine
//				.similarity(11, 13)));
//		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
//				.similarity(3, 5)));
//		assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 * engine
//				.similarity(7, 11)));
//		assertTrue((int) (1000 * engine.similarity(2, 11)) == (int) (1000 * engine
//				.similarity(3, 13)));
//		assertTrue((int) (1000 * engine.similarity(2, 13)) == (int) (1000 * engine
//				.similarity(5, 7)));

	}

}

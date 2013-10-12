package com.guokr.simbase.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSparseVectorSet {
	SparseVectorSet vectorSet;
	private int[] pairs;
	int pairsLen = 0;

	@Before
	public void setUp() throws Exception {
		String[] comps = new String[10];
		pairs = new int[] { 0, 12, 1, 33, 2, 17, 3, 16, 4, 31 };

		for (int i = 0; i < 10; i++) {
			comps[i] = "a";
		}
		for (int i = 1; i < pairs.length; i += 2) {
			pairsLen += pairs[i] * pairs[i];
		}

		vectorSet = new SparseVectorSet(null, new Basis(comps));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSet() throws Exception {
		vectorSet.set(1, pairs);
	}

	@Test
	public void testSetMultiple() throws Exception {
		vectorSet.set(1, pairs);
		vectorSet.set(2, pairs);
		int[] ret = vectorSet.get(1);
		assertNotNull(ret);
		ret = vectorSet.get(2);
		assertNotNull(ret);
	}

	@Test(expected = Exception.class)
	public void testSetTooBigPair() throws Exception {
		vectorSet.set(1, new int[100]);
	}

	@Test(expected = Exception.class)
	public void testSetTooBigIndex() throws Exception {
		vectorSet.set(1, new int[] { 11, 0 });
	}

	@Test(expected = Exception.class)
	public void testSetNegativeIndex() throws Exception {
		vectorSet.set(1, new int[] { -10, 0 });
	}

	@Test
	public void testSetThenGet() throws Exception {
		vectorSet.set(1, pairs);
		int[] ret = vectorSet.get(1);
		assertEquals(pairs[1], ret[0]);
		assertEquals(pairs[3], ret[1]);
		assertEquals(pairs[7], ret[3]);
	}

	@Test
	public void testSetManyTimes() throws Exception {
		int size = 10000;
		for (int i = 0; i < size; i++) {
			vectorSet.set(i, pairs);
		}
		assertEquals(size, vectorSet.size());
	}

	@Test
	public void testSetThenRemoveManyTimes() throws Exception {
		int size = 10000;
		for (int i = 0; i < size; i++) {
			vectorSet.set(i, pairs);
			vectorSet.remove(i);
		}
		assertEquals(0, vectorSet.size());
		for (int i = 0; i < size; i++) {
			vectorSet.set(i, pairs);
			assertEquals(i + 1, vectorSet.size());
		}
		for (int i = 0; i < size; i++) {
			int[] ret = vectorSet.get(i);
			assertNotNull(ret);
			for (int j = 0; j < pairs.length / 2; j++) {
				assertEquals(pairs[j * 2 + 1], ret[j]);
			}
		}
	}

	@Test
	public void testUpdate() throws Exception {
		vectorSet.set(1, pairs);
		pairs[1] = 100;
		vectorSet.set(1, pairs);
		int[] ret = vectorSet.get(1);
		assertEquals(pairs[1], ret[0]);
	}

	@Test
	public void testUpdateSize() throws Exception {
		vectorSet.set(1, pairs);
		pairs = new int[] { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 51 };
		vectorSet.set(1, pairs);
		int[] ret = vectorSet.get(1);
		assertNotNull(ret);
		assertEquals(pairs[11], ret[5]);
		assertEquals(1, vectorSet.size());
	}

	@Test
	public void testRemove() throws Exception {
		vectorSet.set(1, pairs);
		int[] ret = vectorSet.get(1);
		assertNotNull(ret);
		vectorSet.remove(1);
		ret = vectorSet.get(1);
		assertNull(ret);
	}

	@Test
	public void testVectorLength() throws Exception {
		vectorSet.set(1, pairs);
		assertEquals(pairsLen, vectorSet.norm(1));
	}

	@Test
	public void testVectorLength2() throws Exception {
		for (int i = 0; i < 10000; i++) {
			vectorSet.set(i, new int[] { 0, i, 1, i + 1 });
		}
		for (int i = 0; i < 10000; i++) {
			assertEquals(i * i + (i + 1) * (i + 1), vectorSet.norm(i));
		}
	}

	@Test
	public void testVectorLengthAfterOperations() throws Exception {
		for (int i = 0; i < 10000; i++) {
			vectorSet.set(i, pairs);
		}
		for (int i = 0; i < 10000; i++) {
			vectorSet.remove(i);
		}
		for (int i = 10000; i < 20000; i++) {
			vectorSet.set(i, pairs);
		}
		for (int i = 10000; i < 20000; i++) {
			assertEquals(pairsLen, vectorSet.norm(i));
		}
	}

	@Test
	public void testClone() throws Exception {
		for (int i = 0; i < 10000; i++) {
			vectorSet.set(i, pairs);
		}
		for (int i = 0; i < 5000; i++) {
			vectorSet.remove(i);
		}
		SparseVectorSet newSet = vectorSet.clone();
		assertEquals(5000, newSet.size());

		for (int i = 5000; i < 10000; i++) {
			assertEquals(pairsLen, vectorSet.norm(i));
		}
	}
}

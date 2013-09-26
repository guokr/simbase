package com.guokr.simbase.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSparseVectorSet {
    SparseVectorSet vectorSet;
    private int[]   pairs;

    @Before
    public void setUp() throws Exception {
        String[] comps = new String[10];
        pairs = new int[] { 0, 12, 1, 33, 2, 17, 3, 16, 4, 31 };

        for (int i = 0; i < 10; i++) {
            comps[i] = "a";
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
        int size = 1000000;
        for (int i = 0; i < size; i++) {
            vectorSet.set(i, pairs);
        }
        assertEquals(size, vectorSet.size());
    }

    @Test
    public void testSetThenRemoveManyTimes() throws Exception {
        int size = 1000000;
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
}

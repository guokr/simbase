package com.guokr.simbase.util;

import static org.junit.Assert.*;

import gnu.trove.iterator.TFloatIterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.guokr.simbase.util.Basis;
import com.guokr.simbase.util.DenseVectorSet;

public class TestDenseVectorSet {
    private DenseVectorSet vectorSet = null;
    float[]                floats;

    @Before
    public void setUp() throws Exception {
        vectorSet = new DenseVectorSet(null, new Basis(new String[] { "a", "b", "c", "d", "e" }));
        floats = new float[] { 0.1f, 0.2f };
    }

    @After
    public void tearDown() throws Exception {
        vectorSet = null;
    }

    @Test
    public void testSet() throws Exception {
        vectorSet.set(1, floats);
        assertEquals(floats[0], vectorSet.get(1)[0], 0.001);
        assertEquals(floats[1], vectorSet.get(1)[1], 0.001);
    }

    @Test
    public void testSetManyTimes() throws Exception {
        int size = 100000;
        for (int i = 0; i < size; i++) {
            vectorSet.set(i, floats);
            assertEquals(i + 1, vectorSet.size());
        }
    }

    @Test
    public void testSetThenRemoveManyTimes() throws Exception {
        int size = 100000;
        for (int i = 0; i < size; i++) {
            vectorSet.set(i, floats);
            vectorSet.set(size + i, floats);
            vectorSet.remove(i);
            assertEquals(i + 1, vectorSet.size());
        }
        for (int i = 0; i < size; i++) {
            vectorSet.remove(size + i);
            assertEquals(size - i - 1, vectorSet.size());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        vectorSet.set(1, floats);
        floats[0] = floats[0] * 2;
        vectorSet.set(1, floats);
        assertEquals(floats[0], vectorSet.get(1)[0], 0.001);
    }

    @Test
    public void testGet() throws Exception {
        for (int i = 0; i < 10000; i++) {
            vectorSet.set(i, floats);
        }
        for (int i = 0; i < 10000; i++) {
            assertNotNull(vectorSet.get(i));
        }
    }

    @Test
    public void testRemove() throws Exception {
        for (int i = 0; i < 10000; i++) {
            vectorSet.set(i, floats);
        }
        for (int i = 0; i < 10000; i++) {
            vectorSet.remove(i);
        }
        for (int i = 0; i < 10000; i++) {
            assertNull(vectorSet.get(i));
        }

        TFloatIterator iter = vectorSet.hive.iterator();
        while (iter.hasNext()) {
            assertEquals((float) -1.0, iter.next(), 0.001);
        }
    }
}

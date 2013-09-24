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
        floats = new float[] { (float) 0.1, (float) 0.2 };
    }

    @After
    public void tearDown() throws Exception {
        vectorSet = null;
    }

    @Test
    public void testSet() throws Exception {
        vectorSet.set(1, floats);
        assertEquals(floats[0], vectorSet.get(1)[0]);
        assertEquals(floats[1], vectorSet.get(1)[1]);
    }

    @Test
    public void testRepeatSet() throws Exception {
        for (int i = 0; i < 10000; i++) {
            vectorSet.set(1, floats);
        }
        floats[0] = floats[0] * 2;
        vectorSet.set(1, floats);
        assertEquals(floats[0], vectorSet.get(1)[0]);
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
            assertEquals((float)-1.0, iter.next());
        }
    }
}

package com.guokr.simbase.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSparseVectorSet {
    SparseVectorSet vectorSet;
    private float[] floats;

    @Before
    public void setUp() throws Exception {
        String[] comps = new String[10];
        floats = new float[10];
        for (int i = 0; i < 10; i++) {
            floats[i] = (float) (i * 0.1);
            comps[i] = "a";
        }

        vectorSet = new SparseVectorSet(null, new Basis(comps));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSet() throws Exception {
        vectorSet.set(1, floats);
    }

    @Test
    public void testSetAndGet() throws Exception {
        vectorSet.set(1, floats);
        float[] ret = vectorSet.get(1);
        assertEquals(floats[0], ret[0]);
        assertEquals(floats[1], ret[1]);
        assertEquals(floats[4], ret[4]);
    }

    @Test(expected = Exception.class)
    public void testSetInvalidLength() throws Exception {
        for (int i = 0; i < 11; i++) {
            floats[i] = (float) (i * 0.01);
        }
        vectorSet.set(1, floats);
    }

    @Test
    public void testSetTwice() throws Exception {
        vectorSet.set(1, floats);
        floats[0] = floats[0] * 6;
        vectorSet.set(1, floats);
        float[] ret = vectorSet.get(1);
        assertNotNull(ret);
        assertEquals(floats[0], ret[0]);
    }

    @Test
    public void testChangeVectorLength() throws Exception {
        vectorSet.set(1, new float[] { (float) 0.1 });
        vectorSet.set(1, floats);
        float[] ret = vectorSet.get(1);
        assertNotNull(ret);
        for (int i = 0; i < floats.length; i++) {
            assertEquals(floats[i], ret[i]);
        }

        vectorSet.set(2, floats);
        vectorSet.set(2, new float[] { (float) 0.1, (float) 0.2 });
        ret = vectorSet.get(2);
        assertNotNull(ret);
        assertEquals(10, ret.length);
        assertEquals(ret[0], (float) 0.1);
        assertEquals(ret[1], (float) 0.2);
        for (int i = 2; i < ret.length; i++) {
            assertEquals((float)0.0, ret[i]);
        }
    }
}

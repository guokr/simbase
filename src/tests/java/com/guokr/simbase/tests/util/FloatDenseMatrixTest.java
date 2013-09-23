package com.guokr.simbase.tests.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.guokr.simbase.util.Basis;
import com.guokr.simbase.util.FloatDenseMatrix;

public class FloatDenseMatrixTest {
    private FloatDenseMatrix matrix = null;
    float[]                  floats;

    @Before
    public void setUp() throws Exception {
        matrix = new FloatDenseMatrix(null, new Basis(new String[] { "a", "b", "c", "d", "e" }));
        floats = new float[] { (float) 0.1, (float) 0.2 };
    }

    @After
    public void tearDown() throws Exception {
        matrix = null;
    }

    @Test
    public void testSet() throws Exception {
        matrix.set(1, floats);
        assertEquals(floats[0], matrix.get(1)[0]);
        assertEquals(floats[1], matrix.get(1)[1]);
    }

    @Test
    public void testRepeatSet() throws Exception {
        for (int i = 0; i < 10000; i++) {
            matrix.set(1, floats);
        }
        floats[0] = floats[0] * 2;
        matrix.set(1, floats);
        assertEquals(floats[0], matrix.get(1)[0]);
    }

    @Test
    public void testGet() throws Exception {
        for (int i = 0; i < 10000; i++) {
            matrix.set(i, floats);
        }
        for (int i = 0; i < 10000; i++) {
            assertNotNull(matrix.get(i));
        }
    }

    @Test
    public void testRemove() throws Exception {
        for (int i = 0; i < 10000; i++) {
            matrix.set(i, floats);
        }
        for (int i = 0; i < 10000; i++) {
            matrix.remove(i);
        }
        for (int i = 0; i < 10000; i++) {
            assertNull(matrix.get(i));
        }
    }
}

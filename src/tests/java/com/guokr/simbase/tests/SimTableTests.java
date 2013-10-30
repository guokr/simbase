package com.guokr.simbase.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.guokr.simbase.SimTable;

public class SimTableTests {

    private void validator(String[] set) {
        int i = 1;
        float beforeValue = 1.0f;
        int beforeId = set.length / 2;
        while (i < set.length) {
            float thisValue = Float.parseFloat(set[i]);
            int thisId = Integer.parseInt(set[i - 1]);
            assertTrue(beforeValue > thisValue || beforeValue == thisValue && beforeId > thisId);
            beforeValue = thisValue;
            beforeId = thisId;
            i = i + 2;
        }
    }

    @Test
    public void test3d() {
        SimTable table = new SimTable();

        table.add(2, new float[] { 0.9f, 0.1f, 0f });
        table.add(3, new float[] { 0.9f, 0f, 0.1f });
        table.add(5, new float[] { 0.1f, 0.9f, 0f });
        table.add(7, new float[] { 0.1f, 0f, 0.9f });
        table.add(11, new float[] { 0f, 0.9f, 0.1f });
        table.add(13, new float[] { 0f, 0.1f, 0.9f });

        System.out.println(Arrays.asList(table.retrieve(2)));
        System.out.println(Arrays.asList(table.retrieve(3)));
        System.out.println(Arrays.asList(table.retrieve(5)));
        System.out.println(Arrays.asList(table.retrieve(7)));
        System.out.println(Arrays.asList(table.retrieve(11)));
        System.out.println(Arrays.asList(table.retrieve(13)));

        assertTrue((int) (1000 * table.similarity(2, 3)) == (int) (1000 * table.similarity(5, 11)));
        assertTrue((int) (1000 * table.similarity(2, 3)) == (int) (1000 * table.similarity(7, 13)));
        assertTrue((int) (1000 * table.similarity(2, 5)) == (int) (1000 * table.similarity(3, 7)));
        assertTrue((int) (1000 * table.similarity(2, 5)) == (int) (1000 * table.similarity(11, 13)));
        assertTrue((int) (1000 * table.similarity(2, 7)) == (int) (1000 * table.similarity(3, 5)));
        assertTrue((int) (1000 * table.similarity(2, 7)) == (int) (1000 * table.similarity(7, 11)));
        assertTrue((int) (1000 * table.similarity(2, 11)) == (int) (1000 * table.similarity(3, 13)));
        assertTrue((int) (1000 * table.similarity(2, 13)) == (int) (1000 * table.similarity(5, 7)));

        table.add(2, new float[] { 0f, 0.1f, 0.9f });
        table.add(3, new float[] { 0.1f, 0f, 0.9f });
        table.add(5, new float[] { 0f, 0.9f, 0.1f });
        table.add(7, new float[] { 0.9f, 0f, 0.1f });
        table.add(11, new float[] { 0.1f, 0.9f, 0f });
        table.add(13, new float[] { 0.9f, 0.1f, 0f });

        System.out.println(Arrays.asList(table.retrieve(2)));
        System.out.println(Arrays.asList(table.retrieve(3)));
        System.out.println(Arrays.asList(table.retrieve(5)));
        System.out.println(Arrays.asList(table.retrieve(7)));
        System.out.println(Arrays.asList(table.retrieve(11)));
        System.out.println(Arrays.asList(table.retrieve(13)));

        assertTrue((int) (1000 * table.similarity(2, 3)) == (int) (1000 * table.similarity(5, 11)));
        assertTrue((int) (1000 * table.similarity(2, 3)) == (int) (1000 * table.similarity(7, 13)));
        assertTrue((int) (1000 * table.similarity(2, 5)) == (int) (1000 * table.similarity(3, 7)));
        assertTrue((int) (1000 * table.similarity(2, 5)) == (int) (1000 * table.similarity(11, 13)));
        assertTrue((int) (1000 * table.similarity(2, 7)) == (int) (1000 * table.similarity(3, 5)));
        assertTrue((int) (1000 * table.similarity(2, 7)) == (int) (1000 * table.similarity(7, 11)));
        assertTrue((int) (1000 * table.similarity(2, 11)) == (int) (1000 * table.similarity(3, 13)));
        assertTrue((int) (1000 * table.similarity(2, 13)) == (int) (1000 * table.similarity(5, 7)));

    }

    @Test
    public void test4d() {
        SimTable table = new SimTable();

        table.add(0, new float[] { 0.18257418583505536f, 0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f });
        table.add(1, new float[] { 0.18257418583505536f, 0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f });
        table.add(2, new float[] { 0.18257418583505536f, 0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f });
        table.add(3, new float[] { 0.18257418583505536f, 0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f });
        table.add(4, new float[] { 0.18257418583505536f, 0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f });
        table.add(5, new float[] { 0.18257418583505536f, 0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f });
        table.add(6, new float[] { 0.3651483716701107f, 0.18257418583505536f, 0.5477225575051661f, 0.7302967433402214f });
        table.add(7, new float[] { 0.3651483716701107f, 0.18257418583505536f, 0.7302967433402214f, 0.5477225575051661f });
        table.add(8, new float[] { 0.3651483716701107f, 0.5477225575051661f, 0.18257418583505536f, 0.7302967433402214f });
        table.add(9, new float[] { 0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f, 0.18257418583505536f });
        table.add(10, new float[] { 0.3651483716701107f, 0.7302967433402214f, 0.18257418583505536f, 0.5477225575051661f });
        table.add(11, new float[] { 0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f, 0.18257418583505536f });
        table.add(12, new float[] { 0.5477225575051661f, 0.18257418583505536f, 0.3651483716701107f, 0.7302967433402214f });
        table.add(13, new float[] { 0.5477225575051661f, 0.18257418583505536f, 0.7302967433402214f, 0.3651483716701107f });
        table.add(14, new float[] { 0.5477225575051661f, 0.3651483716701107f, 0.18257418583505536f, 0.7302967433402214f });
        table.add(15, new float[] { 0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f, 0.18257418583505536f });
        table.add(16, new float[] { 0.5477225575051661f, 0.7302967433402214f, 0.18257418583505536f, 0.3651483716701107f });
        table.add(17, new float[] { 0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f, 0.18257418583505536f });
        table.add(18, new float[] { 0.7302967433402214f, 0.18257418583505536f, 0.3651483716701107f, 0.5477225575051661f });
        table.add(19, new float[] { 0.7302967433402214f, 0.18257418583505536f, 0.5477225575051661f, 0.3651483716701107f });
        table.add(20, new float[] { 0.7302967433402214f, 0.3651483716701107f, 0.18257418583505536f, 0.5477225575051661f });
        table.add(21, new float[] { 0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f, 0.18257418583505536f });
        table.add(22, new float[] { 0.7302967433402214f, 0.5477225575051661f, 0.18257418583505536f, 0.3651483716701107f });
        table.add(23, new float[] { 0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f, 0.18257418583505536f });

        int count = 0;
        while (count < 24) {
            System.out.println(Arrays.asList(table.retrieve(count)));
            count++;
        }
        assertTrue((int) (1000 * table.similarity(0, 1)) == (int) (1000 * table.similarity(2, 4)));

        count = 0;
        while (count < 24) {
            String[] result = table.retrieve(count);
            validator(result);
            count++;
        }
    }

    @Test
    public void testClone() {
        SimTable table = new SimTable();

        table.add(2, new float[] { 0.9f, 0.1f, 0f });
        table.add(3, new float[] { 0.9f, 0f, 0.1f });
        table.add(5, new float[] { 0.1f, 0.9f, 0f });
        table.add(6, new float[] { 0.2f, 0.8f, 0f });
        System.out.println(Arrays.asList(table.retrieve(2)));
        System.out.println(Arrays.asList(table.retrieve(3)));
        System.out.println(Arrays.asList(table.retrieve(5)));
        System.out.println(Arrays.asList(table.retrieve(6)));

        table.delete(6);
        table = table.clone();
        assertTrue(table.retrieve(6).length == 0);
        System.out.println(Arrays.asList(table.retrieve(2)));
        System.out.println(Arrays.asList(table.retrieve(3)));
        System.out.println(Arrays.asList(table.retrieve(5)));

        table.add(7, new float[] { 0.1f, 0f, 0.9f });
        table.add(11, new float[] { 0f, 0.9f, 0.1f });
        table.add(12, new float[] { 0.8f, 0.2f, 0f });
        table.add(13, new float[] { 0f, 0.1f, 0.9f });

        table.delete(12);
        table = table.clone();

        assertTrue((int) (1000 * table.similarity(2, 3)) == (int) (1000 * table.similarity(5, 11)));
        assertTrue((int) (1000 * table.similarity(2, 3)) == (int) (1000 * table.similarity(7, 13)));
        assertTrue((int) (1000 * table.similarity(2, 5)) == (int) (1000 * table.similarity(3, 7)));
        assertTrue((int) (1000 * table.similarity(2, 5)) == (int) (1000 * table.similarity(11, 13)));
        assertTrue((int) (1000 * table.similarity(2, 7)) == (int) (1000 * table.similarity(3, 5)));
        assertTrue((int) (1000 * table.similarity(2, 7)) == (int) (1000 * table.similarity(7, 11)));
        assertTrue((int) (1000 * table.similarity(2, 11)) == (int) (1000 * table.similarity(3, 13)));
        assertTrue((int) (1000 * table.similarity(2, 13)) == (int) (1000 * table.similarity(5, 7)));
    }

}

package com.guokr.simbase.engine;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;
import com.guokr.simbase.engine.SimBasis;
import com.guokr.simbase.events.VectorSetListener;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class SimTableTests {
    public static SimEngineImpl engine;

    @BeforeClass
    public static void testSetup() {
        SimConfig config = null;
        try {
            Yaml yaml = new Yaml();
            config = new SimConfig((Map<String, Object>) yaml.load(new FileReader("config/simbase.yaml")));
        } catch (FileNotFoundException e) {
        }
        engine = new SimEngineImpl(config.getSub("engine"));
        String[] components = new String[512];
        for (int i = 0; i < components.length; i++) {
            components[i] = "B" + String.valueOf(i);
        }
        try {
            engine.bmk(TestableCallback.noop(), "base", components);
            Thread.sleep(100);
            engine.vmk(TestableCallback.noop(), "base", "article");
            Thread.sleep(100);
            engine.rmk(TestableCallback.noop(), "article", "article", "cosinesq");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void testCleanup() {
        // Teardown for data used by the unit tests
    }

    @Test
    public void test3d() {
        engine.bget(TestableCallback.noop(), "base");
        // engine.vadd(callback, vkey, vecid, vector);
        engine.vadd(TestableCallback.noop(), "article", 2, new float[] { 0.9f, 0.1f, 0f });
        engine.vadd(TestableCallback.noop(), "article", 3, new float[] { 0.9f, 0f, 0.1f });
        engine.vadd(TestableCallback.noop(), "article", 5, new float[] { 0.1f, 0.9f, 0f });
        engine.vadd(TestableCallback.noop(), "article", 7, new float[] { 0.1f, 0f, 0.9f });
        engine.vadd(TestableCallback.noop(), "article", 11, new float[] { 0f, 0.9f, 0.1f });
        engine.vadd(TestableCallback.noop(), "article", 13, new float[] { 0f, 0.1f, 0.9f });
        engine.rrec(new TestableCallback() {

            @Override
            public void validator() {
                exceepted = [];
                assaertListEquals(excepted, decodeList());
            }

        }, "article", 13, "article");
        //
        // System.out.println(Arrays.asList(engine.retrieve(2)));
        // System.out.println(Arrays.asList(engine.retrieve(3)));
        // System.out.println(Arrays.asList(engine.retrieve(5)));
        // System.out.println(Arrays.asList(engine.retrieve(7)));
        // System.out.println(Arrays.asList(engine.retrieve(11)));
        // System.out.println(Arrays.asList(engine.retrieve(13)));
        //
        // assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 *
        // engine.similarity(5, 11)));
        // assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 *
        // engine.similarity(7, 13)));
        // assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 *
        // engine.similarity(3, 7)));
        // assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 *
        // engine.similarity(11, 13)));
        // assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 *
        // engine.similarity(3, 5)));
        // assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 *
        // engine.similarity(7, 11)));
        // assertTrue((int) (1000 * engine.similarity(2, 11)) == (int) (1000 *
        // engine.similarity(3, 13)));
        // assertTrue((int) (1000 * engine.similarity(2, 13)) == (int) (1000 *
        // engine.similarity(5, 7)));
        //
        // engine.vset(2, new float[] { 0f, 0.1f, 0.9f });
        // engine.vset(3, new float[] { 0.1f, 0f, 0.9f });
        // engine.vset(5, new float[] { 0f, 0.9f, 0.1f });
        // engine.vset(7, new float[] { 0.9f, 0f, 0.1f });
        // engine.vset(11, new float[] { 0.1f, 0.9f, 0f });
        // engine.vset(13, new float[] { 0.9f, 0.1f, 0f });
        //
        // System.out.println(Arrays.asList(engine.retrieve(2)));
        // System.out.println(Arrays.asList(engine.retrieve(3)));
        // System.out.println(Arrays.asList(engine.retrieve(5)));
        // System.out.println(Arrays.asList(engine.retrieve(7)));
        // System.out.println(Arrays.asList(engine.retrieve(11)));
        // System.out.println(Arrays.asList(engine.retrieve(13)));
        //
        // assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 *
        // engine.similarity(5, 11)));
        // assertTrue((int) (1000 * engine.similarity(2, 3)) == (int) (1000 *
        // engine.similarity(7, 13)));
        // assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 *
        // engine.similarity(3, 7)));
        // assertTrue((int) (1000 * engine.similarity(2, 5)) == (int) (1000 *
        // engine.similarity(11, 13)));
        // assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 *
        // engine.similarity(3, 5)));
        // assertTrue((int) (1000 * engine.similarity(2, 7)) == (int) (1000 *
        // engine.similarity(7, 11)));
        // assertTrue((int) (1000 * engine.similarity(2, 11)) == (int) (1000 *
        // engine.similarity(3, 13)));
        // assertTrue((int) (1000 * engine.similarity(2, 13)) == (int) (1000 *
        // engine.similarity(5, 7)));

    }

    @Test
    public void test4d() {
        SimBasis table = new SimBasis();

        table.vset(0,
                new float[] { 0.18257418583505536f, 0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f });
        table.vset(1,
                new float[] { 0.18257418583505536f, 0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f });
        table.vset(2,
                new float[] { 0.18257418583505536f, 0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f });
        table.vset(3,
                new float[] { 0.18257418583505536f, 0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f });
        table.vset(4,
                new float[] { 0.18257418583505536f, 0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f });
        table.vset(5,
                new float[] { 0.18257418583505536f, 0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f });
        table.vset(6,
                new float[] { 0.3651483716701107f, 0.18257418583505536f, 0.5477225575051661f, 0.7302967433402214f });
        table.vset(7,
                new float[] { 0.3651483716701107f, 0.18257418583505536f, 0.7302967433402214f, 0.5477225575051661f });
        table.vset(8,
                new float[] { 0.3651483716701107f, 0.5477225575051661f, 0.18257418583505536f, 0.7302967433402214f });
        table.vset(9,
                new float[] { 0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f, 0.18257418583505536f });
        table.vset(10, new float[] { 0.3651483716701107f, 0.7302967433402214f, 0.18257418583505536f,
                0.5477225575051661f });
        table.vset(11, new float[] { 0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f,
                0.18257418583505536f });
        table.vset(12, new float[] { 0.5477225575051661f, 0.18257418583505536f, 0.3651483716701107f,
                0.7302967433402214f });
        table.vset(13, new float[] { 0.5477225575051661f, 0.18257418583505536f, 0.7302967433402214f,
                0.3651483716701107f });
        table.vset(14, new float[] { 0.5477225575051661f, 0.3651483716701107f, 0.18257418583505536f,
                0.7302967433402214f });
        table.vset(15, new float[] { 0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f,
                0.18257418583505536f });
        table.vset(16, new float[] { 0.5477225575051661f, 0.7302967433402214f, 0.18257418583505536f,
                0.3651483716701107f });
        table.vset(17, new float[] { 0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f,
                0.18257418583505536f });
        table.vset(18, new float[] { 0.7302967433402214f, 0.18257418583505536f, 0.3651483716701107f,
                0.5477225575051661f });
        table.vset(19, new float[] { 0.7302967433402214f, 0.18257418583505536f, 0.5477225575051661f,
                0.3651483716701107f });
        table.vset(20, new float[] { 0.7302967433402214f, 0.3651483716701107f, 0.18257418583505536f,
                0.5477225575051661f });
        table.vset(21, new float[] { 0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f,
                0.18257418583505536f });
        table.vset(22, new float[] { 0.7302967433402214f, 0.5477225575051661f, 0.18257418583505536f,
                0.3651483716701107f });
        table.vset(23, new float[] { 0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f,
                0.18257418583505536f });

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

}

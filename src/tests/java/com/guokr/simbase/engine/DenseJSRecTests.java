package com.guokr.simbase.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class DenseJSRecTests {
    public static SimEngineImpl engine;

    @BeforeClass
    public static void setup() throws Exception {
        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        Map<String, Object> basis = new HashMap<String, Object>();
        Map<String, Object> dense = new HashMap<String, Object>();
        Map<String, Object> econf = new HashMap<String, Object>();
        dense.put("accumuFactor", 10.0);
        dense.put("sparseFactor", 2048);
        basis.put("vectorSetType", "dense");
        basis.put("maxlimits", 20);
        econf.put("savepath", "data");
        econf.put("saveinterval", 7200000);
        econf.put("loadfactor", 0.75);
        econf.put("bycount", 100);
        defaults.put("dense", dense);
        defaults.put("basis", basis);
        defaults.put("engine", econf);
        settings.put("defaults", defaults);
        SimConfig config = new SimConfig(settings);
        engine = new SimEngineImpl(config.getSub("engine"));
    }

    @Before
    public void testUp() throws Exception {
        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            components[i] = "B" + String.valueOf(i);
        }
        engine.bmk(TestableCallback.noop(), "btest", components);
        Thread.sleep(100);
        engine.vmk(TestableCallback.noop(), "btest", "vtest");
        Thread.sleep(100);
        engine.vmk(TestableCallback.noop(), "btest", "vtest2");
        Thread.sleep(100);

        engine.vadd(TestableCallback.noop(), "vtest", 2, new float[] { 0.9f, 0.09f, 0.01f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 3, new float[] { 0.89f, 0f, 0.11f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 5, new float[] { 0.1f, 0.89f, 0.01f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 7, new float[] { 0.09f, 0f, 0.91f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 11, new float[] { 0f, 0.89f, 0.11f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 13, new float[] { 0f, 0.09f, 0.91f });
        Thread.sleep(100);

        engine.vadd(TestableCallback.noop(), "vtest2", 2, new float[] { 0.9f, 0.09f, 0.01f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest2", 3, new float[] { 0.89f, 0f, 0.11f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest2", 5, new float[] { 0.1f, 0.89f, 0.01f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest2", 7, new float[] { 0.09f, 0f, 0.91f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest2", 11, new float[] { 0f, 0.89f, 0.11f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest2", 13, new float[] { 0f, 0.09f, 0.91f });
        Thread.sleep(100);

    }

    @After
    public void testDown() throws Exception {
        engine.del(TestableCallback.noop(), "btest");
        Thread.sleep(2000);
    }

    @Test
    public void testRec() throws Exception {
        Thread.sleep(100);
        engine.rmk(TestableCallback.noop(), "vtest", "vtest", "jensenshannon");
        Thread.sleep(100);
        engine.rmk(TestableCallback.noop(), "vtest", "vtest2", "jensenshannon");
        Thread.sleep(100);

        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3, 2 });
            }
        };
        engine.rrec(test, "vtest", 13, "vtest");
        test.waitForFinish();
        test.validate();
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 13, 3, 2, 11, 5 });
            }
        };
        engine.rrec(test2, "vtest", 7, "vtest2");
        test2.waitForFinish();
        test2.validate();
    }

    @Test
    public void testSetRec() throws Exception {
        engine.vset(TestableCallback.noop(), "vtest", 13, new float[] { 0.9f, 0.09f, 0.01f });
        Thread.sleep(100);
        engine.rmk(TestableCallback.noop(), "vtest", "vtest", "cosinesq");
        Thread.sleep(100);
        engine.rmk(TestableCallback.noop(), "vtest", "vtest2", "cosinesq");
        Thread.sleep(100);
        engine.vset(TestableCallback.noop(), "vtest2", 7, new float[] { 0.1f, 8f, 0.1f });
        Thread.sleep(100);
        engine.vset(TestableCallback.noop(), "vtest", 13, new float[] { 0f, 0.09f, 0.91f });
        Thread.sleep(100);
        engine.vset(TestableCallback.noop(), "vtest2", 7, new float[] { 0.09f, 0f, 0.91f });
        Thread.sleep(100);

        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 3, 5, 2 });
            }
        };
        engine.rrec(test, "vtest", 13, "vtest");
        test.waitForFinish();
        test.validate();
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 13, 3, 11, 2, 5 });
            }
        };
        engine.rrec(test2, "vtest", 7, "vtest2");
        test2.waitForFinish();
        test2.validate();
    }

}

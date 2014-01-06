package com.guokr.simbase.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class Sparse3DGeneralTests {
    public static SimEngineImpl engine;

    @BeforeClass
    public static void setup() {

        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        Map<String, Object> basis = new HashMap<String, Object>();
        Map<String, Object> sparse = new HashMap<String, Object>();
        Map<String, Object> econf = new HashMap<String, Object>();
        sparse.put("accumuFactor", 0.01);
        sparse.put("sparseFactor", 2048);
        basis.put("vectorSetType", "sparse");
        econf.put("savepath", "data");
        econf.put("saveinterval", 7200000);
        econf.put("maxlimits", 20);
        econf.put("loadfactor", 0.75);
        econf.put("bycount", 100);
        defaults.put("sparse", sparse);
        defaults.put("basis", basis);
        defaults.put("engine", econf);
        settings.put("defaults", defaults);
        SimConfig config = new SimConfig(settings);

        engine = new SimEngineImpl(config.getSub("engine"));

        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            components[i] = "B" + String.valueOf(i);
        }
        try {
            engine.bmk(TestableCallback.noop(), "base", components);
            Thread.sleep(100);
            engine.vmk(TestableCallback.noop(), "base", "article");
            Thread.sleep(100);
            engine.rmk(TestableCallback.noop(), "article", "article", "jensenshannon");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        engine.bget(TestableCallback.noop(), "base");
        try {
            engine.vadd(TestableCallback.noop(), "article", 2, new float[] { 0.9f, 0.1f, 0f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "article", 3, new float[] { 0.9f, 0f, 0.1f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "article", 5, new float[] { 0.1f, 0.9f, 0f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "article", 7, new float[] { 0.1f, 0f, 0.9f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "article", 11, new float[] { 0f, 0.9f, 0.1f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "article", 13, new float[] { 0f, 0.1f, 0.9f });
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCase1() {
        TestableCallback testbrev = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.brev(testbrev, "base", new String[] { "B3", "B1", "B0" });
        testbrev.waitForFinish();
        testbrev.validate();
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0.1f, 0f, 0f });
            }
        };
        engine.vget(test2, "article", 2);
        test2.waitForFinish();
        test2.validate();

        TestableCallback test3 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0f, 0.1f, 0f });
            }
        };
        engine.vget(test3, "article", 3);
        test3.waitForFinish();
        test3.validate();

        TestableCallback test5 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.9f, 0f, 0f });
            }
        };
        engine.vget(test5, "article", 5);
        test5.waitForFinish();
        test5.validate();

        TestableCallback test7 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0f, 0.9f, 0f });
            }
        };
        engine.vget(test7, "article", 7);
        test7.waitForFinish();
        test7.validate();

        TestableCallback test11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.9f, 0.1f, 0f });
            }
        };
        engine.vget(test11, "article", 11);
        test11.waitForFinish();
        test11.validate();

        TestableCallback test13 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.1f, 0.9f, 0f });
            }
        };
        engine.vget(test13, "article", 13);
        test13.waitForFinish();
        test13.validate();
    }
}

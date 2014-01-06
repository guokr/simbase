package com.guokr.simbase.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class Dense3DGeneralTests {
    public static SimEngineImpl engine;

    @BeforeClass
    public static void setup() throws Exception {

        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        Map<String, Object> basis = new HashMap<String, Object>();
        Map<String, Object> dense = new HashMap<String, Object>();
        Map<String, Object> econf = new HashMap<String, Object>();
        dense.put("accumuFactor", 0.01);
        dense.put("sparseFactor", 2048);
        basis.put("vectorSetType", "dense");
        econf.put("savepath", "data");
        econf.put("saveinterval", 7200000);
        econf.put("maxlimits", 20);
        econf.put("loadfactor", 0.75);
        econf.put("bycount", 100);
        defaults.put("dense", dense);
        defaults.put("basis", basis);
        defaults.put("engine", econf);
        settings.put("defaults", defaults);
        SimConfig config = new SimConfig(settings);

        engine = new SimEngineImpl(config.getSub("engine"));

        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            components[i] = "B" + String.valueOf(i);
        }

        engine.bmk(TestableCallback.noop(), "base", components);
        Thread.sleep(100);

    }

    @Before
    public void testUp() throws Exception {
        engine.vmk(TestableCallback.noop(), "base", "test");
        Thread.sleep(100);
        engine.rmk(TestableCallback.noop(), "test", "test", "jensenshannon");
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "test", 2, new float[] { 0.9f, 0.1f, 0f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "test", 3, new float[] { 0.9f, 0f, 0.1f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "test", 5, new float[] { 0.1f, 0.9f, 0f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "test", 7, new float[] { 0.1f, 0f, 0.9f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "test", 11, new float[] { 0f, 0.9f, 0.1f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "test", 13, new float[] { 0f, 0.1f, 0.9f });
        Thread.sleep(100);
    }

    @After
    public void testDown() throws Exception {
        engine.del(TestableCallback.noop(), "test");
        Thread.sleep(2000);
    }

    @Test
    public void testBRev() throws Exception {
        TestableCallback testbrev = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.brev(testbrev, "base", new String[] { "B3", "B1", "B0" });
        engine.vset(TestableCallback.noop(), "test", 13, new float[] { 0.1f, 0.2f, 0.3f, 0.4f });
        engine.vset(TestableCallback.noop(), "test", 17, new float[] { 0.4f, 0.3f, 0.2f, 0.1f });
        testbrev.waitForFinish();
        testbrev.validate();
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0.1f, 0f, 0f });
            }
        };
        engine.vget(test2, "test", 2);
        test2.waitForFinish();
        test2.validate();

        TestableCallback test3 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0f, 0.1f, 0f });
            }
        };
        engine.vget(test3, "test", 3);
        test3.waitForFinish();
        test3.validate();

        TestableCallback test5 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.9f, 0f, 0f });
            }
        };
        engine.vget(test5, "test", 5);
        test5.waitForFinish();
        test5.validate();

        TestableCallback test7 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0f, 0.9f, 0f });
            }
        };
        engine.vget(test7, "test", 7);
        test7.waitForFinish();
        test7.validate();

        TestableCallback test11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.9f, 0.1f, 0f });
            }
        };
        engine.vget(test11, "test", 11);
        test11.waitForFinish();
        test11.validate();

        TestableCallback test13 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.2f, 0.3f, 0.4f });
            }
        };
        engine.vget(test13, "test", 13);
        test13.waitForFinish();
        test13.validate();

        TestableCallback test17 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.4f, 0.3f, 0.2f, 0.1f });
            }
        };
        engine.vget(test17, "test", 17);
        test17.waitForFinish();
        test17.validate();
    }

    @Test
    public void testSaveLoad() throws Exception {
        engine.vrem(TestableCallback.noop(), "test", 3);
        Thread.sleep(100);
        engine.vrem(TestableCallback.noop(), "test", 7);
        Thread.sleep(100);
        engine.bsave(TestableCallback.noop(), "test");
        Thread.sleep(2000);
        engine.del(TestableCallback.noop(), "test");
        Thread.sleep(100);

        TestableCallback testEmpty = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[0]);
            }
        };
        engine.bget(testEmpty, "test");
        testEmpty.waitForFinish();
        testEmpty.validate();

        engine.bload(TestableCallback.noop(), "test");
        Thread.sleep(100);

        TestableCallback testIds = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 2, 5, 11, 13 });
            }
        };
        engine.vids(testIds, "test");
        testIds.waitForFinish();
        testIds.validate();
    }

}

package com.guokr.simbase.engine;

import java.io.File;
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

        engine.bmk(TestableCallback.noop(), "btest", components);
        Thread.sleep(100);
    }

    @Before
    public void testUp() throws Exception {
        engine.vmk(TestableCallback.noop(), "btest", "vtest");
        Thread.sleep(100);
        engine.rmk(TestableCallback.noop(), "vtest", "vtest", "jensenshannon");
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 2, new float[] { 0.9f, 0.1f, 0f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 3, new float[] { 0.9f, 0f, 0.1f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 5, new float[] { 0.1f, 0.9f, 0f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 7, new float[] { 0.1f, 0f, 0.9f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 11, new float[] { 0f, 0.9f, 0.1f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 13, new float[] { 0f, 0.1f, 0.9f });
        Thread.sleep(100);
    }

    @After
    public void testDown() throws Exception {
        engine.del(TestableCallback.noop(), "vtest");
        Thread.sleep(5000);
    }

    @Test
    public void testBRev() throws Exception {
        TestableCallback testbrev = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.brev(testbrev, "btest", new String[] { "B3", "B1", "B0" });
        engine.vset(TestableCallback.noop(), "vtest", 13, new float[] { 0.1f, 0.2f, 0.3f, 0.4f });
        engine.vset(TestableCallback.noop(), "vtest", 17, new float[] { 0.4f, 0.3f, 0.2f, 0.1f });
        testbrev.waitForFinish();
        testbrev.validate();
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0.1f, 0f, 0f });
            }
        };
        engine.vget(test2, "vtest", 2);
        test2.waitForFinish();
        test2.validate();

        TestableCallback test3 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0f, 0.1f, 0f });
            }
        };
        engine.vget(test3, "vtest", 3);
        test3.waitForFinish();
        test3.validate();

        TestableCallback test5 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.9f, 0f, 0f });
            }
        };
        engine.vget(test5, "vtest", 5);
        test5.waitForFinish();
        test5.validate();

        TestableCallback test7 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0f, 0.9f, 0f });
            }
        };
        engine.vget(test7, "vtest", 7);
        test7.waitForFinish();
        test7.validate();

        TestableCallback test11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.9f, 0.1f, 0f });
            }
        };
        engine.vget(test11, "vtest", 11);
        test11.waitForFinish();
        test11.validate();

        TestableCallback test13 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.2f, 0.3f, 0.4f });
            }
        };
        engine.vget(test13, "vtest", 13);
        test13.waitForFinish();
        test13.validate();

        TestableCallback test17 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.4f, 0.3f, 0.2f, 0.1f });
            }
        };
        engine.vget(test17, "vtest", 17);
        test17.waitForFinish();
        test17.validate();
    }

    @Test
    public void testSaveLoad() throws Exception {
        engine.vrem(TestableCallback.noop(), "vtest", 3);
        Thread.sleep(100);
        engine.vrem(TestableCallback.noop(), "vtest", 7);
        Thread.sleep(100);
        engine.bsave(TestableCallback.noop(), "btest");
        Thread.sleep(4000);
        try {
            engine.del(TestableCallback.noop(), "btest");
            Thread.sleep(1000);

            TestableCallback testEmpty = new TestableCallback() {
                @Override
                public void excepted() {
                    isStringList(new String[0]);
                }
            };
            engine.blist(testEmpty);
            testEmpty.waitForFinish();
            testEmpty.validate();

            engine.bload(TestableCallback.noop(), "btest");
            Thread.sleep(300);
        } catch (Throwable t) {
            throw t;
        } finally {
            File file = new File("./data/btest.dmp");
            file.delete();
        }

        TestableCallback testBases = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "btest" });
            }
        };
        engine.blist(testBases);
        testBases.waitForFinish();
        testBases.validate();

        TestableCallback testVecs = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "vtest" });
            }
        };
        engine.vlist(testVecs, "btest");
        testVecs.waitForFinish();
        testVecs.validate();

        TestableCallback testIds = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 2, 5, 11, 13 });
            }
        };
        engine.vids(testIds, "vtest");
        testIds.waitForFinish();
        testIds.validate();

    }

}

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

public class SparseJSComplexTests {
    public static SimEngineImpl engine;

    @BeforeClass
    public static void setup() throws Exception {
        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        Map<String, Object> basis = new HashMap<String, Object>();
        Map<String, Object> sparse = new HashMap<String, Object>();
        Map<String, Object> econf = new HashMap<String, Object>();
        sparse.put("accumuFactor", 0.5);
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
        engine.rmk(TestableCallback.noop(), "vtest", "vtest", "jensenshannon");
        Thread.sleep(100);
        engine.rmk(TestableCallback.noop(), "vtest", "vtest2", "jensenshannon");
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
                isFloatList(new float[] { 0.9f, 0.09f, 0.01f, 0f });
            }
        };
        engine.vget(test2, "vtest", 2);
        test2.waitForFinish();
        test2.validate();

        TestableCallback test3 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.89f, 0f, 0.11f, 0f });
            }
        };
        engine.vget(test3, "vtest", 3);
        test3.waitForFinish();
        test3.validate();

        TestableCallback test5 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.89f, 0.01f, 0f });
            }
        };
        engine.vget(test5, "vtest", 5);
        test5.waitForFinish();
        test5.validate();

        TestableCallback test7 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.09f, 0f, 0.91f, 0f });
            }
        };
        engine.vget(test7, "vtest", 7);
        test7.waitForFinish();
        test7.validate();

        TestableCallback test11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.89f, 0.11f, 0f });
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

    /**
     * @throws Exception
     */
    @Test
    public void testSaveLoad() throws Exception {
        TestableCallback testRrec = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 3, 5, 7, 11, 13 });
            }
        };
        engine.rrec(testRrec, "vtest", 2, "vtest");
        testRrec.waitForFinish();
        testRrec.validate();

        TestableCallback testRrec2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 2, 3, 5, 7, 11, 13 });
            }
        };

        engine.vids(testRrec2, "vtest2");
        testRrec2.waitForFinish();
        testRrec2.validate();

        engine.rrec(testRrec2, "vtest", 2, "vtest2");
        testRrec2.waitForFinish();
        testRrec2.validate();

        TestableCallback testRrec11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 11, 5, 13, 7, 2, 3 });
            }
        };

        engine.rrec(testRrec11, "vtest", 11, "vtest2");
        testRrec11.waitForFinish();
        testRrec11.validate();

        engine.vrem(TestableCallback.noop(), "vtest", 3);
        engine.vrem(TestableCallback.noop(), "vtest2", 3);
        Thread.sleep(100);
        engine.vrem(TestableCallback.noop(), "vtest", 7);
        engine.vrem(TestableCallback.noop(), "vtest2", 7);
        Thread.sleep(100);
        engine.bsave(TestableCallback.noop(), "btest");
        Thread.sleep(1000);
        try {
            engine.del(TestableCallback.noop(), "btest");
            Thread.sleep(400);

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
            Thread.sleep(400);
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
                isStringList(new String[] { "vtest", "vtest2" });
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

        TestableCallback testRlist = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "vtest", "vtest2" });
            }
        };
        engine.rlist(testRlist, "vtest");
        testRlist.waitForFinish();
        testRlist.validate();

        engine.vadd(TestableCallback.noop(), "vtest", 3, new float[] { 0.89f, 0f, 0.11f });
        engine.vadd(TestableCallback.noop(), "vtest2", 3, new float[] { 0.89f, 0f, 0.11f });
        Thread.sleep(100);
        engine.vadd(TestableCallback.noop(), "vtest", 7, new float[] { 0.09f, 0f, 0.91f });
        engine.vadd(TestableCallback.noop(), "vtest2", 7, new float[] { 0.09f, 0f, 0.91f });
        Thread.sleep(100);

        engine.rrec(testRrec, "vtest", 2, "vtest");
        testRrec.waitForFinish();
        testRrec.validate();
        engine.rrec(testRrec2, "vtest", 2, "vtest2");
        testRrec2.waitForFinish();
        testRrec2.validate();
        engine.rrec(testRrec11, "vtest", 11, "vtest2");
        testRrec11.waitForFinish();
        testRrec11.validate();

    }

    /**
     * @throws Exception
     */
    @Test
    public void testVacc() throws Exception {
        float[] tmp3 = new float[] { 0.2f, 0f, 0.8f };
        float[] tmp7 = new float[] { 0.4f, 0.41f, 0.19f };
        float[] tmp11 = new float[] { 0.6f, 0.11f, 0.29f };
        TestableCallback testRec = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 3, 5, 7, 11, 13 });
            }
        };
        engine.rrec(testRec, "vtest", 2, "vtest");
        Thread.sleep(100);
        testRec.waitForFinish();
        testRec.validate();

        TestableCallback testRec2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 2, 3, 5, 7, 11, 13 });
            }
        };
        engine.rrec(testRec2, "vtest", 2, "vtest2");
        Thread.sleep(100);
        testRec2.waitForFinish();
        testRec2.validate();

        engine.vacc(TestableCallback.noop(), "vtest", 3, tmp3);
        engine.vacc(TestableCallback.noop(), "vtest", 3, tmp3);
        engine.vacc(TestableCallback.noop(), "vtest", 7, tmp7);
        engine.vacc(TestableCallback.noop(), "vtest", 7, tmp7);
        engine.vacc(TestableCallback.noop(), "vtest", 11, tmp11);
        engine.vacc(TestableCallback.noop(), "vtest", 11, tmp11);
        engine.vacc(TestableCallback.noop(), "vtest2", 7, tmp7);
        engine.vacc(TestableCallback.noop(), "vtest2", 7, tmp7);
        Thread.sleep(200);

        TestableCallback testRrec = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 11, 7, 3, 5, 13 });
            }
        };
        engine.rrec(testRrec, "vtest", 2, "vtest");
        Thread.sleep(100);
        testRrec.waitForFinish();
        testRrec.validate();

        TestableCallback testRrec2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 2, 3, 7, 5, 11, 13 });
            }
        };
        engine.rrec(testRrec2, "vtest", 2, "vtest2");
        Thread.sleep(100);
        testRrec2.waitForFinish();
        testRrec2.validate();
    }
}

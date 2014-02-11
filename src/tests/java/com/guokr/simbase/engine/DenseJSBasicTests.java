package com.guokr.simbase.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class DenseJSBasicTests {
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
    }

    @After
    public void testDown() throws Exception {
        engine.del(TestableCallback.noop(), "vtest");
        Thread.sleep(1000);
    }

    @Test
    public void testRec() {
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
                isIntegerList(new int[] { 13, 3, 2, 11, 5 });
            }
        };
        engine.rrec(test2, "vtest", 7, "vtest");
        test2.waitForFinish();
        test2.validate();
    }

    @Test
    public void testVget() {
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0.09f, 0.01f });
            }
        };
        engine.vget(test2, "vtest", 2);
        test2.waitForFinish();
        test2.validate();

        TestableCallback test3 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.89f, 0f, 0.11f });
            }
        };
        engine.vget(test3, "vtest", 3);
        test3.waitForFinish();
        test3.validate();

        TestableCallback test5 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.89f, 0.01f });
            }
        };
        engine.vget(test5, "vtest", 5);
        test5.waitForFinish();
        test5.validate();

        TestableCallback test7 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.09f, 0f, 0.91f });
            }
        };
        engine.vget(test7, "vtest", 7);
        test7.waitForFinish();
        test7.validate();

        TestableCallback test11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.89f, 0.11f });
            }
        };
        engine.vget(test11, "vtest", 11);
        test11.waitForFinish();
        test11.validate();

        TestableCallback test13 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.09f, 0.91f });
            }
        };
        engine.vget(test13, "vtest", 13);
        test13.waitForFinish();
        test13.validate();
    }

    @Test
    public void testRlist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "vtest" });
            }
        };
        engine.rlist(test, "vtest");
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testVrem() throws Exception {
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };

        TestableCallback testRrec = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 3, 5, 7, 11, 13 });
            }
        };
        engine.rrec(testRrec, "vtest", 2, "vtest");
        testRrec.waitForFinish();
        testRrec.validate();

        engine.vrem(testok, "vtest", 5);
        engine.vrem(testok, "vtest", 7);
        testok.waitForFinish();
        testok.validate();
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 11, 3, 2 });
            }
        };
        engine.rrec(test, "vtest", 13, "vtest");
        test.waitForFinish();
        test.validate();
        engine.vadd(TestableCallback.noop(), "vtest", 5, new float[] { 0.1f, 0.89f, 0.01f });
        Thread.sleep(100);
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 11, 5, 3, 2 });
            }
        };
        engine.rrec(test2, "vtest", 13, "vtest");
        test2.waitForFinish();
        test2.validate();

        engine.vadd(TestableCallback.noop(), "vtest", 7, new float[] { 0.09f, 0f, 0.91f });
        Thread.sleep(100);

        engine.rrec(testRrec, "vtest", 2, "vtest");
        testRrec.waitForFinish();
        testRrec.validate();

    }

    // @Test
    public void testVset() {
        // replace 2 with 7 ,and 7 with 2
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vset(testok, "vtest", 2, new float[] { 0.1f, 0f, 0.9f });
        testok.waitForFinish();
        testok.validate();
        engine.vset(testok, "vtest", 7, new float[] { 0.9f, 0.1f, 0f });
        testok.waitForFinish();
        testok.validate();
        TestableCallback test1 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 2, 11, 5, 3, 7 });
            }
        };
        engine.rrec(test1, "vtest", 13, "vtest");
        // Restored to their original
        test1.waitForFinish();
        test1.validate();
        engine.vset(testok, "vtest", 2, new float[] { 0.9f, 0.1f, 0f });
        testok.waitForFinish();
        testok.validate();
        engine.vset(testok, "vtest", 7, new float[] { 0.1f, 0f, 0.9f });
        testok.waitForFinish();
        testok.validate();

        // so recommander restored
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3, 2 });
            }
        };
        engine.rrec(test2, "vtest", 13, "vtest");
        test2.waitForFinish();
        test2.validate();
    }

    @Test
    public void testBlist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "btest" });
            }
        };
        engine.blist(test);
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testVlist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "vtest" });
            }
        };
        engine.vlist(test, "btest");
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testVacc() {
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vacc(testok, "vtest", 5, new float[] { 0.1f, 0.9f, 0f });
        testok.waitForFinish();
        testok.validate();
        TestableCallback testget = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.2f, 1.79f, 0.01f });
            }
        };
        engine.vget(testget, "vtest", 5);
        testget.waitForFinish();
        testget.validate();
    }

    @Test
    public void testBrev() {
        TestableCallback testbrev = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.brev(testbrev, "btest", new String[] { "B2", "B1", "B0" });
        testbrev.waitForFinish();
        testbrev.validate();
    }
}

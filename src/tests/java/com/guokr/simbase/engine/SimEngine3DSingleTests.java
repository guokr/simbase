package com.guokr.simbase.engine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class SimEngine3DSingleTests {
    public static SimEngineImpl engine;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setup() {
        SimConfig config = null;
        try {
            Yaml yaml = new Yaml();
            config = new SimConfig((Map<String, Object>) yaml.load(new FileReader("config/simbase.yaml")));
        } catch (FileNotFoundException e) {
        }
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
            engine.rmk(TestableCallback.noop(), "article", "article", "cosinesq");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        engine.bget(TestableCallback.noop(), "base");
        try {
            engine.vadd(TestableCallback.noop(), "article", 2, new float[] { 0.9f, 0.1f, 0f });
            engine.vadd(TestableCallback.noop(), "article", 3, new float[] { 0.9f, 0f, 0.1f });
            engine.vadd(TestableCallback.noop(), "article", 5, new float[] { 0.1f, 0.9f, 0f });
            engine.vadd(TestableCallback.noop(), "article", 7, new float[] { 0.1f, 0f, 0.9f });
            engine.vadd(TestableCallback.noop(), "article", 11, new float[] { 0f, 0.9f, 0.1f });
            engine.vadd(TestableCallback.noop(), "article", 13, new float[] { 0f, 0.1f, 0.9f });
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testrec() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3, 2 });
            }
        };
        engine.rrec(test, "article", 13, "article");
        test.waitForFinish();
        test.validate();
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 13, 3, 11, 2, 5 });
            }
        };
        engine.rrec(test2, "article", 7, "article");
        test2.waitForFinish();
        test2.validate();
    }

    @Test
    public void testvget() {
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0.1f, 0f });
            }
        };
        engine.vget(test2, "article", 2);
        test2.waitForFinish();
        test2.validate();

        TestableCallback test3 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.9f, 0f, 0.1f });
            }
        };
        engine.vget(test3, "article", 3);
        test3.waitForFinish();
        test3.validate();

        TestableCallback test5 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.9f, 0f });
            }
        };
        engine.vget(test5, "article", 5);
        test5.waitForFinish();
        test5.validate();

        TestableCallback test7 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0f, 0.9f });
            }
        };
        engine.vget(test7, "article", 7);
        test7.waitForFinish();
        test7.validate();

        TestableCallback test11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.9f, 0.1f });
            }
        };
        engine.vget(test11, "article", 11);
        test11.waitForFinish();
        test11.validate();

        TestableCallback test13 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.1f, 0.9f });
            }
        };
        engine.vget(test13, "article", 13);
        test13.waitForFinish();
        test13.validate();
    }

    @Test
    public void testrlist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "article" });
            }
        };
        engine.rlist(test, "article");
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testvrem() {
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vrem(testok, "article", 2);
        testok.waitForFinish();
        testok.validate();
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3 });
            }
        };
        engine.rrec(test, "article", 13, "article");
        test.waitForFinish();
        test.validate();
        try {
            engine.vadd(TestableCallback.noop(), "article", 2, new float[] { 0.9f, 0.1f, 0f });
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3, 2 });
            }
        };
        engine.rrec(test2, "article", 13, "article");
        test2.waitForFinish();
        test2.validate();
    }

    // @Test
    public void testvset() {
        // replace 2 with 7 ,and 7 with 2
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vset(testok, "article", 2, new float[] { 0.1f, 0f, 0.9f });
        testok.waitForFinish();
        testok.validate();
        engine.vset(testok, "article", 7, new float[] { 0.9f, 0.1f, 0f });
        testok.waitForFinish();
        testok.validate();
        TestableCallback test1 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 2, 11, 5, 3, 7 });
            }
        };
        engine.rrec(test1, "article", 13, "article");
        // Restored to their original
        test1.waitForFinish();
        test1.validate();
        engine.vset(testok, "article", 2, new float[] { 0.9f, 0.1f, 0f });
        testok.waitForFinish();
        testok.validate();
        engine.vset(testok, "article", 7, new float[] { 0.1f, 0f, 0.9f });
        testok.waitForFinish();
        testok.validate();

        // so recommander restored
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3, 2 });
            }
        };
        engine.rrec(test2, "article", 13, "article");
        test2.waitForFinish();
        test2.validate();
    }

    @Test
    public void testblist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "base" });
            }
        };
        engine.blist(test);
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testvlist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "article" });
            }
        };
        engine.vlist(test, "base");
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testvacc() {
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vacc(testok, "article", 5, new float[] { 0.1f, 0.9f, 0f });
        testok.waitForFinish();
        testok.validate();
        TestableCallback testget = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.9f, 0f });
            }
        };
        engine.vget(testget, "article", 5);
        testget.waitForFinish();
        testget.validate();
    }

    @Test
    public void testbrev() {
        TestableCallback testbrev = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.brev(testbrev, "base", new String[] { "B2", "B1", "B0" });
        testbrev.waitForFinish();
        testbrev.validate();
    }
}

package com.guokr.simbase.engine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class SimEngine3DComplexTests {
    public static SimEngineImpl engine;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void testSetup() {
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
            engine.rmk(TestableCallback.noop(), "article", "article", "jensenshannon");
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
    public void testbrevAndVget() {
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

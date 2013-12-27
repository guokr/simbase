package com.guokr.simbase.engine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class SimEngineTests {
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
            engine.rmk(TestableCallback.noop(), "article", "article", "cosinesq");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3d() {
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

        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3, 2 });
            }
        };
        engine.rrec(test, "article", 13, "article");
        test.waitForFinish();
        test.validate();
    }
}

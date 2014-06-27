package com.guokr.simbase.benchmarks;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;
import com.guokr.simbase.engine.SimEngineImpl;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.store.Basis;
import com.guokr.simbase.store.VectorSet;

public class SparseConsineSquareBenchmark {

    public static long start       = -1;
    public static long accumulated = 0;

    public static void main(String[] args) {

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

        SimEngineImpl engine = new SimEngineImpl(config.getSub("engine"));

        String[] components = new String[1024];
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

            VectorSetListener listener = new TestMemListener();
            engine.listen("article", listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        start = new Date().getTime();
        for (int i = 1; i <= 10000; i++) {

            float total = 0;
            float[] distr = new float[1024];
            for (int j = 0; j < 1024; j++) {
                distr[j] = (float) Math.random();
                total += distr[j];
            }
            for (int j = 0; j < 1024; j++) {
                distr[j] = distr[j] / total;
            }

            engine.iadd(TestableCallback.noop(), "article", i, Basis.sparsify(4096, distr));

        }
    }

    private static class TestMemListener implements VectorSetListener {

        private int counter = 0;

        @Override
        public void onVectorAdded(VectorSet evtSrc, long vecid, float[] vector) {
            if (counter % 1000 == 0) {
                long duration = new Date().getTime() - start;
                System.out.println("vecid:" + counter + "\tmemory:" + Runtime.getRuntime().totalMemory() + "\ttime:"
                        + (duration / 1000));
                start = new Date().getTime();
            }
            counter++;
        }

        @Override
        public void onVectorAdded(VectorSet evtSrc, long vecid, int[] vector) {
        }

        @Override
        public void onVectorSetted(VectorSet evtSrc, long vecid, float[] old, float[] vector) {
        }

        @Override
        public void onVectorSetted(VectorSet evtSrc, long vecid, int[] old, int[] vector) {
        }

        @Override
        public void onVectorAccumulated(VectorSet evtSrc, long vecid, float[] vector, float[] accumulated) {
        }

        @Override
        public void onVectorAccumulated(VectorSet evtSrc, long vecid, int[] vector, int[] accumulated) {
        }

        @Override
        public void onVectorRemoved(VectorSet evtSrc, long vecid) {
        }

    }
}

package com.guokr.simbase.engine;

import static com.guokr.simbase.TestEngine.bmk;
import static com.guokr.simbase.TestEngine.del;
import static com.guokr.simbase.TestEngine.execCmd;
import static com.guokr.simbase.TestEngine.longList;
import static com.guokr.simbase.TestEngine.ok;
import static com.guokr.simbase.TestEngine.rmk;
import static com.guokr.simbase.TestEngine.rrec;
import static com.guokr.simbase.TestEngine.vadd;
import static com.guokr.simbase.TestEngine.vmk;
import static com.guokr.simbase.TestEngine.vset;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestEngine;

public class SparseCosRecTests {
    public static SimEngineImpl engine;
    public static String[]      components;

    @BeforeClass
    public static void setup() throws Throwable {
        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        Map<String, Object> basis = new HashMap<String, Object>();
        Map<String, Object> sparse = new HashMap<String, Object>();
        Map<String, Object> econf = new HashMap<String, Object>();
        sparse.put("accumuFactor", 10.0);
        sparse.put("sparseFactor", 2048);
        basis.put("vectorSetType", "sparse");
        basis.put("maxlimits", 20);
        econf.put("savepath", "data");
        econf.put("saveinterval", 7200000);
        econf.put("loadfactor", 0.75);
        econf.put("bycount", 100);
        defaults.put("sparse", sparse);
        defaults.put("basis", basis);
        defaults.put("engine", econf);
        settings.put("defaults", defaults);
        SimConfig config = new SimConfig(settings);

        engine = new SimEngineImpl(config.getSub("engine"));
        TestEngine.engine = engine;

        components = new String[3];
        for (int i = 0; i < components.length; i++) {
            components[i] = "B" + String.valueOf(i);
        }

    }

    @Before
    public void testUp() throws Throwable {
        execCmd(bmk("btest", components), ok(), //
                vmk("btest", "vtest"), ok(), //
                vmk("btest", "vtest2"), ok(), //
                vadd("vtest", 2, 0.9f, 0.09f, 0.01f), ok(), //
                vadd("vtest", 3, 0.89f, 0f, 0.11f), ok(), //
                vadd("vtest", 5, 0.1f, 0.89f, 0.01f), ok(), //
                vadd("vtest", 7, 0.09f, 0f, 0.91f), ok(), //
                vadd("vtest", 11, 0f, 0.89f, 0.11f), ok(), //
                vadd("vtest", 13, 0f, 0.09f, 0.91f), ok(), //
                vadd("vtest2", 2, 0.9f, 0.09f, 0.01f), ok(), //
                vadd("vtest2", 3, 0.89f, 0f, 0.11f), ok(), //
                vadd("vtest2", 5, 0.1f, 0.89f, 0.01f), ok(), //
                vadd("vtest2", 7, 0.09f, 0f, 0.91f), ok(), //
                vadd("vtest2", 11, 0f, 0.89f, 0.11f), ok(), //
                vadd("vtest2", 13, 0f, 0.09f, 0.91f), ok() //
        );
    }

    @After
    public void testDown() throws Throwable {
        execCmd(del("btest"), ok());
    }

    @Test
    public void testRec() throws Throwable {
        execCmd(rmk("vtest", "vtest", "cosinesq"), ok(), //
                rmk("vtest", "vtest2", "cosinesq"), ok(), //
                rrec("vtest", 13, "vtest"), longList(7, 11, 3, 5, 2), //
                rrec("vtest", 7, "vtest2"), longList(7, 13, 3, 11, 2, 5));
    }

    @Test
    public void testSetRec() throws Throwable {
        execCmd(vset("vtest", 13, 0.9f, 0.09f, 0.01f), ok(), //
                rmk("vtest", "vtest", "cosinesq"), ok(), //
                rmk("vtest", "vtest2", "cosinesq"), ok(), //
                vset("vtest2", 7, 0.1f, 8f, 0.1f), ok(), //
                vset("vtest", 13, 0f, 0.09f, 0.91f), ok(), //
                vset("vtest2", 7, 0.09f, 0f, 0.91f), ok(), //
                rrec("vtest", 13, "vtest"), longList(7, 11, 3, 5, 2), //
                rrec("vtest", 7, "vtest2"), longList(7, 13, 3, 11, 2, 5), //
                rrec("vtest", 13, "vtest"), longList(7, 11, 3, 5, 2), //
                rrec("vtest", 7, "vtest2"), longList(7, 13, 3, 11, 2, 5));
    }
}

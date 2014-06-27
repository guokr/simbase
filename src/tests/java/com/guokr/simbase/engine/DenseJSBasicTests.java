package com.guokr.simbase.engine;

import static com.guokr.simbase.TestEngine.blist;
import static com.guokr.simbase.TestEngine.bmk;
import static com.guokr.simbase.TestEngine.brev;
import static com.guokr.simbase.TestEngine.del;
import static com.guokr.simbase.TestEngine.execCmd;
import static com.guokr.simbase.TestEngine.floatList;
import static com.guokr.simbase.TestEngine.longList;
import static com.guokr.simbase.TestEngine.ok;
import static com.guokr.simbase.TestEngine.rlist;
import static com.guokr.simbase.TestEngine.rmk;
import static com.guokr.simbase.TestEngine.rrec;
import static com.guokr.simbase.TestEngine.stringList;
import static com.guokr.simbase.TestEngine.vacc;
import static com.guokr.simbase.TestEngine.vadd;
import static com.guokr.simbase.TestEngine.vget;
import static com.guokr.simbase.TestEngine.vlist;
import static com.guokr.simbase.TestEngine.vmk;
import static com.guokr.simbase.TestEngine.vrem;
import static com.guokr.simbase.TestEngine.vset;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestEngine;

public class DenseJSBasicTests {
    public static SimEngineImpl engine;
    public static String[]      components;

    @BeforeClass
    public static void setup() throws Throwable {
        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        Map<String, Object> basis = new HashMap<String, Object>();
        Map<String, Object> dense = new HashMap<String, Object>();
        Map<String, Object> econf = new HashMap<String, Object>();
        dense.put("accumuFactor", 10.0);
        dense.put("sparseFactor", 2048);
        basis.put("vectorSetType", "dense");
        basis.put("maxlimits", 20);
        econf.put("savepath", "data");
        econf.put("saveinterval", 7200000);
        econf.put("loadfactor", 0.75);
        econf.put("bycount", 100);
        defaults.put("dense", dense);
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
                rmk("vtest", "vtest", "jensenshannon"), ok(), //
                vadd("vtest", 2, 0.9f, 0.09f, 0.01f), ok(), //
                vadd("vtest", 3, 0.89f, 0f, 0.11f), ok(), //
                vadd("vtest", 5, 0.1f, 0.89f, 0.01f), ok(), //
                vadd("vtest", 7, 0.09f, 0f, 0.91f), ok(), //
                vadd("vtest", 11, 0f, 0.89f, 0.11f), ok(), //
                vadd("vtest", 13, 0f, 0.09f, 0.91f), ok() //
        );
    }

    @After
    public void testDown() throws Throwable {
        execCmd(del("btest"), ok());
    }

    @Test
    public void testRec() throws Throwable {
        execCmd(rrec("vtest", 13, "vtest"), longList(7, 11, 5, 3, 2), //
                rrec("vtest", 7, "vtest"), longList(13, 3, 2, 11, 5));
    }

    @Test
    public void testVget() throws Throwable {
        execCmd(vget("vtest", 2), floatList(0.9f, 0.09f, 0.01f), //
                vget("vtest", 3), floatList(0.89f, 0f, 0.11f), //
                vget("vtest", 5), floatList(0.1f, 0.89f, 0.01f), //
                vget("vtest", 7), floatList(0.09f, 0f, 0.91f),//
                vget("vtest", 11), floatList(0f, 0.89f, 0.11f),//
                vget("vtest", 13), floatList(0f, 0.09f, 0.91f)//
        );
    }

    @Test
    public void testRlist() throws Throwable {
        execCmd(rlist("vtest"), stringList("vtest"));
    }

    @Test
    public void testVrem() throws Throwable {
        execCmd(rrec("vtest", 2, "vtest"), longList(3, 5, 7, 11, 13), //
                vrem("vtest", 5), ok(), //
                vrem("vtest", 7), ok(), //
                rrec("vtest", 13, "vtest"), longList(11, 3, 2),//
                vadd("vtest", 5, 0.1f, 0.89f, 0.01f), ok(), //
                rrec("vtest", 13, "vtest"), longList(11, 5, 3, 2),//
                vadd("vtest", 7, 0.09f, 0f, 0.91f), ok(), //
                rrec("vtest", 2, "vtest"), longList(3, 5, 7, 11, 13)//
        );
    }

    @Test
    public void testVset() throws Throwable {
        // replace 2 with 7 and 7 with 2
        // and then restore the original
        execCmd(vset("vtest", 2, 0.09f, 0f, 0.91f), ok(), //
                vset("vtest", 7, 0.9f, 0.09f, 0.01f), ok(), //
                rrec("vtest", 13, "vtest"), longList(2, 11, 5, 3, 7), //
                vset("vtest", 2, 0.9f, 0.09f, 0.01f), ok(), //
                vset("vtest", 7, 0.09f, 0f, 0.91f), ok(), //
                rrec("vtest", 13, "vtest"), longList(7, 11, 5, 3, 2) //
        );
    }

    @Test
    public void testBlist() throws Throwable {
        execCmd(blist(), stringList("btest"));
    }

    @Test
    public void testVlist() throws Throwable {
        execCmd(vlist("btest"), stringList("vtest"));
    }

    @Test
    public void testVacc() throws Throwable {
        execCmd(vacc("vtest", 5, 0.1f, 0.9f, 0f), ok(), //
                vget("vtest", 5), floatList(0.2f, 1.79f, 0.01f) //
        );
    }

    @Test
    public void testBrev() throws Throwable {
        execCmd(brev("btest", "B2", "B1", "B0"), ok());
    }
}

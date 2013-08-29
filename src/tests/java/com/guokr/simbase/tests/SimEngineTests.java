package com.guokr.simbase.tests;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.SimBase;

public class SimEngineTests {

	private int[] getDocids(String[] set) {
		int[] docids = new int[set.length / 2];
		int i = 0, j = 0;
		while (i < set.length) {
			docids[j++] = Integer.parseInt(set[i]);
			i = i + 2;
		}
		return docids;
	}

	private void delay(int second) {

		try {
			Thread.currentThread();
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
		}
	}

	private void printSimBase(SimBase simbase, String key) {
		int count = 0;
		while (count < 24) {
			int[] docids = getDocids(simbase.retrieve(key, count));
			if (docids.length != 0) {
				for (int docid : docids) {
					System.out.print(docid + ",");
				}
				System.out.println();
			} else {
				System.out.println("empty");
			}
			count++;
		}
		System.out.println();
	}

	private SimBase initSimBase(String key) {
		Map<String, Object> context = new HashMap<String, Object>();
		try {
			Yaml yaml = new Yaml();
			@SuppressWarnings("unchecked")
			Map<String, Object> config = (Map<String, Object>) yaml
					.load(new FileReader("config/server.yaml"));
			context = new HashMap<String, Object>(config);
		} catch (IOException e) {
		}

		SimBase simbase = new SimBase(context);
		simbase.add(key, 0, new float[] { 0.18257418583505536f,
				0.3651483716701107f, 0.5477225575051661f, 0.7302967433402214f });
		simbase.add(key, 1, new float[] { 0.18257418583505536f,
				0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f });
		simbase.add(key, 2, new float[] { 0.18257418583505536f,
				0.5477225575051661f, 0.3651483716701107f, 0.7302967433402214f });
		simbase.add(key, 3, new float[] { 0.18257418583505536f,
				0.5477225575051661f, 0.7302967433402214f, 0.3651483716701107f });
		simbase.add(key, 4, new float[] { 0.18257418583505536f,
				0.7302967433402214f, 0.3651483716701107f, 0.5477225575051661f });
		simbase.add(key, 5, new float[] { 0.18257418583505536f,
				0.7302967433402214f, 0.5477225575051661f, 0.3651483716701107f });
		simbase.add(key, 6,
				new float[] { 0.3651483716701107f, 0.18257418583505536f,
						0.5477225575051661f, 0.7302967433402214f });
		simbase.add(key, 7,
				new float[] { 0.3651483716701107f, 0.18257418583505536f,
						0.7302967433402214f, 0.5477225575051661f });
		simbase.add(key, 8,
				new float[] { 0.3651483716701107f, 0.5477225575051661f,
						0.18257418583505536f, 0.7302967433402214f });
		simbase.add(key, 9,
				new float[] { 0.3651483716701107f, 0.5477225575051661f,
						0.7302967433402214f, 0.18257418583505536f });
		simbase.add(key, 10,
				new float[] { 0.3651483716701107f, 0.7302967433402214f,
						0.18257418583505536f, 0.5477225575051661f });
		simbase.add(key, 11,
				new float[] { 0.3651483716701107f, 0.7302967433402214f,
						0.5477225575051661f, 0.18257418583505536f });
		simbase.add(key, 12,
				new float[] { 0.5477225575051661f, 0.18257418583505536f,
						0.3651483716701107f, 0.7302967433402214f });
		simbase.add(key, 13,
				new float[] { 0.5477225575051661f, 0.18257418583505536f,
						0.7302967433402214f, 0.3651483716701107f });
		simbase.add(key, 14,
				new float[] { 0.5477225575051661f, 0.3651483716701107f,
						0.18257418583505536f, 0.7302967433402214f });
		simbase.add(key, 15,
				new float[] { 0.5477225575051661f, 0.3651483716701107f,
						0.7302967433402214f, 0.18257418583505536f });
		simbase.add(key, 16,
				new float[] { 0.5477225575051661f, 0.7302967433402214f,
						0.18257418583505536f, 0.3651483716701107f });
		simbase.add(key, 17,
				new float[] { 0.5477225575051661f, 0.7302967433402214f,
						0.3651483716701107f, 0.18257418583505536f });
		simbase.add(key, 18,
				new float[] { 0.7302967433402214f, 0.18257418583505536f,
						0.3651483716701107f, 0.5477225575051661f });
		simbase.add(key, 19,
				new float[] { 0.7302967433402214f, 0.18257418583505536f,
						0.5477225575051661f, 0.3651483716701107f });
		simbase.add(key, 20,
				new float[] { 0.7302967433402214f, 0.3651483716701107f,
						0.18257418583505536f, 0.5477225575051661f });
		simbase.add(key, 21,
				new float[] { 0.7302967433402214f, 0.3651483716701107f,
						0.5477225575051661f, 0.18257418583505536f });
		simbase.add(key, 22,
				new float[] { 0.7302967433402214f, 0.5477225575051661f,
						0.18257418583505536f, 0.3651483716701107f });
		simbase.add(key, 23,
				new float[] { 0.7302967433402214f, 0.5477225575051661f,
						0.3651483716701107f, 0.18257418583505536f });
		return simbase;

	}

	@Test
	public void test_del_clean() {
		String key = "test2";
		SimBase simbase = initSimBase("test2");
		delay(1);// 等待加载完毕
		printSimBase(simbase, key);
		simbase.delete(key, 1);
		// 删除之后，清除之前
		// simbase.add(key, 7,
		// new float[] { 0.3651483716701107f, 0.18257418583505536f,
		// 0.7302967433402214f, 0.5477225575051661f });
		delay(1);
		assertEquals(0, simbase.retrieve(key, 1).length);
		simbase.clear();
		delay(1);
		assertEquals(0, simbase.retrieve(key, 1).length);
		printSimBase(simbase, key);
		simbase.add(key, 7,
				new float[] { 0.3651483716701107f, 0.18257418583505536f,
						0.7302967433402214f, 0.5477225575051661f });
		delay(1);
		assertEquals(0, simbase.retrieve(key, 1).length);
		printSimBase(simbase, key);
		simbase.add(key, 1, new float[] { 0.18257418583505536f,
				0.3651483716701107f, 0.7302967433402214f, 0.5477225575051661f });
		delay(1);
		printSimBase(simbase, key);
		assertEquals(40, simbase.retrieve(key, 1).length);

	}

}

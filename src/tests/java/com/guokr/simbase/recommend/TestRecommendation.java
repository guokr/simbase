package com.guokr.simbase.recommend;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.guokr.simbase.SimContext;

public class TestRecommendation {
	private Recommendation rec;

	@Before
	public void setUp() {
		rec = new Recommendation(new SimContext());
		rec.setLimit(10);
	}

	@Test
	public void testAdd() {
		rec.add(0, 0.1f);
		Assert.assertEquals(0, rec.ids()[0]);
	}

	@Test
	public void testAdd2() {
		rec.add(0, 0.1f);
		rec.add(1, 1);
		int[] ids = rec.ids();
		java.util.Arrays.sort(ids);
		Assert.assertArrayEquals(new int[] { 0, 1 }, ids);
	}

	@Test
	public void testAdd3() {
		for (int i = 1; i < 100; i++) {
			rec.add(i, i * 1.0f);
		}
		int[] expected = new int[] { 90, 91, 92, 93, 94, 95, 96, 97, 98, 99 };
		int[] ids = rec.ids();
		java.util.Arrays.sort(ids);
		Assert.assertArrayEquals(expected, ids);
	}

	@Test
	public void testAdd4() {
		for (int i = 100; i >= 0; i--) {
			rec.add(i, i * 1.0f);
		}
		int[] expected = new int[] { 91, 92, 93, 94, 95, 96, 97, 98, 99, 100 };
		int[] ids = rec.ids();
		java.util.Arrays.sort(ids);

		Assert.assertArrayEquals(expected, ids);
	}

	@Test
	public void testAdd5() {
		for (int i = 50; i < 100; i++) {
			rec.add(i, i);
		}
		for (int i = 50; i < 100; i++) {
			rec.remove(i);
		}
		for (int i = 1; i < 50; i++) {
			rec.add(i, i);
		}
		Assert.assertEquals(89, rec.getWaterline(), 0.01);
		Assert.assertEquals(0, rec.ids().length);
	}

	@Test
	public void testRemove() {
		rec.add(0, 1.0f);
		rec.remove(0);
		Assert.assertEquals(rec.ids().length, 0);
	}

	@Test
	public void testRemove2() {
		for (int i = 0; i < 1000; i++) {
			rec.add(i, i * 1.0f);
		}
		for (int i = 0; i < 1000; i++) {
			rec.remove(i);
		}
		Assert.assertEquals(rec.ids().length, 0);
	}
}

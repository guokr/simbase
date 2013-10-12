package com.guokr.simbase.recommend;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestFloatPriorityQueue {
	private FloatPriorityQueue queue;

	@Before
	public void setUp() {
		queue = new FloatPriorityQueue(10);
	}

	@Test
	public void testInsert() {
		queue.insert(1.0f);
		queue.insert(2.0f);
		queue.insert(3.0f);
		Assert.assertEquals(1.0f, queue.min(), 0.001f);
	}

	@Test
	public void testDel() {
		queue.insert(1.0f);
		queue.insert(2.0f);
		for (int i = 10; i < 100; i++) {
			queue.insert(2.0f * i);
		}
		Assert.assertEquals(1.0f, queue.delMin(), 0.0001f);
		Assert.assertEquals(2.0f, queue.min(), 0.00001f);
		Assert.assertTrue(queue.isMinHeap());
	}

	@Test
	public void testMin() {
		for (int i = 0; i < 100; i++) {
			queue.insert(1.0f * i);
		}
		Assert.assertEquals(0.0, queue.min(), 0.0001f);
		Assert.assertTrue(queue.isMinHeap());
	}

	@Test
	public void testIsEmpty() {
		for (int i = 0; i < 100; i++) {
			queue.insert(1.0f * i);
		}

		Assert.assertFalse(queue.isEmpty());
		for (int j = 0; j < 100; j++) {
			queue.delMin();
		}
		Assert.assertTrue(queue.isMinHeap());
		Assert.assertTrue(queue.isEmpty());
	}
}

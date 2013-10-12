package com.guokr.simbase.recommend;

import java.util.NoSuchElementException;

/**
 * For additional documentation, see <a
 * href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of <i>Algorithms,
 * 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */
public class FloatPriorityQueue {
	private float[] pq; // store items at indices 1 to N
	private int N; // number of items on priority queue
	private boolean ascend;

	public FloatPriorityQueue() {
		this(10, true);
	}

	public FloatPriorityQueue(int initCapacity, boolean ascend) {
		pq = new float[initCapacity + 1];
		N = 0;
		this.ascend = ascend;
	}

	public FloatPriorityQueue(int initCapacity) {
		this(initCapacity, true);
	}

	public FloatPriorityQueue(boolean ascend) {
		this(10, ascend);
	}

	public FloatPriorityQueue(float[] keys, boolean ascend) {
		this.ascend = ascend;
		N = keys.length;
		pq = new float[keys.length + 1];
		for (int i = 0; i < N; i++)
			pq[i + 1] = keys[i];
		for (int k = N / 2; k >= 1; k--)
			sink(k);
		assert isMinHeap();
	}

	public boolean isEmpty() {
		return N == 0;
	}

	public int size() {
		return N;
	}

	/**
	 * 
	 * @throws java.util.NoSuchElementException
	 *             if priority queue is empty.
	 */
	public float min() {
		if (isEmpty())
			throw new NoSuchElementException("Priority queue underflow");
		return pq[1];
	}

	private void resize(int capacity) {
		assert capacity > N;
		float[] temp = new float[capacity];
		for (int i = 1; i <= N; i++)
			temp[i] = pq[i];
		pq = temp;
	}

	public void insert(float x) {
		// double size of array if necessary
		if (N == pq.length - 1)
			resize(2 * pq.length);

		// add x, and percolate it up to maintain heap invariant
		pq[++N] = x;
		swim(N);
		assert isMinHeap();
	}

	/**
	 * @throws java.util.NoSuchElementException
	 *             if priority queue is empty.
	 */
	public float delMin() {
		if (isEmpty())
			throw new NoSuchElementException("Priority queue underflow");
		exch(1, N);
		float min = pq[N--];
		sink(1);
		if ((N > 0) && (N == (pq.length - 1) / 4))
			resize(pq.length / 2);
		assert isMinHeap();
		return min;
	}

	private void swim(int k) {
		while (k > 1 && greater(k / 2, k)) {
			exch(k, k / 2);
			k = k / 2;
		}
	}

	private void sink(int k) {
		while (2 * k <= N) {
			int j = 2 * k;
			if (j < N && greater(j, j + 1))
				j++;
			if (!greater(k, j))
				break;
			exch(k, j);
			k = j;
		}
	}

	private boolean greater(int i, int j) {
		if (ascend) {
			return pq[i] - pq[j] >= 0.0001;
		} else {
			return pq[j] - pq[i] >= 0.0001;
		}
	}

	private void exch(int i, int j) {
		float swap = pq[i];
		pq[i] = pq[j];
		pq[j] = swap;
	}

	boolean isMinHeap() {
		return isMinHeap(1);
	}

	boolean isMinHeap(int k) {
		if (k > N)
			return true;
		int left = 2 * k, right = 2 * k + 1;
		if (left <= N && greater(k, left))
			return false;
		if (right <= N && greater(k, right))
			return false;
		return isMinHeap(left) && isMinHeap(right);
	}
}

package com.guokr.simbase.util;

public class Sorter {

	private int limits = 20;
	private int size = 0;
	private int[] docids;
	private float[] scores;

	public Sorter(int limits) {
		this.limits = limits;
		int maxlen = 1 + limits;
		this.docids = new int[maxlen];
		this.scores = new float[maxlen];

		for (int i = 0; i < maxlen; i++) {
			this.docids[i] = -1;
			this.scores[i] = -1.0f;
		}
	}

	private int indexOf(int docid) {
		int pos = -1;
		for (int i = 0; i < this.size; i++) {
			if (this.docids[i] == docid) {
				pos = i;
				break;
			}
		}
		return pos;
	}

	private int lookup(float score) {
		int pos = -1;
		if (this.size == 0) {
			pos = 0;
		} else if (this.size > 0 && score >= this.scores[0]) {
			pos = 0;
		} else if (this.size > 0 && this.size < this.limits
				&& score < this.scores[this.size - 1]) {
			pos = this.size;
		} else if (this.size > 0 && score < this.scores[this.size - 1]) {
			pos = this.size;
		} else {
			for (int cur = 0; cur < this.size - 1; cur++) {
				if (score < this.scores[cur] && score >= this.scores[cur + 1]) {
					pos = cur + 1;
					break;
				}
			}
		}
		return pos;
	}

	public int size() {
		return this.size;
	}

	public void add(int docid, float score) {
        remove(docid);
		int pos = lookup(score);
		if (pos == 0 && this.size == 0) {
			this.docids[pos] = docid;
			this.scores[pos] = score;
			this.size = this.size + 1;
		} else {
			if (pos > -1) {
				this.size = this.size + 1;
				int[] tdocids = this.docids;
				float[] tscores = this.scores;
				this.docids = new int[this.size];
				this.scores = new float[this.size];

				System.arraycopy(tdocids, 0, this.docids, 0, pos);
				System.arraycopy(tdocids, pos, this.docids, pos + 1, this.size
						- pos - 1);
				System.arraycopy(tscores, 0, this.scores, 0, pos);
				System.arraycopy(tscores, pos, this.scores, pos + 1, this.size
						- pos - 1);

				this.docids[pos] = docid;
				this.scores[pos] = score;
			}
		}
	}

	public int[] docids() {
		int[] result = new int[size];
		System.arraycopy(this.docids, 0, result, 0, size);
		return result;
	}

	public float get(int docid) {
		float result = -1.0f;
		for (int i = 0; i < this.size; i++) {
			if (this.docids[i] == docid) {
				result = this.scores[i];
				break;
			}
		}
		return result;
	}

	public void remove(int docid) {
		int pos = indexOf(docid);
		if (pos > -1) {
			this.size = this.size - 1;
			int[] tdocids = this.docids;
			float[] tscores = this.scores;
			this.docids = new int[this.size];
			this.scores = new float[this.size];

			System.arraycopy(tdocids, 0, this.docids, 0, pos);
			System.arraycopy(tdocids, pos + 1, this.docids, pos, this.size
					- pos);
			System.arraycopy(tscores, 0, this.scores, 0, pos);
			System.arraycopy(tscores, pos + 1, this.scores, pos, this.size
					- pos);
		}
	}

	public float removeLast() {
		this.scores[this.size - 1] = -1.0f;
		this.size = this.size - 1;
		return this.scores[this.size - 1];
	}

	public String[] pickle() {
		String[] result = new String[2 * this.size];
		for (int i = 0; i < this.size; i++) {
			result[2 * i] = String.valueOf(this.docids[i]);
			result[2 * i + 1] = String.valueOf(this.scores[i]);
		}
		return result;
	}

}
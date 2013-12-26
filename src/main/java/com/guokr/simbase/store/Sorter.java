package com.guokr.simbase.store;

import com.guokr.simbase.SimScore.SortOrder;

public class Sorter {

    private SortOrder order;
    private int       limits    = 20;

    private int       size      = 0;
    private float     waterline = 0f;

    private int[]     vecids;
    private float[]   scores;

    public Sorter(SortOrder order, int limits) {
        this.order = order;
        this.limits = limits;

        int maxlen = 1 + limits;
        this.vecids = new int[maxlen];
        this.scores = new float[maxlen];

        for (int i = 0; i < maxlen; i++) {
            this.vecids[i] = -1;
            this.scores[i] = -1.0f;
        }
        
        if (order == SortOrder.Asc) {
            waterline = Float.POSITIVE_INFINITY;
        } else {
            waterline = Float.NEGATIVE_INFINITY;
        }
    }

    private int indexOf(int vecid) {
        int pos = -1;
        for (int i = 0; i < this.size; i++) {
            if (this.vecids[i] == vecid) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    private int asclookup(float score) {
        int pos = -1;
        if (this.size == 0) {
            pos = 0;
        } else if (this.size > 0 && score <= this.scores[0]) {
            pos = 0;
        } else if (this.size > 0 && this.size < this.limits && score > this.scores[this.size - 1]) {
            pos = this.size;
        } else if (this.size > 0 && score > this.scores[this.size - 1]) {
            pos = this.size;
        } else {
            for (int cur = 0; cur < this.size - 1; cur++) {
                if (score > this.scores[cur] && score <= this.scores[cur + 1]) {
                    pos = cur + 1;
                    break;
                }
            }
        }
        return pos;
    }

    private int desclookup(float score) {
        int pos = -1;
        if (this.size == 0) {
            pos = 0;
        } else if (this.size > 0 && score >= this.scores[0]) {
            pos = 0;
        } else if (this.size > 0 && this.size < this.limits && score < this.scores[this.size - 1]) {
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

    public void add(int vecid, float score) {
        if (order == SortOrder.Asc && score > waterline) {
            return;
        }
        if (order == SortOrder.Desc && score < waterline) {
            return;
        }

        remove(vecid);
        int pos = order == SortOrder.Asc ? asclookup(score) : desclookup(score);
        if (pos == 0 && this.size == 0) {
            this.vecids = new int[] { vecid };
            this.scores = new float[] { score };
            this.size = this.size + 1;
        } else {
            if (pos > -1) {
                this.size = this.size + 1;
                int[] tvecids = this.vecids;
                float[] tscores = this.scores;
                this.vecids = new int[this.size];
                this.scores = new float[this.size];

                System.arraycopy(tvecids, 0, this.vecids, 0, pos);
                System.arraycopy(tvecids, pos, this.vecids, pos + 1, this.size - pos - 1);
                System.arraycopy(tscores, 0, this.scores, 0, pos);
                System.arraycopy(tscores, pos, this.scores, pos + 1, this.size - pos - 1);

                this.vecids[pos] = vecid;
                this.scores[pos] = score;
            }
        }

        if (this.size() > this.limits) {
            int count = this.size - this.limits - 1;
            for (int i = count; i > 0; i--) {
                float last = removeLast();
                if (i == 1) {
                    this.waterline = last;
                }
            }
        }
    }

    public int[] vecids() {
        int[] result = new int[size];
        System.arraycopy(this.vecids, 0, result, 0, size);
        return result;
    }

    public float get(int vecid) {
        float result = -1.0f;
        int len = this.size > this.limits ? this.limits : this.size; 
        for (int i = 0; i < len; i++) {
            if (this.vecids[i] == vecid) {
                result = this.scores[i];
                break;
            }
        }
        return result;
    }

    public void remove(int vecid) {
        int pos = indexOf(vecid);
        if (pos > -1) {
            this.size = this.size - 1;
            int[] tvecids = this.vecids;
            float[] tscores = this.scores;
            this.vecids = new int[this.size];
            this.scores = new float[this.size];

            System.arraycopy(tvecids, 0, this.vecids, 0, pos);
            System.arraycopy(tvecids, pos + 1, this.vecids, pos, this.size - pos);
            System.arraycopy(tscores, 0, this.scores, 0, pos);
            System.arraycopy(tscores, pos + 1, this.scores, pos, this.size - pos);
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
            result[2 * i] = String.valueOf(this.vecids[i]);
            result[2 * i + 1] = String.valueOf(this.scores[i]);
        }
        return result;
    }

}
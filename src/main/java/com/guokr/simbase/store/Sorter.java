package com.guokr.simbase.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guokr.simbase.SimScore.SortOrder;

public class Sorter {

    private static final Logger logger    = LoggerFactory
                                                  .getLogger(Sorter.class);

    SortOrder                   order;
    int                         limits    = 20;

    int                         size      = 0;
    float                       waterline = 0f;

    int[]                       vecids;
    float[]                     scores;

    int                         id;
    Recommendation              container;

    public Sorter(Recommendation container, int vecid, SortOrder order,
            int limits) {
        this.id = vecid;
        this.container = container;
        this.order = order;
        this.limits = limits;

        reset();
    }

    public void reset() {
        int maxlen = 1 + limits;

        this.size = 0;
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
        } else {
            if (score > this.scores[this.size - 1]) {
                if (this.size < this.limits) {
                    pos = this.size;
                } else {
                    pos = -1;
                }
            } else if (score <= this.scores[0]) {
                pos = 0;
            } else {
                for (int cur = 0; cur < this.size - 1; cur++) {
                    if (score > this.scores[cur]
                            && score <= this.scores[cur + 1]) {
                        pos = cur + 1;
                        break;
                    }
                }
            }
        }
        return pos;
    }

    private int desclookup(float score) {
        int pos = -1;
        if (this.size == 0) {
            pos = 0;
        } else {
            if (score < this.scores[this.size - 1]) {
                if (this.size < this.limits) {
                    pos = this.size;
                } else {
                    pos = -1;
                }
            } else if (score >= this.scores[0]) {
                pos = 0;
            } else {
                for (int cur = 0; cur < this.size - 1; cur++) {
                    if (score < this.scores[cur]
                            && score >= this.scores[cur + 1]) {
                        pos = cur + 1;
                        break;
                    }
                }
            }
        }
        return pos;
    }

    public int size() {
        return this.size;
    }

    protected void insert(int vecid, float score, int pos) {
        if (pos > -1) {
            this.size = this.size + 1;
            int[] tvecids = this.vecids;
            float[] tscores = this.scores;
            this.vecids = new int[this.size];
            this.scores = new float[this.size];

            System.arraycopy(tvecids, 0, this.vecids, 0, pos);
            System.arraycopy(tvecids, pos, this.vecids, pos + 1, this.size
                    - pos - 1);
            System.arraycopy(tscores, 0, this.scores, 0, pos);
            System.arraycopy(tscores, pos, this.scores, pos + 1, this.size
                    - pos - 1);

            this.vecids[pos] = vecid;
            this.scores[pos] = score;

            container.addReverseIndex(id, vecid);
        }
    }

    protected void tidy() {
        if (this.size() > this.limits) {
            float last = removeLast();
            this.waterline = last;
        }
    }

    public void add(int vecid, float score) {
        if (this.size == 0) {
            this.vecids = new int[] { vecid };
            this.scores = new float[] { score };
            this.size = this.size + 1;
            container.addReverseIndex(id, vecid);
        } else {
            remove(vecid);

            if (order == SortOrder.Asc) {
                if (score < waterline) {
                    int pos = asclookup(score);
                    insert(vecid, score, pos);
                    tidy();
                }
            } else {
                if (score > waterline) {
                    int pos = desclookup(score);
                    insert(vecid, score, pos);
                    tidy();
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
            System.arraycopy(tvecids, pos + 1, this.vecids, pos, this.size
                    - pos);
            System.arraycopy(tscores, 0, this.scores, 0, pos);
            System.arraycopy(tscores, pos + 1, this.scores, pos, this.size
                    - pos);
        }
    }

    public float removeLast() {
        try {
            container.deleteReverseIndex(id, vecids[this.size - 1]);
        } catch (NullPointerException ex) {
            logger.error(
                    String.format(
                            "ReverseIndex %d to %d doesn't not exist, current sorter size is %d",
                            id, vecids[this.size - 1], this.size), ex);
        } finally {
            this.scores[this.size - 1] = -1.0f;
            this.size = this.size - 1;
        }
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
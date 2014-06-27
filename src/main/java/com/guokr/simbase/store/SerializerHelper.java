package com.guokr.simbase.store;

import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongIntMap;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.SimScore;
import com.guokr.simbase.score.CosineSquareSimilarity;
import com.guokr.simbase.score.EuclideanDistanceSquare;
import com.guokr.simbase.score.JensenShannonDivergence;
import com.guokr.simbase.score.ManhattanDistance;

public class SerializerHelper {

    private static final Logger logger = LoggerFactory.getLogger(SerializerHelper.class);

    public static class BasisSerializer extends Serializer<Basis> {

        @Override
        public Basis read(Kryo kryo, Input input, Class<Basis> type) {
            String key = kryo.readObject(input, String.class);
            String[] schema = kryo.readObject(input, String[].class);
            return new Basis(key, schema);
        }

        @Override
        public void write(Kryo kryo, Output output, Basis basis) {
            kryo.writeObject(output, basis.key());
            kryo.writeObject(output, basis.get());
        }

    }

    public static class DenseVectorSetSerializer extends Serializer<DenseVectorSet> {

        private Basis basis;

        public void setBasis(Basis basis) {
            this.basis = basis;
        }

        @Override
        public DenseVectorSet read(Kryo kryo, Input input, Class<DenseVectorSet> type) {
            String key = kryo.readObject(input, String.class);
            float accumuFactor = kryo.readObject(input, float.class);
            int sparseFactor = kryo.readObject(input, int.class);
            DenseVectorSet vectorSet = new DenseVectorSet(key, basis, accumuFactor, sparseFactor);
            int sizeVector = kryo.readObject(input, int.class);
            int vecid, start, length;
            while (sizeVector > 0) {
                vecid = kryo.readObject(input, int.class);
                start = kryo.readObject(input, int.class);
                length = kryo.readObject(input, int.class);

                vectorSet.indexer.put(vecid, start);
                vectorSet.lengths.put(vecid, length);
                sizeVector--;
            }

            int sizeData = kryo.readObject(input, int.class);
            while (sizeData > 0) {
                float val = kryo.readObject(input, float.class);

                vectorSet.data.add(val);
                sizeData--;
            }

            return vectorSet;
        }

        @Override
        public void write(Kryo kryo, Output output, DenseVectorSet vectorSet) {
            output.writeString(vectorSet.key());
            kryo.writeObject(output, vectorSet.accumuFactor);
            kryo.writeObject(output, vectorSet.sparseFactor);

            TLongIntMap indexer = vectorSet.indexer;
            TLongIntIterator iter = indexer.iterator();
            kryo.writeObject(output, indexer.size());
            while (iter.hasNext()) {
                iter.advance();
                long vecid = iter.key();
                int start = iter.value();
                int length = vectorSet.length(vecid);
                kryo.writeObject(output, vecid);
                kryo.writeObject(output, start);
                kryo.writeObject(output, length);
            }

            int size = vectorSet.data.size();
            kryo.writeObject(output, size);
            for (int offset = 0; offset < size; offset++) {
                float val = vectorSet.data.get(offset);
                kryo.writeObject(output, val);
            }
        }

    }

    public static class SparseVectorSetSerializer extends Serializer<SparseVectorSet> {

        private Basis basis;

        public void setBasis(Basis basis) {
            this.basis = basis;
        }

        @Override
        public SparseVectorSet read(Kryo kryo, Input input, Class<SparseVectorSet> type) {
            String key = kryo.readObject(input, String.class);
            float accumuFactor = kryo.readObject(input, float.class);
            int sparseFactor = kryo.readObject(input, int.class);
            SparseVectorSet vectorSet = new SparseVectorSet(key, basis, accumuFactor, sparseFactor);

            int sizeVector = kryo.readObject(input, int.class);
            int vecid, start, length;
            while (sizeVector > 0) {
                vecid = kryo.readObject(input, int.class);
                start = kryo.readObject(input, int.class);
                length = kryo.readObject(input, int.class);

                vectorSet.indexer.put(vecid, start);
                vectorSet.lengths.put(vecid, length);
                sizeVector--;
            }

            int sizeData = kryo.readObject(input, int.class);
            while (sizeData > 0) {
                float val = kryo.readObject(input, float.class);

                vectorSet.data.add(val);
                sizeData--;
            }

            return vectorSet;
        }

        @Override
        public void write(Kryo kryo, Output output, SparseVectorSet vectorSet) {
            output.writeString(vectorSet.key());
            kryo.writeObject(output, vectorSet.accumuFactor);
            kryo.writeObject(output, vectorSet.sparseFactor);

            TLongIntMap indexer = vectorSet.indexer;
            TLongIntIterator iter = indexer.iterator();
            kryo.writeObject(output, indexer.size());
            while (iter.hasNext()) {
                iter.advance();
                long vecid = iter.key();
                int start = iter.value();
                int length = vectorSet.length(vecid);
                kryo.writeObject(output, vecid);
                kryo.writeObject(output, start);
                kryo.writeObject(output, length);
            }

            int size = vectorSet.data.size();
            kryo.writeObject(output, size);
            for (int offset = 0; offset < size; offset++) {
                float val = vectorSet.data.get(offset);
                kryo.writeObject(output, val);
            }
        }
    }

    public static class RecommendationSerializer extends Serializer<Recommendation> {

        private VectorSet source;
        private VectorSet target;

        public void setSource(VectorSet source) {
            this.source = source;
        }

        public void setTarget(VectorSet target) {
            this.target = target;
        }

        @Override
        public Recommendation read(Kryo kryo, Input input, Class<Recommendation> type) {
            String scoringName = kryo.readObject(input, String.class);
            SimScore scoring;
            if (scoringName.equals("euclideansq")) {
                scoring = new EuclideanDistanceSquare();
            } else if (scoringName.equals("manhattan")) {
                scoring = new ManhattanDistance();
            } else if (scoringName.equals("cosinesq")) {
                scoring = new CosineSquareSimilarity();
            } else if (scoringName.equals("jensenshannon")) {
                scoring = new JensenShannonDivergence();
            } else {
                scoring = new CosineSquareSimilarity();
            }

            int limits = kryo.readObject(input, int.class);
            int sortersSize = kryo.readObject(input, int.class);

            Recommendation rec = new Recommendation(source, target, scoring, limits);

            while (sortersSize > 0) {
                int size = kryo.readObject(input, int.class);
                long srcId = kryo.readObject(input, long.class);
                float waterline = kryo.readObject(input, float.class);
                while (size > 0) {
                    long tgtId = kryo.readObject(input, long.class);
                    float score = kryo.readObject(input, float.class);
                    rec.add(srcId, tgtId, score);
                    size--;
                }
                if (rec.sorters.containsKey(srcId)) {
                    rec.sorters.get(srcId).waterline = waterline;
                } else {
                    logger.warn(String.format("vecid[%d] not in sorters", srcId));
                }
                sortersSize--;
            }
            return rec;
        }

        @Override
        public void write(Kryo kryo, Output output, Recommendation recommend) {
            output.writeString(recommend.scoring.name());

            kryo.writeObject(output, recommend.limit);
            kryo.writeObject(output, recommend.sorters.size());

            TLongObjectIterator<Sorter> iter = recommend.sorters.iterator();
            while (iter.hasNext()) {
                iter.advance();
                Sorter sorter = iter.value();
                int size = sorter.size;
                kryo.writeObject(output, size);
                kryo.writeObject(output, iter.key());
                kryo.writeObject(output, sorter.waterline);
                while (size > 0) {
                    size--;
                    kryo.writeObject(output, sorter.vecids[size]);
                    kryo.writeObject(output, sorter.scores[size]);
                }
            }
        }
    }

    private final Kryo                kryo      = new Kryo();

    private BasisSerializer           bSerial   = new BasisSerializer();
    private DenseVectorSetSerializer  dvsSerial = new DenseVectorSetSerializer();
    private SparseVectorSetSerializer svsSerial = new SparseVectorSetSerializer();
    private RecommendationSerializer  rSerial   = new RecommendationSerializer();

    public SerializerHelper() {
        kryo.register(Basis.class, bSerial);
        kryo.register(DenseVectorSet.class, dvsSerial);
        kryo.register(SparseVectorSet.class, svsSerial);
        kryo.register(Recommendation.class, rSerial);
    }

    public BasisSerializer serializerBasis() {
        return bSerial;
    }

    public DenseVectorSetSerializer serializerDenseVectorSet() {
        return dvsSerial;
    }

    public SparseVectorSetSerializer serializerSparseVectorSet() {
        return svsSerial;
    }

    public RecommendationSerializer serializerRecommendation() {
        return rSerial;
    }

    public Basis readB(Input input) {
        return bSerial.read(kryo, input, Basis.class);
    }

    public void writeB(Output output, Basis base) {
        bSerial.write(kryo, output, base);
    }

    public DenseVectorSet readDVS(Basis basis, Input input) {
        dvsSerial.setBasis(basis);
        return dvsSerial.read(kryo, input, DenseVectorSet.class);
    }

    public SparseVectorSet readSVS(Basis basis, Input input) {
        svsSerial.setBasis(basis);
        return svsSerial.read(kryo, input, SparseVectorSet.class);
    }

    public Recommendation readR(VectorSet source, VectorSet target, Input input) {
        rSerial.setSource(source);
        rSerial.setTarget(target);
        return rSerial.read(kryo, input, Recommendation.class);
    }

    public Map<String, VectorSet> readVectorSets(Input input, Basis base) {
        Map<String, VectorSet> vectorSets = new HashMap<String, VectorSet>();
        int size = kryo.readObject(input, int.class);
        while (size > 0) {
            String type = kryo.readObject(input, String.class);
            VectorSet vectorSet;
            if (type.equals("dense")) {
                vectorSet = readDVS(base, input);
            } else if (type.equals("sparse")) {
                vectorSet = readSVS(base, input);
            } else {
                vectorSet = readDVS(base, input);
            }
            vectorSets.put(vectorSet.key(), vectorSet);
            size--;
        }
        return vectorSets;
    }

    public void writeVectorSets(Output output, Map<String, VectorSet> vectorSets) {
        kryo.writeObject(output, vectorSets.size());
        for (String key : vectorSets.keySet()) {
            VectorSet vectorSet = vectorSets.get(key);
            kryo.writeObject(output, vectorSet.type());
            kryo.writeObject(output, vectorSet);
        }
    }

    public Map<String, Recommendation> readRecommendations(Input input, Map<String, VectorSet> vectorSets) {
        Map<String, Recommendation> recs = new HashMap<String, Recommendation>();
        int size = kryo.readObject(input, int.class);
        while (size > 0) {
            String srcKey = kryo.readObject(input, String.class);
            String tgtKey = kryo.readObject(input, String.class);
            VectorSet src = vectorSets.get(srcKey);
            VectorSet tgt = vectorSets.get(tgtKey);
            Recommendation rec = readR(src, tgt, input);
            recs.put(srcKey + '_' + tgtKey, rec);
            src.addListener(rec);
            if (src != tgt) {
                tgt.addListener(rec);
            }
            size--;
        }
        return recs;
    }

    public void writeRecommendations(Output output, Map<String, Recommendation> recommendations) {
        kryo.writeObject(output, recommendations.size());
        for (String key : recommendations.keySet()) {
            Recommendation rec = recommendations.get(key);
            rec.clean();
            kryo.writeObject(output, rec.source.key());
            kryo.writeObject(output, rec.target.key());
            kryo.writeObject(output, rec);
        }
    }
}

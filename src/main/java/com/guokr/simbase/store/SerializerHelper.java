package com.guokr.simbase.store;

import gnu.trove.iterator.TIntObjectIterator;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.guokr.simbase.SimScore;
import com.guokr.simbase.score.CosineSquareSimilarity;
import com.guokr.simbase.score.JensenShannonDivergence;

public class SerializerHelper {

    public static class BasisSerializer extends Serializer<Basis> {
        {
            setImmutable(true);
        }

        @Override
        public Basis read(Kryo kryo, Input input, Class<Basis> type) {
            String key = input.readString();
            String[] schema = kryo.readObject(input, String[].class);
            return new Basis(key, schema);
        }

        @Override
        public void write(Kryo kryo, Output output, Basis basis) {
            output.writeString(basis.key());
            kryo.writeObject(output, basis.get());
        }

    }

    public static class DenseVectorSetSerializer extends Serializer<DenseVectorSet> {
        {
            setImmutable(true);
        }

        private Basis basis;

        public void setBasis(Basis basis) {
            this.basis = basis;
        }

        @Override
        public DenseVectorSet read(Kryo kryo, Input input, Class<DenseVectorSet> type) {
            String key = input.readString();
            float accumuFactor = input.readFloat();
            int sparseFactor = input.readInt();
            DenseVectorSet vectorSet = new DenseVectorSet(key, basis, accumuFactor, sparseFactor);
            int sizeVector = input.readInt();
            int sizeBase = basis.size();
            for (int offset = 0; offset < sizeVector; offset++) {
                for (int index = 0; index < sizeBase; index++) {
                    float prob = input.readFloat();
                    vectorSet.probs.add(prob);
                }
                float vecid = input.readFloat();
                vectorSet.probs.add(vecid);
                vectorSet.indexer.put(offset * (sizeBase + 1), (int) vecid - 1);
            }
            return vectorSet;
        }

        @Override
        public void write(Kryo kryo, Output output, DenseVectorSet vectorSet) {
            output.writeString(vectorSet.key);
            output.writeFloat(vectorSet.accumuFactor);
            output.writeInt(vectorSet.sparseFactor);
            output.writeInt(vectorSet.indexer.size());
            int end = vectorSet.probs.size();
            for (int offset = 0; offset < end; offset++) {
                float val = vectorSet.probs.get(offset);
                if (val >= 0) {
                    output.writeFloat(val);
                }
            }
        }

    }

    public static class SparseVectorSetSerializer extends Serializer<SparseVectorSet> {
        {
            setImmutable(true);
        }

        private Basis basis;

        public void setBasis(Basis basis) {
            this.basis = basis;
        }

        @Override
        public SparseVectorSet read(Kryo kryo, Input input, Class<SparseVectorSet> type) {
            String key = input.readString();
            float accumuFactor = input.readFloat();
            int sparseFactor = input.readInt();
            SparseVectorSet vectorSet = new SparseVectorSet(key, basis, accumuFactor, sparseFactor);
            int sizeVector = input.readInt();
            int offset = 0;
            while (sizeVector > 0) {
                float value = input.readFloat();
                while (value >= 0) {
                    vectorSet.probs.add(value);
                    value = input.readFloat();
                    offset++;
                }
                vectorSet.probs.add(value);
                vectorSet.indexer.put(offset, -(int) value - 1);
                sizeVector--;
            }
            return vectorSet;
        }

        @Override
        public void write(Kryo kryo, Output output, SparseVectorSet vectorSet) {
            output.writeString(vectorSet.key);
            output.writeFloat(vectorSet.accumuFactor);
            output.writeInt(vectorSet.sparseFactor);
            output.writeInt(vectorSet.indexer.size());
            int end = vectorSet.probs.size();
            for (int offset = 0; offset < end; offset++) {
                float val = vectorSet.probs.get(offset);
                if (val != -1) {
                    output.writeFloat(val);
                }
            }
        }
    }

    public static class RecommendationSerializer extends Serializer<Recommendation> {
        {
            setImmutable(true);
        }

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
            int limits = input.readInt();
            String scoringName = input.readString();
            SimScore scoring;
            if (scoringName.equals("cosinesq")) {
                scoring = new CosineSquareSimilarity();
            } else if (scoringName.equals("jensenshannon")) {
                scoring = new JensenShannonDivergence();
            } else {
                scoring = new CosineSquareSimilarity();
            }
            Recommendation rec = new Recommendation(source, target, scoring, limits);

            int sortersSize = input.readInt();
            while (sortersSize > 0) {
                int size = input.readInt();
                int srcId = input.readInt();
                float waterline = input.readFloat();
                while (size > 0) {
                    int tgtId = input.readInt();
                    float score = input.readFloat();
                    rec.add(srcId, tgtId, score);
                    size--;
                }
                rec.sorters.get(srcId).waterline = waterline;
                sortersSize--;
            }
            return rec;
        }

        @Override
        public void write(Kryo kryo, Output output, Recommendation recommend) {
            output.writeInt(recommend.limit);
            output.writeString(recommend.scoring.name());

            output.writeInt(recommend.sorters.size());
            TIntObjectIterator<Sorter> iter = recommend.sorters.iterator();
            while (iter.hasNext()) {
                iter.advance();
                Sorter sorter = iter.value();
                int size = sorter.size;
                output.writeInt(size);
                output.writeInt(iter.key());
                output.writeFloat(sorter.waterline);
                while (size > 0) {
                    output.writeInt(sorter.vecids[size]);
                    output.writeFloat(sorter.scores[size]);
                    size--;
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
        int size = input.readInt();
        System.out.println("vsets size:" + size);
        while (size > 0) {
            String type = input.readString();
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
        output.writeInt(vectorSets.size());
        System.out.println("vsets size:" + vectorSets.size());
        for (String key : vectorSets.keySet()) {
            VectorSet vectorSet = vectorSets.get(key);
            System.out.println("vsets type:" + vectorSet.type());
            output.writeString("dense");
            kryo.writeObject(output, vectorSet);
        }
    }

    public Map<String, Recommendation> readRecommendations(Input input, Map<String, VectorSet> vectorSets) {
        Map<String, Recommendation> recs = new HashMap<String, Recommendation>();
        int size = input.readInt();
        while (size > 0) {
            String srcKey = input.readString();
            String tgtKey = input.readString();
            VectorSet src = vectorSets.get(srcKey);
            VectorSet tgt = vectorSets.get(srcKey);
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
        output.writeInt(recommendations.size());
        for (String key : recommendations.keySet()) {
            Recommendation rec = recommendations.get(key);
            output.writeString(rec.source.key());
            output.writeString(rec.target.key());
            kryo.writeObject(output, rec);
        }
    }
}

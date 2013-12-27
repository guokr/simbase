package com.guokr.simbase.store;

import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SerializerHelper {

    public static class BasisSerializer extends Serializer<Basis> {

        @Override
        public Basis read(Kryo kryo, Input input, Class<Basis> type) {
            String[] schema = kryo.readObject(input, String[].class);
            return new Basis(schema);
        }

        @Override
        public void write(Kryo kryo, Output output, Basis basis) {
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
            return null;
        }

        @Override
        public void write(Kryo kryo, Output output, DenseVectorSet vectorSet) {
            kryo.writeObject(output, vectorSet.type());
            kryo.writeObject(output, vectorSet.key);
            kryo.writeObject(output, vectorSet.accumuFactor);
            kryo.writeObject(output, vectorSet.sparseFactor);
            kryo.writeObject(output, vectorSet.indexer.size());
            int end = vectorSet.probs.size();
            for (int offset = 0; offset < end; offset++) {
                float val = vectorSet.probs.get(offset);
                if (val >= 0) {
                    kryo.writeObject(output, val);
                }
            }
        }

    }

    public class SparseVectorSetSerializer extends Serializer<SparseVectorSet> {

        public void setBasis(Basis basis) {
        }

        @Override
        public SparseVectorSet read(Kryo kryo, Input input, Class<SparseVectorSet> type) {
            return null;
        }

        @Override
        public void write(Kryo kryo, Output output, SparseVectorSet vectorSet) {
        }

    }

    public class RecommendationSerializer extends Serializer<Recommendation> {

        public void setSource(VectorSet source) {
        }

        public void setTarget(VectorSet target) {
        }

        @Override
        public Recommendation read(Kryo kryo, Input input, Class<Recommendation> type) {
            return null;
        }

        @Override
        public void write(Kryo kryo, Output output, Recommendation recommend) {
        }

    }

    private final Kryo                kryo      = new Kryo();

    private BasisSerializer           bSerial   = new BasisSerializer();
    private DenseVectorSetSerializer  dvsSerial = new DenseVectorSetSerializer();
    private SparseVectorSetSerializer svsSerial = new SparseVectorSetSerializer();
    private RecommendationSerializer  rSerial   = new RecommendationSerializer();

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

    public Basis writeB(Output output, Basis base) {
        return null;
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

    public Map<String, VectorSet> readVectorSets(Basis tmpBase) {
        return null;
    }

    public void writeVectorSets(Output output, Map<String, VectorSet> vectorSets) {
    }

    public Map<String, Recommendation> readRecommendations(Map<String, VectorSet> tmpVecSets) {
        return null;
    }

    public void writeRecommendations(Output output, Map<String, Recommendation> recommendations) {
    }
}

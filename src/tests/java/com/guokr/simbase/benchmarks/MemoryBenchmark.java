package com.guokr.simbase.benchmarks;

import java.util.Date;

import com.guokr.simbase.engine.SimBasis;

public class MemoryBenchmark {

	public static void main(String[] args) {
		long accumulated = 0;
		SimBasis engine = new SimBasis();
		for (int i = 0; i < 100000; i++) {

			float total = 0;
			float[] distr = new float[2048];
			for (int j = 0; j < 2048; j++) {
				distr[j] = (float) Math.random();
				total += distr[j];
			}
			for (int j = 0; j < 2048; j++) {
				distr[j] = distr[j] / total;
			}

			long start = new Date().getTime();
			engine.add(i, distr);
			long duration = new Date().getTime() - start;
			accumulated += duration;

			if (i % 1024 == 0) {
				System.out.println("docid:" + i + "\tmemory:"
						+ Runtime.getRuntime().totalMemory() + "\ttime:"
						+ (accumulated / 1024));
				accumulated = 0;
			}

		}
	}

}

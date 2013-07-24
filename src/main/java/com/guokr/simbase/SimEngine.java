package com.guokr.simbase;

import java.util.SortedMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimEngine {
	
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private SimTable table = new SimTable();
	
	public void add(final int docid, final float[] distr) {
		service.execute(new Runnable() {
			public void run() {
				table.add(docid, distr);
			}			
		});
	}

	public void update(final int docid, final float[] distr) {
		service.execute(new Runnable() {
			public void run() {
				table.update(docid, distr);
			}			
		});
	}

	public void delete(final int docid) {
		service.execute(new Runnable() {
			public void run() {
				table.delete(docid);
			}			
		});
	}

	public SortedMap<Integer, Float> retrieve(int docid) {
		return table.retrieve(docid);
	}

}

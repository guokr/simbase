package com.guokr.simbase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

public class SimEngine {

	private ExecutorService service = Executors.newSingleThreadExecutor();
	private final SimTable table = new SimTable();
	private Kryo kryo = new Kryo();
	private String dir = System.getProperty("user.dir")
			+ System.getProperty("file.separator");
	private String path = dir + "simbase.dmp";

	public void save() throws FileNotFoundException {
		service.execute(new Runnable() {
			public void run() {
				Runnable runner = new Runnable() {
					private SimTable data = table.clone();

					@Override
					public void run() {
						Output output = null;
						try {
							output = new Output(new FileOutputStream(path));
							kryo.writeObject(output, data);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} finally {
							if (output != null) {
								output.close();
							}
						}
					}
				};
				new Thread(runner).start();
			}
		});
	}

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

	public SortedSet<Map.Entry<Integer, Float>> retrieve(int docid) {
		return table.retrieve(docid);
	}

}
